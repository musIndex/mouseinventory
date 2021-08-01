<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@page import="java.util.ArrayList" %>
<%=getPageHeader(null, false, true) %>
<%=getNavBar("ManageChangeRequests.jsp", true) %>
<%@ include file='SendMailForm.jspf' %>

<div class="site_container">
    <%
        int id = stringToInt(request.getParameter("id"));
        String confirmed = request.getParameter("confirm");

        if (id < 0) {
    %>
    <p class="main_header">No Change Request ID found</p>
    <%
    } else {

        ArrayList<ChangeRequest> reqs = DBConnect.getChangeRequest(id);
        String table = getChangeRequestsTable(reqs, null);
        if (reqs.size() != 1) {
    %>
    <p class="main_header">No change request with ID <%=id %> found.</p>
    <%
    } else if (confirmed != null && confirmed.equals("Delete Change Request")) {

        DBConnect.deleteChangeRequest(id);
    %>
    <p class="main_header">Successfully deleted Change Request #<%=id %>
    </p>
    <%@ include file='_lastManageRequestsLink.jspf' %>
    <%
    } else {
    %>
    <p class="main_header">Do you want to delete this Change Request?</p>
    <p class="label_text" style="font-size: 24px">Change Request:</p>
    <%=table %>
    <br>
    <form action="DeleteChangeRequest.jsp" method="post">
        <input type="hidden" name="id" value="<%=id %>">
        <p class="label_text">Are you sure that you want to delete the change request above?</p>
        <div class='editRecordButtonRed' style="width: 15%">
            <input type="submit" class="editRecordButtonInput" id ='confirm' name="confirm" value="Delete Change Request">
        </div>
    </form>
    <%
            }
        }
    %>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>
