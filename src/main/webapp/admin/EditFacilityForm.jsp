<%@page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>

<%
    int id = -1;
    ;
    Facility facility = null;
    ArrayList<Integer> facilityMouseIDs = null;
    boolean mayDelete = false;
    try {
        id = HTMLGeneration.stringToInt(request.getParameter("facilityID"));
        facility = DBConnect.getFacility(id);
        if (facility == null) throw new Exception("Facility not found");
        facilityMouseIDs = DBConnect.getMiceInFacility(id);
        mayDelete = facilityMouseIDs.size() <= 0;
    } catch (Exception e) {
%>
<div class="site_container">
    <p class="main_header">Facility not found</p>
</div>
<%
        return;
    }
%>
<div class="site_container">
    <p class="main_header">Edit Facility #<%=facility.getFacilityID() %>
    </p>
    <div class="category">
        <div class="two_column_left">
            <div class="formbody">
                <form action="UpdateFacility.jsp" method="post">
                    <table class="inputForm" style="width: 62%;">
                        <tr class="formFieldH">
                            <td class="formHeaderCell" colspan="2">Facility Information</td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">*Facility Name</td>
                            <td class="formRight">
                                <input type="text" name="facilityName" class='formInput' required
                                       value="<%= facility.getFacilityName() %>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">*Facility Description</td>
                            <td class="formRight">
                                <input type="text" name="facilityDescription" class='formInput' required
                                       value="<%= HTMLGeneration.emptyIfNull(facility.getFacilityDescription()) %>">
                            </td>

                        </tr>
                        <tr class="formField">
                            <td class="formLeft">
                                Local experts
                                <%--                                <br><i>email address first (if any),<br>one expert per line.</i>--%>
                            </td>
                            <td class="formRight">
                                <textarea name="localExperts" class="formInput"
                                          style="resize: none;margin-top: 13px;margin-bottom: 13px;width: 212.8px"
                                          rows="7"><%= HTMLGeneration.emptyIfNull(facility.getLocalExperts()) %></textarea>
                            </td>

                        </tr>
                        <tr class="formField">
                            <td class="formLeft">*Facility Code</td>
                            <td class="formRight">
                                <input type="text" name="facilityCode" class='formInput' required
                                       value="<%= HTMLGeneration.emptyIfNull(facility.getFacilityCode()) %>">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div class="spacing_div_minix2"></div>
                                <div class="MSU_green_button" style="width: 36%;float: right;margin-right: -3px;">
                                    <input type="hidden" name="facilityID" value="<%= id %>">
                                    <input type="hidden" name="command" value="Edit">
                                    <input type="submit" value="Save Changes"
                                           style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                </div>
                            </td>
                        </tr>
                    </table>
                </form>
            </div>
            <% if (mayDelete) { %>
            <form action="UpdateFacility.jsp" method="post">
                <div class="MSU_back_button"
                     style="width: 23%;float: right;float: left;margin-top: -72px;margin-left:0px;">
                    <input type="hidden" name="command" value="Delete">
                    <input type="hidden" name="facilityID" value="<%= id %>">
                    <input type="submit" value="Delete Holder"
                           style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                </div>
            </form>
            <%} else { %>
            <div class="MSU_back_button"
                 style="background-color:#008183ff;width: 23%;margin-left:0px;float: right;float: left;margin-top: -72px;">
                <a class='anchor_no_underline' href='EditMouseSelection.jsp?facility_id=<%= id %>'>
                    <p class="MSU_green_button_Text">
                        Edit this facility's records
                    </p>
                </a>
            </div>
            <%} %>
        </div>
    </div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>
