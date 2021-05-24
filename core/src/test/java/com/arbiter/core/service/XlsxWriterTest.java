package com.arbiter.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.arbiter.core.TestConfig;
import com.arbiter.core.dto.BinaryFileDto;
import com.arbiter.core.dto.stats.SeasonStatsRows;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@DisplayName("XlsxWriter Test")
class XlsxWriterTest {

  private static final String EXPECTED_XLSX = "/test.xlsx";

  @Autowired
  private XlsxWriter xlsxWriter;

  @Test
  void createDocumentTest() throws IOException {
    List<String> headers = List.of("PlayerOne", "PlayerTwo", "PlayerThree", "PlayerFour");
    List<Integer> totals = List.of(1025, 2200, 875, 650);
    List<List<String>> games = List.of(
        List.of("", "", "50", "-50"),
        List.of("-25", "", "25", ""),
        List.of("", "", "50", "-50"),
        List.of("-25", "", "50", "-50"),
        List.of("-50", "50", "", "")
    );
    List<String> created = List.of("1 AUG 2020", "2 AUG 2020", "3 AUG 2020", "4 AUG 2020", "5 AUG 2020");
    SeasonStatsRows statsRows = new SeasonStatsRows(headers, totals, games, created, 5);
    BinaryFileDto result = xlsxWriter.generateDocument(statsRows, "S1|2020");
    assertNotNull(result);
    assertNotNull(result.data());

    InputStream actual = new ByteArrayInputStream(result.data());
    InputStream expected = XlsxWriterTest.class.getResourceAsStream(EXPECTED_XLSX);
    assertEquals(new XSSFExcelExtractor(new XSSFWorkbook(expected)).getText(), new XSSFExcelExtractor(new XSSFWorkbook(actual)).getText());
  }
}
