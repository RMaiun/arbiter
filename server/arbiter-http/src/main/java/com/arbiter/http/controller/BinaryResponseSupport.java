package com.arbiter.http.controller;

import com.arbiter.core.dto.BinaryFileDto;
import java.io.ByteArrayInputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface BinaryResponseSupport {

  default ResponseEntity<InputStreamResource> binaryResponse(BinaryFileDto res) {
    String fileName = String.format("%s.%s", res.fileName().replace("|", "_"), res.extension());
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s", fileName))
        .body(new InputStreamResource(new ByteArrayInputStream(res.data())));
  }
}
