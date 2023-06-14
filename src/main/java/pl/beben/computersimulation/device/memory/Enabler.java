package pl.beben.computersimulation.device.memory;

import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.gate.AndGate;
import pl.beben.computersimulation.device.transport.InputBinder;
public class Enabler extends AbstractDevice {

  final PowerInput[] valueInputs;
  final AndGate[] andGates;
  final InputBinder enableInputBinder;

  public Enabler(String id) {
    super(id);

    //
    //
    // (valueInputs[0])---|```|
    //                    |AND|---(outputs[0])
    //                  .-|...|
    //                  |
    //                  .
    //                  ... repeat from 0 to 7
    //                  .
    //                  |
    // (valueInputs[6])-+-|```|
    //                  | |AND|---(outputs[6])
    //                  *-|...|
    //                  |
    // (valueInputs[7])-+-|```|
    //                  | |AND|---(outputs[7])
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
