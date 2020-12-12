package edu.ucsf.mousedatabase.objects;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLUtilities;
import edu.ucsf.mousedatabase.Log;

public class ChangeRequest {
  
  public enum Action {
    UNDEFINED(0,""), //0
    ADD_HOLDER(1,"Add holder:"), //1
    REMOVE_HOLDER(2,"Remove holder:"), //2
    MARK_ENDANGERED(3,"Mark as endangered."), //3
    OTHER(4,"Make other changes."), //4
    CHANGE_CRYO_LIVE_STATUS(5,"Change live/cryo status of holder:"), //5
    UPLOAD_FILE(6,"Upload File");//6
    
    public String label;
    public int value;
    
    private Action(int value, String label){
      this.value = value;
      this.label = label;
    }
    
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
  private String fileNames;
  private String deleteFileNames;
  
  private Properties props;
  
  private String holderName;
  private String holderFirstname;
  private String holderLastname;
  private String holderEmail;
  private int holderId;
  
  private String facilityName;
  private int facilityId;
  
  private String cryoLiveStatus;
  private String geneticBackgroundInfo;
  
  private String requestSource;
  private Action actionRequested;
  

  public String getNewFileNames(){
    return fileNames;
  }
  public void setNewFileNames(String fileNames){
    this.fileNames = fileNames;
  }
  public String getDeleteFileNames(){
    return deleteFileNames;
  }
  public void setDeleteFileNames(String deleteFileNames){
    this.deleteFileNames =  deleteFileNames;
  }
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
  //Is this what goes to admin view for change request?-EW
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
    if (actionRequested == null) {
      this.actionRequested = Action.UNDEFINED;
      return;
    }
    try {
      this.actionRequested = ActionValues[Integer.parseInt(actionRequested)];
    }
    catch(Exception e) {
      Log.Error("error parsing action " + actionRequested,e);
      this.actionRequested = Action.UNDEFINED;
    }
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
  public Properties getProps() {
    return props;
  }
  public void setProps(Properties props) {
    this.props = props;
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
    if (getProperties() == null || getProperties().isEmpty())
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
  //added fileName to be cleared -EW
  public void clearData() {
    this.adminComment = this.lastAdminDate = this.mouseName = this.properties = this.requestDate = this.status = this.userComment = this.fileNames = this.deleteFileNames = null;
  }
  
  public String validate() {
    ArrayList<String> errors = new ArrayList<String>();
    
    MouseRecord record = DBConnect.getMouseRecord(getMouseID()).get(0);
    
    if (!HTMLUtilities.validateEmail(getEmail())) {
      errors.add("Email address is invalid.");
    }
    if (firstname == null || firstname.isEmpty()) {
      errors.add("First name is required.");
    }
    if (lastname == null || lastname.isEmpty()) {
      errors.add("Last name is required.");
    }
    if (actionRequested == Action.UNDEFINED) {
      errors.add("Please specify the action to be performed.");
    }
    else if (actionRequested == Action.ADD_HOLDER || actionRequested == Action.REMOVE_HOLDER || actionRequested == Action.CHANGE_CRYO_LIVE_STATUS) {
      if (holderId == -1) {
        errors.add("Please select a holder.  If the desired holder is not listed, please select 'Other(specify)' and provide the name.");
      }
      else if (holderId == -2 && (holderName == null || holderName.isEmpty())) {
        errors.add("Please specify the holder name.");
      }
      else if (actionRequested == Action.REMOVE_HOLDER || actionRequested == Action.CHANGE_CRYO_LIVE_STATUS){
        boolean holderFound = false;
        for (MouseHolder holder : record.getHolders()) {
          if (holder.getHolderID() == holderId && holder.getFacilityID() == facilityId) {
            holderFound = true;
          }
        }
        if (!holderFound) {
          errors.add("That holder and facility cannot be " + (actionRequested == Action.REMOVE_HOLDER ? "removed" : "updated") 
              + " - it is not currently listed on the record");
        }
      }
      if (facilityId == -1) {
        errors.add("Please select a facility.  If the desired facility is not listed, please select 'Other(specify)' and provide the name.");
      }
      else if (facilityId == -2 && (facilityName == null || facilityName.isEmpty())) {
        errors.add("Please specify the facility name.");
      }
    }
    else if (actionRequested == Action.OTHER) {
      if (userComment == null || userComment.isEmpty()) {
        errors.add("Please describe the changes in the comment field.");
      }
    }
    /*
    else if (actionRequested == Action.UPLOAD_FILE) {
      if (fileNames == null) {
        errors.add("Please provide file name.");
      }
      */
    
    return StringUtils.join(errors,"|");
  }

  public String getHolderFirstname() {
    return holderFirstname;
  }
  public void setHolderFirstname(String holderFirstname) {
    this.holderFirstname = holderFirstname;
  }
  public String getHolderLastname() {
    return holderLastname;
  }
  public void setHolderLastname(String holderLastname) {
    this.holderLastname = holderLastname;
  }
  public void prepareForSerialization(){
    this.props = this.Properties();
  }
}

