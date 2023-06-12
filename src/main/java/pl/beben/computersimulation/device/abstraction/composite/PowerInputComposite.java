package pl.beben.computersimulation.device.abstraction.composite;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.Device;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import static lombok.AccessLevel.PRIVATE;

@Log4j2
@FieldDefaults(level = PRIVATE)
public class PowerInputComposite extends AbstractDevice implements PowerInput {

  @Getter
  final Device parentDevice;
  final Runnable update;

  @Getter
  PowerOutput output;

  public PowerInputComposite(Device parentDevice, String id) {
    this(parentDevice, id, null);
  }

  public PowerInputComposite(Device parentDevice, String id, Runnable update) {
    super(parentDevice + "#" + id);
    this.parentDevice = parentDevice;
    this.update = update;
  }

  @Override
  public boolean getValue() {
    if (output == null)
      throw new IllegalStateException("Invoked #getValue() on " + this + " but it is not connected to any output");

    return output.getValue();
  }

  @Override
  public void connectTo(PowerOutput output) {
    log.debug("Connecting {} to {}", this, output);
    this.output = output;
    this.output.getConnectedInputs().add(this);
  }

  @Override
  public boolean isConnected() {
    return output != null;
  }

  @Override
  public void update() {
    if (update != null)
      update.run();
  }

}
