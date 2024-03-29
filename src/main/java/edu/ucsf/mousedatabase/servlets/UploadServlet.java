package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.siteRoot;
import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;
import static edu.ucsf.mousedatabase.HTMLGeneration.urlEncode;

import java.io.File;
import java.io.IOException;

//import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
//import jakarta.ws.rs.Path;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;

//import com.microsoft.applicationinsights.core.dependencies.google.logging.type.HttpRequest;

import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import org.apache.commons.io.FilenameUtils;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.objects.ChangeRequest;

import java.util.regex.*; 

//@Path("/{name}")
/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String mouseFieldName =  "mouseID";
	public static final String fileFieldName =  "fieldName";
	public static final String newNameFieldName =  "newName";
	private static final String defaultFileName = "";
	
	
	//private boolean loggedInAsAdmin = true;
	
	

       
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
		//ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory ());
		String mouseID = "";
		String fileName = defaultFileName;
		String fileStatus = "";
		
		ArrayList<File> files = new ArrayList<File>();

	    try {
	    	
	    	//List items = uploadHandler.parseRequest(request);
	    	Collection<Part> items = request.getParts();
	    	Iterator<Part> itr = items.iterator();
	        //Iterator itr = items.iterator();
	        
	        
	        
	        while(itr.hasNext()) {
	        	Log.Info("found item");
	        	FileItem item = (FileItem) itr.next();
	            if(item.isFormField()) {
	            	Log.Info("is form field");
	            }
	           
	            	
	             if (item.getFieldName().equals(mouseFieldName)){
	            		mouseID = item.getString();
	            		Log.Info("is mouseID"+mouseID);
	            	}else if  (item.getFieldName().contentEquals(newNameFieldName)) {
	            		fileName = item.getString();
	            		
	            	} else if (item.getFieldName().contentEquals(fileFieldName)) {
						
	            		Log.Info("is file"); 
	            		
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
					

						File f = new File(mouseID);
						if(f.exists() && f.isDirectory()) { 
							Log.Info("folder exists");
						} else {
							Log.Info("folder does not exist");
						}
						Log.Info("in servlet, filename is currently : " + fileName);
						
						 File file = new File(fileName);
	            		 Log.Info("about to write");
	            		 
	            		 Path fileN = Paths.get(fileName);
	            		 item.write(fileN);
	            		 //item.write(@PathParam(fileName));
	            		
	            		
	            		 files.add(file);
	            		Log.Info("wrote file");
	            	
					} else {
	            		Log.Info("name = " + item.getName());
	            	}
	            }
	        
	        if(!files.isEmpty() && mouseID != null) {
				
			if (request.isUserInRole("administrator")){
				fileStatus = "approved";
				Log.Info("admin approved file");
			}else {
				fileStatus = "new";
				Log.Info("user submitted file");
				
			}
				
				DBConnect.sendFilesToDatabase(files, mouseID, fileStatus); 
				
	        	Log.Info("sending files to database");
	        } else {
	        	Log.Info("files or mouseID not set");
	        }
	    
	       
	    } catch (Exception e) {
	    	Log.Info("Exception occurred while processing post request for file upload");
	    }
	   
	      
	    if (request.isUserInRole("administrator")) {
	      response.sendRedirect(HTMLGeneration.adminRoot + "EditMouseForm.jsp?id=" + mouseID);
	    }else {
	    	 response.setContentType("text/plain");  
	    	 response.setCharacterEncoding("UTF-8"); 
	    	 response.getWriter().write(fileName); 
	    	 request.getSession().setAttribute("fileName",fileName);
	 		 response.sendRedirect(siteRoot + "ChangeRequestForm.jsp?mouseID=" + mouseID);
	    	
	    }
	    	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
		
}}

 
