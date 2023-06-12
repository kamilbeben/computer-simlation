package pl.beben.computersimulation.device.booleanfunction.gate;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;
import pl.beben.computersimulation.utils.MultiInputAndGateTruthTableGenerator;

public class MultiInputAndGate extends LogicGate {

  public MultiInputAndGate(String id, int inputsCount) {
    super(id, inputsCount, MultiInputAndGateTruthTableGenerator.generate(inputsCount));

    assert inputsCount > 1;

    //
    // (input1)---|```|
    //            |AND|--.
    // (input2)---|...|   `--|```|
    //                       |AND|--.
    // (input3)--------------|...|   `--|```|
    //                                  |AND|---(output or more andGates)
    // (input4)-------------------------|...|
    // ... and so on
    //

    final var andGates = new AndGate[inputsCount - 1];

    for (byte i = 0; i < andGates.length; i++) {
      final var andGate = new AndGate(id + "andGate[" + i + "]");
      andGates[i] = andGate;

      if (i == 0) {
        inputs[0] = andGate.getInput(0);
        inputs[1] = andGate.getInput(1);
        continue;
      }

      final var previousAndGate = andGates[i - 1];
      andGate.getInput(0).connectTo(previousAndGate);
      inputs[i + 1] = andGate.getInput(1);
    }

    outputs[0] = andGates[andGates.length - 1];
  }

}
