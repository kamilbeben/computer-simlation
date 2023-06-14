package pl.beben.computersimulation.device.transport;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import static pl.beben.computersimulation.TestUtils.constructOutputSpies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupplies;
import static pl.beben.computersimulation.TestUtils.formatToBinaryString;
import static pl.beben.computersimulation.TestUtils.setInputValue;

class InputOutputBusTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var initialValue = "00";
    final var inputs1Value = "10";
    final var inputs2Value = "01";
    final var combinedValue = "11";

    final var inputs1 = constructPowerSupplies(world, "inputs1", initialValue);
    final var inputs2 = constructPowerSupplies(world, "inputs2", initialValue);

    final var bus = new InputOutputBus("bus", 2);
    bus.connectOutputToBus(inputs1);
    bus.connectOutputToBus(inputs2);

    final var outputs1 = constructOutputSpies("outputs1", bus.outputs);
    final var outputs2 = constructOutputSpies("outputs2", bus.outputs);

    // when
    world.runSynchronously();
    // then
    Assertions.assertEquals(initialValue, formatToBinaryString(inputs1));
    Assertions.assertEquals(initialValue, formatToBinaryString(inputs2));
    Assertions.assertEquals(initialValue, formatToBinaryString(outputs1));
    Assertions.assertEquals(initialValue, formatToBinaryString(outputs2));

    // when
    setInputValue(inputs1, inputs1Value);
    setInputValue(inputs2, inputs2Value);
    world.runSynchronously();
    // then
    Assertions.assertEquals(inputs1Value, formatToBinaryString(inputs1));
    Assertions.assertEquals(inputs2Value, formatToBinaryString(inputs2));
    Assertions.assertEquals(combinedValue, formatToBinaryString(outputs1));
    Assertions.assertEquals(combinedValue, formatToBinaryString(outputs2));

    // when
    setInputValue(inputs2, initialValue);
    world.runSynchronously();
    // then
    Assertions.assertEquals(inputs1Value, formatToBinaryString(inputs1));
    Assertions.assertEquals(initialValue, formatToBinaryString(inputs2));
    Assertions.assertEquals(inputs1Value, formatToBinaryString(outputs1));
    Assertions.assertEquals(inputs1Value, formatToBinaryString(outputs2));
  }

  @Test
  public void connectInvalidInputToBus() {
    try {
      new InputOutputBus(null, 2).connectInputToBus(new PowerInput[1]);
      Assertions.fail("IllegalArgumentException should be thrown - bus width is 2 but is trying to connect 1 input");
    } catch (IllegalArgumentException e) {
      // that's expected
    }
  }

  @Test
  public void connectInvalidOutputToBus() {
    try {
      new InputOutputBus(null, 2).connectOutputToBus(new PowerOutput[1]);
      Assertions.fail("IllegalArgumentException should be thrown - bus width is 2 but is trying to connect 1 input");
    } catch (IllegalArgumentException e) {
      // that's expected
    }
  }

}