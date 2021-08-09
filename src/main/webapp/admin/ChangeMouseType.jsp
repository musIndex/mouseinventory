<%@page import="edu.ucsf.mousedatabase.*" %>
<%@page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page import="java.util.ArrayList" %>

<%=HTMLGeneration.getPageHeader(null, false, true) %>
<%=HTMLGeneration.getNavBar(null, true) %>
<%
    int mouseID = Integer.parseInt(request.getParameter("mouse_id"));
    String expressedsequenceID = request.getParameter("expressedsequence_id");
    String trangenicTypeID = request.getParameter("transgenictype_id");
    int newMouseTypeID = Integer.parseInt(request.getParameter("mousetype_id"));
    String inbredStrainID = request.getParameter("inbred_strain_id");

    ArrayList<MouseRecord> records = DBConnect.getMouseRecord(mouseID);
    MouseRecord record = records.get(0);
    MouseType newMouseType = DBConnect.getMouseType(newMouseTypeID);


    boolean confirmed = Boolean.parseBoolean(request.getParameter("confirm"));


%>

<div class="site_container">

    <%
        if (!confirmed) {
            String table = HTMLGeneration.getMouseTable(records, false, false, false);
    %>

    <p class="main_header" style="line-height: 36px">Please Confirm that you want to edit this rodent's category.
        <br>
        Record #<%= mouseID %> will change from <%= record.getMouseType() %> to <%= newMouseType.getTypeName() %>.
    </p>
    <%= table %>
    <form action="ChangeMouseType.jsp" method="post">
        <input type="hidden" name="mouse_id" value="<%= mouseID %>">
        <input type="hidden" name="mousetype_id" value="<%= newMouseTypeID %>">
        <input type="hidden" name="expressedsequence_id" value="<%= expressedsequenceID %>">
        <input type="hidden" name="transgenictype_id" value="<%= trangenicTypeID %>">
        <input type="hidden" name="inbred_strain_id" value="<%= inbredStrainID %>">
        <input type="hidden" name="confirm" value="true">


        <div class="category">
            <div class="two_column_left" style="width: 90%">
                <p class='label_text'>
                    <br>
                    Yes, I want to change the category of this record from <%= records.get(0).getMouseType() %>
                    to <%= newMouseType.getTypeName() %>:
                </p>
                <div class='editRecordButtonGreen' style="width:19%">
                    <input type="submit" class='editRecordButtonInput' value="Change rodent category.">
                </div>
            </div>
            <div class="two_column_right" style="width: 10%;margin-top:2em">
                <br><br>
                <div class="MSU_back_button" style="width:100%">
                    <p class="MSU_green_button_Text" onclick="history.back()" style="cursor: pointer">
                        Back
                    </p>
                </div>
            </div>
        </div>
    </form>

    <%
    } else {
        record.setMouseType(newMouseType.getTypeName());
        DBConnect.updateMouseRecord(record);
        records = DBConnect.getMouseRecord(mouseID);
    %>
    <p class="main_header">The category of rodent ID <%= mouseID %> has been changed.</p>
    <p class="label_text">
        <a href="EditMouseForm.jsp?id=<%= mouseID %>">Click here</a> to make other changes to record.
        <br>
        If this is an incomplete submission, you will need to return to the submission page to edit it.</p>
    <p class="label_text" style="font-size: 24px">Updated Record:</p>
    <%=HTMLGeneration.getMouseTable(records, true, false, false) %>
    <div style="margin-top: 2em">
        <div class="MSU_green_button" style="margin-bottom: 20px;margin-left: 0px;margin-right: 20px;width:12%;display: inline-block">
            <a class='anchor_no_underline' href="EditMouseSelection.jsp">
                <p class="MSU_green_button_Text">Back to records
                </p>
            </a>
        </div>

        <div class="MSU_green_button" style="margin-bottom: 20px;margin-left: 0px;margin-right: 0px;background-color: #008183ff;width:12%;display: inline-block">
            <a class='anchor_no_underline' href='admin.jsp'>
                <p class="MSU_green_button_Text">Admin Home
                </p>
            </a>
        </div>
    </div>
    <%
        }
    %>

</div>
</div>

<%=HTMLGeneration.getWebsiteFooter()%>
