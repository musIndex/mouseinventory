package edu.ucsf.mousedatabase.beans;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RatSubmission {
      /*********************/
  String ratName;
  String officialRatName;
  String ratType;
  String holderName;
  String otherHolderName;
  String holderFacility;
  String otherHolderFacility;
  String isPublished;
  public String getIs_rat() {
    return is_rat;
  }

  public void setIs_rat(String is_rat) {
    this.is_rat = is_rat;
  }

  String is_rat;

  //Mutant Allele fields
  String MAModificationType;
  String MARgdGeneID;
  String MARgdGeneIDValidationString;
  String MARgdGeneIDValid;

  //Transgenic fields
  String TGExpressedSequence;
  String transgenicType;
  String TGRegulatoryElement;
  String TGReporter;
  String TGKnockedInGene;
  String TGKnockedInGeneValidationString;
  String TGKnockedInGeneValid;
  String TGRatGene;
  String TGRatGeneValidationString;
  String TGRatGeneValid;
  String TGOther;

  //Inbred Strain fields

  String ISSupplier;
  String ISSupplierCatalogNumber;
  String ISSupplierCatalogUrl;

  //Common fields
  String backgroundStrain;
  String mtaRequired;
  String comment;
  String rawRGDComment;
  String ratRGDID;
  String ratRGDIDValidationString;
  String ratRGDIDValid;
  String officialSymbol;
  String PMID;
  String PMIDValidationString;
  String PMIDValid;
  String gensatFounderLine;
  String producedInLabOfHolder;
  String cryopreserved;

  //ERROR messages
  //************************************************
  String ratNameErr;
  String ratTypeErr;
  String ratDisciplinesErr;
  String holderNameErr;
  String holderFacilityErr;
  String isPublishedErr;
  String otherHolderNameErr;
  String otherFacilityErr;

  //Mutant Allele fields
  String MAModificationTypeErr;
  String MARgdGeneIDErr;

  //Transgenic fields
  String TGExpressedSequenceErr;
  String transgenicTypeErr;
  String TGRegulatoryElementErr;
  String TGReporterErr;
  String TGKnockedInGeneErr;
  String TGRatGeneErr;
  String TGOtherErr;

  //Inbred Strain fields
  String ISSupplierErr;

  //Common fields
  String backgroundStrainErr;
  String mtaRequiredErr;
  String commentErr;
  String ratRGDIDErr;
  String officialSymbolErr;
  String PMIDErr;
  String gensatFounderLineErr;

  String producedInLabOfHolderErr;
  String cryopreservedErr;

  public RatSubmission()
  {}

  public void clearRatData()
  {
    ratName = null;
    ratType = null;


    isPublished = null;

    //Mutant Allele fields
    MAModificationType = null;
    MARgdGeneID = null;
    MARgdGeneIDValidationString = null;
    MARgdGeneIDValid = null;

    //Transgenic fields
    TGExpressedSequence = null;
    transgenicType = null;
    TGRegulatoryElement = null;
    TGReporter = null;
    TGKnockedInGene = null;
    TGKnockedInGeneValidationString = null;
    TGKnockedInGeneValid = null;
    TGRatGene = null;
    TGRatGeneValidationString = null;
    TGRatGeneValid = null;
    TGOther = null;

    //Inbred Strain fields

    ISSupplier = null;

    //Common fields
    backgroundStrain = null;
    mtaRequired = null;
    comment = null;
    ratRGDID = null;
    ratRGDIDValidationString = null;
    ratRGDIDValid = null;
    officialSymbol = null;
    PMID = null;
    PMIDValidationString = null;
    PMIDValid = null;
    gensatFounderLine = null;
    producedInLabOfHolder = null;
    cryopreserved = null;
    rawRGDComment = null;


    //ERROR messages
    //************************************************
    ratNameErr = null;
    ratTypeErr = null;
    ratDisciplinesErr = null;
    holderNameErr = null;
    holderFacilityErr = null;
    isPublishedErr = null;
    otherHolderNameErr = null;
    otherFacilityErr = null;

    //Mutant Allele fields
    MAModificationTypeErr = null;
    MARgdGeneIDErr = null;

    //Transgenic fields
    TGExpressedSequenceErr = null;
    transgenicTypeErr = null;
    TGRegulatoryElementErr = null;
    TGReporterErr = null;
    TGKnockedInGeneErr = null;
    TGRatGeneErr = null;
    TGOtherErr = null;

    //Inbred Strain fields
    ISSupplierErr = null;
    ISSupplierCatalogNumber = null;

    //Common fields
    backgroundStrainErr = null;
    mtaRequiredErr = null;
    commentErr = null;
    ratRGDIDErr = null;
    officialSymbolErr = null;
    PMIDErr = null;
    gensatFounderLineErr = null;
    producedInLabOfHolderErr = null;
    cryopreservedErr = null;
  }

  public void clearHolderData()
  {
    holderName = null;
    otherHolderName = null;
    holderFacility = null;
    otherHolderFacility = null;
  }
  public void clearAll()
  {
    clearRatData();
    clearHolderData();
  }


  public boolean isMA(){ return ratType!= null && ratType.equalsIgnoreCase("Mutant Allele");}
  public boolean isTG(){ return ratType!= null && ratType.equalsIgnoreCase("Transgene");}
  public boolean isIS(){return ratType!= null && ratType.equalsIgnoreCase("Inbred Strain");}

  public boolean hasType() { return ratType != null; }

  public boolean isJAX() { return ISSupplierCatalogNumber != null && ISSupplierCatalogNumber.equalsIgnoreCase("jax"); }

  public boolean isGeneExprSeq() { return TGExpressedSequence != null && TGExpressedSequence.equalsIgnoreCase("Rat Gene (unmodified)");}
  public boolean isReporterExprSeq() { return TGExpressedSequence != null && TGExpressedSequence.equalsIgnoreCase("reporter");}
  public boolean isOtherExprSeq() { return TGExpressedSequence != null && TGExpressedSequence.equalsIgnoreCase("Modified rat gene or Other");}
  public boolean isKnockIn() {
    Boolean val = (MAModificationType != null && (MAModificationType.equalsIgnoreCase("endonuclease-mediated")^(MAModificationType.equalsIgnoreCase("targeted knock-in")))) ;
    System.out.println(val);
    return val;
  }//-EW not failing here, it turns to true
  public boolean isRandomInsertion() { return transgenicType != null && transgenicType.equalsIgnoreCase("Random insertion");}
  public boolean isPublished() { return isPublished != null && isPublished.equalsIgnoreCase("Yes"); }

  public boolean hasOtherHolderName() { return holderName != null && holderName.equalsIgnoreCase("Other(specify)");};
  public boolean hasOtherFacilityName() { return holderFacility != null && holderFacility.equalsIgnoreCase("Other(specify)");};

  public boolean hasValidMArgdGeneID() { return MARgdGeneIDValid != null && MARgdGeneIDValid.equalsIgnoreCase("true");};
  public boolean hasValidTGKnockedInGene() { return TGKnockedInGeneValid != null && TGKnockedInGeneValid.equalsIgnoreCase("true");};
  public boolean hasValidTGRatGene() { return TGRatGeneValid != null && TGRatGeneValid.equalsIgnoreCase("true");};
  public boolean hasValidRatRGDID() { return ratRGDIDValid != null && ratRGDIDValid.equalsIgnoreCase("true");};
  public boolean hasValidPMID() { return PMIDValid != null && PMIDValid.equalsIgnoreCase("true");};

  //public boolean isCryoOnly() { return cryopreserved != null && cryopreserved.equalsIgnoreCase("Cryo only");};
  public String liveCryoStatus() {return cryopreserved != null ?  cryopreserved : "Live only";};


  public boolean ValidateHolderInfo(){
    boolean valid = true;
    clearHolderErrors();
    clearRatDetailsErrors();
    if(holderName == null || holderName.equalsIgnoreCase("Choose one"))
    {
      valid = false;
      holderNameErr = "Please select a holder name";
    }
    if(holderName.equalsIgnoreCase("Other(specify)"))
    {
      if(isNullOrEmpty(otherHolderName))
      {
        valid = false;
        otherHolderNameErr = "Please specify the holder name";
      }
    }
    if(holderFacility == null || holderFacility.equalsIgnoreCase("Choose one"))
    {
      valid = false;
      holderFacilityErr = "Please select a facility";
    }
    if(holderFacility.equalsIgnoreCase("Other(specify)"))
    {
      if(isNullOrEmpty(otherHolderFacility))
      {
        valid = false;
        otherFacilityErr = "Please specify the facility name";
      }
    }
    return valid;
  }



  public boolean validateRatType(){
    boolean valid = true;
    clearRatTypeErrors();
    //TODO implement this, just in case javascript doesn't catch all cases

    return valid;
  }



  public boolean validateRatDetails(){
    boolean valid = true;
    clearRatDetailsErrors();

    if(isNullOrEmpty(ratName))
    {
      valid = false;
      ratNameErr = "Please enter rat name";
    }
    /* Temporarily disable rat discipline field validation
    if(isMA() || isTG())
    {

      if(ratDisciplines == null || ratDisciplines.length <= 0)
      {
        if(isNullOrEmpty(otherRatDiscipline))
        {
          valid = false;
          ratDisciplinesErr = "Please select a discipline";
        }
      }
    }
    */
    if(!isMA() && !isIS() && !isTG())
    {
      ratTypeErr = "Unknown error occurred.  Please go back to step 1 and try again.";
      clearAll();
      valid = false;
    }
//    //MUTANT ALLELE
//    if(isMA())
//    {
//      if(MAModificationType == null)
//      {
//        valid = false;
//        MAModificationTypeErr = "Please select modification type";
//      }
//      else if(MAModificationType.equalsIgnoreCase("targeted knock-in") || MAModificationType.equalsIgnoreCase("endonuclease-mediated"))//-EW
//      {
//
//        //COPIED FROM TRANSGENIC CATEGORY
//        if(isNullOrEmpty(TGExpressedSequence))
//        {
//          valid = false;
//          TGExpressedSequenceErr = "Please select the expressed sequence type";
//        }
//        else if (TGExpressedSequence.equalsIgnoreCase("Rat gene")
//                || TGExpressedSequence.equalsIgnoreCase("Rat Gene (unmodified)"))
//        {
//          if(isNullOrEmpty(TGRatGene))
//          {
//            valid = false;
//            TGRatGeneErr = "Please enter the RGD Gene ID number";
//          }
//          else if(!"null".equalsIgnoreCase(TGRatGene) && !isNumericString(TGRatGene))
//          {
//            valid = false;
//            TGRatGeneErr = "Please enter only numbers";
//          } else if(!hasValidTGRatGene())
//          {
//            valid = false;
//            TGRatGeneErr = "Invalid Gene";
//          }
//        }
//        else if (TGExpressedSequence.equalsIgnoreCase("Reporter"))
//        {
//          if(isNullOrEmpty(TGReporter))
//          {
//            valid = false;
//            TGReporterErr = "Please enter the Reporter";
//          }
//        }
//        else if (TGExpressedSequence.equalsIgnoreCase("Other")
//                || TGExpressedSequence.equalsIgnoreCase("Modified rat gene or Other"))
//        {
//          if(isNullOrEmpty(TGOther))
//          {
//            valid = false;
//            TGOtherErr = "Please enter a description of the expressed sequence";
//          }
//        }
//        else if (TGExpressedSequence.equalsIgnoreCase("Cre"))
//        {
//          //no validation rules for this case yet
//        }
//
//      }
//      if(isNullOrEmpty(mtaRequired))
//      {
//        //valid = false;
//        //mtaRequiredErr = "Please specifiy whether or not an MTA is required.  If unknown, choose 'Don't Know'";
//      }
//
//      if(isNullOrEmpty(MARgdGeneID))
//      {
//        valid = false;
//        MARgdGeneIDErr = "Please enter the RGD Gene ID number";
//      }
//      else if(!isNumericString(MARgdGeneID))
//      {
//        valid = false;
//        MARgdGeneIDErr = "Please enter only numbers";
//      }
//      else if(!hasValidMArgdGeneID())  //this pulls the valid result from the result of the client-side AJAX request to validate the RGD number
//      {
//        valid = false;
//        MARgdGeneIDErr = "Invalid gene ID";  //this will get overwritten by MARGDGeneIDValidationString in getMARgdGeneID()
//      }
//
//      //MUTANT ALLELE PUBLISHED
//      if(isPublished != null && isPublished())
//      {
//        valid &= validateRGDRatID("allele");
//        valid &= validateOfficialSymbol("allele");
//        valid &= validatePubmedID();
//      }
//      //MUTANT ALLELE UNPUBLISHED
//      else if (isPublished != null && !isPublished())
//      {
//        if(isNullOrEmpty(producedInLabOfHolder))
//        {
//          valid = false;
//          producedInLabOfHolderErr = "Please specify where the rat was produced.";
//        }
//
//        if(isNullOrEmpty(comment))
//        {
//          valid = false;
//          commentErr = "Please provide a description of the allele.";
//        }
//      }
//      else
//      {
//        isPublishedErr = "You must select whether or not this allele is published in step 2";
//        valid = false;
//      }
//
//      //validate comment
//      //validate background strain
//      valid &= validateMTA();
//
//    }
    //TRANSGENIC
    else if(isTG())
    {
      if(isNullOrEmpty(mtaRequired))
      {
        //valid = false;
        //mtaRequiredErr = "Please specifiy whether or not an MTA is required.  If uknown, choose 'Don't Know'";
      }

      if(isNullOrEmpty(TGExpressedSequence))
      {
        valid = false;
        TGExpressedSequenceErr = "Please select the expressed sequence type";
      }
      else if (TGExpressedSequence.equalsIgnoreCase("Rat gene")
              || TGExpressedSequence.equalsIgnoreCase("Rat Gene (unmodified)"))
      {
        if(isNullOrEmpty(TGRatGene))
        {
          valid = false;
          TGRatGeneErr = "Please enter the RGD Gene ID number";
        }
        else if(!"null".equalsIgnoreCase(TGRatGene) && !isNumericString(TGRatGene))
        {
          valid = false;
          TGRatGeneErr = "Please enter only numbers";
        } else if(!hasValidTGRatGene())
        {
          valid = false;
          TGRatGeneErr = "Invalid Gene";
        }
      }
      else if (TGExpressedSequence.equalsIgnoreCase("Reporter"))
      {
        if(isNullOrEmpty(TGReporter))
        {
          valid = false;
          TGReporterErr = "Please enter the Reporter";
        }
      }
      else if (TGExpressedSequence.equalsIgnoreCase("Other")
              || TGExpressedSequence.equalsIgnoreCase("Modified rat gene  or other"))
      {
        if(isNullOrEmpty(TGOther))
        {
          valid = false;
          TGOtherErr = "Please enter a description of the expressed sequence";
        }
      }
      else if (TGExpressedSequence.equalsIgnoreCase("Cre"))
      {
        //no validation rules for this case yet
      }
      if(isRandomInsertion())
      {


      }
      else
      {
        transgenicTypeErr = "Unrecognized transgenic type!";
        valid = false;
      }

      String typeString = isKnockIn() ? "transgene/knock-in" : "allele";

      //PUBLISHED TRANSGENIC
      if(isPublished != null && isPublished())
      {
        valid &= validateRGDRatID(typeString);
        valid &= validateOfficialSymbol(typeString);
        //valid &= validatePubmedID();
      }
      //UNPUBLISHED TRANSGENIC
      else if (isPublished != null && !isPublished())
      {
        if(isNullOrEmpty(producedInLabOfHolder))
        {
          valid = false;
          producedInLabOfHolderErr = "Please specify where the rat was produced";
        }

        if(isNullOrEmpty(comment))
        {
          valid = false;
          commentErr = "Please provide a description of the transgene.";
        }
      }
      else
      {
        isPublishedErr = "Select Yes or No";
        valid = false;
      }

      valid &= validateMTA();
    }
    else if(isIS())
    {
      if(isNullOrEmpty(ISSupplier))
      {
        valid = false;
        ISSupplierErr = "Please provide the supplier name (e.g. 'JAX')";
      } else if(isNullOrEmpty(ISSupplierCatalogNumber))
      {
        //valid = false;
        //ISSupplierErr = "Please provide the supplier catalog number (e.g '000664')";

      }
      else if(!isNullOrEmpty(ISSupplierCatalogNumber) && !isNumericString(ISSupplierCatalogNumber))
      {
        valid = false;
        ISSupplierErr = "Please enter only numbers (no spaces), or leave this field blank";
      }
    }
    return valid;
  }


  public void clearAllErrors()
  {
    clearHolderErrors();
    clearRatDetailsErrors();
    clearRatTypeErrors();
  }

  private void clearHolderErrors()
  {
    this.holderFacilityErr = null;
    this.holderNameErr = null;
    this.otherFacilityErr = null;
    this.otherHolderNameErr = null;
  }
  private void clearRatTypeErrors()
  {
    ratTypeErr = transgenicTypeErr = isPublishedErr = null;
  }
  private void clearRatDetailsErrors()
  {
    transgenicTypeErr = null;
    ratTypeErr = null;
    isPublishedErr = null;

    ratNameErr = null;
    ratDisciplinesErr = null;

    MAModificationTypeErr = null;
    MARgdGeneIDErr = null;

    TGExpressedSequenceErr = null;
    TGRegulatoryElementErr = null;
    TGReporterErr = null;
    TGKnockedInGeneErr = null;
    TGRatGeneErr = null;
    TGOtherErr = null;

    ISSupplierErr = null;

    backgroundStrainErr = null;
    mtaRequiredErr = null;
    commentErr = null;
    ratRGDIDErr = null;
    officialSymbolErr = null;
    PMIDErr = null;
    gensatFounderLineErr = null;
    producedInLabOfHolderErr = null;
    cryopreservedErr = null;
  }

  private boolean isNullOrEmpty(String input)
  {
    return input == null || input.isEmpty();
  }

  private boolean validateRGDRatID(String typeString)
  {
    boolean valid = true;
    if(ratRGDID == null || ratRGDID.isEmpty())
    {
      ratRGDIDErr = "Please enter the RGD ID for the " + typeString;
      return false;
    }
    if(!"none".equalsIgnoreCase(ratRGDID) && !isNumericString(ratRGDID))
    {
      ratRGDIDErr = "Please enter only numbers or 'none'";
      return false;
    }
    if(!hasValidRatRGDID())
    {
      ratRGDIDErr = "Invalid RGD ID";
      return false;
    }
    return valid;
  }

  private boolean validateOfficialSymbol(String typeString)
  {
    boolean valid = true;
    if(isNullOrEmpty(officialSymbol))
    {
      //officialSymbolErr = "Please enter the Official Symbol for the " + typeString;
      //valid = false;;
    }

    if(ratRGDID != null && ratRGDID.equalsIgnoreCase("none"))
    {
      valid = true;
      //officialSymbolErr = "";
    }

    return valid;
  }

  private boolean validatePubmedID()
  {
    boolean valid = true;
    if(PMID == null || PMID.isEmpty())
    {
      PMIDErr = "Please enter the Pubmed ID number";
      return false;
    }
    if(!isNumericString(PMID))
    {
      PMIDErr = "Please enter only nubmers";
      return false;
    }
    if(!hasValidPMID())
    {
      if(ratRGDID != null && !ratRGDID.equalsIgnoreCase("none"))
      {
        PMIDErr = "Invalid PubmedID";
        return false;
      }
    }
    return valid;
  }

  private boolean validateMTA()
  {
    /*if(mtaRequired == null)
    {
      mtaRequiredErr = "Please select one of the three options";
      return false;
    }*/
    return true;

  }

  private boolean isNumericString(String input)
  {
    Pattern ptn = Pattern.compile("[0-9]+");
    Matcher matcher = ptn.matcher(input);

    return matcher.find() && matcher.matches();
  }

  public String getNiceRatType()
  {
    if(ratType.equalsIgnoreCase("Transgene"))
    {
      return "Transgene rat";
    }
    return ratType;
  }


  public String getFullRatTypeTitle(){
    String ratTypeTitle = "Unknown Rat Type";
    if(isMA())
    {
      ratTypeTitle = "Mutant Allele";
    }
    else if (isTG())
    {
      ratTypeTitle = "Transgene";
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

  public String printRat(String lineDelimeter)
  {
    StringBuffer buf = new StringBuffer();
    buf.append("Name: " + getRatName() + lineDelimeter);
    buf.append("Type: " + getFullRatTypeTitle()+ lineDelimeter);

    buf.append("Holder: " + getHolderName() + lineDelimeter);
    buf.append("Other Holder : " + getOtherHolderName()+ lineDelimeter);
    buf.append("Facility: " +getHolderFacility() + lineDelimeter);
    buf.append("Other Facility: " + getOtherHolderFacility()+ lineDelimeter);
    if(isMA())
    {
      buf.append("Modification Type: " + getMAModificationType()+ lineDelimeter);
      buf.append("Gene: " +getMARgdGeneID() + lineDelimeter);
    }
    if(isTG())
    {
      buf.append("Expressed Sequence: " +getTGExpressedSequence() + lineDelimeter);
      buf.append("Regulatory Element: " +getTGRegulatoryElement() + lineDelimeter);
      buf.append("Reporter: " +getTGReporter() + lineDelimeter);
      buf.append("Knocked-in Gene: " +getTGKnockedInGene() + lineDelimeter);
      buf.append("Rat Gene: " +getTGRatGene() + lineDelimeter);
      buf.append("Description: " + getTGOther()+ lineDelimeter);
      buf.append("Gensat Founder Line: " +getGensatFounderLine() + lineDelimeter);
    }
    if(isIS())
    {
      buf.append("Supplier: " +getISSupplier() + lineDelimeter);
    }
    if(isMA() || isTG())
    {
      buf.append("Background Strain: " +getBackgroundStrain() + lineDelimeter);
      buf.append("Allele/Transgene detail RGD ID: " +getRatRGDID() + lineDelimeter);
      buf.append("Official Symbol: " +getOfficialSymbol() + lineDelimeter);
      buf.append("Pubmed ID: " +getPMID() + lineDelimeter);
      buf.append("Comment: " +getComment() + lineDelimeter);
    }

    return buf.toString();
  }

  public RatSubmission(String tabDelimitedPropertiesString)
  {
    Properties props = new Properties();
    if (tabDelimitedPropertiesString == null)
    {
      return;
    }

    StringTokenizer t = new StringTokenizer(tabDelimitedPropertiesString, "\t");
    while (t.hasMoreTokens()) {
      StringTokenizer t2 = new StringTokenizer(t.nextToken(), "=");
      String prop = t2.nextToken();
      String val = null;
      if (t2.hasMoreElements()) {
        val = t2.nextToken();
      }
      if (val != null && val.length() > 0) {
        props.setProperty(prop, val);
      }
    }

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
      }
      else if(propName.equalsIgnoreCase("officialRatName"))
      {
        setOfficialRatName(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("holder"))
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
      }else if(propName.equalsIgnoreCase("modifcationType"))
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
      }else if(propName.equalsIgnoreCase("retporter"))
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
      }else if(propName.equalsIgnoreCase("gensatFounderLine"))
      {
        setGensatFounderLine(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("supplierForInbredStrain"))
      {
        setISSupplier(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("strain"))
      {
        setBackgroundStrain(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("mta"))
      {
        setMtaRequired(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("comment"))
      {
        setComment(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("repository"))
      {
        setRatRGDID(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("source"))
      {
        setOfficialSymbol(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("pmid"))
      {
        setPMID(props.getProperty(propName));
      }else if(propName.equalsIgnoreCase("cryopreserved"))
      {
        setCryopreserved(props.getProperty(propName));
      }



    }
  }


  public static Properties GetPropertiesString(UserData submitterData, RatSubmission newRat)
  {
    Properties props = new Properties();

    //temporary properties conversion.

    //general
    props.setProperty("First", submitterData.getFirstName());
    props.setProperty("Last",submitterData.getLastName());
    props.setProperty("Dept",submitterData.getDepartment());
    props.setProperty("Email",submitterData.getEmail());

    //all types
    props.setProperty("is_rat", newRat.getIs_rat());
    if (props.get("is_rat").equals("1")){
      newRat.setIs_rat("1");
    }
    //System.out.println("Is rat? " + props.getProperty("is_rat"));
    props.setProperty("RatType", newRat.getRatType());
    if(newRat.isMA() || newRat.isTG())
    {
      props.setProperty("isPublished", newRat.getIsPublished());
    }
    if (newRat.getRatName() != null && !newRat.getRatName().isEmpty())
    {
      props.setProperty("ratName",newRat.getRatName());
    }
    if (newRat.getOfficialRatName() != null && !newRat.getOfficialRatName().isEmpty())
    {
      props.setProperty("officialRatName", newRat.getOfficialRatName());
    }

    props.setProperty("holder",newRat.getHolderName());
    props.setProperty("facility",newRat.getHolderFacility());
    if(newRat.hasOtherHolderName()){
      props.setProperty("otherHolder",newRat.getOtherHolderName());
    }
    if(newRat.hasOtherFacilityName()){
      props.setProperty("otherFacility",newRat.getOtherHolderFacility());
    }

    props.setProperty("comment",emptyIfNull(newRat.getComment()));
    props.setProperty("rawRGDComment", emptyIfNull(newRat.getRawRGDComment()));

    //mutant allele
    if(newRat.isMA())
    {
      props.setProperty("modificationType",newRat.getMAModificationType());
      props.setProperty("geneOfMutantAlleleRat",newRat.getMARgdGeneID());
    }
    //transgenic
    if(newRat.isTG())
    {

      props.setProperty("TransgenicType", newRat.getTransgenicType());
      props.setProperty("regulatoryElement",emptyIfNull(newRat.getTGRegulatoryElement()));

      props.setProperty("knockedInGene",emptyIfNull(newRat.getTGKnockedInGene()));

      props.setProperty("gensatFounderLine",emptyIfNull(newRat.getGensatFounderLine()));
    }

    //inbred strain
    if(newRat.isIS())
    {
      props.setProperty("supplierForInbredStrain",newRat.getISSupplier());
      props.setProperty("supplierForInbredStrainCatalogNumber",emptyIfNull(newRat.getISSupplierCatalogNumber()));
      props.setProperty("supplierForInbredStrainCatalogUrl",emptyIfNull(newRat.getISSupplierCatalogUrl()));
    }
    props.setProperty("cryopreserved",emptyIfNull(newRat.getCryopreserved()));
    //common stuff
    if(newRat.isMA() || newRat.isTG())
    {
      props.setProperty("ExpressedSequence",emptyIfNull(newRat.getTGExpressedSequence()));
      props.setProperty("reporter",emptyIfNull(newRat.getTGReporter()));
      props.setProperty("rat gene",emptyIfNull(newRat.getTGRatGene()));
      props.setProperty("other",emptyIfNull(newRat.getTGOther()));

      props.setProperty("strain",emptyIfNull(newRat.getBackgroundStrain()));
      props.setProperty("mta",emptyIfNull(newRat.getMtaRequired()));
      props.setProperty("producedInLabOfHolder",emptyIfNull(newRat.getProducedInLabOfHolder()));

      props.setProperty("repository",emptyIfNull(newRat.getRatRGDID()));
      props.setProperty("source",emptyIfNull(newRat.getOfficialSymbol()));
      //props.setProperty("pmid",emptyIfNull(newRat.getPMID()));

      props.setProperty("geneValid",emptyIfNull(newRat.getMARgdGeneIDValid()));
      props.setProperty("geneValidationString",emptyIfNull(newRat.getMARgdGeneIDValidationString()));
      props.setProperty("targetGeneValid",emptyIfNull(newRat.getTGRatGeneValid()));
      props.setProperty("targetGeneValidationString",emptyIfNull(newRat.getTGRatGeneValidationString()));
    }
    return props;
  }

  public String getIsPublished()
  {
    return isPublished;
  }
  public void setIsPublished(String isPublished) {
    this.isPublished = isPublished;
  }
  public String getHolderName() {
    return holderName;
  }
  public void setHolderName(String holderName) {
    this.holderName = holderName;
  }
  public String getHolderFacility() {
    return holderFacility;
  }
  public void setHolderFacility(String holderFacility) {
    this.holderFacility = holderFacility;
  }
  public String getRatName() {
    return ratName;
  }
  public void setRatName(String value) {
    this.ratName = value;
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
  public void setRatType(String value) {
    this.ratType = value;
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
    MARgdGeneID = rgdGeneID.trim();
  }
  public String getMtaRequired() {
    return mtaRequired;
  }
  public void setMtaRequired(String mtaRequired) {
    this.mtaRequired = mtaRequired;
  }
  public String getBackgroundStrain() {
    return backgroundStrain;
  }
  public void setBackgroundStrain(String backgroundStrain) {
    this.backgroundStrain = backgroundStrain;
  }
  public String getComment() {
    return comment;
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

  public String getTGExpressedSequence() {
    return TGExpressedSequence;
  }
  public void setTGExpressedSequence(String expressedSequence) {
    this.TGExpressedSequence = expressedSequence;
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
    TGKnockedInGene = knockedInGene.trim();
  }
  public String getISSupplier() {
    return ISSupplier;
  }
  public void setISSupplier(String supplier) {
    if (supplier == null)
    {
      supplier = "";
    }
    ISSupplier = supplier.trim();
  }
  public String getRatRGDID() {
    return ratRGDID;
  }
  public void setRatRGDID(String ratRGDID) {
    this.ratRGDID = ratRGDID.trim();
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
    PMID = pmid.trim();
  }
  public String getGensatFounderLine() {
    return gensatFounderLine;
  }
  public void setGensatFounderLine(String gensatFounderLine) {
    this.gensatFounderLine = gensatFounderLine;
  }
  public String getRatNameErr() {
    return ratNameErr;
  }
  public void setRatNameErr(String ratNameErr) {
    this.ratNameErr = ratNameErr;
  }
  public String getRatTypeErr() {
    return ratTypeErr;
  }
  public void setRatTypeErr(String ratTypeErr) {
    this.ratTypeErr = ratTypeErr;
  }
  public String getHolderNameErr() {
    return holderNameErr;
  }
  public void setHolderNameErr(String holderNameErr) {
    this.holderNameErr = holderNameErr;
  }
  public String getHolderFacilityErr() {
    return holderFacilityErr;
  }
  public void setHolderFacilityErr(String holderFacilityErr) {
    this.holderFacilityErr = holderFacilityErr;
  }
  public String getIsPublishedErr() {
    return isPublishedErr;
  }
  public void setIsPublishedErr(String isPublishedErr) {
    this.isPublishedErr = isPublishedErr;
  }
  public String getMAModificationTypeErr() {
    return MAModificationTypeErr;
  }
  public void setMAModificationTypeErr(String modificationTypeErr) {
    MAModificationTypeErr = modificationTypeErr;
  }
  public String getMARgdGeneIDErr() {
    if(!isNullOrEmpty(MARgdGeneIDValidationString))
      return MARgdGeneIDValidationString;
    return MARgdGeneIDErr;
  }
  public void setMARgdGeneIDErr(String rgdGeneIDErr) {
    MARgdGeneIDErr = rgdGeneIDErr;
  }
  public String getTGExpressedSequenceErr() {
    return TGExpressedSequenceErr;
  }
  public void setTGExpressedSequenceErr(String expressedSequenceErr) {
    TGExpressedSequenceErr = expressedSequenceErr;
  }
  public String getTransgenicTypeErr() {
    return transgenicTypeErr;
  }
  public void setTransgenicTypeErr(String transgenicTypeErr) {
    this.transgenicTypeErr = transgenicTypeErr;
  }
  public String getTGRegulatoryElementErr() {
    return TGRegulatoryElementErr;
  }
  public void setTGRegulatoryElementErr(String regulatoryElementErr) {
    TGRegulatoryElementErr = regulatoryElementErr;
  }
  public String getTGReporterErr() {
    return TGReporterErr;
  }
  public void setTGReporterErr(String reporterErr) {
    TGReporterErr = reporterErr;
  }
  public String getTGKnockedInGeneErr() {
    if(!isNullOrEmpty(TGKnockedInGeneValidationString))
      return TGKnockedInGeneValidationString;
    return TGKnockedInGeneErr;
  }
  public void setTGKnockedInGeneErr(String knockedInGeneErr) {
    TGKnockedInGeneErr = knockedInGeneErr;
  }
  public String getISSupplierErr() {
    return ISSupplierErr;
  }
  public void setISSupplierErr(String supplierErr) {
    ISSupplierErr = supplierErr;
  }
  public String getBackgroundStrainErr() {
    return backgroundStrainErr;
  }
  public void setBackgroundStrainErr(String backgroundStrainErr) {
    this.backgroundStrainErr = backgroundStrainErr;
  }
  public String getMtaRequiredErr() {
    return mtaRequiredErr;
  }
  public void setMtaRequiredErr(String mtaRequiredErr) {
    this.mtaRequiredErr = mtaRequiredErr;
  }
  public String getCommentErr() {
    return commentErr;
  }
  public void setCommentErr(String commentErr) {
    this.commentErr = commentErr;
  }
  public String getRatRGDIDErr() {
    if(!isNullOrEmpty(ratRGDIDValidationString))
      return ratRGDIDValidationString;
    return ratRGDIDErr;
  }
  public void setRatRGDIDErr(String ratRGDIDErr) {
    this.ratRGDIDErr = ratRGDIDErr;
  }
  public String getOfficialSymbolErr() {
    return officialSymbolErr;
  }
  public void setOfficialSymbolErr(String officialSymbolErr) {
    this.officialSymbolErr = officialSymbolErr;
  }
  public String getPMIDErr() {
    if(!isNullOrEmpty(PMIDValidationString))
      return PMIDValidationString;
    return PMIDErr;
  }
  public void setPMIDErr(String err) {
    PMIDErr = err;
  }
  public String getGensatFounderLineErr() {
    return gensatFounderLineErr;
  }
  public void setGensatFounderLineErr(String gensatFounderLineErr) {
    this.gensatFounderLineErr = gensatFounderLineErr;
  }
  public String getTGRatGene() {
    return TGRatGene;
  }
  public void setTGRatGene(String ratGene) {
    TGRatGene = ratGene.trim();
  }
  public String getTGRatGeneErr() {
    if(!isNullOrEmpty(TGRatGeneValidationString))
      return TGRatGeneValidationString;
    return TGRatGeneErr;
  }
  public void setTGRatGeneErr(String ratGeneErr) {
    TGRatGeneErr = ratGeneErr;
  }
  public String getTGOther() {
    return TGOther;
  }
  public void setTGOther(String other) {
    TGOther = other;
  }
  public String getTGOtherErr() {
    return TGOtherErr;
  }
  public void setTGOtherErr(String otherErr) {
    TGOtherErr = otherErr;
  }

  public String getRatDisciplineErr() {
    return ratDisciplinesErr;
  }
  public void setRatDisciplineErr(String ratDisciplineErr) {
    this.ratDisciplinesErr = ratDisciplineErr;
  }

  public String getOtherHolderName() {
    return otherHolderName;
  }
  public void setOtherHolderName(String otherHolderName) {
    this.otherHolderName = otherHolderName;
  }
  public String getOtherHolderFacility() {
    return otherHolderFacility;
  }
  public void setOtherHolderFacility(String otherHolderFacility) {
    this.otherHolderFacility = otherHolderFacility;
  }
  public String getOtherHolderNameErr() {
    return otherHolderNameErr;
  }
  public void setOtherHolderNameErr(String otherHolderNameErr) {
    this.otherHolderNameErr = otherHolderNameErr;
  }
  public String getOtherFacilityErr() {
    return otherFacilityErr;
  }
  public void setOtherFacilityErr(String otherFacilityErr) {
    this.otherFacilityErr = otherFacilityErr;
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


  public String getRatRGDIDValid() {
    return ratRGDIDValid;
  }


  public void setRatRGDIDValid(String ratRGDIDValid) {
    this.ratRGDIDValid = ratRGDIDValid;
  }


  public String getRatDisciplinesErr() {
    return ratDisciplinesErr;
  }


  public void setRatDisciplinesErr(String ratDisciplinesErr) {
    this.ratDisciplinesErr = ratDisciplinesErr;
  }


  public String getRatRGDIDValidationString() {
    return ratRGDIDValidationString;
  }


  public void setRatRGDIDValidationString(String ratRGDIDValidationString) {
    this.ratRGDIDValidationString = ratRGDIDValidationString;
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

  public String getISSupplierCatalogNumber() {
    return ISSupplierCatalogNumber;
  }

  public void setISSupplierCatalogNumber(String supplierCatalogNumber) {
    ISSupplierCatalogNumber = supplierCatalogNumber;
  }

  public String getISSupplierCatalogUrl() {
    return ISSupplierCatalogUrl;
  }

  public void setISSupplierCatalogUrl(String supplierCatalogUrl) {
    ISSupplierCatalogUrl = supplierCatalogUrl;
  }

  public String getProducedInLabOfHolder() {
    return producedInLabOfHolder;
  }

  public void setProducedInLabOfHolder(String producedInLabOfHolder) {
    this.producedInLabOfHolder = producedInLabOfHolder;
  }

  public String getProducedInLabOfHolderErr() {
    return producedInLabOfHolderErr;
  }

  public void setProducedInLabOfHolderErr(String producedInLabOfHolderErr) {
    this.producedInLabOfHolderErr = producedInLabOfHolderErr;
  }

  public String getCryopreserved() {
    return cryopreserved;
  }

  public void setCryopreserved(String cryopreserved) {
    this.cryopreserved = cryopreserved;
  }

  public String getCryopreservedErr() {
    return cryopreservedErr;
  }

  public void setCryopreservedErr(String cryopreservedErr) {
    this.cryopreservedErr = cryopreservedErr;
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