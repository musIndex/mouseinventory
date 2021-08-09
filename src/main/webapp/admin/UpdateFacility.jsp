<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>
<jsp:useBean id="editedFacility" class="edu.ucsf.mousedatabase.objects.Facility" scope="request"></jsp:useBean>
<jsp:setProperty property="*" name="editedFacility"/>

<%
    HTMLUtilities.logRequest(request);
    String command = request.getParameter("command");
    StringBuffer buf = new StringBuffer();

    int id = HTMLGeneration.stringToInt(request.getParameter("facilityID"));
    Facility originalFacility = new Facility();
    ArrayList<Facility> facilities = new ArrayList<Facility>();
    String originalFacilityTable = "";
    if (id >= 0) {
        try {
            originalFacility = DBConnect.getFacility(id);
            facilities.add(originalFacility);
            originalFacilityTable = HTMLGeneration.getFacilityTable(facilities, true);
        } catch (Exception e) {
%>
<div class="site_container">
    <p class="main_header">Facility not found</p>
</div>
<%
            return;
        }
    }
    if (command == null || command.isEmpty()) {
        buf.append("<p class=\"main_header\" style=\"line-height:36px\">No command received.<br>Expected 'Edit','Delete',or 'Insert'</p>");
    } else if (command.equals("Edit")) {
        if (id <= 0) {
            buf.append("<p class=\"main_header\">No facility ID received</p>");
        } else {
            buf.append("<p class=\"main_header\">Changes successful</p>");
            buf.append("<p class=\"label_text\" style=\"font-size:24px\">Before edit:</p> ");
            buf.append(originalFacilityTable);

            DBConnect.updateFacility(editedFacility);

            facilities.clear();
            facilities.add(DBConnect.getFacility(id));
            buf.append("<p class=\"label_text\" style=\"font-size:24px\">After edit:</p> ");
            buf.append(HTMLGeneration.getFacilityTable(facilities, true));
        }
    } else if (command.equals("Insert")) {
        id = DBConnect.insertFacility(editedFacility);
        facilities.clear();
        facilities.add(DBConnect.getFacility(id));
        buf.append("<p class=\"main_header\">Added new facility:</p> ");
        buf.append(HTMLGeneration.getFacilityTable(facilities, true));
    } else if (command.equals("Delete")) {

        boolean confirmed = request.getParameter("confirm") != null && request.getParameter("confirm").equals("yes");
        if (confirmed) {
            ArrayList<Integer> facilityMouseIDs = DBConnect.getMiceInFacility(id);
            if (id <= 0) {
                buf.append("<p class=\"main_header\">No facility ID received</p>");
            } else if (facilityMouseIDs != null && facilityMouseIDs.size() > 0) {
                buf.append("<p class=\"main_header\">This facility is still liked to a record and cannot be deleted</p>");
            } else {
                DBConnect.deleteFacility(id);
                buf.append("<p class=\"main_header\">Facility \"" + originalFacility.getFacilityName() + "\" has been deleted</p>");
            }
        } else {
            buf.append("<p class=\"main_header\">Confirm that you want to delete this facility</p>\r\n");
            buf.append(originalFacilityTable);
            buf.append("<div class=\"spacing_div_minix2\"></div>");
            buf.append("<form action=\"UpdateFacility.jsp\" method=\"post\">");
            buf.append("<div class=\"MSU_back_button\" style=\"width: 13%;float: right;float: left;margin-left:0px;\">");
            buf.append("    <input type=\"hidden\" name=\"facilityID\" value=\"" + id + "\">");
            buf.append("    <input type=\"hidden\" name=\"confirm\" value=\"yes\">");
            buf.append("<input type=\"hidden\" name=\"command\" value=\"Delete\">");
            buf.append("<input type=\"submit\" value=\"Delete Facility\" style=\"width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;\">");
            buf.append("</div></form>");
        }
    } else {
        buf.append("<p class=\"main_header\">Unrecognized command '" + command + "'.  Expected 'Edit','Delete',or 'Insert'</p>");
    }
%>
<div class="site_container">
    <%= buf.toString() %>

    <%
        String link = "EditFacilityChooser.jsp";
        String homeLink = "admin.jsp";
        String title = "Back to facilities";
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
</div>

<%=HTMLGeneration.getWebsiteFooter()%>
