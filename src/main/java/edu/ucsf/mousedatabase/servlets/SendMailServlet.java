package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.objects.EmailTemplate;
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
    String bcc = request.getParameter("bcc");
    String subject = request.getParameter("subject");
    String body = request.getParameter("body");
    String category = request.getParameter("category");
    int templateID = stringToInt(request.getParameter("template_id"));
    int oldDraftID = stringToInt(request.getParameter("old_draft_id"));
    
    int deleteEmailId = stringToInt(request.getParameter("delete"));
    boolean saveAsDraft = Boolean.parseBoolean(request.getParameter("save_as_draft"));
    Properties data = new Properties();
    
    if(!request.isUserInRole("administrator")) {
      //this is actually redundant because the servlet is behind /admin but makes me feel better
      data.setProperty("error","you must be an administrator to send mail");
    }
    else if (deleteEmailId > 0)  {
      DBConnect.deleteEmail(deleteEmailId);
      data.setProperty("id",Integer.toString(deleteEmailId));
    }
    else if (saveAsDraft){
      String templateName = null;
      if (templateID > 0) {
        EmailTemplate template = DBConnect.loadEmailTemplate(templateID);
        templateName = template.name;
      }
    
      MouseMail mail = new MouseMail(recipient, cc, bcc, subject, body, category, templateName,null);
      int newDraftId = mail.saveAsDraft();
      data.setProperty("id",Integer.toString(newDraftId));
      
      if (!mail.status.equals(MouseMail.ErrorStatus) && oldDraftID > 0) {
        DBConnect.deleteEmail(oldDraftID);
      }
    }
    else {
      boolean errors = false;
      HashMap<String,byte[]> attachments = null;
      try {
        attachments = fetchAttachments(request,"attachment_");
      }
      catch (FileUploadException ex) {
        data.setProperty("error", "Error fetching attachment: " + ex.getMessage());
        errors = true;
      }
      if (errors == false) {
        MouseMail mail = MouseMail.send(recipient, cc, bcc, subject, body, category,templateID,oldDraftID,attachments);
        data.setProperty("id", Integer.toString(mail.id));
        if (MouseMail.ErrorStatus.equals(mail.status)) {
          data.setProperty("error",mail.errorMessage);
        }
      }
    }
    
    Gson gson = new Gson();
    gson.toJson(data, response.getWriter());
    response.setStatus(HttpServletResponse.SC_OK);
	}

	
	 @SuppressWarnings("rawtypes")
	private HashMap<String,byte[]> fetchAttachments(HttpServletRequest request, String parameterName) throws FileUploadException {
	  ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory ());
    List items = uploadHandler.parseRequest(request);
    Iterator itr = items.iterator();
    HashMap<String,byte[]> attachments = new HashMap<String, byte[]>();
    while(itr.hasNext()) {
      FileItem item = (FileItem) itr.next();

      if(!item.isFormField()) {
        if (item.getFieldName().startsWith(parameterName)) {
          attachments.put(item.getName(),item.get());
        }
      }
    }
   
    return attachments;
	}
	
}
