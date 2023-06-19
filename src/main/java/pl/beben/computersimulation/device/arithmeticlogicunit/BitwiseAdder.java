package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Getter;
import pl.beben.computersimulation.device.abstraction.BitwiseOperation;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;

@Getter
public class BitwiseAdder extends BitwiseOperation {

  final PowerInput carryInput;
  final PowerOutput carryOutput;

  public BitwiseAdder(String id) {
    super(id, 2);

    //
    //                                                 .---------(carryOutput)
    //                                                 |
    // (inputs[0][7])----------------------------|```````````|
    //                                           | FullAdder |---(output[7])
    //                                  .--------|...........|
    //                                  |              |
    // (inputs[0][6])-------------------|--------|```````````|
    //                                  |        | FullAdder |---(output[6])
    //                                  |.-------|...........|
    //                                  ||             |
    // (inputs[0][5])-------------------||-------|```````````|
    //                                  ||       | FullAdder |---(output[5])
    //                                  ||.------|...........|
    //                                  |||            |
    // (inputs[0][4])-------------------|||------|```````````|
    //                                  |||      | FullAdder |---(output[4])
    //                                  |||.-----|...........|
    //                                  ||||           |
    // (inputs[0][3])-------------------||||-----|```````````|
    //                                  ||||     | FullAdder |---(output[3])
    //                                  ||||.----|...........|
    //                                  |||||          |
    // (inputs[0][2])-------------------|||||----|```````````|
    //                                  |||||    | FullAdder |---(output[2])
    //                                  |||||.---|...........|
    //                                  ||||||         |
    // (inputs[0][1])-------------------||||||---|```````````|
    //                                  ||||||   | FullAdder |---(output[1])
    //                                  ||||||.--|...........|
    //                                  |||||||        |
    // (inputs[0][0])-------------------|||||||--|```````````|
    //                                  |||||||  | FullAdder |---(output[0])
    //                                  |||||||.-|...........|
    //                                  ||||||||       |
    // (inputs[1][7])-------------------`|||||||       |
    // (inputs[1][6])--------------------`||||||       |
    // (inputs[1][5])---------------------`|||||       |
    // (inputs[1][4])----------------------`||||       |
    // (inputs[1][3])-----------------------`|||       |
    // (inputs[1][2])------------------------`||       |
    // (inputs[1][1])-------------------------`|       |
    // (inputs[1][0])--------------------------`       |
    //                                                 |
    // (carryInput)------------------------------------`
    //

    final var fullAdders = new FullAdder[8];
    for (int bitIndex = 0; bitIndex < 8; bitIndex++) {

      final var fullAdder = new FullAdder(id + "#fullAdder[" + bitIndex + "]");
      fullAdders[bitIndex] = fullAdder;

      inputs[0][bitIndex] = fullAdder.getInput(0);
      inputs[1][bitIndex] = fullAdder.getInput(1);
      output[bitIndex] = fullAdder.getOutput();

      if (bitIndex == 0)
        continue;

      fullAdder.getCarryInput().connectTo(fullAdders[bitIndex - 1].getCarryOutput());
    }

    carryInput = fullAdders[0].getCarryInput();
    carryOutput = fullAdders[7].getCarryOutput();
  }

}
