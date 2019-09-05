package edu.ucsf.mousedatabase.servlets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.Log;

public class UploadServlet extends HttpServlet {
	public static final String mouseFieldName = "mouseID";
	public static final String fileFieldName = "file";
	private static final long serialVersionUID = 1L;


	public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	}
	
	@SuppressWarnings("rawtypes")
	  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Log.Info("recieved file for processing");
	    ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory ());
	    try {
	    	List items = uploadHandler.parseRequest(request);
	        Iterator itr = items.iterator();
	        //final HashMap<String,String> parameters = new HashMap<String, String>();
	        ArrayList<File> files = new ArrayList<File>();
	        String mouseID = "";
	        
	        while(itr.hasNext()) {
	        	FileItem item = (FileItem) itr.next();
	            if(item.isFormField()) {
	            	//parameters.put(item.getFieldName(), item.getString());
	            	if (item.getFieldName().equals(mouseFieldName)){
	            		mouseID = item.getString();
	            	} else if (item.getFieldName().contentEquals(fileFieldName)) {
	            		//dataFile = item;
	            		String fileName = item.getName();
	            		 if (fileName != null) {
	            		     fileName = FilenameUtils.getName(fileName);
	            		 }
	            		 File file = new File(fileName);
	            		 item.write(file);
	            		files.add(file);
	            	}
	            }
	        }
	        if(!files.isEmpty() && mouseID != null) {
	        	DBConnect.sendFilesToDatabase(files, mouseID);
	        	Log.Info("sending files to database");
	        } else {
	        	Log.Info("files or mouseID not set");
	        }
	    	
	    } catch (Exception e) {
	    	Log.Info("Exception occurred while processing post request for file upload");
	    }
	}
}
