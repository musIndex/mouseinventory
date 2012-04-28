<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page import="java.util.ArrayList"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditGeneChooser.jsp", true) %>
<jsp:useBean id="editedGene" class="edu.ucsf.mousedatabase.objects.Gene" scope="request"></jsp:useBean>
<jsp:setProperty property="*" name="editedGene"/>
<%
  String command = request.getParameter("command");
  StringBuffer buf = new StringBuffer();
    
  int id = HTMLGeneration.stringToInt(request.getParameter("geneRecordID"));

  Gene originalGene = new Gene();
  ArrayList<Gene> genes = new ArrayList<Gene>();
  String originalGeneTable = "";
  if (id >= 0)
  {
    try
    {
      originalGene = DBConnect.getGene(id);
      genes.add(originalGene);            
        originalGeneTable = HTMLGeneration.getGeneTable(genes,false);
    }
    catch (Exception e)
    {
      buf.append("<h2>Gene not found</h2></div>");
      %>
      <div class="pagecontent">
      <%= buf.toString() %>
      </div>
      <%
      return;
    }
  }
  if (command == null || command.isEmpty())
  {
    buf.append("<h2>No command received.  Expected 'Edit','Delete',or 'Insert'</h2>");
  }
  else if (command.equals("Edit"))
  {
    if (id <= 0)
    {
      buf.append("<h2>No gene ID received</h2>");
    }
    else
    {
        buf.append("<h2>Before edit:</h2> ");
        buf.append(originalGeneTable);
      
      DBConnect.updateGene(editedGene);
      
      genes.clear();
      genes.add(DBConnect.getGene(id));      
        buf.append("<h2>After edit:</h2> ");
        buf.append(HTMLGeneration.getGeneTable(genes,true));
    }
  }
  else if (command.equals("Insert"))
  {
    id = DBConnect.insertGene(editedGene);
    genes.clear();
    genes.add(DBConnect.getGene(id));      
      buf.append("<h2>Added new gene:</h2> ");
      buf.append(HTMLGeneration.getGeneTable(genes,true));
  }
  else if (command.equals("Delete"))
  {
    
    boolean confirmed = request.getParameter("confirm") != null && request.getParameter("confirm").equals("yes");
    if (confirmed)
    {
      ArrayList<Integer> geneMouseIDs = DBConnect.getMiceWithGene(id);
      if (id <=0)
      {
        buf.append("<h2>No gene ID received</h2>");
      }
      else if (geneMouseIDs != null && geneMouseIDs.size() > 0)
      {
        buf.append("<h2>This gene is still liked to a record and cannot be deleted!</h2>");
      }
      else
      {
        DBConnect.deleteGene(id);
        buf.append("<h2>Gene " +  originalGene.getSymbol()+ " has been deleted</h2>");
      }
    }
    else
    {      
        buf.append("<h2>Confirm that you want to delete this gene</h2>\r\n");
        buf.append(originalGeneTable);
      buf.append("<form action=\"UpdateGene.jsp\" method=\"post\">");
      buf.append("    <input type=\"hidden\" name=\"geneRecordID\" value=\"" + id + "\">");
      buf.append("    <input type=\"hidden\" name=\"confirm\" value=\"yes\">");
      buf.append("    Yes, I want to delete this gene: ");
      buf.append("    <input type=\"submit\" class='btn btn-danger' name=\"command\" value=\"Delete\">");
      buf.append("</form>");
      buf.append("<p>Else click your browser's BACK button.</p>");
    }
  }
  else
  {
    buf.append("<h2>Unrecognized command '" + command + "'.  Expected 'Edit','Delete',or 'Insert'</h2>");
  }
%>
<div class="pagecontent">
<%= buf.toString() %>
</div>