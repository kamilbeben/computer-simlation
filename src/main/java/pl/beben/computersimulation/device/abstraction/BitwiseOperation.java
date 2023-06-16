package pl.beben.computersimulation.device.abstraction;

import lombok.Getter;

@Getter
public abstract class BitwiseOperation extends AbstractDevice {

  protected final PowerInput[][] inputs;
  protected PowerOutput[] output;

  protected BitwiseOperation(String id, int inputsCount) {
    super(id);

    inputs = new PowerInput[inputsCount][8];
    output = new PowerOutput[8];
  }

  public PowerInput[] getInput(int index) {
    return inputs[index];
  }

}
