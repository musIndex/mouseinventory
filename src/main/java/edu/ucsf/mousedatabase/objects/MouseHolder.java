package edu.ucsf.mousedatabase.objects;

public class MouseHolder {

  private Holder holder;
  private Facility facility;
  private String cryoLiveStatus;
  private boolean newlyAdded;

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

}
