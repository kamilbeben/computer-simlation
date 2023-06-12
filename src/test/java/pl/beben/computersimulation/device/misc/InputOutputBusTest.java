package pl.beben.computersimulation.device.misc;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;

class InputOutputBusTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var inputs1 = new VccPowerSupply[2];
    final var inputs2 = new VccPowerSupply[2];
    for (int i = 0; i < 2; i++) {
      inputs1[i] = new VccPowerSupply("inputs1[" + i + "]");
      world.registerAsTopLevelDevice(inputs1[i]);

      inputs2[i] = new VccPowerSupply("inputs2[" + i + "]");
      world.registerAsTopLevelDevice(inputs2[i]);
    }

    final var outputs1 = new OutputSpy[2];
    final var outputs2 = new OutputSpy[2];
    for (int i = 0; i < 2; i++) {
      outputs1[i] = new OutputSpy("outputs1[" + i + "]");
      outputs2[i] = new OutputSpy("outputs2[" + i + "]");
    }

    final var bus = new InputOutputBus("bus", 2);

    bus.connectInputToBus(outputs1);
    bus.connectInputToBus(outputs2);

    bus.connectOutputToBus(inputs1);
    bus.connectOutputToBus(inputs2);

    // when
    world.runSynchronously();

    // then
    Assertions.assertFalse(inputs1[0].getValue());
    Assertions.assertFalse(inputs1[1].getValue());
    Assertions.assertFalse(inputs2[0].getValue());
    Assertions.assertFalse(inputs2[1].getValue());

    Assertions.assertFalse(outputs1[0].getValue());
    Assertions.assertFalse(outputs1[1].getValue());
    Assertions.assertFalse(outputs2[0].getValue());
    Assertions.assertFalse(outputs2[1].getValue());

    // when
    inputs1[0].setValue(true);
    inputs2[1].setValue(true);
    world.runSynchronously();

    // then
    Assertions.assertTrue(inputs1[0].getValue());
    Assertions.assertFalse(inputs1[1].getValue());
    Assertions.assertFalse(inputs2[0].getValue());
    Assertions.assertTrue(inputs2[1].getValue());

    Assertions.assertTrue(outputs1[0].getValue());
    Assertions.assertTrue(outputs1[1].getValue());
    Assertions.assertTrue(outputs2[0].getValue());
    Assertions.assertTrue(outputs2[1].getValue());

    // when
    inputs2[1].setValue(false);
    world.runSynchronously();

    // then
    Assertions.assertTrue(inputs1[0].getValue());
    Assertions.assertFalse(inputs1[1].getValue());
    Assertions.assertFalse(inputs2[0].getValue());
    Assertions.assertFalse(inputs2[1].getValue());

    Assertions.assertTrue(outputs1[0].getValue());
    Assertions.assertFalse(outputs1[1].getValue());
    Assertions.assertTrue(outputs2[0].getValue());
    Assertions.assertFalse(outputs2[1].getValue());
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