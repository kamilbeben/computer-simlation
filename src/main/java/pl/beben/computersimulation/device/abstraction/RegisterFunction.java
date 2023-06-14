package pl.beben.computersimulation.device.abstraction;

import pl.beben.computersimulation.device.memory.Register;

public abstract class RegisterFunction extends AbstractDevice {

  protected final Register[] inputs;
  protected final Register[] outputs;

  protected RegisterFunction(String id, int inputsCount, int outputsCount) {
    super(id);

    inputs = new Register[inputsCount];
    outputs = new Register[outputsCount];
  }

  public Register getInput(int index) {
    return inputs[index];
  }

  public Register getOutput(int index) {
    return outputs[index];
  }

  public int getInputsCount() {
    return inputs.length;
  }

  public int getOutputsCount() {
    return outputs.length;
  }

}
