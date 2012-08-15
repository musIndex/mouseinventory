package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.objects.Setting;

/**
 * Servlet implementation class UpdateSettingServlet
 */
public class UpdateSettingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateSettingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  Setting setting = new Setting();
    setting.id = stringToInt(request.getParameter("id"));
    setting.name = request.getParameter("name");
    setting.category = request.getParameter("category");
    setting.label = request.getParameter("label");
    setting.value = request.getParameter("setting_value");
    
    String message;
    
    String redirectPage = request.getParameter("redirect_page");
    String redirectParams = request.getParameter("redirect_params");
    
    if (setting.id > 0) {
      
      DBConnect.updateSetting(setting);
      message = "Updated setting '" + setting.name + "' successfully.";
    }
    else
    {
      DBConnect.insertSetting(setting);
      message = "Added new " + setting.category + " setting '" + setting.name + "' successfully.";
    }
    
    response.sendRedirect(HTMLGeneration.adminRoot + redirectPage + "?message=" + HTMLGeneration.urlEncode(message) + "&" + redirectParams);
  
	}

}
