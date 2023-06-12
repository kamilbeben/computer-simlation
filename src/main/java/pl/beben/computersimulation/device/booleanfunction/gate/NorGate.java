package pl.beben.computersimulation.device.booleanfunction.gate;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;

public class NorGate extends LogicGate {


  public NorGate(String id) {
    super(
      id, 2,
      """
      0 0  1
      0 1  0
      1 0  0
      1 1  0
      """
    );

    //
    //         `orGate`
    //            │
    //            │   `notGate`
    //            │      │
    //            ↓      ↓
    // (input1)---|``|   |```|
    //            |OR|---|NOT|---(output)
    // (input2)---|..|   |...|
    //

    final var orGate = new OrGate(id + "#or");

    final var notGate = new NotGate(id + "#outputNot");
    notGate.getInput(0).connectTo(orGate);

    inputs[0] = orGate.getInput(0);
    inputs[1] = orGate.getInput(1);
    outputs[0] = notGate;
  }
}
