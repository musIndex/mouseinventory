package edu.ucsf.mousedatabase.objects;

public class MouseHolder implements IHolder {

  private Holder holder;
  private Facility facility;
  private String cryoLiveStatus;
  private boolean newlyAdded;
  
  private String submitterName;
  private String submitterEmail;
  private int submitterIndex;

  private boolean covert;

  public MouseHolder()
  {
    this.holder = new Holder();
    this.facility = new Facility();
  }

  public MouseHolder(Holder h, Facility f, boolean covert)
  {
    this.holder = h;
    this.facility = f;
    this.covert = covert;

    //these two cases happen when people specify 'other' for the holder and facility name in a submission
    if (h == null)
    {
      holder = new Holder();
    }
    if (f == null)
    {
      facility = new Facility();
    }
  }

  public String getFullname()
  {
    return holder.getFirstname() + " " + holder.getLastname();
  }

  public String getFullTitle()
  {
    return holder.getLastname() + ", " + holder.getFirstname() + " - " + holder.getDept();
  }


  public String getFirstname() {
    return holder.getFirstname();
  }
  public void setFirstname(String firstname) {
    this.holder.setFirstname(firstname);
  }
  public String getLastname() {
    return holder.getLastname();
  }
  public void setLastname(String lastname) {
    this.holder.setLastname(lastname);
  }
  public int getHolderID() {
    return holder.getHolderID();
  }
  public void setHolderID(int holderID) {
    this.holder.setHolderID(holderID);
  }
  public String getEmail() {
    return holder.getEmail();
  }
  public void setEmail(String email) {
    this.holder.setEmail(email);
  }
  public String getAlternateEmail() {
    return holder.getAlternateEmail();
  }
  public void setAlternateEmail(String email) {
    this.holder.setAlternateEmail(email);
  }
  public String getAlternateName() {
    return holder.getAlternateName();
  }
  public void setAlternateName(String name) {
    this.holder.setAlternateName(name);
  }
  public String getDept() {
    return holder.getDept();
  }
  public void setDept(String dept) {
    this.holder.setDept(dept);
  }
  public String getTel() {
    return holder.getTel();
  }
  public void setTel(String tel) {
    this.holder.setTel(tel);
  }
  public String getFacilityName() {
    return facility.getFacilityName();
  }
  public void setFacilityName(String facility) {
    this.facility.setFacilityName(facility);
  }
  public int getFacilityID() {
    return facility.getFacilityID();
  }
  public void setFacilityID(int facilityID) {
    this.facility.setFacilityID(facilityID);
  }

  public boolean isCovert() {
    return covert;
  }

  public void setCovert(boolean covert) {
    this.covert = covert;
  }

  public String getCryoLiveStatus() {
    return cryoLiveStatus != null ? cryoLiveStatus : "Live only";
  }

  public void setCryoLiveStatus(String cryoLiveStatus) {
    this.cryoLiveStatus = cryoLiveStatus;
  }

  public boolean isNewlyAdded() {
    return newlyAdded;
  }

  public void setNewlyAdded(boolean newlyAdded) {
    this.newlyAdded = newlyAdded;
  }

  public int getVisibleMouseCount() {
    return holder.getVisibleMouseCount();
  }

  public void setVisibleMouseCount(int mouseCount) {
    holder.setVisibleMouseCount(mouseCount);
  }
  
  public int getCovertMouseCount() {
    return holder.getCovertMouseCount();
  }

  public void setCovertMouseCount(int mouseCount) {
    holder.setCovertMouseCount(mouseCount);
  }

  public String getDateValidated() {
    return holder.getDateValidated();
  }

  public void setDateValidated(String dateValidated) {
    holder.setDateValidated(dateValidated);
  }

  public String getValidationComment() {
    return holder.getValidationComment();
  }

  public void setValidationComment(String validationComment) {
    holder.setValidationComment(validationComment);
  }

  public String getSubmitterName() {
    return submitterName;
  }

  public void setSubmitterName(String submitterName) {
    this.submitterName = submitterName;
  }

  public String getSubmitterEmail() {
    return submitterEmail;
  }

  public void setSubmitterEmail(String submitterEmail) {
    this.submitterEmail = submitterEmail;
  }

  public int getSubmitterIndex() {
    return submitterIndex;
  }

  public void setSubmitterIndex(int submitterIndex) {
    this.submitterIndex = submitterIndex;
  }

  
  public void setPrimaryMouseLocation(String location){
    this.holder.setPrimaryMouseLocation(location);
  }
  
  public String getPrimaryMouseLocation(){
    return this.holder.getPrimaryMouseLocation();
  }
  
  public void setDeadbeat(boolean isDeadbeat){
    this.holder.setDeadbeat(isDeadbeat);
  }
  
  public boolean isDeadbeat(){
    return this.holder.isDeadbeat();
  }

  @Override
  public String getValidationStatus() {
    return this.holder.getValidationStatus();
  }

  @Override
  public void setValidationStatus(String status) {
    this.holder.setValidationStatus(status);
  }
  
  
  
}
