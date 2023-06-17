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

class BitwiseAdderTest {

  @ParameterizedTest
  @CsvSource({
    "0, 0000 0000, 0000 0000, 0, 0000 0000",
    "0, 0000 0000, 1111 1111, 0, 1111 1111",
    "0, 0000 0010, 0000 0010, 0, 0000 0100",
    "0, 0000 0001, 0000 0010, 0, 0000 0011",
    "0, 0000 0001, 0000 0110, 0, 0000 0111",
    "0, 1000 0000, 0100 0000, 0, 1100 0000",
  })
  public void test(String carryInputBinaryString,
                   String input1BinaryString,
                   String input2BinaryString,
                   String expectedCarryOutputBinaryString,
                   String expectedOutputBinaryString) {


    // given
    @Cleanup final var world = new TestWorld();

    final var adder = new BitwiseAdder("adder");

    connect(adder.getInput(0), constructPowerSupplies(world, "input1PowerSupplies", input1BinaryString));
    connect(adder.getInput(1), constructPowerSupplies(world, "input2PowerSupplies", input2BinaryString));
    adder.getCarryInput().connectTo(constructPowerSupply(world, "carryInput", carryInputBinaryString));

    final var outputSpies = constructOutputSpies(adder.getOutput());
    final var carryOutputSpy = constructOutputSpy(adder.getCarryOutput());

    // when
    world.runSynchronously();
    // then
    Assertions.assertEquals(expectedOutputBinaryString, formatToBinaryString(outputSpies));
    Assertions.assertEquals(expectedCarryOutputBinaryString, formatToBinaryString(carryOutputSpy));

  }

}