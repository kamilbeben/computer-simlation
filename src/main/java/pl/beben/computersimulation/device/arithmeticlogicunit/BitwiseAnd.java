package pl.beben.computersimulation.device.arithmeticlogicunit;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;
import pl.beben.computersimulation.device.booleanfunction.gate.AndGate;

public class BitwiseAnd extends LogicGateBasedBitwiseOperation {

  public BitwiseAnd(String id) {
    super(id, 2);
  }

  @Override
  protected LogicGate constructLogicGate(String id) {
    return new AndGate(id);
  }

}
