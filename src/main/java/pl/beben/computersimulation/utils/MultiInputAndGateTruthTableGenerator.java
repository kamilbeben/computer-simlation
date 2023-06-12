package pl.beben.computersimulation.utils;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class MultiInputAndGateTruthTableGenerator {

  public static String generate(int inputsCount) {
    assert inputsCount > 1;
    assert inputsCount < 128;

    var truthTableBuilder = new StringBuilder();
    byte rowIndex = 0;

    while (true) {

      final var inputs = BinaryStringFormatter.format(rowIndex, (byte) inputsCount);
      final var inputIsAllOnes = inputs.matches("[1]+");
      final var outputs = inputIsAllOnes ? "1" : "0";

      truthTableBuilder
        .append(inputs)
        .append(outputs)
        .append("\n");

      rowIndex++;

      if (inputIsAllOnes)
        break;
    }

    return truthTableBuilder.toString().trim();
  }
}
