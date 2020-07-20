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
		Log.Info("downloadServlet reached");
		
		Integer id = (Integer) Integer.parseInt(request.getParameter("mouseID"));
		Log.Info("id is " + id);
	
		try {

			String fileName = request.getParameter("fileName");
			Log.Info("filename returned is " + fileName);
			String path = "/userfiles/" + id + "/" +fileName;
			response.setContentType("text");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName +"\"");
			OutputStream output = response.getOutputStream();
			File downloadFile = new File(path);
	        FileInputStream input = new FileInputStream(downloadFile);

	        Log.Info("file path is " + path);
			IOUtils.copy(input, output);

		    output.close();
			input.close();
			
			Log.Info("end of download servlet");
			
		} catch (Exception exception) {
			Log.Error("exception in download servlet", exception);
		}		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
