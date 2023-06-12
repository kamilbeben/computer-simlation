package pl.beben.computersimulation.device.booleanfunction;

import lombok.extern.log4j.Log4j2;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;

@Log4j2
public abstract class BooleanFunction extends AbstractDevice {

  /**
   * For documentation purposes. Also, it's used by BooleanFunctionTest.
   */
  final String truthTable;
  final protected PowerInput[] inputs;
  final protected PowerOutput[] outputs;

  public BooleanFunction(String id, int inputsCount, int outputsCount, String truthTable) {
    super(id);
    this.truthTable = truthTable;

    inputs = new PowerInput[inputsCount];
    outputs = new PowerOutput[outputsCount];
  }

  public PowerInput getInput(int index) {
    return inputs[index];
  }

  public PowerOutput getOutput(int index) {
    return outputs[index];
  }

  public int getInputsCount() {
    return inputs.length;
  }

  public int getOutputsCount() {
    return outputs.length;
  }

}
