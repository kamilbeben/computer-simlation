package pl.beben.computersimulation.device;

import pl.beben.computersimulation.device.abstraction.composite.PowerInputComposite;

public class OutputSpy extends PowerInputComposite {

  public OutputSpy() {
    this("OutputSpy");
  }

  public OutputSpy(String id) {
    super(null, id + "#" + ++sequence);
  }

  static int sequence = 0;

}
