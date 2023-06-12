package pl.beben.computersimulation.device.memory;

import lombok.experimental.FieldDefaults;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.misc.InputBinder;
import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE)
public class Byte extends AbstractDevice {

  final PowerInput[] valueInputs;
  final Bit[] bits;
  final InputBinder setterBridge;

  public Byte(String id) {
    super(id);

    //
    //
    // (valueInputs[0])---|```|
    //                    |BIT|---(outputs[0])
    //                  .-|...|
    //                  |
    //                  .
    //                  ... repeat from 0 to 7
    //                  .
    //                  |
    // (valueInputs[6])-+-|```|
    //                  | |BIT|---(outputs[6])
    //                  *-|...|
    //                  |
    // (valueInputs[7])-+-|```|
    //                  | |BIT|---(outputs[7])
    //                  *-|...|
    //                  |
    // (setterInput)----`
    //

    valueInputs = new PowerInput[8];
    bits = new Bit[8];
    setterBridge = new InputBinder(this, id + "#setterBridge");

    for (int i = 0; i < 8; i++) {
      bits[i] = new Bit(id + "#bit[" + i + "]");
      bits[i].getSetterInput().connectTo(setterBridge);
      valueInputs[i] = bits[i].getValueInput();
    }
  }

  public PowerInput getSetterInput() {
    return setterBridge;
  }

  public PowerInput[] getValueInputs() {
    return valueInputs;
  }

  public PowerInput getValueInput(int index) {
    return valueInputs[index];
  }

  public PowerOutput[] getOutputs() {
    return bits;
  }

  public PowerOutput getOutput(int index) {
    return bits[index];
  }

}
