package pl.beben.computersimulation.device.booleanfunction;

import pl.beben.computersimulation.device.booleanfunction.gate.MultiInputAndGate;
import pl.beben.computersimulation.device.booleanfunction.gate.NotGate;
import pl.beben.computersimulation.device.transport.InputBinder;
import pl.beben.computersimulation.utils.TruthTableSanitizer;
import static pl.beben.computersimulation.utils.DecoderUtils.generateTruthTable;
import static pl.beben.computersimulation.utils.DecoderUtils.inputsCountToOutputsCount;

public class Decoder extends BooleanFunction {

  public Decoder(String id, int inputsCount) {
    this(id, inputsCount, inputsCountToOutputsCount(inputsCount), generateTruthTable(inputsCount));
  }

  private Decoder(String id, int inputsCount, int outputsCount, String truthTable) {
    super(id, inputsCount, outputsCount, truthTable);

    //
    // The diagram is explaining how a 2x4 (2 input 4 output) decoder would look like
    //
    //                        `inputBinders[0]`
    //                                │
    //                                │  ┌─ `notGates[0]`
    //                                │  │
    //                                │  ↓
    //                                ↓  |```|
    // (input1)-----------------------*--|NOT|-.
    //                 `notGates[1]`  |  |...| |
    //                       │        |        |
    // `inputBinders[1]` ─┐  ↓        |        |
    //                    ↓  |```|    |        |
    // (input2)-----------*--|NOT|-.  |        *--|```| ←───────────── `andGates[0]`
    //                    |  |...| |  |        |  |AND|---(outputs[0])
    // ...                |        *--+--------|--|...|
    //                    |        |  |        |
    //                    |        |  |        `--|```| ←───────────── `andGates[1]`
    //                    |        |  |           |AND|---(outputs[1])
    //                    *--------+--+-----------|...|
    //                    |        |  |
    //                    |        |  *-----------|```| ←───────────── `andGates[2]`
    //                    |        |  |           |AND|---(outputs[2])
    //                    |        `--+-----------|...|
    //                    |           |
    //                    |           `-----------|```| ←───────────── `andGates[3]`
    //                    |                       |AND|---(outputs[3])
    //                    `-----------------------|...|
    //
    //
    // And so on.
    // Each input has an `inputBinder[n]` followed by a `notGate[n]`.
    // Each AND gate needs to have the same amount of inputs as the whole function (it's a MultiInputAndGate).
    // Each AND gate is a single output.
    //
    //
    // You can tell how to connect the inputs of a specific AND gate by looking at the truth table.
    // For example, let's consider the following truth table:
    //
    //  0 0  1 0 0 0
    //  0 1  0 1 0 0
    //  1 0  0 0 1 0
    //  1 1  0 0 0 1
    //
    // Going from top to bottom:
    //  Each line represents a single AND gate.
    //  The lines in the truth table are in the same order as the AND gates.
    //
    // Going from left to right:
    //  The inputs are separated from the outputs by a double space.
    //  The inputs are separated from themselves by a space.
    //  The inputs in the truth table are in the same order as the inputs in the AND gate.
    //  First AND gate's input is always connected to the first decoders input, second to the second and so on.
    //  0 means that the AND gate's input needs to be connected to the corresponding NOT gate.
    //  1 means that the AND gate's input needs to be connected to the corresponding input (in the code it's inputBride).
    //

    // initialize gates
    final var inputBinders = new InputBinder[inputsCount];
    final var notGates = new NotGate[inputsCount];
    final var andGates = new MultiInputAndGate[outputsCount];

    // construct inputBinders, notGates and link notGates to the inputBinders
    for (int i = 0; i < inputsCount; i++) {
      final var inputBinder = new InputBinder(this, id + "#inputBinder[" + i + "]");
      inputBinders[i] = inputBinder;

      final var notGate = new NotGate(id + "#not[" + i + "]");
      notGates[i] = notGate;
      notGate.getInput(0).connectTo(inputBinder);
    }

    // construct andGates and link their inputs to required notGates and inputBinders
    final var truthTableLines = TruthTableSanitizer.sanitize(truthTable).split("\n");
    for (int outputIndex = 0; outputIndex < outputsCount; outputIndex++) {
      final var andGate = new MultiInputAndGate(id + "#multi-and[" + outputIndex + "]", inputsCount);
      andGates[outputIndex] = andGate;

      for (int inputIndex = 0; inputIndex < inputsCount; inputIndex++) {
        final var correspondingTruthTableCharacter = truthTableLines[outputIndex].charAt(inputIndex);
        andGate.getInput(inputIndex).connectTo(
          correspondingTruthTableCharacter == '0'
            ? notGates[inputIndex]
            : inputBinders[inputIndex]
        );
      }
    }

    // setup decoder inputs and outputs
    for (int i = 0; i < inputsCount; i++)
      inputs[i] = inputBinders[i];

    for (int i = 0; i < outputsCount; i++)
      outputs[i] = andGates[i];
  }

}
