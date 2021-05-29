package com.arbiter.core.domain;

import java.time.ZonedDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Round {

  @Id
  private String id;
  private String winner1;
  private String winner2;
  private String loser1;
  private String loser2;
  private boolean shutout;
  private String season;
  private ZonedDateTime created;

  public Round() {
  }

  public Round(String id, String winner1, String winner2, String loser1, String loser2, boolean shutout, String season, ZonedDateTime created) {
    this.id = id;
    this.winner1 = winner1;
    this.winner2 = winner2;
    this.loser1 = loser1;
    this.loser2 = loser2;
    this.shutout = shutout;
    this.season = season;
    this.created = created;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getWinner1() {
    return winner1;
  }

  public void setWinner1(String winner1) {
    this.winner1 = winner1;
  }

  public String getWinner2() {
    return winner2;
  }

  public void setWinner2(String winner2) {
    this.winner2 = winner2;
  }

  public String getLoser1() {
    return loser1;
  }

  public void setLoser1(String loser1) {
    this.loser1 = loser1;
  }

  public String getLoser2() {
    return loser2;
  }

  public void setLoser2(String loser2) {
    this.loser2 = loser2;
  }

  public boolean isShutout() {
    return shutout;
  }

  public void setShutout(boolean shutout) {
    this.shutout = shutout;
  }

  public String getSeason() {
    return season;
  }

  public void setSeason(String season) {
    this.season = season;
  }

  public ZonedDateTime getCreated() {
    return created;
  }

  public void setCreated(ZonedDateTime created) {
    this.created = created;
  }
}
