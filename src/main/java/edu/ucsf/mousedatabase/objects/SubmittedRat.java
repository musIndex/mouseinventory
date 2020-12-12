package edu.ucsf.mousedatabase.objects;

import edu.ucsf.mousedatabase.DBConnect;
import edu.ucsf.mousedatabase.HTMLGeneration;

import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubmittedRat {

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
    int ratRecordID;

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

    private String ratName;
    private String officialRatName;
    private String ratType;
    private String holderName;
    private String otherHolderName;
    private String holderFacility;
    private String otherHolderFacility;
    private String isPublished;
    private String cryoLiveStatus;


    //Mutant Allele fields
    private String MAModificationType;
    private String MARgdGeneID;
    private String MARgdGeneIDValidationString;
    private String MARgdGeneIDValid;

    //Transgenic fields
    private String TGExpressedSequence;
    private String transgenicType;
    private String TGRegulatoryElement;
    private String TGReporter;
    private String TGKnockedInGene; //deprecated
    private String TGKnockedInGeneValidationString; //deprecated
    private String TGKnockedInGeneValid; //deprecated
    private String TGRatGene;
    private String TGRatGeneValidationString;
    private String TGRatGeneValid;
    private String TGOther;

    //Inbred Strain fields

    private String ISSupplier;
    private String ISSupplierCatalogNumber;
    private String ISSupplierCatalogUrl;


    //Common fields
    private String backgroundStrain;
    private String mtaRequired;
    private String comment;
    private String rawRGDComment;
    private String ratRGDID;
    private String ratRGDIDValidationString;
    private String ratRGDIDValid;
    private String officialSymbol;
    private String PMID;
    private String PMIDValidationString;
    private String PMIDValid;
    private String gensatFounderLine;
    private String producedInLabOfHolder;

    //other fieds
    private String recordPreviewLink;

    public boolean hasOtherHolderName() { return holderName != null && holderName.equalsIgnoreCase("Other(specify)");};
    public boolean hasOtherFacilityName() { return holderFacility != null && holderFacility.equalsIgnoreCase("Other(specify)");};
    public boolean isMA(){ return ratType!= null && ratType.equalsIgnoreCase("Mutant Allele");}
    public boolean isTG(){ return ratType!= null && (ratType.equalsIgnoreCase("Transgene") || (ratType.equalsIgnoreCase("Transgenic")));}
    public boolean isIS(){return ratType!= null && ratType.equalsIgnoreCase("Inbred Strain");}
    public boolean isKnockIn() {
        Boolean val = transgenicType != null && (transgenicType.equalsIgnoreCase("Knock-in")|| (transgenicType.equalsIgnoreCase("endonuclease-mediated")));
        System.out.println(val+"transgenic type");
        return val;
    }
    public boolean isRandomInsertion() { return transgenicType != null && transgenicType.equalsIgnoreCase("Random insertion");}
    public boolean isPublished() { return isPublished != null && isPublished.equalsIgnoreCase("Yes"); }

    public boolean isCryoOnly() { return cryoLiveStatus != null && cryoLiveStatus.equalsIgnoreCase("Cryo only");};

    public boolean hasType() { return ratType != null; }

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
            if(propName.equalsIgnoreCase("RatType"))
            {
                setRatType(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("isPublished"))
            {
                setIsPublished(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("ratName"))
            {
                setRatName(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("officialRatName"))
            {
                setOfficialRatName(props.getProperty(propName));
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
            }else if(propName.equalsIgnoreCase("geneOfMutantAlleleRat"))
            {
                setMARgdGeneID(props.getProperty(propName));
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
            }else if(propName.equalsIgnoreCase("rat gene"))
            {
                setTGRatGene(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("other"))
            {
                setTGOther(props.getProperty(propName));
            }
//            else if(propName.equalsIgnoreCase("gensatFounderLine"))
//            {
//                setGensatFounderLine(props.getProperty(propName));
//            }
            else if(propName.equalsIgnoreCase("supplierForInbredStrain"))
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
            }else if(propName.equalsIgnoreCase("rawRGDComment"))
            {
                setRawRGDComment(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("repository"))
            {
                setRatRGDID(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("source"))
            {
                setOfficialSymbol(props.getProperty(propName));
//            }else if(propName.equalsIgnoreCase("pmid"))
//            {
//                setPMID(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("producedInLabOfHolder"))
            {
                setProducedInLabOfHolder(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("geneValid"))
            {
                setMARgdGeneIDValid(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("geneValidationString"))
            {
                setMARgdGeneIDValidationString(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("targetGeneValid"))
            {
                setTGRatGeneValid(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("targetGeneValidationString"))
            {
                setTGRatGeneValidationString(props.getProperty(propName));
            }else if(propName.equalsIgnoreCase("cryopreserved"))
            {
                setCryoLiveStatus(props.getProperty(propName));
            }


        }




    }

//    public ArrayList<RatHolder> getHolders()
//    {
//        String holderAndFacilityIds = "";
//        ArrayList<RatHolder> ratHolders = new ArrayList<RatHolder>();
//        if (properties == null || (holderAndFacilityIds = properties.getProperty("HolderFacilityList")) == null)
//        {
//            if (getHolderName() != null && !getHolderName().equals("unassigned"))
//            {
//                RatHolder ratHolder = new RatHolder();
//                Holder holder = DBConnect.findHolder(getHolderName());
//                Facility facility = DBConnect.findFacility(getHolderFacility());
//                if (holder != null) {
//                    ratHolder.setHolderID(holder.getHolderID());
//                    ratHolder.setFirstname(holder.getFirstname());
//                    ratHolder.setLastname(holder.getLastname());
//                    ratHolder.setEmail(holder.getEmail());
//                    ratHolder.setAlternateEmail(holder.getAlternateEmail());
//                    ratHolder.setDept(holder.getDept());
//
//                }
//                else if (hasOtherHolderName()) {
//                    ratHolder.setHolderID(-1);
//                    ratHolder.setFirstname(getOtherHolderName());
//                    ratHolder.setLastname("");
//                }
//                if (facility != null) {
//                    ratHolder.setFacilityID(facility.getFacilityID());
//                    ratHolder.setFacilityName(facility.getFacilityName());
//                }
//                else if (hasOtherFacilityName()) {
//                    ratHolder.setFacilityID(-1);
//                    ratHolder.setFacilityName(getOtherHolderFacility());
//                }
//                ratHolders.add(ratHolder);
//                return ratHolders;
//            }
//        }
//        else
//        {
//
//            int i = 0;
//            for (String holderAndFacilityId : holderAndFacilityIds.split(",")) {
//                String[] tokens = holderAndFacilityId.split("-");
//                int holderId = Integer.parseInt(tokens[0]);
//                int facilityId = Integer.parseInt(tokens[1]);
//                MouseHolder ratHolder = new MouseHolder();
//
//                Holder holder = DBConnect.getHolder(holderId);
//                Facility facility = DBConnect.getFacility(facilityId);
//                ratHolder.setHolderID(holderId);
//                if (holder == null || holder.getFirstname().isEmpty())
//                {
//                    String unrecognizedHolderName = properties.getProperty("Recipient PI Name-" + i);
//                    ratHolder.setFirstname(unrecognizedHolderName);
//                    ratHolder.setLastname("");
//                }
//                else
//                {
//                    ratHolder.setFirstname(holder.getFirstname());
//                    ratHolder.setLastname(holder.getLastname());
//                    ratHolder.setEmail(holder.getEmail());
//                    ratHolder.setAlternateEmail(holder.getAlternateEmail());
//                    ratHolder.setDept(holder.getDept());
//                }
//
//                ratHolder.setSubmitterIndex(i);
//                if (submissionSource.contains("PDU")) {
//                    ratHolder.setSubmitterName(properties.getProperty("Purchaser-" + i));
//                    ratHolder.setSubmitterEmail(properties.getProperty("Purchaser email-" + i));
//                }
//                else if (submissionSource.contains("IDU")) {
//                    ratHolder.setSubmitterName(properties.getProperty("Recipient-" + i));
//                    ratHolder.setSubmitterEmail(properties.getProperty("Recipient Emailame-" + i));
//                }
//
//
//                ratHolder.setFacilityID(facilityId);
//                if (facility == null || facility.getFacilityName().isEmpty())
//                {
//                    String unrecognizedHolderFacilityName = properties.getProperty("Recipient Facility-" + i);
//                    ratHolder.setFacilityName(unrecognizedHolderFacilityName);
//                }
//                else
//                {
//                    ratHolder.setFacilityName(facility.getFacilityName());
//                }
//
//                ratHolders.add(ratHolder);
//                i++;
//            }
//        }
//
//        return ratHolders;
//    }
//
//    public MouseRecord toMouseRecord()
//    {
//        if (isTG()){
//            //for legacy submissions that are set to 'transgenic' instead of 'transgene'
//            ratType = "Transgene";
//        }
//        MouseRecord r = new MouseRecord();
//        r.setMouseName(ratName);
//        r.setOfficialMouseName(officialMouseName);
//        r.setMouseType(ratType);
//
//        r.setGeneID(MARgdGeneID);
//        r.setTargetGeneID(TGMouseGene);
//
//        if(MAModificationType != null && !MAModificationType.equalsIgnoreCase("Select One"))
//            r.setModificationType(MAModificationType);
//
//        r.setRegulatoryElement(TGRegulatoryElement);
//
//        if(transgenicType != null && !transgenicType.equalsIgnoreCase("Select One"))
//            r.setTransgenicType(transgenicType);
//
//        if(isTG() && transgenicType == null)
//        {
//            r.setTransgenicType("Random Insertion");
//        }
//
//        if(TGExpressedSequence != null && !TGExpressedSequence.equalsIgnoreCase("Select One"))
//            r.setExpressedSequence(TGExpressedSequence);
//        r.setReporter(TGReporter);
//        r.setOtherComment(TGOther);
//
//        if(isMA() || isTG())
//        {
//            r.setSource(officialSymbol);
//        }
//        else if (isIS())
//        {
//            String sourceString = getISSupplier();
//            if (getISSupplierCatalogNumber() != null)
//            {
//                sourceString += ", " + getISSupplierCatalogNumber();
//            }
//            if (!getISSupplier().startsWith("JAX"))
//            {
//                sourceString += "||" + getISSupplierCatalogUrl();
//            }
//
//            r.setSource(sourceString);
//        }
//
//
//        String trimmedComment = comment;
//        if (trimmedComment != null && !trimmedComment.isEmpty())
//        {
//            int rawPropertiesIndex = trimmedComment.indexOf("Raw properties returned from RGD");
//            if (rawPropertiesIndex > 0)
//            {
//                trimmedComment = trimmedComment.substring(0,rawPropertiesIndex).trim();
//            }
//        }
//        r.setGeneralComment(trimmedComment);
//
//        if(producedInLabOfHolder != null && producedInLabOfHolder.equalsIgnoreCase("Yes"))
//        {
//            r.setGeneralComment(r.getGeneralComment() + "  Produced in laboratory of holder (" + holderName + ")");
//        }
//
//        ArrayList<String> pmids = new ArrayList<String>();
//        pmids.add(PMID);
//        r.setPubmedIDs(pmids);
//        r.setRepositoryCatalogNumber(ratRGDID);
//        r.setRepositoryTypeID("5");
//
//        r.setBackgroundStrain(backgroundStrain);
//        r.setGensat(gensatFounderLine);
//        r.setMtaRequired(mtaRequired);
//
//
//        try
//        {
//            ArrayList<MouseHolder> mHolders = getHolders();
//            if (mHolders == null)
//            {
//                mHolders = new ArrayList<MouseHolder>();
//            }
//
//            setCryoLiveStatus(cryoLiveStatus);
//            r.setHolders(mHolders);
//        }
//        catch(Exception e)
//        {
//            //use blank holders if anything breaks
//            Holder holder = new Holder();
//            Facility facility = new Facility();
//            MouseHolder mHolder = new MouseHolder(holder,facility,false);
//            ArrayList<MouseHolder> mHolders = new ArrayList<MouseHolder>();
//            mHolders.add(mHolder);
//            r.setHolders(mHolders);
//        }
//
//
//
//        return r;
//    }
//
//    public MouseRecord toMouseRecord()
//    {
//        if (isTG()){
//            //for legacy submissions that are set to 'transgenic' instead of 'transgene'
//            ratType = "Transgene";
//        }
//        MouseRecord r = new MouseRecord();
//        r.setMouseName(ratName);
//        r.setOfficialMouseName(officialMouseName);
//        r.setMouseType(ratType);
//
//        r.setGeneID(MARgdGeneID);
//        r.setTargetGeneID(TGMouseGene);
//
//        if(MAModificationType != null && !MAModificationType.equalsIgnoreCase("Select One"))
//            r.setModificationType(MAModificationType);
//
//        r.setRegulatoryElement(TGRegulatoryElement);
//
//        if(transgenicType != null && !transgenicType.equalsIgnoreCase("Select One"))
//            r.setTransgenicType(transgenicType);
//
//        if(isTG() && transgenicType == null)
//        {
//            r.setTransgenicType("Random Insertion");
//        }
//
//        if(TGExpressedSequence != null && !TGExpressedSequence.equalsIgnoreCase("Select One"))
//            r.setExpressedSequence(TGExpressedSequence);
//        r.setReporter(TGReporter);
//        r.setOtherComment(TGOther);
//
//        if(isMA() || isTG())
//        {
//            r.setSource(officialSymbol);
//        }
//        else if (isIS())
//        {
//            String sourceString = getISSupplier();
//            if (getISSupplierCatalogNumber() != null)
//            {
//                sourceString += ", " + getISSupplierCatalogNumber();
//            }
//            if (!getISSupplier().startsWith("JAX"))
//            {
//                sourceString += "||" + getISSupplierCatalogUrl();
//            }
//
//            r.setSource(sourceString);
//        }
//
//
//        String trimmedComment = comment;
//        if (trimmedComment != null && !trimmedComment.isEmpty())
//        {
//            int rawPropertiesIndex = trimmedComment.indexOf("Raw properties returned from RGD");
//            if (rawPropertiesIndex > 0)
//            {
//                trimmedComment = trimmedComment.substring(0,rawPropertiesIndex).trim();
//            }
//        }
//        r.setGeneralComment(trimmedComment);
//
//        if(producedInLabOfHolder != null && producedInLabOfHolder.equalsIgnoreCase("Yes"))
//        {
//            r.setGeneralComment(r.getGeneralComment() + "  Produced in laboratory of holder (" + holderName + ")");
//        }
//
//        ArrayList<String> pmids = new ArrayList<String>();
//        pmids.add(PMID);
//        r.setPubmedIDs(pmids);
//        r.setRepositoryCatalogNumber(ratRGDID);
//        r.setRepositoryTypeID("5");
//
//        r.setBackgroundStrain(backgroundStrain);
//        r.setGensat(gensatFounderLine);
//        r.setMtaRequired(mtaRequired);
//
//
//        try
//        {
//            ArrayList<MouseHolder> mHolders = getHolders();
//            if (mHolders == null)
//            {
//                mHolders = new ArrayList<MouseHolder>();
//            }
//
//            setCryoLiveStatus(cryoLiveStatus);
//            r.setHolders(mHolders);
//        }
//        catch(Exception e)
//        {
//            //use blank holders if anything breaks
//            Holder holder = new Holder();
//            Facility facility = new Facility();
//            MouseHolder mHolder = new MouseHolder(holder,facility,false);
//            ArrayList<MouseHolder> mHolders = new ArrayList<MouseHolder>();
//            mHolders.add(mHolder);
//            r.setHolders(mHolders);
//        }
//
//
//
//        return r;
//    }

    public String getFullRatTypeTitle(){
        String ratTypeTitle = "Unknown Rat Type";
        if(isMA())
        {
            ratTypeTitle = "Mutant Allele";
        }
        else if (isTG())
        {
            ratTypeTitle = "Transgene";
            if (isKnockIn())
            {
                ratTypeTitle += " Knock-in";
            }
            else if (isRandomInsertion())
            {
                ratTypeTitle += " Random Insertion";
            }
        }
        else if (isIS())
        {
            ratTypeTitle = "Inbred Strain";
        }
        if(!isIS())
        {
            if(isPublished())
            {
                ratTypeTitle += " (Published)";
            }
            else
            {
                ratTypeTitle += " (Unpublished)";
            }
        }
        return ratTypeTitle;
    }
    public int getSubmissionID() {
        return submissionID;
    }
    public void setSubmissionID(int submissionID) {
        this.submissionID = submissionID;
    }
    public int getRatRecordID() {
        return ratRecordID;
    }
    public void setRatRecordID(int ratRecordID) {
        this.ratRecordID = ratRecordID;
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
    public String getRatName() {
        return ratName;
    }
    public void setRatName(String ratName) {
        this.ratName = ratName;
    }
    public String getOfficialRatName() {
        return officialRatName;
    }
    public void setOfficialRatName(String officialRatName) {
        this.officialRatName = officialRatName;
    }
    public String getRatType() {
        return ratType;
    }
    public void setRatType(String ratType) {
        this.ratType = ratType;
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
    public String getMARgdGeneID() {
        return MARgdGeneID;
    }
    public void setMARgdGeneID(String rgdGeneID) {
        MARgdGeneID = rgdGeneID;
    }
    public String getMARgdGeneIDValidationString() {
        return MARgdGeneIDValidationString;
    }
    public void setMARgdGeneIDValidationString(String rgdGeneIDValidationString) {
        MARgdGeneIDValidationString = rgdGeneIDValidationString;
    }
    public String getMARgdGeneIDValid() {
        return MARgdGeneIDValid;
    }
    public void setMARgdGeneIDValid(String rgdGeneIDValid) {
        MARgdGeneIDValid = rgdGeneIDValid;
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
    public String getTGRatGene() {
        return TGRatGene;
    }
    public void setTGRatGene(String ratGene) {
        TGRatGene = ratGene;
    }
    public String getTGRatGeneValidationString() {
        return TGRatGeneValidationString;
    }
    public void setTGRatGeneValidationString(String ratGeneValidationString) {
        TGRatGeneValidationString = ratGeneValidationString;
    }
    public String getTGRatGeneValid() {
        return TGRatGeneValid;
    }
    public void setTGRatGeneValid(String ratGeneValid) {
        TGRatGeneValid = ratGeneValid;
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
    public String getRawRGDComment() {
        return rawRGDComment;
    }
    public void setRawRGDComment(String rawRGDComment) {
        this.rawRGDComment = rawRGDComment;
    }
    public String getRatRGDID() {
        return ratRGDID;
    }
    public void setRatRGDID(String ratRGDID) {
        this.ratRGDID = ratRGDID;
    }
    public String getRatRGDIDValidationString() {
        return ratRGDIDValidationString;
    }
    public void setRatRGDIDValidationString(String ratRGDIDValidationString) {
        this.ratRGDIDValidationString = ratRGDIDValidationString;
    }
    public String getRatRGDIDValid() {
        return ratRGDIDValid;
    }
    public void setRatRGDIDValid(String ratRGDIDValid) {
        this.ratRGDIDValid = ratRGDIDValid;
    }
    public String getOfficialSymbol() {
        return officialSymbol;
    }
    public void setOfficialSymbol(String officialSymbol) {
        this.officialSymbol = officialSymbol;
    }
//    public String getPMID() {
//        return PMID;
//    }
//    public void setPMID(String pmid) {
//        PMID = pmid;
//    }
//    public String getPMIDValidationString() {
//        return PMIDValidationString;
//    }
//    public void setPMIDValidationString(String validationString) {
//        PMIDValidationString = validationString;
//    }
//    public String getPMIDValid() {
//        return PMIDValid;
//    }
//    public void setPMIDValid(String valid) {
//        PMIDValid = valid;
//    }
//    public String getGensatFounderLine() {
//        return gensatFounderLine;
//    }
//    public void setGensatFounderLine(String gensatFounderLine) {
//        this.gensatFounderLine = gensatFounderLine;
//    }
    public String getProducedInLabOfHolder() {
        return producedInLabOfHolder;
    }
    public void setProducedInLabOfHolder(String producedInLabOfHolder) {
        this.producedInLabOfHolder = producedInLabOfHolder;
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
        return DBConnect.loadSetting("general_site_hostname").value + HTMLGeneration.siteRoot + "incomplete.jsp?com=rec&obj=" + this.ratRecordID;
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
