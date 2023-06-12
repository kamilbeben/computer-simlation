package pl.beben.computersimulation.device.booleanfunction;

import lombok.experimental.Delegate;
import pl.beben.computersimulation.device.abstraction.PowerOutput;

public abstract class LogicGate extends BooleanFunction implements PowerOutput {

  public LogicGate(String id, int inputsCount, String truthTable) {
    super(id, inputsCount, 1, truthTable);
  }

  @Delegate(types = PowerOutput.class, excludes = BooleanFunction.class)
  private PowerOutput powerOutputDelegate() {
    return outputs[0];
  }
}
