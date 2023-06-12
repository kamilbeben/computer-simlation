package pl.beben.computersimulation.device.abstraction;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDevice implements Device {

  // for debugging purposes
  public static final Map<String, AbstractDevice> ID_TO_DEVICE = new HashMap<>();

  final String id;

  protected AbstractDevice(String id) {
    this.id = id;
    ID_TO_DEVICE.put(id, this);
  }

  @Override
  public String toString() {
    return id;
  }

}
