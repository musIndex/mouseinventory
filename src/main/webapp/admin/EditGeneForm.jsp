<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditGeneChooser.jsp", true) %>

<%
  int id = -1;;
  Gene gene = null;
  ArrayList<Integer> geneMouseIDs = null;
  boolean mayDelete = false;   
  
  try
  {
    id = HTMLGeneration.stringToInt(request.getParameter("geneRecordID")); 
    gene = DBConnect.getGene(id);
    if (gene == null) throw new Exception("Gene not found");
    geneMouseIDs = DBConnect.getMiceWithGene(id);
    mayDelete = geneMouseIDs.size() <= 0;   
  }
  catch (Exception e)
  {
    %>
    <div class="pagecontent">
    <h2>Gene not found</h2>
    </div>
    <%
    return;
  }
  
%>
<div class="pagecontent">
<h2>Edit Gene</h2>

<form action="UpdateGene.jsp" method="post">
    <table>
        <tr>
            <td>Full name</td>
            <td><input type=text name="fullname" size=80 value="<%= gene.getFullname() %>"></td>
        </tr>
        <tr>
            <td>Symbol</td>
            <td><input type=text name="symbol" size=20 value="<%= gene.getSymbol() %>"></td>
        </tr>
        <tr>
            <td>MGI</td>
            <td><input type=text name="mgiID" size=10 value="<%= gene.getMgiID() %>"></td>
        </tr>
        <tr>
             <td colspan="2">
             <input type="hidden" name="geneRecordID" value="<%= id %>">
             <input type="hidden" name="command" value="Edit">
             <input type="submit" class="btn btn-primary" value="Save Changes"></td>
        </tr>
    </table>
</form>
<br>
<% if (mayDelete) { %>
<form action="UpdateGene.jsp" method="post">
    <input type="hidden" name="geneRecordID" value="<%= id %>">
    Delete this gene? 
    <input type="submit" class="btn btn-danger" name="command" value="Delete">
</form>
<%}else{ %>
  This Gene is linked to one or more mouse records and cannot be deleted:
  <dl>
  <%
  int max = 20;
  int i = 0;
  for(Integer mouseID : geneMouseIDs)
   {%>
    <dd><a href="EditMouseForm.jsp?id=<%=mouseID %>">Edit record #<%=mouseID %></a></dd>
  <%
    i++;
    if (i >= max)
    {
      %>
      <dd>(<%=geneMouseIDs.size() - max %> others)</dd>
      <%
      break;
    }
   } %>
  </dl>
<%} %>
</div>
