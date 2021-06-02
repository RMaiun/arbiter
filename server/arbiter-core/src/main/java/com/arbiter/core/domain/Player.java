package com.arbiter.core.domain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Player {

  @Id
  private String id;

  private String surname;
  private String tid;
  private boolean admin;
  private boolean notificationsEnabled;
  private boolean active;

  public Player() {
  }

  public Player(String id, String surname, String tid, boolean admin, boolean notificationsEnabled, boolean active) {
    this.id = id;
    this.surname = surname;
    this.tid = tid;
    this.admin = admin;
    this.notificationsEnabled = notificationsEnabled;
    this.active = active;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getTid() {
    return tid;
  }

  public void setTid(String tid) {
    this.tid = tid;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public boolean isNotificationsEnabled() {
    return notificationsEnabled;
  }

  public void setNotificationsEnabled(boolean notificationsEnabled) {
    this.notificationsEnabled = notificationsEnabled;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
