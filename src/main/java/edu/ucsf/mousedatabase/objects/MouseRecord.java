package edu.ucsf.mousedatabase.objects;
import java.io.File;

import java.util.*;
//These imports were not in original -EW
//import com.mysql.jdbc.Blob;
//import com.mariadb.jdbc.Blob;
//import java.sql.Blob;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;
import edu.ucsf.mousedatabase.Log;
import edu.ucsf.mousedatabase.beans.UserData;

public class MouseRecord {

  String mouseID;
  String mouseName;
  String officialMouseName;
  String mouseType;

  String geneID;
  String geneName;
  String geneSymbol;

  String targetGeneID;
  String targetGeneName;
  String targetGeneSymbol;

  String modificationType;
  String regulatoryElement;

  String expressedSequence;
  String otherComment;
  String reporter;
  String transgenicType;

  String mtaRequired;
  String generalComment;
  String backgroundStrain;

  String source;
  
  String officialSymbol;

  String repositoryCatalogNumber;
    String repositoryTypeID;

    String gensat;

    String cryopreserved;
    boolean endangered;

    ArrayList<String> pubmedIDs;
  ArrayList<MouseHolder> holders;

  String status;

  String submittedMouseID;
  
  String previewLink;
  
  String adminComment;
  
  String filename;
  
  //ArrayList<String> filenames;
  ArrayList<File> filenames;
  ArrayList<Integer> fileIDs;

  @Override
  public boolean equals(Object o) {
    if (o instanceof MouseRecord) {
      return ((MouseRecord)o).getMouseID().equals(this.getMouseID());
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return this.getMouseID().hashCode();
  }


  public String getOfficialMouseName() {
    return officialMouseName;
  }

  public void setOfficialMouseName(String officialMouseName) {
    this.officialMouseName = officialMouseName;
  }

  public String getSubmittedMouseID() {
    return submittedMouseID;
  }

  public void setSubmittedMouseID(String submittedMouseID) {
    this.submittedMouseID = submittedMouseID;
  }

  public void cleanHiddenFields()
  {
    //NOT FULLY IMPLEMENTED - using javascript to clear hidden fields on edit form for now.
    if (getModificationType() != null)
    {
      if (!getModificationType().equals("targeted knock-in"))
      {
        setTargetGeneID(null);
        setReporter(null);
        setOtherComment(null);
        setExpressedSequence(null);
      }
      else if (getModificationType().equals("targeted knock-in"))
      {
        if (getExpressedSequence() != null)
        {
          if (getExpressedSequence().equalsIgnoreCase("Mouse gene")
              || getExpressedSequence().equalsIgnoreCase("Mouse Gene (unmodified)"))
          {
            setReporter(null);
            setOtherComment(null);
          }
          if (getExpressedSequence().equalsIgnoreCase("Cre"))
          {
            setReporter(null);
            setOtherComment(null);
            setTargetGeneID(null);
          }
          if (getExpressedSequence().equalsIgnoreCase("Reporter"))
          {
            setTargetGeneID(null);
            setOtherComment(null);
          }
          if (getExpressedSequence().equalsIgnoreCase("Other")
              || getExpressedSequence().equalsIgnoreCase("Modified mouse gene or Other"))
          {
            setTargetGeneID(null);
            setReporter(null);
          }

        }
      }


    }
  }
  public boolean isMA(){ return mouseType!= null && mouseType.equalsIgnoreCase("Mutant Allele");}
  public boolean isTG(){ return mouseType!= null && mouseType.equalsIgnoreCase("Transgene");}
  public boolean isIS(){return mouseType!= null && mouseType.equalsIgnoreCase("Inbred Strain");}
  public boolean hasType() { return mouseType != null; }
  public boolean isCryoOnly()
  {
    if (holders == null || holders.size() <= 0)
    {
      return false;
    }
    for(MouseHolder holder : holders)
    {
      if (holder.getCryoLiveStatus().contains("Live"))
      {
        return false;
      }
    }
    return true;
  }

  public boolean isHidden()
  {
    if (getHolders() == null || getHolders().size() <= 0)
    {
      return true;
    }
    if (getStatus() != null && !getStatus().equals("live"))
    {
      return true;
    }

    boolean covertOnly = true;
    for (MouseHolder holder : getHolders())
    {
      covertOnly &= holder.isCovert();
    }
    return covertOnly;
  }

  public String getMouseID() {
    return mouseID;
  }
  public void setMouseID(String mouseID) {
    this.mouseID = mouseID;
  }
  public String getMouseName() {
    return mouseName;
  }
  public void setMouseName(String mouseName) {
    this.mouseName = mouseName;
  }
  public String getMouseType() {
    return mouseType;
  }
  public void setMouseType(String mouseType) {
    this.mouseType = mouseType;
  }
  public String getGeneID() {
    return geneID;
  }
  public void setGeneID(String geneID) {
    this.geneID = geneID;
  }
  public String getGeneName() {
    return geneName;
  }
  public void setGeneName(String geneName) {
    this.geneName = geneName;
  }
  public String getGeneSymbol() {
    return geneSymbol;
  }
  public void setGeneSymbol(String geneSymbol) {
    this.geneSymbol = geneSymbol;
  }
  public String getTargetGeneID() {
    return targetGeneID;
  }
  public void setTargetGeneID(String targetGeneID) {
    this.targetGeneID = targetGeneID;
  }
  public String getTargetGeneName() {
    return targetGeneName;
  }
  public void setTargetGeneName(String targetGeneName) {
    this.targetGeneName = targetGeneName;
  }
  public String getTargetGeneSymbol() {
    return targetGeneSymbol;
  }
  public void setTargetGeneSymbol(String targetGeneSymbol) {
    this.targetGeneSymbol = targetGeneSymbol;
  }
  public String getModificationType() {
    return modificationType;
  }
  public void setModificationType(String modificationType) {
    this.modificationType = modificationType;
  }
  public String getRegulatoryElement() {
    return regulatoryElement;
  }
  public void setRegulatoryElement(String regulatoryElement) {
    this.regulatoryElement = regulatoryElement;
  }
  public String getExpressedSequence() {
    return expressedSequence;
  }
  public void setExpressedSequence(String expressedSequence) {
    this.expressedSequence = expressedSequence;
  }
  public String getOtherComment() {
    return otherComment;
  }
  public void setOtherComment(String otherComment) {
    this.otherComment = otherComment;
  }
  public String getReporter() {
    return reporter;
  }
  public void setReporter(String reporter) {
    this.reporter = reporter;
  }
  public String getTransgenicType() {
    return transgenicType;
  }
  public void setTransgenicType(String transgenicType) {
    this.transgenicType = transgenicType;
  }
  public String getMtaRequired() {
    return mtaRequired;
  }
  public void setMtaRequired(String mtaRequired) {
    this.mtaRequired = mtaRequired;
  }
  public String getGeneralComment() {
    return generalComment;
  }
  public void setGeneralComment(String generalComment) {
    this.generalComment = generalComment;
  }
  public String getBackgroundStrain() {
    return backgroundStrain;
  }
  public void setBackgroundStrain(String backgroundStrain) {
    this.backgroundStrain = backgroundStrain;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public String getRepositoryCatalogNumber() {
    return repositoryCatalogNumber;
  }
  public void setRepositoryCatalogNumber(String repositoryCatalogNumber) {
    this.repositoryCatalogNumber = repositoryCatalogNumber;
  }
  public String getRepositoryTypeID() {
    return repositoryTypeID;
  }
  public void setRepositoryTypeID(String repositoryTypeID) {
    this.repositoryTypeID = repositoryTypeID;
  }
  public String getGensat() {
    return gensat;
  }
  public void setGensat(String gensat) {
    this.gensat = gensat;
  }
  public ArrayList<String> getPubmedIDs() {
    return pubmedIDs;
  }
  public void setPubmedIDs(ArrayList<String> pubmedIDs) {
    this.pubmedIDs = pubmedIDs;
  }
  public ArrayList<MouseHolder> getHolders() {
    return holders;
  }
  public void setHolders(ArrayList<MouseHolder> holders) {
    this.holders = holders;
  }
  public String getCryopreserved() {
    return cryopreserved;
  }
  public void setCryopreserved(String cryopreserved) {
    this.cryopreserved = cryopreserved;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public boolean isEndangered() {
    return endangered;
  }
  public void setEndangered(boolean endangered) {
    this.endangered = endangered;
  }
  
  public String getOfficialSymbol() {
    return officialSymbol;
  }

  public void setOfficialSymbol(String officialSymbol) {
    this.officialSymbol = officialSymbol;
  }
  
  public String getAdminComment() {
    return adminComment;
  }

  public void setAdminComment(String adminComment) {
    this.adminComment = adminComment;
  }
  
  //does not get called
  public String getFilename() {
    //Log.Info("called get filename");
	  return filename;
  }
  
  public ArrayList<File> getFilenames(){
    //Log.Info("called get filenames");
	  return filenames;
  }
  
  public void setFilenames(ArrayList<File> Filenames) {
	  this.filenames = Filenames;
  }
  
  public void addFiles(ArrayList<File> newFiles) {
	  this.filenames.addAll(newFiles);
  }
  
  public void setFileIDs(ArrayList<Integer> FileIDs) {
	  this.fileIDs = FileIDs;
  }
  
  public ArrayList<Integer> getFileIDs(){
	  return fileIDs;
  }
  
  public void setFilename(String filename) {
	  this.filename = filename;
  }

  public String getPreviewLink() {
    return DBConnect.loadSetting("general_site_hostname").value + HTMLGeneration.siteRoot + "incomplete.jsp?com=rec&obj=" + this.mouseID;
  }

  public void prepareForSerialization(){
    if (this.status != null && this.status.equalsIgnoreCase("incomplete")) {
      this.previewLink = getPreviewLink();
    }
    if (this.isMA() || this.isTG()){
      this.setOfficialSymbol(this.getSource());
    }
  }

  public static Properties GetPropertiesString(UserData submitterData, MouseRecord newMouse)
  {
    Properties props = new Properties();

        //temporary properties conversion.

        //general
        props.setProperty("First", submitterData.getFirstName());
        props.setProperty("Last",submitterData.getLastName());
        props.setProperty("Dept",submitterData.getDepartment());
        props.setProperty("Email",submitterData.getEmail());
        props.setProperty("Tel",submitterData.getTelephoneNumber());

        //all types
        props.setProperty("MouseType", newMouse.getMouseType());
    if(newMouse.isMA() || newMouse.isTG())
    {
          //props.setProperty("isPublished", newMouse.getIsPublished());
    }
        props.setProperty("mouseName",emptyIfNull(newMouse.getMouseName()));

        ArrayList<MouseHolder> holders = newMouse.getHolders();
        for (MouseHolder mouseHolder : holders)
    {
           props.setProperty("holder",emptyIfNull(mouseHolder.getFullname()));
             props.setProperty("facility",emptyIfNull(mouseHolder.getFacilityName()));
             break;
    }


        props.setProperty("comment",emptyIfNull(newMouse.getGeneralComment()));

        //mutant allele
        if(newMouse.isMA())
        {
          props.setProperty("modificationType",emptyIfNull(newMouse.getModificationType()));
          props.setProperty("geneOfMutantAlleleMouse",emptyIfNull(newMouse.getGeneID()));
        }
        //transgenic
        if(newMouse.isTG())
        {

          props.setProperty("TransgenicType", emptyIfNull(newMouse.getTransgenicType()));
          props.setProperty("regulatoryElement",emptyIfNull(newMouse.getRegulatoryElement()));

          props.setProperty("knockedInGene",emptyIfNull(newMouse.getTargetGeneID()));

          props.setProperty("gensatFounderLine",emptyIfNull(newMouse.getGensat()));
        }

        //inbred strain
        if(newMouse.isIS())
        {
          props.setProperty("supplierForInbredStrain",emptyIfNull(newMouse.getSource()));
          //props.setProperty("supplierForInbredStrainCatalogNumber",emptyIfNull(newMouse.getISSupplierCatalogNumber()));
          //props.setProperty("supplierForInbredStrainCatalogUrl",emptyIfNull(newMouse.getISSupplierCatalogUrl()));
        }
        props.setProperty("cryopreserved",emptyIfNull(newMouse.getCryopreserved()));
        //common stuff
        if(newMouse.isMA() || newMouse.isTG())
        {
          props.setProperty("ExpressedSequence",emptyIfNull(newMouse.getExpressedSequence()));
          props.setProperty("reporter",emptyIfNull(newMouse.getReporter()));
          props.setProperty("mouse gene",emptyIfNull(newMouse.getGeneID()));
          props.setProperty("other",emptyIfNull(newMouse.getOtherComment()));

          props.setProperty("strain",emptyIfNull(newMouse.getBackgroundStrain()));
          props.setProperty("mta",emptyIfNull(newMouse.getMtaRequired()));
          //props.setProperty("producedInLabOfHolder",emptyIfNull(newMouse.getProducedInLabOfHolder()));
          //if(newMouse.isPublished())
          //{
            props.setProperty("repository",emptyIfNull(newMouse.getRepositoryCatalogNumber()));
            props.setProperty("source",emptyIfNull(newMouse.getSource()));
            for (String pmid : newMouse.getPubmedIDs())
        {
              props.setProperty("pmid",pmid);
              break;
        }

          //}
          //props.setProperty("geneValid",emptyIfNull(newMouse.getMAMgiGeneIDValid()));
         // props.setProperty("geneValidationString",emptyIfNull(newMouse.getMAMgiGeneIDValidationString()));
          //props.setProperty("targetGeneValid",emptyIfNull(newMouse.getTGMouseGeneValid()));
          //props.setProperty("targetGeneValidationString",emptyIfNull(newMouse.getTGMouseGeneValidationString()));
        }
        return props;
  }
  private static String emptyIfNull(String input)
  {
    if (input != null)
    {
      return input;
    }
    else
    {
      return "";
    }
  }
}
