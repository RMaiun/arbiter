package com.arbiter.flows.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rabbit")
public class RabbitProperties {

  private String username;
  private String password;
  private String host;
  private String virtualHost;
  private int port;
  private String inputQueue;
  private String outputQueue;
  private String binaryQueue;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getVirtualHost() {
    return virtualHost;
  }

  public void setVirtualHost(String virtualHost) {
    this.virtualHost = virtualHost;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getInputQueue() {
    return inputQueue;
  }

  public void setInputQueue(String inputQueue) {
    this.inputQueue = inputQueue;
  }

  public String getOutputQueue() {
    return outputQueue;
  }

  public void setOutputQueue(String outputQueue) {
    this.outputQueue = outputQueue;
  }

  public String getBinaryQueue() {
    return binaryQueue;
  }

  public void setBinaryQueue(String binaryQueue) {
    this.binaryQueue = binaryQueue;
  }
}
