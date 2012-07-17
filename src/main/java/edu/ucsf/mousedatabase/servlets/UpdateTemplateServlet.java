package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.objects.EmailTemplate;

/**
 * Servlet implementation class UpdateTemplateServlet
 */
public class UpdateTemplateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateTemplateServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	  EmailTemplate template = new EmailTemplate();
	  template.id = stringToInt(request.getParameter("id"));
    template.name = request.getParameter("name");
    template.category = request.getParameter("category");
    template.subject = request.getParameter("subject");
    template.body = request.getParameter("body");
    
    String message;
	  
    if (template.id > 0) {
      
      DBConnect.updateEmailTemplate(template);
      message = "Updated template '" + template.name + "' successfully.";
    }
    else
    {
      DBConnect.insertEmailTemplate(template);
      message = "Added new template '" + template.name + "' successfully.";
    }
    
    response.sendRedirect(HTMLGeneration.adminRoot + "ManageEmailTemplates.jsp?command=list&message=" + HTMLGeneration.urlEncode(message));
	}

}
