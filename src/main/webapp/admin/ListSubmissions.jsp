<%@ page import="java.util.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="javax.swing.text.html.HTML" %>
<%=getPageHeader(null,false,true) %>
<%=getNavBar("ListSubmissions.jsp", true) %>
<%

  String orderBy = request.getParameter("orderby");
  String entered = request.getParameter("entered");
  String status = request.getParameter("status");
  String submissionSource = request.getParameter("submissionSource");
  int pagenum = HTMLGeneration.stringToInt(request.getParameter("pagenum"));
  int limit = HTMLGeneration.stringToInt(request.getParameter("limit"));
  if (limit == -1) {
    limit = 25;
  }
  if (pagenum == -1) {
    pagenum = 1;
  }
  int offset = limit * (pagenum - 1);

  if (orderBy == null) {
    orderBy = "submittedmouse.id";
  }

  if (entered!= null && entered.equalsIgnoreCase("null")) {
    entered = null;
  }


    /******/
  if (status == null) {
    if ((status = (String)session.getAttribute("listSubmissionStatus")) == null) {
      status = "new";
    }
  }
  if (submissionSource == null) {
   if ((submissionSource = (String)session.getAttribute("listSubmissionSubmissionSource")) == null) {
	   submissionSource = "all";
   }
  }
  else {
   session.setAttribute("listSubmissionSubmissionSource",submissionSource); 
  }
    
  if (status.equalsIgnoreCase("need")) {
      status = "need more info";
  }
  session.setAttribute("listSubmissionStatus",status);
  session.setAttribute("listSubmissionOrderBy",orderBy);

  int submissionCount = DBConnect.countMouseSubmissions(status, entered, orderBy, submissionSource);

  ArrayList<SubmittedMouse> submissions = DBConnect.getMouseSubmissions(status, entered, orderBy, submissionSource, limit, offset);

  String topPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit,pagenum,submissionCount,true);
  String bottomPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit,pagenum,submissionCount,true);

  String[] sortOptions = new String[] {"submittedmouse.id","date","date DESC","mouse.id","mouse.id DESC", "firstname","lastname"};
  String[] sortOptionNiceNames = new String[] {"Submission #", "Submission date","Reverse Submission date", "Record #", "Reverse Record #","Submitter first name", "Submitter last name"};

  String[] filterOptions = new String[] {"new","need more info","rejected","accepted","all"};
  String[] filterOptionNiceNames = new String[] {"New", "Hold", "Rejected","Converted to records","All"};



  String table = getSubmissionTable(submissions, status, entered);

  StringBuffer sortBuf = new StringBuffer();
  sortBuf.append("<form class='view_opts' action='ListSubmissions.jsp' method='get'>");
  sortBuf.append("<div class=\"mousetype_selection_links\" style=\"width:25%;padding-bottom:10px\"><ul class=\"label_text\" style=\"columns:1;font-size:16px\">");
  sortBuf.append("<li style=\"margin-top:0px\">Show:");
  sortBuf.append(genSelect("status",filterOptions,filterOptionNiceNames, status,"",true));
  sortBuf.append("</li><li style=\"margin: 6px 0;\">Sort by:");
  sortBuf.append(genSelect("orderby",sortOptions,sortOptionNiceNames, orderBy,"",true));
  sortBuf.append("</li><input type='hidden' name='entered' value='" + entered +"'>");
  sortBuf.append("</ul></div>");
  
  


  String statusString = status + " submissions";
  if(status.startsWith("need")) {
    statusString = "submissions on hold";
    status="need";
  }
  else if(status.startsWith("accepted"))
  {
    statusString = "submissions that have been converted to records";
  }
  if (!submissionSource.isEmpty() && !submissionSource.equals("all")) {
   statusString += " from source '" + submissionSource + "'"; 
  }
  session.setAttribute("listSubmissionsLastQuery","status=" + status + "&orderby=" + orderBy + "&submissionSource=" + submissionSource);
  session.setAttribute("listSubmissionsLastTitle",statusString);
  statusString = "Listing " + statusString;
%>

<div class="site_container">
<p class="main_header" style="margin-block-end: 0.25em;margin-block-start: 0.5em;"><%= statusString %></p>
<p class="label_text"><%= submissionCount %> found.<span id='matching_search'></span></p>
<form id='search_form' style="float: right">
  <input type="search" placeholder="Search..." style="font-size:120%;vertical-align:top;margin-top: 0px" class="input-xlarge" name="search_terms" id="search_terms">
  <input onclick="document.getElementById('search_form').submit()" type="image" alt="Submit" src="/img/Eyeglass-black.svg" style="height: 28px;margin: 0px">
  <%--Quick search (on page): <input type='text'></input>--%>
<%--  <a class='btn clear_btn' style='display:none'>Clear</a>--%>
</form>
<%= sortBuf.toString()%>
<%--<%= topPageSelectionLinks %>--%>
<%= table%>
<%= bottomPageSelectionLinks %>
</div>
<%=HTMLGeneration.getWebsiteFooter()%>


<script>
!function($){
  
  $('#clearSource').click(function(){
    $("input[name=requestSource]").val('');
    return false;
  });
  
  var search_form = $("form#search_form");
  var clear_btn = $("form#search_form a.clear_btn");
  var search_input = $("form#search_form input[type=search]");
  var matching_label = $("#matching_search");
  search_form.submit(function(e){
    e.preventDefault();
    var term = search_input.val();
    var expr = new RegExp("(^|\\s|-|\\()" + term.trim() + "(\\s|$|\\.|\\?|,|-|\\))",'i');
    var matchCount = 0;
    $("div.mouseTable tr").removeClass('hide');
    if (!term) {
      clear_btn.hide();
      matching_label.text("");
      return false;
    }
    clear_btn.show();
    $("div.mouseTable tr.submissionlist, div.mouseTable tr.submissionlistAlt").each(function(i,row){
      var $row = $(row);
      if (!$row.text().match(expr)) {
        $row.addClass('hide');
      }
      else {
        matchCount++;
      }
    });
    matching_label.text(" (" + matchCount + " matching quick search '" + term + "')");
    
    return false;
  });
  clear_btn.click(function(){
    search_input.val('');
    search_form.submit();
  });
}(jQuery);


function pageSwitch(num){
  document.getElementById("pagenum").value = num;
  this.form.submit();
}

</script>
