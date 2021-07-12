<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="newMouse" class="edu.ucsf.mousedatabase.beans.MouseSubmission" scope="session"></jsp:useBean>
<jsp:setProperty property="*" name="newMouse"/>
<jsp:useBean id="submitterData" class="edu.ucsf.mousedatabase.beans.UserData" scope="session"></jsp:useBean>


<%
    String errorsMessage = "";
    String savedMessage = "";
    newMouse.setIs_rat("0");
    newMouse.setMtaRequired(request.getParameter("mtaRequired"));
    newMouse.setCryopreserved(request.getParameter("cryopreserved"));

    newMouse.clearAllErrors();

    if ("true".equals(request.getParameter("process"))) {
        //check for save or submit button clicked
        if ("Submit Rodent".equalsIgnoreCase(request.getParameter("submitButton"))) {
            if (newMouse.validateMouseDetails()) {
%>
<jsp:forward page="submitmouse.jsp"/>
<%
                return;
            }
            errorsMessage = "Please correct the errors listed in red below.";
        } else if ("Save Data".equalsIgnoreCase(request.getParameter("submitButton"))) {
            savedMessage = "Saved. Data will be lost if you close your browser window.";
        }
    }

    String[] mtaOptions = {"Yes", "No", "Don't Know"};
    String mouseTypeTitle = newMouse.getFullMouseTypeTitle();
    String mouseNameStr = "Rodent Name";
    if (newMouse.isIS()) {
        mouseNameStr = "Strain Name";
    }
%>


<%=getPageHeader("<script language=\"javascript\" type=\"text/javascript\">document.onkeypress = checkCR;</script>\r\n",
        true, false, "onload=\"setFocus('mouseDetails', 'mouseName')\"")%>
<%=HTMLGeneration.getNavBar("submission.jsp", false, false)%>


<script>
    $(document).ready(function () {
        (function () {
            var mgiNumber = null;
            $('#popupDialog').dialog({
                autoOpen: false,
                buttons: {
                    "Close": function () {
                        $(this).dialog("close");
                    }
                }
            });

            $('#mgiautofillbutton').click(function () {
                $('#popupDialog').dialog('open');
                var message = $("#popupDialogMessage");
                message.text("Please wait...");
                fillFormFromMgi();
            });

            function fillFormFromMgi() {
                var expected_type_name = $("#expected_type_name").data("name");
                mgiNumber = $.trim($("#mouseMGIID").val());
                if (mgiNumber == null || mgiNumber == "") {
                    var message = $("#popupDialogMessage");
                    message.css("text-align", "center");
                    message.css("height", "100%");
                    message.css("width", "100%");
                    message.css("display", "table-cell");
                    message.css("vertical-align", "middle");
                    message.css("padding-left", "5px");
                    message.css("padding-right", "5px");
                    message.text("Please enter an MGI ID.");
                    return;
                }
                $.ajax({
                    type: 'GET',
                    url: '/mgidata?query=allele_properties&acc_id=' + mgiNumber + '&expected_type_name=' + expected_type_name,
                    dataType: 'json',
                    success: mgiLookupSuccess,
                    error: mgiLookupError,
                    data: {},
                    async: true
                });

            }

            function mgiLookupSuccess(data) {
                var result = null;

                if (data.is_valid != null && data.is_valid == "true") {

                    if (!data.officialSymbol) {
                        result = {success: false, message: "No data for MGI:" + mgiNumber};
                    } else {
                        var notes = "\n\n";
                        var success = true;
                        $("#mouseMGIID").val(mgiNumber);
                        $("#mouseMGIIDValid").val(true);
                        $("#mouseMGIValidation").html(replaceBrackets(data.officialSymbol + " - " + data.mouseName) + " " + formatMgiLink(mgiNumber)).switchClass("bp_invalid", "bp_valid");

                        if (data.pubMedID) {
                            $("#PMID").val(data.pubMedID);
                            $("#PMIDValid").val(true);
                            $("#PMIDValidation").html(replaceBrackets(data.pubMedTitle + " - " + data.pubMedAuthor) + " " + formatPubmedLink(data.pubMedID)).switchClass("bp_invalid", "bp_valid");
                        } else {
                            $("#PMID").val("");
                            $("#PMIDValid").val(false);
                            $("#PMIDValidation").html("");
                            notes += "NOTE: MGI does not show a reference for this rodent in Pubmed. If it is unpublished, please go back to step 2, select 'unpublished,' and complete the form including the MGI ID you entered here.";
                            success = false;
                        }

                        $("#MAMgiGeneID").val(data.geneMgiID);
                        $("#MAMgiGeneIDValid").val(true);
                        $("#mutantAlleleMGIValidation").html(replaceBrackets(data.geneSymbol + " - " + data.geneName) + " " + formatMgiLink(data.geneMgiID)).switchClass("bp_invalid", "bp_valid");
                        $("#comment").val($.trim(data.description));
                        $("#rawMGIComment").val($.trim(data.description));
                        result = {
                            success: success,
                            message: "Properties for " + data.officialSymbol + " loaded into form.",
                            note: notes
                        };
                        if (!$.trim(data.description)) {
                            result.success = false;
                            result.message = "Failed to load description from MGI.  Please try again.  If this error persists, please notify the administrator.  To complete your submission, please manually copy the description from the MGI website."
                        }
                    }
                } else {
                    result = {success: false, message: data.error_string};
                }
                var message = $("#popupDialogMessage");
                message.text(result.message);
                if (result.success) {
                    message.css("text-align", "center");
                    message.css("height", "100%");
                    message.css("width", "100%");
                    message.css("display", "table-cell");
                    message.css("vertical-align", "middle");
                    message.css("padding-left", "5px");
                    message.css("padding-right", "5px");
                    message.css("color", "black");
                } else if (result.note) {
                    message.css("text-align", "center");
                    message.css("height", "100%");
                    message.css("width", "100%");
                    message.css("display", "table-cell");
                    message.css("vertical-align", "middle");
                    message.css("padding-left", "5px");
                    message.css("padding-right", "5px");
                    message.append($("<br>")).append($("<br>")).append($("<span>", {
                        'class': 'black',
                        text: result.note
                    }));
                } else {
                    message.css("color", "black");
                    message.css("text-align", "center");
                    message.css("height", "100%");
                    message.css("width", "100%");
                    message.css("display", "table-cell");
                    message.css("vertical-align", "middle");
                    message.css("padding-left", "5px");
                    message.css("padding-right", "5px");
                }

            }

            function mgiLookupError(data) {
                var message = $("#popupDialogMessage");
                message.html("Unexpected error.  Please try again later.");
                message.css("text-align", "center");
                message.css("height", "100%");
                message.css("width", "100%");
                message.css("display", "table-cell");
                message.css("vertical-align", "middle");
                message.css("padding-left", "5px");
                message.css("padding-right", "5px");
                message.css("color", "black");
            }

            function formatMgiLink(mgiNumber) {

                return "<a class='MP' target='_blank' href='http://www.informatics.jax.org/accession/MGI:" + mgiNumber + "'>MGI:" + mgiNumber + "</a>";
            }

            function formatPubmedLink(pubmedId) {
                return "<a class='MP' target='_blank' href='http://www.ncbi.nlm.nih.gov/pubmed/" + pubmedId + "?dopt=Abstract'>Pubmed:" + pubmedId + "</a>";
            }

            function htmlEncode(value) {
                return $('<div/>').text(value).html();
            }

            function htmlDecode(value) {
                return $('<div/>').html(value).text();
            }

            function nlToBr(text) {
                return text.replace(/\n/g, "<br>");
            }

        })();
    });
</script>

<div id="popupDialog" title="Retrieving Properties from MGI">
    <div id="popupDialogMessage">

    </div>
</div>

<div class="site_container">
    <%
        if (!newMouse.hasType()) {
            //TODO just redirect them back to step 2, step 1 if there is no contact info saved.
    %>
    <p class="main_header">Unknown Error</p>
    <p class="label_text">We're sorry, but the MSU Rodent Database has encountered an error. <br>
        Please return to step 1 to begin the submission process.</p>

    <%
            return;
        }
    %>
    <p class="main_header">New Submission: Step 3</p>
    <div class="category">
        <div class="two_column_left">
            <div class="formbody">
                <form action="submitformMouseDetails.jsp" name="mouseDetails" method="post" id="mouseDetails">
                    <div id="mouseDetails">
                        <table class="inputForm" style="width: 60%">
                            <tr class="formFieldH">
                                <td class="formHeaderCell" colspan="2"><%=mouseTypeTitle%>
                                </td>
                            </tr>
                            <tr class="formField">
                                <td class="formLeft" style="width: 17%">
                                    * <%=mouseNameStr%>
                                </td>
                                <td class="formRight" style="width: 15%">
                                    <input type="text" name="mouseName" value="${ newMouse.mouseName}" required>
                                    <%--                  <span style="color: #23476b;text-emphasis: #23476b; font-style: italic">${ newMouse.mouseNameErr}</span>--%>
                                </td>
                            </tr>
                            <%
                                if (newMouse.isMA()) {
                            %>
                            <tr id=expected_type_name data-name=allele style="display:none"></tr>
                            <%
                                if (newMouse.isPublished()) {
                            %>
                            <%@ include file="submitformMAPublished.jspf" %>
                            <input type="hidden" name="rawMGIComment" id="rawMGIComment"
                                   value="${ newMouse.rawMGIComment}" required/>
                            <%
                            } else {
                            %>
                            <%@ include file="submitformMAUnpublished.jspf" %>
                            <%
                                }
                            } else if (newMouse.isTG()) {
                            %>
                            <tr id=expected_type_name data-name=transgene style="display:none"></tr>
                            <%
                                if (newMouse.isPublished()) {
                            %>
                            <%@ include file="submitformTGPublished.jspf" %>
                            <input type="hidden" name="rawMGIComment" id="rawMGIComment"
                                   value="${ newMouse.rawMGIComment}"/>
                            <%
                            } else {
                            %>
                            <%@ include file="submitformTGUnpublished.jspf" %>
                            <%
                                }
                            } else if (newMouse.isIS()) {
                            %>
                            <%@ include file="submitformInbredStrain.jspf" %>
                            <%
                                }
                            %>

                            <tr>
                                <td colspan="2">
                                    <div class="spacing_div_minix2"></div>
                                    <div id="backButton" class="MSU_back_button"
                                         style="<%=HTMLGeneration.elementVisibility(newMouse.hasType()) %>">
                                        <a class="anchor_no_underline" href="submitformMouseType.jsp">
                                            <p class="MSU_green_button_Text" style="font-size: 19px">Back</p>
                                        </a>
                                    </div>
                                    <div id="nextButton" class="MSU_green_button"
                                         style="margin-right:-3px;float:right;width: 32%;<%=HTMLGeneration.elementVisibility(newMouse.hasType()) %>">
                                        <input type="hidden" name="process" value="true">
                                        <input type="submit" name="submitButton" value="Submit Rodent"
                                               style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <div id="nextButton" class="MSU_green_button"
                                         style="background-color:#008183ff;margin-right:-3px;float:right;width: 32%;<%=HTMLGeneration.elementVisibility(newMouse.hasType()) %>">
                                        <input type="submit" name="submitButton" value="Save Data"
                                               style="width: 100%;height: 100%;background-color: transparent;border: none;font-size: 19px;color: white;">
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </form>
            </div>
        </div>
        <div class="two_column_right">
            <%
                if (newMouse.isIS()) {
            %>
            <div class="sidebar_desc"
                 style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height: 325px">
                <%--      <h3>--%>
                <%--        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><%=errorsMessage%></span>--%>
                <%--      </h3>--%>
                <p class="block_form_label_text" style="text-align: center">If you leave this page without saving, ALL
                    the data
                    entered will be lost.</p><br><br>
                <p class="block_form_desc_text">If you encounter any difficulties while completing this page, click
                    'Submit
                    Feedback' at the top of the screen.</p><br><br>

                <p class="block_form_desc_text">Use the catalog link below to search for JAX mice.</p><br>
                <a class="anchor_no_underline" href="http://jaxmice.jax.org/query"><p
                        style="text-decoration: underline;margin-block-start: 0em" class="label_text">JAX mice
                    catalog</p></a>

                <p class="block_form_desc_text">If available, please enter the URL for the description of the mouse on
                    the
                    supplier's website.<br><br>

                <p class="block_form_desc_text">When entering a </p>
                <p class="block_form_label_text">comment</p>
                <p class="block_form_desc_text">, use the field to provide a brief description of the strain, its uses,
                    and other pertinent information. (Do not include detailed information that can be found
                    via the link to the description in the supplier's catalog.)

                    <br><br>

                    *Indicates required field.</p>
            </div>

            <div style="height: 75px;width: 100%"></div>
            <div class="sidebar_desc"
                 style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height: 90px">
                <p class="block_form_desc_text">By clicking </p>
                <p class="block_form_label_text">Save data</p>
                <p class="block_form_desc_text">, you will save data that has been entered
                    as long as your browser window stays open, or until you submit the rodent.<br><br>

                    By clicking </p>
                <p class="block_form_label_text">Submit rodent</p>
                <p class="block_form_desc_text">, you will submit the rodent and complete the submission process.
                </p>
            </div>
            <%
            } else if (newMouse.isTG()) {
            %>
            <div class="sidebar_desc"
                 style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height:750px">
                <%--      <h3>--%>
                <%--        <span style="color: #23476b;text-emphasis: #23476b; font-style: italic"><%=errorsMessage%></span>--%>
                <%--      </h3>--%>
                <p class="block_form_label_text" style="text-align: center">If you leave this page without saving, ALL
                    the data
                    entered will be lost.</p><br><br>
                <p class="block_form_desc_text">If you encounter any difficulties while completing this page, click
                    'Submit
                    Feedback' at the top of the screen.<br><br>
                </p>
                <p class="block_form_label_text">Rodent Name</p>
                <p class="block_form_desc_text">is the unofficial nomenclature used by holder.<br><br>

                    <%
                        if (newMouse.isPublished()) {
                    %>

                    Copy the </p>
                <p class="block_form_label_text">MGI ID</p>
                <p class="block_form_desc_text"> for the transgene from the transgene detail page (number only) and
                    paste it
                    here. Although each published transgene should have a detail page, occasionally it does not. If you
                    are
                    absolutely certain that there is no detail page on MGI for the transgene you want to submit, enter
                    'none'.
                    Admin will inform MGI, and they will provide a detail page for the transgene, usually within a few
                    days.<br><br>

                    If the transgene you want to submit is published, there should be a </p>
                <p class="block_form_label_text">Transgene Detail</p>
                <p class="block_form_desc_text"> page for it on <a href="http://www.informatics.jax.org/"
                                                                   style="font-weight: bold">Mouse Genome
                    Informatics</a> (MGI). You need to find this
                    page in
                    order to complete this submission. If you are not certain how to find it, click here:</p><br>
                <div class="MSU_green_button"
                     style="margin-top: 10px;margin-bottom: 10px;display: inline-block;width: 60%">
                    <% Setting s3 = DBConnect.loadSetting("download_files_transgene_id"); %>
                    <a class="anchor_no_underline" href='<%= s3.value %>' target=_blank>
                        <p class="MSU_green_button_Text" style="font-size: 16px">
                            <%= s3.label %>
                        </p>
                    </a>
                </div>
                <br>
                <%
                } else {
                %>
                </p>
                <%
                    }
                %>

                <%
                    if (!newMouse.isPublished()) {
                %>
                <p class="block_form_desc_text">
                    If you have entered a valid MGI ID, the official symbol for the transgene is shown. Please click on
                    the link that has been generated
                    to the MGI accession number for this transgene and</p>
                <p class="block_form_label_text">double check</p>
                <p class="block_form_desc_text"> to make sure that it
                    describes the transgene you want to submit. If not, replace the MGI ID with the one for the correct
                    transgene.
                    <br><br>
                    <%
                        }
                    %>
                </p>
                <p class="block_form_label_text">Official Symbol</p>
                <p class="block_form_desc_text"> for the transgene being submitted (automatically entered when the MGI
                    ID is
                    entered). If the official symbol seems to be correct, click the autofill button. If not, replace the
                    MGI ID
                    with the one for the correct allele.<br><br>
                    <%
                        if (newMouse.isPublished()) {
                    %>
                </p>

                <p class="block_form_label_text">Pubmed ID</p>
                <p class="block_form_desc_text">, generally for the publication in which the mutant allele was first
                    described
                    (automatically entered when the MGI ID for the allele is entered)
                    If you entered 'none' in the MGI ID field above, enter the PMID for a publication in which the
                    mutant allele
                    is described.<br><br>
                    <%
                        }
                    %>
                    If the transgene was produced by Gensat, please provide a</p>
                <p class="block_form_label_text">Founder Line</p>
                <p class="block_form_desc_text">. This is included at the end of the Official Symbol, e.g.
                    'Tg(Epha2-EGFP)DE51Gsat' - the 'Founder Line' here is DE51.<br>
                    <a style="font-weight: bold;text-decoration: underline" href="http://www.gensat.org/search.jsp">Gensat
                        catalog</a><br><br>

                    Text automatically entered in the </p>
                <p class="block_form_label_text">Comment</p>
                <p class="block_form_desc_text"> section is the description of the allele provided by MGI. Please read
                    it and
                    make sure it accurately describes the transgene you want to submit. If not, make the appropriate
                    changes.
                    If, after checking the information that has been entered (i.e. name of gene modified, relevant
                    publication,
                    and description of the allele) you realize that this is not the transgene you want to submit, please
                    start
                    over by entering the MGI ID for the correct transgene and clicking the autofill button.
                    If you entered 'none' in the MGI ID field above, enter a brief description of the transgene
                    here.<br><br>

                </p>

                <p class="block_form_label_text">Gene</p>
                <p class="block_form_desc_text"> (MGI ID), is the MGI ID for the gene that is mutated in the allele
                    being
                    submitted. When auto-filled, the official symbol and full name of the gene will appear. Clicking on
                    the link
                    that has been generated to the MGI accession number will bring up the page that describes this gene.
                    If you
                    are not certain how to find this information, click the 'How to find the MGI gene ID'
                    button.</p><br>
                <div class="MSU_green_button"
                     style="margin-top: 10px;margin-bottom: 10px;display: inline-block;width: 45%">
                    <% Setting s4 = DBConnect.loadSetting("download_files_gene_id"); %>
                    <a class="anchor_no_underline" href='<%= s4.value %>' target=_blank>
                        <p class="MSU_green_button_Text" style="font-size: 16px">
                            <%= s4.label %>
                        </p>
                    </a>
                </div>
                <br>

                <p class="block_form_desc_text">For </p>
                <p class="block_form_label_text">reporter-cre</p>
                <p class="block_form_desc_text"> fusions, select 'Cre'.<br><br>

                    For </p>
                <p class="block_form_label_text">Other</p>
                <p class="block_form_desc_text"> expressed sequence types, write the description of the expressed
                    sequence.<br><br>

                    When entering the </p>
                <p class="block_form_label_text">Regulatory Element</p>
                <p class="block_form_desc_text">, briefly describe the sequences that drive expression of the transgene,
                    (e.g.
                    'CAG promoter'). For mice produced using a BAC (e.g. Gensat mice), state 'BAC containing X gene
                    (provide gene
                    symbol)'.<br><br>

                    Enter only defined </p>
                <p class="block_form_label_text">Background Strains</p>
                <p class="block_form_desc_text"> (i.e. do not enter 'mixed' or 'not known').<br>
                    (Optional: if the mutant allele is being maintained in combination with another mutant allele(s) or
                    a
                    transgene(s), this can be noted here.)<br><br>

                    <%
                        if (!newMouse.isPublished()) {
                    %>
                    If the mouse was produced in
                </p>
                <p class="block_form_label_text">another laboratory</p>
                <p class="block_form_desc_text">, please use the comment field to provide the name and
                    Institution of the PI in whose laboratory the mouse was produced (or from whom the mouse was
                    obtained)<br><br>
                    <%
                        }
                    %>
                    *Indicates required field.</p>
            </div>

            <div style="height: 75px;width: 100%"></div>
            <div class="sidebar_desc"
                 style="width: 100%;margin-left:-100px;padding-left: 10px;margin-top: 0px;padding-top: 3px;padding-right: 6px;height: 90px">
                <p class="block_form_desc_text">By clicking </p>
                <p class="block_form_label_text">Save data</p>
                <p class="block_form_desc_text">, you will save data that has been entered
                    as long as your browser window stays open, or until you submit the rodent.<br><br>

                    By clicking </p>
                <p class="block_form_label_text">Submit rodent</p>
                <p class="block_form_desc_text">, you will submit the rodent and complete the submission process.
                </p>
            </div>
            <%
                }
            %>
            <div class="label_text"
                 style="width: 100%;height: 40px;float: left;margin-left: -100px;margin-top: 25px;vertical-align: middle;">
                <%=savedMessage%>
            </div>
        </div>

    </div>
</div>

<%=HTMLGeneration
        .
                getWebsiteFooter
                        (
                        )%>

