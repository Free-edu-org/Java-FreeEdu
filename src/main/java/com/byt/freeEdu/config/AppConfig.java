package com.byt.freeEdu.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
  private boolean debugMode;

  public boolean isDebugMode() {
    return debugMode;
  }

  public void setDebugMode(final boolean debugMode) {
    this.debugMode = debugMode;
  }
}
