package pl.beben.computersimulation.utils;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class DecoderUtils {

  public static int inputsCountToOutputsCount(int inputsCount) {
    return (byte) Math.pow(2, inputsCount);
  }

  public static String generateTruthTable(int inputsCount) {
    assert inputsCount > 1;
    assert inputsCount < 128;

    final var outputsCount = inputsCountToOutputsCount(inputsCount);
    final var zeroFilledOutputs = String.valueOf('0').repeat(outputsCount);

    var truthTableBuilder = new StringBuilder();
    byte rowIndex = 0;
    byte trueOutputIndex = 0;

    while (true) {

      final var inputs = BinaryStringFormatter.format(rowIndex, (byte) inputsCount);
      final var outputs = StringUtils.replaceCharAt(zeroFilledOutputs, trueOutputIndex, '1');

      truthTableBuilder
        .append(inputs)
        .append(outputs)
        .append("\n");

      rowIndex++;
      trueOutputIndex++;

      final var inputIsAllOnes = inputs.matches("[1]+");
      if (inputIsAllOnes)
        break;
    }

    return truthTableBuilder.toString().trim();
  }

}
