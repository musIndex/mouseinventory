package edu.ucsf.mousedatabase.objects;

public class MGIResult {



  private String accessionID;
  private int type;
  private String symbol;
  private String name;
  private String authors;
  private String title;
  private String errorString;
  private boolean valid;
  private boolean isOGFPage;
  private boolean mgiOffline;
  private boolean mgiConnectionTimedout;
  private String alleleAccessionID;

  public boolean isMgiConnectionTimedout() {
    return mgiConnectionTimedout;
  }
  public void setMgiConnectionTimedout(boolean mgiConnectionTimedout) {
    this.mgiConnectionTimedout = mgiConnectionTimedout;
  }




  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }

  public String getSymbol() {
    return symbol;
  }
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }
  public boolean isValid()
  {
    return valid;
  }
  public void setValid(boolean exists) {
    this.valid = exists;
  }
  public String getAccessionID() {
    return accessionID;
  }


  public void setAccessionID(String accessionID) {
    this.accessionID = accessionID;
  }

  public String getErrorString() {
    return errorString;
  }

  public void setErrorString(String errorString) {
    this.errorString = errorString;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthors() {
    return authors;
  }

  public void setAuthors(String authors) {
    this.authors = authors;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  public boolean isOGFPage() {
    return isOGFPage;
  }
  public void setOGFPage(boolean isOGFPage) {
    this.isOGFPage = isOGFPage;
  }
  public String getAlleleAccessionID() {
    return alleleAccessionID;
  }
  public void setAlleleAccessionID(String alleleAccessionID) {
    this.alleleAccessionID = alleleAccessionID;
  }
  public boolean isMgiOffline() {
    return mgiOffline;
  }
  public void setMgiOffline(boolean mgiOffline) {
    this.mgiOffline = mgiOffline;
  }

}
