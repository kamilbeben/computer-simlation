package pl.beben.computersimulation.device.arithmeticlogicunit;

import pl.beben.computersimulation.device.abstraction.BitwiseOperation;
import pl.beben.computersimulation.device.booleanfunction.LogicGate;

abstract class LogicGateBasedBitwiseOperation extends BitwiseOperation {

  protected LogicGateBasedBitwiseOperation(String id, int inputsCount) {
    super(id, inputsCount);

    //
    // Diagram representing binary (2 input) bitwise operation based on a LogicGate having 2 inputs
    //
    // (inputs[0][0])----------------------------|``````|
    //                                           | Gate |---(output[0])
    //                                  .--------|......|
    //                                  |
    // (inputs[0][1])-------------------|--------|``````|
    //                                  |        | Gate |---(output[1])
    //                                  |.-------|......|
    //                                  ||
    // (inputs[0][2])-------------------||-------|``````|
    //                                  ||       | Gate |---(output[2])
    //                                  ||.------|......|
    //                                  |||
    // (inputs[0][3])-------------------|||------|``````|
    //                                  |||      | Gate |---(output[3])
    //                                  |||.-----|......|
    //                                  ||||
    // (inputs[0][4])-------------------||||-----|``````|
    //                                  ||||     | Gate |---(output[4])
    //                                  ||||.----|......|
    //                                  |||||
    // (inputs[0][5])-------------------|||||----|``````|
    //                                  |||||    | Gate |---(output[5])
    //                                  |||||.---|......|
    //                                  ||||||
    // (inputs[0][6])-------------------||||||---|``````|
    //                                  ||||||   | Gate |---(output[6])
    //                                  ||||||.--|......|
    //                                  |||||||
    // (inputs[0][7])-------------------|||||||--|``````|
    //                                  |||||||  | Gate |---(output[7])
    //                                  |||||||.-|......|
    //                                  ||||||||
    // (inputs[1][0])-------------------`|||||||
    // (inputs[1][1])--------------------`||||||
    // (inputs[1][2])---------------------`|||||
    // (inputs[1][3])----------------------`||||
    // (inputs[1][4])-----------------------`|||
    // (inputs[1][5])------------------------`||
    // (inputs[1][6])-------------------------`|
    // (inputs[1][7])--------------------------`
    //

    for (int bitIndex = 0; bitIndex < 8; bitIndex++) {

      final var logicGate = constructLogicGate(this + "#gate[" + bitIndex + "]");
      output[bitIndex] = logicGate;

      if (logicGate.getInputsCount() != inputs.length)
        throw new IllegalArgumentException(
          "Cannot construct LogicGateBasedBitwiseOperation. LogicGate has " + logicGate.getInputsCount() + " inputs, " +
          "but LogicGateBasedBitwiseOperation supports only " + inputs.length + " inputs."
        );

      for (int inputByteIndex = 0; inputByteIndex < inputs.length; inputByteIndex++) {
        inputs[inputByteIndex][bitIndex] = logicGate.getInput(inputByteIndex);
      }
    }
  }

  protected abstract LogicGate constructLogicGate(String id);

}
