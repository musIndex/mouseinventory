package edu.ucsf.mousedatabase.listeners;

import java.beans.Introspector;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

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

public class ContextListener implements ServletContextListener
{

  @Override
  @SuppressWarnings ("rawtypes")
  public void contextDestroyed(ServletContextEvent event)
  {
    Log.Info("Mouse Inventory Web App Removed.");

    try
    {
      Introspector.flushCaches();
      for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements();)
      {
        Driver driver = (Driver) e.nextElement();
        if (driver.getClass().getClassLoader() == getClass().getClassLoader())
        {
          Log.Info("De-registering " + driver.toString());
          DriverManager.deregisterDriver(driver);
        }
      }
      Log.Info("Removed JDBC driver from DriverManager.");
    }
    catch (Throwable e)
    {
      System.err.println("Failed to cleanup ClassLoader for webapp");
      e.printStackTrace();
    }

  }

  @Override
  public void contextInitialized(ServletContextEvent event)
  {
    Log.Initialize();
    event.getServletContext();

    Log.Info("Mouse Inventory Web App Starting.");





    ImportHandler.InitializeDefinitions();

    Context initCtx;
    try
    {
      initCtx = new InitialContext();

        Context envCtx = (Context) initCtx.lookup("java:comp/env");

        MGIConnect.Initialize((String)envCtx.lookup("MOUSEDATABASE_MGI_DRIVER_NAME"),
          (String)envCtx.lookup("MOUSEDATABASE_MGI_DATABASE_URL"),
          (String)envCtx.lookup("MOUSEDATABASE_MGI_DATABASE_USER"),
          (String)envCtx.lookup("MOUSEDATABASE_MGI_DATABASE_PW"));


        HTMLGeneration.setGoogleAnalyticsId(
            (String)envCtx.lookup("GOOGLE_ANALYTICS_ACCOUNT"),
            (String)envCtx.lookup("GOOGLE_ANALYTICS_DOMAIN_SUFFIX"));
        HTMLGeneration.SiteName = (String)envCtx.lookup("MOUSEDATABASE_SITE_NAME");
        HTMLGeneration.AdminEmail = (String)envCtx.lookup("MOUSEDATABASE_ADMINISTRATOR_EMAIL");

    }
    catch (NamingException e) {
      // TODO Auto-generated catch block
      Log.Error("Naming exception getting environment value",e);
    }
  }

}
