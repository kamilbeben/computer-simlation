package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Getter;
import pl.beben.computersimulation.device.abstraction.BitwiseOperation;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.abstraction.SettablePowerOutput;
import pl.beben.computersimulation.device.abstraction.composite.PowerInputComposite;
import pl.beben.computersimulation.device.abstraction.composite.PowerOutputComposite;
import static pl.beben.computersimulation.device.arithmeticlogicunit.Shifter.ShifterType.LEFT;

@Getter
public abstract class Shifter extends BitwiseOperation {

  public enum ShifterType {
    LEFT,
    RIGHT
  }

  final PowerInput shiftInput;
  final PowerOutput shiftOutput;

  protected Shifter(String id, ShifterType type) {
    super(id, 1);

    //
    // Left shifter:
    //        (shiftOutput)
    //              |
    // (input[7])---`  .-----------(outputs[7])    |
    // (input[6])-----` .----------(outputs[6])    |                     .-`|
    // (input[5])------` .---------(outputs[5])    |                    |   |---(shiftOutput)
    // (input[4])-------` .--------(outputs[4])    |    (inputBus)======| L |===(outputBus)
    // (input[3])--------` .-------(outputs[3])    |    (shiftInput)----|   |
    // (input[2])---------` .------(outputs[2])    |                    |.-`
    // (input[1])----------` .-----(outputs[1])    |                    `
    // (input[0])-----------`   .--(outputs[0])    |
    //                          |
    //                     (shiftInput)
    //
    // Right shifter:
    //                    (shiftInput)
    //                         |
    // (input[7])------------. `---(outputs[7])    |
    // (input[6])-----------. `----(outputs[6])    |                    |`-.
    // (input[5])----------. `-----(outputs[5])    |    (shiftInput)----|   |
    // (input[4])---------. `------(outputs[4])    |    (inputBus)======| R |===(outputBus)
    // (input[3])--------. `-------(outputs[3])    |                    |   |---(shiftOutput)
    // (input[2])-------. `--------(outputs[2])    |                     `-.|
    // (input[1])------. `---------(outputs[1])    |                        `
    // (input[0])---.   `----------(outputs[0])    |
    //              |
    //         (shiftOutput)
    //

    final var input = inputs[0];
    final var output = new PowerOutputComposite[8];
    for (int i = 0; i < 8; i++) {
      output[i] = new PowerOutputComposite(this, "#output[" + i + "]");
      this.output[i] = output[i];
    }

    shiftOutput = new PowerOutputComposite(this, "#shiftOutput");
    
    if (type == LEFT) {
      input[7] = constructInput(7, (SettablePowerOutput) shiftOutput);
      input[6] = constructInput(6, output[7]);
      input[5] = constructInput(5, output[6]);
      input[4] = constructInput(4, output[5]);
      input[3] = constructInput(3, output[4]);
      input[2] = constructInput(2, output[3]);
      input[1] = constructInput(1, output[2]);
      input[0] = constructInput(0, output[1]);
      shiftInput = constructInput("#shiftInput", output[0]);
    } else {
      shiftInput = constructInput("#shiftInput", output[7]);
      input[7] = constructInput(7, output[6]);
      input[6] = constructInput(6, output[5]);
      input[5] = constructInput(5, output[4]);
      input[4] = constructInput(4, output[3]);
      input[3] = constructInput(3, output[2]);
      input[2] = constructInput(2, output[1]);
      input[1] = constructInput(1, output[0]);
      input[0] = constructInput(0, (SettablePowerOutput) shiftOutput);
    }
  }

  private PowerInputComposite constructInput(int id, SettablePowerOutput connectedOutput) {
    return constructInput("#input[" + id + "]", connectedOutput);
  }

  private PowerInputComposite constructInput(String id, SettablePowerOutput connectedOutput) {
    return new PowerInputComposite(this, id, (value) -> connectedOutput.setValue(value));
  }

  public PowerInput[] getInput() {
    return getInput(0);
  }

}
