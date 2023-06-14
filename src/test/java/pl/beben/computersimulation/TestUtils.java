package pl.beben.computersimulation;

import org.junit.jupiter.api.Assertions;
import pl.beben.computersimulation.device.OutputSpy;
import pl.beben.computersimulation.device.World;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.abstraction.SettablePowerOutput;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;

public class TestUtils {

  public static boolean[] parseBinaryString(String binaryStringRaw) {
    final var binaryString = binaryStringRaw.replaceAll("\\s+", "");
    final var value = new boolean[binaryString.length()];
    for (int i = 0; i < binaryString.length(); i++) {
      value[i] = binaryString.charAt(i) == '1';
    }
    return value;
  }

  public static String formatToBinaryString(OutputSpy outputSpy) {
    return formatToBinaryString(new boolean[]{ outputSpy.getValue() });
  }

  public static String formatToBinaryString(OutputSpy[] outputSpies) {
    final var values = new boolean[outputSpies.length];
    for (int i = 0; i < outputSpies.length; i++) {
      values[i] = outputSpies[i].getValue();
    }
    return formatToBinaryString(values);
  }

  public static String formatToBinaryString(VccPowerSupply[] powerInputs) {
    final var values = new boolean[powerInputs.length];
    for (int i = 0; i < powerInputs.length; i++) {
      values[i] = powerInputs[i].getValue();
    }
    return formatToBinaryString(values);
  }

  public static String formatToBinaryString(boolean[] value) {
    final var binaryStringBuilder = new StringBuilder(value.length);

    for (int i = 0; i < value.length; i++) {
      binaryStringBuilder.append(
        value[i]
          ? "1"
          : "0"
      );
    }

    return normalizeBinaryString(binaryStringBuilder.toString().trim());
  }

  public static String normalizeBinaryString(String binaryString) {
    final var binaryStringWithoutSpaces = binaryString.replaceAll("\\s+", "");

    return binaryStringWithoutSpaces.length() == 8
      ? binaryStringWithoutSpaces.substring(0, 4) + " " + binaryStringWithoutSpaces.substring(4, 8)
      : binaryString;
  }

  public static VccPowerSupply[] constructPowerSupplies(World world, String initialValueBinaryString) {
    return constructPowerSupplies(world, "powerSupplies", parseBinaryString(initialValueBinaryString));
  }

  public static VccPowerSupply[] constructPowerSupplies(World world, String id, String initialValueBinaryString) {
    return constructPowerSupplies(world, id, parseBinaryString(initialValueBinaryString));
  }

  public static VccPowerSupply[] constructPowerSupplies(World world, String id, boolean[] initialValue) {
    final var powerSupplies = new VccPowerSupply[initialValue.length];
    for (int i = 0; i < initialValue.length; i++) {
      powerSupplies[i] = constructPowerSupply(world, id + "[" + i + "]", initialValue[i]);
    }
    return powerSupplies;
  }

  public static VccPowerSupply constructPowerSupply(World world, String initialValueBinaryString) {
    return constructPowerSupply(world, parseBinaryString(initialValueBinaryString)[0]);
  }

  public static VccPowerSupply constructPowerSupply(World world, boolean initialValue) {
    return constructPowerSupply(world, "powerSupply", initialValue);
  }

  public static VccPowerSupply constructPowerSupply(World world, String id, String initialValueBinaryString) {
    return constructPowerSupply(world, "powerSupply", parseBinaryString(initialValueBinaryString)[0]);
  }

  public static VccPowerSupply constructPowerSupply(World world, String id, boolean initialValue) {
    final var powerSupply = new VccPowerSupply(id);
    powerSupply.setValue(initialValue);
    world.registerAsTopLevelDevice(powerSupply);
    return powerSupply;
  }

  public static OutputSpy[] constructOutputSpies(String id, int length) {
    final var outputSpies = new OutputSpy[length];
    for (int i = 0; i < length; i++) {
      outputSpies[i] = new OutputSpy(id + "[" + i + "]");
    }
    return outputSpies;
  }

  public static OutputSpy[] constructOutputSpies(PowerOutput[] connectTo) {
    return constructOutputSpies("outputSpy", connectTo);
  }

  public static OutputSpy[] constructOutputSpies(String id, PowerOutput[] connectTo) {
    final var outputSpies = new OutputSpy[connectTo.length];
    for (int i = 0; i < connectTo.length; i++) {
      outputSpies[i] = constructOutputSpy(id + "[" + i + "]", connectTo[i]);
    }
    return outputSpies;
  }

  public static OutputSpy constructOutputSpy(PowerOutput connectTo) {
    return constructOutputSpy("outputSpy", connectTo);
  }

  public static OutputSpy constructOutputSpy(String id, PowerOutput connectTo) {
    final var outputSpy = new OutputSpy(id);
    outputSpy.connectTo(connectTo);
    return outputSpy;
  }

  public static void setInputValue(SettablePowerOutput[] outputs, String valueBinaryString) {
    setInputValue(outputs, parseBinaryString(valueBinaryString));
  }

  public static void setInputValue(SettablePowerOutput[] outputs, boolean[] value) {
    Assertions.assertEquals(outputs.length, value.length);

    for (int i = 0; i < value.length; i++) {
      outputs[i].setValue(value[i]);
    }
  }

}
