package pl.beben.computersimulation.device.booleanfunction.gate;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;

public class OrGate extends LogicGate {

  public OrGate(String id) {
    super(
      id, 2,
      """
      0 0  0
      0 1  1
      1 0  1
      1 1  1
      """
    );

    //
    //      `input1NotGate`
    //            │
    //            ↓        `nandGate`
    //            |```|        │
    // (input1)---|NOT|---.    ↓
    //            |...|    `---|````|
    //                         |NAND|---(output)
    //            |```|    .---|....|
    // (input2)---|NOT|---`
    //            |...|
    //            ↑
    //            │
    //      `input2NotGate`
    //

    final var input1NotGate = new NotGate(id + "#input1Not");
    final var input2NotGate = new NotGate(id + "#input2Not");
    final var nandGate = new NandGate(id + "#nand");

    nandGate.getInput(0).connectTo(input1NotGate);
    nandGate.getInput(1).connectTo(input2NotGate);

    inputs[0] = input1NotGate.getInput(0);
    inputs[1] = input2NotGate.getInput(0);
    outputs[0] = nandGate;
  }

}
