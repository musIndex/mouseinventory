package edu.ucsf.mousedatabase.objects;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeRequest {
  
  public enum Action {
    UNDEFINED, //0
    ADD_HOLDER, //1
    REMOVE_HOLDER, //2
    MARK_ENDANGERED, //3
    OTHER; //4
  }
  
  public static Action[] ActionValues = Action.values();
  
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
  
  private String holderName;
  private String holderEmail;
  private int holderId;
  
  private String facilityName;
  private int facilityId;
  
  private String cryoLiveStatus;
  private String geneticBackgroundInfo;
  
  private String requestSource;
  private Action actionRequested;
  
  
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
  
  public String getHolderName() {
    return holderName;
  }
  public void setHolderName(String holderName) {
    this.holderName = holderName;
  }
  public String getHolderEmail() {
    return holderEmail;
  }
  public void setHolderEmail(String holderEmail) {
    this.holderEmail = holderEmail;
  }
  public int getHolderId() {
    return holderId;
  }
  public void setHolderId(int holderId) {
    this.holderId = holderId;
  }
  public String getFacilityName() {
    return facilityName;
  }
  public void setFacilityName(String facilityName) {
    this.facilityName = facilityName;
  }
  public int getFacilityId() {
    return facilityId;
  }
  public void setFacilityId(int facilityId) {
    this.facilityId = facilityId;
  }
  public String getRequestSource() {
    return requestSource;
  }
  public void setRequestSource(String requestSource) {
    this.requestSource = requestSource;
  }
  public String getActionRequested() {
    return actionRequested.toString();
  }
  public Action actionRequested() {
    return actionRequested;
  }
  public void setActionRequested(Action actionRequested) {
    this.actionRequested = actionRequested;
  }
  public void setActionRequested(String actionRequested) {
    this.actionRequested = ActionValues[Integer.parseInt(actionRequested)];
  }
  
  public String getCryoLiveStatus() {
    return cryoLiveStatus;
  }
  public void setCryoLiveStatus(String cryoLiveStatus) {
    this.cryoLiveStatus = cryoLiveStatus;
  }
  public String getGeneticBackgroundInfo() {
    return geneticBackgroundInfo;
  }
  public void setGeneticBackgroundInfo(String geneticBackgroundInfo) {
    this.geneticBackgroundInfo = geneticBackgroundInfo;
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
  
  public void clearData() {
    this.adminComment = this.lastAdminDate = this.mouseName = this.properties = this.requestDate = this.status = this.userComment = null;
  }
}
