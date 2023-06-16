package pl.beben.computersimulation.device.arithmeticlogicunit;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;
import pl.beben.computersimulation.device.booleanfunction.gate.OrGate;
import pl.beben.computersimulation.device.booleanfunction.gate.XorGate;

public class BitwiseXor extends LogicGateBasedBitwiseOperation {

  public BitwiseXor(String id) {
    super(id, 2);
  }

  @Override
  protected LogicGate constructLogicGate(String id) {
    return new XorGate(id);
  }

}
