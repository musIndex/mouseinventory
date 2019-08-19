package edu.ucsf.mousedatabase.beans;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MouseSubmission {

  /*********************/
  String mouseName;
  String officialMouseName;
  String mouseType;
  String holderName;
  String otherHolderName;
  String holderFacility;
  String otherHolderFacility;
  String isPublished;

  //Mutant Allele fields
  String MAModificationType;
  String MAMgiGeneID;
  String MAMgiGeneIDValidationString;
  String MAMgiGeneIDValid;

  //Transgenic fields
  String TGExpressedSequence;
  String transgenicType;
  String TGRegulatoryElement;
  String TGReporter;
  String TGKnockedInGene;
  String TGKnockedInGeneValidationString;
  String TGKnockedInGeneValid;
  String TGMouseGene;
  String TGMouseGeneValidationString;
  String TGMouseGeneValid;
  String TGOther;

  //Inbred Strain fields

  String ISSupplier;
  String ISSupplierCatalogNumber;
  String ISSupplierCatalogUrl;

  //Common fields
  String backgroundStrain;
  String mtaRequired;
  String comment;
  String rawMGIComment;
  String mouseMGIID;
  String mouseMGIIDValidationString;
  String mouseMGIIDValid;
  String officialSymbol;
  String PMID;
  String PMIDValidationString;
  String PMIDValid;
  String gensatFounderLine;
  String producedInLabOfHolder;
  String cryopreserved;

  //ERROR messages
  //************************************************
  String mouseNameErr;
  String mouseTypeErr;
  String mouseDisciplinesErr;
  String holderNameErr;
  String holderFacilityErr;
  String isPublishedErr;
  String otherHolderNameErr;
  String otherFacilityErr;

  //Mutant Allele fields
  String MAModificationTypeErr;
  String MAMgiGeneIDErr;

  //Transgenic fields
  String TGExpressedSequenceErr;
  String transgenicTypeErr;
  String TGRegulatoryElementErr;
  String TGReporterErr;
  String TGKnockedInGeneErr;
  String TGMouseGeneErr;
  String TGOtherErr;

  //Inbred Strain fields
  String ISSupplierErr;

  //Common fields
  String backgroundStrainErr;
  String mtaRequiredErr;
  String commentErr;
  String mouseMGIIDErr;
  String officialSymbolErr;
  String PMIDErr;
  String gensatFounderLineErr;

  String producedInLabOfHolderErr;
  String cryopreservedErr;

  public MouseSubmission()
  {}

  public void clearMouseData()
  {
    mouseName = null;
    mouseType = null;


    isPublished = null;

    //Mutant Allele fields
    MAModificationType = null;
    MAMgiGeneID = null;
    MAMgiGeneIDValidationString = null;
    MAMgiGeneIDValid = null;

    //Transgenic fields
    TGExpressedSequence = null;
    transgenicType = null;
    TGRegulatoryElement = null;
    TGReporter = null;
    TGKnockedInGene = null;
    TGKnockedInGeneValidationString = null;
    TGKnockedInGeneValid = null;
    TGMouseGene = null;
    TGMouseGeneValidationString = null;
    TGMouseGeneValid = null;
    TGOther = null;

    //Inbred Strain fields

    ISSupplier = null;

    //Common fields
    backgroundStrain = null;
    mtaRequired = null;
    comment = null;
    mouseMGIID = null;
    mouseMGIIDValidationString = null;
    mouseMGIIDValid = null;
    officialSymbol = null;
    PMID = null;
    PMIDValidationString = null;
    PMIDValid = null;
    gensatFounderLine = null;
    producedInLabOfHolder = null;
    cryopreserved = null;
    rawMGIComment = null;


    //ERROR messages
    //************************************************
    mouseNameErr = null;
    mouseTypeErr = null;
    mouseDisciplinesErr = null;
    holderNameErr = null;
    holderFacilityErr = null;
    isPublishedErr = null;
    otherHolderNameErr = null;
    otherFacilityErr = null;

    //Mutant Allele fields
    MAModificationTypeErr = null;
    MAMgiGeneIDErr = null;

    //Transgenic fields
    TGExpressedSequenceErr = null;
    transgenicTypeErr = null;
    TGRegulatoryElementErr = null;
    TGReporterErr = null;
    TGKnockedInGeneErr = null;
    TGMouseGeneErr = null;
    TGOtherErr = null;

    //Inbred Strain fields
    ISSupplierErr = null;
    ISSupplierCatalogNumber = null;

    //Common fields
    backgroundStrainErr = null;
    mtaRequiredErr = null;
    commentErr = null;
    mouseMGIIDErr = null;
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
    clearMouseData();
    clearHolderData();
  }


  public boolean isMA(){ return mouseType!= null && mouseType.equalsIgnoreCase("Mutant Allele");}
  public boolean isTG(){ return mouseType!= null && mouseType.equalsIgnoreCase("Transgene");}
  public boolean isIS(){return mouseType!= null && mouseType.equalsIgnoreCase("Inbred Strain");}

  public boolean hasType() { return mouseType != null; }

  public boolean isJAX() { return ISSupplierCatalogNumber != null && ISSupplierCatalogNumber.equalsIgnoreCase("jax"); }

  public boolean isGeneExprSeq() { return TGExpressedSequence != null && TGExpressedSequence.equalsIgnoreCase("Mouse Gene (unmodified)");}
  public boolean isReporterExprSeq() { return TGExpressedSequence != null && TGExpressedSequence.equalsIgnoreCase("reporter");}
  public boolean isOtherExprSeq() { return TGExpressedSequence != null && TGExpressedSequence.equalsIgnoreCase("Modified mouse gene or Other");}
  public boolean isKnockIn() { return MAModificationType != null && MAModificationType.equalsIgnoreCase("targeted knock-in");}
  public boolean isRandomInsertion() { return transgenicType != null && transgenicType.equalsIgnoreCase("Random insertion");}
  public boolean isPublished() { return isPublished != null && isPublished.equalsIgnoreCase("Yes"); }

  public boolean hasOtherHolderName() { return holderName != null && holderName.equalsIgnoreCase("Other(specify)");};
  public boolean hasOtherFacilityName() { return holderFacility != null && holderFacility.equalsIgnoreCase("Other(specify)");};

  public boolean hasValidMAmgiGeneID() { return MAMgiGeneIDValid != null && MAMgiGeneIDValid.equalsIgnoreCase("true");};
  public boolean hasValidTGKnockedInGene() { return TGKnockedInGeneValid != null && TGKnockedInGeneValid.equalsIgnoreCase("true");};
  public boolean hasValidTGMouseGene() { return TGMouseGeneValid != null && TGMouseGeneValid.equalsIgnoreCase("true");};
  public boolean hasValidMouseMGIID() { return mouseMGIIDValid != null && mouseMGIIDValid.equalsIgnoreCase("true");};
  public boolean hasValidPMID() { return PMIDValid != null && PMIDValid.equalsIgnoreCase("true");};

  //public boolean isCryoOnly() { return cryopreserved != null && cryopreserved.equalsIgnoreCase("Cryo only");};
  public String liveCryoStatus() {return cryopreserved != null ?  cryopreserved : "Live only";};


  public boolean ValidateHolderInfo(){
    boolean valid = true;
    clearHolderErrors();
    clearMouseDetailsErrors();
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



  public boolean validateMouseType(){
    boolean valid = true;
    clearMouseTypeErrors();
    //TODO implement this, just in case javascript doesn't catch all cases

    return valid;
  }



  public boolean validateMouseDetails(){
    boolean valid = true;
    clearMouseDetailsErrors();

    if(isNullOrEmpty(mouseName))
    {
      valid = false;
      mouseNameErr = "Please enter mouse name";
    }
    /* Temporarily disable mouse discipline field validation
    if(isMA() || isTG())
    {

      if(mouseDisciplines == null || mouseDisciplines.length <= 0)
      {
        if(isNullOrEmpty(otherMouseDiscipline))
        {
          valid = false;
          mouseDisciplinesErr = "Please select a discipline";
        }
      }
    }
    */
    if(!isMA() && !isIS() && !isTG())
    {
      mouseTypeErr = "Unknown error occurred.  Please go back to step 1 and try again.";
      clearAll();
      valid = false;
    }
    //MUTANT ALLELE
    if(isMA())
    {
      if(MAModificationType == null)
      {
        valid = false;
        MAModificationTypeErr = "Please select modification type";
      }
      else if(MAModificationType.equalsIgnoreCase("targeted knock-in"))
      {
        //COPIED FROM TRANSGENIC CATEGORY
        if(isNullOrEmpty(TGExpressedSequence))
        {
          valid = false;
          TGExpressedSequenceErr = "Please select the expressed sequence type";
        }
        else if (TGExpressedSequence.equalsIgnoreCase("Mouse gene")
            || TGExpressedSequence.equalsIgnoreCase("Mouse Gene (unmodified)"))
        {
          if(isNullOrEmpty(TGMouseGene))
          {
            valid = false;
            TGMouseGeneErr = "Please enter the MGI Gene ID number";
          }
          else if(!"null".equalsIgnoreCase(TGMouseGene) && !isNumericString(TGMouseGene))
          {
              valid = false;
              TGMouseGeneErr = "Please enter only numbers";
          } else if(!hasValidTGMouseGene())
          {
            valid = false;
            TGMouseGeneErr = "Invalid Gene";
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
            || TGExpressedSequence.equalsIgnoreCase("Modified mouse gene or Other"))
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

      }
      if(isNullOrEmpty(mtaRequired))
      {
        //valid = false;
        //mtaRequiredErr = "Please specifiy whether or not an MTA is required.  If unknown, choose 'Don't Know'";
      }

      if(isNullOrEmpty(MAMgiGeneID))
      {
        valid = false;
        MAMgiGeneIDErr = "Please enter the MGI Gene ID number";
      }
      else if(!isNumericString(MAMgiGeneID))
      {
          valid = false;
          MAMgiGeneIDErr = "Please enter only numbers";
      }
      else if(!hasValidMAmgiGeneID())  //this pulls the valid result from the result of the client-side AJAX request to validate the MGI number
      {
        valid = false;
        MAMgiGeneIDErr = "Invalid gene ID";  //this will get overwritten by MAMGIGeneIDValidationString in getMAMgiGeneID()
      }

      //MUTANT ALLELE PUBLISHED
      if(isPublished != null && isPublished())
      {
        valid &= validateMGIMouseID("allele");
        valid &= validateOfficialSymbol("allele");
        valid &= validatePubmedID();
      }
      //MUTANT ALLELE UNPUBLISHED
      else if (isPublished != null && !isPublished())
      {
        if(isNullOrEmpty(producedInLabOfHolder))
        {
          valid = false;
          producedInLabOfHolderErr = "Please specify where the mouse was produced.";
        }

        if(isNullOrEmpty(comment))
        {
          valid = false;
          commentErr = "Please provide a description of the allele.";
        }
      }
      else
      {
        isPublishedErr = "You must select whether or not this allele is published in step 2";
        valid = false;
      }

      //validate comment
      //validate background strain
      valid &= validateMTA();

    }
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
      else if (TGExpressedSequence.equalsIgnoreCase("Mouse gene")
          || TGExpressedSequence.equalsIgnoreCase("Mouse Gene (unmodified)"))
      {
        if(isNullOrEmpty(TGMouseGene))
        {
          valid = false;
          TGMouseGeneErr = "Please enter the MGI Gene ID number";
        }
        else if(!"null".equalsIgnoreCase(TGMouseGene) && !isNumericString(TGMouseGene))
        {
            valid = false;
            TGMouseGeneErr = "Please enter only numbers";
        } else if(!hasValidTGMouseGene())
        {
          valid = false;
          TGMouseGeneErr = "Invalid Gene";
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
          || TGExpressedSequence.equalsIgnoreCase("Modified mouse gene  or other"))
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
        valid &= validateMGIMouseID(typeString);
        valid &= validateOfficialSymbol(typeString);
        valid &= validatePubmedID();
      }
      //UNPUBLISHED TRANSGENIC
      else if (isPublished != null && !isPublished())
      {
        if(isNullOrEmpty(producedInLabOfHolder))
        {
          valid = false;
          producedInLabOfHolderErr = "Please specify where the mouse was produced";
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
    clearMouseDetailsErrors();
    clearMouseTypeErrors();
  }

  private void clearHolderErrors()
  {
    this.holderFacilityErr = null;
    this.holderNameErr = null;
    this.otherFacilityErr = null;
    this.otherHolderNameErr = null;
  }
  private void clearMouseTypeErrors()
  {
    mouseTypeErr = transgenicTypeErr = isPublishedErr = null;
  }
  private void clearMouseDetailsErrors()
  {
    transgenicTypeErr = null;
    mouseTypeErr = null;
    isPublishedErr = null;

    mouseNameErr = null;
    mouseDisciplinesErr = null;

    MAModificationTypeErr = null;
    MAMgiGeneIDErr = null;

    TGExpressedSequenceErr = null;
    TGRegulatoryElementErr = null;
    TGReporterErr = null;
    TGKnockedInGeneErr = null;
    TGMouseGeneErr = null;
    TGOtherErr = null;

    ISSupplierErr = null;

    backgroundStrainErr = null;
    mtaRequiredErr = null;
    commentErr = null;
    mouseMGIIDErr = null;
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

  private boolean validateMGIMouseID(String typeString)
  {
    boolean valid = true;
    if(mouseMGIID == null || mouseMGIID.isEmpty())
    {
      mouseMGIIDErr = "Please enter the MGI ID for the " + typeString;
      return false;
    }
    if(!"none".equalsIgnoreCase(mouseMGIID) && !isNumericString(mouseMGIID))
    {
      mouseMGIIDErr = "Please enter only numbers or 'none'";
      return false;
    }
    if(!hasValidMouseMGIID())
    {
      mouseMGIIDErr = "Invalid MGI ID";
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

    if(mouseMGIID != null && mouseMGIID.equalsIgnoreCase("none"))
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
      if(mouseMGIID != null && !mouseMGIID.equalsIgnoreCase("none"))
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

  public String getNiceMouseType()
  {
    if(mouseType.equalsIgnoreCase("Transgene"))
    {
      return "Transgene mouse";
    }
    return mouseType;
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

  public String printMouse(String lineDelimeter)
  {
    StringBuffer buf = new StringBuffer();
    buf.append("Name: " + getMouseName() + lineDelimeter);
    buf.append("Type: " + getFullMouseTypeTitle()+ lineDelimeter);

    buf.append("Holder: " + getHolderName() + lineDelimeter);
    buf.append("Other Holder : " + getOtherHolderName()+ lineDelimeter);
    buf.append("Facility: " +getHolderFacility() + lineDelimeter);
    buf.append("Other Facility: " + getOtherHolderFacility()+ lineDelimeter);
    if(isMA())
    {
      buf.append("Modification Type: " + getMAModificationType()+ lineDelimeter);
      buf.append("Gene: " +getMAMgiGeneID() + lineDelimeter);
    }
    if(isTG())
    {
      buf.append("Expressed Sequence: " +getTGExpressedSequence() + lineDelimeter);
      buf.append("Regulatory Element: " +getTGRegulatoryElement() + lineDelimeter);
      buf.append("Reporter: " +getTGReporter() + lineDelimeter);
      buf.append("Knocked-in Gene: " +getTGKnockedInGene() + lineDelimeter);
      buf.append("Mouse Gene: " +getTGMouseGene() + lineDelimeter);
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
      buf.append("Allele/Transgene detail MGI ID: " +getMouseMGIID() + lineDelimeter);
      buf.append("Official Symbol: " +getOfficialSymbol() + lineDelimeter);
      buf.append("Pubmed ID: " +getPMID() + lineDelimeter);
      buf.append("Comment: " +getComment() + lineDelimeter);
    }

    return buf.toString();
  }

  public MouseSubmission(String tabDelimitedPropertiesString)
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
        if(propName.equalsIgnoreCase("MouseType"))
        {
          setMouseType(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("isPublished"))
        {
          setIsPublished(props.getProperty(propName));
        }else if(propName.equalsIgnoreCase("mouseName"))
        {
          setMouseName(props.getProperty(propName));
        }
        else if(propName.equalsIgnoreCase("officialMouseName"))
        {
          setOfficialMouseName(props.getProperty(propName));
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
        }else if(propName.equalsIgnoreCase("retporter"))
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
          setMouseMGIID(props.getProperty(propName));
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


  public static Properties GetPropertiesString(UserData submitterData, MouseSubmission newMouse)
  {
    Properties props = new Properties();

        //temporary properties conversion.

        //general
        props.setProperty("First", submitterData.getFirstName());
        props.setProperty("Last",submitterData.getLastName());
        props.setProperty("Dept",submitterData.getDepartment());
        props.setProperty("Email",submitterData.getEmail());

        //all types
        props.setProperty("MouseType", newMouse.getMouseType());
    if(newMouse.isMA() || newMouse.isTG())
    {
          props.setProperty("isPublished", newMouse.getIsPublished());
    }
    if (newMouse.getMouseName() != null && !newMouse.getMouseName().isEmpty())
        {
      props.setProperty("mouseName",newMouse.getMouseName());
        }
        if (newMouse.getOfficialMouseName() != null && !newMouse.getOfficialMouseName().isEmpty())
        {
          props.setProperty("officialMouseName", newMouse.getOfficialMouseName());
        }

        props.setProperty("holder",newMouse.getHolderName());
        props.setProperty("facility",newMouse.getHolderFacility());
        if(newMouse.hasOtherHolderName()){
          props.setProperty("otherHolder",newMouse.getOtherHolderName());
        }
        if(newMouse.hasOtherFacilityName()){
          props.setProperty("otherFacility",newMouse.getOtherHolderFacility());
        }

        props.setProperty("comment",emptyIfNull(newMouse.getComment()));
        props.setProperty("rawMGIComment", emptyIfNull(newMouse.getRawMGIComment()));

        //mutant allele
        if(newMouse.isMA())
        {
          props.setProperty("modificationType",newMouse.getMAModificationType());
          props.setProperty("geneOfMutantAlleleMouse",newMouse.getMAMgiGeneID());
        }
        //transgenic
        if(newMouse.isTG())
        {

          props.setProperty("TransgenicType", newMouse.getTransgenicType());
          props.setProperty("regulatoryElement",emptyIfNull(newMouse.getTGRegulatoryElement()));

          props.setProperty("knockedInGene",emptyIfNull(newMouse.getTGKnockedInGene()));

          props.setProperty("gensatFounderLine",emptyIfNull(newMouse.getGensatFounderLine()));
        }

        //inbred strain
        if(newMouse.isIS())
        {
          props.setProperty("supplierForInbredStrain",newMouse.getISSupplier());
          props.setProperty("supplierForInbredStrainCatalogNumber",emptyIfNull(newMouse.getISSupplierCatalogNumber()));
          props.setProperty("supplierForInbredStrainCatalogUrl",emptyIfNull(newMouse.getISSupplierCatalogUrl()));
        }
        props.setProperty("cryopreserved",emptyIfNull(newMouse.getCryopreserved()));
        //common stuff
        if(newMouse.isMA() || newMouse.isTG())
        {
          props.setProperty("ExpressedSequence",emptyIfNull(newMouse.getTGExpressedSequence()));
          props.setProperty("reporter",emptyIfNull(newMouse.getTGReporter()));
          props.setProperty("mouse gene",emptyIfNull(newMouse.getTGMouseGene()));
          props.setProperty("other",emptyIfNull(newMouse.getTGOther()));

          props.setProperty("strain",emptyIfNull(newMouse.getBackgroundStrain()));
          props.setProperty("mta",emptyIfNull(newMouse.getMtaRequired()));
          props.setProperty("producedInLabOfHolder",emptyIfNull(newMouse.getProducedInLabOfHolder()));

            props.setProperty("repository",emptyIfNull(newMouse.getMouseMGIID()));
            props.setProperty("source",emptyIfNull(newMouse.getOfficialSymbol()));
            props.setProperty("pmid",emptyIfNull(newMouse.getPMID()));

          props.setProperty("geneValid",emptyIfNull(newMouse.getMAMgiGeneIDValid()));
          props.setProperty("geneValidationString",emptyIfNull(newMouse.getMAMgiGeneIDValidationString()));
          props.setProperty("targetGeneValid",emptyIfNull(newMouse.getTGMouseGeneValid()));
          props.setProperty("targetGeneValidationString",emptyIfNull(newMouse.getTGMouseGeneValidationString()));
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
  public String getMouseName() {
    return mouseName;
  }
  public void setMouseName(String value) {
    this.mouseName = value;
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
  public void setMouseType(String value) {
    this.mouseType = value;
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
    MAMgiGeneID = mgiGeneID.trim();
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
  public String getRawMGIComment() {
    return rawMGIComment;
  }

  public void setRawMGIComment(String rawMGIComment) {
    this.rawMGIComment = rawMGIComment;
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
  public String getMouseMGIID() {
    return mouseMGIID;
  }
  public void setMouseMGIID(String mouseMGIID) {
    this.mouseMGIID = mouseMGIID.trim();
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
  public String getMouseNameErr() {
    return mouseNameErr;
  }
  public void setMouseNameErr(String mouseNameErr) {
    this.mouseNameErr = mouseNameErr;
  }
  public String getMouseTypeErr() {
    return mouseTypeErr;
  }
  public void setMouseTypeErr(String mouseTypeErr) {
    this.mouseTypeErr = mouseTypeErr;
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
  public String getMAMgiGeneIDErr() {
    if(!isNullOrEmpty(MAMgiGeneIDValidationString))
      return MAMgiGeneIDValidationString;
    return MAMgiGeneIDErr;
  }
  public void setMAMgiGeneIDErr(String mgiGeneIDErr) {
    MAMgiGeneIDErr = mgiGeneIDErr;
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
  public String getMouseMGIIDErr() {
    if(!isNullOrEmpty(mouseMGIIDValidationString))
      return mouseMGIIDValidationString;
    return mouseMGIIDErr;
  }
  public void setMouseMGIIDErr(String mouseMGIIDErr) {
    this.mouseMGIIDErr = mouseMGIIDErr;
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
  public String getTGMouseGene() {
    return TGMouseGene;
  }
  public void setTGMouseGene(String mouseGene) {
    TGMouseGene = mouseGene.trim();
  }
  public String getTGMouseGeneErr() {
    if(!isNullOrEmpty(TGMouseGeneValidationString))
      return TGMouseGeneValidationString;
    return TGMouseGeneErr;
  }
  public void setTGMouseGeneErr(String mouseGeneErr) {
    TGMouseGeneErr = mouseGeneErr;
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

  public String getMouseDisciplineErr() {
    return mouseDisciplinesErr;
  }
  public void setMouseDisciplineErr(String mouseDisciplineErr) {
    this.mouseDisciplinesErr = mouseDisciplineErr;
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


  public String getMouseMGIIDValid() {
    return mouseMGIIDValid;
  }


  public void setMouseMGIIDValid(String mouseMGIIDValid) {
    this.mouseMGIIDValid = mouseMGIIDValid;
  }


  public String getMouseDisciplinesErr() {
    return mouseDisciplinesErr;
  }


  public void setMouseDisciplinesErr(String mouseDisciplinesErr) {
    this.mouseDisciplinesErr = mouseDisciplinesErr;
  }


  public String getMouseMGIIDValidationString() {
    return mouseMGIIDValidationString;
  }


  public void setMouseMGIIDValidationString(String mouseMGIIDValidationString) {
    this.mouseMGIIDValidationString = mouseMGIIDValidationString;
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
