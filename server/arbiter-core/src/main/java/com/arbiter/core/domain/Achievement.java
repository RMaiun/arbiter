package com.arbiter.core.domain;

import java.time.ZonedDateTime;

public class Achievement {

  private String code;
  private ZonedDateTime createdAt;

  public Achievement() {
  }

  public Achievement(String code, ZonedDateTime createdAt) {
    this.code = code;
    this.createdAt = createdAt;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(ZonedDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
