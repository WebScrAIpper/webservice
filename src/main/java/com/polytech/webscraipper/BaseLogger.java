package com.polytech.webscraipper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseLogger {
  private final Logger logger;

  public BaseLogger(Class<?> clazz) {
    logger = LoggerFactory.getLogger(clazz);
  }

  public void debug(String message) {
    logger.debug(message);
  }

  public void info(String message) {
    logger.info(message);
  }

  public void warn(String message) {
    logger.warn(message);
  }

  public void error(String message) {
    logger.error(message);
  }

  public void error(String message, Throwable throwable) {
    logger.error(message, throwable);
  }

  public void trace(String message) {
    logger.trace(message);
  }
}
