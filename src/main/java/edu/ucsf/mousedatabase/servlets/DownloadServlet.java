package edu.ucsf.mousedatabase.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.Log;

/**
 * Servlet implementation class DownloadServlet
 */
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//String mouseID = request.getParameter("mouseID");
		//String fileName = request.getParameter("fileName");
		Log.Info("download called");
		Integer id = (Integer) Integer.parseInt(request.getParameter("ID"));
		//File file = null;
		//int BUFF_SIZE = 1024;
		//byte[] buffer = new byte[BUFF_SIZE];
		try {
			//file = DBConnect.getFileByNameAndMouseID(fileName, mouseID);
			//file = DBConnect.getFileByID(id);  //issue: need to get file length
			String fileName = DBConnect.getFilePathByID(id);
			//FileInputStream input = new FileInputStream("/userfiles/" + fileName); 
			

			response.setContentType("text");
			//response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() +"\"");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName +"\"");
			//response.setContentLength((int) file.length()); see if it works with this surpressed
			//Log.Info("file length: " + file.length());
			OutputStream output = response.getOutputStream();
			FileInputStream input = new FileInputStream(fileName);
			IOUtils.copy(input, output);

			/*
			int len = input.read(buffer);
		    while (len != -1) {
		    	output.write(buffer, 0, content);
		    }
		    */
		    //output.flush();
		    output.close();
		    input.close();
			
			//FileUtils.copyFile(file, response.getOutputStream());
		} catch (Exception e) {}		
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
