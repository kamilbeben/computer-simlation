package pl.beben.computersimulation.device.memory;

import lombok.experimental.Delegate;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.booleanfunction.gate.NandGate;
import pl.beben.computersimulation.device.misc.InputBinder;

public class Bit extends AbstractDevice implements PowerOutput {

  final NandGate nandGate1;
  final NandGate nandGate2;
  @Delegate(types = PowerOutput.class, excludes = AbstractDevice.class)
  final NandGate nandGate3;
  final NandGate nandGate4;
  final InputBinder setterBridge;

  public Bit(String id) {
    super(id);

    //
    //                 `inputBinder2131`
    //                      │
    //         `nandGate1`  │                   `nandGate3`
    //              │       │                        │
    //              ↓       │                        ↓
    // (input)------|````|  ↓         .--------------|````|
    //              |NAND|--*--------`               |NAND|--*---(output)
    //          .---|....|  |                     .--|....|  |
    //          |           |                     |          |
    //          |           |                     |          |
    //          |           |                     `----.     |
    //          |   .-------`                     .-----\----`
    //          |   |                             |      `---.
    //          |   |                             |          |
    //          |   `--|````|                     `--|````|  |
    //          |      |NAND|---.                    |NAND|--`
    // (input)--*------|....|    `-------------------|....|
    //          ↑      ↑                             ↑
    //          │      │                             │
    //          │  `nandGate2`                   `nandGate4`
    //          │
    //     `setterBridge`
    //

    nandGate1 = new NandGate(id + "#nand1");
    nandGate2 = new NandGate(id + "#nand2");
    nandGate3 = new NandGate(id + "#nand3");
    nandGate4 = new NandGate(id + "#nand4");
    setterBridge = new InputBinder(this, "setterBridge");

    final var inputBinder2131 = new InputBinder(this, id + "#inputBinder2131");

    // gate 1
    // nandGate1#input1 is going to be connected to the valueInput
    nandGate1.getInput(1).connectTo(setterBridge);

    // gate 2
    nandGate2.getInput(0).connectTo(inputBinder2131);
    nandGate2.getInput(1).connectTo(setterBridge);

    // gate 3
    nandGate3.getInput(0).connectTo(inputBinder2131);
    nandGate3.getInput(1).connectTo(nandGate4);

    // gate 4
    nandGate4.getInput(0).connectTo(nandGate3);
    nandGate4.getInput(1).connectTo(nandGate2);

    // inputBinder2131
    inputBinder2131.connectTo(nandGate1);

    // assert that inputs are connected
    // both nandGate1 inputs and second nandGate2 input are connected to external device, hence missing asserts
    assert nandGate2.getInput(0).isConnected();
    assert nandGate3.getInput(0).isConnected();
    assert nandGate3.getInput(1).isConnected();
    assert nandGate4.getInput(0).isConnected();
    assert nandGate4.getInput(1).isConnected();
  }

  public PowerInput getValueInput() {
    return nandGate1.getInput(0);
  }

  public PowerInput getSetterInput() {
    return setterBridge;
  }

}
