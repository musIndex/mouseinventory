<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%=getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%@include file="mouselistcommon.jspf" %>
<%=getNavBar("MouseReport.jsp", false) %>


<%
  int holderReviewDays = 180;

  int holderID = stringToInt(request.getParameter("holder_id"));
  int geneID = stringToInt(request.getParameter("geneID"));
    int mouseTypeID = stringToInt(request.getParameter("mousetype_id"));
    int creOnly = stringToInt(request.getParameter("creonly"));
    int facilityID = stringToInt(request.getParameter("facility_id"));
    int pagenum = stringToInt(request.getParameter("pagenum"));
    int limit = stringToInt(request.getParameter("limit"));

    String orderBy = request.getParameter("orderby");

    if (orderBy == null || orderBy.isEmpty())
    {
      orderBy = "mouse.name";
    }

    String searchTerms = null;

  if(creOnly < 0)
  {
    creOnly = 0;
  }

  if (limit == -1)
  {
    if (session.getAttribute("limit") != null)
    {
      limit = Integer.parseInt(session.getAttribute("limit").toString());
    }
    else
    {
      limit = 25;
    }
  }
  session.setAttribute("limit",limit);
  if (pagenum == -1)
  {
    pagenum = 1;
  }
  int offset = limit * (pagenum - 1);

  ArrayList<String> query = new ArrayList<String>();
  query.add("holder_id=" + holderID);
  query.add("orderby=" + orderBy);
  query.add("geneID=" + geneID);
  query.add("creonly=" + creOnly);
  query.add("mousetype_id=" + mouseTypeID);
  query.add("facility_id=" + facilityID);
  query.add("status=live");

  String queryString = StringUtils.join(query, "&");

  int mouseCount = DBConnect.countMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null, false, creOnly, facilityID);
  ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null, false, creOnly, facilityID,limit,offset);

    String table = getMouseTable(mice, false, true, false);
    ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();
    String mouseTypeSelectionLinks = getMouseTypeSelectionLinks(
        mouseTypeID, orderBy,holderID,geneID,mouseTypes,null,searchTerms,creOnly,facilityID);

    String topPageSelectionLinks = getPageSelectionLinks(limit,pagenum,mouseCount,true);
    String bottomPageSelectionLinks = getPageSelectionLinks(limit,pagenum,mouseCount,false);

    Holder holder = DBConnect.getHolder(holderID);
    Gene gene = DBConnect.getGene(geneID);
    Facility facility = DBConnect.getFacility(facilityID);

    String mouseTypeStr = "Listing";
    String mouseCountStr = "";
    if(mouseTypeID != -1)
    {
      for(MouseType type : mouseTypes)
      {
        if(type.getMouseTypeID() == mouseTypeID)
        {
          mouseTypeStr += " " + type.getTypeName();
          break;
        }
      }
    }
    else
    {
      mouseTypeStr += " all";
    }

    if(mice.size() > 0)
    {
      mouseCountStr = mouseCount + " found";
      if (limit > 0 && mice.size() == limit)
        mouseCountStr += " (" + limit + " shown per page)";
    }
    String holderData = "";
    String holderListDiv = "<div style=\"color: blue; font-weight: bold; position: "+
      "absolute; left:650px; top:210px; width:250px; \">" +
      "<i>To obtain a list of all mice held by an investigator, go to the 'Holder List'."+
      "</i></div>";
    mouseTypeStr += " records";

    if (facility != null)
    {
      mouseTypeStr += " in facility " + facility.getFacilityName();
    }

    if (holder != null)
    {
      holderListDiv = null;
    mouseTypeStr += " held by " + holder.getFullname();
    }
    else if(gene != null)
    {
    mouseTypeStr += " with gene <span class=\"geneSymbol\">" + gene.getSymbol() + "</span> - <span class=\"geneName\">" + gene.getFullname() + "</span>";
  }

    if(creOnly > 0)
    {
      mouseTypeStr += " with expressed sequence type CRE";
    }
    if (holder != null)
    {
      String holderStatusHeading = "";
    String holderStatusHeadingStyle = "redboldheading";
    if (holder.getDateValidated() != null)
    {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date validationDate = dateFormat.parse(holder.getDateValidated());
      long duration = System.currentTimeMillis() - validationDate.getTime();
      long days = TimeUnit.MILLISECONDS.toDays(duration);

      holderStatusHeading = "It has been " + days + " day" + (days != 1 ? "s" : "")
        + " since this list was thoroughly reviewed.";
      holderStatusHeadingStyle = "boldheading";
      //if (days >= holderReviewDays)
      //{
      //  holderStatusHeading += " Please update.";
      //}
      //else
      //{
      //  holderStatusHeading += " The list is up-to-date.  Thank you.";
      //  holderStatusHeadingStyle = "boldheading";
      //}

      mouseTypeStr += " (Last reviewed: " + holder.getDateValidated() + ")";
    }
    else
    {
      holderStatusHeading = "Please take this opportunity to update the list.";
    }

    holderData = "<div class='holderData'><div class='holderNotice'>" +
    "<span class='"+holderStatusHeadingStyle + "'>" + holderStatusHeading + "</span><br><br>" +

    "<b>To delete a mouse</b> that is no longer being maintained " +
    "but is still listed here, go to the record for that mouse, " +
    "click on 'request change in record,' and follow the instructions " +
    "for deleting a holder.<br><br>" +
    "If any corrections need to be made, click on 'request change in record,'" +
    "and follow the instructions for entering info about the changes.'" +
    "(For example, is an 'unpublished' allele/transgene now published?" +
      "Is there genetic background information that could be included?)" +
      "<br><br>" +
    "<b>To add a mouse</b> that is being maintained by this holder but " +
    "is not yet listed:"+
    "<ul>"+
    "<li>First do a quick search of the database "+
    "(top right of the screen) to see if there is already a record for that mouse."+
    "</li>"+
    "<li>"+
    "If so, click on 'request change in record,' and follow the instructions for adding a holder." +
    "</li>"+
    "<li>"+
    "If there is no record for that mouse in the database, complete a new submission for it or " +
    "contact database admin to request assistance in entering it." +
    "</li>" +
    "</ul>" +
    "</div>" +
    "Use this " + 
    getMailToLink(AdminEmail, null, holder.getLastname() + " mouse list reviewed", 
                "The list of mice held by "+ holder.getFullname() + " " +
                "was thoroughly reviewed and any necessary deletions/additions/corrections " +
                "were made today" +
                "\n(If mice still need to be added, please provide a list of " +
                "their names below.)", "email link") +
    " to notify admin when the list of mice has been thorougly reviewed and updated. " +
    "(Please add any feedback about the database to the e-mail)" +
    "</div>";
    }


%>
<div id="mousecount" style="display:none"><%=mice.size() %></div>
<div class="pagecontent pagecontent-leftaligned">

<h2><%=mouseTypeStr %></h2>
<h4><%=mouseCountStr %></h4>
<div id="controls">
<%=emptyIfNull(holderData) %>

<!-- -<p><a href="EndangeredReport.jsp">View Endangered Mice</a></p>-->
<form action="MouseReport.jsp" method="get"><%= mouseTypeSelectionLinks %>
<%= topPageSelectionLinks %> <%=emptyIfNull(holderListDiv) %>

</div>
<%
if (holder != null)
    { %>
<a class="btn btn-small btn-info" style="text-decoration:none" href="<%= siteRoot %>MouseList<%= (queryString.length() > 0 ? "?" + queryString : "") %> ">Download this list (pdf)</a>
<%} %>
<%= table %>
<div id="bottomControls">
<%= bottomPageSelectionLinks %>
</div>
</form>
</div>
