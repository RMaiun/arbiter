package com.arbiter.core.controller;


import com.arbiter.core.dto.dump.ImportDumpDto;
import com.arbiter.core.dto.stats.GenerateStatsDocumentDto;
import com.arbiter.core.service.DumpExporter;
import com.arbiter.core.service.DumpImporter;
import com.arbiter.core.service.ReportGenerator;
import com.arbiter.core.utils.DateUtils;
import java.time.ZonedDateTime;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class BinaryController implements BinaryResponseSupport {

  private final ReportGenerator reportGeneratorService;
  private final DumpExporter exportService;
  private final DumpImporter importService;

  public BinaryController(ReportGenerator reportGeneratorService, DumpExporter exportService, DumpImporter importService) {
    this.reportGeneratorService = reportGeneratorService;
    this.exportService = exportService;
    this.importService = importService;
  }

  @GetMapping(value = "/reports/xlsx/{season}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<InputStreamResource> xlsxReport(@PathVariable String season) {
    return binaryResponse(reportGeneratorService.generateXslxReport(new GenerateStatsDocumentDto(season)));
  }

  @PostMapping(value = "/dump/import/{moderator}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ImportDumpDto importDump(@RequestPart("file") MultipartFile file, @PathVariable String moderator) {
    return importService.importDump(file, moderator);
  }

  @GetMapping(value = "/dump/export/{moderator}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<InputStreamResource> exportDump(@PathVariable String moderator) {
    ZonedDateTime now = DateUtils.now();
    return binaryResponse(exportService.export(now, moderator));
  }
}
