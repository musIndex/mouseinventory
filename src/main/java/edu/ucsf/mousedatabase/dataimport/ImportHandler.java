package edu.ucsf.mousedatabase.dataimport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.ucsf.mousedatabase.*;
import edu.ucsf.mousedatabase.beans.MouseSubmission;
import edu.ucsf.mousedatabase.beans.UserData;
import edu.ucsf.mousedatabase.dataimport.ImportStatusTracker.ImportStatus;
import edu.ucsf.mousedatabase.objects.ChangeRequest;
import edu.ucsf.mousedatabase.objects.Facility;
import edu.ucsf.mousedatabase.objects.Holder;
import edu.ucsf.mousedatabase.objects.ImportReport;
import edu.ucsf.mousedatabase.objects.MGIResult;
import edu.ucsf.mousedatabase.objects.MouseHolder;
import edu.ucsf.mousedatabase.objects.MouseRecord;
import edu.ucsf.mousedatabase.objects.SubmittedMouse;
import edu.ucsf.mousedatabase.servlets.ImportServlet;

public class ImportHandler
{
  private static final String JacksonLaboratory = "Jackson Laboratory";
  private static final String JaxMice = "Jax Mice";

  private static final String Mmmrrc = "MMRRC";

  private static final int MultipleAlleleMiceMgiId = -2;

  public enum ImportObjectType
  {
    UNKNOWN (0),
    PPTCHANGEREQUEST (1),
    PURCHASESUBMISSION (2),
    PURCHASECHANGEREQUEST (3),
    OTHERINSTITUTIONSUBMISSION (4),
    OTHERINSTITUTIONSCHANGEREQUEST (5);

    public final int Id;

    private ImportObjectType(int id)
    {
      this.Id = id;
    }
  }

  public static ImportObjectType GetImportType(int id)
  {
    switch(id)
    {
      case 1:
        return ImportObjectType.PPTCHANGEREQUEST;
      case 2:
        return ImportObjectType.PURCHASESUBMISSION;
      case 3:
        return ImportObjectType.PURCHASECHANGEREQUEST;
      case 4:
        return ImportObjectType.OTHERINSTITUTIONSUBMISSION;
      case 5:
        return ImportObjectType.OTHERINSTITUTIONSCHANGEREQUEST;
    }
    return ImportObjectType.UNKNOWN;
  }

  private static ArrayList<ImportDefinition> importDefinitions;

  public static Collection<ImportDefinition> getImportDefinitions() {

    return importDefinitions;
  }

  private ImportHandler()
  {
    //private constructor prevents instantiation
  }

  public static void InitializeDefinitions()
  {
    //todo read from user-defined xml file at runtime
    importDefinitions = new ArrayList<ImportDefinition>();
    //make sure the ID is the same as the position in the collection
    importDefinitions.add(new ImportDefinition(0,"Transfer Data Upload (TDU)","Create change requests to add holder to mice based on LARC mouse transfer report"));
    importDefinitions.add(new ImportDefinition(1,"Purchase Data Upload (PDU)","Create submissions for new mice or change requests to add holders to existing mice based on a LARC mouse purchases report"));
    importDefinitions.add(new ImportDefinition(2,"Import Data Upload (IDU)","Create submissions for new mice or change requests to add holders to existing mice based on a LARC institutional transfer report"));
    
  }

  public static void handleImport(ArrayList<HashMap<String, String>> csvData, int importDefinitionId, HashMap<String, String> parameters) throws Exception
  {

    int taskId = ImportStatusTracker.RegisterTask("new import task");
    try
    {
    //based on parameters, parse input

    ImportDefinition importDef = null;
    if (importDefinitions.size() <= importDefinitionId)
    {
      String defs = "";
      for(ImportDefinition def : importDefinitions)
      {
        defs += def.Id + " (" + def.Name + ")";
      }
      ImportStatusTracker.UpdateHeader(taskId,"no definition for report type " + importDefinitionId + " found.  Expected one of: " + defs);
      ImportStatusTracker.UpdateStatus(taskId, ImportStatus.ERROR);
      return;
    }
    importDef = importDefinitions.get(importDefinitionId);
    ImportStatusTracker.UpdateHeader(taskId, importDef.Name);

    switch(importDef.Id)
    {
      case 0:
        handlePPTTransferImport(taskId,csvData,importDef,parameters);
        break;
      case 1:
      case 2:
        handleCombinedImport(taskId,csvData,importDef,parameters);
        break;
    }

//    //throw new Exception("No import handler for action " + importDef.getAction());
    }
    catch(Exception e)
    {
      Log.Error("Unhandled exception handling upload",e);
      ImportStatusTracker.AppendMessage(taskId, "Unhnadled exception processing upload : " + e.getMessage());
      ImportStatusTracker.UpdateStatus(taskId, ImportStatus.ERROR);
    }
  }



  private static void handlePPTTransferImport(int importTaskId,ArrayList<HashMap<String, String>> csvData,
      ImportDefinition importDefinition, HashMap<String, String> parameters)
  {

    DateFormat dateFormat = new SimpleDateFormat("MMMM dd");
    String reportName = "";
    if(parameters.containsKey(ImportServlet.importDescriptionFieldName))
    {
      reportName += parameters.get(ImportServlet.importDescriptionFieldName);
    }
    else
    {
      reportName += importDefinition.Name + " " + dateFormat.format(new Date());
    }

    ImportStatusTracker.UpdateTitle(importTaskId, reportName);
    ImportStatusTracker.UpdateHeader(importTaskId, "");
    ImportStatusTracker.UpdateStatus(importTaskId, ImportStatus.PROCESSING);

    ArrayList<String> newChangeRequests = new ArrayList<String>();
    ArrayList<String> skippedChangeRequests = new ArrayList<String>();
    ArrayList<String> paperFormImports = new ArrayList<String>();
    ArrayList<String> blankStrainImports = new ArrayList<String>();
    ArrayList<String> notMutantImports = new ArrayList<String>();
    ArrayList<String> otherStrainImports = new ArrayList<String>();
    ArrayList<String> invalidImports = new ArrayList<String>();

    ArrayList<Integer> newChangeRequestIds = new ArrayList<Integer>();


    String addedHolderCol = "pi (recipient)";
    String mouseIdCol = "strain";
    String currentHolderCol = "pi (sender)";
    //String quantityCol = "qty";
    String roomNameCol = "room";
    //String dateReceivedCol = "received";
    String addedHolderEmailCol = "recipient pi email";
//    String senderNameCol = "sender name";
//    String senderEmailCol = "sender email";
    String recipientNameCol = "recipient name";
    String recipientEmailCol = "recipient email";

    String mouseIdRegex = "^[\\s]*\\(?[\\s]*([0-9]+)[\\s]*\\)?[\\s]*$";

    String facilityCodeRegex = "(.*)-[0-9]+";

    ImportStatusTracker.AppendMessage(importTaskId, "Processing CSV Data");

    double recordNumber = 1;
    double numRecords = csvData.size();
    for(HashMap<String,String> record : csvData)
    {

      ImportStatusTracker.SetProgress(importTaskId, recordNumber++/numRecords);
      //TODO in importdefintion, have properties for column names ...meh!

      String addedHolder = record.get(addedHolderCol);
      String addedHolderEmail = record.get(addedHolderEmailCol);

      String strain = record.get(mouseIdCol);
      if (strain != null)
      {
        strain = strain.trim();
      }
      int mouseId = toInt(HTMLUtilities.extractFirstGroup(mouseIdRegex,strain));
      String currentHolder = record.get(currentHolderCol);

      String nicelyFormattedAddedHolder = formatHolderName(addedHolder);
      String nicelyFormattedCurrentHolder = formatHolderName(currentHolder);

//      String senderName = record.get(senderNameCol);
//      String senderEmail = record.get(senderEmailCol);

      String recipientName = record.get(recipientNameCol);
      String recipientEmail = record.get(recipientEmailCol);

      String roomName = record.get(roomNameCol);
      String facilityCode = HTMLUtilities.extractFirstGroup(facilityCodeRegex, roomName);

      String rawRecord = "<br><span class='rawRecord'><br>Raw data:";
      for (Object key : record.keySet())
      {
        rawRecord += key + "=" + record.get(key) + " ";
      }
      rawRecord += "</span>";




      if (mouseId > 0)
      {
        Log.Info("Handling import for record #"+mouseId);
        ArrayList<MouseRecord> mice = DBConnect.getMouseRecord(mouseId);
        if (mice.size() <= 0)
        {
          invalidImports.add("<span class='importAction'>Record not found: " + mouseId + "</span> (transfer from "
            + currentHolder +  " to " + addedHolder + ")" + rawRecord );
          continue;
        }
        MouseRecord mouse = mice.get(0);
        //check if this holder is already on the record
        Holder localAddedHolder = DBConnect.findHolderByEmail(addedHolderEmail);
        Facility localAddedFacility = DBConnect.findFacilityByCode(facilityCode);
        boolean isDuplicate = false;
        if (localAddedHolder != null)
        {
          Log.Info("Holder " + addedHolder + " (" + addedHolderEmail + ") recognized as holder #" + localAddedHolder.getHolderID());
          for (MouseHolder mouseHolder : mouse.getHolders())
          {
            if (mouseHolder.getHolderID() == localAddedHolder.getHolderID())
            {
              ImportStatusTracker.AppendMessage(importTaskId, "Duplicate - " + addedHolder + " (" + addedHolderEmail + ") already listed as holder of mouse #" + mouseId);
              skippedChangeRequests.add("<span class='importAction'>" + addedHolder + " is already listed as a holder for record "
                + mouse.getMouseName() +  " (#" + mouseId + ")</span>" + rawRecord );
              isDuplicate = true;
              break;
            }
          }
          if (isDuplicate)
          {
            continue;
          }
        }
        else
        {
          ImportStatusTracker.AppendMessage(importTaskId, "Holder " + addedHolder + " (" + addedHolderEmail + ") not recognized, not checking records for duplicate");
        }

        //check if there is already a pending change request to add this holder
        ArrayList<ChangeRequest> existingRequests = DBConnect.getChangeRequests(new String[]{"new","pending"}, null, mouseId);

        if (existingRequests.size() > 0)
        {
          for (ChangeRequest existingRequest : existingRequests)
          {
            if (existingRequest.Properties() == null)
            {
              Log.Info("Change request #" + existingRequest.getRequestID() + " has no Properties, ignoring");
              continue;
            }
            String addHolderString = (String)existingRequest.Properties().get("Add Holder");
            if (addHolderString == null || addHolderString.isEmpty())
            {
              Log.Info("Change request #" + existingRequest.getRequestID() + " add holder property, ignoring");
              continue;
            }
            int holderId =  -1;

            //if holder id is known, format is holderid|holderfirstname holderlastname
            //if unknown, format is holderfirstname holderlastname

            if (addHolderString.indexOf('|') > 0)
            {
              String holderIdStr = addHolderString.substring(0, addHolderString.indexOf('|'));

              holderId = Integer.parseInt(holderIdStr);
            }
            if (addHolderString.equalsIgnoreCase(nicelyFormattedAddedHolder) || (localAddedHolder != null && localAddedHolder.getHolderID() == holderId))
            {
              ImportStatusTracker.AppendMessage(importTaskId, "There is already an open change request (#"
                  + existingRequest.getRequestID() + ")to add " + nicelyFormattedAddedHolder
                  + " to " + mouse.getMouseName() +  "(#" + mouseId + ")");

              skippedChangeRequests.add("<span class='importAction'>There is already an open change request (#"
                  + existingRequest.getRequestID() + ") to add " + nicelyFormattedAddedHolder
                  + " to " + mouse.getMouseName() +  " (#" + mouseId + ")</span>" + rawRecord );
              isDuplicate = true;
              break;
            }
          }
          if (isDuplicate)
          {
            continue;
          }
        }
        else
        {
          //ImportStatusTracker.AppendMessage(importTaskId, "No open change requests for record #" + mouseId );
        }

        Properties props = new Properties();
        props.setProperty("Request Source", importDefinition.Name);

        props.setProperty("New Holder Email", addedHolderEmail);
        props.setProperty("Recipient", recipientName);
        props.setProperty("Recipient email", recipientEmail);
        props.setProperty("Original PI", nicelyFormattedCurrentHolder);

        ChangeRequest request = createChangeRequest(mouseId, addedHolderEmail, nicelyFormattedAddedHolder, localAddedHolder, localAddedFacility, nicelyFormattedAddedHolder,roomName,props);

        newChangeRequestIds.add(request.getRequestID());

        dateFormat = new SimpleDateFormat("EEEE, MMMM dd");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 14);

        String twoWeeksFromNow = dateFormat.format(cal.getTime());

        String subjectText = "";
        String emailBodyText = "";
        
        try{
        subjectText = URLEncoder.encode("Listing " + nicelyFormattedAddedHolder +
        " as a holder of " + mouse.getMouseName() + ", record number " + mouse.getMouseID() +
        ", in the UCSF Mouse Database","ISO-8859-1");
        emailBodyText = URLEncoder.encode(
        "In an effort to keep the UCSF mouse inventory database up-to-date, we have implemented a system " +
        "that tracks PI to PI transfers, and when a PI receives a mouse carrying a mutant allele or transgene " +
        "that is listed in the database from another PI, the recipient PI's name is automatically added to the list of " +
        "holders for that mouse." +
        "\n\n mouse carrying:  " + mouse.getMouseName() + ", database record number #" + mouseId + " " +
        "was recently transferred from " + nicelyFormattedCurrentHolder + "'s colony to your laboratory's colony." +
        "\n\n If you do not reply by " + twoWeeksFromNow + ", it will be assumed that it is OK to " +
        "list you as a holder of the mouse.","ISO-8859-1");
        }catch(Exception e){
          Log.Error("Failed to encode subject or body",e);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<span class='importAction'>Created change request <span class='changerequest_number'>#" 
            + "</span>" + request.getRequestID() +  ": Add "
            + nicelyFormattedAddedHolder + " to " + mouse.getMouseName() + " #" + mouseId + ".</span>  " +
                "(Transferred from " + nicelyFormattedCurrentHolder + "; ");
        sb.append("Recipient: " + recipientName + ")<br>");

        String emailLink = getMailToLink(recipientEmail, addedHolderEmail, subjectText, emailBodyText, "Email " + nicelyFormattedAddedHolder);

        sb.append(emailLink + rawRecord );
        DBConnect.updateChangeRequest(request.getRequestID(), "pending", request.getUserComment() + "<br>" + emailLink);
        newChangeRequests.add(sb.toString());
        ImportStatusTracker.AppendMessage(importTaskId, "Added change request #" + request.getRequestID() + " for mouse " + HTMLUtilities.getCommentForDisplay(mouse.getSource()));
      }
      else if (strain != null && strain.equalsIgnoreCase("paper form"))
      {
        paperFormImports.add("<span class='importAction'>Ignored paper form record transfer from "
            + nicelyFormattedCurrentHolder +  " to " + nicelyFormattedAddedHolder + "</span>" + rawRecord );
      }
      else if (strain != null && strain.isEmpty())
      {
        blankStrainImports.add("<span class='importAction'>Ignored blank strain name transfer from "
            + nicelyFormattedCurrentHolder +  " to " + nicelyFormattedAddedHolder + "</span>" + rawRecord );
      }
      else if (strain != null && strain.equalsIgnoreCase("not mutant"))
      {
        notMutantImports.add("<span class='importAction'>Ignored not mutant record transfer from "
            + nicelyFormattedCurrentHolder +  " to " + nicelyFormattedAddedHolder + "</span>" + rawRecord );
      }
      else
      {
        otherStrainImports.add("<span class='importAction'>Ignored strain " + strain + ", transfer from "
            + nicelyFormattedCurrentHolder +  " to " + nicelyFormattedAddedHolder + "</span>" + rawRecord );
      }

    } //end for each csv data record
    StringBuilder sb = new StringBuilder();

    if (csvData.size() > 0)
    {
      buildReport(sb,"Newly Created Change Requests",newChangeRequests);
      buildReport(sb,"Duplicates (No Change Request Created)",skippedChangeRequests);
      buildReport(sb,"Paper Form Imports", paperFormImports);
      buildReport(sb,"Blank Strain names", blankStrainImports);
      buildReport(sb,"Not to be listed", notMutantImports);
      buildReport(sb,"Strain names without record number", otherStrainImports);
      buildReport(sb,"Invalid imports", invalidImports);
    }
    else
    {
      sb.append("<h3>No records found in file.  If you are sure the file is not empty, please send file to the developer to examine</h3>");
    }






    ImportReport newReport = new ImportReport();
    newReport.setImportType(ImportObjectType.PPTCHANGEREQUEST);
    newReport.setNewObjectIds(newChangeRequestIds);
    newReport.setName(reportName);
    newReport.setReportText(sb.toString());

    int reportId = DBConnect.insertImportReport(newReport);

    ImportStatusTracker.AppendMessage(importTaskId, "Import complete.  Report #" +reportId);
    ImportStatusTracker.UpdateStatus(importTaskId, ImportStatus.COMPLETED);
  }

  private static void handleCombinedImport(int importTaskId,ArrayList<HashMap<String, String>> csvData,
      ImportDefinition importDefinition, HashMap<String, String> parameters) throws Exception
  {
    DateFormat dateFormat = new SimpleDateFormat("MMMM dd");
    String reportName = "";
    if(parameters.containsKey(ImportServlet.importDescriptionFieldName))
    {
      reportName += parameters.get(ImportServlet.importDescriptionFieldName);
    }
    else
    {
      reportName += importDefinition.Name + " " + dateFormat.format(new Date());
    }

    ImportStatusTracker.UpdateTitle(importTaskId, reportName);
    ImportStatusTracker.UpdateStatus(importTaskId, ImportStatus.PROCESSING);



    MGIResult test = MGIConnect.DoMGIAlleleQuery("3052827");
    if (test.isMgiConnectionTimedout() || test.isMgiOffline())
    {
      ImportStatusTracker.AppendMessage(importTaskId,"Failed to connect to MGI, try again later");
      ImportStatusTracker.UpdateStatus(importTaskId,ImportStatus.ERROR);
      return;
    }

    MmrrcConnect mmrrcData = null;


    String recipientPiNameCol = "pi (recipient)";

    String sourceCol = "vendor";
    String jaxMouseIdCol = "strain";
    String mgiMouseIdCol = "mgi id";

    String roomNameCol = "final destination";

    String recipientPIEmailCol = "recipient pi email";
    String purchaserNameCol = "purchaser name";
    String purchaserEmailCol = "purchaser email";
    String senderInformationCol = "sender institution";
    String officialSymbolCol = "official symbol";
    String notesCol = "notes";
    String pmidCol = "pmid (if published)";
    String recipientNameCol = "recipient name";
    String recipientEmailCol = "recipient email";
    String publishedCol = "published y/n";
    
    String facilityCodeRegex = "(.*)-[0-9]+";

    //List<Integer> purchasedMGIs = new ArrayList<Integer>();
    HashMap<Integer,ArrayList<PurchaseInfo>> purchasesByMgi = new HashMap<Integer, ArrayList<PurchaseInfo>>();
    List<PurchaseInfo> purchases = new ArrayList<PurchaseInfo>();
    HashMap<Integer,MouseSubmission> successfulSubmissions = new HashMap<Integer,MouseSubmission>();

    ImportStatusTracker.UpdateHeader(importTaskId, "Reading CSV data");
    for(HashMap<String,String> record : csvData)
    {
      PurchaseInfo purchase = new PurchaseInfo();

      purchase.source = record.get(sourceCol);
      purchase.strain = extractStockNumber(purchase.source, record.get(jaxMouseIdCol));

      purchase.holderName = record.get(recipientPiNameCol);
      purchase.holderEmail = record.get(recipientPIEmailCol);

      purchase.roomName = record.get(roomNameCol);
      purchase.senderInstitution = record.get(senderInformationCol);
      purchase.officialSymbol = record.get(officialSymbolCol);
      purchase.notes = record.get(notesCol);
      String rawMgi = record.get(mgiMouseIdCol);
      purchase.pmid = record.get(pmidCol);
      purchase.recipientName = record.get(recipientNameCol);
      purchase.recipientEmail = record.get(recipientEmailCol);
      purchase.published = record.get(publishedCol) != null && record.get(publishedCol).equalsIgnoreCase("y");
       
      String mgiNumber = HTMLUtilities.extractFirstGroup("([0-9]+)", rawMgi);
      
      purchase.mgiId = toInt(mgiNumber);
      purchase.catalogMgiIds = new ArrayList<Integer>();

      purchase.purchaserName = record.get(purchaserNameCol);
      purchase.purchaserEmail = record.get(purchaserEmailCol);

      purchase.rawRecord = "<span class='rawRecord'><br>Raw data:";
      for (Object key : record.keySet())
      {
        purchase.rawRecord += key + "=" + record.get(key) + " ";
      }
      purchase.rawRecord += "</span>";

      //filter out duplicate purchase of same mouse by same PI
      boolean isDuplicate = false;
      if (importDefinition.Id == 2 && rawMgi != null && rawMgi.trim().equalsIgnoreCase("duplicate")){
        isDuplicate = true;
      }
      for (PurchaseInfo exisitingPurchase : purchases)
      {
        if (exisitingPurchase.holderName.equals(purchase.holderName)
            && exisitingPurchase.holderEmail.equals(purchase.holderEmail)
            && exisitingPurchase.source != null
            && exisitingPurchase.source.equals(purchase.source)
            && exisitingPurchase.strain != null
            && exisitingPurchase.strain.equals(purchase.strain)
            && exisitingPurchase.mgiId == purchase.mgiId)
        {
            isDuplicate = true;
            ImportStatusTracker.AppendMessage(importTaskId, "Filtered duplicate purchase of strain " + purchase.source + " " + purchase.strain + " for " + purchase.holderName);
            exisitingPurchase.rawRecord += purchase.rawRecord;
            break;
        }
      }
      if (isDuplicate)
      {
        continue;
      }

      purchases.add(purchase);
    }

    double purchaseNumber = 1;
    double numPurchases = purchases.size();
    NumberFormat formatter = NumberFormat.getNumberInstance();
    formatter.setMaximumFractionDigits(0);
    ImportStatusTracker.AppendMessage(importTaskId, "Done reading CSV data - found " + formatter.format(numPurchases) 
        + (importDefinition.Id == 1 ? " purchases " : " imports "));
    //TODO report status, now can estimate total based on # of purchases

    //Look up MGI ids in catalogs, and group purchases by common MGI IDs



    ImportStatusTracker.UpdateHeader(importTaskId, "Looking up MGI numbers from Catalog numbers (Task 1 of 3)");
    for (PurchaseInfo purchaseInfo : purchases)
    {
      ImportStatusTracker.SetProgress(importTaskId, purchaseNumber / numPurchases);

      purchaseNumber++;
      if (importDefinition.Id == 2 || (purchaseInfo.strain != null && !purchaseInfo.strain.equals("")))
      {
        if (purchaseInfo.source != null)
        {
          ArrayList<Integer> allMgiIds = null;
          if(purchaseInfo.source.equalsIgnoreCase(JacksonLaboratory))
          {
            allMgiIds = JaxMiceConnect.GetMGINumbersFromJax(purchaseInfo.strain);
          }
          else if(purchaseInfo.source != null && purchaseInfo.source.contains(Mmmrrc))
          {
            if (mmrrcData == null)
            {
              ImportStatusTracker.AppendMessage(importTaskId, "Downloading data dump from MMRRCC..");
              //lazy download MMRRC data for lookup - it is a ~7mb download that we would ideally cache nightly, but meh it is fast enough to just get on demand
              mmrrcData = new MmrrcConnect();
            }
            if (mmrrcData.isAvailable())
            {
              allMgiIds = mmrrcData.lookupStrain(purchaseInfo.strain);
            }
          }
          else
          {
            ImportStatusTracker.AppendMessage(importTaskId,"No rule to get MGI ID for source '" + purchaseInfo.source +"'");
          }

          if (allMgiIds != null)
          {
            if (allMgiIds.size() == 1)
            {
              purchaseInfo.catalogMgiIds = allMgiIds;
            }
            else
            {
              for(int mgiId : allMgiIds)
              {
                //multipleMgisLabel += "<dd>MGI: " + HTMLGeneration.formatMGI(Integer.toString(mgiId));
                ImportStatusTracker.AppendMessage(importTaskId, "Checking if catalog mgi number MGI:" + mgiId + " is valid");
                MGIResult result = MGIConnect.DoMGIAlleleQuery(Integer.toString(mgiId));
                if(result.isValid())
                {
                  purchaseInfo.catalogMgiIds.add(mgiId);
                  ImportStatusTracker.AppendMessage(importTaskId, "Valid!");
                }
                else
                {

                  ImportStatusTracker.AppendMessage(importTaskId, "Invalid, ignoring. Reason: " + result.getErrorString());
                }
              }
            }
          }
        }
        if (purchaseInfo.catalogMgiIds.size() == 0)
        {
          if (importDefinition.Id == 1){
            ImportStatusTracker.AppendMessage(importTaskId, "Unable to lookup MGI based on catalog information (source:'" + purchaseInfo.source + "', strain:'"
                +purchaseInfo.strain +"'); using manually entered MGI ID: " +purchaseInfo.mgiId);
          }
          else if (importDefinition.Id == 2){
            ImportStatusTracker.AppendMessage(importTaskId, "Adding manually entered MGI ID: " +purchaseInfo.mgiId);
          }
          purchaseInfo.catalogMgiIds.add(purchaseInfo.mgiId);
        }

        int mgiId = purchaseInfo.catalogMgiIds.get(0);
        if (purchaseInfo.catalogMgiIds.size() > 1)
        {
          mgiId = MultipleAlleleMiceMgiId;
        }

        if (!purchasesByMgi.containsKey(mgiId))
        {
          purchasesByMgi.put(mgiId, new ArrayList<PurchaseInfo>());
        }
        if (importDefinition.Id == 1){
          ImportStatusTracker.AppendMessage(importTaskId, purchaseInfo.source + " " + purchaseInfo.strain + " -> MGI:"+ mgiId);
        }
        purchasesByMgi.get(mgiId).add(purchaseInfo);
      }
    }


    if(purchasesByMgi.size() > 0)
    {
      HashMap<Integer,MouseSubmission> newSubs = MGIConnect.SubmissionFromMGI(purchasesByMgi.keySet(),importTaskId);
      for (int mgiId : newSubs.keySet())
      {
        if (newSubs.get(mgiId) != null)
        {
          successfulSubmissions.put(mgiId,newSubs.get(mgiId));
        }
      }
    }
    ImportStatusTracker.SetProgress(importTaskId, 0);
    ArrayList<String> newChangeRequests = new ArrayList<String>();
    ArrayList<String> newSubmissions = new ArrayList<String>();

    ArrayList<String> noActionTakenPurchases = new ArrayList<String>();

    ArrayList<String> duplicateHolders = new ArrayList<String>();
    ArrayList<String> duplicatePurchasers = new ArrayList<String>(); //todo rename/fix

    ArrayList<String> invalidMGIEntries = new ArrayList<String>();
    ArrayList<String> invalidPurchases = new ArrayList<String>();
    ArrayList<String> unpublishedTransfers = new ArrayList<String>();

    //HashMap<Integer,Integer> newSubmissionsByMgiId = new HashMap<Integer, Integer>();
    
    ArrayList<String> duplicateInvalidTransfers = new ArrayList<String>();

    dateFormat = new SimpleDateFormat("EEEE, MMMM dd");

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, 14);



    UserData submitterData = new UserData();
    submitterData.setFirstName("Database");
    submitterData.setLastName("Administrator");
    submitterData.setEmail(HTMLGeneration.AdminEmail);
    submitterData.setDepartment("database admin");
    submitterData.setTelephoneNumber(" ");

    List<Integer> subIds = new ArrayList<Integer>();
    List<Integer> requestIds = new ArrayList<Integer>();

    ImportStatusTracker.UpdateHeader(importTaskId, "Creating submisisons and change requests (Task 3 of 3)");
    purchaseNumber = 1;
    double numMgiIds = purchasesByMgi.size();
    for (int catalogMgiId : purchasesByMgi.keySet())
    {
      ArrayList<PurchaseInfo> currentPurchases = purchasesByMgi.get(catalogMgiId);

      ImportStatusTracker.SetProgress(importTaskId, purchaseNumber / numMgiIds);
      ImportStatusTracker.AppendMessage(importTaskId, "Processing " + (importDefinition.Id == 1 ? "purchase" : "import") + " of MGI:" + catalogMgiId);
      purchaseNumber++;

      if (catalogMgiId == MultipleAlleleMiceMgiId)
      {
        for (PurchaseInfo purchase : currentPurchases)
        {
          //don't do any submissions or change requests for mice where there are multiple alleles

          String multipleMgisLabel = "<dl>";
          for(int mgiId : purchase.catalogMgiIds)
          {
            multipleMgisLabel += "<dd>MGI: " + HTMLGeneration.formatMGI(Integer.toString(mgiId));
            ImportStatusTracker.AppendMessage(importTaskId, "Checking if catalog mgi number MGI:" + mgiId + " is valid");
            MGIResult result = MGIConnect.DoMGIAlleleQuery(Integer.toString(mgiId));
            if(result.isValid())
            {
              multipleMgisLabel += " " + HTMLUtilities.getCommentForDisplay(result.getSymbol())+ "</dd>"  ;
            }
            else
            {
              //this shouldn't happen, we already filtered out bad ones earlier
              multipleMgisLabel += " (not a valid Allele)</dd>";
            }
          }
          multipleMgisLabel += "</dl>";


          String subjectText = "Listing " + formatHolderName(purchase.holderName) +
            " as a holder of " + getPurchaseDescription(purchase) + " in the UCSF Mouse Database";
          String emailBodyText = getCombinedImportEmail(null,null, null, purchase, importDefinition);

          String emailLink = getMailToLink(importDefinition.Id == 1 ? purchase.purchaserEmail : purchase.recipientEmail, 
              purchase.holderEmail, subjectText, emailBodyText, "Email " + formatHolderName(purchase.holderName));

          String noAction = "<span class='importAction'>No action taken for purchase of "
              + getPurchaseDescription(purchase,true)  + "</span>";
          if (importDefinition.Id == 1){
            noAction += " purchased by " + formatHolderName(purchase.purchaserName);
          }
          
          if(importDefinition.Id == 2) {
            noAction += " imported from " + purchase.senderInstitution + " by " + formatHolderName(purchase.recipientName);
            noAction += (purchase.notes != null && !purchase.notes.isEmpty() ? "<br><b>Notes:</b> " + purchase.notes :"");
          }
          
          noActionTakenPurchases.add( noAction
            + " for " + formatHolderName(purchase.holderName) + " " +  emailLink + multipleMgisLabel + purchase.rawRecord );

          continue;
        }
      }
      else if (catalogMgiId == -1)
      {
        for (PurchaseInfo purchase : currentPurchases)
        {
          if (importDefinition.Id == 1){
            invalidPurchases.add("<span class='importAction'>Invalid purchase (No MGI IDs found)</span>.  Strain: " 
              + getPurchaseDescription(purchase,true) +
              ", purchased by " + formatHolderName(purchase.purchaserName)
              + " for " + formatHolderName(purchase.holderName) + purchase.rawRecord );
          } else if(importDefinition.Id == 2){
            if (duplicateInvalidTransfers.contains(purchase.strain + "_" + purchase.recipientEmail + "_" + purchase.holderEmail)) {
              continue;
            }
            String emailBodyText = getCombinedImportEmail(null,null,purchase.strain,purchase,importDefinition);
            String subjectText = "Listing " + formatHolderName(purchase.holderName) +
                " as a holder of " + purchase.strain + " in the UCSF Mouse Database";
            String emailLink = getMailToLink(importDefinition.Id == 1 ? purchase.purchaserEmail : purchase.recipientEmail, 
                    purchase.holderEmail, subjectText, emailBodyText, "Email " + formatHolderName(purchase.holderName));

            
            String blurb = "Strain: " + purchase.strain
                + ", imported from " + purchase.senderInstitution
                + " by " + formatHolderName(purchase.recipientName)
                + " for " + formatHolderName(purchase.holderName) + " " + emailLink + purchase.rawRecord 
                + (purchase.notes != null && !purchase.notes.isEmpty() ? "<br><b>Notes:</b> " + purchase.notes :"") ;
            
            
            if(purchase.published){
              invalidPurchases.add("<span class='importAction'>Invalid import (No MGI IDs found)</span>. " 
                  + " PMID: <span class='pubmed_number'>" + purchase.pmid + "</span>"
                  + blurb);
            }
            else {
              unpublishedTransfers.add("<span class='importAction'>Unpublished import</span>. " + blurb);
            }
            duplicateInvalidTransfers.add(purchase.strain + "_" + purchase.recipientEmail + "_" + purchase.holderEmail);
          }
        }

      }
      else if (!successfulSubmissions.containsKey(catalogMgiId))
      {
        for (PurchaseInfo purchase : currentPurchases)
        {
          if (importDefinition.Id == 1){
            invalidMGIEntries.add("<span class='importAction'>Ignored invalid (not an Allele) MGI ID: " + catalogMgiId
              + "</span>, purchase of " + getPurchaseDescription(purchase)  + " purchased by " + formatHolderName(purchase.purchaserName)
              + " for " + formatHolderName(purchase.holderName) + purchase.rawRecord );
          }else if (importDefinition.Id == 2){
            invalidMGIEntries.add("<span class='importAction'>Ignored invalid (not an Allele) MGI ID: " + catalogMgiId
                + "</span>, purchase of " + getPurchaseDescription(purchase)  + " imported from " + purchase.senderInstitution
                + " by " + formatHolderName(purchase.recipientName) + " for " + formatHolderName(purchase.holderName)
                + (purchase.notes != null && !purchase.notes.isEmpty() ? "<br><b>Notes:</b> " + purchase.notes :"") 
                + purchase.rawRecord );
          }
          
        }
        continue;
      }
      else
      {
        int exisitingRecordId = DBConnect.checkForDuplicates(catalogMgiId,-1);
        if (exisitingRecordId > 0)
        {
        for (PurchaseInfo purchase : currentPurchases)
        {
          purchase.exisitingRecordId = exisitingRecordId;

          if (purchase.catalogMgiIds.size() < 0)
          {
            if (importDefinition.Id == 1){
              invalidPurchases.add("<span class='importAction'>Invalid purchase (No MGI IDs found)</span>.  Strain: " +
                getPurchaseDescription(purchase) + ", purchased by " + formatHolderName(purchase.purchaserName)
                + " for " + formatHolderName(purchase.holderName) + purchase.rawRecord );
            } else if(importDefinition.Id == 2){
              invalidPurchases.add("<span class='importAction'>Invalid purchase (No MGI IDs found)</span>.  Strain: " +
                  getPurchaseDescription(purchase) + ", imported from " + purchase.senderInstitution 
                  + " by " + formatHolderName(purchase.recipientName) + " for " + formatHolderName(purchase.holderName) 
                  + (purchase.notes != null && !purchase.notes.isEmpty() ? "<br><b>Notes:</b> " + purchase.notes :"") 
                  + purchase.rawRecord );
            }
            continue;
          }


          //this record ready exists, add a change request to add the holder in each purchase to the record

          MouseRecord mouse = DBConnect.getMouseRecord(purchase.exisitingRecordId).get(0);

          Holder localAddedHolder = DBConnect.findHolderByEmail(purchase.holderEmail);
          Facility localAddedFacility = DBConnect.findFacilityByCode(HTMLUtilities.extractFirstGroup(facilityCodeRegex, purchase.roomName));
          boolean isDuplicate = false;
          if (localAddedHolder == null)
          {
            Log.Error("Failed to look up holder with email " + purchase.holderEmail);

          }
          else
          {
            Log.Info("Holder " + purchase.holderName + " (" + purchase.holderEmail + ") recognized as holder #"
                + localAddedHolder.getHolderID());


            for (MouseHolder mouseHolder : mouse.getHolders())
            {
              if (mouseHolder.getHolderID() == localAddedHolder.getHolderID())
              {
                Log.Info("Duplicate - " + purchase.holderName + " (" + purchase.holderEmail + ") already listed as holder of mouse #" + mouse.getMouseID());

                if (importDefinition.Id == 1){
                  duplicateHolders.add("<span class='importAction'>Ignored duplicate MGI:" + catalogMgiId + "</span>, purchase of "
                      + mouse.getSource()  +  " purchased by " + formatHolderName(purchase.purchaserName) + " for  " +
                      formatHolderName(purchase.holderName) + " (already a holder of record #" + mouse.getMouseID() + ")"+ purchase.rawRecord );
                } else if (importDefinition.Id == 2){
                  duplicateHolders.add("<span class='importAction'>Ignored duplicate MGI:" + catalogMgiId + "</span>, "
                      + mouse.getSource()  + " imported from " + purchase.senderInstitution 
                      + " by " + formatHolderName(purchase.recipientName) + " for  " +
                      formatHolderName(purchase.holderName) + " (already a holder of record #" + mouse.getMouseID() + ")"
                      + (purchase.notes != null && !purchase.notes.isEmpty() ? "<br><b>Notes:</b> " + purchase.notes :"")
                      + purchase.rawRecord );
                }
                isDuplicate = true;
                break;
              }
            }
            if (isDuplicate)
            {
              continue;
            }
          }
          Properties props = new Properties();
          props.setProperty("Request Source", importDefinition.Name);

          //TODO map room name to facility


          props.setProperty("New Holder Email", purchase.holderEmail);
          if (importDefinition.Id == 1){
            props.setProperty("Purchaser", purchase.purchaserName);
            props.setProperty("Purchaser email", purchase.purchaserEmail);
          }
          else if (importDefinition.Id == 2){
            props.setProperty("Sender institution", purchase.senderInstitution);
            props.setProperty("Recipient", purchase.recipientName);
            props.setProperty("Recipient Email", purchase.recipientEmail);
            props.setProperty("Import Notes",purchase.notes != null && !purchase.notes.isEmpty() ? purchase.notes :"");
          }
          props.setProperty("MouseMGIID", Integer.toString(catalogMgiId));
          props.setProperty("CatalogNumber",purchase.strain);

          ChangeRequest request = createChangeRequest(Integer.parseInt(mouse.getMouseID()), purchase.holderEmail, purchase.holderName,
              localAddedHolder, localAddedFacility, purchase.holderName, purchase.roomName, props);

          int requestId = request.getRequestID();
          requestIds.add(requestId);
          Log.Info("Created change request #" + requestId + " to add holder " + purchase.holderName + " to record " + mouse.getMouseID() + " for purchase of MGI ID " + catalogMgiId);

          StringBuilder sb = new StringBuilder();
          sb.append("<span class='importAction'>Created change request <span class='changerequest_number'>#" 
              + requestId +  "</span>: Add "
              + formatHolderName(purchase.holderName) + " to " + mouse.getMouseName() + " #"
              + mouse.getMouseID() + ".</span>  ");
          if(importDefinition.Id == 1) {
              sb.append("(purchased by " + formatHolderName(purchase.purchaserName) + ") ");
          }
          else if(importDefinition.Id == 2) {
            sb.append("(imported from " + purchase.senderInstitution + " by " + formatHolderName(purchase.recipientName)+ ") ");
          }

          String subjectText = "Listing " + formatHolderName(purchase.holderName) +
          " as a holder of " + mouse.getSource() + ", record number " + mouse.getMouseID() + ", in the UCSF Mouse Database";
          String

          emailBodyText = getCombinedImportEmail(mouse.getMouseID(),mouse.getOfficialMouseName(),mouse.getSource(),purchase,importDefinition);

          String emailLink = getMailToLink(importDefinition.Id == 1 ? purchase.purchaserEmail : purchase.recipientEmail, 
                  purchase.holderEmail, subjectText, emailBodyText, "Email " + formatHolderName(purchase.holderName));

          sb.append(emailLink + purchase.rawRecord );
          DBConnect.updateChangeRequest(request.getRequestID(), "pending", request.getUserComment() + "<br>" + emailLink);
          newChangeRequests.add(sb.toString());

          continue;
        }
        }
        else
        {

          PurchaseInfo firstPurchase = currentPurchases.get(0);

          MouseSubmission sub = successfulSubmissions.get(catalogMgiId);

          String officialSymbol = sub.getOfficialSymbol();
          if (officialSymbol != null && !(officialSymbol.contains("<tm") || officialSymbol.contains("Tg(")))
          {
            for (PurchaseInfo purchase : currentPurchases) {
              if (importDefinition.Id == 1){
                noActionTakenPurchases.add("<span class='importAction'>No action taken for mouse with suspect symbol:</span> purchase of "
                  + getPurchaseDescription(purchase,true)  + " MGI: " + HTMLGeneration.formatMGI(Integer.toString(catalogMgiId)) + " " 
                  + HTMLUtilities.getCommentForDisplay(officialSymbol) + " purchased by " + formatHolderName(purchase.purchaserName)
                  + " for " + formatHolderName(purchase.holderName)
                  +  purchase.rawRecord );
              } else if(importDefinition.Id == 2){
                noActionTakenPurchases.add("<span class='importAction'>No action taken for mouse with suspect symbol:</span> purchase of "
                  + getPurchaseDescription(purchase,true)  + " MGI: " + HTMLGeneration.formatMGI(Integer.toString(catalogMgiId)) + " " 
                  + HTMLUtilities.getCommentForDisplay(officialSymbol) + " imported from " + purchase.senderInstitution
                  + " by " + formatHolderName(purchase.recipientName) + " for " + formatHolderName(purchase.holderName)
                  + (purchase.notes != null && !purchase.notes.isEmpty() ? "<br><b>Notes:</b> " + purchase.notes :"")
                  +  purchase.rawRecord );
              }
            }

            continue;
          }


          //String facilityCode = HTMLUtilities.extractFirstGroup(facilityCodeRegex, firstPurchase.roomName);

          //Facility localAddedFacility = DBConnect.findFacilityByCode(facilityCode);
          //sub.setHolderFacility(localAddedFacility == null ? firstPurchase.roomName : HTMLGeneration.emptyIfNull(localAddedFacility.getFacilityName()));
          //sub.setHolderName(firstPurchase.holderName);
          //sub.setComment(sub.getComment() + "\r\n\r\n*Genetic Background Info:* " + purchase.geneticBackground);
          if (sub.getMouseType() == "Inbred Strain")
          {
            sub.setISSupplier(getMouseSourceShortName(firstPurchase.source));
            sub.setISSupplierCatalogNumber(firstPurchase.strain);
          }

          Properties props = MouseSubmission.GetPropertiesString(submitterData,sub);

          ArrayList<String> holderFacilities = new ArrayList<String>();
          String additionalHoldersComment = "";
          String allPurchasedHoldersComment = "";
          props.setProperty("holderCount", Integer.toString(currentPurchases.size()));
          for(int i = 0; i < currentPurchases.size(); i++)
          {
            PurchaseInfo nextPurchase = currentPurchases.get(i);
            String nextFacilityCode = HTMLUtilities.extractFirstGroup(facilityCodeRegex, nextPurchase.roomName);
            Facility nextLocalAddedFacility = DBConnect.findFacilityByCode(nextFacilityCode);

            String facilityName = "";
            if (nextLocalAddedFacility != null)
            {
              facilityName =  HTMLGeneration.emptyIfNull(nextLocalAddedFacility.getFacilityName());
            }
            else
            {
              facilityName = nextPurchase.roomName;
            }

            props.setProperty("Recipient PI Name-" + i, nextPurchase.holderName);
            props.setProperty("Recipient Facility-" + i, facilityName);
            if (importDefinition.Id == 1){
              props.setProperty("Purchaser-" + i,nextPurchase.purchaserName);
              props.setProperty("Purchaser email-" + i, nextPurchase.purchaserEmail);
            }
            else if (importDefinition.Id == 2){
              props.setProperty("Sender institution-" +i, nextPurchase.senderInstitution);
              props.setProperty("Recipient-" + i, nextPurchase.recipientName);
              props.setProperty("Recipient Email-" + i, nextPurchase.recipientEmail);
              props.setProperty("Import notes-" + i, (nextPurchase.notes != null && !nextPurchase.notes.isEmpty() ? nextPurchase.notes :""));
            }
            props.setProperty("New Holder Email-" + i,nextPurchase.holderEmail);

            allPurchasedHoldersComment += "\r\n*Purchased by " + nextPurchase.holderName + "(" + nextPurchase.holderEmail + ") " + nextPurchase.roomName + "*";

            Holder localAddedHolder = DBConnect.findHolderByEmail(nextPurchase.holderEmail);
            String facilityCode = HTMLUtilities.extractFirstGroup(facilityCodeRegex, nextPurchase.roomName);
            Facility localAddedFacility = DBConnect.findFacilityByCode(facilityCode);

            if (localAddedHolder != null && localAddedFacility != null)
            {
              holderFacilities.add(localAddedHolder.getHolderID() + "-" + localAddedFacility.getFacilityID());
            }
            else
            {
              holderFacilities.add("0-0");
              additionalHoldersComment += "\r\n*" + (importDefinition.Id == 1 ? "Purchased" : "Imported") + " by unrecognized holder: " + nextPurchase.holderName + " (" + nextPurchase.holderEmail + ") - " + facilityName + "*";
            }

          }


          if (holderFacilities.size() > 0)
          {
            StringBuilder sb = new StringBuilder();
            for (String holderAndFacilityId : holderFacilities)
            {
              if (sb.length() > 0) { sb.append(","); }
              sb.append(holderAndFacilityId);
            }
            props.setProperty("HolderFacilityList", sb.toString());
          }

          props.setProperty("NewMouseName", sub.getOfficialSymbol() + ", " + sub.getOfficialMouseName());
          props.setProperty("MouseMGIID", Integer.toString(catalogMgiId));
          props.setProperty("CatalogNumber",firstPurchase.strain);

          props.setProperty(SubmittedMouse.SubmissionSourceKey, importDefinition.Id == 1 ? SubmittedMouse.PurchaseImport : SubmittedMouse.OtherInstitutionImport);

          StringBuilder sb = new StringBuilder();


          //See if there is already an open submission for this MGI ID
          int submissionID = DBConnect.submissionExists(catalogMgiId);
          //if there is, just add the holders and use that sub iD
          if (submissionID > 0)
          {
            SubmittedMouse exisitingSubmission = DBConnect.getMouseSubmission(submissionID).get(0);
            Properties exisitingProps = exisitingSubmission.getProperties();
            ArrayList<MouseHolder> exisitingHolders = exisitingSubmission.getHolders();
            if (exisitingHolders == null)
            {
              exisitingProps.setProperty("HolderFacilityList", props.getProperty("HolderFacilityList"));
              StringBuffer propsBuf = new StringBuffer();
                  for (Object name : exisitingProps.keySet())
                  {
                      propsBuf.append(name + "=" + exisitingProps.get(name) + "\t");
                  }
                  DBConnect.updateSubmissionProperties(submissionID, propsBuf.toString());
                  DBConnect.updateSubmission(submissionID,"new","Added additional holder(s) from " + importDefinition.Name + "\r\n" + additionalHoldersComment);
              sb.append("<span class='importAction'>Added holder(s) to open submission <span class='submission_number'>#" 
                  + submissionID +  "</span>:   " +
                  HTMLUtilities.getCommentForDisplay(sub.getOfficialSymbol()) + "</span><dl>");
            }
            else {
              //TODO properly merge exisiting holders on the submission with the new ones
              //for don't do anything fancy/automatically but make a note
              DBConnect.updateSubmission(submissionID,"new","Added additional holder(s) from " + importDefinition.Name + "\r\n" + allPurchasedHoldersComment);
              sb.append("<span class='importAction'>Added note to add holder(s) to open automatically-generated submission <span class='changerequest_number'>#" 
              + submissionID +  "</span>:   " +  HTMLUtilities.getCommentForDisplay(sub.getOfficialSymbol()) + "</span><dl>");
            }
          }
          else {
            submissionID = DBConnect.insertSubmission(submitterData,sub,props);
            DBConnect.updateSubmission(submissionID,"new","Auto-generated from " + importDefinition.Name + "\r\n" + additionalHoldersComment);

            sb.append("<span class='importAction'>Created submission <span class='submission_number'>#" 
            + submissionID +  "</span>:   " +
                HTMLUtilities.getCommentForDisplay(sub.getOfficialSymbol()) + "</span><dl>");
          }
          subIds.add(submissionID);
          //newSubmissionsByMgiId.put(catalogMgiId, submissionID);

          for (PurchaseInfo purchaseInfo : currentPurchases)
          {
            sb.append("<dd>");
            if (importDefinition.Id == 1){
              sb.append("Purchased from " + getPurchaseDescription(purchaseInfo)
                  + " by " + formatHolderName(purchaseInfo.purchaserName));
            }
            else if (importDefinition.Id == 2){
              sb.append("Imported from " + purchaseInfo.senderInstitution 
                  + " by " + formatHolderName(purchaseInfo.recipientName));
            }
            sb.append(" for " + formatHolderName(purchaseInfo.holderName) + " ");
            String subjectText = "Listing " + formatHolderName(purchaseInfo.holderName) +
            " as a holder of " + sub.getOfficialSymbol() + " in the UCSF Mouse Database";
            String emailBodyText = getCombinedImportEmail(null,sub.getOfficialMouseName(),sub.getOfficialSymbol(),purchaseInfo, importDefinition);

            String emailLink = getMailToLink(importDefinition.Id == 1 ? purchaseInfo.purchaserEmail : purchaseInfo.recipientEmail,
                            purchaseInfo.holderEmail, subjectText, emailBodyText, "Email " + formatHolderName(purchaseInfo.holderName));

            sb.append(emailLink 
                + (purchaseInfo.notes != null && !purchaseInfo.notes.isEmpty() ? "<br><b>Notes:</b> " + purchaseInfo.notes :"")
                + "</dd>"
                + purchaseInfo.rawRecord );
          }


          newSubmissions.add(sb.toString());
        }
      }// for each purchase for this MGI ID
    }//for each MGI ID

    StringBuilder submissionReport = new StringBuilder();
    StringBuilder changeRequestReport = new StringBuilder();

    if (csvData.size() > 0)
    {
      buildReport(submissionReport,"Newly Created Submissions",newSubmissions);
      buildReport(changeRequestReport,"Newly Created Change Requests",newChangeRequests);
      buildReport(submissionReport, "Duplicate Purchasers", duplicatePurchasers);
      buildReport(submissionReport, "No Action Taken", noActionTakenPurchases);

      buildReport(changeRequestReport,"Duplicates", duplicateHolders);
      buildReport(submissionReport,"Invalid MGI IDs", invalidMGIEntries);
      buildReport(submissionReport, importDefinition.Id == 1 ? "Invalid purchases" : "Invalid imports",invalidPurchases);
      if (importDefinition.Id == 2){
        buildReport(submissionReport, "Unpublished imports",unpublishedTransfers);
      }
    }
    else
    {
      submissionReport.append("<h3>No records found in file.  If you are sure the file is not empty, please send file to the developer to examine</h3>");
    }




    ImportReport newReport = new ImportReport();
    newReport.setImportType((importDefinition.Id == 1 ? ImportObjectType.PURCHASESUBMISSION : ImportObjectType.OTHERINSTITUTIONSUBMISSION));
    newReport.setNewObjectIds(subIds);
    newReport.setName(reportName);
    newReport.setReportText(submissionReport.toString());

    int reportId = DBConnect.insertImportReport(newReport);

    if (csvData.size() > 0)
    {
      newReport = new ImportReport();
      newReport.setImportType((importDefinition.Id == 1 ? ImportObjectType.PURCHASECHANGEREQUEST : ImportObjectType.OTHERINSTITUTIONSCHANGEREQUEST));
      newReport.setNewObjectIds(requestIds);
      newReport.setName(reportName);
      newReport.setReportText(changeRequestReport.toString());

      DBConnect.insertImportReport(newReport);
    }
    ImportStatusTracker.AppendMessage(importTaskId, "Import complete.  Report #" +reportId);
    ImportStatusTracker.UpdateHeader(importTaskId, "");
    ImportStatusTracker.UpdateStatus(importTaskId, ImportStatus.COMPLETED);
    //todo add report Id to import task status

    return;
  }
  
  private static String getCombinedImportEmail(String mouseID, String officialMouseName, String officialSymbol, 
                                               PurchaseInfo purchase,  ImportDefinition definition){

    String action = definition.Id == 1 ? "purchase" : "import";
    String action_past = definition.Id == 1 ? "purchased" : "imported";
    String pi_desc = definition.Id == 1 ? "purchasing PI's name" : "name of the PI who imported it";
    String mouseDescription = getPurchaseDescription(purchase);
    
    StringBuilder emailBodyText = new StringBuilder();
    emailBodyText.append(
      "In an effort to keep the UCSF mouse inventory database up-to-date, we have implemented a system that tracks " +
      "mouse " + action + "s.  ");
    if (definition.Id == 1 || purchase.published){
      emailBodyText.append("When a mouse carrying a published mutant allele or transgene that is ");
      emailBodyText.append(mouseID != null ? "already listed " : "not yet listed ");
      emailBodyText.append("in the database is " +  action_past + ", ");
      if (mouseID != null){
        emailBodyText.append("the " + pi_desc + " can be added to the list of holders for that mouse.");
      }
      else {
       emailBodyText.append("a new database record can be created with the PI who " + action_past + " it listed as a holder"); 
      }
    }
    emailBodyText.append("\n\nA mouse (mice) described as " + officialSymbol + ", ");
    if (officialMouseName != null){
      emailBodyText.append(officialMouseName + ", ");
    }
    if (mouseID != null){
      emailBodyText.append("which is described in record number " + mouseID + " in the database, ");
    }
    emailBodyText.append("was " + action_past + " from ");
    emailBodyText.append(definition.Id == 1 ? mouseDescription : purchase.senderInstitution);
    emailBodyText.append( ", by " + formatHolderName((definition.Id == 1 ? purchase.purchaserName : purchase.recipientName)));
    emailBodyText.append(" for the " + holderLastName(purchase.holderName) +  " lab.");
    if (definition.Id == 2 && !purchase.published){
      emailBodyText.append("\n\nWe would like to list all alleles and transgenes that are imported into the UCSF barrier in the database, even if they have not yet been published, and are therefore writing to ask if you would be willing to provide the information necessary to create a record(s) in the database for the allele(s) or transgene(s) carried by the mice you imported.");
    }
    try {
      return URLEncoder.encode(emailBodyText.toString(),"ISO-8859-1");
    } catch (UnsupportedEncodingException e) {
      Log.Error("Failed to encode email body text",e);
      return "Error.";
    }
  }


  private static String getMailToLink(String address, String cc, String subject, String body, String linkText)
  {
    String ccAddr = "?";
    if (cc != null && !cc.equals(address))
    {
      ccAddr = "?cc=" + cc + "&";
    }
    if (cc != null && address == null){
      address = cc;
      ccAddr = "?";
    }
    return "<a href=\"mailto:" + address + ccAddr + "subject=" + subject + "&body=" + body + "\">" + linkText + "</a>";
  }


  private static void buildReport(StringBuilder sb, String requestType, ArrayList<String> requests)
  {
    if (requests.size() <= 0)
    {
      return;
    }
    sb.append("<h3>" + requestType + " (" + requests.size() + ")</h3>");
    sb.append("<div class='reportBody'>\r\n");
    boolean alternate = true;
    for (String request : requests)
    {
      String cssStyle = alternate ? "reportEntry" : "reportEntryAlt";

      sb.append("<div class='" + cssStyle + "'>" + request + "</div>");
      alternate = !alternate;
    }
    if (requests.size() <=0)
    {
      sb.append("<p>(None)</p>");
    }

    sb.append("</div>\r\n");
    sb.append("<br>");

  }

  private static String formatHolderName(String holderName)
  {
    if (holderName == null)
    {
      return "";
    }
    //from Ansel, K. Mark to Mark Ansel
    //OR
    //Bluestone, Jeffrey A. to Jeffrey Bluestone
    //OR
    //Bruneau, Benoit to Benoit Bruneau
    String[] tokens = holderName.split(",");
    if (tokens.length != 2)
    {
      return holderName;
    }
    String lastname = tokens[0].trim();
    String firstname = tokens[1].trim();

    int periodIndex = firstname.indexOf('.');

    if (periodIndex == 1)
    {
      firstname = firstname.substring(periodIndex + 1).trim();
    }
    else if (periodIndex == firstname.length()-1)
    {
      firstname = firstname.substring(0,periodIndex - 2).trim();
    }

    return firstname + " " + lastname;
  }

  private static String holderLastName(String holderName)
  {
    if (holderName == null)
    {
      return "";
    }
    //from Ansel, K. Mark to Ansel
    //OR
    //Bluestone, Jeffrey A. to Bluestone
    //OR
    //Bruneau, Benoit to Bruneau
    String[] tokens = holderName.split(",");
    if (tokens.length != 2)
    {
      return holderName;
    }
    String lastname = tokens[0].trim();

    return lastname;
  }


  private static ChangeRequest createChangeRequest(int mouseId, String requestorEmail, String requestorName, Holder holderToAdd, Facility facilityToAdd, String addedHolder, String addedFacility, Properties extraProps)
  {
    //create a new change request
    ChangeRequest newRequest = new ChangeRequest();
    newRequest.setMouseID(mouseId);
    newRequest.setEmail(requestorEmail);

    String[] tokens = requestorName.split(",");
    if (tokens.length == 2)
    {
      newRequest.setFirstname(tokens[1].trim());
      newRequest.setLastname(tokens[0].trim());
    }
    else
    {
      newRequest.setFirstname("");
      newRequest.setLastname(requestorName);
    }

    newRequest.setStatus("pending");
    newRequest.setUserComment("Auto-generated change request");
    Properties props = new Properties();

    String addHolderPropertyValue = (holderToAdd != null ?
        Integer.toString(holderToAdd.getHolderID()) + "|" : "") +
        addedHolder;

    String addFacilityPropertyValue = (facilityToAdd != null ?
        Integer.toString(facilityToAdd.getFacilityID()) + "|" : "") +
        addedFacility;


    props.setProperty("Add Holder", addHolderPropertyValue);
    props.setProperty("Add Facility", addFacilityPropertyValue);
    props.setProperty("Recipient PI Name", addedHolder);

    for(Object propName : extraProps.keySet())
    {
      props.setProperty(propName.toString(), extraProps.getProperty(propName.toString()));
    }

    newRequest.setProperties(props);

    int requestId = DBConnect.insertChangeRequest(newRequest);
    Log.Info("ImportHandler created new change request # " + requestId);
    newRequest.setRequestID(requestId);
    return newRequest;

  }


  private static int toInt(String string)
  {
    if (string == null)
    {
      return -1;
    }
    try
    {
      return Integer.parseInt(string);
    }
    catch (NumberFormatException e)
    {
      return -1;
    }

  }

  private static String getPurchaseDescription(PurchaseInfo purchase)
  {
    return getPurchaseDescription(purchase, false);
  }


  private static String getPurchaseDescription(PurchaseInfo purchase, boolean generateLink)
  {

    String description = "";
    if(purchase.source != null){
      String shortName = getMouseSourceShortName(purchase.source) + ",";
      String catalogDescription = purchase.strain;
      try
      {
        Integer.parseInt(purchase.strain);
        catalogDescription = " catalog #" + purchase.strain;
      }
      catch (Exception e) {
  
      }
  
      description += shortName + catalogDescription;
    }
    else {
      description += purchase.officialSymbol;
    }
    if (generateLink)
    {
      if (purchase.source != null && purchase.source.equals(JacksonLaboratory))
      {
        String jaxUrl = "http://jaxmice.jax.org/strain/";
        String jaxUrlTail = ".html";

        String fixedUrl = jaxUrl + purchase.strain + jaxUrlTail;

        description = "<a class=\"MP\" target=\"_blank\" href='" + fixedUrl + "'>"
        + description + "</a>";
      }
    }

    return description;

  }

  private static String getMouseSourceShortName(String longName)
  {
    String shortName = null;
    if (longName != null)
    {
      if (longName.equalsIgnoreCase(JacksonLaboratory))
      {
        shortName = JaxMice;
      }
      else
      {
        shortName = longName;
      }
    }
    else
    {
      shortName = "";
    }
    return shortName;
  }

  private static String extractStockNumber(String supplier, String strainInfo)
  {
    if (supplier == null)
    {
      return strainInfo;
    }

    if (supplier.equalsIgnoreCase(JacksonLaboratory))
    {
      String extracted = HTMLUtilities.extractFirstGroup("([\\d]{6})", strainInfo);
      if (extracted == null || extracted.isEmpty())
      {
        return strainInfo;
      }
      return extracted;
    }
    else if (supplier.contains(Mmmrrc))
    {
      //032908-UCD
      String extracted = HTMLUtilities.extractFirstGroup("([\\d]{6}-[\\w]{2,3})", strainInfo);
      if (extracted == null || extracted.isEmpty())
      {
        return strainInfo;
      }
      return extracted;
    }


    return strainInfo;
  }

  private static class PurchaseInfo
  {

    public String source;
    public String strain;

    public String holderName;
    public String holderEmail;
    public String roomName;

    public String purchaserName;
    public String purchaserEmail;
    public String senderInstitution;
    public String officialSymbol;
    public String notes;
    public String pmid;
    public String recipientName;
    public String recipientEmail;
    public boolean published;
    
    public int mgiId;
    public ArrayList<Integer> catalogMgiIds;

    public String rawRecord;

    public int exisitingRecordId;
  }
}
