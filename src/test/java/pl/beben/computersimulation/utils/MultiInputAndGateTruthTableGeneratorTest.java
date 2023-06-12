package pl.beben.computersimulation.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

class MultiInputAndGateTruthTableGeneratorTest {

  @ParameterizedTest
  @MethodSource("computeGenerateTestArgumentsStream")
  public void generateTest(int inputsCount, String expectedTruthTable) {
    Assertions.assertEquals(expectedTruthTable, MultiInputAndGateTruthTableGenerator.generate(inputsCount));
  }

  static Stream<Arguments> computeGenerateTestArgumentsStream() {
    return Stream.of(
      Arguments.of(
        (byte) 2,
        TruthTableSanitizer.sanitize(
          """
          0 0  0
          0 1  0
          1 0  0
          1 1  1
          """
        )
      ),
      Arguments.of(
        (byte) 3,
        TruthTableSanitizer.sanitize(
          """
          0 0 0  0
          0 0 1  0
          0 1 0  0
          0 1 1  0
          1 0 0  0
          1 0 1  0
          1 1 0  0
          1 1 1  1
          """
        )
      )
    );
  }


}