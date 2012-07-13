<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("MouseReport.jsp", false) %>

<%

  int holderID = HTMLGeneration.stringToInt(request.getParameter("holder_id"));
  int geneID = HTMLGeneration.stringToInt(request.getParameter("geneID"));
    int mouseTypeID = HTMLGeneration.stringToInt(request.getParameter("mousetype_id"));
    String orderBy = request.getParameter("orderby");
    int pagenum = HTMLGeneration.stringToInt(request.getParameter("pagenum"));
    int limit = HTMLGeneration.stringToInt(request.getParameter("limit"));
    if (limit == -1)
    {
      limit = 100;
    }

    if (pagenum == -1)
    {
      pagenum = 1;
    }
    int offset = limit * (pagenum - 1);
    int mouseCount = DBConnect.countMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null,true,-1,-1);
  ArrayList<MouseRecord> mice = DBConnect.getMouseRecords(mouseTypeID, orderBy, holderID, geneID, "live", null, true);
    String table = HTMLGeneration.getMouseTable(mice, false, true, false);
    ArrayList<MouseType> mouseTypes = DBConnect.getMouseTypes();
    String mouseTypeSelectionLinks = HTMLGeneration.getMouseTypeSelectionLinks(mouseTypeID, orderBy,holderID,geneID,mouseTypes,null,null,-1,-1);
    Holder holder = DBConnect.getHolder(holderID);

    Gene gene = DBConnect.getGene(geneID);

    String mouseTypeStr = "Endangered Mice: Listing";
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
      mouseCountStr = mice.size() + " found.";
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


%>
<div class="site_container">

    <h2><%=mouseTypeStr %></h2>
    <h4><%=mouseCountStr %></h4>
    <p style="color: red">Mice are listed here because the holder(s) is considering no longer maintaining it. If you are interested in obtaining the mouse before it is no longer maintained at UCSF, please contact the holder(s).</p>
    <p><a href="MouseReport.jsp">Back to list all mice</a></p>

<%= table %>
</div>
