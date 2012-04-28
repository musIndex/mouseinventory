<%@page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.*"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>

<%
  int id = -1;;
  Holder holder = null;
  ArrayList<Integer> holderMouseIDs = null;
  boolean mayDelete = false;
  try
  {
      id = HTMLGeneration.stringToInt(request.getParameter("holderID"));
    holder = DBConnect.getHolder(id);
    if (holder == null) throw new Exception("Holder not found");
      holderMouseIDs = DBConnect.getMiceWithHolder(id);
       mayDelete = holderMouseIDs.size() <= 0;
  }
  catch (Exception e)
  {
    %>
    <div class="pagecontent">
    <h2>Holder not found</h2>
    </div>
    <%
    return;
  }
%>
<div class="pagecontent">
<h2>Edit Holder #<%=holder.getHolderID() %></h2>

<form action="UpdateHolder.jsp" method="post">
    <table>
        <tr>
            <td>First name</td>
            <td><input type=text name="firstname" size=20 value="<%= holder.getFirstname() %>"></td>
        </tr>
        <tr>
            <td>Last name</td>
            <td><input type=text name="lastname" size=20 value="<%= holder.getLastname() %>"></td>
        </tr>
        <tr>
            <td>Department</td>
            <td><input type=text name="dept" size=40 value="<%= holder.getDept() %>"></td>
        </tr>
        <tr>
            <td>Email</td>
            <td><input type=text name="email" size=40 value="<%= holder.getEmail() %>"></td>
        </tr>
        <tr>
            <td>Alternate Email
            </td>
            <td><input type=text name="alternateEmail" size=40 value="<%= HTMLGeneration.emptyIfNull(holder.getAlternateEmail()) %>">
            <br><i>This email will be added as a cc on email links for this holder in the mouse list</i></td>
        </tr>
        <tr>
            <td>Tel</td>
            <td><input type=text name="tel" size=20 value="<%= holder.getTel() %>"></td>
        </tr>
    <tr>
            <td>Last review date: (format: yyyy-mm-dd)</td>
            <td><input type=text name="dateValidated" size=20
            value="<%= HTMLGeneration.emptyIfNull(holder.getDateValidated())%>"></td>
        </tr>

        <tr>
            <td colspan=2 align="center">
            <input type="hidden" name="command" value="Edit">
            <input type="hidden" name="holderID" value="<%= id %>">
            <input type="submit" class="btn btn-primary" value="Submit Changes"></td>
        </tr>
    </table>
</form>
<br>
<%if(mayDelete) { %>
<form action="UpdateHolder.jsp" method="post">
    <input type="hidden" name="holderID" value="<%= id %>">
    Delete this holder?
    <input type="submit" class="btn btn-danger" name="command" value="Delete">
</form>
<%}else{ %>
  This Holder is linked to one or more mouse records and cannot be deleted:
  <dl>
  <%
  int max = 20;
  int i = 0;
  for(Integer mouseID : holderMouseIDs)
   {%>
    <dd><a href="EditMouseForm.jsp?id=<%=mouseID %>">Edit record #<%=mouseID %></a></dd>
  <%
    i++;
    if (i >= max)
    {
      %>
      <dd>(<%=holderMouseIDs.size() - max %> others)</dd>
      <%
      break;
    }
   } %>
  </dl>
<%} %>
</div>
