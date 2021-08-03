<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>
<%@include file='SendMailForm.jspf' %>
<%

    //todo Order by links
%>
<%
    String command = request.getParameter("command");
    String orderby = request.getParameter("orderby");

    String[] orderOpts = new String[]{"firstname,lastname", "lastname,firstname", "department", "count", "datevalidated", "datevalidated desc", "primary_mouse_location"};
    String[] orderOptLabels = new String[]{"First name", "Last name", "Department", "Mice held", "Last review date", "Reverse last review date", "Primary location of Colony"};

    if (command == null || command.isEmpty() || command.equals("edit")) {
        ArrayList<Holder> holders = DBConnect.getAllHolders(false, orderby);
        String table = HTMLGeneration.getHolderTable(holders, true);
%>
<div class="site_container">
    <p class="main_header">Edit Holders:</p>

    <div class="MSU_green_button" style="width: 11%;margin-left: 0;margin-right: 0;float: right;margin-top: -50px">
        <a class="anchor_no_underline" href="EditHolderChooser.jsp?command=add">
            <p class="MSU_green_button_Text">Add Holder</p>
        </a>

    </div>
    <br>
    <form id='search_form' style="float: right">
        <input type="search" placeholder="Search..." style="font-size:120%;vertical-align:top;margin-top: 0px"
               class="input-xlarge" name="search_terms" id="search_terms">
        <input onclick="this.form.submit()" type="image" alt="Submit" src="/img/Eyeglass-black.svg"
               style="height: 28px;margin: 0px">
    </form>
    <form style='display: inline-block;width: 23%' class='view_opts' action='EditHolderChooser.jsp'>
        <div class="mousetype_selection_links" style="padding-bottom: 10px;">
            <ul class="label_text" style="columns:1;font-size:16px">
                <li style="margin-top: 0px;">
                    Sort by:  <%= genSelect("orderby", orderOpts, orderOptLabels, orderby, "", true) %>
                </li>
            </ul>
        </div>
    </form>

    <%= table %>
    <%
    } else if (command.equals("add")) {
    %>
    <div class="site_container">
        <p class="main_header">Add Holder</p>
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
                                    <input class='formInput' type=text name="firstname" required>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft">*Last name</td>
                                <td class="formRight">
                                    <input type=text name="lastname" class='formInput' required>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft">*Department</td>
                                <td class="formRight">
                                    <input type=text name="dept" class='formInput' required>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft">*Email</td>
                                <td class="formRight">
                                    <input type=email name="email" class='formInput' required>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft" style="line-height: 21px">*Primary Contact Name</td>
                                <td class="formRight">
                                    <input type=text name="alternateName" class='formInput' required>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft" style="line-height: 21px">Primary Contact Email</td>
                                <td class="formRight">
                                    <input type=email name="alternateEmail" class='formInput'>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft" style="line-height: 21px">*Primary Colony Location</td>
                                <td class="formRight">
                                    <input type=text name="primaryMouseLocation" class='formInput' required>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <div class="spacing_div_minix2"></div>
                                    <div class="MSU_green_button" style="width: 32%;float: right;margin-right: -3px;">
                                        <input type="hidden" name="command" value="Insert">
                                        <input type="submit" value="Create Holder"
                                               style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
            <div class="two_column_right">
                <div class="sidebar_desc"
                     style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height: 175px">
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
                        * Indicates required field.</p>
                </div>
            </div>
        </div>
    </div>
    <%
        }
    %>
</div>

</div> <!-- This end div is here to end the site container div. For some reason it's not picked up by intellisense, but it is necessary. -->

<%=HTMLGeneration.getWebsiteFooter()%>
<script>
    !function ($) {
        var search_form = $("form#search_form");
        var clear_btn = $("form#search_form a.clear_btn");
        var search_input = $("form#search_form input[type=search]");
        search_form.submit(function () {
            var term = search_input.val();
            var expr = new RegExp(term, 'i');
            $("div.facilityTable tr").removeClass('hide');
            if (!term) {
                clear_btn.hide();
                return false;
            }
            clear_btn.show();
            $("div.facilityTable tr.holderlist, div.facilityTable tr.holderlistAlt").each(function (i, row) {
                var $row = $(row);
                if (!$row.text().match(expr)) {
                    $row.addClass('hide');
                }
            });
            return false;
        });
        clear_btn.click(function () {
            search_input.val('');
            search_form.submit();
        });
    }(jQuery);

</script>
