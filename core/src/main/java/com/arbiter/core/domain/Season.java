package com.arbiter.core.domain;

import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Season {

  @Id
  private String id;
  private String name;
  private ZonedDateTime seasonEndNotification;

  public Season() {
  }

  public Season(String id, String name, ZonedDateTime seasonEndNotification) {
    this.id = id;
    this.name = name;
    this.seasonEndNotification = seasonEndNotification;
  }

  public static Season of(String name) {
    return new Season(null, name, null);
  }

  public static Season of(String name, ZonedDateTime seasonEndNotification) {
    return new Season(null, name, seasonEndNotification);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ZonedDateTime getSeasonEndNotification() {
    return seasonEndNotification;
  }

  public void setSeasonEndNotification(ZonedDateTime seasonEndNotification) {
    this.seasonEndNotification = seasonEndNotification;
  }
}
