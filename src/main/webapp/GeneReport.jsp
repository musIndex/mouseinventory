<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.servlets.LoginServlet" %>
<%@ page import="static edu.ucsf.mousedatabase.HTMLGeneration.scriptRoot" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("GeneReport.jsp", false)%>
<%
    String orderBy = request.getParameter("orderby");
    if (orderBy == null)
    {
        orderBy = "fullname";
    }

    String[] orderOpts = new String[]{"symbol","fullname","mgi"};
    String[] orderOptLabels = new String[]{"Symbol","Full name","MGI number"};


    ArrayList<Gene> genes = DBConnect.getAllGenes(orderBy);
    String table = HTMLGeneration.getGeneTable(genes,false);
%>
<div class="site_container">
    <p class="main_header">Gene List for Mutant Alleles</p>
    <div style="padding-bottom: 15px;width: 100%">
        <form class='view_opts' style='color:black;font-size: 16px;display: inline;' action='GeneReport.jsp'>
            Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderBy,null) %>
            <input type = hidden name="page" value="gene_search">
        </form>

        <form method="post" action="loginServlet" class="search_right">
            <input type="text" placeholder="Search..." style='font-size:120%;vertical-align:top;margin-top: 0px' class="input-xlarge" name="search_terms" id="search_terms"></input>
            <input type="image" alt="Submit" src=/img/Eyeglass-black.svg style="height: 28px;margin: 0px">
            <input type="hidden" name="page" value="search_bar">
        </form>
    </div>
    <div style="width: 100%">
        <%= table%>
    </div>

    <div class="spacing_div"></div>
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
            <p class="button_body_text">You can contact the MSU Rodent Database admin at ORA.MSURodentDatabase@msu.edu.</p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="mailto:ORA.MSURodentDatabase@msu.edu">
                    <p class="MSU_green_button_Text">Email</p>
                </a>
            </div>
        </div>


        <div class="three_column_right">
            <img src="/img/AboutUs.svg" class="image-center" style="width: 50%;">
            <br>
            <p class="button_header">About Us</p>
            <p class="button_body_text">Learn more about the history of the MSU Rodent Database.</p>
            <div class="MSU_green_button">
                <a class="anchor_no_underline" href="history.jsp">
                    <p class="MSU_green_button_Text">Go</p>
                </a>
            </div>
        </div>
    </div>
</div>
</div> <!-- This end div is here to end the site container div. For some reason it's not picked up by intellisense, but it is necessary. -->

<%=HTMLGeneration.getWebsiteFooter()%>
