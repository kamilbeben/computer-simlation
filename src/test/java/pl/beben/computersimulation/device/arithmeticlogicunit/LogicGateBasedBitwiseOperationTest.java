package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.beben.computersimulation.device.TestWorld;
import static pl.beben.computersimulation.TestUtils.connect;
import static pl.beben.computersimulation.TestUtils.constructOutputSpies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupplies;
import static pl.beben.computersimulation.TestUtils.formatToBinaryString;

class LogicGateBasedBitwiseOperationTest {

  @ParameterizedTest
  @CsvSource({
    "0000 0000, 0000 0000, 0000 0000",
    "1111 1111, 1111 1111, 1111 1111",
    "0000 0000, 1111 1111, 0000 0000",
    "1100 0000, 1000 0000, 1000 0000",
  })
  public void testAnd(String input1BinaryString, String input2BinaryString, String expectedOutputBinaryString) {

    testBinaryBitwiseOperator(
      new BitwiseAnd("bitwiseAnd"),
      input1BinaryString, input2BinaryString, expectedOutputBinaryString
    );
  }

  @ParameterizedTest
  @CsvSource({
    "0000 0000, 0000 0000, 0000 0000",
    "1111 1111, 1111 1111, 1111 1111",
    "0000 0000, 1111 1111, 1111 1111",
    "1100 0000, 1010 0000, 1110 0000",
  })
  public void testOr(String input1BinaryString, String input2BinaryString, String expectedOutputBinaryString) {

    testBinaryBitwiseOperator(
      new BitwiseOr("bitwiseOr"),
      input1BinaryString, input2BinaryString, expectedOutputBinaryString
    );
  }

  @ParameterizedTest
  @CsvSource({
    "0000 0000, 0000 0000, 0000 0000",
    "1111 1111, 1111 1111, 0000 0000",
    "0000 0000, 1111 1111, 1111 1111",
    "1100 0000, 1010 0000, 0110 0000",
  })
  public void testXor(String input1BinaryString, String input2BinaryString, String expectedOutputBinaryString) {

    testBinaryBitwiseOperator(
      new BitwiseXor("bitwiseXor"),
      input1BinaryString, input2BinaryString, expectedOutputBinaryString
    );
  }

  private void testBinaryBitwiseOperator(LogicGateBasedBitwiseOperation instance,
                                         String input1BinaryString, String input2BinaryString, String expectedOutputBinaryString) {

    // given
    @Cleanup final var world = new TestWorld();

    connect(instance.getInput(0), constructPowerSupplies(world, "input1PowerSupplies", input1BinaryString));
    connect(instance.getInput(1), constructPowerSupplies(world, "input2PowerSupplies", input2BinaryString));

    final var outputSpies = constructOutputSpies(instance.getOutput());

    // when
    world.runSynchronously();
    // then
    Assertions.assertEquals(expectedOutputBinaryString, formatToBinaryString(outputSpies));
  }

}