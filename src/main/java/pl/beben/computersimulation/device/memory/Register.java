package pl.beben.computersimulation.device.memory;

import lombok.experimental.FieldDefaults;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
public class Register extends AbstractDevice {

  final Byte byteDevice;
  final Enabler enabler;

  public Register(String id) {
    super(id);

    //
    //                    |```|   |```|
    // (valueInputs[0])---|   |---| E |---(outputs[0])
    // (valueInputs[1])---|   |---| N |---(outputs[1])
    // (valueInputs[2])---| B |---| A |---(outputs[2])
    // (valueInputs[3])---| Y |---| B |---(outputs[3])
    // (valueInputs[4])---| T |---| L |---(outputs[4])
    // (valueInputs[5])---| E |---| E |---(outputs[5])
    // (valueInputs[6])---|   |---| R |---(outputs[6])
    // (valueInputs[7])---|   |---|   |---(outputs[7])
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
