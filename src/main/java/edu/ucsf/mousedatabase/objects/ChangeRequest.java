package edu.ucsf.mousedatabase.objects;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeRequest {
  private int requestID;
  private int mouseID;
  private String firstname;
  private String lastname;
  private String email;
  private String status;
  private String userComment;
  private String adminComment;
  private String requestDate;
  private String lastAdminDate;
  private String properties;
  private String mouseName;
  public String getMouseName() {
    return mouseName;
  }
  public void setMouseName(String mouseName) {
    this.mouseName = mouseName;
  }
  public int getRequestID() {
    return requestID;
  }
  public void setRequestID(int requestID) {
    this.requestID = requestID;
  }
  public int getMouseID() {
    return mouseID;
  }
  public void setMouseID(int mouseID) {
    this.mouseID = mouseID;
  }
  public String getFirstname() {
    return firstname;
  }
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }
  public String getLastname() {
    return lastname;
  }
  public void setLastname(String lastname) {
    this.lastname = lastname;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public String getUserComment() {
    return userComment;
  }
  public void setUserComment(String userComment) {
    this.userComment = userComment;
  }
  public String getAdminComment() {
    return adminComment;
  }
  public void setAdminComment(String adminComment) {
    this.adminComment = adminComment;
  }
  public String getRequestDate() {
    return requestDate;
  }
  public void setRequestDate(String requestDate) {
    this.requestDate = requestDate;
  }
  public String getLastAdminDate() {
    return lastAdminDate;
  }
  public void setLastAdminDate(String lastAdminDate) {
    this.lastAdminDate = lastAdminDate;
  }
  public String getProperties() {
    return properties;
  }
  public void setProperties(String properties) {
    this.properties = properties;
  }
  public void setProperties(Properties props)
  {
    StringBuilder sb = new StringBuilder();
    for (Object key : props.keySet())
    {
      sb.append(key);
      if (props.get(key) != null)
      {
        sb.append("=");
        sb.append(props.get(key));
      }
      sb.append('\t');
    }
    this.properties = sb.toString();
  }
  
  public Properties Properties()
  {
    if (getProperties() == null)
    {
      return null;
    }
    Properties props = new Properties();
    Pattern ptn = Pattern.compile("([^=\t]+)=([^\\t]+)?");
    Matcher match = ptn.matcher(getProperties());
    
        while (match.find()) 
        {
            String prop = match.group(1);
            String val = null;
            if (match.groupCount() > 1) {
                val = match.group(2);
            }
            if (val != null && val.length() > 0) 
            {
                props.setProperty(prop, val);
            }
        }
    return props;
  }
}
