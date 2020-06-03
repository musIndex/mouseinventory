package edu.ucsf.mousedatabase.valves;

import org.apache.catalina.valves.ValveBase;

import edu.ucsf.mousedatabase.Log;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import java.io.IOException;
import javax.servlet.ServletException;

public class BasicValve extends ValveBase {
	
	 public void invoke(Request request, Response response) throws IOException, ServletException {
		 
		 Log.Info("This should do something when a page is loaded.");
		 getNext().invoke(request, response);
	 }

}
