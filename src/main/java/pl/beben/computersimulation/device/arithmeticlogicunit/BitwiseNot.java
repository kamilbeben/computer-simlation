package pl.beben.computersimulation.device.arithmeticlogicunit;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;
import pl.beben.computersimulation.device.booleanfunction.gate.NotGate;

public class BitwiseNot extends LogicGateBasedBitwiseOperation {

  public BitwiseNot(String id) {
    super(id, 1);
  }

  @Override
  protected LogicGate constructLogicGate(String id) {
    return new NotGate(id);
  }

}
