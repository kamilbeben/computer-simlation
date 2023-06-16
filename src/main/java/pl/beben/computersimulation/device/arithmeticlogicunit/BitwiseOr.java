package pl.beben.computersimulation.device.arithmeticlogicunit;

import pl.beben.computersimulation.device.booleanfunction.LogicGate;
import pl.beben.computersimulation.device.booleanfunction.gate.OrGate;

public class BitwiseOr extends LogicGateBasedBitwiseOperation {

  public BitwiseOr(String id) {
    super(id, 2);
  }

  @Override
  protected LogicGate constructLogicGate(String id) {
    return new OrGate(id);
  }

}
