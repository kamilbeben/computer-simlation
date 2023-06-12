package pl.beben.computersimulation.device.booleanfunction.gate;

import lombok.experimental.Delegate;
import pl.beben.computersimulation.device.abstraction.Device;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.LogicGate;
import pl.beben.computersimulation.device.misc.InputBinder;

public class XorGate extends LogicGate {

  final InputBinder input1Bridge;
  final InputBinder input2Bridge;
  final AndGate andGate;
  final NorGate norGate;
  final OrGate orGate;
  @Delegate(types = PowerOutput.class, excludes = Device.class)
  final NotGate notGate;

  public XorGate(String id) {
    super(
      id, 2,
      """
      0 0  0
      0 1  1
      1 0  1
      1 1  0
      """
    );

    //
    //                  `norGate`
    //                     │
    //     `input1Bridge`  │         `orGate`
    //           │         │            │
    //           ↓         ↓            │   `notGate`
    // (input1)--*---------|```|        │      │
    //           |         |NOR|---.    ↓      ↓
    //           `--.    .-|...|    `---|``|   |```|
    //           .---\--`               |OR|---|NOT|---(output)
    //           |    `----|```|    .---|..|   |...|
    //           |         |AND|---`
    // (input2)--*---------|...|
    //           ↑         ↑
    //           │         │
    //           │     `andGate`
    //           │
    //     `input2Bridge`
    //

    input1Bridge = new InputBinder(this, id + "#input1Bridge");
    input2Bridge = new InputBinder(this, id + "#input2Bridge");
    andGate = new AndGate(id + "#andGate");
    norGate = new NorGate(id + "#norGate");
    orGate = new OrGate(id + "#orGate");
    notGate = new NotGate(id + "#notGate");

    // nor
    norGate.getInput(0).connectTo(input1Bridge);
    norGate.getInput(1).connectTo(input2Bridge);

    // and
    andGate.getInput(0).connectTo(input1Bridge);
    andGate.getInput(1).connectTo(input2Bridge);

    // or
    orGate.getInput(0).connectTo(norGate);
    orGate.getInput(1).connectTo(andGate);

    // not
    notGate.getInput(0).connectTo(orGate);

    inputs[0] = input1Bridge;
    inputs[1] = input2Bridge;
    outputs[0] = notGate;
  }

}
