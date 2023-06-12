package pl.beben.computersimulation.device.abstraction.composite;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.Device;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.SettablePowerOutput;
import java.util.ArrayList;
import java.util.List;
import static lombok.AccessLevel.PRIVATE;

@Log4j2
@Getter
@FieldDefaults(level = PRIVATE)
public class PowerOutputComposite extends AbstractDevice implements SettablePowerOutput {

  final List<PowerInput> connectedInputs = new ArrayList<>();
  boolean value;

  public PowerOutputComposite(Device parentDevice, String id) {
    super(parentDevice + "#" + id);
  }

  @Override
  public void setValue(boolean value) {
    this.value = value;
  }

  @Override
  public boolean getValue() {
    return value;
  }

}
