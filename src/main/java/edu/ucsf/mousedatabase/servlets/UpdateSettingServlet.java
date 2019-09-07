package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.HTMLUtilities;
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
	  HTMLUtilities.logRequest(request);
	  Setting setting = new Setting();
    setting.id = stringToInt(request.getParameter("id"));
    setting.name = request.getParameter("name");
    setting.category_id = stringToInt(request.getParameter("category_id"));
    setting.label = request.getParameter("label");
    setting.value = request.getParameter("setting_value");
    setting.secondaryValue = request.getParameter("secondary_value");
    
    String message;
    
    String redirectPage = request.getParameter("redirect_page");
    String redirectParams = request.getParameter("redirect_params");
    
    if (setting.id > 0) {
      
      DBConnect.updateSetting(setting);
      message = "Updated setting '" + setting.label + "' successfully.";
    }
    else
    {
      DBConnect.insertSetting(setting);
      message = "Added new " + Setting.getSettingCategory(setting.category_id).Name + " setting successfully.";
    }
    
    response.sendRedirect(HTMLGeneration.adminRoot + redirectPage + "?message=" + HTMLGeneration.urlEncode(message) + "&" + redirectParams);
  
	}

}
