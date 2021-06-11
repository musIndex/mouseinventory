<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%@ page import="edu.ucsf.mousedatabase.HTMLGeneration" %>

<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=HTMLGeneration.getNavBar("about.jsp", false)%>



<div class="site_container_pic" style="position: relative;margin:0px;overflow-y: hidden">

    <img src="/img/msu_airview_cropped_smallest.png" style="filter: brightness(70%);width: 100%;">


    <div class="site_container" style="position: absolute;top: 15%;width:100%;margin: 0px 0px !important;">
        <div class="category">


        <div class="two_column_left" style="padding-left:40px;width: 40%">
            <div class="MSU_green_button_transparent">
                <a class="anchor_no_underline" href='<%=siteRoot%>search.jsp?search-source=about_banner'>
                    <img src="/img/eyeglass_homepage.svg" class="homepage_image">
                    <p class="MSU_green_button_transparent_text">Search for rodents</p>
                </a>
            </div>
            <div class="spacing_div_mini"></div>
            <div class="MSU_green_button_transparent">
                <a class="anchor_no_underline" href='<%=siteRoot%>submission.jsp'>
                    <img src="/img/plus.svg" class="homepage_image">
                    <p class="MSU_green_button_transparent_text">Submit a new rodent</p>
                </a>
            </div>
            <div class="spacing_div_mini"></div>
            <div class="MSU_green_button_transparent">
                <a class="anchor_no_underline" href='#about_details'>
                    <img src="/img/question_mark.svg" class="homepage_image">
                    <p class="MSU_green_button_transparent_text">Learn about the database</p>
                </a>
            </div>
        </div>
        <div class="two_column_right" style="padding-right:40px">
            <p class="homepage_label">
                Collaborate, Investigate, Learn.
                <br>
                Spartans Will.
                <br><br>
            </p>
            <p class="homepage_text" style="display: inline">Welcome to the </p><p class="homepage_text_bold" style="display: inline">MSU Rodent Database:</p>
            <p class="homepage_text">Explore unique and inbred strains while fostering partnership in research.</p>
        </div>
    </div>
    </div>

</div>

<div class='site_container'>
    <div class="spacing_div"></div>
    <div class="category">
        <div class="three_column_left">
            <img src="/img/Download.svg" class="image-center" style="width: 50%;">
            <br>
            <p class="button_header">Rodent Mutants Explained</p>
            <p class="button_body_text">Learn more about how to use the Michigan State University Rodent Database.</p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="https://msurodentdatabasefiles.s3.us-east-2.amazonaws.com/Mouse+Mutants+Explained.pdf">
                    <p class="MSU_green_button_Text">Download</p>
                </a>
            </div>
        </div>
        <div class="three_column_center">
            <img src="/img/Download.svg" class="image-center" style="width: 50%;">
            <br>
            <p class="button_header">Rodent Database User Manual</p>
            <p class="button_body_text">Dr. Martin created these presentations to explain the differences between mutant allele and transgene mice. </p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="https://msurodentdatabasefiles.s3.us-east-2.amazonaws.com/Rodent+Database+User+Manual+2-13-21.pdf">
                    <p class="MSU_green_button_Text">Download</p>
                </a>
            </div>
        </div>
        <div class="three_column_right">
            <img src="/img/wordbubble.svg" class="image-center" style="width: 50%;">
            <br>
            <p class="button_header">Request a Demonstration</p>
            <p class="button_body_text">Want to schedule a demonstration?<br>Let us know!</p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="mailto:ORA.MSURodentDatabase@msu.edu">
                    <p class="MSU_green_button_Text">Contact Us</p>
                </a>
            </div>
        </div>
    </div>

    <div class="spacing_div"></div>


</div>
<div class='about_container'>
    <div id='about_details'>
        <p class="main_header" id="details" style="text-align: left;padding-left: 30px">MSU Rodent Database Details and Resources</p>
        <div class="category">
            <div class="two_column_left" id="left_side" style="width: 30%">
                <div class="MSU_sidebar_button">
                    <a style='cursor: pointer' class="anchor_no_underline" onclick="sidebar(this, 'rodent_record')">
                        <p class="MSU_green_button_Text">What is a rodent record?</p>
                        <p id="rodent_record" style="display: none">The Rodent Record contains 6 parts: Name, Category, Details, Comment, Files and Holders.

                            Each record has a unique database Record # and includes the 'informal' rodent name provided by the person who submitted the entry, the rodent category (Mutant Allele, Transgene, or Inbred Strain) and the name of the 'holder' (investigator with an approved protocol and colony location).

                            When a mutant allele, transgene, or inbred strain is being maintained by two or more holders, there is only one record for it, which lists all holders.  If the rodent is maintained with another mutation, it can be listed in the comment field.

                            For mutant alleles, the type of modification is shown, as well as the official name and symbol of the gene that is modified. In addition, the ID number for that gene in the Mouse Genome Informatics (MGI) database or Rodent Genome Database (RGD) is shown.

                            Each entry is for an individual mutant allele, transgene, or inbred strain irrespective of whether the allele or transgene is maintained in combination with other mutant alleles or transgenes. The 'Background Strain' or 'Comment' fields in the rodent record can be used to list rodents maintained with other mutations.
                            If the mutant allele is a knock-in, the expressed sequence is described; if it is a rodent gene, the official name, symbol and MGI ID of that gene is shown, with a link to MGI.

                            For transgenes, the expressed sequence and regulatory elements are described; if the expressed sequence is a rodent gene, the official name, symbol and MGI/RGD ID of the gene is shown, with a link to MGI/RGD.

                            A comment on or brief description of the mutant allele/transgene may be provided.

                            For inbred strains, the name of the commercial supplier is provided, For those obtained from JAX Mice, there is a link to the description of that strain on the JAX Mice website. For those obtained from the RGD, there is a link to the description of that strain on the RGD website.

                            If the mutant allele or transgene has been published, the record also provides the MGI/RGD allele/transgene ID and official symbol for the rodent, along with the Pubmed ID for the most relevant publication(s) describing the genetic modifications in the allele/transgene. Clicking on the MGI allele/transgene ID or the Pubmed ID will bring up the relevant pages on MGI or Pubmed, respectively.

                            Background strain of the mutant allele/transgene can be listed.
                        </p>
                    </a>
                </div>
                <div class="MSU_sidebar_button">
                    <a style='cursor: pointer' class="anchor_no_underline" onclick="sidebar(this, 'rodent_types')">
                        <p class="MSU_green_button_Text">Types of mutant rodents</p>
                        <p id="rodent_types" style="display: none">Each rodent entry in the MSU Rodent Database is classified in one of three categories.

                            1) Mutant Allele Mutations are in a specific gene or sequence with a type of modification being targeted disruption, conditional allele, targeted knock-in, gene trap insertion, chemically induced (ENU), spontaneous mutation, endonuclease-mediated, or other (description provided).

                            2) Transgene Mutations express randomly inserted sequences in the genome. It requires the insertion of a regulatory element and the expressed sequence is listed as Cre, Reporter, rodent gene, or other.

                            3) Inbred Strain These rodents are isogenic as a result of extensive inbreeding of siblings, 20+ generations.
                        </p>
                    </a>
                </div>
                <div class="MSU_sidebar_button">
                    <a style='cursor: pointer' class="anchor_no_underline" onclick="sidebar(this, 'change_request')">
                        <p class="MSU_green_button_Text">Want to request a change?</p>
                        <p id="change_request" style="display: none">Only the administrator(s) of the MSU Rodent Database can make changes to the data that are on display in the database.

                            If you want to add a rodent that is not already in the inventory, click on 'Submit Rodents' at the top of this page, and complete the submission form.

                            If a rodent is already in the inventory and you want to add the name of an additional holder, go to 'Rodent Records' and find the record for that rodent, click on 'Request change in record' (under the rodent name) and complete the request change form.

                            If a rodent is no longer being maintained by a particular holder, the name of that holder should be deleted from the record using the same procedure as for adding a holder. Note, that if the holder to be deleted is the only investigator maintaining the rodent, the record for that rodent will be deleted from the database.

                            Requests for other changes to a record can also be submitted using the request change form.

                            When a completed submission or request change form is submitted, an administrator will generate a record for the rodent in the database or make the requested alterations in holders or other information about a rodent.
                        </p>
                    </a>
                </div>
                <div class="MSU_sidebar_button">
                    <a style='cursor: pointer' class="anchor_no_underline" onclick="sidebar(this, 'technical_details')">
                        <p class="MSU_green_button_Text">Technical details</p>
                        <p id="technical_details" style="display: none">The MSU Rodent Database is a web application consisting of Java Server Pages and custom Java libraries running on Tomcat, backed by a MySQL database. JavaScript must be enabled to access all features of the database.The code behind the database is open source, and is available on the licenses page.

                            Version 1, Programmed by Nick Didkovsky, resources provided by Mary Elizabeth Hatten (Rockefeller University) & specified by Gail Martin (UCSF) April 2009.
                            Version 2 Developed by Jonathan Scoles, San Francisco, CA. February, 2013.
                            Version 2.1 Updated by Estelle Wall, Douglas Johnston, Carlo Quinonez September 2019.
                            Version 3.1 Updated by Jacob Willinger, Chris Cardimen November 2020.
                            Version 3.2 Updated by Chris Cardimen February 2021.
                            Version 4.0 Updated by Chris Cardimen June 2021.
                        </p>
                    </a>
                </div>
            </div>

            <div id="right_side" class="sidebar_desc">
                <p class="sidebar_text" id="inner_text">

                </p>
            </div>
        </div>

    </div>
</div>

<script>
    function sidebar(element, string){
        var description = document.getElementById("inner_text");
        var text = document.getElementById(string)
        description.innerText = text.innerText;
    }
</script>

<%=HTMLGeneration.getWebsiteFooter()%>


