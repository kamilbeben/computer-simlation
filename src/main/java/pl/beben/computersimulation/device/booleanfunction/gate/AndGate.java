package pl.beben.computersimulation.device.booleanfunction.gate;

import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.LogicGate;

public class AndGate extends LogicGate implements PowerOutput {

  public AndGate(String id) {
    super(
      id, 2,
      """
      0 0  0
      0 1  0
      1 0  0
      1 1  1
      """
    );

    //
    //        `nandGate`
    //            │
    //            │     `notGate`
    //            │        │
    //            ↓        ↓
    // (input1)---|````|   |```|
    //            |NAND|---|NOT|---(output)
    // (input2)---|....|   |...|
    //

    final var nandGate = new NandGate(id + "#nand");
    final var notGate = new NotGate(id + "#not");
    notGate.getInput(0).connectTo(nandGate);

    inputs[0] = nandGate.getInput(0);
    inputs[1] = nandGate.getInput(1);
    outputs[0] = notGate;
  }

}
