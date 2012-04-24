package edu.ucsf.mousedatabase.servlets;

import java.io.IOException;
import java.nio.charset.Charset;
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
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.HTMLUtilities;
import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.dataimport.ImportHandler;

 
 
public class ImportServlet extends HttpServlet {

	public static final String fileFieldName = "datafile";	
	public static final String importDefinitionIdFieldName = "importType";
	public static final String importDescriptionFieldName = "importDescription";
	private static final long serialVersionUID = 1L;
 
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

	}
	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory ());
		try {
			/*
			 * Parse the request
			 */
			List items = uploadHandler.parseRequest(request);
			Iterator itr = items.iterator();
			
			final HashMap<String,String> parameters = new HashMap<String, String>(); 
			FileItem dataFile = null;
			int importDefinitionId = -1;
			while(itr.hasNext()) 
			{
				FileItem item = (FileItem) itr.next();
				if(item.isFormField()) 
				{
					parameters.put(item.getFieldName(), item.getString());
					//Log.Info("Input field Name = "+item.getFieldName()+", Value = "+item.getString());
					if (item.getFieldName().equals(importDefinitionIdFieldName))
					{
						importDefinitionId = Integer.parseInt(item.getString());
					}
				} 
				else 
				{
					if (item.getFieldName().equals(fileFieldName))
					{
						dataFile = item;
					}
				}

			}
			String[] charsetsToTry = new String[]{"UTF-16","UTF-8","ASCII"};
			//Charset encoding = detectEncoding(dataFile); 			
			ArrayList<HashMap<String,String>> csvData = null;
			
			for (String charsetName : charsetsToTry)
			{
				csvData = HTMLUtilities.readCSVFileFromStream(dataFile,Charset.forName(charsetName));
				if (csvData.size() > 0)
				{
					Log.Info("ImportServlet: Detected charset for " + dataFile.getName() + ": " + charsetName);
					break;
				}
			}
			
			final ArrayList<HashMap<String,String>> finalData = csvData;
			final int finalDefinitionId = importDefinitionId;
			
			Thread t = new Thread(new Runnable(){
				@Override
				public void run() {
					try 
					{
						ImportHandler.handleImport(finalData, finalDefinitionId,parameters);
					} 
					catch (Exception e) 
					{
							Log.Error("Unhandled exception handling import.",e);
					}
				}});
			t.start();
			//redirect to results jsp results page for reportID
			
			response.sendRedirect(HTMLGeneration.adminRoot + "ImportStatus.jsp");
			
		}
		catch(FileUploadException ex) 
		{
			log("Error encountered while parsing the request",ex);
			response.sendRedirect(HTMLGeneration.siteRoot + "error.jsp");
		} 
		catch(Exception ex) 
		{
			log("Error encountered while uploading file",ex);
			throw new ServletException(ex);
		}
 
	}
	
	

//	private Charset detectEncoding(FileItem dataFile) 
//	{
//		// TODO try several, looking at first few characters for dictionary words 
//		return Charset.forName("UTF-16");
//	}
	
	
 
	
	
}