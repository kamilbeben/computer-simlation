package pl.beben.computersimulation.device.memory;

import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;

public class Register extends AbstractDevice {

  final Byte byteDevice;
  final Enabler enabler;

  public Register(String id) {
    super(id);

    //
    //                    |```|   |```|
    // (valueInputs[7])---|   |---| E |---(outputs[7])
    // (valueInputs[6])---|   |---| N |---(outputs[6])
    // (valueInputs[5])---| B |---| A |---(outputs[5])
    // (valueInputs[4])---| Y |---| B |---(outputs[4])
    // (valueInputs[3])---| T |---| L |---(outputs[3])
    // (valueInputs[2])---| E |---| E |---(outputs[2])
    // (valueInputs[1])---|   |---| R |---(outputs[1])
    // (valueInputs[0])---|   |---|   |---(outputs[0])
    //                    |...|   |...|
    //                      |       |
    // (setterInput)--------`       |
    // (enableInput)----------------`
    //

    this.byteDevice = new Byte(id + "#byte");
    this.enabler = new Enabler(id + "#enabler");

    for (int i = 0; i < 8; i++) {
      enabler.getValueInput(i).connectTo(byteDevice.getOutput(i));
    }
  }

  public PowerInput getEnableInput() {
    return enabler.getEnableInput();
  }

  public PowerInput getSetterInput() {
    return byteDevice.getSetterInput();
  }

  public void connectTo(PowerOutput[] outputs) {
    byteDevice.connectTo(outputs);
  }

  public PowerInput[] getValueInputs() {
    return byteDevice.getValueInputs();
  }

  public PowerInput getValueInput(int index) {
    return byteDevice.getValueInput(index);
  }

  public PowerOutput[] getOutputs() {
    return enabler.getOutputs();
  }

  public PowerOutput getOutput(int index) {
    return enabler.getOutput(index);
  }

}
