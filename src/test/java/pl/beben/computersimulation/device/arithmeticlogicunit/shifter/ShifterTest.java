package pl.beben.computersimulation.device.arithmeticlogicunit.shifter;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.beben.computersimulation.device.TestWorld;
import static pl.beben.computersimulation.TestUtils.constructOutputSpies;
import static pl.beben.computersimulation.TestUtils.constructOutputSpy;
import static pl.beben.computersimulation.TestUtils.constructPowerSupplies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupply;
import static pl.beben.computersimulation.TestUtils.formatToBinaryString;

class ShifterTest {

  @ParameterizedTest
  @CsvSource({
    "RIGHT, 0, 1111 1111, 1, 0111 1111",
    "RIGHT, 0, 0100 0010, 0, 0010 0001",
    "RIGHT, 1, 0100 0010, 0, 1010 0001",
    "RIGHT, 1, 0100 0011, 1, 1010 0001",
    "RIGHT, 0, 0001 1111, 1, 0000 1111",
    "RIGHT, 1, 0001 1111, 1, 1000 1111",
    "RIGHT, 1, 0001 1001, 1, 1000 1100",
    "LEFT,  0, 1111 1111, 1, 1111 1110",
    "LEFT,  0, 0100 0010, 0, 1000 0100",
  })
  public void test(Shifter.ShifterType type,
                   String shiftInputBinaryString, String inputBinaryString,
                   String expectedShiftOutputBinaryString, String expectedOutputBinaryString) {

    // given
    @Cleanup final var world = new TestWorld();

    final var shifter = switch (type) {
      case LEFT -> new Shifter.LeftShifter("leftShifter");
      case RIGHT -> new Shifter.RightShifter("rightShifter");
      default -> throw new IllegalStateException("Unexpected value: " + type);
    };

    final var alwaysOnPowerSupply = constructPowerSupply(world, "alwaysOn", true);
    final var powerSupplies = constructPowerSupplies(world, inputBinaryString);
    final var shiftInput = constructPowerSupply(world, "shiftInput", shiftInputBinaryString);
    final var outputSpies = constructOutputSpies(shifter.getOutput().getOutputs());
    final var shiftOutput = constructOutputSpy("shiftOutput", shifter.getShiftOutput());

    shifter.getInput().connectTo(powerSupplies);
    shifter.getShiftInput().connectTo(shiftInput);

    shifter.getInput().getSetterInput().connectTo(alwaysOnPowerSupply);
    shifter.getInput().getEnableInput().connectTo(alwaysOnPowerSupply);
    shifter.getOutput().getSetterInput().connectTo(alwaysOnPowerSupply);
    shifter.getOutput().getEnableInput().connectTo(alwaysOnPowerSupply);

    // when
    world.runSynchronously();

    // then
    Assertions.assertEquals(expectedOutputBinaryString, formatToBinaryString(outputSpies));
    Assertions.assertEquals(expectedShiftOutputBinaryString, formatToBinaryString(shiftOutput));
  }
}