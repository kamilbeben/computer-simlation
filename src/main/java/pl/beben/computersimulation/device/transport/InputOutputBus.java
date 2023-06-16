package pl.beben.computersimulation.device.transport;

import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.Device;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InputOutputBus extends AbstractDevice {

  final InputBinder[] inputs;
  final OutputBinder[] outputs;
  final int width;

  public InputOutputBus(String id, int width) {
    super(id);

    this.width = width;

    inputs = new InputBinder[width];
    outputs = new OutputBinder[width];

    for (int i = 0; i < width; i++) {
      outputs[i] = new OutputBinder(id + "#output[" + i + "]");
      inputs[i] = new InputBinder(this, id + "#input[" + i + "]");
      inputs[i].connectTo(outputs[i]);
    }
  }

  public void connectInputToBus(PowerInput[] devices) {
    assertThatDevicesLengthIsEqualToBusWidth(devices);

    for (int i = 0; i < devices.length; i++) {
      devices[i].connectTo(inputs[i]);
    }
  }

  public void connectOutputToBus(PowerOutput[] devices) {
    assertThatDevicesLengthIsEqualToBusWidth(devices);

    for (int i = 0; i < devices.length; i++) {
      outputs[i].bindOutput(devices[i]);
    }
  }

  private void assertThatDevicesLengthIsEqualToBusWidth(Device[] inputOutputDevices) {

    if (inputOutputDevices.length != width)
      throw new IllegalArgumentException(
        "Bus width and the inputOutputDevices.length does not match. " +
        "this.width = " + width + ", " +
        "inputOutputDevices.length = " + inputOutputDevices.length + ", " +
        "inputOutputDevices = [" + Arrays.stream(inputOutputDevices).map(String::valueOf).collect(Collectors.joining(", ")) + "]"
      );
  }

}
