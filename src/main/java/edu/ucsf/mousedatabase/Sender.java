/*


This currently is not secure enough to be implemented.
Will revisit after figuring out how to use the microsoft exchange
email server to verify with the ORA msu email.





package edu.ucsf.mousedatabase;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Sender {
    */
/*
    * The base idea for this code comes from https://stackoverflow.com/questions/3649014/send-email-using-java.
    * In that post, there was a comment by AndroidDev, on March 20, 2013 that showed how to use a gmail mailing
    * server to send out mail. While the code has been quite heavily edited since that post, I'd like to credit
    * where I got my ideas and base code from.
    *//*



    //username_password consists of a string formatted like username;password
    String username_password = DBConnect.loadSetting("email_access_details").value;
    //Break the string into an array to separate the user from the password
    String[] details = getUsername_password().split(";");

    //Assign username
    private String username = details[0];
    //Assign password
    private String password = details[1];
    //The account we send from never changes, it's always the same.
    private final String send_from = "mouseinventorymailer@gmail.com";

    */
/* Getter and setter methods
    * ----------------------------------------------------------------------------------
    *//*

    private String getUsername_password() {
        return username_password;
    }
    private String getUsername() {
        return username;
    }
    private String getPassword() {
        return password;
    }
    private String getSend_from() {
        return send_from;
    }
    */
/* Getter and setter methods
     * ----------------------------------------------------------------------------------
     *//*


    //This function sets up the authentication necessary for sending an email
    private Session Auth() {
        //Define properties of ports being used
        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        //Create a working session
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(getUsername(), getPassword());
                    }
                });
        //Return the session with the aforemention properties
        return session;
    }

    */
/*
    * The SendMail function is the main function used in this class.
    * It will take in any given parameters and compose them into an email.
    * The only necessity is that send_to is not null. (If it was, there would
    * be nobody to send the email to).
    *//*

    public void SendMail(String send_to, String subject, String body_text) {
        if (send_to != null) {
            try {
                //Create a new email object
                Message email = new MimeMessage(Auth());
                //Set who the email is from
                email.setFrom(new InternetAddress(getSend_from()));
                //Set recipients
                email.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(send_to));
                //Set subject
                email.setSubject(subject);
                //Set text
                email.setText(body_text);
                //Send the email!
                Transport.send(email);
                //System.out.println("Email sent");
            }
            //Catch exceptions
            catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}*/
