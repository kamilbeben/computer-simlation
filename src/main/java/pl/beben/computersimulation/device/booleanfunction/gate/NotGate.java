package pl.beben.computersimulation.device.booleanfunction.gate;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;
import pl.beben.computersimulation.device.transport.InputBinder;

public class NotGate extends LogicGate {

  public NotGate(String id) {
    super(
      id, 1,
      """
      0 1
      1 0
      """
    );

    //
    //     `inputBinder`
    //           │
    //           │  ┌─ `nandGate`
    //           │  │
    //           ↓  ↓
    //           .--|````|
    // (input)---|  |NAND|---(output)
    //           `--|....|
    //

    final var nandGate = new NandGate(id + "#inputBinder#nand");
    final var inputBinder = new InputBinder(this, "inputBinder");

    nandGate.getInput(0).connectTo(inputBinder);
    nandGate.getInput(1).connectTo(inputBinder);

    inputs[0] = inputBinder;
    outputs[0] = nandGate;
  }

}
