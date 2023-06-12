package pl.beben.computersimulation.device.abstraction;

import lombok.RequiredArgsConstructor;
import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(access = PROTECTED)
public abstract class AbstractDevice implements Device {

  final String id;

  @Override
  public String toString() {
    return id;
  }

}
