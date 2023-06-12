package pl.beben.computersimulation.device.memory;

import lombok.Getter;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.booleanfunction.Decoder;
import pl.beben.computersimulation.device.booleanfunction.gate.AndGate;
import pl.beben.computersimulation.device.misc.InputBinder;
import pl.beben.computersimulation.device.misc.InputOutputBus;
import pl.beben.computersimulation.device.powersupply.VccPowerSupply;

@Getter
public class RandomAccessMemory extends AbstractDevice {

  private final Register memoryAddressRegister;
  private final PowerInput busSetterInput;
  private final PowerInput busEnableInput;
  private final InputOutputBus bus;

  final AndGate[][] activators;
  final Register[][] registers;

  public RandomAccessMemory(String id) {
    super(id);

    //
    //                      .--------------------------.
    //                      | .----------------------. |
    // (regInput1)---|`|----` | .------------------. | |
    // (regInput2)---|R|------` | .--------------. | | |
    // (regInput3)---|E|--------` |              | | | |
    // (regInput4)---|G|----------`        |````````````````|
    //               |I|                   |  Decoder 4x16  |
    // (regInput5)---|S|----------.        |................|
    // (regInput6)---|T|---------. |  |````|++++++++++++++++
    // (regInput7)---|E|-------. | |  |    |++++++++++++++++
    // (regInput8)---|R|-----. | | `--|  D |++++++++++++++++
    // (regSetter)---|.|     | | `----|  e |++++++++++++++++
    //                |      | `------|  c |++++++++++++++++
    //                |      `--------|  o |++++++++++++++++
    //    (enableInputPowerSupply)    |  d |++++++++++++++++
    //                                |  e |++++++++++++++++
    //    This register is always     |  r |++++++++++++++++
    //    enabled by being conne-     |    |++++++++++++++++
    //    -cted to a power supply     |  4 |++++++++++++++++
    //    that is always on           |  x |++++++++++++++++
    //                                |  1 |++++++++++++++++
    //                                |  6 |++++++++++++++++
    //                                |    |++++++++++++++++
    //                                |....|++++++++++++++++
    //                                           || | |
    // (bus)=====================================:` | |
    // (busSetter)----------------------------------` |
    // (busEnabler)-----------------------------------`
    //
    // Each `+` looks like this
    //
    //                (verticalDecoder)
    //                        |
    //                        |
    // (horizontalDecoder)----|----*-------------...  `setter`
    //                        |    |                     │
    //                        |    `--|```|← `activator` ↓
    //                        |       |AND|----------*---|```|   |``````````|
    //                        *-------|...|          |   |AND|--[setter]    |
    //                        |                 .----|---|...|   |          |
    //                        |                 |    |           | Register |
    //                        .                 |    `---|```|   |          |
    //                        .                 |        |AND|--[enabler]   |
    //                        .                 |    .---|...|   |..[i/o]...|
    //                                          |    |    ↑           ||
    // (busSetter)------------------------------`    |    │           ||
    // (busEnabler)----------------------------------` `enabler`      ||
    // (bus)==========================================================:`
    //
    // This RAM device has
    //  - 1 register (Memory address register) used to select data holding register
    //  - 256 data holding registers, 1 byte each
    //

    // setup large picture (the first diagram)

    memoryAddressRegister = new Register(id + "#mar");

    final var alwaysOnPowerOutput = new VccPowerSupply(id + "#alwaysOnPowerInput");
    alwaysOnPowerOutput.setValue(true);
    memoryAddressRegister.getEnableInput().connectTo(alwaysOnPowerOutput);

    busSetterInput = new InputBinder(this, id + "#busSetter");
    busEnableInput = new InputBinder(this, id + "#busEnabler");
    bus = new InputOutputBus(id + "#bus", 8);

    final var verticalDecoder = new Decoder(id + "#verticalDecoder", 4);
    verticalDecoder.getInput(0).connectTo(memoryAddressRegister.getOutput(0));
    verticalDecoder.getInput(1).connectTo(memoryAddressRegister.getOutput(1));
    verticalDecoder.getInput(2).connectTo(memoryAddressRegister.getOutput(2));
    verticalDecoder.getInput(3).connectTo(memoryAddressRegister.getOutput(3));

    final var horizontalDecoder = new Decoder(id + "#horizontalDecoder", 4);
    horizontalDecoder.getInput(0).connectTo(memoryAddressRegister.getOutput(4));
    horizontalDecoder.getInput(1).connectTo(memoryAddressRegister.getOutput(5));
    horizontalDecoder.getInput(2).connectTo(memoryAddressRegister.getOutput(6));
    horizontalDecoder.getInput(3).connectTo(memoryAddressRegister.getOutput(7));

    // setup details (the part starting with "Each `+` looks like this")
    registers = new Register[16][16];
    activators = new AndGate[16][16];

    for (int verticalIndex = 0; verticalIndex < verticalDecoder.getOutputsCount(); verticalIndex++) {
      for (int horizontalIndex = 0; horizontalIndex < horizontalDecoder.getOutputsCount(); horizontalIndex++) {
        final var idPostfix = "[" + verticalIndex + "][" + horizontalIndex + "]";

        final var activator = new AndGate(id + "#activator" + idPostfix);
        activator.getInput(0).connectTo(verticalDecoder.getOutput(verticalIndex));
        activator.getInput(1).connectTo(horizontalDecoder.getOutput(horizontalIndex));

        final var setter = new AndGate(id + "#setter" + idPostfix);
        setter.getInput(0).connectTo(activator);
        setter.getInput(1).connectTo((InputBinder) this.busSetterInput);

        final var enabler = new AndGate(id + "#enabler" + idPostfix);
        enabler.getInput(0).connectTo(activator);
        enabler.getInput(1).connectTo((InputBinder) this.busEnableInput);

        final var register = new Register(id + "#register" + idPostfix);
        register.getEnableInput().connectTo(enabler);
        register.getSetterInput().connectTo(setter);
        bus.connectInputToBus(register.getValueInputs());
        bus.connectOutputToBus(register.getOutputs());

        activators[verticalIndex][horizontalIndex] = activator;
        registers[verticalIndex][horizontalIndex] = register;
      }
    }
  }


}
