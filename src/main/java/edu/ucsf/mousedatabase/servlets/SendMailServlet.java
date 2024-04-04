package edu.ucsf.mousedatabase.servlets;

import static edu.ucsf.mousedatabase.HTMLGeneration.stringToInt;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;

import com.google.gson.Gson;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLUtilities;
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
	  HTMLUtilities.logRequest(request);
    Properties data = new Properties();
	  Object[] maps;
    
    
	  
	  
    
  
    if(!request.isUserInRole("administrator")) {
      //this is actually redundant because the servlet is behind /admin but makes me feel better
      data.setProperty("error","you must be an administrator to send mail");
    }
    else if (request.getParameter("delete") != null)  {
      int deleteEmailId = stringToInt(request.getParameter("delete"));
      DBConnect.deleteEmail(deleteEmailId);
      data.setProperty("id",Integer.toString(deleteEmailId));
    }
    else {
      try {
        
        maps = fetchMultipartParams(request, "attachment_");
        
      } catch (FileUploadException e) {
        data.setProperty("error","Failed to parse form parameters: " + e.getMessage());
        Gson gson = new Gson();
        gson.toJson(data, response.getWriter());
        response.setStatus(HttpServletResponse.SC_OK);
        return; 
      }
      
      @SuppressWarnings("unchecked")
      HashMap<String,String> params = (HashMap<String,String>)maps[0];
      @SuppressWarnings("unchecked")
      HashMap<String,byte[]> attachments = (HashMap<String,byte[]>)maps[1];
      
      String recipient = params.get("recipient");
      String cc = params.get("cc");
      String bcc = params.get("bcc");
      String subject = params.get("subject");
      String body = params.get("body");
      String category = params.get("category");
      int templateID = stringToInt(params.get("template_id"));
      int oldDraftID = stringToInt(params.get("old_draft_id"));
      boolean saveAsDraft = Boolean.parseBoolean(params.get("save_as_draft"));
      
      if (saveAsDraft){
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
  
        if (errors == false) {
          MouseMail mail = MouseMail.send(recipient, cc, bcc, subject, body, category,templateID,oldDraftID,attachments);
          data.setProperty("id", Integer.toString(mail.id));
          if (MouseMail.ErrorStatus.equals(mail.status)) {
            data.setProperty("error",mail.errorMessage);
          }
        }
      }
  	}
    
    Gson gson = new Gson();
    gson.toJson(data, response.getWriter());
    response.setStatus(HttpServletResponse.SC_OK);
	}

	
	 @SuppressWarnings("rawtypes")
	private Object[] fetchMultipartParams(HttpServletRequest request, String attachmentParameterName) throws FileUploadException, IOException, ServletException {
	  //ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory ());
    //List items = uploadHandler.parseRequest(request);
		 DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
		 JakartaServletFileUpload uploadHandler = new JakartaServletFileUpload(factory);
		 List items = uploadHandler.parseRequest(request);
         Iterator itr = items.iterator();
         HashMap<String,byte[]> attachments = new HashMap<String, byte[]>();
         HashMap<String,String> parameters = new HashMap<String, String>();
         while(itr.hasNext()) {
        	 FileItem item = (FileItem) itr.next();

        	 if(!item.isFormField()) {
        		 if (item.getFieldName().startsWith(attachmentParameterName)) {
        			 attachments.put(item.getName(),item.get());
        		 }
        	 	}else{
        	 		parameters.put(item.getFieldName(),item.getString());
      }
    }
   
    return new Object[]{parameters,attachments};
	
	 }
	

	
}
