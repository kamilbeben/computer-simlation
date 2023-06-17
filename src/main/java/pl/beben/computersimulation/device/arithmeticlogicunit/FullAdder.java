package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Getter;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.gate.AndGate;
import pl.beben.computersimulation.device.booleanfunction.gate.OrGate;
import pl.beben.computersimulation.device.booleanfunction.gate.XorGate;
import pl.beben.computersimulation.device.transport.InputBinder;

@Getter
public class FullAdder extends AbstractDevice {

  final PowerInput[] inputs;
  final PowerInput carryInput;
  final PowerOutput output;
  final PowerOutput carryOutput;

  public FullAdder(String id) {
    super(id);

    //
    // (inputs[0])---*---|```|
    //               |   |XOR|---*--.
    // (inputs[1])---|-*-|...|   |   `-|```|
    //               | |         |     |XOR|---(output)
    // (carryInput)--|-|---------|-*---|...|
    //               | |         | |
    //               | |         | `--|```|
    //               | |         |    |AND|---.
    //               | |         `----|...|    `--|``|
    //               | |                          |OR|---(carryOutput)
    //               | `--------------|```|    .--|..|
    //               |                |AND|---`
    //               `----------------|...|
    //

    inputs = new PowerInput[2];
    inputs[0] = new InputBinder(this, id + "#inputs[0]");
    inputs[1] = new InputBinder(this, id + "#inputs[1]");
    carryInput = new InputBinder(this, id + "#carryInput");

    final var inputXorGate = new XorGate(id + "#inputXorGate");
    inputXorGate.getInput(0).connectTo((InputBinder) inputs[0]);
    inputXorGate.getInput(1).connectTo((InputBinder) inputs[1]);

    final var inputXorGateOutputInputBinder = new InputBinder(this, "inputXorGateOutputInputBinder");
    inputXorGateOutputInputBinder.connectTo(inputXorGate);

    final var outputXorGate = new XorGate(id + "#outputXorGate");
    outputXorGate.getInput(0).connectTo(inputXorGateOutputInputBinder);
    outputXorGate.getInput(1).connectTo((InputBinder) carryInput);

    final var xorCarryAndGate = new AndGate(id + "#xorCarryAndGate");
    xorCarryAndGate.getInput(0).connectTo((InputBinder) carryInput);
    xorCarryAndGate.getInput(1).connectTo(inputXorGateOutputInputBinder);

    final var inputsAndGate = new AndGate(id + "#inputsAndGate");
    inputsAndGate.getInput(0).connectTo((InputBinder) inputs[0]);
    inputsAndGate.getInput(1).connectTo((InputBinder) inputs[1]);

    final var carryOutputOrGate = new OrGate(id + "#carryOutputOrGate");
    carryOutputOrGate.getInput(0).connectTo(xorCarryAndGate);
    carryOutputOrGate.getInput(1).connectTo(inputsAndGate);

    output = outputXorGate;
    carryOutput = carryOutputOrGate;
  }

  public PowerInput getInput(int index) {
    return inputs[index];
  }

}
