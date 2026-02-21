package edu.ucsf.mousedatabase;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.StringReader;
import java.lang.Thread;

import edu.ucsf.mousedatabase.beans.MouseSubmission;
import edu.ucsf.mousedatabase.dataimport.ImportStatusTracker;
import edu.ucsf.mousedatabase.dataimport.ImportStatusTracker.ImportStatus;
import edu.ucsf.mousedatabase.objects.MGIResult;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MGIConnect {

  //todo get these from the mgi database, they shouldn't be static
	//MGI_MARKER is for GENE page in informatics.jax.org
  //These int values were used in the old MGI postgresql database, just keep use it to distinguish the different informatics.jax.org page types
  public static int MGI_MARKER = 2;
  //MGI_ALLELE is set in MGIResult object, called in HTMLGeneration, same as old MGI postgresql db "mgiTypeKey"
  public static int MGI_ALLELE = 11;
  //MGI_REFRENCE is for PUBMED articles
  public static int MGI_REFERENCE = 1;
// from old MGI postgresql db
  public static int MGI_MARKER_OTHER_GENOME_FEATURE = 9;
//These are not used. -EW
  public static final String pmDBurl = "http://www.ncbi.nlm.nih.gov/pubmed/";
  public static final String pmDBurlTail = "?dopt=Abstract";
  public static final String mgiDBurl = "http://www.informatics.jax.org/accession/MGI:";

 
  // TODO make this method public and make callers figure out typeIDs
  public static MGIResult doMGIQuery(String accessionID, int expectedTypeID, String wrongTypeString) {
    return doMGIQuery(accessionID, expectedTypeID, wrongTypeString, true);
  }

  public static MGIResult doMGIQuery(String accessionID, int expectedTypeID, String wrongTypeString,
      boolean offlineOK) {

    MGIResult result = new MGIResult();
    result.setAccessionID(accessionID);
    //expectedTypeID will set to either MGI_MARKER for MGI GENE PAGE or MGI_ALLELE for Mutant Allele/Transgene MGI Page
    result.setType(expectedTypeID);
   

    String accID = accessionID;
    if (expectedTypeID != MGI_REFERENCE) {
    	//This will set both allele ID or gene ID
      accID = "MGI:" + accessionID;
    }
    //Setting GENE symbol and name, only way to set symbol and name, no edit field in mouse record.
    if (expectedTypeID == MGI_MARKER) {
    	String mgiGeneJson = mgiGeneRest(accID);
    	if (mgiGeneJson == null) {
    		result.setValid(offlineOK);
    	    result.setErrorString("Not able to access MGI data.");
    	    result.setTitle(result.getErrorString());
    	    result.setAuthors("");
    	    result.setName(result.getErrorString());
    	    result.setSymbol("");
    	    result.setMgiConnectionTimedout(true);
    		
    	}else {
    	JsonReader jsonGeneReader = Json.createReader(new StringReader(mgiGeneJson));
		JsonObject jsonGeneObject = jsonGeneReader.readObject();
		
    	String geneSymbol = null;
    	geneSymbol = jsonGeneObject.getString("symbol");

    	String geneName = null;
    	geneName = jsonGeneObject.getString("name");
    	 result.setSymbol(geneSymbol);
         result.setName(geneName);
         result.setValid(true);
    	}
    }

    if (expectedTypeID == MGI_ALLELE) {
    	String alleleJson= mgiAlleleRest(accID);
    	
			if (alleleJson == null) {
				result.setValid(false);
				result.setErrorString(wrongTypeString);
			} else {
			JsonReader jsonReader = Json.createReader(new StringReader(alleleJson));
			JsonObject jsonObject = jsonReader.readObject();
			JsonObject innerJson = jsonObject.getJsonObject("allele");
			
			String alleleSymbol = null;
			alleleSymbol = innerJson.get("alleleSymbol").asJsonObject().getString("formatText").replaceAll("<sup>", "<").replaceAll("</sup>", ">");
			//String fixedSymbolObj = symbolObj.replaceAll("<sup>", "<").replaceAll("</sup>", ">");
			result.setSymbol(alleleSymbol);
			//REST does not have Allele Name in JSON
			result.setName("");
			result.setValid(true);
			result.setAuthors("");
			result.setTitle("");
            } 
			//Can't set Reference pubmed article ID not in REST JSON
	if (expectedTypeID == MGI_REFERENCE) {
	              result.setAuthors("");
	              result.setTitle("");
	              result.setValid(true);
	            }		
    }
  
    return result;
}
  private static String mgiAlleleRest(String mgiAlleleId) {
		String alleleJson = null;
		String restMGI = "https://www.alliancegenome.org/api/allele/" + mgiAlleleId;
		Client clientMouse = ClientBuilder.newClient();
		try {
			
			Response response = clientMouse.target(restMGI).request().get();
			if (response.getStatus() == Response.Status.OK.getStatusCode() || response.getStatus() == Response.Status.CREATED.getStatusCode()) {
              //System.out.println("Request successful!");
              alleleJson = clientMouse.target(restMGI)
  					.request(MediaType.APPLICATION_JSON)
  					.get(String.class);
          } else {
              // Handle other non-2xx status codes explicitly if needed
              System.out.println("Request failed with status: " + response.getStatus());
          }
			
		}catch (Exception e) {
	    	//allelJSON will be set to null if can't connect
	    }
	    finally 
	    {
	    	clientMouse.close();
	    }
		System.out.println("allele json:"+ alleleJson);
		return alleleJson;
	}
  
  public static String mgiGeneRest(String mgiID) {
	  	String geneJson = null;
	  	Client clientGene = ClientBuilder.newClient();
	  	String geneMGI = "https://www.alliancegenome.org/api/gene/" + mgiID;
	  	
	    try {
	    	Response response = clientGene.target(geneMGI).request().get();
			if (response.getStatus() == Response.Status.OK.getStatusCode() || response.getStatus() == Response.Status.CREATED.getStatusCode())
				{
				geneJson = clientGene.target(geneMGI)
			    		.request(MediaType.APPLICATION_JSON)
			    		.get(String.class);  
				}
			
	    }catch (Exception e) {
	    	//geneJSON will be set to null if can't connect
	    }
	    finally 
	    {
	    	clientGene.close();
	    }
	    
	  return geneJson;
  }
  public static HashMap<Integer, MouseSubmission> SubmissionFromMGI(Collection<Integer> accessionIDs,
      int importTaskId) {
    HashMap<Integer, MouseSubmission> newSubmissions = new HashMap<Integer, MouseSubmission>();

    // TODO validate accession IDs first? so that we don't have to duplicate logic
    // like
    // checking that it isn't a QTL or other genome feature

    HashMap<Integer, Properties> results = getPropertiesFromAlleleMgiID(accessionIDs, importTaskId);
    //System.out.println("SubmissionFromMGI method, printing results: "+ results);
    for (int key : results.keySet()) {

      Properties props = results.get(key);
      if (props != null && !props.isEmpty()) {
        MouseSubmission sub = new MouseSubmission();
        sub.setMouseMGIID(Integer.toString(key));

        sub.setIsPublished("No");
        sub.setHolderFacility("unassigned");
        sub.setHolderName("unassigned");

        sub.setMouseType("unknown");

       // if (verbose) System.out.println("*****************************");
       // if (verbose) System.out.println("Allele MGI ID: " + key);

        String geneId = null;

        StringBuilder propertyList = new StringBuilder();

        for (String prop : props.stringPropertyNames())
        {
          String value = props.getProperty(prop);


        //  if (verbose) System.out.println(prop + ": " + value);
          if (prop.equals("mouseName"))
          {
            sub.setOfficialMouseName(trimOfficialName(value));
          }
          else if (prop.equals("mouseType"))
          {
            propertyList.append("*" + prop + ":* " + value + "\r\n");
            //targeted captures knockout or knockin mice, can be transchromosomal 
            if(value.startsWith("Targeted"))
            {
              sub.setMouseType("Mutant Allele");

              sub.setMAModificationType("undetermined");
              if(value.contains("knock-out"))
              {

              }
              else if (value.contains("knock-in"))
              {

              }
            }
            //Added Radiation induced muations -EW
            else if (value.startsWith("Radiation")) 
            {
              sub.setMouseType("Mutant Allele");
              sub.setMAModificationType("undetermined");
            }
            else if (value.startsWith("Spontaneous"))
            {
              sub.setMouseType("Mutant Allele");
              sub.setMAModificationType("undetermined");
            }
            else if (value.startsWith("Transgenic"))
            {
              sub.setMouseType("Transgene");
              sub.setTransgenicType("undetermined");
              sub.setTGExpressedSequence("undetermined");
              
            }
            else if (value.startsWith("Gene trapped"))
            {
              sub.setMouseType("Mutant Allele");
              sub.setMAModificationType("undetermined");
            }
            else if (value.startsWith("Not Applicable"))
            {
       
            		sub.setMouseType("Inbred Strain");
            	
            }
            else if (value.startsWith("QTL"))
            {
            	sub.setMouseType("Inbred Strain");
            }
            //Enodnuclease-mediated mice will switch to this type -EW
            else if (value.startsWith("Endonuclease-mediated"))
            {
              sub.setMouseType("Mutant Allele");
              sub.setMAModificationType("undetermined");
            }
            //Transposon induced added to Mutant Allele category -EW
            else if (value.startsWith("Transposon induced"))
            {
            	sub.setMouseType("Mutant Allele");
            	sub.setMAModificationType("undetermined");
            }
            else if (value.startsWith("Chemically"))
            {
            	sub.setMouseType("Mutant Allele");
            	sub.setMAModificationType("undetermined");
            }
            
            else
            {
              sub.setMouseType("undetermined");
            }

        }
          else if (prop.equals("mutationType"))
          {
            propertyList.append("*" + prop + ":* " + value + "\r\n");
            //?????
            if(value.equals("Insertion"))
            {
              //sub.setTransgenicType("random insertion");
            }
            else if (value.equals("Single point mutation"))
            {

            }
            else if (value.equals("Other"))
            {

            }
            else if (value.equals("Intragenic deletion"))
            {

            }
          }
          else if(prop.equals("pubMedID"))
          {
            sub.setIsPublished("Yes");
            sub.setPMID(value);
          }
          else if(prop.equals("geneMgiID"))
          {
            geneId = value;
          }

          else if (prop.equals("officialSymbol"))
          {
            sub.setOfficialSymbol(value);
          }

          else if (prop.equals("description"))
          {
            sub.setComment(value);
          }
        }

        if (sub.getMouseType() != null && sub.getMouseType().equals("Mutant Allele"))
        {

          sub.setMAMgiGeneID(geneId);
        }

        sub.setComment(sub.getComment() + "\r\n\r\nRaw properties returned from MGI:\r\n" + propertyList.toString());

        newSubmissions.put(key, sub);
      }
      else
      {
        newSubmissions.put(key,null);
      }
    }
    return newSubmissions;
  }

  private static String trimOfficialName(String rawName)
  {
    return rawName;
  }

  public static HashMap<Integer,Properties> getPropertiesFromAlleleMgiID(Collection<Integer> accessionIDs, int importTaskId)
  {
    HashMap<Integer,Properties> results = new HashMap<Integer,Properties>();
    
    ImportStatusTracker.UpdateHeader(importTaskId, "Downloading Allele data from MGI (Task 2 of 3)");
    ImportStatusTracker.SetProgress(importTaskId, 0);

    double mgiIdNumber = 1;
    double mgiCount = accessionIDs.size();

    try
    {
     
      for(int accessionID : accessionIDs)
      {

        ImportStatusTracker.SetProgress(importTaskId, mgiIdNumber / mgiCount);

        mgiIdNumber++;
        if (accessionID < 0)
        {
          Log.Info("Ignored invalid accession ID: " + accessionID);
          continue;
        }
     
        Properties props = new Properties();
        results.put(accessionID, props);
       
        //NEED to change from ResultSet to REST results from AllianceGenome has MGI Data
      
        //Call method for allele REST
        //REST doesn't have allele name
        String accId = "MGI:"+accessionID;
        String alleleJson = mgiAlleleRest(accId);
        ImportStatusTracker.AppendMessage(importTaskId,"Allele Json " + alleleJson );
        String alleleSymbol = "";
        String mutation = "";
        String alleleType = "";//Mutant Allele or Transgene
        String mgiGeneID = "";
        String fixedGeneID = "";
        String geneSymbol = "";
        String geneName = "";
        String details = "";
        
    	if (alleleJson == null) {
		//need to error here or check properties set to null, will manually need to add
    		Log.Info("No markers for allele MGI: " + accessionID);
		} else {
		JsonReader jsonReader = Json.createReader(new StringReader(alleleJson));
		JsonObject jsonObject = jsonReader.readObject();
		JsonObject innerJson = jsonObject.getJsonObject("allele");
		JsonArray detailsJsonArray = innerJson.getJsonArray("relatedNotes");
		JsonObject detailsFirstObject = detailsJsonArray.getJsonObject(0);
		
		details = detailsFirstObject.getString("freeText");
		
		alleleSymbol = innerJson.get("alleleSymbol").asJsonObject().getString("formatText").replaceAll("<sup>", "<").replaceAll("</sup>", ">");
		//Determine if Mutant Allele or Transgene
		
		if (jsonObject.containsKey("alleleOfGene"))
		{
		mgiGeneID = jsonObject.get("alleleOfGene").asJsonObject().getString("primaryExternalId");
		//Use fixMgiGeneID as Prop
		fixedGeneID = mgiGeneID.replace("MGI:","");
		String symbolPattern = "<Tg";
		Pattern pattern = Pattern.compile(symbolPattern);
		Matcher symbolMatch = pattern.matcher(alleleSymbol);
			if(symbolMatch.find()) {
				// THIS IS TRANSGENE 
				alleleType = "Transgene";
			}else {
			//THIS IS MUTANT ALLELE need to set as type
				alleleType = "Mutant Allele";
			//Call method to get Gene JSON, set gene name and symbol
				String geneJson = mgiGeneRest(mgiGeneID);
				JsonReader geneJsonReader = Json.createReader(new StringReader(geneJson));
				JsonObject geneJsonObject = geneJsonReader.readObject();
				
				geneName = geneJsonObject.getString("name");
				geneSymbol = geneJsonObject.getString("symbol");
				System.out.println("gene Name is : "+ geneName);
		}
		}
		else {
			//transgene does not have known gene insert
			alleleType = "Transgene";
			
		}
		
         
        //MGI REST does not have mouse name
        props.setProperty("mouseName","");
        props.setProperty("officialSymbol", alleleSymbol);

        ImportStatusTracker.AppendMessage(importTaskId, "MGI:" + accessionID + " -> " + HTMLUtilities.getCommentForDisplay(props.getProperty("officialSymbol")));
       
     
          
          //Manually set mutation after PDU upload on record edit form
          props.setProperty("mutationType",mutation);
        

       
          //convert MGI terminology to ours
          props.setProperty("mouseType",alleleType);
        
       
            props.setProperty("geneSymbol",geneSymbol);
            props.setProperty("geneName",geneName);

           //geneMgiID is set as integer without 'MGI:' need to strip and just have number
              props.setProperty("geneMgiID", fixedGeneID);
              //todo set gene MGI accession ID
         
            props.setProperty("pubMedAuthor", "");
            props.setProperty("pubMedTitle", "");
       
            props.setProperty("pubMedID", "");
         
            props.setProperty("referenceMgiAccessionId", "");
            

    
            props.setProperty("description", details );
		}
		}
    }	
    catch (Exception e)
    {
      Log.Error("Error fetching Allele details from MGI",e);
      ImportStatusTracker.UpdateStatus(importTaskId, ImportStatus.ERROR);
      ImportStatusTracker.AppendMessage(importTaskId,"Error fetching allele details: " + e.getMessage());
    }
    finally
    {
      ImportStatusTracker.SetProgress(importTaskId, 1);
    }



      for(int mgiId : results.keySet()){
        Properties props = results.get(mgiId);
        System.out.println("Properties for MGI:" + mgiId);
        for (String prop : props.stringPropertyNames())
        {
          String value = props.getProperty(prop);
          System.out.println(prop + ": " + value);
        }
      }

    return results;
  

    }


  public static MGIResult DoReferenceQuery(String refAccessionID)
  {
    return doMGIQuery(refAccessionID, MGI_REFERENCE, "Pubmed ID not found.  Please confirm that you entered it correctly.");
  }

    public static MGIResult DoMGIModifiedGeneQuery(String geneAccessionID)
    {
      return doMGIQuery(geneAccessionID, MGI_MARKER, "This is a valid Accession ID, but it does not correspond to a gene detail page.  Click on the link to see what it does correspond to. Find the gene detail page for the gene that is modified in the mouse being submitted and re-enter the ID.");
    }

    public static MGIResult DoMGIInsertGeneQuery(String geneAccessionID)
    {
      return doMGIQuery(geneAccessionID, MGI_MARKER, "Valid Accession ID, but not a gene detail page.");
    }

    public static MGIResult DoMGIExpressedGeneQuery(String geneAccessionID)
    {
      return doMGIQuery(geneAccessionID, MGI_MARKER, "This is a valid Accession ID, but it does not correspond to a gene detail page.  Click on the link to see what it does correspond to. Find the gene detail page for the gene that is expressed and re-enter the ID.");
    }

    public static MGIResult DoMGIKnockedinGeneQuery(String geneAccessionID)
    {
      return doMGIQuery(geneAccessionID, MGI_MARKER, "This is a valid Accession ID, but it does not correspond to a gene detail page.  Click on the link to see what it does correspond to. Find the gene detail page for the gene into which the expressed sequence was inserted and re-enter the ID.");
    }

    public static MGIResult DoMGIAlleleQuery(String alleleAccessionID)
    {
      return doMGIQuery(alleleAccessionID, MGI_ALLELE, "This is a valid Accession ID, but it does not correspond to an allele detail page.  Click on the link to see what it does correspond to.  Find the allele detail page for the mouse being submitted and re-enter the ID.");
    }

    public static MGIResult DoMGITransgeneQuery(String transgeneAccessionID)
    {
      return doMGIQuery(transgeneAccessionID, MGI_ALLELE, "This is a valid Accession ID, but it does not correspond to a transgene detail page.  Click on the link to see what it does correspond to.  Find the transgene detail page for the mouse being submitted and re-enter the ID. ");
    }


}
