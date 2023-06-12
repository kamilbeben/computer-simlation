package pl.beben.computersimulation.device;

import lombok.SneakyThrows;
import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.SECONDS;

public class TestWorld extends World {

  private CountDownLatch stepLatch;

  public void runSynchronously() {
    start();
    await();
  }

  @SneakyThrows
  public void await() {
    if (stepLatch != null)
      throw new IllegalStateException("The world already has a stepLatch assigned");

    // awaiting 3 steps because
    //  - 1st might already be in progress when input was set
    //  - 2nd might not yet have every input set, complex circuits might require a few steps to 'warm up'
    //  - 3rd time's a charm it seems
    stepLatch = new CountDownLatch(3);

    try {
      stepLatch.await(10, SECONDS);
    } finally {
      stepLatch = null;
    }
  }

  @Override
  public void stop() {
    super.stop();

    if (stepLatch != null && stepLatch.getCount() > 0)
      throw new IllegalStateException("The world has stopped before the stepLatch went off");

    else
      stepLatch = null;
  }

  @Override
  protected void step() {
    super.step();

    if (stepLatch != null) {
      stepLatch.countDown();
    }
  }

}
