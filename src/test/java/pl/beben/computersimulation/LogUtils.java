package pl.beben.computersimulation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class LogUtils {

  private static Level DEFAULT_LOG_LEVEL = LogManager.getRootLogger().getLevel();

  public static void setLogLevel(Level level) {
    Configurator.setRootLevel(level);
  }

  public static void restoreDefaultLogLevel() {
    setLogLevel(DEFAULT_LOG_LEVEL);
  }
}
