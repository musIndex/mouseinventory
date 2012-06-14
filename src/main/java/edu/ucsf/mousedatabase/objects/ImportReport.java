package edu.ucsf.mousedatabase.objects;

import java.util.Collection;
import java.util.Date;

import edu.ucsf.mousedatabase.dataimport.ImportHandler.ImportObjectType;

public class ImportReport
{
  private int importReportID;
  private ImportObjectType importType;
  private String name;
  private Date creationDate;
  private String reportText;
  private Collection<Integer> newObjectIds;
  private Collection<String> newObjects;
  public int getImportReportID() {
    return importReportID;
  }
  public void setImportReportID(int importReportID) {
    this.importReportID = importReportID;
  }
  public ImportObjectType getImportType() {
    return importType;
  }
  public void setImportType(ImportObjectType importType) {
    this.importType = importType;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Date getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }
  public String getReportText() {
    return reportText;
  }
  public void setReportText(String reportText) {
    this.reportText = reportText;
  }
  public Collection<Integer> getNewObjectIds() {
    return newObjectIds;
  }
  public void setNewObjectIds(Collection<Integer> newObjectIds) {
    this.newObjectIds = newObjectIds;
  }
  public Collection<String> getNewObjects() {
    return newObjects;
  }
  public void setNewObjects(Collection<String> newObjects) {
    this.newObjects = newObjects;
  }

}
