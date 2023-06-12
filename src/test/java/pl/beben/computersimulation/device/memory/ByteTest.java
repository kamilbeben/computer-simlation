package pl.beben.computersimulation.device.memory;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

class ByteTest {

  @Test
  public void test() {

    // given
    @Cleanup final var world = new TestWorld();

    final var valuePowerSupplies = new VccPowerSupply[8];
    for (int i = 0; i < 8; i++) {
      valuePowerSupplies[i] = new VccPowerSupply("VccPowerSupply[" + i + "]");
      world.registerAsTopLevelDevice(valuePowerSupplies[i]);
    }

    final var setterPowerSupply = new VccPowerSupply("SetterPowerSupply");
    world.registerAsTopLevelDevice(setterPowerSupply);

    final var byteDevice = new Byte("Byte");
    for (int i = 0; i < 8; i++) {
      byteDevice.getValueInput(i).connectTo(valuePowerSupplies[i]);
    }
    byteDevice.getSetterInput().connectTo(setterPowerSupply);

    final var outputSpies = new OutputSpy[8];
    for (int i = 0; i < 8; i++) {
      outputSpies[i] = new OutputSpy();
      outputSpies[i].connectTo(byteDevice.getOutput(i));
    }

    // when
    world.start();

    for (int i = 0; i < 8; i++) {
      testSingleBit(world, valuePowerSupplies, setterPowerSupply, outputSpies, i);
    }
  }

  private void testSingleBit(TestWorld world, VccPowerSupply[] valuePowerSupplies, VccPowerSupply setterPowerSupply, OutputSpy[] outputSpies, int bitIndex) {

    // then
    // Initial state of a memory bit is undefined, it needs to be manually set to a specific state (in this case `false`) first
    step(
      "Set initial state, expect outputValue to be false",
      world,
      () -> {
        for (int i = 0; i < 8; i++) {
          valuePowerSupplies[i].setValue(false);
        }
        setterPowerSupply.setValue(true);
      },
      outputSpies,
      bitIndex,
      false
    );

    step(
      "Disable setter, expect outputValue to remain unchanged",
      world,
      () -> {
        setterPowerSupply.setValue(false);
      },
      outputSpies,
      bitIndex,
      false
    );

    step(
      "Enable value[" + bitIndex + "], expect outputValue to remain unchanged because setter was disabled",
      world,
      () -> {
        valuePowerSupplies[bitIndex].setValue(true);
      },
      outputSpies,
      bitIndex,
      false
    );

    step(
      "Enable setter, expect outputValue[" + bitIndex + "] to catch up with previously set value[" + bitIndex + "] (true)",
      world,
      () -> {
        setterPowerSupply.setValue(true);
      },
      outputSpies,
      bitIndex,
      true
    );

    step(
      "Disable setter, expect outputValue to remain unchanged",
      world,
      () -> {
        setterPowerSupply.setValue(false);
      },
      outputSpies,
      bitIndex,
      true
    );

    step(
      "Disable value[" + bitIndex + "], expect outputValue[" + bitIndex + "] to remain unchanged because setter was disabled",
      world,
      () -> {
        valuePowerSupplies[bitIndex].setValue(false);
      },
      outputSpies,
      bitIndex,
      true
    );

    step(
      "Enable setter, expect outputValue[" + bitIndex + "] to catch up with previously set value[" + bitIndex + "] (false)",
      world,
      () -> {
        setterPowerSupply.setValue(true);
      },
      outputSpies,
      bitIndex,
      false
    );
  }

  private void step(String description,
                    TestWorld world,
                    Runnable action,
                    OutputSpy[] outputSpies,
                    int bitIndex,
                    boolean expectedOutputValue) {

    // given
    final var expectedOutputValues = new ArrayList<Boolean>();
    for (int i = 0; i < 8; i++) {
      expectedOutputValues.add(bitIndex == i ? expectedOutputValue : false);
    }

    // when
    action.run();
    world.await();

    // then
    Assertions.assertEquals(
      expectedOutputValues,
      Arrays.stream(outputSpies).map(OutputSpy::getValue).collect(Collectors.toList()),
      description
    );
  }

}
