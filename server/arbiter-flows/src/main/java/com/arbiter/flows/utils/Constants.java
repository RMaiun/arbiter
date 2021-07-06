package com.arbiter.flows.utils;

import org.apache.commons.lang3.StringUtils;

public interface Constants {

  String LINE_SEPARATOR = System.lineSeparator();
  String DELIMITER = StringUtils.repeat("-", 34) + LINE_SEPARATOR;
  String SUFFIX = "```";
  String PREFIX = SUFFIX + LINE_SEPARATOR;
  String DEFAULT_RESULT = "";
  String PERCENT = "%";
  String NA = "n/a";
}
