package edu.ucsf.mousedatabase.servlets;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;

import static edu.ucsf.mousedatabase.HTMLGeneration.*;
import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.objects.ChangeRequest;

/**
 * Servlet implementation class SubmitChangeRequestServlet
 */
public class SubmitChangeRequestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitChangeRequestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "";
		boolean success = false;
		try {
		  ChangeRequest changeRequest = new ChangeRequest();
		  
		  changeRequest.setMouseID(stringToInt(request.getParameter("mouseID")));
		  
		  changeRequest.setEmail(request.getParameter("email"));
		  changeRequest.setFirstname(request.getParameter("firstname"));
		  changeRequest.setLastname(request.getParameter("lastname"));
		  
		  changeRequest.setUserComment(request.getParameter("userComment"));
		  
		  changeRequest.setActionRequested(request.getParameter("actionRequested"));
		  changeRequest.setCryoLiveStatus(request.getParameter("cryoLiveStatus"));
		  
		  int holderId = stringToInt(request.getParameter("holderId"));
		  
		  if (holderId > 0) {
		    changeRequest.setHolderId(holderId);
		  }
		  else {
  		  changeRequest.setHolderName(request.getParameter("holderName"));
		  }  
		  
		  int facilityId = stringToInt(request.getParameter("facilityId"));
		  
		  if (facilityId > 0) {
		    changeRequest.setFacilityId(facilityId);
		  }
		  else {
		    changeRequest.setFacilityName(request.getParameter("facilityName"));
		  }
		  request.getSession().setAttribute("changeRequest", changeRequest);
		  message = changeRequest.validate();
		  if (!message.isEmpty()) {
		    return;
		  }
		  
		  int existingRequest = DBConnect.changeRequestExists(changeRequest);
		  if (existingRequest >= 0) {
		     message = "You have already submitted this change request.";
		     return;
		  }
		  
		  changeRequest.setStatus("new");
		  changeRequest.setRequestSource("Change request form");
      DBConnect.insertChangeRequest(changeRequest);
		  success = true;
		}
		catch (Exception e) {
		  message = e.getMessage();
		}
		finally {
		  response.sendRedirect(siteRoot + "ChangeRequestForm.jsp?mouseID=" + request.getParameter("mouseID") 
		      + "&success=" + urlEncode(Boolean.toString(success)) + "&message=" + urlEncode(message));
		}
	}
}
