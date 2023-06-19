package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Getter;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.gate.AndGate;
import pl.beben.computersimulation.device.booleanfunction.gate.MultiInputAndGate;
import pl.beben.computersimulation.device.booleanfunction.gate.NotGate;
import pl.beben.computersimulation.device.booleanfunction.gate.OrGate;
import pl.beben.computersimulation.device.booleanfunction.gate.XorGate;
import pl.beben.computersimulation.device.transport.InputBinder;

@Getter
public class BitComparator extends AbstractDevice {

  final PowerInput[] inputs;
  final PowerInput allBitsAboveAreEqualInput;
  final PowerInput firstInputIsLargerInput;

  final PowerOutput output;
  final PowerOutput allBitsSoFarAreEqualOutput;
  final PowerOutput firstInputIsLargerOutput;

  public BitComparator(String id) {
    super(id);

    //
    //                                            (firstInputIsLargerInput)
    //                                                        |
    //                           (allBitsAboveAreEqualInput)  |
    //                                        |               |
    //                                        |               |
    //                                        *----|```|      |
    //             .--------------------------|----|AND|---.  |
    //             |                          | .--|...|   |  |
    // (inputs[0])-*-|```|                    | |          |  |
    //               |XOR|---*----------------|-*----------|--|---(output)
    // (inputs[1])---|...|   |                |            |  |
    //                       |  |```|         |            |  |
    //                       `--|NOT|-----.   |            |  |
    //                          |...|     |   |            |  |
    //                                    |```|            |``|
    //                                    |AND|            |OR|---.
    //                                    |...|            |..|   |
    //                                      |                     |
    //                         (allBitsSoFarAreEqualOutput)       |
    //                                                            |
    //                                                 (firstInputIsLargerOutput)
    //

    inputs = new PowerInput[2];

    final var allBitsAboveAreEqualInputBinder = new InputBinder(this, "allBitsAboveAreEqual");
    allBitsAboveAreEqualInput = allBitsAboveAreEqualInputBinder;

    final var firstInputBinder = new InputBinder(this, "firstInput");
    inputs[0] = firstInputBinder;

    final var xorGate = new XorGate(id + "#xor");
    xorGate.getInput(0).connectTo(firstInputBinder);
    inputs[1] = xorGate.getInput(1);
    output = xorGate;

    final var notGate = new NotGate(id + "#not");
    notGate.getInput(0).connectTo(xorGate);

    final var andGate = new AndGate(id + "#and");
    andGate.getInput(0).connectTo(notGate);
    andGate.getInput(1).connectTo(allBitsAboveAreEqualInputBinder);
    allBitsSoFarAreEqualOutput = andGate;

    final var multiAndGate = new MultiInputAndGate(id + "#multiAnd", 3);
    multiAndGate.getInput(0).connectTo(allBitsAboveAreEqualInputBinder);
    multiAndGate.getInput(1).connectTo(firstInputBinder);
    multiAndGate.getInput(2).connectTo(xorGate);

    final var orGate = new OrGate(id + "#or");
    orGate.getInput(0).connectTo(multiAndGate);
    firstInputIsLargerInput = orGate.getInput(1);
    firstInputIsLargerOutput = orGate;
  }

  public PowerInput getInput(int index) {
    return inputs[index];
  }
}
