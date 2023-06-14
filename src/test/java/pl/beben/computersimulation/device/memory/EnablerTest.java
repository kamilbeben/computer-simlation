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

class EnablerTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var initialValue = "0000 0000";
    final var value1 = "0100 0001";
    final var value2 = "1111 0001";

    final var valuePowerSupplies = constructPowerSupplies(world, "ValuePowerSupply", initialValue);
    final var enablePowerSupply = constructPowerSupply(world, "EnablePowerSupply");

    final var enabler = new Enabler("Byte");
    enabler.connectTo(valuePowerSupplies);
    enabler.getEnableInput().connectTo(enablePowerSupply);

    final var outputSpies = constructOutputSpies(enabler.getOutputs());

    // when
    world.runSynchronously();
    // then - nothing happens
    Assertions.assertEquals(initialValue, formatToBinaryString(outputSpies));

    // when
    setInputValue(valuePowerSupplies, value1);
    world.runSynchronously();
    // then - nothing happens, enablePowerSupply is disabled
    Assertions.assertEquals(initialValue, formatToBinaryString(outputSpies));

    // when
    enablePowerSupply.setValue(true);
    world.runSynchronously();
    // then - values which were previously set are now enabled
    Assertions.assertEquals(value1, formatToBinaryString(outputSpies));

    // when
    setInputValue(valuePowerSupplies, value2);
    world.runSynchronously();

    // then
    Assertions.assertEquals(value2, formatToBinaryString(outputSpies));
  }

}
