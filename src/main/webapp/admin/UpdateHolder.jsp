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
        buf.append("<p class=\"main_header\" style=\"line-height:36px\">No command received.<br>Expected 'Edit','Delete',or 'Insert'</p>");
    } else if (command.equals("Edit")) {
        if (id <= 0) {
            buf.append("<p class=\"main_header\">No holder ID received</p>");
        } else {
            buf.append("<p class=\"main_header\">Changes successful</p>");
            buf.append("<p class=\"label_text\" style=\"font-size:24px\">Before edit:</p> ");
            buf.append(originalHolderTable);

            DBConnect.updateHolder(editedHolder);

            holders.clear();
            holders.add(DBConnect.getHolder(id));
            buf.append("<p class=\"label_text\" style=\"font-size:24px\">After edit:</p> ");
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
                buf.append("<p class=\"main_header\">This holder is still liked to a record and cannot be deleted</p>");
            } else {
                DBConnect.deleteHolder(id);
                buf.append("<p class=\"main_header\">Holder " + originalHolder.getFullname() + " has been deleted</p>");
            }
        } else {
            buf.append("<p class=\"main_header\">Confirm that you want to delete this holder</p>\r\n");
            buf.append(originalHolderTable);
            buf.append("<div class=\"spacing_div_minix2\"></div>");
            buf.append("<form action=\"UpdateHolder.jsp\" method=\"post\">");
            buf.append("<div class=\"MSU_back_button\" style=\"width: 13%;float: right;float: left;margin-left:0px;\">");
            buf.append("    <input type=\"hidden\" name=\"holderID\" value=\"" + id + "\">");
            buf.append("    <input type=\"hidden\" name=\"confirm\" value=\"yes\">");
            buf.append("<input type=\"hidden\" name=\"command\" value=\"Delete\">");
            buf.append("<input type=\"submit\" value=\"Delete Holder\" style=\"width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;\">");
            buf.append("</div></form>");
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
    <div class="spacing_div_mini"></div>
    <div class="MSU_green_button"
         style="margin-bottom: 20px;margin-left: 0px;margin-right: 20px;width:13%;display: inline-block">
        <a class='anchor_no_underline' href='<%= link %>'>
            <p class="MSU_green_button_Text"><%= title %>
            </p>
        </a>
    </div>

    <div class="MSU_green_button"
         style="margin-bottom: 20px;margin-left: 0px;margin-right: 0px;background-color: #008183ff;width:12%;display: inline-block">
        <a class='anchor_no_underline' href='<%=homeLink%>'>
            <p class="MSU_green_button_Text"><%=homeTitle%>
            </p>
        </a>
    </div>
</div>

</div></div> <!-- This end div is here to end the site container div. For some reason it's not picked up by intellisense, but it is necessary. -->


<%=HTMLGeneration.getWebsiteFooter()%>
