package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.beben.computersimulation.device.TestWorld;
import static pl.beben.computersimulation.TestUtils.constructOutputSpy;
import static pl.beben.computersimulation.TestUtils.constructPowerSupplies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupply;
import static pl.beben.computersimulation.TestUtils.formatToBinaryString;

class FullAdderTest {

  @ParameterizedTest
  @CsvSource({
    "00,0,0,0",
    "00,1,1,0",
    "01,0,1,0",
    "10,0,1,0",
    "01,1,0,1",
    "10,1,0,1",
    "11,0,0,1",
    "11,1,1,1"
  })
  public void test(String inputBinaryString, String carryInputBinaryString, String expectedOutputBinaryString, String expectedCarryOutputBinaryString) {

    // given
    @Cleanup final var world = new TestWorld();

    final var inputPowerSupplies = constructPowerSupplies(world, inputBinaryString);
    final var carryInputPowerSupply = constructPowerSupply(world, carryInputBinaryString);

    final var fullAdder = new FullAdder("fullAdder");
    fullAdder.getInput(0).connectTo(inputPowerSupplies[0]);
    fullAdder.getInput(1).connectTo(inputPowerSupplies[1]);
    fullAdder.getCarryInput().connectTo(carryInputPowerSupply);

    final var outputSpy = constructOutputSpy(fullAdder.getOutput());
    final var carryOutputSpy = constructOutputSpy(fullAdder.getCarryOutput());

    // when
    world.runSynchronously();

    // then
    Assertions.assertEquals(expectedOutputBinaryString, formatToBinaryString(outputSpy));
    Assertions.assertEquals(expectedCarryOutputBinaryString, formatToBinaryString(carryOutputSpy));
  }

}