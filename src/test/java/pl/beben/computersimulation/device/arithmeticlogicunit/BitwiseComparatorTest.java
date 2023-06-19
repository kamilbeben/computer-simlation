package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.beben.computersimulation.device.TestWorld;
import static pl.beben.computersimulation.TestUtils.connect;
import static pl.beben.computersimulation.TestUtils.constructOutputSpies;
import static pl.beben.computersimulation.TestUtils.constructOutputSpy;
import static pl.beben.computersimulation.TestUtils.constructPowerSupplies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupply;
import static pl.beben.computersimulation.TestUtils.formatToBinaryString;

class BitwiseComparatorTest {

  @ParameterizedTest
  @CsvSource({
  // 1,         2,         3,  4, 5,         6, 7
    "0000 0000, 0000 0000, 1,  0, 0000 0000, 1, 0",
    "0000 0000, 0000 0000, 0,  0, 0000 0000, 0, 0",
    "0000 0001, 0000 0001, 1,  0, 0000 0000, 1, 0",
    "0001 0001, 0001 0001, 1,  0, 0000 0000, 1, 0",
    "0001 1111, 0001 1111, 1,  0, 0000 0000, 1, 0",
    "0111 1111, 0111 1111, 1,  0, 0000 0000, 1, 0",
    "1111 1111, 1111 1111, 1,  0, 0000 0000, 1, 0",
    "0000 0011, 0000 0001, 1,  0, 0000 0010, 0, 1",
    "0000 0010, 0000 0001, 1,  0, 0000 0011, 0, 1",
    "0000 0001, 0000 0010, 1,  0, 0000 0011, 0, 0",
    "0000 0001, 0000 0010, 1,  1, 0000 0011, 0, 1",
    "1111 1111, 0111 1111, 1,  0, 1000 0000, 0, 1",
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

    final var comparator = new BitwiseComparator("comparator");
    connect(comparator.getInput(0), constructPowerSupplies(world, "firstInput", firstInput));
    connect(comparator.getInput(1), constructPowerSupplies(world, "secondInput", secondInput));
    comparator.getFirstInputIsLargerInput().connectTo(constructPowerSupply(world, firstInputIsLargerInput));
    comparator.getAllBitsAboveAreEqualInput().connectTo(constructPowerSupply(world, allBitsAboveAreEqualInput));

    final var outputSpies = constructOutputSpies(comparator.getOutput());
    final var allBitsSoFarAreEqualOutputSpy = constructOutputSpy(comparator.getAllBitsSoFarAreEqualOutput());
    final var firstInputIsLargerOutputSpy = constructOutputSpy(comparator.getFirstInputIsLargerOutput());

    // when
    world.runSynchronously();

    // then
    Assertions.assertEquals(output, formatToBinaryString(outputSpies));
    Assertions.assertEquals(allBitsSoFarAreEqualOutput, formatToBinaryString(allBitsSoFarAreEqualOutputSpy));
    Assertions.assertEquals(firstInputIsLargerOutput, formatToBinaryString(firstInputIsLargerOutputSpy));
  }

}