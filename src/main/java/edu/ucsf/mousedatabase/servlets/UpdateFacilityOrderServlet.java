package edu.ucsf.mousedatabase.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.ucsf.mousedatabase.DBConnect;

/**
 * Servlet implementation class UpdateFacilityOrderServlet
 */
public class UpdateFacilityOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateFacilityOrderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String rawPositions = request.getParameter("positions");
		if (rawPositions == null) {
		  fail(response,"Positions were not received.");
		  return;
		}
		try {
  		String[] positions = rawPositions.split(",");
  		HashMap<Integer,Integer> newPositions = new HashMap<Integer,Integer>();
  		for (String token : positions) {
  		  String[] subtoken = token.split("-");
  		  int facilityId = Integer.parseInt(subtoken[0]);
  		  int position = Integer.parseInt(subtoken[1]);
  		  if (newPositions.get(position) != null) {
  		    fail(response,"Multiple facilities assigned to position " + position + ".  Tried to assign " 
  		                + facilityId + " but " + newPositions.get(position) + " was already assigned!");
  		    return;
  		  }
  		  newPositions.put(position, facilityId);
  		}
  		
  		for (int position : newPositions.keySet()) {
  		  int facilityId = newPositions.get(position);
  		  DBConnect.updateFacilityPosition(facilityId,position);
  		}
  		
  		success(response);
		}
  	catch(Exception e) {
  	  fail(response,"Failed: " + e.getMessage());
  	}
	}
	
	private void success(HttpServletResponse response) throws IOException{
	  Properties data = new Properties();
	  data.setProperty("success", "true");
	  new Gson().toJson(data,response.getWriter());
	  response.setStatus(HttpServletResponse.SC_OK);
	}
	
	private void fail(HttpServletResponse response, String reason) throws IOException{
	  Properties data = new Properties();
    data.setProperty("message", reason);
    new Gson().toJson(data,response.getWriter());
    response.setStatus(HttpServletResponse.SC_OK);
  }

}
