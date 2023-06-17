package pl.beben.computersimulation.device.memory;

import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.gate.AndGate;
import pl.beben.computersimulation.device.transport.InputBinder;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Enabler extends AbstractDevice {

  final PowerInput[] valueInputs;
  final AndGate[] andGates;
  final InputBinder enableInputBinder;

  public Enabler(String id) {
    super(id);

    //
    //
    // (valueInputs[7])---|```|
    //                    |AND|---(outputs[7])
    //                  .-|...|
    //                  |
    //                  .
    //                  ... repeat from 7 to 0
    //                  .
    //                  |
    // (valueInputs[1])-+-|```|
    //                  | |AND|---(outputs[1])
    //                  *-|...|
    //                  |
    // (valueInputs[0])-+-|```|
    //                  | |AND|---(outputs[0])
    //                  *-|...|
    //                  |
    // (enableInput)----`
    //

    enableInputBinder = new InputBinder(this, id + "#inputBinder");

    valueInputs = new PowerInput[8];
    andGates = new AndGate[8];
    for (int i = 0; i < 8; i++) {
      andGates[i] = new AndGate(id + "#and[" + i + "]");
      andGates[i].getInput(1).connectTo(enableInputBinder);
      valueInputs[i] = andGates[i].getInput(0);
    }
  }

  public PowerInput getEnableInput() {
    return enableInputBinder;
  }

  public void connectTo(PowerOutput[] outputs) {
    if (outputs.length != valueInputs.length)
      throw new IllegalArgumentException(
        "Attempted to connect " + valueInputs.length + " inputs to " + outputs.length + " outputs." +
        "Enabler " + this + ", outputs = [" + Arrays.stream(outputs).map(String::valueOf).collect(Collectors.joining(", ")) + "]"
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
    return andGates;
  }

  public PowerOutput getOutput(int index) {
    return andGates[index];
  }

}
