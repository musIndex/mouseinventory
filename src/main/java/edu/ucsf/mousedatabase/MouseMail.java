package edu.ucsf.mousedatabase;

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
  
  
    public static boolean sendPlainEmail(String recipient, String cc,  String subject, String body){
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
        if (cc != null && !cc.isEmpty()){
          email.addCc(cc);
        }
        email.send();
        return true;
      }
      catch(Exception e){
        Log.Error("Error sending email",e);
        return false;
      }
    }
}
