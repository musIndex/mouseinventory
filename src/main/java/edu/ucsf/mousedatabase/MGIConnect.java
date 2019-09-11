package edu.ucsf.mousedatabase;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.lang.Thread;

import edu.ucsf.mousedatabase.beans.MouseSubmission;
import edu.ucsf.mousedatabase.dataimport.ImportStatusTracker;
import edu.ucsf.mousedatabase.dataimport.ImportStatusTracker.ImportStatus;
import edu.ucsf.mousedatabase.objects.MGIResult;

public class MGIConnect {

  //todo get these from the mgi database, they shouldn't be static
  public static int MGI_MARKER = 2;
  public static int MGI_ALLELE = 11;
  public static int MGI_REFERENCE = 1;

  public static int MGI_MARKER_OTHER_GENOME_FEATURE = 9;

  public static final String pmDBurl = "http://www.ncbi.nlm.nih.gov/pubmed/";
  public static final String pmDBurlTail = "?dopt=Abstract";
  public static final String mgiDBurl = "http://www.informatics.jax.org/accession/MGI:";

  private static String databaseConnectionString;
  private static String databaseDriverName;

  private static boolean initialized = false;

  public static boolean verbose = false;

  public static boolean Initialize(String databaseDriverName, String databaseConnectionString) {
    if (initialized) {
      return false;
    }
    MGIConnect.databaseDriverName = databaseDriverName;
    MGIConnect.databaseConnectionString = databaseConnectionString;
    initialized = true;
    return true;
  }

  // TODO make this method public and make callers figure out typeIDs
  public static MGIResult doMGIQuery(String accessionID, int expectedTypeID, String wrongTypeString) {
    return doMGIQuery(accessionID, expectedTypeID, wrongTypeString, true);
  }

  public static MGIResult doMGIQuery(String accessionID, int expectedTypeID, String wrongTypeString,
      boolean offlineOK) {

    MGIResult result = new MGIResult();
    result.setAccessionID(accessionID);
    result.setType(expectedTypeID);
    String query = "";
    Connection connection = null;

    String accID = accessionID;
    if (expectedTypeID != MGI_REFERENCE) {
      accID = "MGI:" + accessionID;
    }

    try {
      connection = connect();
      query = "select _Accession_key, ACC_Accession._MGIType_key, primaryKeyName, _Object_key, tableName "
          + "from ACC_Accession left join ACC_MGIType on ACC_Accession._MGIType_key=ACC_MGIType._MGIType_key "
          + "where accID='" + accID + "' " + "and ACC_Accession._MGIType_key in(" + MGI_MARKER + "," + MGI_ALLELE + ","
          + MGI_REFERENCE + ")";
      // the last line above is kind of a hack because sometimes you get multiple
      // results for accession ids, such as evidence types

      java.sql.Statement stmt = connection.createStatement();
      if (verbose) System.out.println(query);
      ResultSet rs = stmt.executeQuery(query);

      if (rs.next()) {
        // int accessionKey = rs.getInt("_Accession_key");
        int mgiTypeKey = rs.getInt("_MGIType_key");
        String primaryKeyName = rs.getString("primaryKeyName");
        int objectKey = rs.getInt("_Object_key");
        String tableName = rs.getString("tableName");

        if (mgiTypeKey != expectedTypeID) // TODO lookup type id, don't hard code it
        {
          if (verbose) System.out.println("type key mismatch! " + mgiTypeKey + " != " + expectedTypeID);
          // see if this is a possible other genome feature issue
          if (mgiTypeKey == MGI_MARKER && expectedTypeID == MGI_ALLELE) {
            query = "select * from " + tableName + " where " + primaryKeyName + "=" + objectKey;
            if (verbose) System.out.println(query);
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next() && rs.getInt("_Marker_Type_key") == MGI_MARKER_OTHER_GENOME_FEATURE) {
              query = getAlleleQueryFromOGFID(accessionID);
              stmt = connection.createStatement();
              rs = stmt.executeQuery(query);
              if (rs.next()) {
                String allelePageID = rs.getString("accID");
                return doMGIQuery(allelePageID, expectedTypeID, wrongTypeString);
              }
            }
          }
          result.setValid(false);
          result.setErrorString(wrongTypeString);

        } else {
          query = "select * from " + tableName + " where " + primaryKeyName + "=" + objectKey;
          if (verbose) System.out.println(query);
          stmt = connection.createStatement();
          rs = stmt.executeQuery(query);

          if (rs.next()) {
            if (mgiTypeKey == MGI_ALLELE) {
              result.setSymbol(rs.getString("symbol"));
              result.setName(trimOfficialName(rs.getString("name")));
              result.setValid(true);

              query = "select term from ALL_Allele aa inner join voc_term voc on aa._Allele_Type_key=voc._Term_key where _Allele_key="
                  + objectKey;
              if (verbose) System.out.println(query);
              rs = stmt.executeQuery(query);
              if (rs.next()) {
                String alleleType = rs.getString("term");
                if (verbose) System.out.println("allele type: " + alleleType);
                if (alleleType.equalsIgnoreCase("QTL")) {
                  result.setValid(false);
                  result.setErrorString(
                      "This ID corresponds to a QTL variant. Please go back to step 2 and do a submission for the relevant inbred strain");
                }
              }

            } else if (mgiTypeKey == MGI_MARKER) {
              result.setSymbol(rs.getString("symbol"));
              result.setName(rs.getString("name"));
              result.setValid(true);
            } else if (mgiTypeKey == MGI_REFERENCE) {
              result.setAuthors(rs.getString("authors"));
              result.setTitle(rs.getString("title"));
              result.setValid(true);
            }
          }
        }
      } else {
        if (expectedTypeID == MGI_REFERENCE) {
          result.setErrorString("Not found.  Please confirm that you have the correct Pubmed ID.");
          result.setValid(false);
        } else {
          result.setErrorString("Not found in MGI database.  Confirm that you have the correct Accession ID");
          result.setValid(false);
        }

      }
    } catch (NullPointerException e) {
      result.setValid(offlineOK);
      result.setErrorString("Connection to MGI timed out.");
      result.setTitle(result.getErrorString());
      result.setAuthors("");
      result.setName(result.getErrorString());
      result.setSymbol("");
      result.setMgiConnectionTimedout(true);
      e.printStackTrace(System.err);

    } catch (Exception e) {

      result.setValid(offlineOK);
      result.setErrorString(
          "MGI database connection unavailable.  This is to be expected late at night on weekdays.  Please manually verify that this is the correct ID");
      result.setTitle(result.getErrorString());
      result.setAuthors("");
      result.setName(result.getErrorString());
      result.setSymbol("");
      result.setMgiOffline(true);
      // System.err.println(query);
      e.printStackTrace(System.err);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }
    // Close the connection to MGI.

    return result;
  }

  public static HashMap<Integer, MouseSubmission> SubmissionFromMGI(Collection<Integer> accessionIDs,
      int importTaskId) {
    HashMap<Integer, MouseSubmission> newSubmissions = new HashMap<Integer, MouseSubmission>();

    // TODO validate accession IDs first? so that we don't have to duplicate logic
    // like
    // checking that it isn't a QTL or other genome feature

    HashMap<Integer, Properties> results = getPropertiesFromAlleleMgiID(accessionIDs, importTaskId);

    for (int key : results.keySet()) {

      Properties props = results.get(key);
      if (props != null && !props.isEmpty()) {
        MouseSubmission sub = new MouseSubmission();
        sub.setMouseMGIID(Integer.toString(key));

        sub.setIsPublished("No");
        sub.setHolderFacility("unassigned");
        sub.setHolderName("unassigned");

        sub.setMouseType("unknown");

        if (verbose) System.out.println("*****************************");
        if (verbose) System.out.println("Allele MGI ID: " + key);

        String geneId = null;

        StringBuilder propertyList = new StringBuilder();

        for (String prop : props.stringPropertyNames())
        {
          String value = props.getProperty(prop);


          if (verbose) System.out.println(prop + ": " + value);
          if (prop.equals("mouseName"))
          {
            sub.setOfficialMouseName(trimOfficialName(value));
          }
          else if (prop.equals("mouseType"))
          {
            propertyList.append("*" + prop + ":* " + value + "\r\n");
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
              //extract from 'Transgenic (Reporter)'
              //or 'Transgenic (random, expressed)'
              //or 'Transgenic (Cre/Flp)'
            }
            else if (value.startsWith("Gene trapped"))
            {
              sub.setMouseType("Mutant Allele");
              sub.setMAModificationType("undetermined");
            }
            else if (value.equals("Not Applicable"))
            {
              sub.setMouseType("Inbred Strain");
              //TODO ????
//              Allele MGI ID: 3579311
//              mouseType : Not Applicable
//              pubMedTitle : THE AKR THYMIC ANTIGEN AND ITS DISTRIBUTION IN LEUKEMIAS AND NERVOUS TISSUES.
//              gene name : thymus cell antigen 1, theta
//              gene symbol : Thy1
//              mouseName : a variant
//              pubMedID : 14207060
//              pubMedAuthor : REIF AE
//              officialSymbol : Thy1<a>
//              gene mgi ID : 98747
            }
            //Enodnuclease-mediated mice will switch to this type -EW
            else if (value.startsWith("Endonuclease"))
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
//    if (rawName == null || rawName.isEmpty())
//    {
//      return rawName;
//    }
//
//    if (rawName.toLowerCase().indexOf("targeted mutation ") < 0 && rawName.toLowerCase().indexOf("transgene insertion ") < 0)
//    {
//      return rawName;
//    }
//
//    int index = rawName.indexOf(',') + 1;
//    if (index >= rawName.length() - 1)
//    {
//      return rawName;
//    }
//    return rawName.substring(index).trim();

  }

  public static HashMap<Integer,Properties> getPropertiesFromAlleleMgiID(Collection<Integer> accessionIDs, int importTaskId)
  {
    HashMap<Integer,Properties> results = new HashMap<Integer,Properties>();
    Connection connection = null;
    Statement stmt = null;
    ResultSet rs = null;

    ImportStatusTracker.UpdateHeader(importTaskId, "Downloading Allele data from MGI (Task 2 of 3)");
    ImportStatusTracker.SetProgress(importTaskId, 0);

    double mgiIdNumber = 1;
    double mgiCount = accessionIDs.size();

    try
    {
      connection = connect();
      stmt = connection.createStatement();
      for(int accessionID : accessionIDs)
      {

        ImportStatusTracker.SetProgress(importTaskId, mgiIdNumber / mgiCount);

        mgiIdNumber++;
        if (accessionID < 0)
        {
          Log.Info("Ignored invalid accession ID: " + accessionID);
          continue;
        }
        if (verbose) System.out.println("Fetching details for MGI:" + accessionID);
        Properties props = new Properties();
        results.put(accessionID, props);
        //TODO get the comment??
        String query = "select _Object_key " +
          "from ACC_Accession left join ACC_MGIType on ACC_Accession._MGIType_key=ACC_MGIType._MGIType_key " +
          "where accid='MGI:" + accessionID + "' " +
          "and ACC_Accession._MGIType_key=11";
        if (verbose) System.out.println(query);
        rs = stmt.executeQuery(query);
        if(!rs.next())
        {
          //accession ID not found, return early
          Log.Info("No matching alleles found for accession ID: " + accessionID);
          continue;
        }
        int alleleKey = rs.getInt("_Object_key");
        query = "select _Marker_key,name,symbol from ALL_Allele where _Allele_key=" + alleleKey;
        if (verbose) System.out.println(query);
        rs = stmt.executeQuery(query);

        if(!rs.next())
        {
          //no data in allele table, very strange
          if (verbose) System.out.println("No data found for allele key: " + alleleKey);
          continue;
        }
        props.setProperty("mouseName",rs.getString("name"));
        props.setProperty("officialSymbol",rs.getString("symbol"));

        ImportStatusTracker.AppendMessage(importTaskId, "MGI:" + accessionID + " -> " + HTMLUtilities.getCommentForDisplay(props.getProperty("officialSymbol")));
        int markerKey = rs.getInt("_Marker_key");

        query = "select term from ALL_Allele_Mutation mut inner join voc_term voc on mut._mutation_key=voc._term_key where _Allele_key=" + alleleKey;
        if (verbose) System.out.println(query);
        rs = stmt.executeQuery(query);
        if (rs.next())
        {
          String mutation = rs.getString("term");
          //convert MGI terminology to ours
          props.setProperty("mutationType",mutation);
        }

        query = "select term from ALL_Allele aa inner join voc_term voc on aa._Allele_Type_key=voc._Term_key where _Allele_key=" + alleleKey;
        if (verbose) System.out.println(query);
        rs = stmt.executeQuery(query);
        if (rs.next())
        {
          String alleleType = rs.getString("term");
          //convert MGI terminology to ours
          props.setProperty("mouseType",alleleType);
        }
        if (markerKey > 0)
        {
          query = "select symbol,name from MRK_Marker where _Marker_key=" + markerKey;
          if (verbose) System.out.println(query);
          rs = stmt.executeQuery(query);
          if (rs.next())
          {
            //todo set gene properties
            String symbol = rs.getString("symbol");
            String name = rs.getString("name");
            props.setProperty("geneSymbol",symbol);
            props.setProperty("geneName",name);

            query = "select numericPart from ACC_Accession WHERE _MGIType_key=2  " +
                "and _Object_key=" + markerKey + " and prefixPart='MGI:'  and preferred=1";
            if (verbose) System.out.println(query);
            rs = stmt.executeQuery(query);
            if (rs.next())
            {
              int geneMgiId = rs.getInt("numericPart");
              props.setProperty("geneMgiID",Integer.toString(geneMgiId));
              //todo set gene MGI accession ID
            }
          }
        }
        else
        {
          Log.Info("No markers for allele MGI: " + accessionID);
        }

        query = "select _Refs_key from mgi_reference_assoc ref inner join mgi_refassoctype ty using(_refassoctype_key) where _Object_key=" + alleleKey + " and assoctype='Original'";
        if (verbose) System.out.println(query);
        rs = stmt.executeQuery(query);
        if (rs.next())
        {
          int refKey = rs.getInt("_Refs_key");
          query = "select _primary,title from BIB_refs where _Refs_key=" + refKey;
          if (verbose) System.out.println(query);
          rs = stmt.executeQuery(query);
          if (rs.next())
          {
            props.setProperty("pubMedAuthor", rs.getString("_primary"));
            props.setProperty("pubMedTitle", rs.getString("title"));
          }
          query = "select accID from ACC_Accession where _MGIType_key=1 and _Object_key=" + refKey + " and prefixPart is NULL";
          if (verbose) System.out.println(query);
          rs = stmt.executeQuery(query);
          if (rs.next())
          {
            props.setProperty("pubMedID", rs.getString("accID"));
          }
          else
          {
            query = "select accID from ACC_Accession where _MGIType_key=1 and _Object_key=" + refKey + "and prefixPart='MGI:'";
            if (verbose) System.out.println(query);
            rs = stmt.executeQuery(query);
            if (rs.next())
            {
              props.setProperty("referenceMgiAccessionId", rs.getString("accID"));
            }
          }

        }

        StringBuilder sb = new StringBuilder();
        query = "select note " +
            "from MGI_Note n, MGI_NoteType t, MGI_NoteChunk c " +
            "where n._NoteType_key = t._NoteType_key " +
            "and t._MGIType_key = 11 " +
            "and n._Note_key = c._Note_key " +
            "and _object_key="+ alleleKey + " " +
            "and noteType='Molecular' order by sequenceNum";
        if (verbose) System.out.println(query);
        rs= stmt.executeQuery(query);
        while (rs.next())
        {
          sb.append(rs.getString("note"));
        }
        if (sb.length() > 0)
        {
          props.setProperty("description", sb.toString());
        }
        else {
          Log.Error("No description found for allele w/ accID: " + accessionID + ".  Ran this query:\n" + query);
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
      if(connection != null)
        {
          try
          {
            connection.close();
          }
          catch(SQLException ex)
          {
             ex.printStackTrace();
          }
        }
      ImportStatusTracker.SetProgress(importTaskId, 1);
    }

    if(verbose){

      for(int mgiId : results.keySet()){
        Properties props = results.get(mgiId);
        System.out.println("Properties for MGI:" + mgiId);
        for (String prop : props.stringPropertyNames())
        {
          String value = props.getProperty(prop);
          System.out.println(prop + ": " + value);
        }
      }


    }

    return results;
  }

//  private static void  getOfficialMouseNames(String[] accessionIds)
//  {
//    Connection connection = null;
//    Statement stmt = null;
//    ResultSet rs = null;
//
//    try
//    {
//      connection = connect();
//      stmt = connection.createStatement();
//      for(String accessionID : accessionIds)
//      {
//        if (verbose) System.out.println("Fetching details for MGI:" + accessionID);
//        //Properties props = new Properties();
//        //TODO get the comment??
//        String query = "select ACC_Accession.numericPart, symbol,ALL_ALLELE.name " +
//        "from ACC_Accession left join ACC_MGIType on ACC_Accession._MGIType_key=ACC_MGIType._MGIType_key " +
//        "left join ALL_ALLELE on ACC_Accession._Object_key = ALL_Allele._Allele_key " +
//        "where accID='" + accessionID + "' " +
//        "and ACC_Accession._MGIType_key = 11  ";
//        if (verbose) System.out.println(query);
//        rs = stmt.executeQuery(query);
//        if(!rs.next())
//        {
//          //accession ID not found, return early
//          Log.Info("No matching alleles found for accession ID: " + accessionID);
//          continue;
//        }
//
//        int id = rs.getInt(1);
//        //String symbol = rs.getString(2);
//        String name = rs.getString(3);
//
//        if (verbose) System.out.println(name + "\t" + id);
//
//      }
//
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//    }
//    finally
//    {
//      if(connection != null)
//        {
//          try
//          {
//            connection.close();
//          }
//          catch(SQLException ex)
//          {
//             ex.printStackTrace();
//          }
//        }
//    }
//
//  }


  private static String getAlleleQueryFromOGFID(String OGFID)
  {
    return "select a2.accId from acc_accession a1, all_allele e, acc_accession a2 where a1.accid='" + OGFID + "' and a1._mgitype_key=2 and a2._mgitype_key=11 and a2._object_key=e._allele_key and e._marker_key=a1._object_key";

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


    private static Connection connect() throws Exception
    {
      if (!initialized)
      {
        throw new Exception("Tried to connect to MGI before initializing connection parameters!");
      }

      ConnectionThread t = new ConnectionThread();
      t.start();

      long timeoutMillis = 10000;

    t.join(timeoutMillis);
    if (t.isAlive())
    {
      //timeout reached, kill thread and throw timeout exception
      t.interrupt();
      Log.Info("Timeout reached, interrupting mgi connection thread");
    }

        return t.getConnection();
    }

    private static class ConnectionThread extends Thread
    {
      private Connection connection = null;

      public ConnectionThread()
      {
        super();
      }

    @Override
    public void run() {
      try {
        // Load the JDBC driver: MySQL MM JDBC driver
        Class.forName(env.get("DB_CLASSNAME"));
        Class.forName(env.get("DB_CONNECTION_STRING"));
        // Create a new connection to MGI
        setConnection(DriverManager.getConnection(databaseConnectionString));
        if (verbose) System.out.println("Successfully connected to MGI, returning connection");
      } catch (ClassNotFoundException e) {
        Log.Error("Failed to connect to MGI:", e);
      } catch (SQLException e) {
        Log.Error("Failed to connect to MGI:", e);
      }

    }

    void setConnection(Connection connection) {
      this.connection = connection;
    }

    Connection getConnection() {
      return connection;
    }



    }


}
