package pl.beben.computersimulation.device.abstraction;

import java.util.List;

public interface PowerOutput extends Device {

  boolean getValue();

  List<PowerInput> getConnectedInputs();

}
