package edu.ucsf.mousedatabase.objects;

import java.sql.Date;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
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
      this.emailType = PlainEmailType;
    }



    public static int send(String recipient, String cc, String subject, String body){
      
      MouseMail mail = new MouseMail(recipient, cc, subject, body);
      mail.trySend();
      mail.save();
      return mail.id;
    }
    
    private void trySend(){
      //TODO check type, support html and multipart?
      try {
        Email email = new SimpleEmail();
        email.setHostName(SMTP_SERVER);
        email.setSmtpPort(SMTP_PORT);
        email.setAuthenticator(new DefaultAuthenticator(SMTP_USER, SMTP_PW));
        if (SMTP_SSL_ENABLED){
          email.setSSL(true);
        }
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
        Log.Info("Sending mail " + subject + " to " + recipient + " + " + ccs);
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
