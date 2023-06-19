package pl.beben.computersimulation.device.arithmeticlogicunit;

import lombok.Getter;
import pl.beben.computersimulation.device.abstraction.BitwiseOperation;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;

@Getter
public class BitwiseComparator extends BitwiseOperation {

  final PowerInput allBitsAboveAreEqualInput;
  final PowerInput firstInputIsLargerInput;
  final PowerOutput allBitsSoFarAreEqualOutput;
  final PowerOutput firstInputIsLargerOutput;

  public BitwiseComparator(String id) {
    super(id, 2);

    //
    // (firstInputIsLargerInput)------------------------------.
    // (allBitsAboveAreEqualInput)------------------.         |
    //                                              |         |
    // (inputs[0][7])----------------------------|```````````````|
    //                                           | BitComparator |---(output[7])
    //                                  .--------|...............|
    //                                  |           |         |
    // (inputs[0][6])-------------------|--------|```````````````|
    //                                  |        | BitComparator |---(output[6])
    //                                  |.-------|...............|
    //                                  ||          |         |
    // (inputs[0][5])-------------------||-------|```````````````|
    //                                  ||       | BitComparator |---(output[5])
    //                                  ||.------|...............|
    //                                  |||         |         |
    // (inputs[0][4])-------------------|||------|```````````````|
    //                                  |||      | BitComparator |---(output[4])
    //                                  |||.-----|...............|
    //                                  ||||        |         |
    // (inputs[0][3])-------------------||||-----|```````````````|
    //                                  ||||     | BitComparator |---(output[3])
    //                                  ||||.----|...............|
    //                                  |||||       |         |
    // (inputs[0][2])-------------------|||||----|```````````````|
    //                                  |||||    | BitComparator |---(output[2])
    //                                  |||||.---|...............|
    //                                  ||||||         |      |
    // (inputs[0][1])-------------------||||||---|```````````````|
    //                                  ||||||   | BitComparator |---(output[1])
    //                                  ||||||.--|...............|
    //                                  |||||||     |         |
    // (inputs[0][0])-------------------|||||||--|```````````````|
    //                                  |||||||  | BitComparator |---(output[0])
    //                                  |||||||.-|...............|
    //                                  ||||||||    |         |
    // (inputs[1][7])-------------------`|||||||    |         `------(firstInputIsLargerOutput)
    // (inputs[1][6])--------------------`||||||    |
    // (inputs[1][5])---------------------`|||||    `----------------(allBitsAboveAreEqualOutput)
    // (inputs[1][4])----------------------`||||
    // (inputs[1][3])-----------------------`|||
    // (inputs[1][2])------------------------`||
    // (inputs[1][1])-------------------------`|
    // (inputs[1][0])--------------------------`
    //

    final var bitComparators = new BitComparator[8];
    for (int bitIndex = 7; bitIndex >= 0; bitIndex--) {
      final var bitComparator = new BitComparator(id + "#bitComparator[" + bitIndex + "]");
      bitComparators[bitIndex] = bitComparator;

      inputs[0][bitIndex] = bitComparator.getInput(0);
      inputs[1][bitIndex] = bitComparator.getInput(1);
      output[bitIndex] = bitComparator.getOutput();

      if (bitIndex == 7)
        continue;

      final var nextBitComparator = bitComparators[bitIndex + 1];
      bitComparator.getAllBitsAboveAreEqualInput().connectTo(nextBitComparator.getAllBitsSoFarAreEqualOutput());
      bitComparator.getFirstInputIsLargerInput().connectTo(nextBitComparator.getFirstInputIsLargerOutput());
    }

    allBitsAboveAreEqualInput = bitComparators[7].getAllBitsAboveAreEqualInput();
    firstInputIsLargerInput = bitComparators[7].getFirstInputIsLargerInput();
    allBitsSoFarAreEqualOutput = bitComparators[0].getAllBitsSoFarAreEqualOutput();
    firstInputIsLargerOutput = bitComparators[0].getFirstInputIsLargerOutput();
  }

}
