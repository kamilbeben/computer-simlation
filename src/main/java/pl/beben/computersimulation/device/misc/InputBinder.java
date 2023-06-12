package pl.beben.computersimulation.device.misc;

import lombok.Getter;
import lombok.experimental.Delegate;
import lombok.extern.log4j.Log4j2;
import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.Device;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import pl.beben.computersimulation.device.abstraction.composite.PowerInputComposite;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows exposing multiple inputs as one - when the PowerInput parts state changes, all the connectedInputs will be notified
 */
@Log4j2
public class InputBinder extends AbstractDevice implements PowerInput, PowerOutput {

  @Getter
  final Device parentDevice;

  // required by PowerOutput
  @Getter
  final List<PowerInput> connectedInputs;

  // exposes `boolean getValue()` which is also required by PowerOutput
  @Delegate(types = PowerInput.class, excludes = Device.class)
  final PowerInput input;

  public InputBinder(Device parentDevice, String id) {
    super(id);

    this.input = new PowerInputComposite(this, "composite");
    this.parentDevice = parentDevice;
    this.connectedInputs = new ArrayList<>();
  }

}
