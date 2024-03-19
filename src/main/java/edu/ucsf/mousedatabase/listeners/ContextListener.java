package edu.ucsf.mousedatabase.listeners;

import java.beans.Introspector;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Map;
import java.lang.System;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

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
    Log.Info("SMTP_PORT: " + env.get("SMTP_PORT"));
    
    MGIConnect.Initialize(env.get("MGI_DB_DRIVER_CLASSNAME"), env.get("MGI_DB_CONNECTION_STRING"));

    HTMLGeneration.setGoogleAnalyticsId(env.get("GOOGLE_ANALYTICS_ACCOUNT"), env.get("GOOGLE_ANALYTICS_DOMAIN_SUFFIX"));

    MouseMail.intitialize(env.get("SMTP_SERVER"), env.get("SMTP_USER"), env.get("SMTP_PW"),
        Integer.parseInt(env.get("SMTP_PORT")), Boolean.parseBoolean(env.get("SMTP_SSL_ENABLED")));

  }

}
