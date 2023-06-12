package pl.beben.computersimulation.device.memory;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;

class BitTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var valuePowerSupply = new VccPowerSupply("ValuePowerSupply");
    world.registerAsTopLevelDevice(valuePowerSupply);

    final var setterPowerSupply = new VccPowerSupply("SetterPowerSupply");
    world.registerAsTopLevelDevice(setterPowerSupply);

    final var bit = new Bit("Bit");
    bit.getValueInput().connectTo(valuePowerSupply);
    bit.getSetterInput().connectTo(setterPowerSupply);

    final var outputSpy = new OutputSpy();
    outputSpy.connectTo(bit);

    // when
    world.start();

    // then
    // Initial state of a memory bit is undefined, it needs to be manually set to a specific state (in this case `false`) first
    step(
      "Set initial state, expect outputValue to be false",
      world,
      () -> {
        assert !valuePowerSupply.getValue();
        assert !setterPowerSupply.getValue();

        setterPowerSupply.setValue(true);
      },
      outputSpy,
      false
    );

    step(
      "Disable setter, expect outputValue to remain unchanged",
      world,
      () -> {
        assert !valuePowerSupply.getValue();
        assert setterPowerSupply.getValue();

        setterPowerSupply.setValue(false);
      },
      outputSpy,
      false
    );

    step(
      "Enable value, expect outputValue to remain unchanged because setter was disabled",
      world,
      () -> {
        assert !valuePowerSupply.getValue();
        assert !setterPowerSupply.getValue();

        valuePowerSupply.setValue(true);
      },
      outputSpy,
      false
    );

    step(
      "Enable setter, expect outputValue to catch up with previously set value (true)",
      world,
      () -> {
        assert valuePowerSupply.getValue();
        assert !setterPowerSupply.getValue();

        setterPowerSupply.setValue(true);
      },
      outputSpy,
      true
    );

    step(
      "Disable setter, expect outputValue to remain unchanged",
      world,
      () -> {
        assert valuePowerSupply.getValue();
        assert setterPowerSupply.getValue();

        setterPowerSupply.setValue(false);
      },
      outputSpy,
      true
    );

    step(
      "Disable value, expect outputValue to remain unchanged because setter was disabled",
      world,
      () -> {
        assert valuePowerSupply.getValue();
        assert !setterPowerSupply.getValue();

        valuePowerSupply.setValue(false);
      },
      outputSpy,
      true
    );

    step(
      "Enable setter, expect outputValue to catch up with previously set value (false)",
      world,
      () -> {
        assert !valuePowerSupply.getValue();
        assert !setterPowerSupply.getValue();

        setterPowerSupply.setValue(true);
      },
      outputSpy,
      false
    );
  }

  private void step(String description, TestWorld world, Runnable action, OutputSpy outputSpy, boolean expectedOutputValue) {
    // when
    action.run();
    world.await();

    // then
    Assertions.assertEquals(
      expectedOutputValue,
      outputSpy.getValue(),
      description
    );
  }
}
