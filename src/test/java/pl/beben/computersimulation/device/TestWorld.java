package pl.beben.computersimulation.device;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import pl.beben.computersimulation.device.abstraction.Device;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.SECONDS;

@Log4j2
public class TestWorld extends World {

  record StepStatistics (int stepDurationInMs, int deviceStepCount) {}
  Deque<StepStatistics> stepStatistics = new ConcurrentLinkedDeque<>();
  int deviceStepCounter;

  RuntimeException exceptionThrownByStep;
  CountDownLatch stepLatch;

  public void runSynchronously() {
    start();
    await();
    stop();
  }

  @SneakyThrows
  public void await() {
    if (stepLatch != null)
      throw new IllegalStateException("The world already has a stepLatch assigned");

    // awaiting 6 steps because
    //  - 1st might already be in progress when input was set
    //  - 2nd might not yet have every input set, complex circuits might require a few steps to 'warm up'
    //  - 3rd time's a charm it seems (for most tests)
    //  - it takes two times that for RandomAccessMemoryTest to pass, which means that
    //    eventually everything is refreshing as it's supposed to, but it takes a few more
    //    steps for more complicated circuits
    //  - 8 steps for BitwiseComparatorTest

    // It might seem like a dirty hack but in all honesty - the electricity in the computer travels almost instantly, so it's not noticeable, but it works
    // like just like that - not everything is set immediately, some outputs will be flipping until they eventually settle.
    // At least that's my understanding of these things, perhaps I am wrong and the way World#step works could be improved.

    stepLatch = new CountDownLatch(8);
    exceptionThrownByStep = null;

    try {
      stepLatch.await(10, SECONDS);
    } finally {
      stepLatch = null;
    }

    if (exceptionThrownByStep != null)
      throw exceptionThrownByStep;

  }

  @Override
  protected void run() {
    assert isRunning;

    executor.submit(() -> {
      try {
        while (isRunning) {
          step();
        }
      } catch (RuntimeException e) {
        exceptionThrownByStep = e;

        while (stepLatch != null && stepLatch.getCount() > 0) {
          stepLatch.countDown();
        }
      }
    });
  }

  @Override
  public void start() {
    stepStatistics.clear();
    super.start();
  }

  @Override
  public void stop() {
    super.stop();

    // this.stepStatistics can be cleared by another thread resulting in ConcurrentModificationException
    final var stepStatistics = new ArrayList<>(this.stepStatistics);

    log.info(
      "World has stopped. Took {}ms, stepsCount: {}, average stepDuration: {}ms, average processedDevicesCount: {}",
      stepStatistics.stream()
        .mapToInt(StepStatistics::stepDurationInMs)
        .sum(),
      stepStatistics.size(),
      (int) stepStatistics.stream()
        .mapToInt(StepStatistics::stepDurationInMs)
        .summaryStatistics()
        .getAverage(),
      (int) stepStatistics.stream()
        .mapToInt(StepStatistics::deviceStepCount)
        .summaryStatistics()
        .getAverage()
    );

    if (stepLatch != null && stepLatch.getCount() > 0)
      throw new IllegalStateException("The world has stopped before the stepLatch went off");

    stepLatch = null;
    exceptionThrownByStep = null;
  }

  @Override
  protected void step() {
    deviceStepCounter = 0;
    final var timestampBeforeStep = System.currentTimeMillis();

    super.step();

    final var timestampAfterStep = System.currentTimeMillis();
    stepStatistics.add(new StepStatistics((int) (timestampAfterStep - timestampBeforeStep), deviceStepCounter));

    if (stepLatch != null) {
      stepLatch.countDown();
    }
  }

  @Override
  protected void deviceStep(LinkedList<DeviceConnection> deviceConnectionQueue, Device device) {
    super.deviceStep(deviceConnectionQueue, device);
    deviceStepCounter++;
  }

}
