package edu.ucsf.mousedatabase;
import java.util.logging.*;

public class Log {

  private static Logger _logger;
  public static final String loggerName = "mousespaceLogger";


  public static void Initialize()
  {
    _logger = Logger.getLogger(loggerName);
    _logger.setLevel(Level.ALL);
    _logger.info("Logging initialized.");
  }


  public static void Info(Object o)
  {
    if (o == null) {
      return;
    }
    _logger.info(o.toString());
  }
  
  public static void Info(String string, Object object) {
    _logger.log(Level.INFO, string, object);
  }

  public static void Error(Object o)
  {
    Error(o, null);
  }

  public static void Error(Object o, Throwable exception)
  {
    if (o == null)
    {
      _logger.log(Level.SEVERE, "<null log message>", exception);
    }
    else
    {
      _logger.log(Level.SEVERE, o.toString(), exception);
    }
  }
}
