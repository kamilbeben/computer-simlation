package pl.beben.computersimulation.device.memory;

import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.misc.InputBinder;
import java.util.Arrays;
import java.util.stream.Collectors;
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

  public void connectTo(PowerOutput[] outputs) {
    if (outputs.length != valueInputs.length)
      throw new IllegalArgumentException(
        "Attempted to connect " + valueInputs.length + " inputs to " + outputs.length + " outputs." +
        "Byte " + this + ", outputs = [" + Arrays.stream(outputs).map(String::valueOf).collect(Collectors.joining(", ")) + "]"
      );

    for (int i = 0; i < valueInputs.length; i++) {
      valueInputs[i].connectTo(outputs[i]);
    }
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
