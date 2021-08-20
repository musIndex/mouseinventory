<%@page import="java.util.ArrayList" %>
<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>

<%
    int id = -1;
    ;
    Holder holder = null;
    ArrayList<Integer> holderMouseIDs = null;
    boolean mayDelete = false;
    try {
        id = HTMLGeneration.stringToInt(request.getParameter("holderID"));
        holder = DBConnect.getHolder(id);
        if (holder == null) throw new Exception("Holder not found");
        holderMouseIDs = DBConnect.getMiceWithHolder(id);
        mayDelete = holderMouseIDs.size() <= 0;
    } catch (Exception e) {
%>
<div class="site_container">
    <p class="main_header">Holder not found</p>
</div>
<%
        return;
    }
%>
<div class="site_container">
    <p class="main_header">Editing Holder #<%=holder.getHolderID() %>
    </p>
    <div class="category">
        <div class="two_column_left">
            <div class="formbody">
                <form action="UpdateHolder.jsp" method="post">
                    <table class="inputForm" style="width: 62%;">
                        <tr class="formFieldH">
                            <td class="formHeaderCell" colspan="2">Holder Information</td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">*First name</td>
                            <td class="formRight">
                                <input required type=text name="firstname"
                                       value="<%= holder.getFirstname() %>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">*Last name</td>
                            <td class="formRight">
                                <input required type=text name="lastname"
                                       value="<%= holder.getLastname() %>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">*Department</td>
                            <td class="formRight">
                                <input type=text name="dept" required value="<%= holder.getDept() %>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">*Email</td>
                            <td class="formRight">
                                <input type=email name="email" required
                                       value="<%= holder.getEmail() %>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft" style="line-height: 21px">*Primary Contact Name</td>
                            <td class="formRight">
                                <input type=text name="alternateName" required
                                       value="<%= HTMLGeneration.emptyIfNull(holder.getAlternateName()) %>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft" style="line-height: 21px">Primary Contact Email</td>
                            <td class="formRight">
                                <input type=email name="alternateEmail"
                                       value="<%= HTMLGeneration.emptyIfNull(holder.getAlternateEmail()) %>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft" style="line-height: 21px">*Primary Colony Location</td>
                            <td class="formRight">
                                <input type=text name="primaryMouseLocation" required
                                       value="<%= emptyIfNull(holder.getPrimaryMouseLocation()) %>"></td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">Last review date</td>
                            <td class="formRight">
                                <input type=text name="dateValidated"
                                       value="<%= HTMLGeneration.emptyIfNull(holder.getDateValidated())%>">
                            </td>
                        </tr>
                        <tr class="formField">
                            <td class="formLeft">Review status</td>
                            <td class="formRight">
                                <input type=text name="validationStatus" size=20
                                       value="<%= HTMLGeneration.emptyIfNull(holder.getValidationStatus())%>">
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div class="spacing_div_minix2"></div>
                                <div class="MSU_green_button" style="width: 36%;float: right;margin-right: -3px;">
                                    <input type="hidden" name="command" value="Edit">
                                    <input type="hidden" name="holderID" value="<%= id %>">
                                    <input type="submit" value="Save Changes"
                                           style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                </div>
                            </td>
                        </tr>
                    </table>
                </form>
                <%if (mayDelete) { %>
                <form action="UpdateHolder.jsp" method="post">
                    <div class="MSU_back_button"
                         style="width: 23%;float: right;float: left;margin-top: -72px;margin-left:0px;">
                        <input type="hidden" name="command" value="Delete">
                        <input type="hidden" name="holderID" value="<%= id %>">
                        <input type="submit" value="Delete Holder"
                               style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                    </div>
                </form>
                <%} else { %>
                <div class="MSU_back_button"
                     style="background-color:#008183ff;width: 23%;margin-left:0px;float: right;float: left;margin-top: -72px;">
                    <a class='anchor_no_underline' href='<%=adminRoot %>EditMouseSelection.jsp?holder_id=<%= id %>'>
                        <p class="MSU_green_button_Text">
                            Edit this holder's records (<%=holderMouseIDs.size() %>)
                        </p>
                    </a>

                </div>
                <%} %>
            </div>
        </div>
        <div class="two_column_right">
            <div class="sidebar_desc"
                 style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height: 225px">
                <p class="block_form_desc_text">When entering a</p>
                <p class="block_form_label_text"> primary contact name</p>
                <p class="block_form_desc_text">, please use either the holder themselves or the individual
                    appointed by the
                    holder to review and accept communications about the database.<br><br></p>
                <p class="block_form_desc_text">Similarly, the</p>
                <p class="block_form_label_text">primary contact email</p>
                <p class="block_form_desc_text">will be the recipient for email links for this holder in the mouse
                    list, with the holder's email address added as a cc.<br>
                    Please leave this field blank if the holder is the primary contact.<br><br>
                    Format for the </p>
                <p class="block_form_label_text"> last review date</p>
                <p class="block_form_desc_text"> is as follows: (mm-dd-yyyy).<br><br>
                    * Indicates required field.</p>
            </div>
            <%
                if (!mayDelete) {
            %>
            <div style="margin-left: -100px;">
                <p class="label_text" style="font-weight: bold">This Holder is linked to one or more mouse records and
                    cannot be deleted.</p>
            </div>
            <%
                }
            %>
        </div>
    </div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>