package edu.ucsf.mousedatabase.objects;

public interface IHolder {

  public abstract String getFullname();

  public abstract String getFirstname();

  public abstract void setFirstname(String firstname);

  public abstract String getLastname();

  public abstract void setLastname(String lastname);

  public abstract int getHolderID();

  public abstract void setHolderID(int holderID);

  public abstract String getEmail();

  public abstract void setEmail(String email);

  public abstract String getAlternateEmail();

  public abstract void setAlternateEmail(String alternateEmail);

  public abstract String getAlternateName();

  public abstract void setAlternateName(String alternateName);

  public abstract String getDept();

  public abstract void setDept(String dept);

  public abstract String getTel();

  public abstract void setTel(String tel);

  public abstract int getVisibleMouseCount();

  public abstract void setVisibleMouseCount(int mouseCount);

  public abstract int getCovertMouseCount();
  
  public abstract void setCovertMouseCount(int mouseCount);
  
  public abstract String getDateValidated();

  public abstract void setDateValidated(String dateValidated);

  public abstract String getValidationComment();

  public abstract void setValidationComment(String validationComment);
  
  public abstract String getPrimaryMouseLocation();
  
  public abstract void setPrimaryMouseLocation(String mouseLocation);
  
  public abstract boolean isDeadbeat();
  
  public abstract void setDeadbeat(boolean isDeadbeat);
  
  public abstract String getValidationStatus();
  
  public abstract void setValidationStatus(String status);

}
