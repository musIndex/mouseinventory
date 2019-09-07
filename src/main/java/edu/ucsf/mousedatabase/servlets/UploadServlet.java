package edu.ucsf.mousedatabase.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String mouseFieldName =  "mouseID";
	public static final String fileFieldName =  "fieldName";
	public static final String newNameFieldName =  "newName";
       
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
		String fileName = "";
		ArrayList<File> files = new ArrayList<File>();
		
	    try {
	    	List items = uploadHandler.parseRequest(request);
	        Iterator itr = items.iterator();
	        //final HashMap<String,String> parameters = new HashMap<String, String>();
	        
	        
	        
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
	            		//dataFile = item;
	            		Log.Info("is file");
	            		//String fileName = item.getName();
	            		if(fileName == null) {
	            			fileName = item.getName();
	            		}
	            		Log.Info("filename is " + fileName);
	            		 if (fileName != null) {
	            		     fileName = FilenameUtils.getName(fileName);
	            		     Log.Info("new filename: " + fileName);
	            		 }
	            		 File file = new File(fileName);
	            		 Log.Info("about to write");
	            		 item.write(file);
	            		files.add(file);
	            		Log.Info("wrote file");
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
