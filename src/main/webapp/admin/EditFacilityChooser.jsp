<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>
<%
    String command = request.getParameter("command");
    String orderby = request.getParameter("orderby");

    String[] orderOpts = new String[]{"position", "facility", "count"};
    String[] orderOptLabels = new String[]{"Public-facing", "Name", "Record count"};

    if (orderby == null) {
        orderby = orderOpts[0];
    }

    if (command == null || command.isEmpty() || command.equals("edit")) {
        ArrayList<Facility> facilities = DBConnect.getAllFacilities(false, orderby);
        String table = HTMLGeneration.getFacilityTable(facilities, true);
%>
<div class="site_container">
    <p class="main_header">Edit Facilities:</p>
    <form style='display: inline-block;width: 16%' class='view_opts' action='EditFacilityChooser.jsp'>
        <div class="mousetype_selection_links" style="padding-bottom: 10px;">
            <ul class="label_text" style="columns:1;font-size:16px">
                <li style="margin-top: 0px;">
                    Sort by: <%= HTMLGeneration.genSelect("orderby", orderOpts, orderOptLabels, orderby, null, true)  %>
                </li>
            </ul>
        </div>
    </form>
    <div class="MSU_green_button" style="float:right;display: inline-block;width: 9%">
        <a class='anchor_no_underline' href="EditFacilityChooser.jsp?command=add">
            <p class="MSU_green_button_Text">Add Facility</p>
        </a>
    </div>
    <div class="MSU_green_button"
         style="float:right;display: inline-block;width: 17%;margin-right: 10px;background-color:#008183ff;">
        <a class='anchor_no_underline' href='#' id='sort_button'
           style="height: 100%;display: flex;align-content: center;justify-content: center;align-items: center;margin: auto;text-align: center;color: white;font-size: 18px;">
            Change public sort order
        </a>&nbsp;&nbsp;
    </div>
    <div class="spacing_div_minix2"></div>


    <div class='sort-instructions' style='display:none'>
        <p class="label_text">Click and drag rows to reorder them. Click 'Save changes' when done.</p>
    </div>
    <%= table %>
    <%
    } else if (command.equals("add")) {
    %>
    <div class="site_container">
        <p class="main_header">Add New Facility</p>
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
                                    <input type=text name="facilityName" class='formInput' required>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft">*Facility Description</td>
                                <td class="formRight">
                                    <input type=text name="facilityDescription" class='formInput' required>
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
                                          rows="7"></textarea>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft">*Facility Code</td>
                                <td class="formRight">
                                    <input type=text name="facilityCode" class='formInput' required>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <div class="spacing_div_minix2"></div>
                                    <div class="MSU_green_button" style="width: 32%;float: right;margin-right: -3px;">
                                        <input type="hidden" name="command" value="Insert">
                                        <input type="submit" value="Create Facility"
                                               style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <%
        }
    %>
</div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>

<script>
    !function ($) {
        var order = '<%=orderby%>';
        var sorting = false;
        var table_body = $("div.facilityTable table tbody");
        var sort_button = $("#sort_button");
        var instructions = $(".sort-instructions");
        var positions = "";
        sort_button.click(function () {
            if (order != "position") {
                updateStatus("To change the public-facing sort order, please choose 'Public-facing' from the sort menu first.");
                return false;
            }
            if (sort_button.hasClass('disabled')) {
                return false;
            }
            sorting = !sorting;

            if (sorting) {
                clearStatus();
                var normalRows = document.getElementsByClassName("facilitylist");
                var altRows = document.getElementsByClassName("facilitylistAlt");
                var index = 0;
                while (index < normalRows.length) {
                    normalRows[index].classList.add('facilityListSpecial');
                    normalRows[index].classList.remove('facilitylist');
                    if (index < altRows.length) {
                        altRows[index].classList.add('facilityListSpecialAlt');
                        altRows[index].classList.remove('facilitylistAlt');
                    }
                    if (index % 2 == 0) {

                    } else {
                        index++;
                    }
                }
                table_body.addClass('sorting');
                sort_button.text('Save changes');
                instructions.show();
                table_body.sortable("enable");
                table_body.sortable({
                    placeholder: 'adminFacilityID',
                    distance: 15,
                    items: 'tr',
                    forcePlaceholderSize: true,
                    change: dndChange,
                    update: dndUpdate
                }).disableSelection();
            } else {
                var normalRows = document.getElementsByClassName("facilityListSpecial");
                var altRows = document.getElementsByClassName("facilityListSpecialAlt");
                document.getSelection()
                var index = 0;
                if ((normalRows.length > 0) && (altRows.length > 0)) {
                    while (index < normalRows.length) {
                        normalRows[index].classList.add('facilitylist');
                        normalRows[index].classList.remove('facilityListSpecial');
                        if (index < altRows.length) {
                            altRows[index].classList.add('facilitylistAlt');
                            altRows[index].classList.remove('facilityListSpecialAlt');
                        }
                        if (index % 2 == 0) {

                        } else {
                            index++;
                        }
                    }
                }
                sort_button.addClass('disabled');
                sort_button.text('Saving...');
                table_body.removeClass('sorting');
                $.ajax({
                    type: 'post',
                    url: '<%=HTMLGeneration.adminRoot %>UpdateFacilityOrder',
                    dataType: 'json',
                    success: updateOrderSuccess,
                    error: updateOrderFailed,
                    data: 'positions=' + positions,
                    async: true
                });
                table_body.sortable("disable");

            }


        });

        function updateOrderSuccess(data) {
            if (data.success) {
                updateStatus('Order saved', true);
            } else {
                updateOrderFailed(data);
            }
            sort_button.removeClass('disabled');
            sort_button.text('Change public sort order');
            instructions.hide();

        }

        function updateOrderFailed(data) {
            updateStatus('Failed to save changes: ' + data.message, false);
        }

        function updateStatus(message, success) {
            $("#top_status_message").text(message)
                .removeClass('alert-success').removeClass('alert-error')
                .addClass(success ? 'alert-success' : 'alert-error')
                .text(message)
                .show();
        }

        function clearStatus() {
            $("#top_status_message").hide();
        }


        function dndChange(event, ui) {
            positions = "";
        }

        function dndUpdate(event, ui) {
            positions = getSortOrder();
        }

        function getSortOrder() {
            var order = "";
            $("tbody tr td:first-child").each(function (i, column) {
                if (i > 0) {
                    order += ',';
                }
                order += $(column).text() + '-' + i;
            });
            return order;
        }

        dndUpdate();
    }(jQuery);
</script>