package edu.ucsf.mousedatabase.objects;

import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;
import org.jsoup.Jsoup;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.Log;

//syncrhonously send email

public class MouseMail {

    private static String SMTP_SERVER;
    private static String SMTP_USER;
    private static String SMTP_PW;
    private static int SMTP_PORT;
    private static boolean SMTP_SSL_ENABLED;
  
    public static void intitialize(String server, String user, String pw, int port, boolean sslEnabled){      
        SMTP_SERVER = server;
        SMTP_USER = user;
        SMTP_PW = pw;
        SMTP_PORT = port;
        SMTP_SSL_ENABLED = sslEnabled;
    }
  
  
    public static String PlainEmailType = "plain";
    public static String HTMLEmailType = "html";
    public static String MultipartEmailType = "multipart";
    
    public static String SentStatus = "sent";
    public static String ErrorStatus = "error";
    public static String DraftStatus = "draft";

    
    public int id;
    public String recipient;
    public String ccs;
    public String bccs;
    public String subject;
    public String body;
    public String emailType;
    public String status;
    public String category;
    public String templateName;
    public Timestamp dateCreated;
    public String errorMessage;
    public String attachmentNames;
    
    private HashMap<String,byte[]> attachments;
    
    
    
    public MouseMail(String recipient, String ccs, String bccs, String subject, String body, String category, String templateName, String attachmentNames) {
      super();
      this.recipient = recipient;
      this.ccs = ccs;
      this.bccs = bccs;
      this.subject = subject;
      this.body = body;
      this.emailType = HTMLEmailType;
      this.category = category;
      this.templateName = templateName;
      this.attachmentNames = attachmentNames;
    }


    public static MouseMail send(String recipient, String cc, String bcc, String subject, String body, String category, 
                                int templateID, int draftId,  HashMap<String,byte[]> attachments) {
      
      String templateName = null;
      if (templateID > 0) {
        EmailTemplate template = DBConnect.loadEmailTemplate(templateID);
        templateName = template.name;
      }
      
      String attachmentNames = "";
      if (attachments != null) {
        attachmentNames = StringUtils.join(attachments.keySet(),", ");
      }
      MouseMail mail = new MouseMail(recipient, cc, bcc, subject, body, category, templateName,attachmentNames);
      mail.attachments = attachments;
      mail.trySend();
      mail.save();
      
      if (mail.status != ErrorStatus && draftId > 0) {
        DBConnect.deleteEmail(draftId);
      }
      
      return mail;
    }
    
    private void trySend(){
      //TODO check type, support plain and multipart?
      try {
        HtmlEmail email = new HtmlEmail();
        email.setHostName(SMTP_SERVER);
        email.setSmtpPort(SMTP_PORT);
        email.setAuthenticator(new DefaultAuthenticator(SMTP_USER, SMTP_PW));
        if (SMTP_SSL_ENABLED){
          email.setSSL(true);
        }
        email.setFrom(DBConnect.loadSetting("admin_info_email").value);
        email.setSubject(subject);
        email.setHtmlMsg(body);
        email.setTextMsg(stripHtml(body));
        email.addTo(recipient);
        if (ccs != null && !ccs.isEmpty()){
          email.addCc(ccs);
        }
        if (bccs != null && !bccs.isEmpty()){
          email.addBcc(bccs);
        }
        
        if (attachments != null) {
          for(String attachmentName : attachments.keySet()) {
            email.attach(new ByteArrayDataSource(attachments.get(attachmentName),null),attachmentName,attachmentName);
          }
        }
        
        Log.Info("Sending mail " + subject + " to " + recipient + " + " + ccs);
        email.send();
        status = SentStatus;
      }
      catch(Exception e){
        Log.Error("Error sending email",e);
        status = ErrorStatus;
        errorMessage = e.getMessage();
      }
    }
    
    private void save(){
      id = DBConnect.insertEmail(this);
    }
   
    public int saveAsDraft(){
      status = DraftStatus;
      id = DBConnect.insertEmail(this);
      return id;
    }
    
    private String stripHtml(String text){
      return Jsoup.parse(text).text();
    }
}
