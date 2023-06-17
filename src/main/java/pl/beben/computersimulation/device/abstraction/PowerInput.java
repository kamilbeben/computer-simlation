package pl.beben.computersimulation.device.abstraction;

public interface PowerInput extends Device {

  Device getParentDevice();

  boolean getValue();

  void connectTo(PowerOutput output);
  boolean isConnected();

  void update();

}
