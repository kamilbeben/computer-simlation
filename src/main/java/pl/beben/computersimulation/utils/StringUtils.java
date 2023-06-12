package pl.beben.computersimulation.utils;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
class StringUtils {

  static String replaceCharAt(String string, int index, char withChar) {
    final var builder = new StringBuilder(string);
    builder.setCharAt(index, withChar);
    return builder.toString();
  }

}
