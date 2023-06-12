package pl.beben.computersimulation.utils;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TruthTableSanitizer {

  public static String sanitize(String truthTable) {
    return truthTable
      .replaceAll("[ ]+", "") // spaces
      .replaceAll("\n$", ""); // empty line at the end
  }

}
