package pl.beben.computersimulation.device.booleanfunction.gate;

import pl.beben.computersimulation.device.abstraction.composite.PowerInputComposite;
import pl.beben.computersimulation.device.abstraction.composite.PowerOutputComposite;
import pl.beben.computersimulation.device.booleanfunction.LogicGate;

public class NandGate extends LogicGate {

  public NandGate(String id) {
    super(
      id, 2,
      """
      0 0  1
      0 1  1
      1 0  1
      1 1  0
      """
    );

    // Basic building unit, not going to simulate electricity and transistors but this
    // is how a NAND gate circuit could look like
    //
    //                    +Vcc ←── Power supply
    //                      |
    //                      |
    //                      |
    //                     /R/ ←── Resistor
    //                      |
    //                      +------------(output)
    //                      |
    //                     /                    "    / "
    // (input1)----/R/~--<|    ←───  This whole " -<|  " block is an NPN transistor. In this diagram, the (input1)
    //                     \                    "    \ " is connected to a resistor which is connected to the base
    //                      |                            of a transistor. Collector is on top, emitter is on bottom.
    //                     /
    // (input2)----/R/~--<|
    //                     \
    //                      |
    //                      |
    //                     GND ←── Ground
    //
    // Description:
    // `input1` and `input2` are connected through resistors to the bases of two transistors.
    // If there is a HIGH voltage on such input, the transistor is going to short (connect) its
    // collector and emitter pins.
    //
    // If both `input1` and `input2` have HIGH voltage, both transistors will be shorted, resulting
    // in a direct connection from +Vcc (Power supply) to the GND (Ground). Such connection will have
    // significantly smaller resistance than the `output`, which would result on LOW voltage on the `output`
    // and HIGH voltage going to the GND (HIGH is translated to `1` / `on` / `true`, LOW is translated to
    // `0` / `off` / `false`).
    //
    // In any other scenario (either `input1` or `input2` are LOW), one or both of the transistors would
    // be open - meaning that there no longer is a direct connection between the +Vcc (Power supply) and
    // the GND (Ground) - hence, the HIGH voltage would choose the path to the `output`.
    //
    // Link to the simulation I've created on falstad's electronic circuit simulator
    // Data is encoded in the `ctz` query parameter, so it should be working until their website goes
    // down, or they change their data model (accessed on 2023 Jun 10)
    // https://www.falstad.com/circuit/circuitjs.html?ctz=CQAgjCAMB0l3BWcMBMcUHYMGZIA4UA2ATmIxAUgoqoQFMBaMMAKACUQ8AWPEFLqt15gMhKOIEgxVGdAQsATpx59JQkLi7iwkFgHdlvTSGIojkLbrCEUJsxot2jhXrYAmdAGYBDAK4AbABcGfzo3cHFZSFYDU2deOJAeGRZAkAxuDTRDLJkImAwEQmxiSGJmYsgEYmxpaAzi7C4sAi4BYmLkKg8fAMDU9Myuaxzh6Xz6orMy9qauBGGoepqF6swOs2xhOBAevyD9HOxa0Z3dA3UubFtLvBSAc1G7nIQwWxTY+2SnlIBndJO1yoGS0QMiIB8-l+dBY-xwYjGgy0iLykOhiikGFsYPhuW0uiUhCxSRGuJRyHYmOx2RsoOyeUk41k8g4RNsiNpJKZSSo3Jg8iAA

    inputs[0] = new PowerInputComposite(this, "input1", this::update);
    inputs[1] = new PowerInputComposite(this, "input2", this::update);
    outputs[0] = new PowerOutputComposite(this, "output");
  }

  private void update() {
    ((PowerOutputComposite) outputs[0]).setValue(
      !inputs[0].getValue() || !inputs[1].getValue()
    );
  }

}
