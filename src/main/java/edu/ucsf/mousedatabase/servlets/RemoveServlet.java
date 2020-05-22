package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.siteRoot;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;
//import static edu.ucsf.mousedatabase.HTMLGeneration.siteRoot;

/**
 * Servlet implementation class RemoveServlet
 */
public class RemoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Integer id = Integer.parseInt(request.getParameter("id"));
		//String mouseID = request.getParameter("mouseID");
		Integer mouseID = Integer.parseInt(request.getParameter("mouseID"));
		String filestatus ="";
		
		try {
			
			if (request.isUserInRole("administrator")){
				filestatus = DBConnect.getFileStatus(id);
				Log.Info("filestatus is "+filestatus);
				DBConnect.deleteFileByID(id, mouseID, filestatus);
			}else {
				filestatus = "delete";
				Log.Info("filestatus is "+filestatus);
				DBConnect.updateFileStatus(filestatus, id);
					
			}
			
		} catch (Exception e) {}
		if (request.isUserInRole("administrator")) {
			response.sendRedirect(HTMLGeneration.adminRoot + "EditMouseForm.jsp?id=" + mouseID);
		}else {
			//response.sendRedirect(HTMLGeneration.siteRoot + "ChangeRequestForm.jsp?mouseID="+mouseID);
			request.getRequestDispatcher(siteRoot + "ChangeRequestForm.jsp?mouseID=" + mouseID).forward(request, response);
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
