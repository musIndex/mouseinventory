package edu.ucsf.mousedatabase.listeners;

import java.beans.Introspector;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Map;
import java.lang.System.getenv;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.MGIConnect;
import edu.ucsf.mousedatabase.dataimport.ImportHandler;
import edu.ucsf.mousedatabase.objects.MouseMail;

public class ContextListener implements ServletContextListener {

  @Override
  @SuppressWarnings("rawtypes")
  public void contextDestroyed(ServletContextEvent event) {
    Log.Info("Mouse Inventory Web App Removed.");

    try {
      Introspector.flushCaches();
      for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();) {
        Driver driver = (Driver) e.nextElement();
        if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
          Log.Info("De-registering " + driver.toString());
          DriverManager.deregisterDriver(driver);
        }
      }
      Log.Info("Removed JDBC driver from DriverManager.");
    } catch (Throwable e) {
      System.err.println("Failed to cleanup ClassLoader for webapp");
      e.printStackTrace();
    }

  }

  @Override
  public void contextInitialized(ServletContextEvent event) {
    Log.Initialize();
    event.getServletContext();

    Log.Info("Mouse Inventory Web App Starting.");

    ImportHandler.InitializeDefinitions();

    Map<String, String> env = System.getenv();

    Context initCtx;
    try {
      MGIConnect.Initialize(env.get("MOUSEDATABASE_MGI_DRIVER_NAME"),
          env.get("MOUSEDATABASE_MGI_CONNECTION_STRING"));

      HTMLGeneration.setGoogleAnalyticsId(env.get("GOOGLE_ANALYTICS_ACCOUNT"),
          env.get("GOOGLE_ANALYTICS_DOMAIN_SUFFIX"));

      //TODO: Send APPINSIGHTS_INSTRUMENTATIONKEY to logger;

      MouseMail.intitialize(env.get("MOUSEDATABASE_SMTP_SERVER"), env.get("MOUSEDATABASE_SMTP_USER"),
          env.get("MOUSEDATABASE_SMTP_PW"), Integer.parseInt(env.get("MOUSEDATABASE_SMTP_PORT")),
          Boolean.parseBoolean(env.get("MOUSEDATABASE_SMTP_SSL_ENABLED")));

    } catch (NamingException e) {
      Log.Error("Naming exception getting environment value", e);
    }
  }

}
