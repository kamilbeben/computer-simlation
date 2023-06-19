package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.beben.computersimulation.device.TestWorld;
import static pl.beben.computersimulation.TestUtils.constructOutputSpy;
import static pl.beben.computersimulation.TestUtils.constructPowerSupply;
import static pl.beben.computersimulation.TestUtils.formatToBinaryString;

class BitComparatorTest {

  @ParameterizedTest
  @CsvSource({
  // 1, 2, 3, 4,  5, 6, 7
    "0, 0, 1, 0,  0, 1, 0",
    "1, 1, 1, 0,  0, 1, 0",
    "0, 0, 0, 1,  0, 0, 1",
    "1, 0, 1, 0,  1, 0, 1",
    "0, 1, 0, 0,  1, 0, 0",
    "0, 1, 1, 0,  1, 0, 0",
    "0, 1, 1, 1,  1, 0, 1",
  })
  public void test(String firstInput, // 1
                   String secondInput, // 2
                   String allBitsAboveAreEqualInput, // 3
                   String firstInputIsLargerInput, // 4
                   String output, // 5
                   String allBitsSoFarAreEqualOutput, // 6
                   String firstInputIsLargerOutput) { // 7

    // given
    @Cleanup final var world = new TestWorld();

    final var comparator = new BitComparator("comparator");
    comparator.getInput(0).connectTo(constructPowerSupply(world, firstInput));
    comparator.getInput(1).connectTo(constructPowerSupply(world, secondInput));
    comparator.getFirstInputIsLargerInput().connectTo(constructPowerSupply(world, firstInputIsLargerInput));
    comparator.getAllBitsAboveAreEqualInput().connectTo(constructPowerSupply(world, allBitsAboveAreEqualInput));

    final var outputSpy = constructOutputSpy(comparator.getOutput());
    final var allBitsSoFarAreEqualOutputSpy = constructOutputSpy(comparator.getAllBitsSoFarAreEqualOutput());
    final var firstInputIsLargerOutputSpy = constructOutputSpy(comparator.getFirstInputIsLargerOutput());

    // when
    world.runSynchronously();

    // then
    Assertions.assertEquals(output, formatToBinaryString(outputSpy));
    Assertions.assertEquals(allBitsSoFarAreEqualOutput, formatToBinaryString(allBitsSoFarAreEqualOutputSpy));
    Assertions.assertEquals(firstInputIsLargerOutput, formatToBinaryString(firstInputIsLargerOutputSpy));
  }

}