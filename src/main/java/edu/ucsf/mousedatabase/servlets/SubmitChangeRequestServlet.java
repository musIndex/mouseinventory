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
import edu.ucsf.mousedatabase.HTMLUtilities;
import edu.ucsf.mousedatabase.objects.ChangeRequest;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import edu.ucsf.mousedatabase.Log;
import java.util.regex.*; 

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
		HTMLUtilities.logRequest(request);
	  String message = "";
		boolean success = false;


		try {
		  ChangeRequest changeRequest = new ChangeRequest();
		  
		  changeRequest.setMouseID(stringToInt(request.getParameter("mouseID")));
		  
		  changeRequest.setEmail(request.getParameter("email"));
		  changeRequest.setFirstname(request.getParameter("firstname"));
		  changeRequest.setLastname(request.getParameter("lastname"));
		  
		  changeRequest.setUserComment(request.getParameter("userComment"));
		  //getFileName and getDeleteFileName strings
		  
		  //changeRequest.setNewFileNames((String)request.getSession().getAttribute("fileName"));
		  
		  changeRequest.setNewFileNames((String)request.getAttribute("fileName"));
		  //changeRequest.setNewFileNames(request.getParameter("fileNames"));
		  changeRequest.setDeleteFileNames(request.getParameter("deleteFileNames"));
		  
		  changeRequest.setActionRequested(request.getParameter("actionRequested"));
		  changeRequest.setCryoLiveStatus(request.getParameter("cryoLiveStatus"));
		  changeRequest.setGeneticBackgroundInfo(request.getParameter("geneticBackgroundInfo"));
		  
		  int holderId = stringToInt(request.getParameter("holderId"));
		  changeRequest.setHolderId(holderId);
		  if (holderId == -2) {
  		  changeRequest.setHolderName(request.getParameter("holderName"));
		  }  
		  
		  int facilityId = stringToInt(request.getParameter("facilityId"));
		  changeRequest.setFacilityId(facilityId);
		  if (facilityId == -2) {
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
