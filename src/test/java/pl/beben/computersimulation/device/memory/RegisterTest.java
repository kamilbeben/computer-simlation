package pl.beben.computersimulation.device.memory;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.TestWorld;
import static pl.beben.computersimulation.TestUtils.constructOutputSpies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupplies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupply;
import static pl.beben.computersimulation.TestUtils.formatToBinaryString;
import static pl.beben.computersimulation.TestUtils.setInputValue;

public class RegisterTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var initialValue = "0000 0000";
    final var value = "0100 0001";

    final var valuePowerSupplies = constructPowerSupplies(world, "VccPowerSupply", initialValue);
    final var setterPowerSupply = constructPowerSupply(world, "setterPowerSupply");
    final var enablePowerSupply = constructPowerSupply(world, "enablePowerSupply");

    final var register = new Register("register");
    register.connectTo(valuePowerSupplies);
    register.getSetterInput().connectTo(setterPowerSupply);
    register.getEnableInput().connectTo(enablePowerSupply);

    final var outputSpies = constructOutputSpies(register.getOutputs());

    // when
    world.runSynchronously();

    // then - nothing happens
    Assertions.assertEquals(initialValue, formatToBinaryString(outputSpies));

    // when
    setInputValue(valuePowerSupplies, value);
    world.runSynchronously();
    // then - nothing happens (setter is disabled; enabler is disabled)
    Assertions.assertEquals(initialValue, formatToBinaryString(outputSpies));

    // when
    setterPowerSupply.setValue(true);
    world.runSynchronously();
    // then - nothing happens (enabler is disabled)
    Assertions.assertEquals(initialValue, formatToBinaryString(outputSpies));

    // when
    enablePowerSupply.setValue(true);
    world.runSynchronously();
    // then - output is passed on
    Assertions.assertEquals(value, formatToBinaryString(outputSpies));

    // when
    setterPowerSupply.setValue(false);
    world.runSynchronously();
    // then - disabled setter, but enabler is enabled so output is passed on
    Assertions.assertEquals(value, formatToBinaryString(outputSpies));

    // when
    enablePowerSupply.setValue(false);
    world.runSynchronously();
    // then disabled enabler, everything turns to false,
    Assertions.assertEquals(initialValue, formatToBinaryString(outputSpies));
  }
}
