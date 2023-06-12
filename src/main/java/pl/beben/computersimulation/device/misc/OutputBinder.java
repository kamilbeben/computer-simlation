package pl.beben.computersimulation.device.misc;

import pl.beben.computersimulation.device.abstraction.AbstractDevice;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Allows binding multiple outputs together, if any of them is true - then its output will be true
 *
 */
public class OutputBinder extends AbstractDevice implements PowerOutput {

  final List<PowerOutput> outputs;

  public OutputBinder(String id) {
    super(id);
    outputs = new ArrayList<>();
  }

  public void bindOutput(PowerOutput output) {
    outputs.add(output);
  }

  @Override
  public boolean getValue() {
    return outputs.stream().anyMatch(PowerOutput::getValue);
  }

  @Override
  public List<PowerInput> getConnectedInputs() {
    return outputs.stream()
      .map(PowerOutput::getConnectedInputs).flatMap(List::stream)
      .distinct()
      .collect(Collectors.toList());
  }

}
