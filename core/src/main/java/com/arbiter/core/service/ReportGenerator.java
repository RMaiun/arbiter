package com.arbiter.core.service;


import com.arbiter.core.dto.BinaryFileDto;
import com.arbiter.core.dto.stats.GenerateStatsDocumentDto;
import com.arbiter.core.validation.ValidationTypes;
import com.arbiter.core.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class ReportGenerator {

  private final StatisticsService statisticsService;
  private final XlsxWriter xlsxWriter;

  public ReportGenerator(StatisticsService statisticsService, XlsxWriter xlsxWriter) {
    this.statisticsService = statisticsService;
    this.xlsxWriter = xlsxWriter;
  }

  public BinaryFileDto generateXslxReport(GenerateStatsDocumentDto dto) {
    Validator.validate(dto, ValidationTypes.generateStatsDocumentValidationType);
    var stats = statisticsService.seasonStatisticsRows(dto.season());
    return xlsxWriter.generateDocument(stats, dto.season());
  }


}
