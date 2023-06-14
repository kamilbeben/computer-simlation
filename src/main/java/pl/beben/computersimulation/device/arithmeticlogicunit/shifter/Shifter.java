package pl.beben.computersimulation.device.arithmeticlogicunit.shifter;

import lombok.Getter;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.abstraction.RegisterFunction;
import pl.beben.computersimulation.device.memory.Register;
import static pl.beben.computersimulation.device.arithmeticlogicunit.shifter.Shifter.ShifterType.LEFT;
import static pl.beben.computersimulation.device.arithmeticlogicunit.shifter.Shifter.ShifterType.RIGHT;

@Getter
public abstract class Shifter extends RegisterFunction {

  enum ShifterType {
    LEFT,
    RIGHT
  }

  final PowerInput shiftInput;
  final PowerOutput shiftOutput;

  protected Shifter(String id, ShifterType type) {
    super(id, 1, 1);

    //
    // Left shifter:
    //                    (shiftInput)
    //                          |
    // (inputs[0])---|```````|--`  .-----------|````````|---(outputs[0])    |
    // (inputs[1])---|       |----` .----------|        |---(outputs[1])    |                     .-`|
    // (inputs[2])---| Input |-----` .---------| Output |---(outputs[2])    |                    |   |---(shiftOutput)
    // (inputs[3])---| Regi- |------` .--------| Regi-  |---(outputs[3])    |    (inputBus)======| L |===(outputBus)
    // (inputs[4])---| ster  |-------` .-------| ster   |---(outputs[4])    |    (shiftInput)----|   |
    // (inputs[5])---|       |--------` .------|        |---(outputs[5])    |                    |.-`
    // (inputs[6])---|       |---------` .-----|        |---(outputs[6])    |                    `
    // (inputs[7])---|.......|----------`   .--|........|---(outputs[7])    |
    //                                      |
    //                                 (shiftOutput)
    //
    // Right shifter:
    //                                (shiftOutput)
    //                                      |
    // (inputs[0])---|```````|------------. `--|````````|---(outputs[0])    |
    // (inputs[1])---|       |-----------. `---|        |---(outputs[1])    |                    |`-.
    // (inputs[2])---| Input |----------. `----| Output |---(outputs[2])    |    (shiftInput)----|   |
    // (inputs[3])---| Regi- |---------. `-----| Regi-  |---(outputs[3])    |    (inputBus)======| R |===(outputBus)
    // (inputs[4])---| ster  |--------. `------| ster   |---(outputs[4])    |                    |   |---(shiftOutput)
    // (inputs[5])---|       |-------. `-------|        |---(outputs[5])    |                     `-.|
    // (inputs[6])---|       |------. `--------|        |---(outputs[6])    |                        `
    // (inputs[7])---|.......|---.   `---------|........|---(outputs[7])    |
    //                           |
    //                      (shiftInput)
    //

    inputs[0] = new Register(id + "#inputs[0]");
    outputs[0] = new Register(id + "#outputs[0]");

    if (type == LEFT) {
      shiftInput = outputs[0].getValueInput(7);
      shiftOutput = inputs[0].getOutput(0);

      outputs[0].getValueInput(0).connectTo(inputs[0].getOutput(1));
      outputs[0].getValueInput(1).connectTo(inputs[0].getOutput(2));
      outputs[0].getValueInput(2).connectTo(inputs[0].getOutput(3));
      outputs[0].getValueInput(3).connectTo(inputs[0].getOutput(4));
      outputs[0].getValueInput(4).connectTo(inputs[0].getOutput(5));
      outputs[0].getValueInput(5).connectTo(inputs[0].getOutput(6));
      outputs[0].getValueInput(6).connectTo(inputs[0].getOutput(7));
    } else {
      shiftInput = outputs[0].getValueInput(0);
      shiftOutput = inputs[0].getOutput(7);

      outputs[0].getValueInput(1).connectTo(inputs[0].getOutput(0));
      outputs[0].getValueInput(2).connectTo(inputs[0].getOutput(1));
      outputs[0].getValueInput(3).connectTo(inputs[0].getOutput(2));
      outputs[0].getValueInput(4).connectTo(inputs[0].getOutput(3));
      outputs[0].getValueInput(5).connectTo(inputs[0].getOutput(4));
      outputs[0].getValueInput(6).connectTo(inputs[0].getOutput(5));
      outputs[0].getValueInput(7).connectTo(inputs[0].getOutput(6));
    }
  }

  public Register getInput() {
    return getInput(0);
  }

  public Register getOutput() {
    return getOutput(0);
  }

  public static class LeftShifter extends Shifter {
    public LeftShifter(String id) {
      super(id, LEFT);
    }
  }

  public static class RightShifter extends Shifter {
    public RightShifter(String id) {
      super(id, RIGHT);
    }
  }

}
