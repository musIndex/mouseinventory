<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%@include file="../mouselistcommon.jspf" %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<%@include file="SendMailForm.jspf" %>
<script type="text/javascript" src="<%=scriptRoot%>jquery.highlight.js" ></script>

<%

  int holderID = HTMLGeneration.stringToInt(request.getParameter("holder_id"));
  int geneID = HTMLGeneration.stringToInt(request.getParameter("geneID"));
  int mouseTypeID = HTMLGeneration.stringToInt(request.getParameter("mousetype_id"));

  String searchTerms = request.getParameter("searchterms");
  String orderBy = request.getParameter("orderby");
  String status = request.getParameter("status");

  int pagenum = HTMLGeneration.stringToInt(request.getParameter("pagenum"));
    int limit = HTMLGeneration.stringToInt(request.getParameter("limit"));
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
  if(status == null)
  {
    if ((status = (String)session.getAttribute("editMiceStatus")) == null)
    {
      status = "all";
    }
  }
  session.setAttribute("editMiceStatus",status);
  if(orderBy == null)
  {
    orderBy = (String)session.getAttribute("editMiceOrderBy");
  }
  else
  {
    session.setAttribute("editMiceOrderBy",orderBy);
  }

  ArrayList<String> query = new ArrayList<String>();
    query.add("holder_id=" + holderID);
    query.add("orderby=" + orderBy);
    query.add("geneID=" + geneID);
    //query.add("creonly=" + creOnly);
    query.add("mousetype_id=" + mouseTypeID);
    //query.add("facility_id=" + facilityID);
    query.add("searchterms=" + searchTerms);
    query.add("status=" + status);

    String queryString = "";

    for (String s : query)
    {
        queryString += s + "&";
    }

  int mouseCount = DBConnect.countMouseRecords(mouseTypeID, orderBy, holderID, geneID, status, searchTerms, false, -1, -1);

  ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(mouseTypeID, orderBy, holderID, geneID, status, searchTerms,false,-1,-1,limit,offset);

  String table = HTMLGeneration.getMouseTable(mice, true, false, false);

  //String table = generateMouseList(mouseTypeID, null, (orderBy== null || orderBy.equals("mouse.id")) ? "mouse.id desc" : orderBy, true);

  ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();

    String mouseTypeSelectionLinks = HTMLGeneration.getMouseTypeSelectionLinks(mouseTypeID, orderBy,holderID,geneID, mouseTypes, status,searchTerms,-1,-1);
    String topPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit,pagenum,mouseCount,true);
    String bottomPageSelectionLinks = HTMLGeneration.getNewPageSelectionLinks(limit,pagenum,mouseCount,false);


    Holder holder = DBConnect.getHolder(holderID);

    Gene gene = DBConnect.getGene(geneID);

    String mouseTypeStr = "Edit: Listing";
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
      mouseCountStr = mouseCount + " found (" + mice.size() + " shown)";
    }


    mouseTypeStr += " records";

    if (holder != null)
    {
    mouseTypeStr += " held by " + holder.getFullname();
   }
    else if(gene != null)
    {
    mouseTypeStr += " with gene <span class=\"geneSymbol\">" + gene.getSymbol() + "</span> - <span class=\"geneName\">" + gene.getFullname() + "</span>";
  }

    if(searchTerms != null && !searchTerms.isEmpty())
    {
      mouseTypeStr += " matching search term '" + searchTerms + "'";
    }

    if(!status.equals("all"))
  {
    mouseTypeStr += " with status='" + status + "'";
  }

%>
<div class="site_container">

    <h2><%=mouseTypeStr %></h2>
    <h4><%=mouseCountStr %></h4>
    <a href="CovertMice.jsp">Covert Mice</a>
    

  <form class='view_opts' action="EditMouseSelection.jsp">
    <div style='position:relative'>
    <%= mouseTypeSelectionLinks %>
    <%= topPageSelectionLinks %>
    <div style='position:absolute;bottom:0;right:0;'>
    <a class="btn" style="text-decoration:none" href="<%= siteRoot %>MouseList<%= (queryString.length() > 0 ? "?" + queryString : "") %> ">Download this list (pdf)</a>
    </div>
    </div>
    <br>
    <%= table %>
    <%= bottomPageSelectionLinks %>
  </form>
</div>

<script type='text/javascript'>
function highlight_searchterms(searchterms){
  $('.mouseTable').each(function(){
    var $results = $(this);
    $results.find(".mouselist, .mouselistAlt").highlight(searchterms.split(' '),{className: 'highlight-searchterm'});
    $results.find(".lbl").unhighlight({className: 'highlight-searchterm'});
  });

  $("span.highlight-searchterm").parent().parent().each(function(){
    var $element = $(this);
    if($element.is("dt")) {
      if($element.parent().hasClass("mouselist-holderlist")){
        $element.show();
      }
    }
  });
}

var searchterms = $("#mousetypeselection_searchterms").val();

if (searchterms) {
  highlight_searchterms(searchterms);
}
</script>
