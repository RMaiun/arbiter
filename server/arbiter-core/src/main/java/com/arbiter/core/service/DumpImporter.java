package com.arbiter.core.service;

import com.arbiter.core.domain.Player;
import com.arbiter.core.domain.Round;
import com.arbiter.core.domain.Season;
import com.arbiter.core.dto.dump.ImportDumpData;
import com.arbiter.core.dto.dump.ImportDumpDto;
import com.arbiter.core.exception.DumpException;
import com.arbiter.core.repository.PlayerRepository;
import com.arbiter.core.repository.RoundRepository;
import com.arbiter.core.repository.SeasonRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DumpImporter {

  public static final Logger logger = LogManager.getLogger(DumpImporter.class);

  private final ObjectMapper objectMapper;
  private final SeasonRepository seasonRepository;
  private final PlayerRepository playerRepository;
  private final RoundRepository roundRepository;
  private final UserRightsService userRightsService;

  public DumpImporter(ObjectMapper objectMapper, SeasonRepository seasonRepository, PlayerRepository playerRepository, RoundRepository roundRepository,
      UserRightsService userRightsService) {
    this.objectMapper = objectMapper;
    this.seasonRepository = seasonRepository;
    this.playerRepository = playerRepository;
    this.roundRepository = roundRepository;
    this.userRightsService = userRightsService;
  }

  public ImportDumpDto importDump(MultipartFile file, String moderator) {
    userRightsService.checkUserIsAdmin(moderator);
    var is = filePartToInputStream(file);
    var data = fetchDumpData(is);
    return storeDumpData(data);
  }


  private ImportDumpDto storeDumpData(ImportDumpData data) {
    var totalDeleted = clearTables();
    logger.info("Total deleted rows before import = {}", totalDeleted);
    var seasonsStored = importSeasons(data.seasonList());
    var playersStored = importPlayers(data.playersList());
    var roundsStored = importRounds(data.roundsList());
    return new ImportDumpDto(seasonsStored, playersStored, roundsStored);
  }

  private Long clearTables() {
    var x = roundRepository.removeAll();
    var y = seasonRepository.removeAll();
    var z = playerRepository.removeAll();
    return x + y + z;
  }

  private InputStream filePartToInputStream(MultipartFile file) {
    try {
      return file.getInputStream();
    } catch (IOException e) {
      throw new DumpException(e);
    }
  }

  private ImportDumpData fetchDumpData(InputStream is) {
    logger.info("Start data import from archive dump");
    List<Season> seasonList = new ArrayList<>();
    List<Player> playersList = new ArrayList<>();
    List<Round> roundsList = new ArrayList<>();

    try (ZipInputStream zis = new ZipInputStream(is)) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (entry.getName().contains("season")) {
          TypeReference<List<Season>> seasonsTR = new TypeReference<>() {
          };
          processZipEntry(zis, bytes -> seasonList.addAll(readData(bytes, seasonsTR)));
        } else if (entry.getName().contains("players")) {
          TypeReference<List<Player>> playersTR = new TypeReference<>() {
          };
          processZipEntry(zis, bytes -> playersList.addAll(readData(bytes, playersTR)));
        } else {
          TypeReference<List<Round>> roundsTR = new TypeReference<>() {
          };
          processZipEntry(zis, bytes -> roundsList.addAll(readData(bytes, roundsTR)));
        }
      }
    } catch (Throwable e) {
      throw new DumpException(e);
    }
    logger.info("Data import from archive dump successfully finished");
    return new ImportDumpData(seasonList, playersList, roundsList);
  }

  private <T> List<T> readData(byte[] bytes, TypeReference<List<T>> tr) {
    try {
      return objectMapper.readValue(bytes, tr);
    } catch (IOException e) {
      throw new DumpException(e);
    }
  }

  private void processZipEntry(ZipInputStream zis, Consumer<byte[]> consumer) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    IOUtils.copy(zis, bos);
    consumer.accept(bos.toByteArray());
    bos.close();
    zis.closeEntry();
  }

  private Map<String, Integer> importRounds(List<Round> rounds) {
    return rounds.stream()
        .collect(Collectors.groupingBy(Round::getSeason))
        .entrySet().stream()
        .map(e -> Pair.of(e.getKey(), roundRepository.bulkSave(e.getValue())))
        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
  }

  private long importSeasons(List<Season> seasons) {
    return seasonRepository.bulkSave(seasons);
  }

  private long importPlayers(List<Player> players) {
    return playerRepository.bulkSave(players);
  }
}
