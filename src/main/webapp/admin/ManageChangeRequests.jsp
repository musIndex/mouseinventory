<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.ResultSet" %>

<%=getPageHeader(null,false,true) %>
<%=getNavBar("ManageChangeRequests.jsp", true) %>
<%@ include file='SendMailForm.jspf' %>
<div class="site_container">



<%
  String newStatus = request.getParameter("newStatus");
  String idToUpdate = request.getParameter("idToUpdate");
  String updateMessage = "";

  String status = request.getParameter("status");
  String orderBy = request.getParameter("orderby");

  if(status == null)
  {
    if ((status = (String)session.getAttribute("manageChangeRequestStatus")) == null)
    {
      status = "new";
    }
  }
  session.setAttribute("manageChangeRequestStatus",status);
  if(orderBy == null)
  {
    if((orderBy = (String)session.getAttribute("manageChangeRequestOrderBy")) == null)
    {
      orderBy = "changerequest.id";
    }
  }
  else
  {
    session.setAttribute("manageChangeRequestOrderBy",orderBy);
  }
  
    String[] sortOptions = new String[] {"changerequest.id","requestdate","requestdate DESC", "mouse_id","mouse_id DESC","firstname","lastname"};
    String[] sortOptionNiceNames = new String[] {"Request #","Request date", "Reverse request date","Record #", "Reverse Record #","Requestor first name", "Requestor last name"};

    String[] filterOptions = new String[] {"new","pending","done","all"};
    String[] filterOptionNiceNames = new String[] {"New", "Pending", "Completed","All"};

    StringBuffer sortBuf = new StringBuffer();
    sortBuf.append("<form action=\"ManageChangeRequests.jsp\" method=\"get\">");
    sortBuf.append("&nbsp;Show: ");
    sortBuf.append(genFlatRadio("status",filterOptions,filterOptionNiceNames, status,""));
    sortBuf.append("<br>&nbsp;Sort by: ");
    sortBuf.append(genFlatRadio("orderby",sortOptions,sortOptionNiceNames, orderBy,""));
    sortBuf.append("<br>&nbsp;<input class='btn' type='submit' value=\"Update\">");
    sortBuf.append("</form>");

  ArrayList<ChangeRequest> requests = DBConnect.getChangeRequests(status, orderBy);

  String newTable = getChangeRequestsTable(requests, status);

  String statusString = "Listing " + status + " change requests";
  if(status.equalsIgnoreCase("done"))
  {
    statusString = "Listing completed change requests";
  }

  int kount = requests.size();
%>

<h2><%= statusString %></h2>
<h4><%= kount %> found.</h4>
<%= updateMessage %>
<%= sortBuf.toString() %>

<%= newTable.toString() %>

</div>
