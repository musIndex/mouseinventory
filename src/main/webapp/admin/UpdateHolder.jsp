<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>
<%@include file='SendMailForm.jspf' %>
<jsp:useBean id="editedHolder" class="edu.ucsf.mousedatabase.objects.Holder" scope="request"></jsp:useBean>
<jsp:setProperty property="*" name="editedHolder"/>
<%
    HTMLUtilities.logRequest(request);
    String command = request.getParameter("command");
    StringBuffer buf = new StringBuffer();
    editedHolder.setDeadbeat(request.getParameter("isDeadbeat") != null && request.getParameter("isDeadbeat").equals("on"));
    int id = HTMLGeneration.stringToInt(request.getParameter("holderID"));
    Holder originalHolder = new Holder();
    ArrayList<Holder> holders = new ArrayList<Holder>();
    String originalHolderTable = "";
    if (id >= 0) {
        try {
            originalHolder = DBConnect.getHolder(id);
            holders.add(originalHolder);
            originalHolderTable = HTMLGeneration.getHolderTable(holders, true);
        } catch (Exception e) {
%>
<div class="site_container">
    <p class="main_header">Holder not found</p>
</div>
<%
            return;
        }
    }
    if (command == null || command.isEmpty()) {
        buf.append("<p class=\"main_header\">No command received.<br>Expected 'Edit','Delete',or 'Insert'</p>");
    } else if (command.equals("Edit")) {
        if (id <= 0) {
            buf.append("<p class=\"main_header\">No holder ID received</p>");
        } else {
            buf.append("<h2>Before edit:</h2> ");
            buf.append(originalHolderTable);

            DBConnect.updateHolder(editedHolder);

            holders.clear();
            holders.add(DBConnect.getHolder(id));
            buf.append("<h2>After edit:</h2> ");
            buf.append(HTMLGeneration.getHolderTable(holders, true));
        }
    } else if (command.equals("Insert")) {
        id = DBConnect.insertHolder(editedHolder);
        holders.clear();
        holders.add(DBConnect.getHolder(id));
        buf.append("<p class=\"main_header\">Added new holder:</p> ");
        buf.append(HTMLGeneration.getHolderTable(holders, true));
    } else if (command.equals("Delete")) {

        boolean confirmed = request.getParameter("confirm") != null &&
                request.getParameter("confirm").equals("yes");
        if (confirmed) {
            ArrayList<Integer> holderMouseIDs = DBConnect.getMiceWithHolder(id);
            if (id <= 0) {
                buf.append("<p class=\"main_header\">No holder ID received</p>");
            } else if (holderMouseIDs != null && holderMouseIDs.size() > 0) {
                buf.append("<h2>This holder is still liked to a record and cannot be deleted!</h2>");
            } else {
                DBConnect.deleteHolder(id);
                buf.append("<h2>Holder " + originalHolder.getFullname() + " has been deleted</h2>");
            }
        } else {
            buf.append("<h2>Confirm that you want to delete this holder</h2>\r\n");
            buf.append(originalHolderTable);
            buf.append("<form action=\"UpdateHolder.jsp\" method=\"post\">");
            buf.append("    <input type=\"hidden\" name=\"holderID\" value=\"" + id + "\">");
            buf.append("    <input type=\"hidden\" name=\"confirm\" value=\"yes\">");
            buf.append("    Yes, I want to delete this holder: ");
            buf.append("    <input type=\"submit\" class='btn btn-danger' name=\"command\" value=\"Delete\">");
            buf.append("</form>");
            buf.append("<p>Else click your browser's BACK button.</p>");
        }
    } else {
        buf.append("<p class=\"main_header\">Unrecognized command '" + command + "'.  Expected 'Edit','Delete',or 'Insert'</p>");
    }
%>
<div class="site_container">
    <%= buf.toString() %>

    <%
        String link = adminRoot + "EditHolderChooser.jsp";
        String homeLink = adminRoot + "admin.jsp";
        String title = "Back to holders";
        String homeTitle = "Admin Home";
    %>
    <div class="spacing_div_minix2"></div>
    <div class="MSU_green_button" style="margin-bottom: 20px;margin-left: 0px;margin-right: 20px;width:13%;display: inline-block">
        <a class='anchor_no_underline' href='<%= link %>'>
            <p class="MSU_green_button_Text"><%= title %>
            </p>
        </a>
    </div>

    <div class="MSU_green_button" style="margin-bottom: 20px;margin-left: 0px;margin-right: 0px;background-color: #008183ff;width:12%;display: inline-block">
        <a class='anchor_no_underline' href='<%=homeLink%>'>
            <p class="MSU_green_button_Text"><%=homeTitle%>
            </p>
        </a>
    </div>
</div>

</div> <!-- This end div is here to end the site container div. For some reason it's not picked up by intellisense, but it is necessary. -->


<%=HTMLGeneration.getWebsiteFooter()%>
