<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.beans.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%=HTMLGeneration.getPageHeader(null, true, false) %>
<%=HTMLGeneration.getNavBar("submitforminit.jsp", false) %>
<jsp:useBean id="newRat" class="edu.ucsf.mousedatabase.beans.RatSubmission" scope="session"/>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"/>

<div class="site_container" style="height: 400px">

    <%
        if (!submitterData.ValidateContactInfo() || !newRat.ValidateHolderInfo()) {
    %>
    <p class="label_text">Invalid contact information. Please go back to step 1.</p>
    <a href="submitforminit.jsp">Back to step 1</a>
    <%
            return;
        }
        if (!newRat.validateRatType()) {
    %>
    <p class="label_text">Invalid rodent type. Please go back to step 2.</p>
    <a href="submitformRatType.jsp">Back to step 2</a>

    <%
            return;
        }
        if (!newRat.validateRatDetails()) {
    %>
    <p class="label_text">Invalid rodent data. Please go back to step 3.</p>
    <a href="submitformRatDetails.jsp">Back to step 3</a>

    <%
            return;
        }

        String err = "";
        boolean ok = true;
        String existingRecordTable = "";
        String submissionAdminComment = "";
        int submissionID = -1;
        boolean isDuplicate = false;
        int existingRecordID = -1;

        if (newRat.getRatRGDID() != null && !(newRat.getRatRGDID().isEmpty()) && (newRat.isTG() || newRat.isMA())) {
            String repositoryCatalogID = newRat.getRatRGDID();
            if (repositoryCatalogID != null && !repositoryCatalogID.equalsIgnoreCase("none")) {
                existingRecordID = DBConnect.checkForDuplicates(Integer.parseInt(newRat.getRatRGDID()), -1);
            }
        } else if (newRat.isIS()) {
            //check supplier
            String supplier = newRat.getISSupplier();
            if (newRat.getISSupplierCatalogNumber() == null) {
                //don't dupe check records with no catalog number
                supplier += ": " + newRat.getISSupplierCatalogUrl();
            } else {
                supplier += ": " + newRat.getISSupplierCatalogNumber();

                if (supplier != null) {
                    //TODO have different validation rules for non-jax mice
                    String supplierRegex = supplier.trim().replace(",", "[,]*");
                    supplierRegex = supplierRegex.replace(" ", "[ ]*");
                    existingRecordID = DBConnect.checkForDuplicates(supplierRegex);
                }
            }
        }
        if (existingRecordID > 0) {
            ArrayList<MouseRecord> existingMice = DBConnect.getMouseRecord(existingRecordID);
            if (existingMice.size() > 0) {
                MouseRecord existingRat = existingMice.get(0);

                if (!existingRat.isHidden()) {
                    existingRecordTable = HTMLGeneration.getMouseTable(existingMice, false, true, false);
                    err = "This appears to be a duplicate entry and will not be processed.  The exisiting record is shown below.";
                    err += "<br><br>To add another holder to this rodent record, click 'request change in record,' and submit a change request.";
                    isDuplicate = true;
                    ok = false;
                } else {
                    submissionAdminComment += "NOTE: this is a DUPLICATE of record #" + existingRecordID +
                            ", but the submission was allowed because that record is hidden.  (Incomplete, deleted, or only covert holders)";
                }
            }
        }

        if (!isDuplicate) {
            String id = newRat.getRatRGDID();
            RGDResult result = RGDConnect.getGeneQuery(id);
            newRat.setOfficialSymbol(result.getSymbol());

            Properties props = RatSubmission.GetPropertiesString(submitterData, newRat);
//            newRat.setTGExpressedSequence(props.getProperty("ExpressedSequence"));
            newRat.setIs_rat("1");
            submissionID = DBConnect.insertSubmission(submitterData, newRat, props, SubmittedMouse.SubmissionFormSource);
            if (!submissionAdminComment.isEmpty()) {
                DBConnect.updateSubmission(submissionID, "new", submissionAdminComment);
            }
            if (submissionID < 0) {
                ok = false;
                err += "A problem occurred while entering your submission.  Please contact the administrator.";
            }
        } else {
            newRat.clearRatData();
        }

    %>

    <% if (ok) {
        HTMLUtilities.logRequest(request);
    %>
    <div class="category" style="height: 100%">
        <div class="two_column_left" style="width: 45%">
            <p class="main_header" style="line-height: 36px">Submission #<%=submissionID %> (<%=newRat.getRatName()%>) was successful.</p>
            <p class="label_text">We have received your request to add your rodent to the MSU Rodent Database.
                It will be reviewed by the administrator.
                <br>
                <br>
                If you believe you have made an error, click on "submit feedback"
                or send an explanatory email to the Rodent Database Administrator.
                <br>
                Thank you.

            <br>
            Your submission is complete! You now can either submit another rodent or go to the homepage.
            <br><br>
            New listings are checked every Tuesday and Friday.
            <br>
            If you'd like to inquire about your listing, email us at <a href="mailto:ORA.MSURodentDatabase@msu.edu">ORA.MSURodentDatabase@msu.edu</a>.
            </p>
        </div>
        <div class="two_column_right" style="width: 55%;display: flex;height: 100%;align-content: center;justify-content: center;align-items: center;">
            <table style="border-spacing: 0px;width: 90%;margin-left: auto;margin-right: auto;display: table;text-align: center;">
                <tr style="background-color: #cccccc6e;color: black;font-size: 15px;height: 50px">
                    <td class="table_top_left">
                        Monday
                    </td>
                    <td class="table_top_mid">
                        Tuesday
                    </td>
                    <td class="table_top_mid">
                        Wednesday
                    </td>
                    <td class="table_top_mid">
                        Thursday
                    </td>
                    <td class="table_top_mid">
                        Friday
                    </td>
                    <td class="table_top_mid">
                        Saturday
                    </td>
                    <td class="table_top_right">
                        Sunday
                    </td>
                </tr>
                <tr style="height: 75px">
                    <td class="table_bot_left">

                    </td>
                    <td class="table_bot_mid">
                        X
                    </td>
                    <td class="table_bot_mid">

                    </td>
                    <td class="table_bot_mid">

                    </td>
                    <td class="table_bot_mid">
                        X
                    </td>
                    <td class="table_bot_mid">

                    </td>
                    <td class="table_bot_right">

                    </td>
                </tr>
            </table>
        </div>
    </div>
    <%
        newRat.clearRatData();
    } else {%>
    <br>
    <p class="description_text">
        <%= err %>
    </p>
    <%=existingRecordTable %>
    <%}%>

    <div class="category">
        <div class="three_column_left">
            <img src="/img/Home.svg" class="image-center" style="width: 50%;">
            <br>
            <p class="button_header">Home</p>
            <p class="button_body_text">Return to the MSU Rodent Database homepage.</p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="about.jsp">
                    <p class="MSU_green_button_Text">Go</p>
                </a>
            </div>
        </div>

        <div class="three_column_center">
            <img src="/img/Questions.svg" class="image-center" style="width: 50%;">
            <br>
            <p class="button_header">Questions?</p>
            <p class="button_body_text">You can contact the MSU Rodent Database admin at
                ORA.MSURodentDatabase@msu.edu.</p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="mailto:ORA.MSURodentDatabase@msu.edu">
                    <p class="MSU_green_button_Text">Email</p>
                </a>
            </div>
        </div>


        <div class="three_column_right">
            <img src="/img/Mouse.svg" class="image-center" style="width: 50%;">
            <br>

            <p class="button_header">Submit Another Rodent</p>
            <p class="button_body_text">Have more rodents to submit? Click below to return to the beginning of the
                submission process.</p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="submission.jsp">
                    <p class="MSU_green_button_Text">Go</p>
                </a>
            </div>
        </div>
    </div>

</div>

<%=HTMLGeneration.getWebsiteFooter()%>
