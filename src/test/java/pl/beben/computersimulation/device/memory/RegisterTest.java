package pl.beben.computersimulation.device.memory;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;

public class RegisterTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var valuePowerSupplies = new VccPowerSupply[8];
    for (int i = 0; i < 8; i++) {
      valuePowerSupplies[i] = new VccPowerSupply("VccPowerSupply[" + i + "]");
      world.registerAsTopLevelDevice(valuePowerSupplies[i]);
    }

    final var setterPowerSupply = new VccPowerSupply("setterPowerSupply");
    world.registerAsTopLevelDevice(setterPowerSupply);

    final var enablePowerSupply = new VccPowerSupply("enablePowerSupply");
    world.registerAsTopLevelDevice(enablePowerSupply);

    final var register = new Register("register");
    for (int i = 0; i < 8; i++) {
      register.getValueInput(i).connectTo(valuePowerSupplies[i]);
    }
    register.getSetterInput().connectTo(setterPowerSupply);
    register.getEnableInput().connectTo(enablePowerSupply);

    final var outputSpies = new OutputSpy[8];
    for (int i = 0; i < 8; i++) {
      outputSpies[i] = new OutputSpy();
      outputSpies[i].connectTo(register.getOutput(i));
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
    valuePowerSupplies[7].setValue(true);
    world.runSynchronously();

    // then - nothing happens (setter is disabled; enabler is disabled)
    Assertions.assertFalse(outputSpies[0].getValue());
    Assertions.assertFalse(outputSpies[1].getValue());
    Assertions.assertFalse(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertFalse(outputSpies[7].getValue());

    // when
    setterPowerSupply.setValue(true);
    world.runSynchronously();

    // then - nothing happens (enabler is disabled)
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

    // then - output is passed on
    Assertions.assertTrue(outputSpies[0].getValue());
    Assertions.assertFalse(outputSpies[1].getValue());
    Assertions.assertFalse(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertTrue(outputSpies[7].getValue());

    // when
    setterPowerSupply.setValue(false);
    world.runSynchronously();

    // then - disabled setter, but enabler is enabled so output is passed on
    Assertions.assertTrue(outputSpies[0].getValue());
    Assertions.assertFalse(outputSpies[1].getValue());
    Assertions.assertFalse(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertTrue(outputSpies[7].getValue());

    // when
    enablePowerSupply.setValue(false);
    world.runSynchronously();

    // then disabled enabler, everything turns to false,
    Assertions.assertFalse(outputSpies[0].getValue());
    Assertions.assertFalse(outputSpies[1].getValue());
    Assertions.assertFalse(outputSpies[2].getValue());
    Assertions.assertFalse(outputSpies[3].getValue());
    Assertions.assertFalse(outputSpies[4].getValue());
    Assertions.assertFalse(outputSpies[5].getValue());
    Assertions.assertFalse(outputSpies[6].getValue());
    Assertions.assertFalse(outputSpies[7].getValue());
  }
}
