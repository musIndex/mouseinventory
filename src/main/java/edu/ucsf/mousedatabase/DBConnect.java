package edu.ucsf.mousedatabase;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import edu.ucsf.mousedatabase.beans.MouseSubmission;
import edu.ucsf.mousedatabase.beans.UserData;
import edu.ucsf.mousedatabase.dataimport.ImportHandler;
import edu.ucsf.mousedatabase.dataimport.ImportHandler.ImportObjectType;
import edu.ucsf.mousedatabase.objects.*;
import edu.ucsf.mousedatabase.servlets.ReportServlet;

import org.apache.commons.lang3.StringUtils;

public class DBConnect {

  //set this to true for debugging
  private static final boolean logQueries = false;


  private static final String mouseRecordTableColumns =
    "mouse.id, name, mousetype, modification_type," +
    "transgenictype.transgenictype,regulatory_element_comment as 'regulatory element',"
      +"expressedsequence.expressedsequence, reporter_comment as 'reporter', strain, " +
    "general_comment, source, mta_required, repository_id, repository.repository, " +
    "repository_catalog_number,gensat,other_comment, gene.mgi as 'gene MGI', " +
    "gene.symbol as 'gene symbol', gene.fullname as 'gene name',cryopreserved," +
    "status,endangered,submittedmouse_id, targetgenes.mgi as 'target gene MGI'," +
    "targetgenes.symbol as 'target gene symbol', targetgenes.fullname as 'target gene name', official_name\r\n";

  private static final String mouseRecordTableJoins =
    "   left join mousetype on mouse.mousetype_id=mousetype.id\r\n"
      +"  left join gene on mouse.gene_id=gene.id\r\n"
      +"  left join gene as targetgenes on mouse.target_gene_id=targetgenes.id\r\n"
      +"  left join transgenictype on mouse.transgenictype_id=transgenictype.id\r\n"
      +"  left join expressedsequence on mouse.expressedsequence_id=expressedsequence.id\r\n"
      +"  left join repository on mouse.repository_id=repository.id\r\n ";

  private static final String mouseRecordQueryHeader =
    "SELECT " +
    mouseRecordTableColumns
      +" FROM mouse\r\n"
      + mouseRecordTableJoins;

  private static final String mouseRecordQueryCountHeader =
    "SELECT count(*) as count"
      +" FROM mouse\r\n"
      + mouseRecordTableJoins;

  private static final String mouseSubmissionQueryHeader =
    "SELECT  submittedmouse.* , mouse.id as mouseRecordID\r\n"
    + " FROM submittedmouse left join mouse on submittedmouse.id=mouse.submittedmouse_id\r\n ";

  private static final String changeRequestQueryHeader =
    "SELECT changerequest.*, mouse.name\r\n" +
    " FROM changerequest left join mouse on changerequest.mouse_id=mouse.id\r\n ";

  private static final String holderQueryHeader =
    "SELECT holder.*, (select count(*) \r\n" +
    " FROM mouse_holder_facility left join mouse on mouse_holder_facility.mouse_id=mouse.id\r\n" +
    " WHERE holder_id=holder.id and covert=0 and mouse.status='live') as 'mice held'\r\n" +
    " FROM holder\r\n ";

  private static final String facilityQueryHeader =
    "SELECT id, facility, description, code" +
    ", (select count(*) from mouse_holder_facility where facility_id=facility.id) as 'mice held'\r\n" +
    " FROM facility\r\n ";

  private static final String mouseHolderQueryHeader =
    "SELECT holder_id, facility_id, covert, cryo_live_status, firstname, lastname, " +
    "department, email, alternate_email, tel, facility" +
    "\r\n FROM mouse_holder_facility t1 left join holder on t1.holder_id=holder.id " +
    "left join facility on t1.facility_id=facility.id \r\n";

  private static final String geneQueryHeader = "SELECT id,fullname,symbol,mgi \r\n FROM gene\r\n ";

  private static final String mouseIDSearchTermsRegex = "^(#[0-9]+,?)+$";



  private static Connection connect() throws Exception
  {
    try
    {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      DataSource ds = (DataSource) envCtx.lookup("jdbc/mouse_inventory");

      return ds.getConnection();
    }
    catch (Exception e)
    {
      Log.Error("Problem connecting",e);
      throw e;
    }
  }

  //************************************************************
  //VIEW Methods
  //************************************************************
  public static ArrayList<SubmittedMouse> getMouseSubmissions(String status, String entered, String orderBy)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
          whereTerms.add("submittedmouse.status='" + status + "'");
      }
      if (entered != null && !entered.isEmpty())
      {
        whereTerms.add("entered='" + entered +"'");
      }

      return getSubmissions(additionalJoins,whereTerms,orderBy);
  }

  public static ArrayList<SubmittedMouse> getMouseSubmissions(List<Integer> submittedMouseIds)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";

    StringBuilder sb = new StringBuilder();
    sb.append("submittedmouse.id in(");
    boolean first = true;
    for(int id : submittedMouseIds)
    {
      if(first)
      {
        first = false;
      }
      else
      {
        sb.append(",");
      }
      sb.append(id);
    }
    sb.append(")");

    whereTerms.add(sb.toString());

      return getSubmissions(additionalJoins,whereTerms,null);
  }

  public static ArrayList<SubmittedMouse> getMouseSubmission(int submittedMouseID)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    whereTerms.add("submittedmouse.id="+submittedMouseID);

      return getSubmissions(additionalJoins,whereTerms,null);
  }

  private static SubmittedMouse getSubmission(int submissionID)
  {
    String query = mouseSubmissionQueryHeader +
    " WHERE submittedmouse.id=" + submissionID;
    ArrayList<SubmittedMouse> results = SubmittedMouseResultGetter.getInstance().Get(query);
      return results.size() > 0 ? results.get(0) : null;

  }

  private static ArrayList<SubmittedMouse> getSubmissions(String additionalJoins,
                              ArrayList<String> whereTerms,
                              String orderBy)
  {
    String whereClause = "";
    String orderByClause = "";
    String joinsClause = "";
    if(whereTerms != null && !whereTerms.isEmpty())
    {
      String prefix = " WHERE";
      for(String whereTerm : whereTerms)
      {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }
    if(orderBy != null && !orderBy.equalsIgnoreCase(""))
    {
      orderByClause = "ORDER BY " + orderBy;
    }
    if(additionalJoins != null)
    {
      joinsClause = additionalJoins + "\r\n ";
    }

    String query = mouseSubmissionQueryHeader + "\r\n " +
      joinsClause + whereClause + " " + orderByClause;
    return SubmittedMouseResultGetter.getInstance().Get(query);
  }


  public static ArrayList<MouseRecord> getMouseRecord(int mouseID)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("mouse.id="+mouseID);
    String constraints = buildMouseQueryConstraints(null, whereTerms, null, -1, -1);
      return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }
  
  public static ArrayList<MouseRecord> getMouseRecords(List<Integer> mouseIds) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("mouse.id in(" + (mouseIds.size() > 0 ? StringUtils.join(mouseIds, ",") : "0") + ")");
    String constraints = buildMouseQueryConstraints(null, whereTerms, null, -1, -1);
    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }
 
  
  public static ArrayList<MouseRecord> getMouseRecordFromSubmission(int submissionID)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("mouse.submittedmouse_id="+submissionID);
    String constraints = buildMouseQueryConstraints(null, whereTerms, null, -1, -1);
      return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }
  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID,
                            String orderBy,
                            int holderID,
                            int geneRecordID,
                            String status)
  {
    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, null);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID,
                            String orderBy,
                            int holderID,
                            int geneRecordID,
                            String status,
                            String searchTerms)
  {
    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, false);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID,
                            String orderBy,
                            int holderID,
                            int geneRecordID,
                            String status,
                            String searchTerms,
                            boolean endangeredOnly)
  {

    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, -1);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID,
                            String orderBy,
                            int holderID,
                            int geneRecordID,
                            String status,
                            String searchTerms,
                            boolean endangeredOnly,
                            int creOnly)
  {

    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, creOnly, -1);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID,
                            String orderBy,
                            int holderID,
                            int geneRecordID,
                            String status,
                            String searchTerms,
                            boolean endangeredOnly,
                            int creOnly,
                            int facilityID)
  {
    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, creOnly, facilityID, -1, -1);
  }

  public static ArrayList<MouseRecord> getCovertMice(String orderby, String status, String searchTerms, int mouseTypeID)
  {
    String query = "SELECT distinct mouse_id from mouse_holder_facility where covert=1";
    ArrayList<Integer> covertMouseIds = IntResultGetter.getInstance("mouse_id").Get(query);

    StringBuilder builder = new StringBuilder();
    Iterator<Integer> iter = covertMouseIds.iterator();
    while (iter.hasNext()) {
         builder.append(iter.next());
         if (!iter.hasNext()) {
           break;
         }
         builder.append(",");
     }

    ArrayList<String> whereTerms = buildMouseQueryWhereTerms(status, searchTerms, mouseTypeID, -1,-1,-1,-1,false);
    whereTerms.add("mouse.id in (" + builder + ")");

    String constraints = buildMouseQueryConstraints(null, whereTerms, orderby, -1, -1);
    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader +  buildMouseQueryJoins(-1, -1, searchTerms), constraints));
  }

  public static int countMouseRecords(int mouseTypeID,
      String orderBy,
      int holderID,
      int geneRecordID,
      String status,
      String searchTerms,
      boolean endangeredOnly,
      int creOnly,
      int facilityID)
  {

    ArrayList<String> whereTerms = buildMouseQueryWhereTerms(status, searchTerms, mouseTypeID, geneRecordID,
        facilityID, holderID,creOnly, endangeredOnly);
    String additionalJoins = buildMouseQueryJoins(holderID, facilityID,searchTerms);

    String constraints = buildMouseQueryConstraints(additionalJoins, whereTerms, orderBy, -1, -1);

      ArrayList<Integer> results = IntResultGetter.getInstance("count").Get(
          buildMouseQuery(mouseRecordQueryCountHeader, constraints));
      if (results.size() > 0)
      {
        return results.get(0);
      }
      return -1;
  }


  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID,
      String orderBy,
      int holderID,
      int geneRecordID,
      String status,
      String searchTerms,
      boolean endangeredOnly,
      int creOnly,
      int facilityID,
      int limit,
      int offset)
      {
    ArrayList<String> whereTerms = buildMouseQueryWhereTerms(status, searchTerms, mouseTypeID, geneRecordID, facilityID, holderID,creOnly, endangeredOnly);
    String additionalJoins = buildMouseQueryJoins(holderID, facilityID,searchTerms);

    String constraints = buildMouseQueryConstraints(additionalJoins, whereTerms, orderBy, limit, offset);

    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }
  
  

  private static String buildMouseQueryJoins(int holderID, int facilityID, String searchTerms)
  {
    String additionalJoins = "";
    if(holderID != -1 || facilityID != -1)
      {
        additionalJoins += "  left join mouse_holder_facility on mouse.id=mouse_holder_facility.mouse_id\r\n ";
      }
    if(searchTerms != null && !searchTerms.isEmpty())
      {
        additionalJoins += "  left join flattened_mouse_search on mouse.id=flattened_mouse_search.mouse_id";
      }
    return additionalJoins;
  }

  private static ArrayList<String> buildMouseQueryWhereTerms(String status, String searchTerms,
      int mouseTypeID,int geneRecordID,int facilityID,int holderID,int creOnly,
      boolean endangeredOnly)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    if(status.equalsIgnoreCase("all"))
    {
      whereTerms.add("mouse.status<>'incomplete'");
    }
    else
    {
      whereTerms.add("mouse.status='" + status + "'");
    }
    if (mouseTypeID != -1) {
          whereTerms.add("mousetype.id=" + mouseTypeID);
    }
    if (geneRecordID != -1)
    {
      whereTerms.add("(gene_id=" + geneRecordID + " or target_gene_id=" + geneRecordID + ")");
    }
    if (facilityID != -1)
    {
      whereTerms.add("(facility_id=" + facilityID + ")");
    }
    if(holderID != -1)
    {
      whereTerms.add("holder_id=" + holderID);
      whereTerms.add("covert=false");
    }
    if(searchTerms != null && !searchTerms.isEmpty())
    {
      SearchResult match = doMouseSearch(searchTerms, status);
      whereTerms.add("mouse.id in(" + (match.getTotal() > 0 ? StringUtils.join(match.getMatchingIds(), ",") : "0") + ")");
    }
    if(endangeredOnly)
    {
      whereTerms.add("endangered=true");
    }
    if(creOnly > 0)
    {
      whereTerms.add("expressedsequence.expressedsequence='Cre'");
    }

    return whereTerms;
  }
  
  
  public static SearchResult doMouseSearch(String searchTerms, String status) {
    SearchResult results = new SearchResult();
    ArrayList<Integer> mouseIds = new ArrayList<Integer>();
    if(searchTerms.matches(mouseIDSearchTermsRegex))
    {
      
      //if the user enters '#101', we give them record 101 only.
      //if they enter '#101,#102', we give them records 101 and 102.
      Log.Info("SearchDebug: loading record numbers from terms " + searchTerms);
      for(String token : StringUtils.splitByCharacterType(searchTerms)) {
        if (token.matches("[0-9]+")) {
          mouseIds.add(Integer.parseInt(token));
        }
        results.setStrategy(new SearchStrategy(0, "record-id", "Exact record number lookup"));
      }
      Log.Info("SearchDebug: loaded record numbers from terms " + searchTerms + " => " + StringUtils.join(mouseIds,","));
    }
    else
    {
      List<SearchStrategy> strategies = new ArrayList<SearchStrategy>();
      //strategies.put("natural","Natural language match");
      strategies.add(new SearchStrategy(0,"word","Exact match"));
      strategies.add(new SearchStrategy(2,"word-expanded","Expanded match"));
      strategies.add(new SearchStrategy(5,"word-chartype","Partial word match"));
      strategies.add(new SearchStrategy(8,"word-chartype-expanded","Partial word match"));
      strategies.add(new SearchStrategy(10,"like-wildcard","No word matches, showing partial matches."));
      
      for(SearchStrategy strategy : strategies) {
        mouseIds = doMouseSearchQuery(searchTerms, strategy, status);
        if (mouseIds.size() > 0) {
          results.setStrategy(strategy);
          break;
        }
       }
    }
    results.setMatchingIds(mouseIds);
    return results;
  }
  
  
  private static ArrayList<Integer> doMouseSearchQuery(String searchTerms, SearchStrategy strategy, String status){
    
    String query = "select mouse_id from flattened_mouse_search, mouse";
    String statusTerm;
    if(status.equalsIgnoreCase("all"))
    {
      statusTerm = " and mouse.status<>'incomplete'";
    }
    else
    {
      statusTerm = " and mouse.status='" + status + "'";
    }
    searchTerms = StringUtils.remove(searchTerms, '"');
    searchTerms = StringUtils.remove(searchTerms, '\'');
    searchTerms = StringUtils.replace(searchTerms, "\\", " ");
    
    if (strategy.getName().equals("natural"))
    {
      query += " WHERE match(searchtext) against('" + searchTerms + "')";
    }
    else if (strategy.getName().equals("word"))
    {
      query += " WHERE match(searchtext) against(" + tokenizeBoolean(searchTerms, false, false) + ")";
    }
    else if (strategy.getName().equals("word-expanded"))
    {
      query += " WHERE match(searchtext) against(" + tokenizeBoolean(searchTerms, true, false) + ")";
    }
    else if (strategy.getName().equals("word-chartype"))
    {
      query += " WHERE match(searchtext) against(" + tokenizeBoolean(searchTerms, false, true) + ")";
    }
    else if (strategy.getName().equals("word-chartype-expanded"))
    {
      query += " WHERE match(searchtext) against(" + tokenizeBoolean(searchTerms, true, true) + ")";
    }
    else if (strategy.getName().equals("like-wildcard"))
    {
      query += " WHERE searchtext LIKE ('%" + addMySQLEscapes(searchTerms) + "%')";
    }
    else
    {
      //invalid search strategy, default to natural
      Log.Error("Invalid search strategy: " + strategy + ", defaulting to natural language search");
      query += " WHERE match(searchtext) against(" + searchTerms + ")";
    }
    query += " and mouse_id=mouse.id" + statusTerm;
    Log.Info("SearchDebug:[" + strategy.getName() + "] " + query);
    return IntResultGetter.getInstance("mouse_id").Get(query);    
  }
  
  private static String tokenizeBoolean(String searchTerms, boolean expand, boolean charType) {
    ArrayList<String> terms = new ArrayList<String>();
    
    String[] tokens;
    if (charType) {
      tokens = StringUtils.splitByCharacterType(searchTerms);
    }
    else
    {
      tokens = StringUtils.split(searchTerms," -");
    }
    for(String token : tokens) {
      String term = "+" + token;
      if (expand) {
        term += "*";
      }
      terms.add(term);
    }
    return "'" + StringUtils.join(terms," ") + "' IN BOOLEAN MODE";
  }

  private static String buildMouseQueryConstraints(String additionalJoins, ArrayList<String> whereTerms, String orderBy,int limit, int offset)
  {
    String whereClause = "";
    String orderByClause = "";
    String joinsClause = "";
    String limitClause = "";
    if(whereTerms != null && !whereTerms.isEmpty())
    {
      String prefix = " WHERE";
      for(String whereTerm : whereTerms)
      {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }
    if(orderBy != null && !orderBy.equalsIgnoreCase(""))
    {
      orderByClause = "ORDER BY " + orderBy;
    }
    if(additionalJoins != null)
    {
      joinsClause = additionalJoins + "\r\n ";
    }
    if (limit > 0 && offset >=0)
    {
      limitClause = "LIMIT " + offset + "," + limit;
    }

    return joinsClause + whereClause + " " + orderByClause + " " + limitClause;
  }

  private static String buildMouseQuery(String selectFrom, String constraints)
  {
    return selectFrom + "\r\n " + constraints;
  }

  public static ArrayList<MouseHolder> getAllMouseHolders()
  {
    return MouseHolderResultGetter.getInstance().Get(mouseHolderQueryHeader);
  }


  public static Holder getHolder(int holderID)
  {
    String query = holderQueryHeader +
    " WHERE id='" + holderID + "'";

      ArrayList<Holder> results = HolderResultGetter.getInstance().Get(query);
      if (results.size() > 0)
      {
        return results.get(0);
      }
      return null;

  }



  public static Holder findHolder(String holderFullName)
  {
    if(holderFullName == null)
      return new Holder();
    String[] holderName = holderFullName.split("[,]");
    String query = null;
    if (holderName.length > 1)
    {
        query = holderQueryHeader +
          " WHERE (firstname='"
          + addMySQLEscapes(holderName[1]).trim() + "' and lastname='"
          + addMySQLEscapes(holderName[0]).trim() + "')";
    }
    else
    {
      Log.Info("Unrecognized holder fullname format: " + holderFullName);
      return null;
    }
      ArrayList<Holder> results = HolderResultGetter.getInstance().Get(query);
      if (results.size() > 0)
      {
        return results.get(0);
      }
      return null;
  }

  public static Holder findHolderByEmail(String holderEmail)
  {
    if(holderEmail == null)
      return null;
    String query = null;
    if (!holderEmail.isEmpty())
    {
        query = holderQueryHeader +
          " WHERE email='"
          + holderEmail + "'";
    }
    else
    {
      return null;
    }
      ArrayList<Holder> results = HolderResultGetter.getInstance().Get(query);
      if (results.size() > 0)
      {
        return results.get(0);
      }
      return null;
  }

  public static Facility findFacilityByCode(String facilityCode)
  {
    if (facilityCode == null)
      return null;
    String query = null;
    if (!facilityCode.isEmpty())
    {
      query = facilityQueryHeader +
        " WHERE code='"
        + facilityCode + "'";
    }
    else
    {
      return null;
    }
    ArrayList<Facility> results = FacilityResultGetter.getInstance().Get(query);
    if (results.size() > 0)
    {
      return results.get(0);
    }
    return null;
  }

  public static ArrayList<Holder> getAllHolders()
  {
    return getAllHolders(true);
  }

  public static ArrayList<Holder> getAllHolders(String orderby)
  {
    return getAllHolders(true,orderby);
  }

  public static ArrayList<Holder> getAllHolders(boolean includeBlank)
  {
    return getAllHolders(includeBlank, null);
  }

  public static ArrayList<Holder> getAllHolders(boolean includeBlank, String orderby)
  {
    if (orderby != null && orderby.equals("count"))
    {
      orderby = "`mice held` desc";
    }
    String query = holderQueryHeader;
      if(!includeBlank)query += " WHERE id > 1";
      query += "\r\n ORDER BY ";
      query += orderby != null ? orderby : "lastname, firstname";
      return HolderResultGetter.getInstance().Get(query);
  }


  public static Facility getFacility(int facilityID)
  {
    String query = facilityQueryHeader +
      " WHERE id=" + facilityID + "";
    ArrayList<Facility> results = FacilityResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;
  }

  public static Facility findFacility(String facilityName)
  {
      String query = facilityQueryHeader +
        " WHERE facility='" + addMySQLEscapes(facilityName) + "'";
      ArrayList<Facility> results = FacilityResultGetter.getInstance().Get(query);
      return results.size() > 0 ? results.get(0) : null;
  }

  public static ArrayList<Facility> getAllFacilities()
  {
    return getAllFacilities(true);
  }

  public static ArrayList<Facility> getAllFacilities(String orderby)
  {
    return getAllFacilities(true,orderby);
  }

  public static ArrayList<Facility> getAllFacilities(boolean includeblank)
  {
    return getAllFacilities(includeblank, null);
  }


  public static ArrayList<Facility> getAllFacilities(boolean includeblank,String orderby)
  {
    if (orderby != null && orderby.equals("count"))
    {
      orderby = "`mice held` desc";
    }
    String query = facilityQueryHeader;
    if (!includeblank)
    {
      query += " WHERE id > 1";
    }
    query += "\r\n ORDER BY ";
    query += orderby != null ? orderby : "id";
    return FacilityResultGetter.getInstance().Get(query);
  }


  public static Gene getGene(int geneRecordID)
  {
    String query = geneQueryHeader + " WHERE id='" + geneRecordID + "'";
    ArrayList<Gene> results = GeneResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;
  }

  public static Gene findGene(String mgiAccessionID)
  {
    String query = geneQueryHeader + " WHERE mgi='" + mgiAccessionID + "'";
    ArrayList<Gene> results = GeneResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;
  }

  public static ArrayList<Gene> getAllGenes(String orderby)
  {
    String query = geneQueryHeader + " ORDER BY " + orderby;
    return GeneResultGetter.getInstance().Get(query);
  }


  public static ArrayList<MouseType> getMouseTypes()
  {
    String query = "SELECT * FROM mousetype";
    String[] columnNames = new String[]{"id","mousetype"};

    ArrayList<ArrayList<String>> results = StringArrayListResultGetter.getInstance(columnNames).Get(query);
    ArrayList<MouseType> types = new ArrayList<MouseType>();

    for (ArrayList<String> result : results)
    {
      MouseType type = new MouseType();
      type.setMouseTypeID(Integer.parseInt(result.get(0)));
      type.setTypeName(result.get(1));
      types.add(type);
    }
    return types;
  }

  public static MouseType getMouseType(int mouseTypeId)
  {
    ArrayList<MouseType> types = getMouseTypes();
    for (MouseType type : types)
    {
      if (type.getMouseTypeID() == mouseTypeId)
      {
        return type;
      }
    }
    return null;


  }


  public static ArrayList<ChangeRequest> getChangeRequest(int changeRequestID)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    String orderBy = "";
    whereTerms.add("changerequest.id=" + changeRequestID);

    return getChangeRequests(additionalJoins,whereTerms,orderBy);
  }


  public static ArrayList<ChangeRequest> getChangeRequests(String[] statuses, String orderBy, int mouseID)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (statuses != null && statuses.length > 0)
    {
      int i = 0;
      String s = "(changerequest.status=";
      for (String status : statuses)
      {
        if (i > 0)
        {
          s += " OR changerequest.status=";
        }
        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
              s += "'" + status + "'";
          }
        i++;
      }
      s += ")";
      whereTerms.add(s);
    }

    whereTerms.add("mouse_id=" + mouseID);

    return getChangeRequests(additionalJoins,whereTerms,orderBy);
  }

  public static ArrayList<ChangeRequest> getChangeRequests(String status, String orderBy, int mouseID)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
          whereTerms.add("changerequest.status='" + status + "'");
      }

    whereTerms.add("mouse_id=" + mouseID);

    return getChangeRequests(additionalJoins,whereTerms,orderBy);
  }

  public static ArrayList<ChangeRequest> getChangeRequests(String status, String orderBy)
  {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
          whereTerms.add("changerequest.status='" + status + "'");
      }
      return getChangeRequests(additionalJoins,whereTerms,orderBy);

  }

  private static ArrayList<ChangeRequest> getChangeRequests(String additionalJoins,
                              ArrayList<String> whereTerms,
                              String orderBy)
  {
    String whereClause = "";
    String orderByClause = "";
    String joinsClause = "";
    if(whereTerms != null && !whereTerms.isEmpty())
    {
      String prefix = " WHERE";
      for(String whereTerm : whereTerms)
      {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }
    if(orderBy != null && !orderBy.equalsIgnoreCase(""))
    {
      orderByClause = "ORDER BY " + orderBy;
    }
    if(additionalJoins != null)
    {
      joinsClause = additionalJoins + "\r\n ";
    }

    String query = changeRequestQueryHeader + "\r\n " + joinsClause + whereClause + " " + orderByClause;
    return ChangeRequestResultGetter.getInstance().Get(query);
  }

  public static ArrayList<ImportReport> getAllImportReports()
  {
    String query = "SELECT * FROM import_reports order by id desc";
    return ImportReportResultGetter.getInstance().Get(query);
  }

  public static ArrayList<ImportReport> getImportReports(ImportObjectType type)
  {
    String query = "SELECT * FROM import_reports WHERE report_type=" + type.Id + " order by id desc";
    return ImportReportResultGetter.getInstance().Get(query);
  }

  public static ArrayList<ImportReport> getImportReport(int id)
  {
    String query = "SELECT * FROM import_reports where id=" + id;
    return ImportReportResultGetter.getInstance().Get(query);
  }


  public static ArrayList<String> getAllHolderNames()
  {
    return getAllHolderNames(true);
  }

  public static ArrayList<String> getAllHolderNames(boolean includeBlank)
  {
    ArrayList<Holder> allHolders = getAllHolders(includeBlank);
    ArrayList<String> results = new ArrayList<String>();
    results.add("Choose One");
    for (Holder holder : allHolders)
    {
      if (!includeBlank && holder.getHolderID() == 1) continue;
      results.add(holder.getLastname() + ", " + holder.getFirstname());
    }
      results.add("Other(specify)");
    return results;
  }

  public static ArrayList<String> getAllFacilityNames()
  {
    return getAllFacilityNames(false);
  }

  public static ArrayList<String> getAllFacilityNames(boolean includeBlank)
  {
    ArrayList<Facility> allFacilities = getAllFacilities(includeBlank);
    ArrayList<String> results = new ArrayList<String>();
    results.add("Choose One");
    for (Facility facility : allFacilities)
    {
      if (!includeBlank && facility.getFacilityID() == 1) continue;
      results.add(facility.getFacilityName());
    }
      results.add("Other(specify)");
    return results;
  }

  public static ArrayList<Integer> getMiceInFacility(int facilityID)
  {
    String query = "SELECT mouse_id FROM mouse_holder_facility WHERE facility_id=" + facilityID;
    return IntResultGetter.getInstance("mouse_id").Get(query);
  }

  public static ArrayList<Integer> getMiceWithHolder(int holderID)
  {
    String query = "SELECT mouse_id FROM mouse_holder_facility WHERE holder_id=" + holderID;
    return IntResultGetter.getInstance("mouse_id").Get(query);
  }

  public static ArrayList<Integer> getMiceWithGene(int geneID)
  {
    String query = "SELECT id FROM mouse WHERE gene_id=" + geneID + " OR target_gene_id=" + geneID;
    return IntResultGetter.getInstance("id").Get(query);
  }

  //************************************************************
  //UPDATE Methods
  //************************************************************

  public static void setSubmissionID(int mouseID, int submissionID)
  {
    String query = "UPDATE mouse SET submittedmouse_id=" + submissionID + " WHERE id=" + mouseID;
    executeNonQuery(query);
  }

  public static String updateMouseRecord(MouseRecord updatedRecord)
  {
    if(updatedRecord == null)
    {
      return "record was null";
    }
    if(recordExists(updatedRecord.getMouseID()) < 0)
    {
      return "record does not exist";
    }
    StringBuilder query = new StringBuilder();
    query.append("UPDATE mouse \r\n");
    query.append("SET name="+ (updatedRecord.getMouseName() != null ? "'" +
        addMySQLEscapes(updatedRecord.getMouseName()) + "'" : "NULL") +", \r\n");
    query.append("official_name="+ (updatedRecord.getOfficialMouseName() != null ? "'" +

        addMySQLEscapes(updatedRecord.getOfficialMouseName()) + "'" : "NULL") +", \r\n");

    query.append("mousetype_id="+ (updatedRecord.getMouseType() != null ? "'" +
        mouseTypeToTypeID(updatedRecord.getMouseType()) + "'" : "NULL") +", \r\n");

    query.append("gene_id="+ (updatedRecord.getGeneID() != null ? "'" +
        updatedRecord.getGeneID() + "'" : "NULL") +", \r\n");

    query.append("target_gene_id="+ (updatedRecord.getTargetGeneID() != null ? "'" +
        updatedRecord.getTargetGeneID() + "'" : "NULL") +", \r\n");

    query.append("modification_type="+ (updatedRecord.getModificationType() != null ? "'" +
        updatedRecord.getModificationType() + "'" : "NULL") +", \r\n");
    query.append("regulatory_element_comment="+ (updatedRecord.getRegulatoryElement() != null ? "'" +
        addMySQLEscapes(updatedRecord.getRegulatoryElement()) + "'" : "NULL") +", \r\n");

    query.append("expressedsequence_id="+ (updatedRecord.getExpressedSequence() != null ? "'" +
        expressedSequenceToTypeID(updatedRecord.getExpressedSequence()) + "'" : -1) +", \r\n");
    query.append("other_comment="+ (updatedRecord.getOtherComment() != null ? "'" +
        addMySQLEscapes(updatedRecord.getOtherComment()) + "'" : "NULL") +", \r\n");
    query.append("reporter_comment="+ (updatedRecord.getReporter() != null ? "'" +
        addMySQLEscapes(updatedRecord.getReporter()) + "'" : "NULL") +", \r\n");
    query.append("transgenictype_id="+ (updatedRecord.getTransgenicType() != null ? "'" +
        transgenicTypeToTypeID(updatedRecord.getTransgenicType()) + "'" : -1) +", \r\n");

    query.append("mta_required="+ (updatedRecord.getMtaRequired() != null ? "'" +
        updatedRecord.getMtaRequired() + "'" : "'N'") +", \r\n");
    query.append("general_comment="+ (updatedRecord.getGeneralComment() != null ? "'" +
        addMySQLEscapes(updatedRecord.getGeneralComment()) + "'" : "NULL") +", \r\n");
    query.append("strain="+ (updatedRecord.getBackgroundStrain() != null ? "'" +
        addMySQLEscapes(updatedRecord.getBackgroundStrain()) + "'" : "NULL") +", \r\n");

    query.append("source="+ (updatedRecord.getSource() != null ? "'" +
        addMySQLEscapes(updatedRecord.getSource()) + "'" : "NULL") +", \r\n");

    query.append("repository_catalog_number="+ (updatedRecord.getRepositoryCatalogNumber() != null ? "'" +
        updatedRecord.getRepositoryCatalogNumber() + "'" : "NULL") +", \r\n");
    query.append("repository_id="+ (updatedRecord.getRepositoryTypeID() != null ? "'" +
        updatedRecord.getRepositoryTypeID() + "'" : -1) +", \r\n");

    query.append("gensat="+ (updatedRecord.getGensat() != null ? "'" +
        addMySQLEscapes(updatedRecord.getGensat()) + "'" : "NULL") +", \r\n");
    query.append("cryopreserved="+ (updatedRecord.getCryopreserved() != null ? "'" +
        updatedRecord.getCryopreserved() + "'" : 0) +", \r\n");
    query.append("status=" + (updatedRecord.getStatus() != null ? "'" +
        updatedRecord.getStatus() + "'" : "NULL") + ", \r\n");
    query.append("endangered=" + updatedRecord.isEndangered() + "\r\n");

    query.append("WHERE id=" + updatedRecord.getMouseID());
    Log.Info("Updating mouse record with query: \r\n" + query);
    executeNonQuery(query.toString());

    //holders
    updateMouseHolders(updatedRecord);


    //pubmed ids
    updateMouseLiterature(updatedRecord);

    //search text

    updateMouseSearchTable(updatedRecord.getMouseID());

    return null;
  }

  public static void updateMouseSubmission(SubmittedMouse updatedSubmission) throws Exception
  {
    throw new Exception("not implemented");
  }


  public static void updateSubmission(int submissionID, String status, String notes)
  {
    String entered = status.equalsIgnoreCase("accepted") ? "Y" : "N";

    String query = "UPDATE submittedmouse SET status='" + status + "'";
    if(notes != null) query += ", admincomment='" + addMySQLEscapes(notes) + "'";
    query+= ", entered='" + entered + "' WHERE id=" + submissionID;
    executeNonQuery(query);
  }

  public static void updateSubmissionProperties(int submissionID, String properties)
  {

    String query = "UPDATE submittedmouse SET properties='" + properties + "' WHERE id=" + submissionID;
    executeNonQuery(query);
  }

  public static void updateChangeRequest(int requestID, String status, String notes)
  {
    Date now = new Date(System.currentTimeMillis());
    String query = "UPDATE changerequest " +
        "SET " +
        "status='" + status
        + "', admin_comment='" + addMySQLEscapes(notes) +
        "', lastadmindate='" + now
        + "'  WHERE id=" + requestID;
    executeNonQuery(query);
  }

  public static void updateFacility(Facility updatedFacility)
  {
    String query = "UPDATE facility " +
      "SET "
      + "facility='"  + addMySQLEscapes(updatedFacility.getFacilityName())
      + "',description='" + addMySQLEscapes(updatedFacility.getFacilityDescription())
      + "',code='" + addMySQLEscapes(updatedFacility.getFacilityCode())
      + "'\r\nWHERE id=" + updatedFacility.getFacilityID();
    executeNonQuery(query);
  }

  public static void updateHolder(Holder updatedHolder)
  {

    String dateValidated = updatedHolder.getDateValidated();

    if (dateValidated != null && !dateValidated.isEmpty() && !dateValidated.trim().isEmpty())
    {
      dateValidated = "'" + addMySQLEscapes(dateValidated) + "'";
    }
    else
    {
      dateValidated = "NULL";
    }

    String query = "UPDATE holder " +
        "SET "
        + "firstname='"  + addMySQLEscapes(updatedHolder.getFirstname())
        + "',lastname='" + addMySQLEscapes(updatedHolder.getLastname())
        + "',department='" + addMySQLEscapes(updatedHolder.getDept())
        + "',email='" + addMySQLEscapes(updatedHolder.getEmail())
        + "',alternate_email='" + addMySQLEscapes(updatedHolder.getAlternateEmail())
        + "',tel='" + addMySQLEscapes(updatedHolder.getTel())
        + "',datevalidated=" + dateValidated
        + "\r\nWHERE id=" + updatedHolder.getHolderID();
    executeNonQuery(query);
  }

  public static void updateGene(Gene updatedGene)
  {
    String query = "UPDATE gene " +
        "SET "
        + "fullname='"  + addMySQLEscapes(updatedGene.getFullname())
        + "',symbol='" + addMySQLEscapes(updatedGene.getSymbol())
        + "',mgi='" + addMySQLEscapes(updatedGene.getMgiID())
        + "'\r\nWHERE id=" + updatedGene.getGeneRecordID();
    executeNonQuery(query);
  }

  public static void updateImportReport(ImportReport updatedImportReport)
  {
    String query = "UPDATE import_reports " +
        "SET "
        + "name='"  + addMySQLEscapes(updatedImportReport.getName())
        + "',reporttext='" + addMySQLEscapes(updatedImportReport.getReportText())
        + "'\r\nWHERE id=" + updatedImportReport.getImportReportID();
    executeNonQuery(query);
  }

  //************************************************************
  //INSERT Methods
  //************************************************************

  public static int insertMouseRecord(MouseRecord newRecord)
  {
    String query = "INSERT into mouse (id) VALUES (NULL)";
    int mouseID = executeNonQuery(query,true);
    newRecord.setMouseID(String.valueOf(mouseID));
    updateMouseRecord(newRecord);
    return mouseID;
  }

  public static int addReference(int PubMedID)
  {
    int referenceID = -1;
    if((referenceID = referenceExists(PubMedID)) > 0)
    {
      return referenceID;
    }
    String query =
      "INSERT into literature (pmid) \r\n" +
      "VALUES ('" + PubMedID + "')";
    return executeNonQuery(query,true);
  }

  public static void addReferenceToMouse(int referenceID, int mouseID)
  {
    //TODO check for duplicates first

    String query = "INSERT into mouse_literature (literature_id, mouse_id) values ('" +
    referenceID + "','" + mouseID + "')";
    executeNonQuery(query);
  }

  public static void addHolderToMouse(int holderID, int facilityID, int mouseID)
  {

    String query = "INSERT into mouse_holder_facility (mouse_id, holder_id, facility_id) values ('" +
    mouseID + "', '" + holderID + "', '" + facilityID + "')";
    executeNonQuery(query);
  }

  //TODO make this an int
  //TODO refactor the code that calls this
  public static String addGeneManually(String geneID, String fullname, String symbol)
  {
    String newGeneID = "-1";
    //see if this gene already exists in our local genes table
    String query = "SELECT id FROM gene WHERE mgi='" + addMySQLEscapes(geneID.trim()) + "'";

    ArrayList<String> results = StringResultGetter.getInstance("id").Get(query);
    if (results.size() > 0)
    {
      return results.get(0);
    }
    //if it doesn't exist, insert it
    query = "INSERT INTO gene(fullname, symbol, mgi) \r\n" +
        "VALUES "
        +"('" + addMySQLEscapes(fullname)
        + "', '" + addMySQLEscapes(symbol)
        + "', '" + addMySQLEscapes(geneID.trim())
        + "')";
    newGeneID = String.valueOf(executeNonQuery(query,true));
    return newGeneID;
  }

  @SuppressWarnings("rawtypes")
  public static int insertAdminSubmission(String username, String notes, String status,  Properties props)
  {
    StringBuffer propsBuf = new StringBuffer();
        Enumeration names = props.propertyNames();
        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            propsBuf.append(name + "=" + props.getProperty(name) + "\t");
        }

    String query = "INSERT into submittedmouse (id,firstname,lastname,dept,address,email,tel,date,properties) " +
        "VALUES " +
          "(NULL,'" +
          addMySQLEscapes(username) +
          "', '','','','','', curdate(),'"+
          addMySQLEscapes(propsBuf.toString()) + "')";
    int subID = executeNonQuery(query,true);
    updateSubmission(subID, status, notes);
    return subID;

  }

  @SuppressWarnings("rawtypes")
  public static int insertSubmission(UserData submitterData, MouseSubmission submission, Properties props)
  {
    StringBuffer propsBuf = new StringBuffer();
        Enumeration names = props.propertyNames();
        while (names.hasMoreElements())
        {
            String name = (String) names.nextElement();
            propsBuf.append(name + "=" + props.getProperty(name) + "\t");
        }

    String query = "INSERT into submittedmouse " +
        "(id,firstname,lastname,dept,address,email,tel,date,properties) " +
        "VALUES " +
          "(NULL"
          + ",\r\n'"  + addMySQLEscapes(submitterData.getFirstName())
          + "',\r\n'"  + addMySQLEscapes(submitterData.getLastName())
          + "',\r\n'"  + addMySQLEscapes(submitterData.getDepartment())
          + "',\r\n'"  + ""
          + "',\r\n'"  + addMySQLEscapes(submitterData.getEmail())
          + "',\r\n'"  + addMySQLEscapes(submitterData.getTelephoneNumber())
          + "',\r\n curdate(),\r\n'"
          + addMySQLEscapes(propsBuf.toString()) + "')";
    return executeNonQuery(query,true);

  }

  public static int insertFacility(Facility newFacility)
  {
    String query = "INSERT into facility (id,facility,description,code) " +
      "VALUES (NULL"
      + ",'"  + addMySQLEscapes(newFacility.getFacilityName())
      + "','" + addMySQLEscapes(newFacility.getFacilityDescription())
      + "','" + addMySQLEscapes(newFacility.getFacilityCode())
      + "')";
    return executeNonQuery(query,true);
  }

  public static int insertHolder(Holder newHolder)
  {
    String query = "INSERT into holder (id,firstname,lastname,department,email,alternate_email,tel) " +
        "VALUES (NULL"
        + ",'"  + addMySQLEscapes(newHolder.getFirstname())
        + "','" + addMySQLEscapes(newHolder.getLastname())
        + "','" + addMySQLEscapes(newHolder.getDept())
        + "','" + addMySQLEscapes(newHolder.getEmail())
        + "','" + addMySQLEscapes(newHolder.getAlternateEmail())
        + "','" + addMySQLEscapes(newHolder.getTel())
        + "')";
    return executeNonQuery(query,true);
  }

  public static int insertGene(Gene newGene)
  {
    String query = "INSERT into gene (id,fullname,symbol,mgi) " +
        "VALUES (NULL"
        + ",'"  + addMySQLEscapes(newGene.getFullname())
        + "','" + addMySQLEscapes(newGene.getSymbol())
        + "','" + addMySQLEscapes(newGene.getMgiID())
        + "')";
    return executeNonQuery(query,true);
  }

  public static int insertChangeRequest(ChangeRequest newChangeRequest)
  {
    String query = "INSERT into changerequest " +
        "(id,mouse_id,firstname,lastname,email,status,user_comment," +
          "admin_comment,requestdate,properties) " +
        "VALUES (NULL"
        + ","  + newChangeRequest.getMouseID()
        + ",'"  + addMySQLEscapes(newChangeRequest.getFirstname())
        + "','" + addMySQLEscapes(newChangeRequest.getLastname())
        + "','" + addMySQLEscapes(newChangeRequest.getEmail())
        + "','" + addMySQLEscapes(newChangeRequest.getStatus())
        + "','" + addMySQLEscapes(newChangeRequest.getUserComment())
        + "','" + addMySQLEscapes(HTMLGeneration.emptyIfNull(newChangeRequest.getAdminComment()))
        + "'," + "curdate()"
        + ",'" + addMySQLEscapes(HTMLGeneration.emptyIfNull(newChangeRequest.getProperties()))
        + "')";
    return executeNonQuery(query,true);
  }

  public static int insertImportReport(ImportReport newReport)
  {
    String query = "INSERT into import_reports (name,report_type,creationdate,reporttext) " +
        "VALUES ("
        + "'"  + addMySQLEscapes(newReport.getName())
        + "', "  + newReport.getImportType().ordinal()
        + "," + "curdate()"
        + ",'" + addMySQLEscapes(newReport.getReportText())
        + "')";
    int reportId = executeNonQuery(query,true);

    if (newReport.getNewObjectIds() != null && newReport.getNewObjectIds().size() > 0)
    {
      query = "INSERT into import_new_objects (import_report_id,object_id) VALUES ";
      boolean first = true;
      for(int objectId : newReport.getNewObjectIds())
      {
        if (first)
          first = false;
        else
          query += ",";

        query += "(" + reportId + "," + objectId + ")";
      }

      query += ";";
      executeNonQuery(query);
    }


    return reportId;
  }

  public static ArrayList<Integer> getImportNewObjectIds(int reportId)
  {
    return IntResultGetter.getInstance("object_id").Get("SELECT * FROM import_new_objects WHERE import_report_id=" + reportId);
  }

  //************************************************************
  //DELETE Methods
  //************************************************************

  public static void deleteSubmission(int submissionID)
  {
    String query = "DELETE FROM submittedmouse WHERE id=" + submissionID;
    executeNonQuery(query);

    query = "SELECT id,status from mouse WHERE submittedmouse_id=" + submissionID;
    String[] columnNames = new String[]{"id","status"};

    ArrayList<ArrayList<String>> results = StringArrayListResultGetter.getInstance(columnNames).Get(query);

    if(results.size() > 0)
    {
      ArrayList<String> result = results.get(0);
      String status = result.get(1);
      if(status.equalsIgnoreCase("incomplete"))
      {
        int id = Integer.parseInt(result.get(0));
        deleteMouseRecord(id);
      }
    }
  }

  public static void deleteMouseRecord(int mouseID) {
    String query = "DELETE from mouse WHERE id=" + mouseID;
    executeNonQuery(query);

    deleteAllMouseHolders(mouseID);

    query = "DELETE from mouse_literature WHERE mouse_id=" + mouseID;
    executeNonQuery(query);

    query = "DELETE from flattened_mouse_search WHERE mouse_id=" + mouseID;
    executeNonQuery(query);
  }

  public static void deleteAllMouseHolders(int mouseID)
  {
    String query = "DELETE FROM mouse_holder_facility WHERE mouse_id=" + mouseID;
    executeNonQuery(query);
  }

  public static void deleteFacility(int facilityID)
  {
    String query = "DELETE FROM facility "
      + "\r\nWHERE id=" + facilityID;
    executeNonQuery(query);
  }

  public static void deleteHolder(int holderID)
  {
    String query = "DELETE FROM holder "
        + "\r\nWHERE id=" + holderID;
    executeNonQuery(query);
  }

  public static void deleteGene(int geneID)
  {
    String query = "DELETE FROM gene "
        + "\r\nWHERE id=" + geneID;
    executeNonQuery(query);
  }

  public static void deleteImportReport(int importReportID)
  {
    String query = "DELETE FROM import_reports "
        + "\r\nWHERE id=" + importReportID;
    executeNonQuery(query);
  }

  public static void deleteChangeRequest(int changeRequestID)
  {
    String query = "DELETE FROM changerequest "
        + "\r\nWHERE id=" + changeRequestID;
    executeNonQuery(query);
  }

  //************************************************************
  //HELPER Methods
  //************************************************************

  public static void updateSearchIndex() {
    ArrayList<String> mouseIds = StringResultGetter.getInstance("id").Get("select id from mouse");
    for(String mouseId : mouseIds) {
      updateMouseSearchTable(mouseId);
    }
  }
  
  public static void updateMouseSearchTable(String recordID)
  {
    ArrayList<MouseRecord> records = getMouseRecord(Integer.parseInt(recordID));
    if(records.size() <= 0)
    {
      return;
    }
    MouseRecord record = records.get(0);

    ArrayList<String> list = new ArrayList<String>();

    addFlattenedData(list, record.getBackgroundStrain());    
    addFlattenedData(list, record.getExpressedSequence());   
    addFlattenedData(list, record.getGeneID());    
    addFlattenedData(list, record.getGeneName());    
    addFlattenedData(list, record.getGeneralComment());
    addFlattenedData(list, record.getGeneSymbol());
    addFlattenedData(list, record.getGensat());
    addFlattenedData(list, record.getModificationType());
    addFlattenedData(list, record.getMouseID());
    addFlattenedData(list, record.getMouseName());
    addFlattenedData(list, record.getOtherComment());
    addFlattenedData(list, record.getRegulatoryElement());
    addFlattenedData(list, record.getReporter());
    addFlattenedData(list, record.getRepositoryCatalogNumber());
    addFlattenedData(list, record.getSource());
    addFlattenedData(list, record.getTargetGeneID());
    addFlattenedData(list, record.getTargetGeneName());
    addFlattenedData(list, record.getTargetGeneSymbol());   
    addFlattenedData(list, record.getTransgenicType());
    

    if(record.isEndangered())
    {
      addFlattenedData(list, "endangered ");
    }

    if(record.getCryopreserved() != null && record.getCryopreserved().equalsIgnoreCase("yes"))
    {
      addFlattenedData(list, "cryopreserved ");
    }

    for(MouseHolder hldr : record.getHolders())
    {
      addFlattenedData(list, hldr.getFullname() + " ");
    }

    for(String pmid : record.getPubmedIDs())
    {
      addFlattenedData(list, pmid + " ");
    }

    String query = "DELETE from flattened_mouse_search WHERE mouse_id='" + recordID + "'";
    executeNonQuery(query);

    query = "INSERT into flattened_mouse_search (mouse_id,searchtext) VALUES ('" +
    recordID + "', '" + addMySQLEscapes(StringUtils.join(list, " :: ")) + "')";
    executeNonQuery(query);
  }
  
  private static void addFlattenedData(ArrayList<String> list, Object value) {
    if (value != null) list.add(value.toString());
  }

  private static void updateMouseHolders(MouseRecord r)
  {
    //delete all existing holders

    String query = "DELETE FROM mouse_holder_facility WHERE mouse_id='" + r.getMouseID() + "'";
      executeNonQuery(query);

     //add all holders from the record
      for(MouseHolder holder : r.getHolders())
      {
          if (holder.getHolderID() != 1)
          {
              query = "INSERT INTO mouse_holder_facility (mouse_id, holder_id, facility_id," +
                  "covert,cryo_live_status) "
                      + " VALUES ('" + r.getMouseID() + "', " + holder.getHolderID() + ", " +
                      holder.getFacilityID() + ", " + holder.isCovert() + ", '" +
                      holder.getCryoLiveStatus() + "')";
              executeNonQuery(query);
          }
      }


  }

  private static void updateMouseLiterature(MouseRecord r)
  {
    //delete existing references
    String query = "DELETE FROM mouse_literature \r\nWHERE mouse_id=" + r.getMouseID();
    executeNonQuery(query);

    //add all references from the passed in record

    for (String pmid : r.getPubmedIDs())
    {
          if (pmid != null && pmid.length() > 0) {
              // First see if there is an entry already in literature, and get its primary key litID
              pmid = pmid.trim();
              int litID;
              query = "SELECT id \r\nFROM literature \r\nWHERE pmid='" + pmid + "'";
              ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
          if(results.size() > 0)
          {
            litID = results.get(0);
          }
              else
              {
              //create new literature entries if necessary
                    query = "INSERT INTO literature (pmid) \r\nVALUES ('" + pmid + "')";
                    litID = executeNonQuery(query,true);
              }
              query = "INSERT INTO mouse_literature (mouse_id, literature_id) \r\nVALUES('" +
              r.getMouseID() + "', '" + litID + "')";
                executeNonQuery(query);
          }
      }


  }

  public static int checkForDuplicates(int mouseMGIID, int currentRecordID)
  {
    String query = "SELECT id FROM mouse WHERE length(repository_catalog_number) != 0 " +
        "AND repository_catalog_number='" + mouseMGIID + "'";

    if (currentRecordID > 0)
    {
      query += " AND id<>" + currentRecordID;
    }
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    int recordID = -1;
    if (results.size() > 0)
    {
      recordID = results.get(0);
    }

    return recordID;
  }

  public static int checkForDuplicates(String inbredStrainSource)
  {
    //TODO add inbred strain type ID check
    String query = "SELECT id FROM mouse "+
      "WHERE source regexp '" + addMySQLEscapes(inbredStrainSource) + "'";

    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    int recordID = -1;
    if (results.size() > 0)
    {
      recordID = results.get(0);
    }

    return recordID;
  }

  public static int recordExists(String mouseRecordID)
  {
    String query = "SELECT id FROM mouse WHERE id='" + mouseRecordID + "'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if(results.size() > 0)
    {
      return results.get(0);
    }
    return -1;
  }

  public static int submissionExists(int mouseMgiId)
  {
    String query = "SELECT id FROM submittedmouse WHERE properties LIKE('%repository=" + mouseMgiId + "%') AND entered != 'Y'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if(results.size() > 0)
    {
      return results.get(0);
    }
    return -1;
  }

  public static int changeRequestExists(ChangeRequest changeRequest)
  {
    String query =
      "SELECT id " +
      "FROM changerequest " +
      "WHERE mouse_id=" + changeRequest.getMouseID() +
      " AND user_comment='" + changeRequest.getUserComment() +
      "' AND email='" + changeRequest.getEmail() + "'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if(results.size() > 0)
    {
      return results.get(0);
    }
    return -1;
  }


  public static int referenceExists(int PubMedID)
  {
    String query = "SELECT id FROM literature WHERE pmid='" + PubMedID + "'";

    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if(results.size() > 0)
    {
      return results.get(0);
    }
    return -1;
  }

  public static int mouseTypeToTypeID(String mouseType)
  {
    String query = "SELECT id \r\nFROM mousetype \r\nWHERE mousetype.mousetype='" + mouseType +"'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if(results.size() > 0)
    {
      return results.get(0);
    }
    return -1;

  }

  public static int transgenicTypeToTypeID(String transgenicType)
  {
    String query = "SELECT id \r\nFROM transgenictype \r\nWHERE transgenictype.transgenictype='" +
    transgenicType +"'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if(results.size() > 0)
    {
      return results.get(0);
    }
    return -1;

  }

  public static int expressedSequenceToTypeID(String expressedSequence)
  {
    String query = "SELECT id \r\nFROM expressedsequence " +
        "\r\nWHERE expressedsequence.expressedsequence='" + expressedSequence +"'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if(results.size() > 0)
    {
      return results.get(0);
    }
    return -1;
  }

  //***********************************************************
  //Report methods
  //***********************************************************
  public static String RunReport(String reportName, Object[] args)
  {
    if (reportName.equalsIgnoreCase(ReportServlet.RecordsReportName))
    {
      return runRecordsReport();
    }
    else if (reportName.equals(ReportServlet.HolderReportName))
    {
      String query =
        "SELECT holder.id as 'Holder ID', lastname as 'Last Name',firstname as 'First Name'," +
        "department as 'Department',email as 'E-Mail',datevalidated as 'Date of last review', " +
          "(select count(*) " +
          "\r\nFROM mouse_holder_facility " +
          "\r\nWHERE holder_id=holder.id) as 'Number of Mice Held'" +
        "\r\nFROM holder left join mouse_holder_facility  on mouse_holder_facility.holder_id = holder.id" +
        "\r\nWHERE holder.id<> 1" +
        "\r\nGROUP by holder.id" +
        "\r\nORDER BY lastname, firstname";

      String[] columnHeaders = new String[]{
          "Holder ID","Last Name","First Name","Department","E-Mail","Number of mice held","Date of last review"
          };
      return runSimpleReport(query,columnHeaders);
    }
    else if (reportName.equals(ReportServlet.LarcRecordHolderReportName))
    {
      String query =
        "SELECT t2.id as \"holder id\", " +
        "concat('\"',t2.lastname,'\"') as \"holder lastname\", " +
        "concat('\"',t2.firstname,'\"') as \"holder firstname\", " +
        "t3.id as \"database record ID\", " +
        "concat('\"',t3.name,'\"') as \"mouse name\", " +
        "concat('\"',source,'\"') as \"official symbol\" " +
        "FROM mouse_holder_facility t1 left join holder t2 on t1.holder_id=t2.id " +
        "left join mouse t3 on t1.mouse_id=t3.id " +
        "WHERE t3.id is not null and t3.status='live' " +
        "ORDER BY t2.lastname";

      String[] columnHeaders = new String[]{
          "holder id","holder lastname","holder firstname","database record ID",
          "mouse name","official symbol"
          };
      return runSimpleReport(query,columnHeaders,false);
    }
    else if (reportName.equals(ReportServlet.PPTChangeRequestImportReportName))
    {
      if (args == null)
      {
        throw new IndexOutOfBoundsException("No report specified for import change request report");
      }
      int reportId =  Integer.parseInt((String)args[0]);
      return runImportPPTChangeRequestReport(reportId);
    }
    else if (reportName.equals(ReportServlet.PurchaseChangeRequestImportReportName))
    {
      if (args == null)
      {
        throw new IndexOutOfBoundsException("No report specified for import change request report");
      }
      int reportId =  Integer.parseInt((String)args[0]);
      return runImportPurchaseChangeRequestReport(reportId);
    }
    else if (reportName.equals(ReportServlet.PurchaseSubmissionsImportReportName))
    {
      if (args == null)
      {
        throw new IndexOutOfBoundsException("No report specified for import change request report");
      }
      int reportId =  Integer.parseInt((String)args[0]);
      return runImportPurchaseSubmissionReport(reportId);
    }
    else
    {
      throw new IndexOutOfBoundsException("Report " + reportName + " not found.  " );
    }
  }

  private static String runSimpleReport(String query, String[] columnHeaders)
  {
    return runSimpleReport(query, columnHeaders, true);
  }

  private static String runSimpleReport(String query, String[] columnHeaders, boolean addQuotes)
  {
    StringBuilder result = new StringBuilder();

    boolean first = true;
    for(String header : columnHeaders)
    {
      if (first) {first = false;  }
      else {result.append(",");}

      result.append(header);
    }
    result.append("\r\n");

    ArrayList<ArrayList<String>> results = StringArrayListResultGetter.getInstance(columnHeaders).Get(query);

    for (ArrayList<String> ArrayList : results) {
      first = true;
      for(int i = 0; i < columnHeaders.length; i++)
      {
        if (first) { first = false;  }
        else { result.append(","); }

        if (addQuotes){ result.append("\""); }
        String value = ArrayList.get(i);
        result.append(value != null ? value : "");
        if (addQuotes){ result.append("\""); }
      }
      result.append("\r\n");
    }

    return result.toString();
  }

  private static String runRecordsReport()
  {
    StringBuilder result = new StringBuilder();
    int maxHolders = 5;
    result.append("Record Number,Mouse Name,Category,Official Symbol,MGI ID,PMID(s),MTA Status,Submitter Name");
    for (int i=0;i<maxHolders;i++)
    {
      result.append(",Holder " + (i + 1));
    }
    result.append("\r\n");
    ArrayList<MouseRecord> records = getMouseRecords(-1,null,-1,-1,"live");
    for (MouseRecord mouseRecord : records)
    {
      int submissionID = -1;
      try {
        submissionID = Integer.parseInt(mouseRecord.getSubmittedMouseID());
      }
      catch (Exception e)
      {}

      SubmittedMouse submission = getSubmission(submissionID);

      String pubmeds = "";
      boolean first = true;
      for (String pmid : mouseRecord.getPubmedIDs())
      {
        if (!first)
        {
          pubmeds += "; ";
        }
        first = false;
        pubmeds += pmid;
      }

      StringBuffer holderList = new StringBuffer();
      first = true;
      ArrayList<MouseHolder> holders = mouseRecord.getHolders();
      for (int i=0; i< maxHolders;i++)
      {
        if (!first)
        {
          holderList.append(",");
        }
        first = false;
        holderList.append("\"");
        if (i < holders.size())
        {
          holderList.append(holders.get(i).getFullname());
        }
        holderList.append("\"");
      }

      result.append(mouseRecord.getMouseID());
      result.append(",\"");
      result.append(mouseRecord.getMouseName());
      result.append("\",");
      result.append(mouseRecord.getMouseType());
      result.append(",\"");
      result.append(mouseRecord.getSource());
      result.append("\",");
      result.append(mouseRecord.getRepositoryCatalogNumber());
      result.append(',');
      result.append(pubmeds);
      result.append(',');
      result.append(mouseRecord.getMtaRequired());
      if (submission != null)
      {
        result.append(',');
        result.append('"');
        result.append(HTMLGeneration.emptyIfNull(submission.getFirstName()));
        result.append(' ');
        result.append(HTMLGeneration.emptyIfNull(submission.getLastName()));
        result.append('"');
      }
      result.append(',');
      result.append(holderList);
      result.append("\r\n");
    }
    return result.toString();
  }

  private static String runImportPPTChangeRequestReport(int reportId)
  {
    ArrayList<Integer> changeRequestIds = getImportNewObjectIds(reportId);
    if (changeRequestIds.size() <= 0)
    {
      return "No change requests were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on changerequest.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append("Email Sent,Request Number,PI Recipient,Mouse Name,Record #,Response,PI Sender,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<ChangeRequest> requests = getChangeRequests(additionalJoins, whereTerms,null);

    Comparator<ChangeRequest> comparator = new Comparator<ChangeRequest>(){
      public int compare(ChangeRequest a, ChangeRequest b)
      {
        return HTMLGeneration.emptyIfNull(a.Properties().getProperty("Recipient PI Name"))
          .compareTo(HTMLGeneration.emptyIfNull(b.Properties().getProperty("Recipient PI Name")));
      }
    };
    Collections.sort(requests,comparator);

    for (ChangeRequest request : requests)
    {
      Pattern ptn = Pattern.compile("([^=\t]+)=([^\\t]+)?");
      Matcher match = ptn.matcher(request.getProperties());

      Properties props = new Properties();
          while (match.find())
          {
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
          result.append(",");
      result.append(request.getRequestID());
      result.append(",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name")));
      result.append("\",\"");
      result.append(request.getMouseName());
      result.append("\",");
      result.append(request.getMouseID());
      result.append(",,\"");
          result.append(HTMLGeneration.emptyIfNull(props.getProperty("Original PI")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient email")));
      result.append("\"");
      result.append("\r\n");

    }
    return result.toString();
  }

  private static String runImportPurchaseChangeRequestReport(int reportId)
  {
    ArrayList<Integer> changeRequestIds = getImportNewObjectIds(reportId);
    if (changeRequestIds.size() <= 0)
    {
      return "No change requests were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on changerequest.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append("Message Sent,Response,Request Number,PI Recipient,Mouse Name,Record #,MGI ID,Catalog Number,Purchaser,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<ChangeRequest> requests = getChangeRequests(additionalJoins, whereTerms,null);

    Comparator<ChangeRequest> comparator = new Comparator<ChangeRequest>(){
      public int compare(ChangeRequest a, ChangeRequest b)
      {
        return HTMLGeneration.emptyIfNull(a.Properties().getProperty("Recipient PI Name"))
          .compareTo(HTMLGeneration.emptyIfNull(b.Properties().getProperty("Recipient PI Name")));
      }
    };

    Collections.sort(requests, comparator);

    for (ChangeRequest request : requests)
    {
      Properties props = request.Properties();
          result.append(",");
          result.append(",");
      result.append(request.getRequestID());
      result.append(",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name")));
      result.append("\",\"");
      result.append(request.getMouseName());
      result.append("\",");
      result.append(request.getMouseID());
      result.append(",");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("MouseMGIID")));
      result.append(",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("CatalogNumber")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser email")));
      result.append("\"");
      result.append("\r\n");

    }
    return result.toString();
  }
  private static final class Sub {
    public String PIName;
    public String Line;
    public Sub(String piName, String line)
    {
      PIName = piName;
      Line = line;
    }
  }
  private static String runImportPurchaseSubmissionReport(int reportId)
  {
    ArrayList<Integer> submissionIds = getImportNewObjectIds(reportId);
    if (submissionIds.size() <= 0)
    {
      return "No submissions were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on submittedmouse.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append("Message Sent,Response,Submission Number,PI Recipient,Mouse Name,MGI ID,Catalog Number,Purchaser,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<SubmittedMouse> submissions = getSubmissions(additionalJoins, whereTerms,null);

    List<Sub> linesByRecipientPI = new ArrayList<Sub>();


    for (SubmittedMouse submission : submissions)
    {
      Properties props = submission.getProperties();

      if (props.containsKey("holderCount"))
      {
        int holderCount = Integer.parseInt(props.getProperty("holderCount"));
        for(int i =0; i< holderCount;i++)
        {
          StringBuilder line = new StringBuilder();
          String piName = HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name-"+i ));
          line.append(",");
          line.append(",");
          line.append(submission.getSubmissionID());
          line.append(",\"");
          line.append(piName);
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("NewMouseName")));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("MouseMGIID")));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("CatalogNumber")));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser-"+i)));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email-"+i)));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser email-"+i)));
          line.append("\"");
          line.append("\r\n");
          linesByRecipientPI.add(new Sub(piName, line.toString()));
        }
      }
      else
      {
        StringBuilder line = new StringBuilder();
        String piName = HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name" ));
            line.append(submission.getSubmissionID());
        line.append(",\"");
        line.append(piName);
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("NewMouseName")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("MouseMGIID")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("CatalogNumber")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser email")));
        line.append("\"");
        line.append("\r\n");
        linesByRecipientPI.add(new Sub(piName, line.toString()));
      }
    }



    /*Comparator<Sub> comparator = new Comparator<Sub>(){
      public int compare(Sub a, Sub b)
      {
        return HTMLGeneration.emptyIfNull(a.PIName)
          .compareTo(HTMLGeneration.emptyIfNull(b.PIName));
      }
    };
    Collections.sort(linesByRecipientPI,comparator);
    */
    for (Sub sub : linesByRecipientPI) {
      //TODO why don't we want the PIName as well??
      result.append(sub.Line);
    }


    return result.toString();
  }

  //************************************************************
  //SQL Methods
  //************************************************************

  private static int executeNonQuery(String query)
  {
    return executeNonQuery(query, false);
  }

  private static int executeNonQuery(String query, boolean fetchLastInsertID)
  {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    int lastInsertID = -1;

    query = validateQuery(query);

    try
    {
      connection = connect();
      statement = connection.createStatement();
      if (logQueries) Log.Info("*****************************\r\n" +
          "Executing insert query:\r\n" + query);
      statement.execute(query);
      if (fetchLastInsertID)
      {
        resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next())
        {
          lastInsertID = resultSet.getInt("last_insert_id()");
        }
      }
    }
    catch (Exception e)
    {
      Log.Error("Error executing query: \r\n", e);
    }
    finally
    {
      try
      {
        if (resultSet != null) resultSet.close();
      }
      catch (Exception e)
      {
        Log.Error("Error closing SQL resultSet",e);
      }
      try
      {
        if (statement != null) statement.close();
      }
      catch (Exception e)
      {
        Log.Error("Error closing SQL statement.",e);
      }
      try
      {
        if (connection != null) connection.close();
      }
      catch (Exception e)
      {
        Log.Error("Error closing SQL connection.",e);
      }
    }
    return lastInsertID;
  }

  public static String addMySQLEscapes(String input)
  {
    if (input == null)
    {
      return "";
    }

    String output = input.replace("'", "\\'");
    output = output.replace("\"","\\\"");

    return output;
  }

  private static String validateQuery(String query)
  {
    String newQuery = query;
//    if (query.indexOf(';') > 0)
//    {
//      //Possible SQL injection attack
//      System.err.println("WARNING - Possible SQL injection attack in query: \r\n"
//          + query);
//
//      newQuery = query.substring(0,query.indexOf(';'));
//      System.err.println("Trimming query up to first semicolon.  Executing query below:\r\n"
//          + newQuery);
//
//    }
    return newQuery;
  }


  //************************************************************
  //Object accessor classes
  //************************************************************

  private abstract static class ResultGetter
  {
    protected Connection _connection = null;
    protected Statement _statement = null;
    protected ResultSet _resultSet = null;
    protected boolean _preserveConnection = false;

    protected boolean is_preserveConnection() {
      return _preserveConnection;
    }

    protected void set_preserveConnection(boolean _preserveConnection) {
      this._preserveConnection = _preserveConnection;
    }

    public final <T> ArrayList<T> Get(String query)
    {
      query = validateQuery(query);
      ArrayList<T> results = new ArrayList<T>();
      try
      {
        if (_connection == null)
        {
          _connection = connect();
          if (logQueries)
          {
            Log.Info("Opened connection " + _connection.toString());
          }
        }
        _statement = _connection.createStatement();

        if (logQueries)
        {
          Log.Info("*****************************\r\n" +
          "Executing query: \r\n" +
          "Connection:" + _connection.toString() + "\r\n" +
           query);
        }

        _resultSet =  _statement.executeQuery(query);

        getResults(results);
      }
      catch (Exception e)
      {
        Log.Error("Error executing query.\r\n" + "Connection:" +
            _connection.toString() + "\r\n"+ query,e);
      }
      finally
      {
        try
        {
          if (_resultSet != null) _resultSet.close();
        }
        catch (Exception e)
        {
          Log.Error("Error closing SQL resultset.",e);
        }
        try
        {
          if (_statement != null) _statement.close();
        }
        catch (Exception e)
        {
          Log.Error("Error closing SQL statement.\r\n",e);
        }
        try
        {
          if (!is_preserveConnection() && _connection != null)
          {
            if (logQueries)
            {
              System.out.print("Closing connection " + _connection.toString() + "...");
            }
            _connection.close();
            if (logQueries)
            {
              Log.Info("Closed!");
            }
          }
        }
        catch (Exception e)
        {
          Log.Error("Error closing SQL connection",e);
        }
      }
      return results;
    }

    @SuppressWarnings("unchecked")
    protected <T> void getResults(ArrayList<T> results) throws SQLException
    {
      while(_resultSet.next())
      {
        results.add((T)getNextItem());
      }
    }

    abstract protected Object getNextItem() throws SQLException;
  }

  private static final class MouseRecordResultGetter extends ResultGetter
  {
    public static MouseRecordResultGetter getInstance()
    {
      return new MouseRecordResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      MouseRecord nextMouse = new MouseRecord();

      nextMouse.setMouseID(_resultSet.getString("id"));
      nextMouse.setOfficialMouseName(_resultSet.getString("official_name"));
      nextMouse.setMouseName(_resultSet.getString("name"));
      nextMouse.setMouseType(_resultSet.getString("mousetype"));

      nextMouse.setGeneID(_resultSet.getString("gene mgi"));
      nextMouse.setGeneName(_resultSet.getString("gene name"));
      nextMouse.setGeneSymbol(_resultSet.getString("gene symbol"));

      nextMouse.setTargetGeneID(_resultSet.getString("target gene mgi"));
      nextMouse.setTargetGeneName(_resultSet.getString("target gene name"));
      nextMouse.setTargetGeneSymbol(_resultSet.getString("target gene symbol"));

      nextMouse.setModificationType(_resultSet.getString("modification_type"));
      nextMouse.setRegulatoryElement(_resultSet.getString("regulatory element"));

      nextMouse.setExpressedSequence(_resultSet.getString("expressedSequence"));
      nextMouse.setOtherComment(_resultSet.getString("other_comment"));
      nextMouse.setReporter(_resultSet.getString("reporter"));
      nextMouse.setTransgenicType(_resultSet.getString("transgenictype"));

      nextMouse.setMtaRequired(_resultSet.getString("mta_required"));
      nextMouse.setGeneralComment(_resultSet.getString("general_comment"));
      nextMouse.setBackgroundStrain(_resultSet.getString("strain"));

      nextMouse.setSource(_resultSet.getString("source"));
      nextMouse.setRepositoryTypeID(_resultSet.getString("repository_id"));
      nextMouse.setRepositoryCatalogNumber(_resultSet.getString("repository_catalog_number"));

      nextMouse.setGensat(_resultSet.getString("gensat"));

      nextMouse.setCryopreserved(_resultSet.getString("cryopreserved"));

      nextMouse.setStatus(_resultSet.getString("status"));

      nextMouse.setEndangered(_resultSet.getBoolean("endangered"));

      nextMouse.setSubmittedMouseID(_resultSet.getString("submittedmouse_id"));

      nextMouse.setHolders(getMouseHolders(nextMouse.getMouseID()));
      nextMouse.setPubmedIDs(getMousePubmedIDs(nextMouse.getMouseID()));
      return nextMouse;
    }

    private ArrayList<MouseHolder> getMouseHolders(String mouseID) throws SQLException
    {
      String query = mouseHolderQueryHeader +
        " WHERE mouse_id='" + mouseID + "' \r\nORDER BY lastname";

        return MouseHolderResultGetter.getInstance(_connection).Get(query);
    }

    private ArrayList<String> getMousePubmedIDs(String mouseID) throws SQLException
    {
      String query = "SELECT pmid \r\nFROM mouse_literature left join " +
          "literature on mouse_literature.literature_id=literature.id " +
          "\r\n WHERE mouse_id='" + mouseID + "'";
      return StringResultGetter.getInstance("pmid",_connection).Get(query);
    }
  }

  private static final class ChangeRequestResultGetter extends ResultGetter
  {
    public static ChangeRequestResultGetter getInstance()
    {
      return new ChangeRequestResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      ChangeRequest result = new ChangeRequest();

      result.setRequestID(_resultSet.getInt("changerequest.id"));
      int mouseRecordID = _resultSet.getInt("mouse_id");
      result.setMouseID(mouseRecordID <= 0 ? -1 : mouseRecordID);
      result.setMouseName(_resultSet.getString("mouse.name"));
      result.setAdminComment(_resultSet.getString("admin_comment"));
      result.setFirstname(_resultSet.getString("firstname"));
      result.setLastname(_resultSet.getString("lastname"));
      result.setEmail(_resultSet.getString("email"));
      result.setStatus(_resultSet.getString("changerequest.status"));
      result.setAdminComment(_resultSet.getString("admin_comment"));
      result.setUserComment(_resultSet.getString("user_comment"));

      result.setRequestDate(_resultSet.getDate("requestDate").toString());

      Date lastAdminDate = _resultSet.getDate("lastadmindate");
      result.setLastAdminDate(lastAdminDate != null ? lastAdminDate.toString() : "");

      result.setProperties(_resultSet.getString("properties"));

      return result;
    }
  }

  private static final class SubmittedMouseResultGetter extends ResultGetter
  {
    public static SubmittedMouseResultGetter getInstance()
    {
      return new SubmittedMouseResultGetter();
    }
    @Override
    protected Object getNextItem() throws SQLException
    {
      SubmittedMouse result = new SubmittedMouse();

      result.setSubmissionID(_resultSet.getInt("submittedmouse.id"));
      int mouseRecordID = _resultSet.getInt("mouseRecordID");
      result.setMouseRecordID(mouseRecordID <= 0 ? -1 : mouseRecordID);

      result.setFirstName(_resultSet.getString("firstname"));
      result.setLastName(_resultSet.getString("lastname"));
      result.setDepartment(_resultSet.getString("dept"));
      result.setEmail(_resultSet.getString("email"));
      result.setTelephoneNumber(_resultSet.getString("tel"));

      result.setSubmissionDate(_resultSet.getDate("date"));
      result.setStatus(_resultSet.getString("status"));
      result.setAdminComment(_resultSet.getString("admincomment"));
      result.setEntered(_resultSet.getString("entered").equalsIgnoreCase("Y"));

      result.parseProperties(_resultSet.getString("properties"));
      return result;
    }
  }

  private static final class MouseHolderResultGetter extends ResultGetter
  {

    private void set_connection(Connection connection)
    {
      _connection = connection;
    }
    public static MouseHolderResultGetter getInstance()
    {
      return getInstance(null);
    }

    public static MouseHolderResultGetter getInstance(Connection connection)
    {

      MouseHolderResultGetter instance = new MouseHolderResultGetter();
      if (connection != null)
      {
        instance.set_connection(connection);
        instance.set_preserveConnection(true);
      }
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      MouseHolder holder = new MouseHolder();
        holder.setHolderID(_resultSet.getInt("holder_id"));
        holder.setFirstname(_resultSet.getString("firstname"));
        holder.setLastname(_resultSet.getString("lastname"));
        holder.setDept(_resultSet.getString("department"));
        holder.setEmail(_resultSet.getString("email"));
        holder.setAlternateEmail(_resultSet.getString("alternate_email"));
        holder.setTel(_resultSet.getString("tel"));

        holder.setFacilityID(_resultSet.getInt("facility_id"));
        holder.setFacilityName(_resultSet.getString("facility"));

        holder.setCovert(_resultSet.getBoolean("covert"));
        holder.setCryoLiveStatus(_resultSet.getString("cryo_live_status"));
      return holder;
    }
  }

  private static final class FacilityResultGetter extends ResultGetter
  {
    public static FacilityResultGetter getInstance()
    {
      return new FacilityResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      Facility result = new Facility();
      result.setFacilityID(_resultSet.getInt("id"));
      result.setFacilityName(_resultSet.getString("facility"));
      result.setFacilityDescription(_resultSet.getString("description"));
      result.setFacilityCode(_resultSet.getString("code"));
      result.setRecordCount(_resultSet.getInt("mice held"));
      return result;

    }
  }

  private static final class HolderResultGetter extends ResultGetter
  {

    public static HolderResultGetter getInstance()
    {
      return new HolderResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      Holder result = new Holder();
        result.setHolderID(_resultSet.getInt("id"));
        result.setFirstname(_resultSet.getString("firstname"));
        result.setLastname(_resultSet.getString("lastname"));
        result.setDept(_resultSet.getString("department"));
        result.setEmail(_resultSet.getString("email"));
        result.setAlternateEmail(_resultSet.getString("alternate_email"));
        result.setTel(_resultSet.getString("tel"));
        result.setVisibleMouseCount(_resultSet.getInt("mice held"));
        result.setDateValidated(_resultSet.getString("datevalidated"));
        result.setValidationComment(_resultSet.getString("validation_comment"));
        return result;
    }
  }

  private static final class GeneResultGetter extends ResultGetter
  {

    public static GeneResultGetter getInstance()
    {
      return new GeneResultGetter();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> void getResults(ArrayList<T> results) throws SQLException
    {
      Gene result = null;
      Map<Integer, Gene> tempResults = new HashMap<Integer, Gene>();

        while(_resultSet.next())
        {
          result = new Gene();
          result.setGeneRecordID(_resultSet.getInt("id"));
          result.setFullname(_resultSet.getString("fullname"));
          result.setSymbol(_resultSet.getString("symbol"));
          result.setMgiID(_resultSet.getString("mgi"));
          results.add((T)result);
          tempResults.put(result.getGeneRecordID(), result);
        }

        String query = "select g1.id,g1.symbol,g1.fullname,g1.mgi,count(*) as 'record count'" +
          " from mouse left join gene g1 on mouse.gene_id=g1.id" +
          " left join gene g2 on mouse.target_gene_id=g2.id" +
          " where status='live'" +
          " group by g1.id";

        _resultSet = _statement.executeQuery(query);
        while (_resultSet.next())
        {
          Integer key = _resultSet.getInt("id");
          if (tempResults.containsKey(key))
          {
            tempResults.get(key).setRecordCount(_resultSet.getInt("record count"));
          }
        }
    }
    @Override
    protected Object getNextItem() throws SQLException {
      //unused because we override getResults in this class
      return null;
    }
  }

  private static final class StringArrayListResultGetter extends ResultGetter
  {
    private String[] _columnNames;

    private void set_columnName(String[] columnNames) {
      this._columnNames = columnNames;
    }

    public static StringArrayListResultGetter getInstance(String[] columnNames)
    {
      StringArrayListResultGetter instance = new StringArrayListResultGetter();
      instance.set_columnName(columnNames);
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      ArrayList<String> result = new ArrayList<String>();
      for (String columnName : _columnNames)
      {
        result.add(_resultSet.getString(columnName));
      }

      return result;
    }
  }

  private static final class StringResultGetter extends ResultGetter
  {
    private String _columnName;

    private void set_columnName(String columnName) {
      this._columnName = columnName;
    }
    private void set_connection(Connection connection)
    {
      _connection = connection;
    }

    public static StringResultGetter getInstance(String columnName)
    {
      return getInstance(columnName,null);
    }

    public static StringResultGetter getInstance(String columnName,Connection connection)
    {

      StringResultGetter instance = new StringResultGetter();
      if (connection != null)
      {
        instance.set_connection(connection);
        instance.set_preserveConnection(true);
      }
      instance.set_columnName(columnName);
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      return _resultSet.getString(_columnName);
    }
  }

  private static final class IntResultGetter extends ResultGetter
  {
    private String _columnName;

    private void set_columnName(String columnName) {
      this._columnName = columnName;
    }

    public static IntResultGetter getInstance(String columnName)
    {

      IntResultGetter  instance = new IntResultGetter();
      instance.set_columnName(columnName);
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      return _resultSet.getInt(_columnName);
    }
  }

  private static final class ImportReportResultGetter extends ResultGetter
  {

    public static ImportReportResultGetter getInstance()
    {
      return new ImportReportResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException
    {
      ImportReport result = new ImportReport();
        result.setImportReportID(_resultSet.getInt("id"));
        result.setImportType(ImportHandler.GetImportType(_resultSet.getInt("report_type")));
        result.setName(_resultSet.getString("name"));
        result.setCreationDate(_resultSet.getDate("creationdate"));
        result.setReportText(_resultSet.getString("reporttext"));
        return result;
    }
  }

  public static boolean isDuplicateSubmission(MouseSubmission newMouse, StringBuilder outputBuffer)
  {
    boolean isDuplicate = false;
      int existingRecordID = -1;

    if(newMouse.isPublished() && (newMouse.isTG() || newMouse.isMA()))
    {
        String repositoryCatalogID = newMouse.getMouseMGIID();
        if (repositoryCatalogID != null && !repositoryCatalogID.equalsIgnoreCase("none")) {
            //querier.setQuery("SELECT id FROM mouse WHERE repository_catalog_number='" + repositoryCatalogID + "'");
            //String existingMouseID = querier.getFirstQueryResult();
            existingRecordID = checkForDuplicates(Integer.parseInt(newMouse.getMouseMGIID()),-1);
        }
    }
    else if(newMouse.isIS())
    {
      //check supplier
      String supplier = newMouse.getISSupplier();
      if (newMouse.getISSupplierCatalogNumber() == null)
      {
        //don't dupe check records with no catalog number
        supplier = null;
      }
      else
      {
        supplier += ", " + newMouse.getISSupplierCatalogNumber();
      }
        if (supplier != null)
        {
          //TODO have different validation rules for non-jax mice
        String supplierRegex = supplier.trim().replace(",","[,]*");
        supplierRegex = supplierRegex.replace(" ","[ ]*");
            existingRecordID = checkForDuplicates(supplierRegex);
        }
    }
    if (existingRecordID > 0 )
    {
      ArrayList<MouseRecord> existingMice = getMouseRecord(existingRecordID);
      if (existingMice.size() > 0)
      {
        outputBuffer.append("<div class='duplicateDescriptor'><h4>Duplicate entry detected for submission with MGI ID " + newMouse.getMouseMGIID() + ".  Existing record ID is #" + existingRecordID + "</h4>");
        MouseRecord existingMouse = existingMice.get(0);

        if (!existingMouse.isHidden())
        {
            outputBuffer.append(HTMLGeneration.getMouseTable(existingMice,false,true,false));
          outputBuffer.append("<br>This appears to be a duplicate entry and will not be processed.<br>");
          isDuplicate = true;
        }
        else
        {
          outputBuffer.append("<br>NOTE: this is a DUPLICATE but the submission was allowed because that record is hidden.  (Incomplete, deleted, or only covert holders)");
        }
      }
      outputBuffer.append("</div>");
    }
    return isDuplicate;
  }
}
