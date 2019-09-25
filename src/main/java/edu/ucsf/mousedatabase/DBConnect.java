package edu.ucsf.mousedatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import edu.ucsf.mousedatabase.beans.MouseSubmission;
import edu.ucsf.mousedatabase.beans.UserData;
import edu.ucsf.mousedatabase.dataimport.ImportHandler;
import edu.ucsf.mousedatabase.dataimport.ImportHandler.ImportObjectType;
import edu.ucsf.mousedatabase.objects.*;
import edu.ucsf.mousedatabase.objects.ChangeRequest.Action;
import edu.ucsf.mousedatabase.servlets.ReportServlet;

import org.apache.commons.lang3.StringUtils;

public class DBConnect {

  // set this to true for debugging
  private static final boolean logQueries = false;

  private static final String mouseRecordTableColumns = "mouse.id, name, mousetype, modification_type,"
      + "transgenictype.transgenictype,regulatory_element_comment as 'regulatory element',"
      + "expressedsequence.expressedsequence, reporter_comment as 'reporter', strain, "
      + "general_comment, source, mta_required, repository_id, repository.repository, "
      + "repository_catalog_number,gensat,other_comment, gene.mgi as 'gene MGI', "
      + "gene.symbol as 'gene symbol', gene.fullname as 'gene name',cryopreserved,"
      + "status,endangered,submittedmouse_id, targetgenes.mgi as 'target gene MGI',"
      + "targetgenes.symbol as 'target gene symbol', targetgenes.fullname as 'target gene name', official_name,"
      + "admin_comment\r\n";

  private static final String mouseRecordTableJoins = "   left join mousetype on mouse.mousetype_id=mousetype.id\r\n"
      + "  left join gene on mouse.gene_id=gene.id\r\n"
      + "  left join gene as targetgenes on mouse.target_gene_id=targetgenes.id\r\n"
      + "  left join transgenictype on mouse.transgenictype_id=transgenictype.id\r\n"
      + "  left join expressedsequence on mouse.expressedsequence_id=expressedsequence.id\r\n"
      + "  left join repository on mouse.repository_id=repository.id\r\n ";

  private static final String mouseRecordQueryHeader = "SELECT " + mouseRecordTableColumns + " FROM mouse\r\n"
      + mouseRecordTableJoins;

  private static final String mouseRecordQueryCountHeader = "SELECT count(*) as count" + " FROM mouse\r\n"
      + mouseRecordTableJoins;

  private static final String mouseSubmissionQueryHeader = "SELECT  submittedmouse.* , mouse.id as mouseRecordID\r\n"
      + " FROM submittedmouse left join mouse on submittedmouse.id=mouse.submittedmouse_id\r\n ";

  private static final String mouseSubmissionQueryCountHeader = "SELECT  count(*) as count "
      + " FROM submittedmouse\r\n ";

  private static final String changeRequestQueryHeader = "SELECT changerequest.*, mouse.name, holder.firstname, holder.lastname, holder.email, facility.facility\r\n"
      + " FROM changerequest left join mouse on changerequest.mouse_id=mouse.id\r\n"
      + " left join holder on changerequest.holder_id=holder.id\r\n"
      + " left join facility on changerequest.facility_id=facility.id";

  private static final String changeRequestQueryCountHeader = "SELECT count(*) as count " + " FROM changerequest\r\n ";

  private static final String holderQueryHeader = "SELECT holder.*, (select count(*) \r\n"
      + " FROM mouse_holder_facility left join mouse on mouse_holder_facility.mouse_id=mouse.id\r\n"
      + " WHERE holder_id=holder.id and covert=0 and mouse.status='live') as 'mice held',\r\n" + "(select count(*) \r\n"
      + " FROM mouse_holder_facility left join mouse on mouse_holder_facility.mouse_id=mouse.id\r\n"
      + " WHERE holder_id=holder.id and covert=1 and mouse.status='live') as 'covert mice held'\r\n"
      + " FROM holder\r\n";

  private static final String facilityQueryHeader = "SELECT id, facility, description, code, local_experts"
      + ", (select count(*) from mouse_holder_facility where facility_id=facility.id) as 'mice held'\r\n"
      + " FROM facility\r\n ";

  private static final String mouseHolderQueryHeader = "SELECT holder_id, facility_id, covert, cryo_live_status, firstname, lastname, "
      + "department, email, alternate_email, alternate_name, tel, facility"
      + "\r\n FROM mouse_holder_facility t1 left join holder on t1.holder_id=holder.id "
      + "left join facility on t1.facility_id=facility.id \r\n";

  private static final String geneQueryHeader = "SELECT id,fullname,symbol,mgi \r\n FROM gene\r\n ";

  private static final String mouseIDSearchTermsRegex = "^(#[0-9]+,?\\s*)+$";

  private static Connection connect() throws Exception {
    try {

      Context initCtx = new InitialContext();
      Context envCtx = (Context) initCtx.lookup("java:comp/env");
      DataSource ds = (DataSource) envCtx.lookup("jdbc/mouse_inventory");

      return ds.getConnection();
    } catch (Exception e) {
      Log.Error("Problem connecting", e);
      throw e;
    }
  }

  // ************************************************************
  // VIEW Methods
  // ************************************************************
  public static ArrayList<SubmittedMouse> getMouseSubmissions(String status, String entered, String orderBy,
      String submissionSource) {
    return getMouseSubmissions(status, entered, orderBy, submissionSource, 0, 0);

  }

  public static ArrayList<SubmittedMouse> getMouseSubmissions(String status, String entered, String orderBy,
      String submissionSource, int limit, int offset) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
      whereTerms.add("submittedmouse.status='" + status + "'");
    }
    if (entered != null && !entered.isEmpty()) {
      whereTerms.add("entered='" + entered + "'");
    }
    if (submissionSource != null && !submissionSource.equalsIgnoreCase("all")) {
      whereTerms.add("submission_source like '%" + addMySQLEscapes(submissionSource) + "%'");
    }

    return getSubmissions(additionalJoins, whereTerms, orderBy, limit, offset);
  }

  public static int countMouseSubmissions(String status, String entered, String orderBy, String submissionSource) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
      whereTerms.add("submittedmouse.status='" + status + "'");
    }
    if (entered != null && !entered.isEmpty()) {
      whereTerms.add("entered='" + entered + "'");
    }
    if (submissionSource != null && !submissionSource.equalsIgnoreCase("all")) {
      whereTerms.add("submission_source like '%" + addMySQLEscapes(submissionSource) + "%'");
    }
    String whereClause = "";
    if (whereTerms != null && !whereTerms.isEmpty()) {
      String prefix = " WHERE";
      for (String whereTerm : whereTerms) {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }

    ArrayList<Integer> results = IntResultGetter.getInstance("count")
        .Get(mouseSubmissionQueryCountHeader + " " + whereClause);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  public static ArrayList<SubmittedMouse> getMouseSubmissions(List<Integer> submittedMouseIds) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";

    StringBuilder sb = new StringBuilder();
    sb.append("submittedmouse.id in(");
    boolean first = true;
    for (int id : submittedMouseIds) {
      if (first) {
        first = false;
      } else {
        sb.append(",");
      }
      sb.append(id);
    }
    sb.append(")");

    whereTerms.add(sb.toString());

    return getSubmissions(additionalJoins, whereTerms, null);
  }

  public static ArrayList<SubmittedMouse> getMouseSubmission(int submittedMouseID) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    whereTerms.add("submittedmouse.id=" + submittedMouseID);

    return getSubmissions(additionalJoins, whereTerms, null);
  }

  private static SubmittedMouse getSubmission(int submissionID) {
    String query = mouseSubmissionQueryHeader + " WHERE submittedmouse.id=" + submissionID;
    ArrayList<SubmittedMouse> results = SubmittedMouseResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;

  }

  private static ArrayList<SubmittedMouse> getSubmissions(String additionalJoins, ArrayList<String> whereTerms,
      String orderBy) {
    return getSubmissions(additionalJoins, whereTerms, orderBy, 0, 0);

  }

  private static ArrayList<SubmittedMouse> getSubmissions(String additionalJoins, ArrayList<String> whereTerms,
      String orderBy, int limit, int offset) {
    String whereClause = "";
    String orderByClause = "";
    String joinsClause = "";
    String limitClause = "";
    if (whereTerms != null && !whereTerms.isEmpty()) {
      String prefix = " WHERE";
      for (String whereTerm : whereTerms) {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }
    if (orderBy != null && !orderBy.equalsIgnoreCase("")) {
      orderByClause = "ORDER BY " + orderBy;
    }
    if (additionalJoins != null) {
      joinsClause = additionalJoins + "\r\n ";
    }
    if (limit > 0 && offset >= 0) {
      limitClause = "LIMIT " + offset + "," + limit;
    }

    String query = mouseSubmissionQueryHeader + "\r\n " + joinsClause + whereClause + " " + orderByClause + " "
        + limitClause;
    return SubmittedMouseResultGetter.getInstance().Get(query);
  }

  public static ArrayList<MouseRecord> getMouseRecord(int mouseID) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("mouse.id=" + mouseID);
    String constraints = buildMouseQueryConstraints(null, whereTerms, null, -1, -1);
    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }

  public static ArrayList<MouseRecord> getMouseRecords(List<Integer> mouseIds, boolean preserveOrder) {
    if (preserveOrder) {
      // TODO make this more efficient
      // todo make this preserve the order, use a temp table and join it
      ArrayList<MouseRecord> records = new ArrayList<MouseRecord>();
      for (Integer mouseId : mouseIds) {
        records.addAll(getMouseRecord(mouseId));
      }
      return records;
    }
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("mouse.id in(" + (mouseIds.size() > 0 ? StringUtils.join(mouseIds, ",") : "0") + ")");
    String constraints = buildMouseQueryConstraints(null, whereTerms, null, -1, -1);
    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }

  public static ArrayList<MouseRecord> getMouseRecordFromSubmission(int submissionID) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("mouse.submittedmouse_id=" + submissionID);
    String constraints = buildMouseQueryConstraints(null, whereTerms, null, -1, -1);
    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID,
      String status) {
    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, null);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID,
      String status, String searchTerms) {
    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, false);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID,
      String status, String searchTerms, boolean endangeredOnly) {

    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, -1);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID,
      String status, String searchTerms, boolean endangeredOnly, int creOnly) {

    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, creOnly,
        -1);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID,
      String status, String searchTerms, boolean endangeredOnly, int creOnly, int facilityID) {
    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, creOnly,
        facilityID, -1, -1);
  }

  public static ArrayList<MouseRecord> getCovertMice(String orderby, String status, String searchTerms,
      int mouseTypeID) {
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

    ArrayList<String> whereTerms = buildMouseQueryWhereTerms(status, searchTerms, mouseTypeID, -1, -1, -1, -1, false,
        false);
    whereTerms.add("mouse.id in (" + builder + ")");

    String constraints = buildMouseQueryConstraints(null, whereTerms, orderby, -1, -1);
    return new MouseRecordResultGetter()
        .Get(buildMouseQuery(mouseRecordQueryHeader + buildMouseQueryJoins(-1, -1, searchTerms), constraints));
  }

  public static int countMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID, String status,
      String searchTerms, boolean endangeredOnly, int creOnly, int facilityID) {
    return countMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, creOnly,
        facilityID, false);
  }

  public static int countMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID, String status,
      String searchTerms, boolean endangeredOnly, int creOnly, int facilityID, boolean edit) {

    ArrayList<String> whereTerms = buildMouseQueryWhereTerms(status, searchTerms, mouseTypeID, geneRecordID, facilityID,
        holderID, creOnly, endangeredOnly, edit);
    String additionalJoins = buildMouseQueryJoins(holderID, facilityID, searchTerms);

    String constraints = buildMouseQueryConstraints(additionalJoins, whereTerms, orderBy, -1, -1);

    ArrayList<Integer> results = IntResultGetter.getInstance("count")
        .Get(buildMouseQuery(mouseRecordQueryCountHeader, constraints));
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID,
      String status, String searchTerms, boolean endangeredOnly, int creOnly, int facilityID, int limit, int offset) {
    return getMouseRecords(mouseTypeID, orderBy, holderID, geneRecordID, status, searchTerms, endangeredOnly, creOnly,
        facilityID, limit, offset, false);
  }

  public static ArrayList<MouseRecord> getMouseRecords(int mouseTypeID, String orderBy, int holderID, int geneRecordID,
      String status, String searchTerms, boolean endangeredOnly, int creOnly, int facilityID, int limit, int offset,
      boolean edit) {

    ArrayList<String> whereTerms = buildMouseQueryWhereTerms(status, searchTerms, mouseTypeID, geneRecordID, facilityID,
        holderID, creOnly, endangeredOnly, edit);
    String additionalJoins = buildMouseQueryJoins(holderID, facilityID, searchTerms);

    String constraints = buildMouseQueryConstraints(additionalJoins, whereTerms, orderBy, limit, offset);

    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }

  public static ArrayList<MouseRecord> getMouseRecordsByIds(ArrayList<Integer> ids) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("mouse.id in(" + (ids.size() > 0 ? StringUtils.join(ids, ",") : "0") + ")");
    String constraints = buildMouseQueryConstraints(null, whereTerms, null, -1, -1);
    return new MouseRecordResultGetter().Get(buildMouseQuery(mouseRecordQueryHeader, constraints));
  }

  private static String buildMouseQueryJoins(int holderID, int facilityID, String searchTerms) {
    String additionalJoins = "";
    if (holderID != -1 || facilityID != -1) {
      additionalJoins += "  left join mouse_holder_facility on mouse.id=mouse_holder_facility.mouse_id\r\n ";
    }
    if (searchTerms != null && !searchTerms.isEmpty()) {
      additionalJoins += "  left join flattened_mouse_search on mouse.id=flattened_mouse_search.mouse_id";
    }
    return additionalJoins;
  }

  private static ArrayList<String> buildMouseQueryWhereTerms(String status, String searchTerms, int mouseTypeID,
      int geneRecordID, int facilityID, int holderID, int creOnly, boolean endangeredOnly, boolean edit) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    if (status.equalsIgnoreCase("all")) {
      whereTerms.add("mouse.status<>'incomplete'");
    } else {
      whereTerms.add("mouse.status='" + status + "'");
    }
    if (mouseTypeID != -1) {
      whereTerms.add("mousetype.id=" + mouseTypeID);
    }
    if (geneRecordID != -1) {
      whereTerms.add("(gene_id=" + geneRecordID + " or target_gene_id=" + geneRecordID + ")");
    }
    if (facilityID != -1) {
      whereTerms.add("(facility_id=" + facilityID + ")");
    }
    if (holderID != -1) {
      whereTerms.add("holder_id=" + holderID);
      if (!edit) {
        whereTerms.add("covert=false");
      }
    }
    if (searchTerms != null && !searchTerms.isEmpty()) {
      ArrayList<SearchResult> results = doMouseSearch(searchTerms, status);
      ArrayList<Integer> allMatches = new ArrayList<Integer>();
      for (SearchResult result : results) {
        allMatches.addAll(result.getMatchingIds());
      }
      whereTerms.add("mouse.id in(" + (allMatches.size() > 0 ? StringUtils.join(allMatches, ",") : "0") + ")");
    }
    if (endangeredOnly) {
      whereTerms.add("endangered=true");
    }
    if (creOnly > 0) {
      whereTerms.add("expressedsequence.expressedsequence='Cre'");
    }

    return whereTerms;
  }

  public static ArrayList<SearchResult> doMouseSearch(String searchTerms, String status) {
    ArrayList<SearchResult> resultSets = new ArrayList<SearchResult>();
    searchTerms = searchTerms.trim();

    if (searchTerms.matches(mouseIDSearchTermsRegex)) {
      SearchResult result = new SearchResult();
      ArrayList<Integer> mouseIds = new ArrayList<Integer>();
      SearchStrategy strat = new SearchStrategy(0, "record-id", "Exact record number lookup",
          "Preceding numbers with the '#' sign caused only the record number to matched.");
      // if the user enters '#101', we give them record 101 only.
      // if they enter '#101,#102', we give them records 101 and 102.
      Log.Info("SearchDebug: loading record numbers from terms " + searchTerms);
      ArrayList<Integer> notFound = new ArrayList<Integer>();
      String[] tokens = StringUtils.splitByCharacterType(searchTerms);
      for (String token : tokens) {
        if (token.matches("[0-9]+")) {
          int id = Integer.parseInt(token);
          ArrayList<MouseRecord> mice = getMouseRecord(id);
          if (mice.size() > 0) {
            MouseRecord mouse = mice.get(0);
            if (status.equalsIgnoreCase("all") || mouse.getStatus().equals(status)) {
              mouseIds.add(id);
            }
          } else {
            notFound.add(id);
          }
        }
      }

      strat.setTokens(tokens);
      result.setMatchingIds(mouseIds);
      result.setStrategy(strat);
      resultSets.add(result);
      Log.Info(
          "SearchDebug: loaded record numbers from terms " + searchTerms + " => " + StringUtils.join(mouseIds, ","));
    } else {
      List<SearchStrategy> strategies = new ArrayList<SearchStrategy>();

      boolean wildcardAdded = false;
      if (searchTerms.matches(".*[-/\\)\\(\\.].*")) {
        strategies.add(new SearchStrategy(0, "like-wildcard", "Exact phrase matches",
            "Matches records with the exact phrase as you typed it"));
        wildcardAdded = true;
        if (searchTerms.indexOf(" ") > 0) {
          strategies.add(new SearchStrategy(0, "like-wildcard-split-special", "Exact term matches",
              "Matches records that contain the terms you entered, anywhere in the text"));
        }
        strategies.add(new SearchStrategy(2, "word", "Partial term matches",
            "Interprets special characters such as hyphens and parentheses as spaces, thereby splitting the query into separate terms, then matches records containing all the terms in your query, in any order or position. <br>Single-character terms are ignored."));
        strategies.add(new SearchStrategy(2, "word-expanded", "Partial term matches (expanded)",
            "Interprets special characters such as hyphens and parentheses as spaces, thereby splitting the query into separate terms, then matches records containing terms that begin with the letters of the terms in your query. <br>Single-character terms are ignored."));
      } else if (searchTerms.matches("(.* .*)+")) {
        strategies.add(new SearchStrategy(0, "like-wildcard", "Exact phrase matches",
            "Matches records with the exact phrase as you typed it"));
        wildcardAdded = true;
        strategies.add(new SearchStrategy(0, "word", "Exact term matches",
            "Matches records that contain all of the terms in your query, in any order or position.  <br>Single-character terms are ignored."));
        strategies.add(new SearchStrategy(2, "word-expanded", "Partial term matches",
            "Interprets special characters such as hyphens and parentheses as spaces, thereby splitting the query into separate terms, then matches records containing terms that begin with the letters of the terms in your query. <br>Single-character terms are ignored."));
      } else {
        strategies.add(
            new SearchStrategy(0, "word", "Exact term matches", "Matches records containing the term in your query"));
        strategies.add(new SearchStrategy(2, "word-expanded", "Partial term matches",
            "Matches records containing terms that begin with the letters of the term in your query. <br>Single-character terms are ignored."));
      }

      if (!wildcardAdded && !searchTerms.matches(".* .*") && searchTerms.matches(".*\\w.*")
          && searchTerms.matches(".*\\d.*")) {
        strategies.add(new SearchStrategy(5, "like-wildcard", "Partial phrase matches",
            "When the query contains both letters and numbers, matches records that contain the exact phrase you entered, anywhere in the text"));
        wildcardAdded = true;
      }

      strategies.add(new SearchStrategy(5, "like-wildcard-split", "Partial split matches",
          "Splits your query into terms, and matches records that contain the letters of each term you entered, anywhere in the text"));

      // strategies.add(new SearchStrategy(8,"word-chartype","Partial sub-word
      // matches"));
      strategies.add(new SearchStrategy(8, "word-chartype-expanded", "Partial sub-term matches",
          "Splits your query into terms based on character type, such as letters, numbers, or special characters, and matches records that contain all of those terms,  e.g. wnt12 is split into 'wnt' and '12'.  Matches records that contain terms starting with those letters or numbers. <br>Single-character terms are ignored."));
      strategies.add(new SearchStrategy(8, "like-wildcard-split-chartype", "Partial sub-term split matches",
          "Splits your query into terms based on character type, such as letters, number or special characters, and matches records that contain the letters of each term you entered, anywhere in the text"));

      if (!wildcardAdded) {
        strategies.add(new SearchStrategy(8, "like-wildcard", "Partial phrase matches",
            "Matches records that contain the exact phrase you entered, anywhere in the text"));
        wildcardAdded = true;
      }

      if (searchTerms.indexOf(" ") > 0) {
        strategies.add(new SearchStrategy(8, "natural-5", "Partial matches",
            "Matches records that contain most of the terms in your query.  Records that are the best matches appear at the top of the list."));
        strategies.add(new SearchStrategy(9, "natural-0", "Possible matches",
            "Matches records that contain any of the terms in your query.  Records that are the best matches appear at the top of the list."));

      } else {
        strategies.add(new SearchStrategy(8, "natural-tokenize-5", "Partial matches",
            "Interprets special characters such as hyphens and parentheses as spaces, thereby splitting the query into separate terms, and matches records that contain most of those terms.  Records that are the best matches appear at the top of the list. <br>Single-character terms are ignored."));

        strategies.add(new SearchStrategy(9, "natural-tokenize-0", "Possible matches",
            "Interprets special characters such as hyphens and parentheses as spaces, thereby splitting the query into separate terms, and matches records that contain any of those terms. Records that are the best matches appear at the top of the list.  <br>Single-character terms are ignored."));

      }

      ArrayList<Integer> allMouseIds = new ArrayList<Integer>();
      for (SearchStrategy strategy : strategies) {
        if (allMouseIds.size() > 0 && strategy.getQualityValue() > 8) {
          continue;
        }

        SearchResult result = new SearchResult();
        ArrayList<Integer> mouseIds = doMouseSearchQuery(searchTerms, strategy, status);
        if (!allMouseIds.isEmpty()) {
          ArrayList<Integer> temp = new ArrayList<Integer>();
          for (int id : mouseIds) {
            if (!allMouseIds.contains(id)) {
              temp.add(id);
            }
          }
          mouseIds = temp;
        }

        allMouseIds.addAll(mouseIds);
        result.setStrategy(strategy);
        result.setMatchingIds(mouseIds);
        resultSets.add(result);
      }
    }

    return resultSets;
  }

  private static ArrayList<Integer> doMouseSearchQuery(String searchTerms, SearchStrategy strategy, String status) {

    String query = "select mouse_id from flattened_mouse_search, mouse WHERE ";
    String statusTerm;
    String orderBy = "mouse_id";
    if (status.equalsIgnoreCase("all")) {
      statusTerm = " and mouse.status<>'incomplete'";
    } else {
      statusTerm = " and mouse.status='" + status + "'";
    }
    searchTerms = StringUtils.remove(searchTerms, '"');
    searchTerms = StringUtils.remove(searchTerms, '\'');
    searchTerms = StringUtils.replace(searchTerms, "\\", " ");
    searchTerms = StringUtils.replace(searchTerms, "#", " ");

    String[] tokens = tokenize(searchTerms, false, false);
    if (strategy.getName().equals("natural-5")) {
      query += "match(searchtext) against('" + searchTerms + "') > 5";
      orderBy = "match(searchtext) against('" + searchTerms + "') desc";
    } else if (strategy.getName().equals("natural-0")) {
      query += "match(searchtext) against('" + searchTerms + "') > 0";
      orderBy = "match(searchtext) against('" + searchTerms + "') desc";
    } else if (strategy.getName().equals("natural-tokenize-5")) {
      query += "match(searchtext) against('" + StringUtils.join(tokens, ' ') + "') > 5";
      orderBy = "match(searchtext) against('" + searchTerms + "') desc";
    } else if (strategy.getName().equals("natural-tokenize-0")) {
      query += "match(searchtext) against('" + StringUtils.join(tokens, ' ') + "') > 0";
      orderBy = "match(searchtext) against('" + searchTerms + "') desc";
    } else if (strategy.getName().equals("word")) {
      query += "match(searchtext) against(" + tokenizeBoolean(tokens, false) + ")";
    } else if (strategy.getName().equals("word-expanded")) {
      query += "match(searchtext) against(" + tokenizeBoolean(tokens, true) + ")";
    } else if (strategy.getName().equals("word-chartype")) {
      tokens = tokenize(searchTerms, true, false);
      query += "match(searchtext) against(" + tokenizeBoolean(tokens, false) + ")";
    } else if (strategy.getName().equals("word-chartype-expanded")) {
      tokens = tokenize(searchTerms, true, false);
      query += "match(searchtext) against(" + tokenizeBoolean(tokens, true) + ")";
    } else if (strategy.getName().equals("like-wildcard")) {
      tokens = new String[] { searchTerms };
      query += "searchtext LIKE ('%" + addMySQLEscapes(searchTerms) + "%')";
    } else if (strategy.getName().equals("like-wildcard-split")) {
      boolean first = true;
      for (String token : tokens) {
        if (first) {
          first = false;
        } else {
          query += " AND ";
        }
        query += "searchtext LIKE ('%" + addMySQLEscapes(token) + "%')";
      }
    } else if (strategy.getName().equals("like-wildcard-split-chartype")) {
      tokens = tokenize(searchTerms, true, false);
      boolean first = true;
      for (String token : tokens) {
        if (first) {
          first = false;
        } else {
          query += " AND ";
        }
        query += "searchtext LIKE ('%" + addMySQLEscapes(token) + "%')";
      }
    } else if (strategy.getName().equals("like-wildcard-split-special")) {
      tokens = tokenize(searchTerms, false, true);
      boolean first = true;
      for (String token : tokens) {
        if (first) {
          first = false;
        } else {
          query += " AND ";
        }
        query += "searchtext LIKE ('%" + addMySQLEscapes(token) + "%')";
      }
    } else {
      // invalid search strategy, default to natural
      Log.Error("Invalid search strategy: " + strategy + ", defaulting to natural language search");
      query += "match(searchtext) against(" + searchTerms + ")";
    }
    strategy.setTokens(tokens);
    query += " and mouse_id=mouse.id" + statusTerm;
    if (orderBy != null) {
      query += " ORDER BY " + orderBy;
    }
    // Log.Info("SearchDebug:[" + strategy.getName() + "] " + query);
    return IntResultGetter.getInstance("mouse_id").Get(query);
  }

  private static String[] tokenize(String searchTerms, boolean charType, boolean spacesOnly) {
    String[] tokens;
    if (charType) {
      tokens = StringUtils.splitByCharacterType(searchTerms.toLowerCase());
    } else if (spacesOnly) {
      tokens = StringUtils.split(searchTerms, " ");
    } else {
      tokens = StringUtils.split(searchTerms, " -\\()/.");
    }
    List<String> acceptableTokens = new ArrayList<String>();
    for (String token : tokens) {
      if (token.length() > 1) {
        acceptableTokens.add(token);
      }
    }
    return acceptableTokens.toArray(new String[acceptableTokens.size()]);
  }

  private static String tokenizeBoolean(String[] tokens, boolean expand) {
    ArrayList<String> terms = new ArrayList<String>();
    for (String token : tokens) {
      String term = "+" + token;
      if (expand) {
        term += "*";
      }
      terms.add(term);
    }
    return "'" + StringUtils.join(terms, " ") + "' IN BOOLEAN MODE";
  }

  private static String buildMouseQueryConstraints(String additionalJoins, ArrayList<String> whereTerms, String orderBy,
      int limit, int offset) {
    String whereClause = "";
    String orderByClause = "";
    String joinsClause = "";
    String limitClause = "";
    if (whereTerms != null && !whereTerms.isEmpty()) {
      String prefix = " WHERE";
      for (String whereTerm : whereTerms) {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }
    if (orderBy != null && !orderBy.equalsIgnoreCase("")) {
      orderByClause = "ORDER BY " + orderBy;
    }
    if (additionalJoins != null) {
      joinsClause = additionalJoins + "\r\n ";
    }
    if (limit > 0 && offset >= 0) {
      limitClause = "LIMIT " + offset + "," + limit;
    }

    return joinsClause + whereClause + " " + orderByClause + " " + limitClause;
  }

  private static String buildMouseQuery(String selectFrom, String constraints) {
    return selectFrom + "\r\n " + constraints;
  }

  public static ArrayList<MouseHolder> getAllMouseHolders() {
    return MouseHolderResultGetter.getInstance().Get(mouseHolderQueryHeader + " WHERE status='active'");
  }

  public static Holder getHolder(int holderID) {
    String query = holderQueryHeader + " WHERE id='" + holderID + "'";

    ArrayList<Holder> results = HolderResultGetter.getInstance().Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return null;

  }

  public static Holder findHolder(String holderFullName) {
    if (holderFullName == null) {
      return new Holder();
    }

    String firstname = "";
    String lastname = "";
    if (holderFullName.length() > 1 && holderFullName.indexOf(",") > 0) {
      String[] holderName = holderFullName.split("[,]");
      firstname = holderName[1];
      lastname = holderName[0];
    } else if (holderFullName.length() > 1 && holderFullName.indexOf(" ") > 0) {
      String[] parts = holderFullName.split("[| ]");
      if (parts.length == 3) {
        firstname = parts[1];
        lastname = parts[2];
      } else if (parts.length == 2) {
        firstname = parts[0];
        lastname = parts[1];
      }
    } else {
      Log.Info("Unrecognized holder fullname format: " + holderFullName);
      return null;
    }
    String query = null;
    query = holderQueryHeader + " WHERE status='active' and (firstname='" + addMySQLEscapes(firstname).trim()
        + "' and lastname='" + addMySQLEscapes(lastname).trim() + "')";
    ArrayList<Holder> results = HolderResultGetter.getInstance().Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return null;
  }

  public static Holder findHolderByEmail(String holderEmail) {
    if (holderEmail == null) return null;
    String query = null;
    if (!holderEmail.isEmpty()) {
      query = holderQueryHeader + " WHERE status='active' and email like '%" + holderEmail.trim() + "%'";
    } else {
      return null;
    }
    ArrayList<Holder> results = HolderResultGetter.getInstance().Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return null;
  }

  public static Facility findFacilityByCode(String facilityCode) {
    if (facilityCode != null && !facilityCode.isEmpty()) {
      ArrayList<Facility> facilities = getAllFacilities();
      for (Facility f : facilities) {
        if (f.getFacilityCode() == null || f.getFacilityCode().isEmpty()) {
          continue;
        }
        if (Pattern.compile(f.getFacilityCode()).matcher(facilityCode).find()) {
          return f;
        }
      }
    }
    return null;
  }

  public static ArrayList<Holder> getAllHolders() {
    return getAllHolders(true);
  }

  public static ArrayList<Holder> getAllHolders(String orderby) {
    return getAllHolders(true, orderby);
  }

  public static ArrayList<Holder> getAllHolders(boolean includeBlank) {
    return getAllHolders(includeBlank, null);
  }

  public static ArrayList<Holder> getAllHolders(boolean includeBlank, String orderby) {
    if (orderby != null && orderby.equals("count")) {
      orderby = "`mice held` desc";
    }
    String query = holderQueryHeader;
    if (!includeBlank) {
      query += " WHERE id > 1 and status='active'";
    } else {
      query += " WHERE status='active'";
    }
    query += "\r\n ORDER BY ";
    query += orderby != null ? orderby : "lastname, firstname";
    return HolderResultGetter.getInstance().Get(query);
  }

  public static Facility getFacility(int facilityID) {
    String query = facilityQueryHeader + " WHERE id=" + facilityID + "";
    ArrayList<Facility> results = FacilityResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;
  }

  public static Facility findFacility(String facilityName) {
    String query = facilityQueryHeader + " WHERE facility='" + addMySQLEscapes(facilityName) + "'";
    ArrayList<Facility> results = FacilityResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;
  }

  public static ArrayList<Facility> getAllFacilities() {
    return getAllFacilities(true);
  }

  public static ArrayList<Facility> getAllFacilities(String orderby) {
    return getAllFacilities(true, orderby);
  }

  public static ArrayList<Facility> getAllFacilities(boolean includeblank) {
    return getAllFacilities(includeblank, null);
  }

  public static ArrayList<Facility> getAllFacilities(boolean includeblank, String orderby) {
    if (orderby != null && orderby.equals("count")) {
      orderby = "`mice held` desc";
    }
    String query = facilityQueryHeader;
    if (!includeblank) {
      query += " WHERE id > 1";
    }
    query += "\r\n ORDER BY ";
    query += orderby != null ? orderby : "position";
    return FacilityResultGetter.getInstance().Get(query);
  }

  public static Gene getGene(int geneRecordID) {
    String query = geneQueryHeader + " WHERE id='" + geneRecordID + "'";
    ArrayList<Gene> results = GeneResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;
  }

  public static Gene findGene(String mgiAccessionID) {
    String query = geneQueryHeader + " WHERE mgi='" + mgiAccessionID + "'";
    ArrayList<Gene> results = GeneResultGetter.getInstance().Get(query);
    return results.size() > 0 ? results.get(0) : null;
  }

  public static ArrayList<Gene> getAllGenes(String orderby) {
    String query = geneQueryHeader + " ORDER BY " + orderby;
    return GeneResultGetter.getInstance().Get(query);
  }

  public static ArrayList<MouseType> getMouseTypes() {
    String query = "SELECT * FROM mousetype";
    String[] columnNames = new String[] { "id", "mousetype" };

    ArrayList<ArrayList<String>> results = StringArrayListResultGetter.getInstance(columnNames).Get(query);
    ArrayList<MouseType> types = new ArrayList<MouseType>();

    for (ArrayList<String> result : results) {
      MouseType type = new MouseType();
      type.setMouseTypeID(Integer.parseInt(result.get(0)));
      type.setTypeName(result.get(1));
      types.add(type);
    }
    return types;
  }

  public static MouseType getMouseType(int mouseTypeId) {
    ArrayList<MouseType> types = getMouseTypes();
    for (MouseType type : types) {
      if (type.getMouseTypeID() == mouseTypeId) {
        return type;
      }
    }
    return null;

  }

  public static ArrayList<ChangeRequest> getChangeRequest(int changeRequestID) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    String orderBy = "";
    whereTerms.add("changerequest.id=" + changeRequestID);

    return getChangeRequests(additionalJoins, whereTerms, orderBy);
  }

  public static ArrayList<ChangeRequest> getChangeRequests(String[] statuses, String orderBy, int mouseID) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (statuses != null && statuses.length > 0) {
      int i = 0;
      String s = "(changerequest.status=";
      for (String status : statuses) {
        if (i > 0) {
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

    return getChangeRequests(additionalJoins, whereTerms, orderBy);
  }

  public static ArrayList<ChangeRequest> getChangeRequests(String status, String orderBy, int mouseID) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
      whereTerms.add("changerequest.status='" + status + "'");
    }

    whereTerms.add("mouse_id=" + mouseID);

    return getChangeRequests(additionalJoins, whereTerms, orderBy);
  }

  public static ArrayList<ChangeRequest> getChangeRequests(String status, String orderBy) {
    return getChangeRequests(status, orderBy, "all");
  }

  public static int countChangeRequests(String status, String orderBy, String requestSource) {

    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
      whereTerms.add("changerequest.status=" + safeText(status));
    }
    if (requestSource != null && !requestSource.isEmpty() && !requestSource.equalsIgnoreCase("all")) {
      whereTerms.add("changerequest.request_source like '%" + requestSource + "%'");
    }
    String whereClause = "";
    if (whereTerms != null && !whereTerms.isEmpty()) {
      String prefix = " WHERE";
      for (String whereTerm : whereTerms) {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }
    ArrayList<Integer> results = IntResultGetter.getInstance("count")
        .Get(changeRequestQueryCountHeader + " " + whereClause);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  public static ArrayList<ChangeRequest> getChangeRequests(String status, String orderBy, String requestSource) {
    return getChangeRequests(status, orderBy, requestSource, 0, 0);
  }

  public static ArrayList<ChangeRequest> getChangeRequests(String status, String orderBy, String requestSource,
      int limit, int offset) {
    ArrayList<String> whereTerms = new ArrayList<String>();
    String additionalJoins = "";
    if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
      whereTerms.add("changerequest.status=" + safeText(status));
    }
    if (requestSource != null && !requestSource.isEmpty() && !requestSource.equalsIgnoreCase("all")) {
      whereTerms.add("changerequest.request_source like '%" + requestSource + "%'");
    }
    return getChangeRequests(additionalJoins, whereTerms, orderBy, limit, offset);

  }

  private static ArrayList<ChangeRequest> getChangeRequests(String additionalJoins, ArrayList<String> whereTerms,
      String orderBy) {
    return getChangeRequests(additionalJoins, whereTerms, orderBy, 0, 0);

  }

  private static ArrayList<ChangeRequest> getChangeRequests(String additionalJoins, ArrayList<String> whereTerms,
      String orderBy, int limit, int offset) {
    String whereClause = "";
    String orderByClause = "";
    String limitClause = "";
    String joinsClause = "";
    if (whereTerms != null && !whereTerms.isEmpty()) {
      String prefix = " WHERE";
      for (String whereTerm : whereTerms) {
        whereClause += prefix + " " + whereTerm + "\r\n ";
        prefix = " AND";
      }
    }
    if (orderBy != null && !orderBy.equalsIgnoreCase("")) {
      orderByClause = "ORDER BY " + orderBy;
    }
    if (limit > 0 && offset >= 0) {
      limitClause = "LIMIT " + offset + "," + limit;
    }
    if (additionalJoins != null) {
      joinsClause = additionalJoins + "\r\n ";
    }

    String query = changeRequestQueryHeader + "\r\n " + joinsClause + whereClause + " " + orderByClause + " "
        + limitClause;
    return ChangeRequestResultGetter.getInstance().Get(query);
  }

  public static ArrayList<ArrayList<String>> getOpenRequestSources(String status) {
    String query = "SELECT request_source, count(*) as 'count' from changerequest where status='" + status
        + "' group by request_source;";
    return StringArrayListResultGetter.getInstance(new String[] { "request_source", "count" }).Get(query);
  }

  public static ArrayList<ArrayList<String>> getOpenSubmissionSources(String status) {
    String query = "SELECT submission_source, count(*) as 'count' from submittedmouse where status='" + status
        + "' group by submission_source;";
    return StringArrayListResultGetter.getInstance(new String[] { "submission_source", "count" }).Get(query);
  }

  public static ArrayList<ImportReport> getAllImportReports() {
    String query = "SELECT * FROM import_reports order by id desc";
    return ImportReportResultGetter.getInstance().Get(query);
  }

  public static ArrayList<ImportReport> getImportReports(ImportObjectType type) {
    String query = "SELECT * FROM import_reports WHERE report_type=" + type.Id + " order by id desc";
    return ImportReportResultGetter.getInstance().Get(query);
  }

  public static ArrayList<ImportReport> getImportReport(int id) {
    String query = "SELECT * FROM import_reports where id=" + id;
    return ImportReportResultGetter.getInstance().Get(query);
  }

  public static ArrayList<String> getAllHolderNames() {
    return getAllHolderNames(true);
  }

  public static ArrayList<String> getAllHolderNames(boolean includeBlank) {
    ArrayList<Holder> allHolders = getAllHolders(includeBlank);
    ArrayList<String> results = new ArrayList<String>();
    results.add("Choose One");
    for (Holder holder : allHolders) {
      if (!includeBlank && holder.getHolderID() == 1) continue;
      results.add(holder.getLastname() + ", " + holder.getFirstname());
    }
    results.add("Other(specify)");
    return results;
  }

  public static ArrayList<String> getAllFacilityNames() {
    return getAllFacilityNames(false);
  }

  public static ArrayList<String> getAllFacilityNames(boolean includeBlank) {
    ArrayList<Facility> allFacilities = getAllFacilities(includeBlank);
    ArrayList<String> results = new ArrayList<String>();
    results.add("Choose One");
    for (Facility facility : allFacilities) {
      if (!includeBlank && facility.getFacilityID() == 1) continue;
      results.add(facility.getFacilityName());
    }
    results.add("Other(specify)");
    return results;
  }

  public static ArrayList<Integer> getMiceInFacility(int facilityID) {
    String query = "SELECT mouse_id FROM mouse_holder_facility WHERE facility_id=" + facilityID;
    return IntResultGetter.getInstance("mouse_id").Get(query);
  }

  public static ArrayList<Integer> getMiceWithHolder(int holderID) {
    String query = "SELECT mouse_id FROM mouse_holder_facility WHERE holder_id=" + holderID;
    return IntResultGetter.getInstance("mouse_id").Get(query);
  }

  public static ArrayList<Integer> getMiceWithGene(int geneID) {
    String query = "SELECT id FROM mouse WHERE gene_id=" + geneID + " OR target_gene_id=" + geneID;
    return IntResultGetter.getInstance("id").Get(query);
  }

  // ************************************************************
  // UPDATE Methods
  // ************************************************************

  public static void setSubmissionID(int mouseID, int submissionID) {
    String query = "UPDATE mouse SET submittedmouse_id=" + submissionID + " WHERE id=" + mouseID;
    executeNonQuery(query);
  }

  public static String updateMouseRecord(MouseRecord updatedRecord) {
    if (updatedRecord == null) {
      return "record was null";
    }
    if (recordExists(updatedRecord.getMouseID()) < 0) {
      return "record does not exist";
    }
    StringBuilder query = new StringBuilder();
    query.append("UPDATE mouse \r\n");
    query.append("SET name="
        + (updatedRecord.getMouseName() != null ? "'" + addMySQLEscapes(updatedRecord.getMouseName()) + "'" : "NULL")
        + ", \r\n");
    query.append("official_name=" + (updatedRecord.getOfficialMouseName() != null ? "'" +

        addMySQLEscapes(updatedRecord.getOfficialMouseName()) + "'" : "NULL") + ", \r\n");

    query.append("mousetype_id="
        + (updatedRecord.getMouseType() != null ? "'" + mouseTypeToTypeID(updatedRecord.getMouseType()) + "'" : "NULL")
        + ", \r\n");

    query.append(
        "gene_id=" + (updatedRecord.getGeneID() != null ? "'" + updatedRecord.getGeneID() + "'" : "NULL") + ", \r\n");

    query.append("target_gene_id="
        + (updatedRecord.getTargetGeneID() != null ? "'" + updatedRecord.getTargetGeneID() + "'" : "NULL") + ", \r\n");

    query.append("modification_type="
        + (updatedRecord.getModificationType() != null ? "'" + updatedRecord.getModificationType() + "'" : "NULL")
        + ", \r\n");
    query.append("regulatory_element_comment=" + (updatedRecord.getRegulatoryElement() != null
        ? "'" + addMySQLEscapes(updatedRecord.getRegulatoryElement()) + "'"
        : "NULL") + ", \r\n");

    query.append("expressedsequence_id=" + (updatedRecord.getExpressedSequence() != null
        ? "'" + expressedSequenceToTypeID(updatedRecord.getExpressedSequence()) + "'"
        : -1) + ", \r\n");
    query.append("other_comment="
        + (updatedRecord.getOtherComment() != null ? "'" + addMySQLEscapes(updatedRecord.getOtherComment()) + "'"
            : "NULL")
        + ", \r\n");
    query.append("reporter_comment="
        + (updatedRecord.getReporter() != null ? "'" + addMySQLEscapes(updatedRecord.getReporter()) + "'" : "NULL")
        + ", \r\n");
    query.append("transgenictype_id=" + (updatedRecord.getTransgenicType() != null
        ? "'" + transgenicTypeToTypeID(updatedRecord.getTransgenicType()) + "'"
        : -1) + ", \r\n");

    query.append("mta_required="
        + (updatedRecord.getMtaRequired() != null ? "'" + updatedRecord.getMtaRequired() + "'" : "'N'") + ", \r\n");
    query.append("general_comment="
        + (updatedRecord.getGeneralComment() != null ? "'" + addMySQLEscapes(updatedRecord.getGeneralComment()) + "'"
            : "NULL")
        + ", \r\n");
    query.append("strain=" + (updatedRecord.getBackgroundStrain() != null
        ? "'" + addMySQLEscapes(updatedRecord.getBackgroundStrain()) + "'"
        : "NULL") + ", \r\n");

    query.append("source="
        + (updatedRecord.getSource() != null ? "'" + addMySQLEscapes(updatedRecord.getSource()) + "'" : "NULL")
        + ", \r\n");

    query.append("repository_catalog_number="
        + (updatedRecord.getRepositoryCatalogNumber() != null ? "'" + updatedRecord.getRepositoryCatalogNumber() + "'"
            : "NULL")
        + ", \r\n");
    query.append("repository_id="
        + (updatedRecord.getRepositoryTypeID() != null ? "'" + updatedRecord.getRepositoryTypeID() + "'" : -1)
        + ", \r\n");

    query.append("gensat="
        + (updatedRecord.getGensat() != null ? "'" + addMySQLEscapes(updatedRecord.getGensat()) + "'" : "NULL")
        + ", \r\n");
    query.append("cryopreserved="
        + (updatedRecord.getCryopreserved() != null ? "'" + updatedRecord.getCryopreserved() + "'" : 0) + ", \r\n");
    query.append(
        "status=" + (updatedRecord.getStatus() != null ? "'" + updatedRecord.getStatus() + "'" : "NULL") + ", \r\n");
    query.append("endangered=" + updatedRecord.isEndangered() + ",\r\n");

    query.append("admin_comment=" + safeText(updatedRecord.getAdminComment()) + "\r\n");

    query.append("WHERE id=" + updatedRecord.getMouseID());
    Log.Info("Updating mouse record with query: \r\n" + query);
    executeNonQuery(query.toString());
    
    /*
    ArrayList<File> files = updatedRecord.getFilenames();
    
    for (File file : files) {
    	//Blob createdBlob = _connection.createBlob(file);
    	byte[] createdBlob = new byte[(int) file.length()];
    	FileInputStream inputStream = null;
    	try {
    		// create an input stream pointing to the file
    		inputStream = new FileInputStream(file);
    		// read the contents of file into byte array
    		inputStream.read(createdBlob);
    	} catch (IOException e) {
    		
    	} finally {
    		// close input stream
    		if (inputStream != null) {
    			try {
    				inputStream.close();
    			} catch (Exception e) {
    				///
    			}      
    		}
    	}
    	String fileQuery = "Insert into mouseFiles (filename, file, mouseID) VALUES (" + file.getName() + ", " + createdBlob
        		+ ", " + updatedRecord.getMouseID() + ");";//for saving files
        executeNonQuery(fileQuery);
    }*/
    
    
    

    // holders
    updateMouseHolders(updatedRecord);

    // pubmed ids
    updateMouseLiterature(updatedRecord);

    // search text

    updateMouseSearchTable(updatedRecord.getMouseID());

    return null;
  } 

  public static void initialTable() {
  String  query =  "create table if not exists mouseFiles (ID int auto_increment, filename text, file blob, mouseID text, PRIMARY  KEY  (ID));";
  executeNonQuery(query);
  }
  
  public static void testSend() {
	  Connection con = null; 
	  try {
		  if (con == null) {
			con = connect();
		  }
		  String fileName = "Test.txt";
		  File file = new File(fileName);
		  PrintWriter writer = new PrintWriter("Test.txt", "UTF-8");
		  writer.println("this test is runing");
		  writer.close();
		  Blob createdBlob = makeBlobFromFile(file);

		  //Blob createdBlob = con.createBlob();//makeBlobFromFile(file);
		  String mouseID = "6";
		  String query = "Insert into mouseFiles (filename, file, mouseID) VALUES (?, ?, ?)";
		  String test2 = "Insert into mouseTest (name) values (?)";
		  PreparedStatement statement = con.prepareStatement(query);
		  PreparedStatement statement2 = con.prepareStatement(test2);
		  statement2.setString(1, "testing");
		  
		  statement.setString(1, fileName);
		  statement.setBlob(2, createdBlob);
		  statement.setString(3, mouseID);
		  Log.Info("about to execute statement: " + query);
		  if (createdBlob != null) {
			  Log.Info("blob exists");
		  } else {
			  Log.Info("no blob");
		  }
		  
		  statement.execute();
		  //statement2.execute();
	  } catch (Exception e) {}	  
  }
  
  public static void sendFilesToDatabase(ArrayList<File> files, String mouseID) {
	  Connection con = null; 
	  for(File file : files){
			String fileName = file.getName();
			Blob createdBlob = makeBlobFromFile(file);
			
			try {
				if (con == null) {
					con = connect();
				}
				
				//try with basic string query
				
				String query = "Insert into mouseFiles (filename, file, mouseID) VALUES (?, ?, ?)";
				//String test2 = "Insert into mouseTest (name) values (\'testing\')";
				//executeNonQuery(test2);
				//PreparedStatement statement = con.prepareStatement(test2);
				PreparedStatement statement = con.prepareStatement(query);
				statement.setNString(1, fileName);
				statement.setBlob(2, createdBlob);
				statement.setNString(3, mouseID);
				statement.execute();
				
			} catch (Exception e) {
				///
			}
	    	/*String fileQuery = "Insert into mouseFiles (filename, file, mouseID) VALUES (" + fileName + ", " + createdBlob
	        		+ ", " + mouseID + ");";//for saving files
	    	*/

	    	
	    	
	    	Log.Info("fileQuery");
	        //DBConnect.executeNonQuery(fileQuery);//note: executeNonQuery will not handle blobs.
	  }
  }
  
  public static void testFunction(String fileQuery) {
	  DBConnect.executeNonQuery(fileQuery);
  }
  
  public static Blob makeBlobFromFile(File file) {
	  //String fileName = file.getName();
	  Blob fileBlob = null;
  	byte[] byteArray = new byte[(int) file.length()];
  	FileInputStream inputStream = null;
  	try {
  		// create an input stream pointing to the file
  		inputStream = new FileInputStream(file);
  		// read the contents of file into byte array
  		inputStream.read(byteArray);
  		fileBlob = new javax.sql.rowset.serial.SerialBlob(byteArray);
  	} catch (Exception e) {
  		///
  	} finally {
  		// close input stream
  		if (inputStream != null) {
  			try {
  				inputStream.close();
  			} catch (Exception e) {
  				///
  			}      
  		}
  	}
  	
  	return fileBlob;
  }

  public static void updateMouseSubmission(SubmittedMouse updatedSubmission) throws Exception {
    throw new Exception("not implemented");
  }

  public static void updateSubmission(int submissionID, String status, String notes) {
    String entered = status.equalsIgnoreCase("accepted") ? "Y" : "N";

    String query = "UPDATE submittedmouse SET status='" + status + "'";
    if (notes != null) query += ", admincomment='" + addMySQLEscapes(notes) + "'";
    query += ", entered='" + entered + "' WHERE id=" + submissionID;
    executeNonQuery(query);
  }

  public static void updateSubmissionProperties(int submissionID, String properties) {

    String query = "UPDATE submittedmouse SET properties='" + properties + "' WHERE id=" + submissionID;
    executeNonQuery(query);
  }

  public static void updateChangeRequest(int requestID, String status, String notes) {
    Date now = new Date(System.currentTimeMillis());
    String query = "UPDATE changerequest " + "SET " + "status='" + status + "', admin_comment='"
        + addMySQLEscapes(notes) + "', lastadmindate='" + now + "'  WHERE id=" + requestID;
    executeNonQuery(query);
  }

  public static void updateFacility(Facility updatedFacility) {
    String query = "UPDATE facility " + "SET " + "facility='" + addMySQLEscapes(updatedFacility.getFacilityName())
        + "',description='" + addMySQLEscapes(updatedFacility.getFacilityDescription()) + "',code='"
        + addMySQLEscapes(updatedFacility.getFacilityCode()) + "',local_experts='"
        + addMySQLEscapes(updatedFacility.getLocalExperts()) + "'\r\nWHERE id=" + updatedFacility.getFacilityID();
    executeNonQuery(query);
  }

  public static void updateFacilityPosition(int facilityId, int position) {
    String query = "UPDATE facility SET position=" + position + " WHERE id=" + facilityId;
    executeNonQuery(query);
  }

  public static void updateSettingPosition(int settingId, int position) {
    String query = "UPDATE settings SET position=" + position + " WHERE id=" + settingId;
    executeNonQuery(query);
  }

  public static void updateHolder(Holder updatedHolder) {

    String dateValidated = updatedHolder.getDateValidated();

    if (dateValidated != null && !dateValidated.isEmpty() && !dateValidated.trim().isEmpty()) {
      dateValidated = "'" + addMySQLEscapes(dateValidated) + "'";
    } else {
      dateValidated = "NULL";
    }

    String query = "UPDATE holder " + "SET " + "firstname='"
        + addMySQLEscapes(updatedHolder.getFirstname() != null ? updatedHolder.getFirstname().trim() : "")
        + "',lastname='"
        + addMySQLEscapes(updatedHolder.getLastname() != null ? updatedHolder.getLastname().trim() : "")
        + "',department='" + addMySQLEscapes(updatedHolder.getDept() != null ? updatedHolder.getDept().trim() : "")
        + "',email='" + addMySQLEscapes(updatedHolder.getEmail() != null ? updatedHolder.getEmail().trim() : "")
        + "',alternate_email='"
        + addMySQLEscapes(updatedHolder.getAlternateEmail() != null ? updatedHolder.getAlternateEmail().trim() : "")
        + "',alternate_name='"
        + addMySQLEscapes(updatedHolder.getAlternateName() != null ? updatedHolder.getAlternateName().trim() : "")
        + "',tel='" + addMySQLEscapes(updatedHolder.getTel()) + "',primary_mouse_location='"
        + addMySQLEscapes(updatedHolder.getPrimaryMouseLocation()) + "',is_deadbeat=" + updatedHolder.isDeadbeat()
        + ",validation_status='"
        + addMySQLEscapes(updatedHolder.getValidationStatus() != null ? updatedHolder.getValidationStatus().trim() : "")
        + "',datevalidated=" + dateValidated + "\r\nWHERE id=" + updatedHolder.getHolderID();
    executeNonQuery(query);
  }

  public static void updateGene(Gene updatedGene) {
    String query = "UPDATE gene " + "SET " + "fullname='" + addMySQLEscapes(updatedGene.getFullname()) + "',symbol='"
        + addMySQLEscapes(updatedGene.getSymbol()) + "',mgi='" + addMySQLEscapes(updatedGene.getMgiID())
        + "'\r\nWHERE id=" + updatedGene.getGeneRecordID();
    executeNonQuery(query);
  }

  public static void updateImportReport(ImportReport updatedImportReport) {
    String query = "UPDATE import_reports " + "SET " + "name='" + addMySQLEscapes(updatedImportReport.getName())
        + "',reporttext='" + addMySQLEscapes(updatedImportReport.getReportText()) + "'\r\nWHERE id="
        + updatedImportReport.getImportReportID();
    executeNonQuery(query);
  }

  // ************************************************************
  // INSERT Methods
  // ************************************************************

  public static int insertMouseRecord(MouseRecord newRecord) {
    String query = "INSERT into mouse (id) VALUES (NULL)";
    int mouseID = executeNonQuery(query, true);
    newRecord.setMouseID(String.valueOf(mouseID));
    updateMouseRecord(newRecord);
    return mouseID;
  }

  public static int addReference(int PubMedID) {
    int referenceID = -1;
    if ((referenceID = referenceExists(PubMedID)) > 0) {
      return referenceID;
    }
    String query = "INSERT into literature (pmid) \r\n" + "VALUES ('" + PubMedID + "')";
    return executeNonQuery(query, true);
  }

  public static void addReferenceToMouse(int referenceID, int mouseID) {
    // TODO check for duplicates first

    String query = "INSERT into mouse_literature (literature_id, mouse_id) values ('" + referenceID + "','" + mouseID
        + "')";
    executeNonQuery(query);
  }

  public static void addHolderToMouse(int holderID, int facilityID, int mouseID) {

    String query = "INSERT into mouse_holder_facility (mouse_id, holder_id, facility_id) values ('" + mouseID + "', '"
        + holderID + "', '" + facilityID + "')";
    executeNonQuery(query);
  }

  // TODO make this an int
  // TODO refactor the code that calls this
  public static String addGeneManually(String geneID, String fullname, String symbol) {
    String newGeneID = "-1";
    // see if this gene already exists in our local genes table
    String query = "SELECT id FROM gene WHERE mgi='" + addMySQLEscapes(geneID.trim()) + "'";

    ArrayList<String> results = StringResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    // if it doesn't exist, insert it
    query = "INSERT INTO gene(fullname, symbol, mgi) \r\n" + "VALUES " + "('" + addMySQLEscapes(fullname) + "', '"
        + addMySQLEscapes(symbol) + "', '" + addMySQLEscapes(geneID.trim()) + "')";
    newGeneID = String.valueOf(executeNonQuery(query, true));
    return newGeneID;
  }

  @SuppressWarnings("rawtypes")
  public static int insertAdminSubmission(String username, String notes, String status, Properties props) {
    StringBuffer propsBuf = new StringBuffer();
    Enumeration names = props.propertyNames();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      propsBuf.append(name + "=" + props.getProperty(name) + "\t");
    }

    String query = "INSERT into submittedmouse (id,firstname,lastname,dept,address,email,tel,date,properties) "
        + "VALUES " + "(NULL,'" + addMySQLEscapes(username) + "', '','','','','', curdate(),'"
        + addMySQLEscapes(propsBuf.toString()) + "')";
    int subID = executeNonQuery(query, true);
    updateSubmission(subID, status, notes);
    return subID;

  }

  @SuppressWarnings("rawtypes")
  public static int insertSubmission(UserData submitterData, MouseSubmission submission, Properties props,
      String submissionSource) {
    StringBuffer propsBuf = new StringBuffer();
    Enumeration names = props.propertyNames();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      propsBuf.append(name + "=" + props.getProperty(name) + "\t");
    }

    String query = "INSERT into submittedmouse "
        + "(id,firstname,lastname,dept,address,email,tel,date,properties,submission_source) " + "VALUES " + "(NULL"
        + ",\r\n'" + addMySQLEscapes(submitterData.getFirstName()) + "',\r\n'"
        + addMySQLEscapes(submitterData.getLastName()) + "',\r\n'" + addMySQLEscapes(submitterData.getDepartment())
        + "',\r\n'" + "" + "',\r\n'" + addMySQLEscapes(submitterData.getEmail()) + "',\r\n'"
        + addMySQLEscapes(submitterData.getTelephoneNumber()) + "',\r\n curdate(),\r\n'"
        + addMySQLEscapes(propsBuf.toString()) + "'" + ",\r\n'" + addMySQLEscapes(submissionSource) + "')";
    return executeNonQuery(query, true);

  }

  public static int insertFacility(Facility newFacility) {
    String query = "INSERT into facility (id,facility,description,code,local_experts) " + "VALUES (NULL" + ",'"
        + addMySQLEscapes(newFacility.getFacilityName()) + "','" + addMySQLEscapes(newFacility.getFacilityDescription())
        + "','" + addMySQLEscapes(newFacility.getFacilityCode()) + "','"
        + addMySQLEscapes(newFacility.getLocalExperts()) + "')";
    return executeNonQuery(query, true);
  }

  public static int insertHolder(Holder newHolder) {
    String query = "INSERT into holder (id,firstname,lastname,department,email,alternate_email,alternate_name,tel,primary_mouse_location) "
        + "VALUES (NULL" + ",'"
        + addMySQLEscapes(newHolder.getFirstname() != null ? newHolder.getFirstname().trim() : "") + "','"
        + addMySQLEscapes(newHolder.getLastname() != null ? newHolder.getLastname().trim() : "") + "','"
        + addMySQLEscapes(newHolder.getDept() != null ? newHolder.getDept().trim() : "") + "','"
        + addMySQLEscapes(newHolder.getEmail() != null ? newHolder.getEmail().trim() : "") + "','"
        + addMySQLEscapes(newHolder.getAlternateEmail() != null ? newHolder.getAlternateEmail().trim() : "") + "','"
        + addMySQLEscapes(newHolder.getAlternateName() != null ? newHolder.getAlternateName().trim() : "") + "','"
        + addMySQLEscapes(newHolder.getTel()) + "','" + addMySQLEscapes(newHolder.getPrimaryMouseLocation()) + "')";
    return executeNonQuery(query, true);
  }

  public static int insertGene(Gene newGene) {
    String query = "INSERT into gene (id,fullname,symbol,mgi) " + "VALUES (NULL" + ",'"
        + addMySQLEscapes(newGene.getFullname()) + "','" + addMySQLEscapes(newGene.getSymbol()) + "','"
        + addMySQLEscapes(newGene.getMgiID()) + "')";
    return executeNonQuery(query, true);
  }

  public static int insertChangeRequest(ChangeRequest req) {
    String query = "INSERT into changerequest " + "(id,mouse_id,firstname,lastname,email,status,user_comment,"
        + "admin_comment,requestdate,properties,holder_name,holder_id,holder_email,"
        + "facility_name,facility_id,action_requested,request_source,cryo_live_status," + "genetic_background_info) "
        + "VALUES (NULL" + "," + req.getMouseID() + "," + safeText(req.getFirstname()) + ","
        + safeText(req.getLastname()) + "," + safeText(req.getEmail()) + "," + safeText(req.getStatus()) + ","
        + safeText(req.getUserComment()) + "," + safeText(HTMLGeneration.emptyIfNull(req.getAdminComment())) + ","
        + "curdate()" + "," + safeText(HTMLGeneration.emptyIfNull(req.getProperties())) + ","
        + safeText(req.getHolderName()) + "," + req.getHolderId() + "," + safeText(req.getHolderEmail()) + ","
        + safeText(req.getFacilityName()) + "," + req.getFacilityId() + "," + req.actionRequested().ordinal() + ","
        + safeText(req.getRequestSource()) + "," + safeText(req.getCryoLiveStatus()) + ","
        + safeText(req.getGeneticBackgroundInfo()) + ")";
    return executeNonQuery(query, true);
  }

  public static int insertImportReport(ImportReport newReport) {
    String query = "INSERT into import_reports (name,report_type,creationdate,reporttext) " + "VALUES (" + "'"
        + addMySQLEscapes(newReport.getName()) + "', " + newReport.getImportType().ordinal() + "," + "curdate()" + ",'"
        + addMySQLEscapes(newReport.getReportText()) + "')";
    int reportId = executeNonQuery(query, true);

    boolean first = true;
    if (newReport.getNewObjectIds() != null && newReport.getNewObjectIds().size() > 0) {
      query = "INSERT into import_new_objects (import_report_id,object_id) VALUES ";
      for (int objectId : newReport.getNewObjectIds()) {
        if (first)
          first = false;
        else
          query += ",";

        query += "(" + reportId + "," + objectId + ")";
      }
    } else if (newReport.getNewObjects() != null && newReport.getNewObjects().size() > 0) {
      query = "INSERT into import_new_objects (import_report_id,object_data) VALUES ";
      for (String object : newReport.getNewObjects()) {
        if (first)
          first = false;
        else
          query += ",";

        query += "(" + reportId + ", '" + addMySQLEscapes(object) + "')";
      }
    }

    query += ";";
    executeNonQuery(query);

    return reportId;
  }

  public static ArrayList<Integer> getImportNewObjectIds(int reportId) {
    return IntResultGetter.getInstance("object_id")
        .Get("SELECT * FROM import_new_objects WHERE import_report_id=" + reportId);
  }

  public static ArrayList<String> getImportNewObjects(int reportId) {
    return StringResultGetter.getInstance("object_data")
        .Get("SELECT object_data from import_new_objects WHERE import_report_id=" + reportId);
  }

  private static String safeText(String text) {
    return "'" + addMySQLEscapes(text) + "'";
  }

  public static int insertEmail(MouseMail email) {
    String query = "INSERT into emails (recipients,ccs,bccs,emailType,subject,body,status,category,template_name,attachment_names)";
    query += "VALUES (";
    query += safeText(email.recipient) + ",";
    query += safeText(email.ccs) + ",";
    query += safeText(email.bccs) + ",";
    query += safeText(email.emailType) + ",";
    query += safeText(email.subject) + ",";
    query += safeText(email.body) + ",";
    query += safeText(email.status) + ",";
    query += safeText(email.category) + ",";
    query += safeText(email.templateName) + ",";
    query += safeText(email.attachmentNames);
    query += ");";
    return executeNonQuery(query);
  }

  public static ArrayList<MouseMail> getEmails(String category, String orderby, String status) {

    String query = "select * from emails";
    boolean whereAdded = false;
    if (category != null && !category.equals("all")) {
      query += "\n WHERE category=" + safeText(category);
      whereAdded = true;
    }
    if (status != null) {
      if (whereAdded) {
        query += "\n AND ";
      } else {
        query += "\n WHERE ";
        query += "status=" + safeText(status);
        whereAdded = true;
      }
    }
    if (orderby == null) {
      orderby = "date_created";
    } else if (orderby.equals("date_created")) {
      orderby = "date_created desc";
    } else if (orderby.equals("date_crated_inv")) {
      orderby = "date_created";
    } else {
      orderby = addMySQLEscapes(orderby);
    }
    query += "\n ORDER BY " + orderby;

    return EmailResultGetter.getInstance().Get(query);
  }

  public static void deleteEmail(int emailId) {
    executeNonQuery("delete from emails where id=" + emailId);
  }

  public static int insertSetting(Setting setting) {
    String query = "INSERT into settings (name,category_id,label,secondary_value,setting_value)";
    query += "VALUES (";
    query += safeText(setting.name) + ",";
    query += setting.category_id + ",";
    query += safeText(setting.label) + ",";
    query += safeText(setting.secondaryValue) + ",";
    query += safeText(setting.value);
    query += ");";
    return executeNonQuery(query);
  }

  public static void updateSetting(Setting setting) {
    String query = "UPDATE settings SET ";
    query += " name=" + safeText(setting.name) + ",";
    query += " label=" + safeText(setting.label) + ",";
    query += " secondary_value=" + safeText(setting.secondaryValue) + ",";
    query += " setting_value=" + safeText(setting.value);
    query += " WHERE id=" + setting.id + ";";
    executeNonQuery(query);
  }

  public static Setting loadSetting(String name) {
    return (Setting) SettingResultGetter.getInstance().Get("select * from settings where name=" + safeText(name))
        .get(0);
  }

  public static Setting loadSetting(int id) {
    return (Setting) SettingResultGetter.getInstance().Get("select * from settings where id=" + id).get(0);
  }

  public static void deleteSetting(int id) {
    executeNonQuery("DELETE from settings where id=" + id);
  }

  public static ArrayList<Setting> getCategorySettings(int category_id) {
    return SettingResultGetter.getInstance()
        .Get("select * from settings where category_id=" + category_id + " ORDER BY position");

  }

  public static ArrayList<Setting> getCategorySettings(int category_id, String orderby) {
    return SettingResultGetter.getInstance()
        .Get("select * from settings where category_id=" + category_id + " ORDER BY " + orderby);

  }

  public static int insertEmailTemplate(EmailTemplate template) {
    String query = "INSERT into email_templates (name,subject,body,category)";
    query += "VALUES (";
    query += safeText(template.name) + ",";
    query += safeText(template.subject) + ",";
    query += safeText(template.body) + ",";
    query += safeText(template.category);
    query += ");";
    return executeNonQuery(query);
  }

  public static void updateEmailTemplate(EmailTemplate template) {
    String query = "UPDATE email_templates SET ";
    query += " name=" + safeText(template.name) + ",";
    query += " subject=" + safeText(template.subject) + ",";
    query += " body=" + safeText(template.body) + ",";
    query += " category=" + safeText(template.category);
    query += " WHERE id=" + template.id + ";";
    executeNonQuery(query);
  }

  public static EmailTemplate loadEmailTemplate(int id) {
    return (EmailTemplate) EmailTemplateResultGetter.getInstance().Get("select * from email_templates where id=" + id)
        .get(0);
  }

  public static void deleteEmailTemplate(int id) {
    executeNonQuery("DELETE FROM email_templates WHERE id=" + id);
  }

  public static ArrayList<EmailTemplate> getCategoryEmailTemplates(String category) {
    return EmailTemplateResultGetter.getInstance()
        .Get("select * from email_templates where category=" + safeText(category));
  }

  public static ArrayList<EmailTemplate> getEmailTemplates() {
    return getEmailTemplates(null, null);
  }

  public static ArrayList<EmailTemplate> getEmailTemplates(String category, String orderby) {

    String query = "select * from email_templates";
    if (category != null) {
      query += "\n WHERE category=" + safeText(category);
    }
    if (orderby == null) {
      orderby = "category,name";
    } else if (orderby.equals("date_updated")) {
      orderby = "date_updated desc";
    } else {
      orderby = addMySQLEscapes(orderby);
    }
    query += "\n ORDER BY " + orderby;

    return EmailTemplateResultGetter.getInstance().Get(query);
  }

  // ************************************************************
  // DELETE Methods
  // ************************************************************

  public static void deleteSubmission(int submissionID) {
    String query = "DELETE FROM submittedmouse WHERE id=" + submissionID;
    executeNonQuery(query);

    query = "SELECT id,status from mouse WHERE submittedmouse_id=" + submissionID;
    String[] columnNames = new String[] { "id", "status" };

    ArrayList<ArrayList<String>> results = StringArrayListResultGetter.getInstance(columnNames).Get(query);

    if (results.size() > 0) {
      ArrayList<String> result = results.get(0);
      String status = result.get(1);
      if (status.equalsIgnoreCase("incomplete")) {
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

  public static void deleteAllMouseHolders(int mouseID) {
    String query = "DELETE FROM mouse_holder_facility WHERE mouse_id=" + mouseID;
    executeNonQuery(query);
  }

  public static void deleteFacility(int facilityID) {
    String query = "DELETE FROM facility " + "\r\nWHERE id=" + facilityID;
    executeNonQuery(query);
  }

  public static void deleteHolder(int holderID) {
    String query = "UPDATE holder set status='deleted'" + "\r\nWHERE id=" + holderID;
    executeNonQuery(query);
  }

  public static void deleteGene(int geneID) {
    String query = "DELETE FROM gene " + "\r\nWHERE id=" + geneID;
    executeNonQuery(query);
  }

  public static void deleteImportReport(int importReportID) {
    String query = "DELETE FROM import_reports " + "\r\nWHERE id=" + importReportID;
    executeNonQuery(query);
  }

  public static void deleteChangeRequest(int changeRequestID) {
    String query = "DELETE FROM changerequest " + "\r\nWHERE id=" + changeRequestID;
    executeNonQuery(query);
  }

  // ************************************************************
  // HELPER Methods
  // ************************************************************

  public static void updateSearchIndex() {
    ArrayList<String> mouseIds = StringResultGetter.getInstance("id").Get("select id from mouse");
    for (String mouseId : mouseIds) {
      updateMouseSearchTable(mouseId);
    }
  }

  public static void updateMouseSearchTable(String recordID) {
    ArrayList<MouseRecord> records = getMouseRecord(Integer.parseInt(recordID));
    if (records.size() <= 0) {
      return;
    }
    MouseRecord record = records.get(0);

    ArrayList<String> list = new ArrayList<String>();

    addFlattenedData(list, record.getBackgroundStrain());
    if (record.isTG() || (record.isMA() && record.getExpressedSequence() != null
        && (record.getExpressedSequence().equalsIgnoreCase("mouse gene")
            || record.getExpressedSequence().equalsIgnoreCase("Mouse Gene (unmodified)")))) {
      addFlattenedData(list, record.getExpressedSequence());
    }
    addFlattenedData(list, record.getGeneID());
    addFlattenedData(list, record.getGeneName());
    addFlattenedData(list, record.getGeneralComment());
    addFlattenedData(list, record.getGeneSymbol());
    addFlattenedData(list, record.getGensat());
    addFlattenedData(list, record.getModificationType());
    addFlattenedData(list, record.getMouseID());
    addFlattenedData(list, record.getMouseName());
    addFlattenedData(list, record.getOfficialMouseName());
    addFlattenedData(list, record.getOtherComment());
    addFlattenedData(list, record.getRegulatoryElement());
    addFlattenedData(list, record.getReporter());
    addFlattenedData(list, record.getRepositoryCatalogNumber());
    String source = record.getSource();
    if (source != null && source.indexOf("<") >= 0 && source.indexOf(">") >= 0) {
      addFlattenedData(list, source.replace("<", "").replace(">", ""));
    }
    addFlattenedData(list, record.getSource());

    addFlattenedData(list, record.getTargetGeneID());
    addFlattenedData(list, record.getTargetGeneName());
    addFlattenedData(list, record.getTargetGeneSymbol());

    if (record.isEndangered()) {
      addFlattenedData(list, "endangered");
    }

    if (record.getCryopreserved() != null && record.getCryopreserved().equalsIgnoreCase("yes")) {
      addFlattenedData(list, "cryopreserved");
    }

    for (MouseHolder hldr : record.getHolders()) {
      if (hldr.isCovert()) {
        continue;
      }
      addFlattenedData(list, hldr.getLastname() + " " + hldr.getFacilityName());
    }

    for (String pmid : record.getPubmedIDs()) {
      addFlattenedData(list, pmid + " ");
    }

    if ((record.isMA() || record.isTG()) && (record.getPubmedIDs() == null || record.getPubmedIDs().isEmpty())) {
      addFlattenedData(list, "unpublished");
    }

    String query = "DELETE from flattened_mouse_search WHERE mouse_id='" + recordID + "'";
    executeNonQuery(query);

    query = "INSERT into flattened_mouse_search (mouse_id,searchtext) VALUES ('" + recordID + "', '"
        + addMySQLEscapes(StringUtils.join(list, " :: ")) + "')";
    executeNonQuery(query);
  }

  private static void addFlattenedData(ArrayList<String> list, Object value) {
    if (value != null) list.add(value.toString());
  }

  private static void updateMouseHolders(MouseRecord r) {
    // delete all existing holders

    String query = "DELETE FROM mouse_holder_facility WHERE mouse_id='" + r.getMouseID() + "'";
    executeNonQuery(query);

    // add all holders from the record
    for (MouseHolder holder : r.getHolders()) {
      if (holder.getHolderID() != 1) {
        query = "INSERT INTO mouse_holder_facility (mouse_id, holder_id, facility_id," + "covert,cryo_live_status) "
            + " VALUES ('" + r.getMouseID() + "', " + holder.getHolderID() + ", " + holder.getFacilityID() + ", "
            + holder.isCovert() + ", '" + holder.getCryoLiveStatus() + "')";
        executeNonQuery(query);
      }
    }
    if (r.getHolders() == null || r.getHolders().size() == 0) {
      query = "UPDATE mouse set status='deleted' where id=" + r.getMouseID() + ";";
      executeNonQuery(query);
    }

  }

  private static void updateMouseLiterature(MouseRecord r) {
    // delete existing references
    String query = "DELETE FROM mouse_literature \r\nWHERE mouse_id=" + r.getMouseID();
    executeNonQuery(query);

    // add all references from the passed in record

    for (String pmid : r.getPubmedIDs()) {
      if (pmid != null && pmid.length() > 0) {
        // First see if there is an entry already in literature, and get its
        // primary key litID
        pmid = pmid.trim();
        int litID;
        query = "SELECT id \r\nFROM literature \r\nWHERE pmid='" + pmid + "'";
        ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
        if (results.size() > 0) {
          litID = results.get(0);
        } else {
          // create new literature entries if necessary
          query = "INSERT INTO literature (pmid) \r\nVALUES ('" + pmid + "')";
          litID = executeNonQuery(query, true);
        }
        query = "INSERT INTO mouse_literature (mouse_id, literature_id) \r\nVALUES('" + r.getMouseID() + "', '" + litID
            + "')";
        executeNonQuery(query);
      }
    }

  }

  public static int checkForDuplicates(int mouseMGIID, int currentRecordID) {
    String query = "SELECT id FROM mouse WHERE length(repository_catalog_number) != 0 "
        + "AND repository_catalog_number='" + mouseMGIID + "'";

    if (currentRecordID > 0) {
      query += " AND id<>" + currentRecordID;
    }
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    int recordID = -1;
    if (results.size() > 0) {
      recordID = results.get(0);
    }

    return recordID;
  }

  public static int checkForDuplicates(String inbredStrainSource) {
    // TODO add inbred strain type ID check
    String query = "SELECT id FROM mouse " + "WHERE source regexp '" + addMySQLEscapes(inbredStrainSource) + "'";

    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    int recordID = -1;
    if (results.size() > 0) {
      recordID = results.get(0);
    }

    return recordID;
  }

  public static int recordExists(String mouseRecordID) {
    String query = "SELECT id FROM mouse WHERE id='" + mouseRecordID + "'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  public static int submissionExists(int mouseMgiId) {
    String query = "SELECT id FROM submittedmouse WHERE properties LIKE('%repository=" + mouseMgiId
        + "%') AND entered != 'Y'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  public static int changeRequestExists(ChangeRequest changeRequest) {
    String query = "SELECT id " + "FROM changerequest " + "WHERE mouse_id=" + changeRequest.getMouseID()
        + " AND user_comment='" + changeRequest.getUserComment() + "' AND email='" + changeRequest.getEmail() + "'"
        + " AND (status='need more info' OR status='new')" + " AND holder_id=" + changeRequest.getHolderId()
        + " AND cryo_live_status='" + changeRequest.getCryoLiveStatus() + "'"
        + (changeRequest.actionRequested() != Action.UNDEFINED
            ? " AND action_requested=" + changeRequest.actionRequested().ordinal()
            : "");
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  public static int referenceExists(int PubMedID) {
    String query = "SELECT id FROM literature WHERE pmid='" + PubMedID + "'";

    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  public static int mouseTypeToTypeID(String mouseType) {
    String query = "SELECT id \r\nFROM mousetype \r\nWHERE mousetype.mousetype='" + mouseType + "'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;

  }

  public static int transgenicTypeToTypeID(String transgenicType) {
    String query = "SELECT id \r\nFROM transgenictype \r\nWHERE transgenictype.transgenictype='" + transgenicType + "'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;

  }

  public static int expressedSequenceToTypeID(String expressedSequence) {
    String query = "SELECT id \r\nFROM expressedsequence " + "\r\nWHERE expressedsequence.expressedsequence='"
        + expressedSequence + "'";
    ArrayList<Integer> results = IntResultGetter.getInstance("id").Get(query);
    if (results.size() > 0) {
      return results.get(0);
    }
    return -1;
  }

  // ***********************************************************
  // Report methods
  // ***********************************************************
  public static String RunReport(String reportName, Object[] args) {
    if (reportName.equalsIgnoreCase(ReportServlet.RecordsReportName)) {
      return runRecordsReport();
    } else if (reportName.equals(ReportServlet.HolderReportName)) {
      String query = "SELECT holder.id as 'Holder ID', lastname as 'Last Name',firstname as 'First Name',"
          + "department as 'Department',email as 'E-Mail',alternate_name as 'Primary contact name',"
          + "alternate_email as 'Primary contact email', datevalidated as 'Date of last review', " + "(select count(*) "
          + "\r\nFROM mouse_holder_facility " + "\r\nWHERE holder_id=holder.id) as 'Number of Mice Held'"
          + "\r\nFROM holder left join mouse_holder_facility  on mouse_holder_facility.holder_id = holder.id"
          + "\r\nWHERE holder.id<> 1" + "\r\nGROUP by holder.id" + "\r\nORDER BY lastname, firstname";

      String[] columnHeaders = new String[] { "Holder ID", "Last Name", "First Name", "Department", "E-Mail",
          "Primary contact name", "Primary contact email", "Number of mice held", "Date of last review" };
      return runSimpleReport(query, columnHeaders);
    } else if (reportName.equals(ReportServlet.LarcRecordHolderReportName)) {
      String query = "SELECT t2.id as \"holder id\", " + "concat('\"',t2.lastname,'\"') as \"holder lastname\", "
          + "concat('\"',t2.firstname,'\"') as \"holder firstname\", " + "t3.id as \"database record ID\", "
          + "concat('\"',t3.name,'\"') as \"mouse name\", " + "concat('\"',source,'\"') as \"official symbol\" "
          + "FROM mouse_holder_facility t1 left join holder t2 on t1.holder_id=t2.id "
          + "left join mouse t3 on t1.mouse_id=t3.id " + "WHERE t3.id is not null and t3.status='live' "
          + "ORDER BY t2.lastname";

      String[] columnHeaders = new String[] { "holder id", "holder lastname", "holder firstname", "database record ID",
          "mouse name", "official symbol" };
      return runSimpleReport(query, columnHeaders, false);
    } else if (reportName.equals(ReportServlet.PPTChangeRequestImportReportName)) {
      if (args == null) {
        throw new IndexOutOfBoundsException("No report specified for import change request report");
      }
      int reportId = Integer.parseInt((String) args[0]);
      return runImportPPTChangeRequestReport(reportId);
    } else if (reportName.equals(ReportServlet.PurchaseChangeRequestImportReportName)) {
      if (args == null) {
        throw new IndexOutOfBoundsException("No report specified for import change request report");
      }
      int reportId = Integer.parseInt((String) args[0]);
      return runImportPurchaseChangeRequestReport(reportId);
    } else if (reportName.equals(ReportServlet.PurchaseSubmissionsImportReportName)) {
      if (args == null) {
        throw new IndexOutOfBoundsException("No report specified for import change request report");
      }
      int reportId = Integer.parseInt((String) args[0]);
      return runImportPurchaseSubmissionReport(reportId);
    } else if (reportName.equals(ReportServlet.OtherInstitutionsChangeRequestImportReportName)) {
      if (args == null) {
        throw new IndexOutOfBoundsException(
            "No report specified for other institutions transfer change request report");
      }
      int reportId = Integer.parseInt((String) args[0]);
      return runImportOtherInstitutionsChangeRequestReport(reportId);
    } else if (reportName.equals(ReportServlet.OtherInstitutionsSubmissionsImportReportName)) {
      if (args == null) {
        throw new IndexOutOfBoundsException(
            "No report specified for other institutions transfer change request report");
      }
      int reportId = Integer.parseInt((String) args[0]);
      return runImportOtherInstitutionsSubmissionReport(reportId);
    } else if (reportName.equals(ReportServlet.OtherInstitutionsUnpublishedImportReportName)) {
      if (args == null) {
        throw new IndexOutOfBoundsException("No report specified for other institutions transfer unpublished report");
      }
      int reportId = Integer.parseInt((String) args[0]);
      return runImportOtherInstitutionsUnpublishedReport(reportId);
    } else {
      throw new IndexOutOfBoundsException("Report " + reportName + " not found.  ");
    }
  }

  private static String runSimpleReport(String query, String[] columnHeaders) {
    return runSimpleReport(query, columnHeaders, true);
  }

  private static String runSimpleReport(String query, String[] columnHeaders, boolean addQuotes) {
    StringBuilder result = new StringBuilder();

    boolean first = true;
    for (String header : columnHeaders) {
      if (first) {
        first = false;
      } else {
        result.append(",");
      }

      result.append(header);
    }
    result.append("\r\n");

    ArrayList<ArrayList<String>> results = StringArrayListResultGetter.getInstance(columnHeaders).Get(query);

    for (ArrayList<String> ArrayList : results) {
      first = true;
      for (int i = 0; i < columnHeaders.length; i++) {
        if (first) {
          first = false;
        } else {
          result.append(",");
        }

        if (addQuotes) {
          result.append("\"");
        }
        String value = ArrayList.get(i);
        result.append(value != null ? value : "");
        if (addQuotes) {
          result.append("\"");
        }
      }
      result.append("\r\n");
    }

    return result.toString();
  }

  private static String runRecordsReport() {
    StringBuilder result = new StringBuilder();
    int maxHolders = 5;
    result.append("Record Number,Mouse Name,Category,Official Symbol,MGI ID,PMID(s),MTA Status,Submitter Name");
    for (int i = 0; i < maxHolders; i++) {
      result.append(",Holder " + (i + 1));
    }
    result.append("\r\n");
    ArrayList<MouseRecord> records = getMouseRecords(-1, "mouse.name", -1, -1, "live");
    for (MouseRecord mouseRecord : records) {
      int submissionID = -1;
      try {
        submissionID = Integer.parseInt(mouseRecord.getSubmittedMouseID());
      } catch (Exception e) {
      }

      SubmittedMouse submission = getSubmission(submissionID);

      String pubmeds = "";
      boolean first = true;
      for (String pmid : mouseRecord.getPubmedIDs()) {
        if (!first) {
          pubmeds += "; ";
        }
        first = false;
        pubmeds += pmid;
      }

      StringBuffer holderList = new StringBuffer();
      first = true;
      ArrayList<MouseHolder> holders = mouseRecord.getHolders();
      for (int i = 0; i < maxHolders; i++) {
        if (!first) {
          holderList.append(",");
        }
        first = false;
        holderList.append("\"");
        if (i < holders.size()) {
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
      if (submission != null) {
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

  private static String runImportPPTChangeRequestReport(int reportId) {
    ArrayList<Integer> changeRequestIds = getImportNewObjectIds(reportId);
    if (changeRequestIds.size() <= 0) {
      return "No change requests were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on changerequest.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append(
        "Email Sent,Request Number,PI Recipient,Mouse Name,Record #,Response,PI Sender,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<ChangeRequest> requests = getChangeRequests(additionalJoins, whereTerms, null);

    Comparator<ChangeRequest> comparator = new Comparator<ChangeRequest>() {
      public int compare(ChangeRequest a, ChangeRequest b) {
        return HTMLGeneration.emptyIfNull(a.getHolderLastname())
            .compareTo(HTMLGeneration.emptyIfNull(b.getHolderLastname()));
      }
    };
    Collections.sort(requests, comparator);

    for (ChangeRequest request : requests) {

      Properties props = request.Properties();

      result.append(",");
      result.append(request.getRequestID());
      result.append(",\"");
      result.append(HTMLGeneration.emptyIfNull(request.getHolderLastname() + ", " + request.getHolderFirstname()));
      result.append("\",\"");
      result.append(request.getMouseName());
      result.append("\",");
      result.append(request.getMouseID());
      result.append(",,\"");

      String oHolder = HTMLGeneration.emptyIfNull(props.getProperty("Original holder"));
      if (oHolder.isEmpty()) {
        oHolder = HTMLGeneration.emptyIfNull(props.getProperty("Original PI")); // legacy
                                                                                // property
                                                                                // name
      }
      result.append(oHolder);
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient email")));
      result.append("\"");
      result.append("\r\n");

    }
    return result.toString();
  }

  private static String runImportPurchaseChangeRequestReport(int reportId) {
    ArrayList<Integer> changeRequestIds = getImportNewObjectIds(reportId);
    if (changeRequestIds.size() <= 0) {
      return "No change requests were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on changerequest.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append(
        "Message Sent,Response,Request Number,PI Recipient,Mouse Name,Record #,MGI ID,Catalog Number,Purchaser,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<ChangeRequest> requests = getChangeRequests(additionalJoins, whereTerms, null);

    Comparator<ChangeRequest> comparator = new Comparator<ChangeRequest>() {
      public int compare(ChangeRequest a, ChangeRequest b) {
        return HTMLGeneration.emptyIfNull(a.getHolderLastname())
            .compareTo(HTMLGeneration.emptyIfNull(b.getHolderLastname()));
      }
    };

    Collections.sort(requests, comparator);

    for (ChangeRequest request : requests) {
      Properties props = request.Properties();
      result.append(",");
      result.append(",");
      result.append(request.getRequestID());
      result.append(",\"");
      result.append(HTMLGeneration.emptyIfNull(request.getHolderLastname() + ", " + request.getHolderFirstname()));
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

  private static String runImportOtherInstitutionsChangeRequestReport(int reportId) {
    ArrayList<Integer> changeRequestIds = getImportNewObjectIds(reportId);
    if (changeRequestIds.size() <= 0) {
      return "No change requests were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on changerequest.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append(
        "Message Sent,Response,Request Number,PI Recipient,Mouse Name,Record #,MGI ID,Sender Institution,Recipient,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<ChangeRequest> requests = getChangeRequests(additionalJoins, whereTerms, null);

    Comparator<ChangeRequest> comparator = new Comparator<ChangeRequest>() {
      public int compare(ChangeRequest a, ChangeRequest b) {
        return HTMLGeneration.emptyIfNull(a.Properties().getProperty("Recipient PI Name"))
            .compareTo(HTMLGeneration.emptyIfNull(b.Properties().getProperty("Recipient PI Name")));
      }
    };

    Collections.sort(requests, comparator);

    for (ChangeRequest request : requests) {
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
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Sender institution")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email")));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient Email")));
      result.append("\"");
      result.append("\r\n");

    }
    return result.toString();
  }

  private static String runImportOtherInstitutionsUnpublishedReport(int reportId) {
    ArrayList<String> unpublishedTransfers = getImportNewObjects(reportId);
    if (unpublishedTransfers.size() <= 0) {
      return "No unpublished transfers were found in report id " + reportId;
    }

    StringBuilder result = new StringBuilder();
    result.append(
        "Message Sent,Response,PI Recipient,Strain,Sender Institution,Recipient,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    // see importhandler.java where this is inserted
    // (unpublishedTransfersObjects)
    // 0 purchase.holderName, 1 purchase.strain, 2 purchase.pmid, 3
    // purchase.senderInstitution,
    // 4 purchase.recipientName, 5 purchase.recipientEmail, 6
    // purchase.holderEmail

    Comparator<String> comparator = new Comparator<String>() {
      public int compare(String a, String b) {
        return HTMLGeneration.emptyIfNull(a.split("\\|")[0]).compareTo(HTMLGeneration.emptyIfNull(b.split("\\|")[0]));
      }
    };

    Collections.sort(unpublishedTransfers, comparator);

    for (String str : unpublishedTransfers) {
      String[] transfer = str.split("\\|");
      result.append(",");
      result.append(",\"");
      result.append(HTMLGeneration.emptyIfNull(transfer[0]));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(transfer[1]));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(transfer[3]));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(transfer[4]));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(transfer[5]));
      result.append("\",\"");
      result.append(HTMLGeneration.emptyIfNull(transfer[6]));
      result.append("\"");
      result.append("\r\n");

    }
    return result.toString();
  }

  private static final class Sub {
    public String PIName;
    public String Line;

    public Sub(String piName, String line) {
      PIName = piName;
      Line = line;
    }
  }

  private static String runImportPurchaseSubmissionReport(int reportId) {
    ArrayList<Integer> submissionIds = getImportNewObjectIds(reportId);
    if (submissionIds.size() <= 0) {
      return "No submissions were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on submittedmouse.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append(
        "Message Sent,Response,Submission Number,PI Recipient,Mouse Name,MGI ID,Catalog Number,Purchaser,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<SubmittedMouse> submissions = getSubmissions(additionalJoins, whereTerms, null);

    List<Sub> linesByRecipientPI = new ArrayList<Sub>();

    for (SubmittedMouse submission : submissions) {
      Properties props = submission.getProperties();

      if (props.containsKey("holderCount")) {
        int holderCount = Integer.parseInt(props.getProperty("holderCount"));
        for (int i = 0; i < holderCount; i++) {
          StringBuilder line = new StringBuilder();
          String piName = HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name-" + i));
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
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser-" + i)));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email-" + i)));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("Purchaser email-" + i)));
          line.append("\"");
          line.append("\r\n");
          linesByRecipientPI.add(new Sub(piName, line.toString()));
        }
      } else {
        StringBuilder line = new StringBuilder();
        String piName = HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name"));
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

    /*
     * Comparator<Sub> comparator = new Comparator<Sub>(){ public int
     * compare(Sub a, Sub b) { return HTMLGeneration.emptyIfNull(a.PIName)
     * .compareTo(HTMLGeneration.emptyIfNull(b.PIName)); } };
     * Collections.sort(linesByRecipientPI,comparator);
     */
    for (Sub sub : linesByRecipientPI) {
      // TODO why don't we want the PIName as well??
      result.append(sub.Line);
    }

    return result.toString();
  }

  private static String runImportOtherInstitutionsSubmissionReport(int reportId) {
    ArrayList<Integer> submissionIds = getImportNewObjectIds(reportId);
    if (submissionIds.size() <= 0) {
      return "No submissions were created for report id " + reportId;
    }

    String additionalJoins = "left join import_new_objects on submittedmouse.id=import_new_objects.object_id";
    ArrayList<String> whereTerms = new ArrayList<String>();
    whereTerms.add("import_new_objects.import_report_id=" + reportId);
    StringBuilder result = new StringBuilder();
    result.append(
        "Message Sent,Response,Submission Number,PI Recipient,Mouse Name,MGI ID,Source Institution,Recipient,Email Recipient 1, Email Recipient 2");
    result.append("\r\n");

    List<SubmittedMouse> submissions = getSubmissions(additionalJoins, whereTerms, null);

    List<Sub> linesByRecipientPI = new ArrayList<Sub>();

    for (SubmittedMouse submission : submissions) {
      Properties props = submission.getProperties();

      if (props.containsKey("holderCount")) {
        int holderCount = Integer.parseInt(props.getProperty("holderCount"));
        for (int i = 0; i < holderCount; i++) {
          StringBuilder line = new StringBuilder();
          String piName = HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name-" + i));
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
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("Sender institution-" + i)));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient-" + i)));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email-" + i)));
          line.append("\",\"");
          line.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient Email-" + i)));
          line.append("\"");
          line.append("\r\n");
          linesByRecipientPI.add(new Sub(piName, line.toString()));
        }
      } else {
        StringBuilder line = new StringBuilder();
        String piName = HTMLGeneration.emptyIfNull(props.getProperty("Recipient PI Name"));
        line.append(submission.getSubmissionID());
        line.append(",\"");
        line.append(piName);
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("NewMouseName")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("MouseMGIID")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("Sender institution")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("New Holder Email")));
        line.append("\",\"");
        line.append(HTMLGeneration.emptyIfNull(props.getProperty("Recipient Email")));
        line.append("\"");
        line.append("\r\n");
        linesByRecipientPI.add(new Sub(piName, line.toString()));
      }
    }

    /*
     * Comparator<Sub> comparator = new Comparator<Sub>(){ public int
     * compare(Sub a, Sub b) { return HTMLGeneration.emptyIfNull(a.PIName)
     * .compareTo(HTMLGeneration.emptyIfNull(b.PIName)); } };
     * Collections.sort(linesByRecipientPI,comparator);
     */
    for (Sub sub : linesByRecipientPI) {
      // TODO why don't we want the PIName as well??
      result.append(sub.Line);
    }

    return result.toString();
  }

  // ************************************************************
  // SQL Methods
  // ************************************************************

  private static int executeNonQuery(String query) {
    return executeNonQuery(query, false);
  }

  private static int executeNonQuery(String query, boolean fetchLastInsertID) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    int lastInsertID = -1;

    query = validateQuery(query);

    try {
      connection = connect();
      statement = connection.createStatement();
      if (logQueries) Log.Info("*****************************\r\n" + "Executing insert query:\r\n" + query);
      statement.execute(query);
      if (fetchLastInsertID) {
        resultSet = statement.executeQuery("SELECT LAST_INSERT_ID()");
        if (resultSet.next()) {
          lastInsertID = resultSet.getInt("last_insert_id()");
        }
      }
    } catch (Exception e) {
      Log.Error("Error executing query: \r\n", e);
    } finally {
      try {
        if (resultSet != null) resultSet.close();
      } catch (Exception e) {
        Log.Error("Error closing SQL resultSet", e);
      }
      try {
        if (statement != null) statement.close();
      } catch (Exception e) {
        Log.Error("Error closing SQL statement.", e);
      }
      try {
        if (connection != null) connection.close();
      } catch (Exception e) {
        Log.Error("Error closing SQL connection.", e);
      }
    }
    return lastInsertID;
  }

  public static String addMySQLEscapes(String input) {
    if (input == null) {
      return "";
    }

    String output = input.replace("'", "\\'");
    output = output.replace("\"", "\\\"");

    return output;
  }

  private static String validateQuery(String query) {
    String newQuery = query;
    // if (query.indexOf(';') > 0)
    // {
    // //Possible SQL injection attack
    // System.err.println("WARNING - Possible SQL injection attack in query:
    // \r\n"
    // + query);
    //
    // newQuery = query.substring(0,query.indexOf(';'));
    // System.err.println("Trimming query up to first semicolon. Executing query
    // below:\r\n"
    // + newQuery);
    //
    // }
    return newQuery;
  }

  // ************************************************************
  // Object accessor classes
  // ************************************************************

  private abstract static class ResultGetter {
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

    public final <T> ArrayList<T> Get(String query) {
      query = validateQuery(query);
      ArrayList<T> results = new ArrayList<T>();
      try {
        if (_connection == null) {
          _connection = connect();
          if (logQueries) {
            Log.Info("Opened connection " + _connection.toString());
          }
        }
        _statement = _connection.createStatement();

        if (logQueries) {
          Log.Info("*****************************\r\n" + "Executing query: \r\n" + "Connection:"
              + _connection.toString() + "\r\n" + query);
        }

        _resultSet = _statement.executeQuery(query);

        getResults(results);
      } catch (Exception e) {
        Log.Error("Error executing query.\r\n" + "Connection:" + _connection.toString() + "\r\n" + query, e);
      } finally {
        try {
          if (_resultSet != null) _resultSet.close();
        } catch (Exception e) {
          Log.Error("Error closing SQL resultset.", e);
        }
        try {
          if (_statement != null) _statement.close();
        } catch (Exception e) {
          Log.Error("Error closing SQL statement.\r\n", e);
        }
        try {
          if (!is_preserveConnection() && _connection != null) {
            if (logQueries) {
              Log.Info("Closing connection " + _connection.toString() + "...");
            }
            _connection.close();
            if (logQueries) {
              Log.Info("Closed!");
              Log.Info("**************************");
            }
          }
        } catch (Exception e) {
          Log.Error("Error closing SQL connection", e);
        }
      }
      return results;
    }

    @SuppressWarnings("unchecked")
    protected <T> void getResults(ArrayList<T> results) throws SQLException {
      while (_resultSet.next()) {
        results.add((T) getNextItem());
      }
    }

    abstract protected Object getNextItem() throws SQLException;

    protected int g_int(String fieldName) throws SQLException {
      return _resultSet.getInt(fieldName);
    }

    protected String g_str(String fieldName) throws SQLException {
      return _resultSet.getString(fieldName);
    }

    protected Boolean g_bool(String fieldName) throws SQLException {
      return _resultSet.getBoolean(fieldName);
    }

    protected Date g_date(String fieldName) throws SQLException {
      return _resultSet.getDate(fieldName);
    }

    protected Timestamp g_ts(String fieldName) throws SQLException {
      return _resultSet.getTimestamp(fieldName);
    }
    
    protected Blob g_blob(String fieldName) throws SQLException{
    	return _resultSet.getBlob(fieldName);
    }
    
  }

  private static final class MouseRecordResultGetter extends ResultGetter {
    public static MouseRecordResultGetter getInstance() {
      return new MouseRecordResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      MouseRecord nextMouse = new MouseRecord();

      nextMouse.setMouseID(g_str("id"));
      nextMouse.setOfficialMouseName(g_str("official_name"));
      nextMouse.setMouseName(g_str("name"));
      nextMouse.setMouseType(g_str("mousetype"));

      nextMouse.setGeneID(g_str("gene mgi"));
      nextMouse.setGeneName(g_str("gene name"));
      nextMouse.setGeneSymbol(g_str("gene symbol"));

      nextMouse.setTargetGeneID(g_str("target gene mgi"));
      nextMouse.setTargetGeneName(g_str("target gene name"));
      nextMouse.setTargetGeneSymbol(g_str("target gene symbol"));

      nextMouse.setModificationType(g_str("modification_type"));
      nextMouse.setRegulatoryElement(g_str("regulatory element"));

      nextMouse.setExpressedSequence(g_str("expressedSequence"));
      nextMouse.setOtherComment(g_str("other_comment"));
      nextMouse.setReporter(g_str("reporter"));
      nextMouse.setTransgenicType(g_str("transgenictype"));

      nextMouse.setMtaRequired(g_str("mta_required"));
      nextMouse.setGeneralComment(g_str("general_comment"));
      nextMouse.setBackgroundStrain(g_str("strain"));

      nextMouse.setSource(g_str("source"));
      nextMouse.setRepositoryTypeID(g_str("repository_id"));
      nextMouse.setRepositoryCatalogNumber(g_str("repository_catalog_number"));

      nextMouse.setGensat(g_str("gensat"));

      nextMouse.setCryopreserved(g_str("cryopreserved"));

      nextMouse.setStatus(g_str("status"));

      nextMouse.setEndangered(g_bool("endangered"));

      nextMouse.setSubmittedMouseID(g_str("submittedmouse_id"));

      nextMouse.setAdminComment(g_str("admin_comment"));

      nextMouse.setHolders(getMouseHolders(nextMouse.getMouseID()));
      nextMouse.setPubmedIDs(getMousePubmedIDs(nextMouse.getMouseID()));
      
      nextMouse.setFilenames(getFilenames(nextMouse.getMouseID()));
      nextMouse.setFileIDs(getFileIDs(nextMouse.getMouseID()));

      return nextMouse;
    }

    private ArrayList<Integer> getFileIDs(String mouseID) throws  SQLException {
      String query = "SELECT ID FROM mouseFiles WHERE mouseID='" + mouseID + "'";
      return IntResultGetter.getInstance("ID").Get(query);
    }

    private ArrayList<MouseHolder> getMouseHolders(String mouseID) throws SQLException {
      String query = mouseHolderQueryHeader + " WHERE mouse_id='" + mouseID
          + "' and status='active'\r\nORDER BY lastname";

      return MouseHolderResultGetter.getInstance(_connection).Get(query);
    }

    private ArrayList<String> getMousePubmedIDs(String mouseID) throws SQLException {
      String query = "SELECT pmid \r\nFROM mouse_literature left join "
          + "literature on mouse_literature.literature_id=literature.id " + "\r\n WHERE mouse_id='" + mouseID + "'";
      return StringResultGetter.getInstance("pmid", _connection).Get(query);
    }
    
    private ArrayList<File> getFilenames(String mouseID) throws SQLException
    {
  	  String query = "SELECT file, filename FROM mouseFiles WHERE mouseID='" + mouseID + "'";
  	  return MouseFileResultGetter.getInstance(_connection).Get(query);
    }
  }
  //added static to getFileByNameAndMouseID -EW
  public static File getFileByNameAndMouseID(String fileName, String mouseID) throws Exception
  {
    Connection con = connect();
    String query = "SELECT file, filename FROM mouseFiles WHERE mouseID='" + mouseID + "'" + " AND filename='" + fileName + "'";
      ArrayList<File> allFiles = MouseFileResultGetter.getInstance(con).Get(query);
    return allFiles.get(0);
  } 

    public static String getFileNamesAsString(String mouseID) throws Exception{
    ArrayList<String> filenames = getFilenamesByMouseID(mouseID);
    String store = "";
    for(String name : filenames){
      store += "/" + name;
    }
    return store;
  }
  
  public static String getIDsAsString(String mouseID) throws Exception{
    ArrayList<Integer> ids = getFileIDsByMouseID(mouseID);
    String store = "";
    for(Integer id : ids){
      store += "/" + id;
    }
    return store;
  }

  public static ArrayList<Integer> getFileIDsByMouseID(String mouseID) throws Exception{
    return MouseRecordResultGetter.getFileIDs(mouseID);
  }

  public static File getFileByID(Integer ID) throws Exception {
    Connection con = connect();
    String query = "SELECT file, filename FROM mouseFiles WHERE ID='" + ID + "'";
    ArrayList<File> allFiles = MouseFileResultGetter.getInstance(con).Get(query);
    return allFiles.get(0);
  }

  public static void deleteFileByID(Integer ID) throws Exception {
    //Connection con = connect();
    String query = "DELETE FROM mouseFiles WHERE ID = '" + ID + "'";
    executeNonQuery(query);
  }

  private static final class ChangeRequestResultGetter extends ResultGetter {
    public static ChangeRequestResultGetter getInstance() {
      return new ChangeRequestResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      ChangeRequest result = new ChangeRequest();

      result.setRequestID(g_int("changerequest.id"));
      int mouseRecordID = g_int("mouse_id");
      result.setMouseID(mouseRecordID <= 0 ? -1 : mouseRecordID);
      result.setMouseName(g_str("mouse.name"));
      result.setAdminComment(g_str("admin_comment"));
      result.setFirstname(g_str("firstname"));
      result.setLastname(g_str("lastname"));
      result.setEmail(g_str("email"));
      result.setStatus(g_str("changerequest.status"));
      result.setAdminComment(g_str("admin_comment"));
      result.setUserComment(g_str("user_comment"));
      result.setRequestSource(g_str("request_source"));
      int holderId = g_int("holder_id");
      result.setHolderId(holderId);
      String holderName = g_str("holder_name");

      result.setRequestDate(g_date("requestDate").toString());

      Date lastAdminDate = g_date("lastadmindate");
      result.setLastAdminDate(lastAdminDate != null ? lastAdminDate.toString() : "");

      result.setProperties(g_str("properties"));
      result.setActionRequested(ChangeRequest.ActionValues[g_int("action_requested")]);

      if (result.actionRequested() != Action.ADD_HOLDER && result.actionRequested() != Action.REMOVE_HOLDER
          && result.actionRequested() != Action.CHANGE_CRYO_LIVE_STATUS) {
        return result;
      }

      if (holderId != 0 || holderName != null) {
        // newer change requests
        int facilityId = g_int("facility_id");
        result.setFacilityId(facilityId);
        if (facilityId > 0) {
          result.setFacilityName(g_str("facility.facility"));
        } else if (facilityId == -2) {
          result.setFacilityName(g_str("facility_name"));
        }

        if (holderId > 0) {
          result.setHolderName(g_str("holder.lastname") + ", " + g_str("holder.firstname"));
          result.setHolderFirstname(g_str("holder.firstname"));
          result.setHolderLastname(g_str("holder.lastname"));
          result.setHolderEmail(g_str("holder.email"));
        } else {
          if (holderName != null && holderName.indexOf(' ') > 0) {
            int lastSpaceIndex = holderName.lastIndexOf(' ');
            result.setHolderLastname(holderName.substring(lastSpaceIndex + 1));
            result.setHolderFirstname(holderName.substring(0, lastSpaceIndex));
          } else {
            result.setHolderLastname(holderName);
            result.setHolderFirstname("");
          }
          result.setHolderEmail(g_str("holder_email"));
          result.setHolderName(holderName);
        }
        result.setGeneticBackgroundInfo(g_str("genetic_background_info"));
        result.setCryoLiveStatus(g_str("cryo_live_status"));
      } else if (result.Properties() != null) {
        // legacy change requests
        result.setFacilityId(-1);
        result.setHolderId(-1);
        if (result.Properties() != null) {
          for (Object key : result.Properties().keySet()) {
            String propertyName = (String) key;
            String propertyValue = (String) result.Properties().get(key);
            String preValue = null;

            int splitterIndex = propertyValue.indexOf('|');
            if (splitterIndex > 0) {
              preValue = propertyValue.substring(0, splitterIndex);
              propertyValue = propertyValue.substring(splitterIndex + 1);
            }
            if (propertyName.matches("Add Holder( Name)?|Remove Holder|Delete Holder Name")) {
              String holderPropertyName = propertyValue;
              if (holderPropertyName == null) {
                continue;
              }

              String holderLastname = "";
              String holderFirstname = "";
              if (holderPropertyName.indexOf(',') > 0) {
                String[] tokens = holderPropertyName.split(",");
                if (tokens.length != 2) {
                  continue;
                }
                holderLastname = tokens[0].trim();
                holderFirstname = tokens[1].trim();

                int periodIndex = holderFirstname.indexOf('.');

                if (periodIndex == 1) {
                  holderFirstname = holderFirstname.substring(periodIndex + 1).trim();
                } else if (periodIndex == holderFirstname.length() - 1) {
                  holderFirstname = holderFirstname.substring(0, periodIndex - 2).trim();
                }
              } else if (holderPropertyName.indexOf(' ') > 0) {
                int lastSpaceIndex = holderPropertyName.lastIndexOf(' ');
                holderLastname = holderPropertyName.substring(lastSpaceIndex + 1);
                holderFirstname = holderPropertyName.substring(0, lastSpaceIndex);
              }

              result.setHolderName(holderPropertyName);
              result.setHolderFirstname(holderFirstname);
              result.setHolderLastname(holderLastname);

              if (propertyName.matches("Add Holder( Name)?")) {
                result.setActionRequested(Action.ADD_HOLDER);
              } else {
                result.setActionRequested(Action.REMOVE_HOLDER);
              }
              if (preValue != null && preValue.matches("[\\d]+")) {
                result.setHolderId(Integer.parseInt(preValue));
              }
            } else if (propertyName.matches("Add Facility( Name)?|Facility|Delete Facility Name")) {
              result.setFacilityName(propertyValue);
            } else if (propertyName.equals("Request Source")) {
              result.setRequestSource(propertyValue);
            } else if (propertyName.equals("New Holder Email")) {
              result.setHolderEmail(propertyValue);
            }
          }
        } else {
          // TODO parse holder name out of comment
        }
        String comment = result.getUserComment();
        if (comment.contains("(Live only)")) {
          result.setCryoLiveStatus("Live only");
        } else if (comment.contains("(Live and Cryo)")) {
          result.setCryoLiveStatus("Live and Cryo");
        } else if (comment.contains("(Cryo only)")) {
          result.setCryoLiveStatus("Cryo only");
        }
      }
      return result;
    }
  }

  private static final class SubmittedMouseResultGetter extends ResultGetter {
    public static SubmittedMouseResultGetter getInstance() {
      return new SubmittedMouseResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      SubmittedMouse result = new SubmittedMouse();

      result.setSubmissionID(g_int("submittedmouse.id"));
      int mouseRecordID = g_int("mouseRecordID");
      result.setMouseRecordID(mouseRecordID <= 0 ? -1 : mouseRecordID);

      result.setFirstName(g_str("firstname"));
      result.setLastName(g_str("lastname"));
      result.setDepartment(g_str("dept"));
      result.setEmail(g_str("email"));
      result.setTelephoneNumber(g_str("tel"));

      result.setSubmissionDate(g_date("date"));
      result.setStatus(g_str("status"));
      result.setAdminComment(g_str("admincomment"));
      result.setEntered(g_str("entered").equalsIgnoreCase("Y"));

      result.parseProperties(g_str("properties"));
      result.setSubmissionSource(g_str("submission_source"));
      return result;
    }
  }

  private static final class MouseHolderResultGetter extends ResultGetter {

    private void set_connection(Connection connection) {
      _connection = connection;
    }

    public static MouseHolderResultGetter getInstance() {
      return getInstance(null);
    }

    public static MouseHolderResultGetter getInstance(Connection connection) {

      MouseHolderResultGetter instance = new MouseHolderResultGetter();
      if (connection != null) {
        instance.set_connection(connection);
        instance.set_preserveConnection(true);
      }
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException {
      MouseHolder holder = new MouseHolder();
      holder.setHolderID(g_int("holder_id"));
      holder.setFirstname(g_str("firstname"));
      holder.setLastname(g_str("lastname"));
      holder.setDept(g_str("department"));
      holder.setEmail(g_str("email"));
      holder.setAlternateEmail(g_str("alternate_email"));
      holder.setAlternateName(g_str("alternate_name"));
      holder.setTel(g_str("tel"));

      holder.setFacilityID(g_int("facility_id"));
      holder.setFacilityName(g_str("facility"));

      holder.setCovert(g_bool("covert"));
      holder.setCryoLiveStatus(g_str("cryo_live_status"));
      return holder;
    }
  }

  private static final class FacilityResultGetter extends ResultGetter {
    public static FacilityResultGetter getInstance() {
      return new FacilityResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      Facility result = new Facility();
      result.setFacilityID(g_int("id"));
      result.setFacilityName(g_str("facility"));
      result.setFacilityDescription(g_str("description"));
      result.setFacilityCode(g_str("code"));
      result.setRecordCount(g_int("mice held"));
      result.setLocalExperts(g_str("local_experts"));
      return result;

    }
  }

  private static final class HolderResultGetter extends ResultGetter {

    public static HolderResultGetter getInstance() {
      return new HolderResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      Holder result = new Holder();
      result.setHolderID(g_int("id"));
      result.setFirstname(g_str("firstname"));
      result.setLastname(g_str("lastname"));
      result.setDept(g_str("department"));
      result.setEmail(g_str("email"));
      result.setAlternateEmail(g_str("alternate_email"));
      result.setAlternateName(g_str("alternate_name"));
      result.setTel(g_str("tel"));
      result.setVisibleMouseCount(g_int("mice held"));
      result.setCovertMouseCount(g_int("covert mice held"));
      result.setDateValidated(g_str("datevalidated"));
      result.setValidationComment(g_str("validation_comment"));
      result.setValidationStatus(g_str("validation_status"));
      result.setStatus(g_str("status"));
      result.setDeadbeat(g_bool("is_deadbeat"));
      result.setPrimaryMouseLocation(g_str("primary_mouse_location"));
      return result;
    }
  }

  private static final class GeneResultGetter extends ResultGetter {

    public static GeneResultGetter getInstance() {
      return new GeneResultGetter();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> void getResults(ArrayList<T> results) throws SQLException {
      Gene result = null;
      Map<Integer, Gene> tempResults = new HashMap<Integer, Gene>();

      while (_resultSet.next()) {
        result = new Gene();
        result.setGeneRecordID(g_int("id"));
        result.setFullname(g_str("fullname"));
        result.setSymbol(g_str("symbol"));
        result.setMgiID(g_str("mgi"));
        results.add((T) result);
        tempResults.put(result.getGeneRecordID(), result);
      }

      String query = "select g1.id,g1.symbol,g1.fullname,g1.mgi,count(*) as 'record count'"
          + " from mouse left join gene g1 on mouse.gene_id=g1.id" + " left join gene g2 on mouse.target_gene_id=g2.id"
          + " where status='live'" + " group by g1.id";

      _resultSet = _statement.executeQuery(query);
      while (_resultSet.next()) {
        Integer key = g_int("id");
        if (tempResults.containsKey(key)) {
          tempResults.get(key).setRecordCount(g_int("record count"));
        }
      }
    }

    @Override
    protected Object getNextItem() throws SQLException {
      // unused because we override getResults in this class
      return null;
    }
  }

  private static final class StringArrayListResultGetter extends ResultGetter {
    private String[] _columnNames;

    private void set_columnName(String[] columnNames) {
      this._columnNames = columnNames;
    }

    public static StringArrayListResultGetter getInstance(String[] columnNames) {
      StringArrayListResultGetter instance = new StringArrayListResultGetter();
      instance.set_columnName(columnNames);
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException {
      ArrayList<String> result = new ArrayList<String>();
      for (String columnName : _columnNames) {
        result.add(g_str(columnName));
      }

      return result;
    }
  }

  private static final class StringResultGetter extends ResultGetter {
    private String _columnName;

    private void set_columnName(String columnName) {
      this._columnName = columnName;
    }

    private void set_connection(Connection connection) {
      _connection = connection;
    }

    public static StringResultGetter getInstance(String columnName) {
      return getInstance(columnName, null);
    }

    public static StringResultGetter getInstance(String columnName, Connection connection) {

      StringResultGetter instance = new StringResultGetter();
      if (connection != null) {
        instance.set_connection(connection);
        instance.set_preserveConnection(true);
      }
      instance.set_columnName(columnName);
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException {
      return g_str(_columnName);
    }
  }

  private static final class IntResultGetter extends ResultGetter {
    private String _columnName;

    private void set_columnName(String columnName) {
      this._columnName = columnName;
    }

    public static IntResultGetter getInstance(String columnName) {

      IntResultGetter instance = new IntResultGetter();
      instance.set_columnName(columnName);
      return instance;
    }

    @Override
    protected Object getNextItem() throws SQLException {
      return g_int(_columnName);
    }
  }
  
  private static final class MouseFileResultGetter extends ResultGetter
  {
	  /*public static MouseFileResultGetter getInstance()
	  {
		  return new MouseFileResultGetter();
	  }*/
	  
	  private void set_connection(Connection connection)
	    {
	      _connection = connection;
	    }
	  
	  //gets the files associated with a given mouse
	  public static MouseFileResultGetter getInstance(Connection connection)
	  {
		  MouseFileResultGetter instance = new MouseFileResultGetter();
	      if (connection != null)
	      {
	        instance.set_connection(connection);
	        instance.set_preserveConnection(true);
	      }
	      return instance;
	  }
	  
	  
	  //gets the next blob and filename, makes a file with the right name, fills it with data from the blob
	  @Override
	  protected Object getNextItem() throws SQLException
	  {
		  //get blob and filename
		  String filename = g_str("filename");
		  Blob myBlob = g_blob("file");
		  File file = new File(filename);
		  InputStream input = myBlob.getBinaryStream();
		  try {
			  //int fileLength = input.available();
			  byte[] buffer = new byte[input.available()];
		  
			  OutputStream output = new FileOutputStream(file);
		  
			  input.read(buffer);
			  output.write(buffer);
			  output.close();
			  
		  } catch(IOException e) {
			  //exception
		  }
		  return file;
	  }
  }

  private static final class ImportReportResultGetter extends ResultGetter {

    public static ImportReportResultGetter getInstance() {
      return new ImportReportResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      ImportReport result = new ImportReport();
      result.setImportReportID(g_int("id"));
      result.setImportType(ImportHandler.GetImportType(g_int("report_type")));
      result.setName(g_str("name"));
      result.setCreationDate(g_date("creationdate"));
      result.setReportText(g_str("reporttext"));
      return result;
    }
  }

  private static final class EmailResultGetter extends ResultGetter {
    public static EmailResultGetter getInstance() {
      return new EmailResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      MouseMail email = new MouseMail(g_str("recipients"), g_str("ccs"), g_str("bccs"), g_str("subject"), g_str("body"),
          g_str("category"), g_str("template_name"), g_str("attachment_names"));
      email.dateCreated = g_ts("date_created");
      email.status = g_str("status");
      email.id = g_int("id");
      email.emailType = g_str("emailType");
      return email;
    }
  }

  private static final class SettingResultGetter extends ResultGetter {
    public static SettingResultGetter getInstance() {
      return new SettingResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      Setting setting = new Setting();
      setting.id = g_int("id");
      setting.name = g_str("name");
      setting.category_id = g_int("category_id");
      setting.label = g_str("label");
      setting.value = g_str("setting_value");
      setting.secondaryValue = g_str("secondary_value");
      setting.dateUpdated = g_ts("date_updated");
      setting.textAreaRows = g_int("text_area_rows");
      return setting;
    }
  }

  private static final class EmailTemplateResultGetter extends ResultGetter {
    public static EmailTemplateResultGetter getInstance() {
      return new EmailTemplateResultGetter();
    }

    @Override
    protected Object getNextItem() throws SQLException {
      EmailTemplate template = new EmailTemplate();
      template.id = g_int("id");
      template.name = g_str("name");
      template.subject = g_str("subject");
      template.body = g_str("body");
      template.category = g_str("category");
      template.dateUpdated = g_ts("date_updated");
      return template;
    }
  }

  public static boolean isDuplicateSubmission(MouseSubmission newMouse, StringBuilder outputBuffer) {
    boolean isDuplicate = false;
    int existingRecordID = -1;

    if (newMouse.isPublished() && (newMouse.isTG() || newMouse.isMA())) {
      String repositoryCatalogID = newMouse.getMouseMGIID();
      if (repositoryCatalogID != null && !repositoryCatalogID.equalsIgnoreCase("none")) {
        // querier.setQuery("SELECT id FROM mouse WHERE
        // repository_catalog_number='" + repositoryCatalogID + "'");
        // String existingMouseID = querier.getFirstQueryResult();
        existingRecordID = checkForDuplicates(Integer.parseInt(newMouse.getMouseMGIID()), -1);
      }
    } else if (newMouse.isIS()) {
      // check supplier
      String supplier = newMouse.getISSupplier();
      if (newMouse.getISSupplierCatalogNumber() == null) {
        // don't dupe check records with no catalog number
        supplier = null;
      } else {
        supplier += ", " + newMouse.getISSupplierCatalogNumber();
      }
      if (supplier != null) {
        // TODO have different validation rules for non-jax mice
        String supplierRegex = supplier.trim().replace(",", "[,]*");
        supplierRegex = supplierRegex.replace(" ", "[ ]*");
        existingRecordID = checkForDuplicates(supplierRegex);
      }
    }
    if (existingRecordID > 0) {
      ArrayList<MouseRecord> existingMice = getMouseRecord(existingRecordID);
      if (existingMice.size() > 0) {
        outputBuffer.append("<div class='duplicateDescriptor'><h4>Duplicate entry detected for submission with MGI ID "
            + newMouse.getMouseMGIID() + ".  Existing record ID is #" + existingRecordID + "</h4>");
        MouseRecord existingMouse = existingMice.get(0);

        if (!existingMouse.isHidden()) {
          outputBuffer.append(HTMLGeneration.getMouseTable(existingMice, false, true, false));
          outputBuffer.append("<br>This appears to be a duplicate entry and will not be processed.<br>");
          isDuplicate = true;
        } else {
          outputBuffer.append(
              "<br>NOTE: this is a DUPLICATE but the submission was allowed because that record is hidden.  (Incomplete, deleted, or only covert holders)");
        }
      }
      outputBuffer.append("</div>");
    }
    return isDuplicate;
  }
}
