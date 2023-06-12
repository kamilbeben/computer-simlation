package pl.beben.computersimulation.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

class DecoderUtilsTest {

  @ParameterizedTest
  @MethodSource("computeGenerateTruthTableTestArgumentsStream")
  public void generateTruthTableTest(int inputsCount, String expectedTruthTable) {
    Assertions.assertEquals(expectedTruthTable, DecoderUtils.generateTruthTable(inputsCount));
  }

  static Stream<Arguments> computeGenerateTruthTableTestArgumentsStream() {
    return Stream.of(
      Arguments.of(
        (byte) 2,
        TruthTableSanitizer.sanitize(
          """
          0 0  1 0 0 0
          0 1  0 1 0 0
          1 0  0 0 1 0
          1 1  0 0 0 1
          """
        )
      ),
      Arguments.of(
        (byte) 3,
        TruthTableSanitizer.sanitize(
          """
          0 0 0  1 0 0 0 0 0 0 0
          0 0 1  0 1 0 0 0 0 0 0
          0 1 0  0 0 1 0 0 0 0 0
          0 1 1  0 0 0 1 0 0 0 0
          1 0 0  0 0 0 0 1 0 0 0
          1 0 1  0 0 0 0 0 1 0 0
          1 1 0  0 0 0 0 0 0 1 0
          1 1 1  0 0 0 0 0 0 0 1
          """
        )
      )
    );
  }

}