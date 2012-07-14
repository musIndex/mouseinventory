package edu.ucsf.mousedatabase;

import java.sql.Date;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

//syncrhonously send email

public class MouseMail {

    private static String SMTP_SERVER;
    private static String SMTP_USER;
    private static String SMTP_PW;
  
    public static void intitialize(String server, String user, String pw){      
        SMTP_SERVER = server;
        SMTP_USER = user;
        SMTP_PW = pw;
       
    }
  
  
    public static String PlainEmailType = "plain";
    public static String HTMLEmailType = "html";
    public static String MultipartEmailType = "multipart";
    
    public static String SentStatus = "sent";
    public static String ErrorStatus = "error";

    
    public int id;
    public String recipient;
    public String ccs;
    public String bccs;
    public String subject;
    public String body;
    public String emailType;
    public String status;
    public Date dateCreated;
    
    
    
    public MouseMail(String recipient, String ccs, String subject, String body) {
      super();
      this.recipient = recipient;
      this.ccs = ccs;
      this.subject = subject;
      this.body = body;
    }



    public static void send(String recipient, String cc, String subject, String body){
      
      MouseMail mail = new MouseMail(recipient, cc, subject, body);
      mail.trySend();
      mail.save();
    }
    
    private void trySend(){
      try {
        Email email = new SimpleEmail();
        email.setHostName(SMTP_SERVER);
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(SMTP_USER, SMTP_PW));
        email.setSSL(true);
        email.setFrom(HTMLGeneration.AdminEmail);
        email.setSubject(subject);
        email.setMsg(body);
        email.addTo(recipient);
        if (ccs != null && !ccs.isEmpty()){
          email.addCc(ccs);
        }
        if (bccs != null && !bccs.isEmpty()){
          email.addBcc(bccs);
        }
        email.send();
        status = SentStatus;
      }
      catch(Exception e){
        Log.Error("Error sending email",e);
        status = ErrorStatus;
      }
    }
    
    private void save(){
      id = DBConnect.insertEmail(this);
    }
}
