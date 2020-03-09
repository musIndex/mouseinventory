package edu.ucsf.mousedatabase.servlets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.applicationinsights.core.dependencies.google.logging.type.HttpRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;
import java.util.regex.*; 

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String mouseFieldName =  "mouseID";
	public static final String fileFieldName =  "fieldName";
	public static final String newNameFieldName =  "newName";
	private static final String defaultFileName = "";
	//public static final String userFieldName = "adminState";
	//private static final String adminStateName = "admin";
	private boolean loggedInAsAdmin = true;
	
	boolean isAdmin(HttpServletRequest request) {
	  //if (adminState.equals(adminStateName)) {
	if(Pattern.matches(request.getRequestURI(), ".*/admin/.*")){
	    return true;
	  } else {
	    return false;
	  }
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Log.Info("recieved file for processing");
		ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory ());
		String mouseID = "";
		String fileName = defaultFileName;
		ArrayList<File> files = new ArrayList<File>();

		// File folder = new File(":/");
		// 	String[] listOfFiles = folder.list();
		// 	for (String f : listOfFiles){
		// 		Log.Info("filename: " + f);
		// 	}
		
	    try {
	    	List items = uploadHandler.parseRequest(request);
	        Iterator itr = items.iterator();
	        //final HashMap<String,String> parameters = new HashMap<String, String>();
	        loggedInAsAdmin = isAdmin(request);
	        Log.Info("setting admin in uploadServlet: " + loggedInAsAdmin);
	        
	        
	        while(itr.hasNext()) {
	        	Log.Info("found item");
	        	FileItem item = (FileItem) itr.next();
	            if(item.isFormField()) {
	            	Log.Info("is form field");
	            }
	            	//parameters.put(item.getFieldName(), item.getString());
	            	if (item.getFieldName().equals(mouseFieldName)){
	            		mouseID = item.getString();
	            		Log.Info("is mouseID");
	            	}else if  (item.getFieldName().contentEquals(newNameFieldName)) {
	            		fileName = item.getString();
	            		
	            	} else if (item.getFieldName().contentEquals(fileFieldName)) {
						//this might still not work with pdfs- depends on if fileitem works with them.
	            		//dataFile = item;
	            		Log.Info("is file"); 
	            		//String fileName = item.getName();
	            		if(fileName.length() == 0) {
	            			fileName = item.getName();
	            		}
	            		Log.Info("filename is " + fileName); 
	            		 if (fileName != null) {
	            		     fileName = FilenameUtils.getName(fileName);
	            		     Log.Info("new filename: [" + fileName + "]");
						 }
						 ArrayList<String> mouseFiles = DBConnect.getFilenamesByMouseID(mouseID);
						 if(mouseFiles.contains(fileName)){
							String fileParts[] = fileName.split("\\.");
							fileName = fileParts[0] + " (1)." + fileParts[1];
						 }
						// while(mouseFiles.contains(fileName)){
						for(int i = 0; i <mouseFiles.size(); i++){
							if(mouseFiles.contains(fileName)){
								int dotPlace = fileName.indexOf(".");
								int place = dotPlace - 2;//fileName.length() - 2;
								int num = Integer.parseInt(String.valueOf(fileName.charAt(place)));
								Log.Info("num = " + num);
								int num2 = num + 1;
								Log.Info("num2 = " + num2);
							   fileName = fileName.replace("(" + num + ")", "(" + num2 + ")");//    charAt(place) = Integer.toString(num + 1);
							
							}
						}
						 //Path path = Paths.get(mouseID);

						//  if (Files.exists(path)) {
						// 	 Log.Info("folder exists");
						//  } else {
						// 	 Log.Info("folder does not exist");
						//  }


						File f = new File(mouseID);
						if(f.exists() && f.isDirectory()) { 
							Log.Info("folder exists");
						} else {
							Log.Info("folder does not exist");
						}
						Log.Info("in servlet, filename is currently : " + fileName);
						//  String targetName = "6/" + fileName; 
						//  Log.Info("target name is " + targetName);
						//  File file = new File(targetName);
						 File file = new File(fileName);
	            		 Log.Info("about to write");
	            		 item.write(file);
	            		files.add(file);
	            		Log.Info("wrote file");
	            	//} else if (item.getFieldName().contentEquals(userFieldName)) {
	            	  
	            	  //isAdmin = Boolean.parseBoolean(item.getString());
					} else {
	            		Log.Info("name = " + item.getName());
	            	}
	            }
	        //}
	        if(!files.isEmpty() && mouseID != null) {
				DBConnect.sendFilesToDatabase(files, mouseID); 
				
	        	Log.Info("sending files to database");
	        } else {
	        	Log.Info("files or mouseID not set");
	        }
	    	 
	    } catch (Exception e) {
	    	Log.Info("Exception occurred while processing post request for file upload");
	    }	
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	    
	      response.sendRedirect(HTMLGeneration.adminRoot + "EditMouseForm.jsp?id=" + mouseID);

		
		/*
		else {
		//set this to wherever the new redirect should be
		String url = HTMLGeneration.siteRoot + "ChangeRequestForm.jsp" + "?mouseID=" + request.getParameter(mouseID) + "&success=true";

	      response.sendRedirect(url);// + "EditMouseForm.jsp?id=" + mouseID); 
		}
		*/
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
