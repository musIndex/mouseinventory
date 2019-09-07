
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%@include file="../mouselistcommon.jspf" %>
<%=HTMLGeneration.getNavBar("EditMouseSelection.jsp", true) %>
<script type="text/javascript" src="<%=HTMLGeneration.scriptRoot%>jquery.highlight.js" ></script>
<%

  String orderBy = request.getParameter("orderby");
  String status = request.getParameter("status");
  String searchTerms = request.getParameter("searchterms");
  int mouseTypeID = HTMLGeneration.stringToInt(request.getParameter("mousetype_id"));

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


  ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();
  String mouseTypeSelectionLinks = HTMLGeneration.getMouseTypeSelectionLinks(mouseTypeID, orderBy,-1,-1, mouseTypes, status,searchTerms,-1,-1);
  ArrayList<MouseRecord> mice = DBConnect.getCovertMice(orderBy,status,searchTerms,mouseTypeID);

  String table = HTMLGeneration.getMouseTable(mice, true, false, false);

  //String table = generateMouseList(mouseTypeID, null, (orderBy== null || orderBy.equals("mouse.id")) ? "mouse.id desc" : orderBy, true);

  String mouseTypeStr = "Edit: Listing";
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

    mouseTypeStr += " records (with a covert holder)";

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
    <h4><%=mice.size() %> records found.</h4>
    <form class='view_opts' action="CovertMice.jsp" >
     <%=mouseTypeSelectionLinks %>
  </form>
    <%= table %>

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