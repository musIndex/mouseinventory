package edu.ucsf.mousedatabase.objects;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;

public class SubmittedMouse {

  public static final String STATUS_IN_HOLDING = "need more info";
  public static final String STATUS_REJECTED = "rejected";
  public static final String STATUS_NEW = "new";
  public static final String STATUS_ACCEPTED = "accepted";
  
  
  public static final String SubmissionFormSource = "Submission form";
//  public static final String SubmissionSourceKey = "Submisison Source";
//  public static final String ManualSubmission = "Manual Submission";
//  public static final String PurchaseImport = "Data Import";
//  public static final String OtherInstitutionImport = "Other Institution Import";

  int submissionID;
  int mouseRecordID;
  public String getIs_rat() {
    return is_rat;
  }

  public void setIs_rat(String is_rat) {
    this.is_rat = is_rat;
  }

  String is_rat;
  private Properties properties;

  private java.util.Date submissionDate;

  private String status;
  private boolean entered;

  private String adminComment;

  private String firstName;
  private String lastName;
  private String email;
  private String telephoneNumber;
  private String department;
  
  private String submissionSource;

  private String mouseName;
  private String officialMouseName;
  private String mouseType;
  private String holderName;
  private String otherHolderName;
  private String holderFacility;
  private String otherHolderFacility;
  private String isPublished;
  private String cryoLiveStatus;


  //Mutant Allele fields
  private String MAModificationType;
  private String MAMgiGeneID;
  private String MAMgiGeneIDValidationString;
  private String MAMgiGeneIDValid;

  //Transgenic fields
  private String TGExpressedSequence;
  private String transgenicType;
  private String TGRegulatoryElement;
  private String TGReporter;
  private String TGKnockedInGene; //deprecated
  private String TGKnockedInGeneValidationString; //deprecated
  private String TGKnockedInGeneValid; //deprecated
  private String TGMouseGene;
  private String TGMouseGeneValidationString;
  private String TGMouseGeneValid;
  private String TGOther;

  //Inbred Strain fields

  private String ISSupplier;
  private String ISSupplierCatalogNumber;
  private String ISSupplierCatalogUrl;


  //Common fields
  private String backgroundStrain;
  private String mtaRequired;
  private String comment;
  private String rawMGIComment;
  private String mouseMGIID;
  private String mouseMGIIDValidationString;
  private String mouseMGIIDValid;
  private String officialSymbol;
  private String PMID;
  private String PMIDValidationString;
  private String PMIDValid;
  private String gensatFounderLine;
  private String producedInLabOfHolder;

  //Rat specific fields
  private String ratName;
  private String RatType;
  private String rawRGDComment;
  
  //other fieds
  private String recordPreviewLink;

  public boolean hasOtherHolderName() { return holderName != null && holderName.equalsIgnoreCase("Other(specify)");};
  public boolean hasOtherFacilityName() { return holderFacility != null && holderFacility.equalsIgnoreCase("Other(specify)");};
  public boolean isMA(){ return mouseType!= null && mouseType.equalsIgnoreCase("Mutant Allele");}
  public boolean isTG(){ return mouseType!= null && (mouseType.equalsIgnoreCase("Transgene") || (mouseType.equalsIgnoreCase("Transgenic")));}
  public boolean isIS(){return mouseType!= null && mouseType.equalsIgnoreCase("Inbred Strain");}
  public boolean isKnockIn() { 
    Boolean val = transgenicType != null && (transgenicType.equalsIgnoreCase("Knock-in")|| (transgenicType.equalsIgnoreCase("endonuclease-mediated")));
    System.out.println(val+"transgenic type");
    return val;
  }
  public boolean isRandomInsertion() { return transgenicType != null && transgenicType.equalsIgnoreCase("Random insertion");}
  public boolean isPublished() { return isPublished != null && isPublished.equalsIgnoreCase("Yes"); }

  public boolean isCryoOnly() { return cryoLiveStatus != null && cryoLiveStatus.equalsIgnoreCase("Cryo only");};

  public boolean hasType() { return mouseType != null; }
  
  public boolean isManualFormSubmission() { return submissionSource.equals(SubmissionFormSource); }

  public void parseProperties(String propString)
  {
    if(propString == null)
    {
      return;
    }
    Pattern ptn = Pattern.compile("([^=\t]+)=([^\\t]+)?");
    Matcher match = ptn.matcher(propString);

    Properties props = new Properties();
        //StringTokenizer t = new StringTokenizer(propString, "\t");
        while (match.find())
        {
            //StringTokenizer t2 = new StringTokenizer(t.nextToken(), "=");
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

        this.properties = props;

      for(Object property : props.keySet())
      {
        String propName = (String)property;
        if(propName.equalsIgnoreCase("MouseType"))
        {
          setMouseType(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("RatType")) {
          setMouseType(props.getProperty(propName));
        }
        else if(propName.equalsIgnoreCase("isPublished"))
        {
          setIsPublished(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("mouseName"))
        {
          setMouseName(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("ratName"))
        {
          setMouseName(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("officialMouseName"))
        {
          setOfficialMouseName(props.getProperty(propName));
        }
        else if(propName.equalsIgnoreCase("holder"))
        {
          setHolderName(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("facility"))
        {
          setHolderFacility(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("otherHolder"))
        {
          setOtherHolderName(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("otherFacility"))
        {
          setOtherHolderFacility(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("modificationType"))
        {
          setMAModificationType(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("geneOfMutantAlleleMouse"))
        {
          setMAMgiGeneID(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("ExpressedSequence"))
        {
          setTGExpressedSequence(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("TransgenicType"))
        {
          setTransgenicType(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("regulatoryElement"))
        {
          setTGRegulatoryElement(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("reporter"))
        {
          setTGReporter(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("knockedInGene"))
        {
          setTGKnockedInGene(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("mouse gene"))
        {
          setTGMouseGene(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("other"))
        {
          setTGOther(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("gensatFounderLine"))
        {
          setGensatFounderLine(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("supplierForInbredStrain"))
        {
          setISSupplier(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("supplierForInbredStrainCatalogNumber"))
        {
          setISSupplierCatalogNumber(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("supplierForInbredStrainCatalogUrl"))
        {
          setISSupplierCatalogUrl(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("strain"))
        {
          setBackgroundStrain(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("mta"))
        {
          setMtaRequired(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("comment"))
        {
          setComment(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("rawMGIComment"))
        {
          setRawMGIComment(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("rawRGDComment"))
        {
          setRawRGDComment(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("repository"))
        {
          setMouseMGIID(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("source"))
        {
          setOfficialSymbol(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("pmid"))
        {
          setPMID(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("producedInLabOfHolder"))
        {
          setProducedInLabOfHolder(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("geneValid"))
        {
          setMAMgiGeneIDValid(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("geneValidationString"))
        {
          setMAMgiGeneIDValidationString(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("targetGeneValid"))
        {
          setTGMouseGeneValid(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("targetGeneValidationString"))
        {
          setTGMouseGeneValidationString(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("cryopreserved"))
        {
          setCryoLiveStatus(props.getProperty(propName));
        }
        else if (propName.equalsIgnoreCase("is_rat")){
          setIs_rat(props.getProperty(propName));
        }


      }




  }

  public ArrayList<MouseHolder> getHolders()
  {
    String holderAndFacilityIds = "";
    ArrayList<MouseHolder> mouseHolders = new ArrayList<MouseHolder>();
    if (properties == null || (holderAndFacilityIds = properties.getProperty("HolderFacilityList")) == null)
    {
      if (getHolderName() != null && !getHolderName().equals("unassigned"))
      {
        MouseHolder mouseHolder = new MouseHolder();
        Holder holder = DBConnect.findHolder(getHolderName());
        Facility facility = DBConnect.findFacility(getHolderFacility());
        if (holder != null) {
          mouseHolder.setHolderID(holder.getHolderID());
          mouseHolder.setFirstname(holder.getFirstname());
          mouseHolder.setLastname(holder.getLastname());
          mouseHolder.setEmail(holder.getEmail());
          mouseHolder.setAlternateEmail(holder.getAlternateEmail());
          mouseHolder.setDept(holder.getDept());
          
        }
        else if (hasOtherHolderName()) {
          mouseHolder.setHolderID(-1);
          mouseHolder.setFirstname(getOtherHolderName());
          mouseHolder.setLastname("");
        }
        if (facility != null) {
          mouseHolder.setFacilityID(facility.getFacilityID());
          mouseHolder.setFacilityName(facility.getFacilityName());
        }
        else if (hasOtherFacilityName()) {
          mouseHolder.setFacilityID(-1);
          mouseHolder.setFacilityName(getOtherHolderFacility());
        }
        mouseHolders.add(mouseHolder);
        return mouseHolders;
      }
    }
    else
    {
    
      int i = 0;
      for (String holderAndFacilityId : holderAndFacilityIds.split(",")) {
        String[] tokens = holderAndFacilityId.split("-");
        int holderId = Integer.parseInt(tokens[0]);
        int facilityId = Integer.parseInt(tokens[1]);
        MouseHolder mouseHolder = new MouseHolder();
  
        Holder holder = DBConnect.getHolder(holderId);
        Facility facility = DBConnect.getFacility(facilityId);
        mouseHolder.setHolderID(holderId);
        if (holder == null || holder.getFirstname().isEmpty())
        {
          String unrecognizedHolderName = properties.getProperty("Recipient PI Name-" + i);
          mouseHolder.setFirstname(unrecognizedHolderName);
          mouseHolder.setLastname("");
        }
        else
        {
          mouseHolder.setFirstname(holder.getFirstname());
          mouseHolder.setLastname(holder.getLastname());
          mouseHolder.setEmail(holder.getEmail());
          mouseHolder.setAlternateEmail(holder.getAlternateEmail());
          mouseHolder.setDept(holder.getDept());
        }
        
        mouseHolder.setSubmitterIndex(i);
        if (submissionSource.contains("PDU")) {
          mouseHolder.setSubmitterName(properties.getProperty("Purchaser-" + i));
          mouseHolder.setSubmitterEmail(properties.getProperty("Purchaser email-" + i));
        }
        else if (submissionSource.contains("IDU")) {
          mouseHolder.setSubmitterName(properties.getProperty("Recipient-" + i));
          mouseHolder.setSubmitterEmail(properties.getProperty("Recipient Emailame-" + i));
        }
  
  
        mouseHolder.setFacilityID(facilityId);
        if (facility == null || facility.getFacilityName().isEmpty())
        {
          String unrecognizedHolderFacilityName = properties.getProperty("Recipient Facility-" + i);
          mouseHolder.setFacilityName(unrecognizedHolderFacilityName);
        }
        else
        {
          mouseHolder.setFacilityName(facility.getFacilityName());
        }
  
        mouseHolders.add(mouseHolder);
        i++;
      }
    }

    return mouseHolders;
  }

  public MouseRecord toMouseRecord()
  {
    if (isTG()){
      //for legacy submissions that are set to 'transgenic' instead of 'transgene'
      mouseType = "Transgene";
    }
    MouseRecord r = new MouseRecord();
    r.setMouseName(mouseName);
    r.setOfficialMouseName(officialMouseName);
    r.setMouseType(mouseType);
    r.setGeneID(MAMgiGeneID);
    r.setTargetGeneID(TGMouseGene);

    if(MAModificationType != null && !MAModificationType.equalsIgnoreCase("Select One"))
      r.setModificationType(MAModificationType);

    r.setRegulatoryElement(TGRegulatoryElement);

    if(transgenicType != null && !transgenicType.equalsIgnoreCase("Select One"))
      r.setTransgenicType(transgenicType);

    if(isTG() && transgenicType == null)
    {
      r.setTransgenicType("Random Insertion");
    }

    if(TGExpressedSequence != null && !TGExpressedSequence.equalsIgnoreCase("Select One"))
      r.setExpressedSequence(TGExpressedSequence);
    r.setReporter(TGReporter);
    r.setOtherComment(TGOther);

    if(isMA() || isTG())
    {
      r.setSource(officialSymbol);
    }
    else if (isIS())
    {
      String sourceString = getISSupplier();
      if (getISSupplierCatalogNumber() != null)
      {
        sourceString += ", " + getISSupplierCatalogNumber();
      }
      if (!getISSupplier().startsWith("JAX"))
      {
        sourceString += "||" + getISSupplierCatalogUrl();
      }

      r.setSource(sourceString);
    }


    String trimmedComment = comment;
    if (trimmedComment != null && !trimmedComment.isEmpty())
    {
      int rawPropertiesIndex = trimmedComment.indexOf("Raw properties returned from MGI");
      if (rawPropertiesIndex > 0)
      {
        trimmedComment = trimmedComment.substring(0,rawPropertiesIndex).trim();
      }
    }
    r.setGeneralComment(trimmedComment);

    if(producedInLabOfHolder != null && producedInLabOfHolder.equalsIgnoreCase("Yes"))
    {
      r.setGeneralComment(r.getGeneralComment() + "  Produced in laboratory of holder (" + holderName + ")");
    }

    ArrayList<String> pmids = new ArrayList<String>();
    pmids.add(PMID);
    r.setPubmedIDs(pmids);
    r.setRepositoryCatalogNumber(mouseMGIID);
    r.setRepositoryTypeID("5");

    r.setBackgroundStrain(backgroundStrain);
    r.setGensat(gensatFounderLine);
    r.setMtaRequired(mtaRequired);


    try
    {
      ArrayList<MouseHolder> mHolders = getHolders();
      if (mHolders == null)
      {
        mHolders = new ArrayList<MouseHolder>();
      }

      setCryoLiveStatus(cryoLiveStatus);
      r.setHolders(mHolders);
    }
    catch(Exception e)
    {
      //use blank holders if anything breaks
      Holder holder = new Holder();
      Facility facility = new Facility();
      MouseHolder mHolder = new MouseHolder(holder,facility,false);
      ArrayList<MouseHolder> mHolders = new ArrayList<MouseHolder>();
      mHolders.add(mHolder);
      r.setHolders(mHolders);
    }


    r.setRat(is_rat);
    System.out.println(r.isRat());
    return r;
  }

  public MouseRecord toRatRecord()
  {
    if (isTG()){
      //for legacy submissions that are set to 'transgenic' instead of 'transgene'
      mouseType = "Transgene";
    }
    MouseRecord r = new MouseRecord();
    r.setMouseName(mouseName);
    r.setOfficialMouseName(officialMouseName);
    r.setMouseType(mouseType);

    r.setGeneID(MAMgiGeneID);
    r.setTargetGeneID(TGMouseGene);

    if(MAModificationType != null && !MAModificationType.equalsIgnoreCase("Select One"))
      r.setModificationType(MAModificationType);

    r.setRegulatoryElement(TGRegulatoryElement);

    if(transgenicType != null && !transgenicType.equalsIgnoreCase("Select One"))
      r.setTransgenicType(transgenicType);

    if(isTG() && transgenicType == null)
    {
      r.setTransgenicType("Random Insertion");
    }

    if(TGExpressedSequence != null && !TGExpressedSequence.equalsIgnoreCase("Select One"))
      r.setExpressedSequence(TGExpressedSequence);
    r.setReporter(TGReporter);
    r.setOtherComment(TGOther);

    if(isMA() || isTG())
    {
      r.setSource(officialSymbol);
    }
    else if (isIS())
    {
      String sourceString = getISSupplier();
      if (getISSupplierCatalogNumber() != null)
      {
        sourceString += ", " + getISSupplierCatalogNumber();
      }
      if (!getISSupplier().startsWith("JAX"))
      {
        sourceString += "||" + getISSupplierCatalogUrl();
      }

      r.setSource(sourceString);
    }


    String trimmedComment = comment;
    if (trimmedComment != null && !trimmedComment.isEmpty())
    {
      int rawPropertiesIndex = trimmedComment.indexOf("Raw properties returned from MGI");
      if (rawPropertiesIndex > 0)
      {
        trimmedComment = trimmedComment.substring(0,rawPropertiesIndex).trim();
      }
    }
    r.setGeneralComment(trimmedComment);

    if(producedInLabOfHolder != null && producedInLabOfHolder.equalsIgnoreCase("Yes"))
    {
      r.setGeneralComment(r.getGeneralComment() + "  Produced in laboratory of holder (" + holderName + ")");
    }

    ArrayList<String> pmids = new ArrayList<String>();
    pmids.add(PMID);
    r.setPubmedIDs(pmids);
    r.setRepositoryCatalogNumber(mouseMGIID);
    r.setRepositoryTypeID("5");

    r.setBackgroundStrain(backgroundStrain);
    r.setGensat(gensatFounderLine);
    r.setMtaRequired(mtaRequired);


    try
    {
      ArrayList<MouseHolder> mHolders = getHolders();
      if (mHolders == null)
      {
        mHolders = new ArrayList<MouseHolder>();
      }

      setCryoLiveStatus(cryoLiveStatus);
      r.setHolders(mHolders);
    }
    catch(Exception e)
    {
      //use blank holders if anything breaks
      Holder holder = new Holder();
      Facility facility = new Facility();
      MouseHolder mHolder = new MouseHolder(holder,facility,false);
      ArrayList<MouseHolder> mHolders = new ArrayList<MouseHolder>();
      mHolders.add(mHolder);
      r.setHolders(mHolders);
    }



    return r;
  }

  public String getFullMouseTypeTitle(){
    String mouseTypeTitle = "Unknown Mouse Type";
    if(isMA())
    {
      mouseTypeTitle = "Mutant Allele";
    }
    else if (isTG())
    {
      mouseTypeTitle = "Transgene";
      if (isKnockIn())
      {
        mouseTypeTitle += " Knock-in";
      }
      else if (isRandomInsertion())
      {
        mouseTypeTitle += " Random Insertion";
      }
    }
    else if (isIS())
    {
      mouseTypeTitle = "Inbred Strain";
    }
    if(!isIS())
    {
      if(isPublished())
      {
        mouseTypeTitle += " (Published)";
      }
      else
      {
        mouseTypeTitle += " (Unpublished)";
      }
    }
    return mouseTypeTitle;
  }
  public int getSubmissionID() {
    return submissionID;
  }
  public void setSubmissionID(int submissionID) {
    this.submissionID = submissionID;
  }
  public int getMouseRecordID() {
    return mouseRecordID;
  }
  public void setMouseRecordID(int mouseRecordID) {
    this.mouseRecordID = mouseRecordID;
  }
  public java.util.Date getSubmissionDate() {
    return submissionDate;
  }
  public void setSubmissionDate(java.util.Date submissionDate) {
    this.submissionDate = submissionDate;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public boolean isEntered() {
    return entered;
  }
  public void setEntered(boolean entered) {
    this.entered = entered;
  }
  public String getAdminComment() {
    return adminComment;
  }
  public void setAdminComment(String adminComment) {
    this.adminComment = adminComment;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getTelephoneNumber() {
    return telephoneNumber;
  }
  public void setTelephoneNumber(String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }
  public String getDepartment() {
    return department;
  }
  public void setDepartment(String department) {
    this.department = department;
  }
  
  public String getSubmissionSource() {
    return submissionSource;
  }
  public void setSubmissionSource(String submissionSource) {
    this.submissionSource = submissionSource;
  }
  public String getMouseName() {
    return mouseName;
  }
  public void setMouseName(String mouseName) {
    this.mouseName = mouseName;
  }
  public String getOfficialMouseName() {
    return officialMouseName;
  }
  public void setOfficialMouseName(String officialMouseName) {
    this.officialMouseName = officialMouseName;
  }
  public String getMouseType() {
    return mouseType;
  }
  public void setMouseType(String mouseType) {
    this.mouseType = mouseType;
  }
  public String getHolderName() {
    return holderName;
  }
  public void setHolderName(String holderName) {
    this.holderName = holderName;
  }
  public String getOtherHolderName() {
    return otherHolderName;
  }
  public void setOtherHolderName(String otherHolderName) {
    this.otherHolderName = otherHolderName;
  }
  public String getHolderFacility() {
    return holderFacility;
  }
  public void setHolderFacility(String holderFacility) {
    this.holderFacility = holderFacility;
  }
  public String getOtherHolderFacility() {
    return otherHolderFacility;
  }
  public void setOtherHolderFacility(String otherHolderFacility) {
    this.otherHolderFacility = otherHolderFacility;
  }
  public String getIsPublished() {
    return isPublished;
  }
  public void setIsPublished(String isPublished) {
    this.isPublished = isPublished;
  }
  public String getMAModificationType() {
    return MAModificationType;
  }
  public void setMAModificationType(String modificationType) {
    MAModificationType = modificationType;
  }
  public String getMAMgiGeneID() {
    return MAMgiGeneID;
  }
  public void setMAMgiGeneID(String mgiGeneID) {
    MAMgiGeneID = mgiGeneID;
  }
  public String getMAMgiGeneIDValidationString() {
    return MAMgiGeneIDValidationString;
  }
  public void setMAMgiGeneIDValidationString(String mgiGeneIDValidationString) {
    MAMgiGeneIDValidationString = mgiGeneIDValidationString;
  }
  public String getMAMgiGeneIDValid() {
    return MAMgiGeneIDValid;
  }
  public void setMAMgiGeneIDValid(String mgiGeneIDValid) {
    MAMgiGeneIDValid = mgiGeneIDValid;
  }
  public String getTGExpressedSequence() {
    return TGExpressedSequence;
  }
  public void setTGExpressedSequence(String expressedSequence) {
    TGExpressedSequence = expressedSequence;
  }
  public String getTransgenicType() {
    return transgenicType;
  }
  public void setTransgenicType(String transgenicType) {
    this.transgenicType = transgenicType;
  }
  public String getTGRegulatoryElement() {
    return TGRegulatoryElement;
  }
  public void setTGRegulatoryElement(String regulatoryElement) {
    TGRegulatoryElement = regulatoryElement;
  }
  public String getTGReporter() {
    return TGReporter;
  }
  public void setTGReporter(String reporter) {
    TGReporter = reporter;
  }
  public String getTGKnockedInGene() {
    return TGKnockedInGene;
  }
  public void setTGKnockedInGene(String knockedInGene) {
    TGKnockedInGene = knockedInGene;
  }
  public String getTGKnockedInGeneValidationString() {
    return TGKnockedInGeneValidationString;
  }
  public void setTGKnockedInGeneValidationString(
      String knockedInGeneValidationString) {
    TGKnockedInGeneValidationString = knockedInGeneValidationString;
  }
  public String getTGKnockedInGeneValid() {
    return TGKnockedInGeneValid;
  }
  public void setTGKnockedInGeneValid(String knockedInGeneValid) {
    TGKnockedInGeneValid = knockedInGeneValid;
  }
  public String getTGMouseGene() {
    return TGMouseGene;
  }
  public void setTGMouseGene(String mouseGene) {
    TGMouseGene = mouseGene;
  }
  public String getTGMouseGeneValidationString() {
    return TGMouseGeneValidationString;
  }
  public void setTGMouseGeneValidationString(String mouseGeneValidationString) {
    TGMouseGeneValidationString = mouseGeneValidationString;
  }
  public String getTGMouseGeneValid() {
    return TGMouseGeneValid;
  }
  public void setTGMouseGeneValid(String mouseGeneValid) {
    TGMouseGeneValid = mouseGeneValid;
  }
  public String getTGOther() {
    return TGOther;
  }
  public void setTGOther(String other) {
    TGOther = other;
  }
  public String getISSupplier() {
    return ISSupplier;
  }
  public void setISSupplier(String supplier) {
    ISSupplier = supplier;
  }
  public String getISSupplierCatalogNumber() {
    return ISSupplierCatalogNumber;
  }
  public void setISSupplierCatalogNumber(String supplierCatalogNumber) {
    ISSupplierCatalogNumber = supplierCatalogNumber;
  }
  public String getBackgroundStrain() {
    return backgroundStrain;
  }
  public void setBackgroundStrain(String backgroundStrain) {
    this.backgroundStrain = backgroundStrain;
  }
  public String getMtaRequired() {
    return mtaRequired;
  }
  public void setMtaRequired(String mtaRequired) {
    this.mtaRequired = mtaRequired;
  }
  public String getComment() {
    return comment;
  }
  public String getCommentForDisplay() {
    if (comment == null)
    {
      return null;
    }
    String fixed = comment.replaceAll("<", "&lt;");
    fixed = fixed.replaceAll(">", "&gt;");
    fixed = fixed.replaceAll("\r\n", "<br>");
    fixed = fixed.replaceAll("\\*([^*]+)\\*","<b>$1</b>");
    return fixed;
  }
  public void setComment(String comment) {
    this.comment = comment;
  }
  public String getRawMGIComment() {
    return rawMGIComment;
  }
  public void setRawMGIComment(String rawMGIComment) {
    this.rawMGIComment = rawMGIComment;
  }
  public String getMouseMGIID() {
    return mouseMGIID;
  }
  public void setMouseMGIID(String mouseMGIID) {
    this.mouseMGIID = mouseMGIID;
  }
  public String getMouseMGIIDValidationString() {
    return mouseMGIIDValidationString;
  }
  public void setMouseMGIIDValidationString(String mouseMGIIDValidationString) {
    this.mouseMGIIDValidationString = mouseMGIIDValidationString;
  }
  public String getMouseMGIIDValid() {
    return mouseMGIIDValid;
  }
  public void setMouseMGIIDValid(String mouseMGIIDValid) {
    this.mouseMGIIDValid = mouseMGIIDValid;
  }
  public String getOfficialSymbol() {
    return officialSymbol;
  }
  public void setOfficialSymbol(String officialSymbol) {
    this.officialSymbol = officialSymbol;
  }
  public String getPMID() {
    return PMID;
  }
  public void setPMID(String pmid) {
    PMID = pmid;
  }
  public String getPMIDValidationString() {
    return PMIDValidationString;
  }
  public void setPMIDValidationString(String validationString) {
    PMIDValidationString = validationString;
  }
  public String getPMIDValid() {
    return PMIDValid;
  }
  public void setPMIDValid(String valid) {
    PMIDValid = valid;
  }
  public String getGensatFounderLine() {
    return gensatFounderLine;
  }
  public void setGensatFounderLine(String gensatFounderLine) {
    this.gensatFounderLine = gensatFounderLine;
  }
  public String getProducedInLabOfHolder() {
    return producedInLabOfHolder;
  }
  public void setProducedInLabOfHolder(String producedInLabOfHolder) {
    this.producedInLabOfHolder = producedInLabOfHolder;
  }

  public String getRatName() {
    return ratName;
  }

  public void setRatName(String ratName) {
    this.ratName = ratName;
  }

  public String getRatType() {
    return RatType;
  }

  public void setRatType(String ratType) {
    RatType = ratType;
  }

  public String getRawRGDComment() {
    return rawRGDComment;
  }

  public void setRawRGDComment(String rawRGDComment) {
    this.rawRGDComment = rawRGDComment;
  }

  public String getCryoLiveStatus() {
    return cryoLiveStatus;
  }
  public void setCryoLiveStatus(String cryoLiveStatus) {
    this.cryoLiveStatus = cryoLiveStatus;
  }

  public String getISSupplierCatalogUrl() {
    return ISSupplierCatalogUrl;
  }
  public void setISSupplierCatalogUrl(String supplierCatalogUrl) {
    ISSupplierCatalogUrl = supplierCatalogUrl;
  }
  public Properties getProperties() {
    return properties;
  }
  
  public String getRecordPreviewLink() {
    return DBConnect.loadSetting("general_site_hostname").value + HTMLGeneration.siteRoot + "incomplete.jsp?com=rec&obj=" + this.mouseRecordID;
  }

  public void prepareForSerialization(){
    if (this.isInHolding()) {
      this.recordPreviewLink = getRecordPreviewLink();
    }
  }
  
  public boolean isNew(){
    return status != null && status.equals(STATUS_NEW);
  }
  public boolean isInHolding(){
    return status != null && status.equals(STATUS_IN_HOLDING);
  }
  public boolean isAccepted(){
    return status != null && status.equals(STATUS_ACCEPTED);
  }
  public boolean isRejected(){
    return status != null && status.equals(STATUS_REJECTED);
  }

}
