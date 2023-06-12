package pl.beben.computersimulation.device.powersupply;

import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.SettablePowerOutput;
import pl.beben.computersimulation.device.abstraction.composite.PowerOutputComposite;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE)
public class VccPowerSupply implements SettablePowerOutput {

  final String id;

  @Delegate(types = SettablePowerOutput.class, excludes = AbstractDevice.class)
  final PowerOutputComposite output;

  public VccPowerSupply(String id) {
    this.id = id;
    this.output = new PowerOutputComposite(this, id + "#output");
  }

  @Override
  public String toString() {
    return id;
  }

}
