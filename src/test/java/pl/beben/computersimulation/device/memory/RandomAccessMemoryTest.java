package pl.beben.computersimulation.device.memory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.TestWorld;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.gate.AndGate;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static org.apache.logging.log4j.Level.INFO;
import static pl.beben.computersimulation.TestUtils.constructOutputSpies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupplies;
import static pl.beben.computersimulation.TestUtils.constructPowerSupply;
import static pl.beben.computersimulation.LogUtils.restoreDefaultLogLevel;
import static pl.beben.computersimulation.LogUtils.setLogLevel;

class RandomAccessMemoryTest {

  private static final boolean[] REGISTER_1_ADDRESS = new boolean[] { false, false, false, false, false, false, false, true  }; // [0][1]
  private static final boolean[] REGISTER_2_ADDRESS = new boolean[] { false, false, false, false, false, false, true,  false }; // [0][2]
  private static final boolean[] REGISTER_3_ADDRESS = new boolean[] { false, false, false, false, false, false, true,  true  }; // [0][3]

  private static final boolean[] VALUE_INITIAL =      new boolean[] { false, false, false, false, false, false, false, false };
  private static final boolean[] VALUE_1 =            new boolean[] { false, false, false, false, false, true,  false, false };
  private static final boolean[] VALUE_2 =            new boolean[] { false, false, false, false, false, true,  false, true  };

  TestWorld world;
  VccPowerSupply[] marInputPowerSuppliers;
  VccPowerSupply marSetterInput;
  VccPowerSupply[] busInputPowerSuppliers;
  VccPowerSupply busEnableInput;
  VccPowerSupply busSetterInput;
  OutputSpy[] busOutputSpies;
  RandomAccessMemory ram;
  AndGate activator1;
  AndGate activator2;
  Register register1;
  Register register2;

  @BeforeEach
  void initialize() {
    // Logging the activity of over 70,000 devices in RAM using the default log level (DEBUG) would significantly
    // impact the runtime of this testcase and result in excessive log output that is not readable anyway
    setLogLevel(INFO);

    // given
    world = new TestWorld();

    marInputPowerSuppliers = constructPowerSupplies(world, "marInputPowerSuppliers", "0000 0000");
    marSetterInput = constructPowerSupply(world, "marSetterInput", "1");

    busInputPowerSuppliers = constructPowerSupplies(world, "busInputPowerSuppliers", "0000 0000");
    busEnableInput = constructPowerSupply(world, "busEnableInput");
    busSetterInput = constructPowerSupply(world, "busSetterInput");
    busOutputSpies = constructOutputSpies("busOutputSpies", 8);

    ram = new RandomAccessMemory("ram");

    ram.getMemoryAddressRegister().getSetterInput().connectTo(marSetterInput);
    ram.getMemoryAddressRegister().connectTo(marInputPowerSuppliers);
    ram.getBus().connectOutputToBus(busInputPowerSuppliers);
    ram.getBus().connectInputToBus(busOutputSpies);
    ram.getBusEnableInput().connectTo(busEnableInput);
    ram.getBusSetterInput().connectTo(busSetterInput);

    activator1 = ram.activators[0][1];
    activator2 = ram.activators[0][2];
    register1 = ram.registers[0][1];
    register2 = ram.registers[0][2];

  }

  @AfterEach
  public void cleanup() {
    world.close();
    restoreDefaultLogLevel();
  }

  @Test
  public void test() {

    // when
    world.runSynchronously();
    // then
    assertThatValuesAreEqualTo(busOutputSpies, VALUE_INITIAL);

    // internal RAM devices asserts
    assertThatValuesAreEqualTo(register1.getValueInputs(), VALUE_INITIAL);
    assertThatValuesAreEqualTo(register2.getValueInputs(), VALUE_INITIAL);

    // when
    moveDataToRegister(REGISTER_1_ADDRESS, VALUE_1);
    // then
    // value is available on the bus
    assertThatValuesAreEqualTo(busOutputSpies, VALUE_1);

    // internal RAM devices asserts
    // decoders are pointing to register1 and not register2
    Assertions.assertTrue(activator1.getValue());
    Assertions.assertFalse(activator2.getValue());
    // register 1 is not enabled but settable
    Assertions.assertFalse(register1.getEnableInput().getValue());
    Assertions.assertTrue(register1.getSetterInput().getValue());
    // register 2 is neither enabled nor settable
    Assertions.assertFalse(register2.getEnableInput().getValue());
    Assertions.assertFalse(register2.getSetterInput().getValue());
    // register1 values has been set
    assertThatValuesAreEqualTo(register1.byteDevice.getOutputs(), VALUE_1);
    // register2 values has not been affected
    assertThatValuesAreEqualTo(register2.byteDevice.getOutputs(), VALUE_INITIAL);

    // when
    disableEnablerAndSetter();
    // then
    Assertions.assertFalse(register1.getEnableInput().getValue());
    Assertions.assertFalse(register1.getSetterInput().getValue());

    // when
    moveDataToRegister(REGISTER_2_ADDRESS, VALUE_2);
    // then
    // value is available on the bus
    assertThatValuesAreEqualTo(busOutputSpies, VALUE_2);

    // internal RAM devices asserts
    // decoders are now pointing to register2 and not register1
    Assertions.assertFalse(activator1.getValue());
    Assertions.assertTrue(activator2.getValue());
    // register 1 is neither enabled nor settable
    Assertions.assertFalse(register1.getEnableInput().getValue());
    Assertions.assertFalse(register1.getSetterInput().getValue());
    // register 2 is not enabled but settable
    Assertions.assertFalse(register2.getEnableInput().getValue());
    Assertions.assertTrue(register2.getSetterInput().getValue());
    // register1 values has not been affected
    assertThatValuesAreEqualTo(register1.byteDevice.getOutputs(), VALUE_1);
    // register2 values has been set
    assertThatValuesAreEqualTo(register2.byteDevice.getOutputs(), VALUE_2);


    // when
    disableEnablerAndSetter();
    // then
    // register2 is no longer enabled or able to set value
    Assertions.assertFalse(register2.getEnableInput().getValue());
    Assertions.assertFalse(register2.getSetterInput().getValue());


    // when
    disableBusSetter();
    resetBusInputPowerSuppliers();
    // then
    assertThatValuesAreEqualTo(busOutputSpies, VALUE_INITIAL);


    // when
    readDataFromRegister(REGISTER_1_ADDRESS);
    // then
    assertThatValuesAreEqualTo(busOutputSpies, VALUE_1);


    // when
    readDataFromRegister(REGISTER_2_ADDRESS);
    // then
    assertThatValuesAreEqualTo(busOutputSpies, VALUE_2);


    // when
    readDataFromRegister(REGISTER_3_ADDRESS);
    // then
    assertThatValuesAreEqualTo(busOutputSpies, VALUE_INITIAL);
  }

  private void moveDataToRegister(boolean[] registerAddress, boolean[] data) {
    setPowerSupplyValues(marInputPowerSuppliers, registerAddress);
    world.runSynchronously();

    busEnableInput.setValue(false);
    busSetterInput.setValue(true);
    setPowerSupplyValues(busInputPowerSuppliers, data);
    world.runSynchronously();
  }

  private void disableEnablerAndSetter() {
    busEnableInput.setValue(false);
    busSetterInput.setValue(false);
    world.runSynchronously();
  }

  private void disableBusSetter() {
    busSetterInput.setValue(false);
    world.runSynchronously();
  }

  private void resetBusInputPowerSuppliers() {
    setPowerSupplyValues(busInputPowerSuppliers, VALUE_INITIAL);
    world.runSynchronously();
  }

  private void readDataFromRegister(boolean[] registerAddress) {
    busEnableInput.setValue(true);
    busSetterInput.setValue(false);
    setPowerSupplyValues(marInputPowerSuppliers, registerAddress);
    world.runSynchronously();
  }

  private void assertThatValuesAreEqualTo(PowerInput[] inputs, boolean[] value) {
    assert inputs.length == value.length;

    Assertions.assertEquals(
      formatOutput(value),
      formatOutput(Arrays.stream(inputs).map(PowerInput::getValue).collect(Collectors.toList()))
    );
  }

  private void assertThatValuesAreEqualTo(PowerOutput[] inputs, boolean[] value) {
    assert inputs.length == value.length;

    Assertions.assertEquals(
      formatOutput(value),
      formatOutput(Arrays.stream(inputs).map(PowerOutput::getValue).collect(Collectors.toList()))
    );
  }

  private String formatOutput(List<Boolean> values) {
    final var valuesArray = new boolean[8];

    for (int i = 0; i < valuesArray.length; i++) {
      valuesArray[i] = values.get(i);
    }

    return formatOutput(valuesArray);
  }

  private String formatOutput(boolean[] values) {
    final var outputBuilder = new StringBuilder();

    for (int i = 0; i < values.length; i++) {
      outputBuilder.append(
        values[i] ? "1 " : "0 "
      );
    }

    return outputBuilder.toString().trim();
  }

  private void setPowerSupplyValues(VccPowerSupply[] powerSupplies, boolean[] registerAddress) {
    assert powerSupplies.length == registerAddress.length;

    for (int i = 0; i < powerSupplies.length; i++) {
      powerSupplies[i].setValue(registerAddress[i]);
    }
  }

}