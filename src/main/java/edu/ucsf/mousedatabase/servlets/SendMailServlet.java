package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import edu.ucsf.mousedatabase.objects.MouseMail;

/**
 * Servlet implementation class SendMailServlet
 */
public class SendMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String recipient = request.getParameter("recipient");
    String cc = request.getParameter("cc");
    String subject = request.getParameter("subject");
    String body = request.getParameter("body");
    String category = request.getParameter("category");
    int templateID = stringToInt(request.getParameter("template_id"));
    
    Properties data = new Properties();
    
    String error = validate(recipient,cc,subject,body);
    
    if (error != null) {
      data.setProperty("error", error);
    }
    else if(!request.isUserInRole("administrator")) {
      //this is actually redundant because the servlet is behind /admin but makes me feel better
      data.setProperty("error","you must be an administrator to send mail");
    }
    else {
      data.setProperty("id", Integer.toString(MouseMail.send(recipient, cc, subject, body, category,templateID)));
    }
    
    Gson gson = new Gson();
    gson.toJson(data, response.getWriter());
    response.setStatus(HttpServletResponse.SC_OK);
	}

	private String validate(String recipient, String cc, String subject, String body){
	  //TODO validate if we end up having trouble with badly formatted emails
	  return null;
	}
	
}
