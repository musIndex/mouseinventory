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
  String requestSource = request.getParameter("requestSource");
  int currentHolderId = -1;

  if(status == null) {
    if ((status = (String)session.getAttribute("manageChangeRequestStatus")) == null) {
      status = "new";
    }
  }
  session.setAttribute("manageChangeRequestStatus",status);
  if (orderBy == null) {
    if ((orderBy = (String)session.getAttribute("manageChangeRequestOrderBy")) == null) {
      orderBy = "changerequest.id";
    }
  }
  else {
    session.setAttribute("manageChangeRequestOrderBy",orderBy);
  }
  if (requestSource == null) {
   if ((requestSource = (String)session.getAttribute("manageChangeRquestRequestSource")) == null) {
	   requestSource = "all";
   }
  }
  else {
   session.setAttribute("manageChangeRequestRequestSource",requestSource); 
  }
  
  if (request.getParameter("holder_id") == null && session.getAttribute("manageChangeRequestHolderId") != null) {
      currentHolderId = (Integer)session.getAttribute("manageChangeRequestHolderId");
  }
  else {
   currentHolderId = stringToInt(request.getParameter("holder_id")); 
  }
  session.setAttribute("manageChangeRequestHolderId", currentHolderId);
  
  String[] sortOptions = new String[] {"changerequest.id","requestdate","requestdate DESC", "mouse_id","mouse_id DESC","firstname","lastname"};
  String[] sortOptionNiceNames = new String[] {"Request #","Request date", "Reverse request date","Record #", "Reverse Record #","Requestor first name", "Requestor last name"};

  String[] filterOptions = new String[] {"new","pending","done","all"};
  String[] filterOptionNiceNames = new String[] {"New", "Pending", "Completed","All"};
  

  StringBuffer sortBuf = new StringBuffer();
  sortBuf.append("<form class='view_opts' action='ManageChangeRequests.jsp'>");
  sortBuf.append("&nbsp;Show: ");
  sortBuf.append(genSelect("status",filterOptions,filterOptionNiceNames, status,null));
  sortBuf.append("&nbsp;Source: ");
  sortBuf.append("<input name='requestSource' style='width: 200px' type='text' value='" + (requestSource.equals("all") ? "" : requestSource) + "'>");
  sortBuf.append("&nbsp;<input class='btn' type='submit' value='Update'>");
  sortBuf.append("&nbsp;<a id='clearSource' class='btn'>Clear</a>");
  //sortBuf.append("&nbsp;Filter by holder: ");
  //sortBuf.append(getHolderSelect("holder_id", currentHolderId, false));
  sortBuf.append("&nbsp;Sort by: ");
  sortBuf.append(genSelect("orderby",sortOptions,sortOptionNiceNames, orderBy,null));
  sortBuf.append("</form>");

  ArrayList<ChangeRequest> requests = DBConnect.getChangeRequests(status, orderBy, requestSource);
  Holder currentHolder = DBConnect.getHolder(currentHolderId);
  if (currentHolder != null) {
    ArrayList<ChangeRequest> temp = new ArrayList<ChangeRequest>();
    for (ChangeRequest req : requests) {
      if (req.getHolderEmail() != null && req.getHolderEmail().equalsIgnoreCase(currentHolder.getEmail())) {
       temp.add(req); 
      }
    }
    requests = temp;
  }

  String newTable = getChangeRequestsTable(requests, status);

  String statusString = status + " change requests";
  if (status.equalsIgnoreCase("done")) {
    statusString = " completed change requests";
  }
  if (!requestSource.equalsIgnoreCase("all") && !requestSource.isEmpty()){
    statusString += " from source " + requestSource; 
  }
  if (currentHolderId > 0) {
    IHolder holder = DBConnect.getHolder(currentHolderId);
    statusString += " for holder " + holder.getFullname();
  }
 

  session.setAttribute("manageRequestsLastQuery","status=" + status + "&orderby=" + orderBy + "&requestSource=" + requestSource);
  session.setAttribute("manageRequestsLastTitle",statusString);
  statusString = "Listing " + statusString;
  
  int kount = requests.size();
%>



<h2><%= statusString %></h2>
<h4><%= kount %> found.<span id='matching_search'></span></h4>
<%= updateMessage %>
<%= sortBuf.toString() %>
<form id='search_form'>
Quick search: <input type='text'></input> <a class='btn clear_btn' style='display:none'>Clear</a>
</form>

<%= newTable.toString() %>

</div>
<script>


!function($){
  
  $('#clearSource').click(function(){
    $("input[name=requestSource]").val('');
    return false;
  });
  
  var search_form = $("form#search_form");
  var clear_btn = $("form#search_form a.clear_btn");
  var search_input = $("form#search_form input[type=text]");
  var matching_label = $("#matching_search");
  search_form.submit(function(){
    var term = search_input.val();
    var expr = new RegExp(term,'i');
    var matchCount = 0;
    $("div.mouseTable tr").removeClass('hide');
    if (!term) {
      clear_btn.hide();
      matching_label.text("");
      return false;
    }
    clear_btn.show();
    $("div.mouseTable tr.changerequestlist, div.mouseTable tr.changerequestlistAlt").each(function(i,row){
      var $row = $(row);
      if (!$row.text().match(expr)) {
        $row.addClass('hide');
      }
      else {
        matchCount++;
      }
    });
    matching_label.text(" (" + matchCount + " matching quick search)");
    
    return false;
  });
  clear_btn.click(function(){
    search_input.val('');
    search_form.submit();
  });
}(jQuery);

</script>
