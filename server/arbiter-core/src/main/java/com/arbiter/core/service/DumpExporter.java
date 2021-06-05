package com.arbiter.core.service;

import com.arbiter.core.domain.Player;
import com.arbiter.core.domain.Round;
import com.arbiter.core.domain.Season;
import com.arbiter.core.dto.BinaryFileDto;
import com.arbiter.core.exception.DumpException;
import com.arbiter.core.repository.PlayerRepository;
import com.arbiter.core.repository.RoundRepository;
import com.arbiter.core.repository.SeasonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class DumpExporter {

  public static final Logger log = LogManager.getLogger(DumpExporter.class);

  private final String SEASONS = "seasons.json";
  private final String PLAYERS = "players.json";
  private final String ROUNDS = "rounds_%s.json";

  private final SeasonRepository seasonRepository;
  private final PlayerRepository playerRepository;
  private final RoundRepository roundRepository;
  private final ObjectMapper objectMapper;
  private final UserRightsService userRightsService;

  public DumpExporter(SeasonRepository seasonRepository, PlayerRepository playerRepository, RoundRepository roundRepository, ObjectMapper objectMapper,
      UserRightsService userRightsService) {
    this.seasonRepository = seasonRepository;
    this.playerRepository = playerRepository;
    this.roundRepository = roundRepository;
    this.objectMapper = objectMapper;
    this.userRightsService = userRightsService;
  }

  public BinaryFileDto export(ZonedDateTime before, String moderator) {
    userRightsService.checkUserIsAdmin(moderator);
    var seasons = seasonRepository.listAll();
    var players = playerRepository.listAll(false);
    var rounds = roundRepository.listLastRoundsBeforeDate(before);
    return prepareZipArchive(seasons, players, rounds);
  }

  private BinaryFileDto prepareZipArchive(List<Season> seasons, List<Player> players, List<Round> rounds) {
    log.info("Starting archive preparation for export");
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ZipOutputStream zipOutputStream = new ZipOutputStream(bos);
    Try<ZipOutputStream> zosResult = Try.of(() -> zipOutputStream)
        .flatMap(zos -> writeZipEntry(seasons, SEASONS, zos))
        .flatMap(zos -> writeZipEntry(players, PLAYERS, zos))
        .flatMap(zos -> prepareRoundsBySeason(rounds, zos))
        .andFinallyTry(zipOutputStream::close);
    log.info("Archive preparation for export was successfully finished");
    return zosResult.map(__ -> bos.toByteArray())
        .map(this::prepareResultDto)
        .getOrElseThrow(DumpException::new);
  }

  private BinaryFileDto prepareResultDto(byte[] bytes) {
    LocalDateTime now = LocalDateTime.now();
    int day = now.getDayOfMonth();
    String month = now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    int year = now.getYear();
    String date = String.format("%d_%s_%d", day, month, year);
    String ARCHIVE_NAME = "cata_dump_%s";
    String archiveName = String.format(ARCHIVE_NAME, date);
    return new BinaryFileDto(bytes, archiveName, "zip");
  }

  private Try<ZipOutputStream> prepareRoundsBySeason(List<Round> rounds, ZipOutputStream zos) {
    Stream<Entry<String, List<Round>>> roundsPerSeasonStream = rounds.stream()
        .collect(Collectors.groupingBy(Round::getSeason))
        .entrySet()
        .stream();
    io.vavr.collection.List<Entry<String, List<Round>>> vavrRoundsList = io.vavr.collection.Stream.ofAll(roundsPerSeasonStream).toList();
    return writeRoundsRecursively(vavrRoundsList, Try.of(() -> zos));
  }

  private Try<ZipOutputStream> writeRoundsRecursively(io.vavr.collection.List<Entry<String, List<Round>>> roundsPerSeason, Try<ZipOutputStream> zos) {
    if (roundsPerSeason.isEmpty()) {
      return zos;
    }
    Try<ZipOutputStream> modifiedZos = zos.flatMap(zosVal -> {
      Entry<String, List<Round>> head = roundsPerSeason.head();
      String fileName = head.getKey().replace("|", "_");
      return writeZipEntry(head.getValue(), String.format(ROUNDS, fileName), zosVal);
    });
    return writeRoundsRecursively(roundsPerSeason.tail(), modifiedZos);
  }

  private Try<ZipOutputStream> writeZipEntry(Object data, String name, ZipOutputStream zos) {
    return Try.of(() -> {
      ZipEntry e = new ZipEntry(name);
      zos.putNextEntry(e);
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(new CloseShieldOutputStream(zos), data);
      zos.closeEntry();
      return zos;
    });
  }
}
