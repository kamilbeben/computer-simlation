package pl.beben.computersimulation.device;

import lombok.extern.log4j.Log4j2;
import pl.beben.computersimulation.device.abstraction.Device;
import pl.beben.computersimulation.device.abstraction.PowerInput;
import pl.beben.computersimulation.device.abstraction.PowerOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Log4j2
public class World implements AutoCloseable {

  protected ExecutorService executor = Executors.newSingleThreadExecutor();
  protected final List<Device> topLevelDevices = new ArrayList<>();

  protected boolean isRunning;

  public void registerAsTopLevelDevice(Device device) {
    topLevelDevices.add(device);
  }

  public void start() {
    isRunning = true;
    run();
  }

  public void stop() {
    isRunning = false;
  }

  /**
   * Alias for {@link #stop()}
   */
  @Override
  public void close() {
    stop();
  }

  protected void run() {
    assert isRunning;

    executor.submit(() -> {
      try {
        while (isRunning) {
          step();
        }
      } catch (Exception e) {
        log.error("Step error", e);
      }
    });
  }

  protected void step() {

    // Propagate that power changed in a breadth-first manner (as in breadth first search).
    // Let's consider the following diagram:
    //
    //              |`|   |`|
    //           .--|1|---|4|
    //           |  |.|   |.|
    //           |
    //           |  |`|   |`|   |`|
    // (input)---*--|2|---|5|---|6|
    //           |  |.|   |.|   |.|
    //           |
    //           |  |`|
    //           `--|3|
    //              |.|
    //
    // Since those gates are both `PowerInput` and `PowerOutput`, they can have children (hence `input instanceof PowerOutput` below)
    // The order in which these inputs will be processed by the queue goes as follows: 1, 2, 3, 4, 5, 6.

    log.debug("Started");

    final var exploredConnections = new HashSet<DeviceConnection>();
    final var deviceConnectionQueue = new LinkedList<>(
      topLevelDevices.stream()
        .map(device -> new DeviceConnection(null, device))
        .collect(Collectors.toList())
    );

    while (!deviceConnectionQueue.isEmpty()) {
      final var deviceConnection = deviceConnectionQueue.pollFirst();

      // otherwise, feedback loops (eg Bit#nandGate3 and Bit#nandGate4) would cause StackOverflowException
      final var connectionIsExploredForTheFirstTime = exploredConnections.add(deviceConnection);
      if (!connectionIsExploredForTheFirstTime)
        continue;

      final var device = deviceConnection.to();
      deviceStep(deviceConnectionQueue, device);

      if (device instanceof PowerOutput)
        log.debug("  Processing device [{}] = {}", device, ((PowerOutput) device).getValue());
      else if (device instanceof PowerInput && ((PowerInput) device).getParentDevice() instanceof PowerOutput)
        log.debug("  Processing device [{}] = {}", device, ((PowerOutput) ((PowerInput) device).getParentDevice()).getValue());
    }

    log.debug("Finished");
  }

  protected void deviceStep(LinkedList<DeviceConnection> deviceConnectionQueue, Device device) {
    if (device instanceof PowerOutput)
      powerOutputStep(deviceConnectionQueue, (PowerOutput) device);

    if (device instanceof PowerInput)
      powerInputStep(deviceConnectionQueue, (PowerInput) device);
  }

  protected void powerOutputStep(LinkedList<DeviceConnection> deviceConnectionQueue, PowerOutput device) {
    for (final var inputDevice : device.getConnectedInputs()) {
      final var connection = new DeviceConnection(device, inputDevice);
      deviceConnectionQueue.addLast(connection);
    }
  }

  protected void powerInputStep(LinkedList<DeviceConnection> deviceConnectionQueue, PowerInput device) {
    device.update();

    final var parentDevice = device.getParentDevice();
    if (parentDevice instanceof PowerOutput)
      powerOutputStep(deviceConnectionQueue, (PowerOutput) parentDevice);
  }

  record DeviceConnection(Device left, Device to) {

  }

}
