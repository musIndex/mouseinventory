package edu.ucsf.mousedatabase.objects;

public class Holder implements IHolder {


  private int holderID;
  private String firstname;
  private String lastname;
  private String email;
  private String alternateEmail;
  private String alternateName;
  private String dept;
  private String tel;
  private String dateValidated;
  private String validationComment;
  private String validationStatus;
  private String status;
  private String primaryMouseLocation;
  private boolean isDeadbeat;

  private int visibleMouseCount;
  private int covertMouseCount;


  public String getFullname()
  {
    return firstname + " " + lastname;
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

  public int getHolderID() {
    return holderID;
  }
  public void setHolderID(int holderID) {
    this.holderID = holderID;
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }

  public String getAlternateEmail() {
    return alternateEmail;
  }

  public void setAlternateEmail(String alternateEmail) {
    this.alternateEmail = alternateEmail;
  }

  public String getAlternateName() {
    return alternateName;
  }

  public void setAlternateName(String alternateName) {
    this.alternateName = alternateName;
  }

  public String getDept() {
    return dept;
  }
  public void setDept(String dept) {
    this.dept = dept;
  }
  public String getTel() {
    return tel;
  }
  public void setTel(String tel) {
    this.tel = tel;
  }


  public int getVisibleMouseCount()
  {
    return visibleMouseCount;
  }


  public void setVisibleMouseCount(int mouseCount)
  {
    this.visibleMouseCount = mouseCount;
  }

  public int getCovertMouseCount()
  {
    return covertMouseCount;
  }


  public void setCovertMouseCount(int mouseCount)
  {
    this.covertMouseCount = mouseCount;
  }
  
  
  public String getDateValidated() {
    return dateValidated;
  }


  public void setDateValidated(String dateValidated) {
    this.dateValidated = dateValidated;
  }


  public String getValidationComment() {
    return validationComment;
  }


  public void setValidationComment(String validationComment) {
    this.validationComment = validationComment;
  }


  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
  
  public boolean isActive(){
    return this.status.equals("active");
  }


  public String getPrimaryMouseLocation() {
    return primaryMouseLocation;
  }


  public void setPrimaryMouseLocation(String primaryMouseLocation) {
    this.primaryMouseLocation = primaryMouseLocation;
  }


  public boolean isDeadbeat() {
    return isDeadbeat;
  }


  public void setDeadbeat(boolean isDeadbeat) {
    this.isDeadbeat = isDeadbeat;
  }


  @Override
  public String getValidationStatus() {
    return this.validationStatus;
  }


  @Override
  public void setValidationStatus(String status) {
    this.validationStatus = status; 
  }
  
  

}
