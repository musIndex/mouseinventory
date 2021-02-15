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
        <table>
            <tr>
                <td style="width:50%;vertical-align: top">
                    <h2>Gene List for Mutant Alleles</h2>


                    <form class='view_opts' action='loginServlet'>
                        Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderBy,null) %>
                        <input type = hidden name="page" value="gene_search">

                    </form>
                    <%= table%>
                </td>
                <td style="width:50%; text-align: center;vertical-align: top">
                    <form method="post" action="loginServlet" style="horiz-align: center">

                    <table style="text-align: center">
                        <tr style="text-align: center">
                            <div class="flexBox">
                                <div class="centered">
                                    <h2>Database Search:</h2>
                                        <input type="text" name="search_terms" id="search_terms">
                                        <input type="hidden" name="page" value="search_bar">

                                        <input type="submit" class = "btn btn-primary" value="Search">
                                    <br>
                                    <br>
                                    <b>Search examples:</b>
                                    <br>
                                    <i>shh null</i><br>
                                    Match records that contain both 'shh' <b>and</b> 'null'<br>
                                    <i>htr</i><br>
                                    Match words that start with htr, such as htr2c, or htr1a<br>
                                    <i>htr2c</i><br>
                                    Find the specific gene 'htr2c'<br>
                                    <i>1346833</i><br>
                                    Look up MGI ID 1346833<br>
                                    <i>12590258</i><br>
                                    Look up Pubmed ID 1346833<br>
                                    <i>#101,#103</i><br>
                                    Show record numbers 101 and 103<br>
                                </div>

                            </div>
                    </table>
                    </form>

                </td>
            </tr>
        </table>
    </div>