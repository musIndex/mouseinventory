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
  String rawMGIComment;
  String ratMGIID;
  String ratMGIIDValidationString;
  String ratMGIIDValid;
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
  String MAMgiGeneIDErr;

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
  String ratMGIIDErr;
  String officialSymbolErr;
  String PMIDErr;
  String gensatFounderLineErr;

  String producedInLabOfHolderErr;
  String cryopreservedErr;
}