package pl.beben.computersimulation.device.memory;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;

class EnablerTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var valuePowerSupplies = new VccPowerSupply[8];
    for (int i = 0; i < 8; i++) {
      valuePowerSupplies[i] = new VccPowerSupply("VccPowerSupply[" + i + "]");
      world.registerAsTopLevelDevice(valuePowerSupplies[i]);
    }

    final var enablePowerSupply = new VccPowerSupply("enablePowerSupply");
    world.registerAsTopLevelDevice(enablePowerSupply);

    final var enabler = new Enabler("Byte");
    for (int i = 0; i < 8; i++) {
      enabler.getValueInput(i).connectTo(valuePowerSupplies[i]);
    }
    enabler.getEnableInput().connectTo(enablePowerSupply);

    final var outputSpies = new OutputSpy[8];
    for (int i = 0; i < 8; i++) {
      outputSpies[i] = new OutputSpy();
      outputSpies[i].connectTo(enabler.getOutput(i));
    }

    // when
    world.runSynchronously();

    // then - nothing happens
    Assertions.assertFalse(outputSpies[0].getValue());
    Assertions.assertFalse(outputSpies[1].getValue());
    Assertions.assertFalse(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertFalse(outputSpies[7].getValue());

    // when
    valuePowerSupplies[0].setValue(true);
    valuePowerSupplies[1].setValue(true);
    valuePowerSupplies[7].setValue(true);
    world.runSynchronously();

    // then - nothing happens, enablePowerSupply is disabled
    Assertions.assertFalse(outputSpies[0].getValue());
    Assertions.assertFalse(outputSpies[1].getValue());
    Assertions.assertFalse(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertFalse(outputSpies[7].getValue());

    // when
    enablePowerSupply.setValue(true);
    world.runSynchronously();

    // then - values which were previously set are now enabled
    Assertions.assertTrue(outputSpies[0].getValue());
    Assertions.assertTrue(outputSpies[1].getValue());
    Assertions.assertFalse(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertTrue(outputSpies[7].getValue());

    // when
    valuePowerSupplies[0].setValue(false);
    valuePowerSupplies[2].setValue(true);
    world.runSynchronously();

    // then
    Assertions.assertFalse(outputSpies[0].getValue());
    Assertions.assertTrue(outputSpies[1].getValue());
    Assertions.assertTrue(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertTrue(outputSpies[7].getValue());
  }

}
