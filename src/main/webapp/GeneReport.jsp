<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.servlets.LoginServlet" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("GeneReport.jsp", false) %>


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
<script id="access_granted" type="text/template">
    <div class="site_container">
        <h2>Gene List for Mutant Alleles</h2>

        <form class='view_opts' action='GeneReport.jsp'>
            Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderBy,null) %>
        </form>
        <%= table%>

    </div>
</script>

<script id="access_denied" type="text/template">
    <div>
        <table class="site_container">
            <tr>
                <td style="width: 50%">
                    <h2>Gene List Login</h2>
                    Welcome to the Rodent Research Database Application's Gene List.<br>
                    Before you're able to view the gene list, ensure that
                    you have filled out the database application.<br>
                    If your application has been approved, please enter your information
                    below.<br><br>
                    <form method="post" action="loginServlet">
                    <table>
                            <tr>
                                <td><label for="email">Email address:</label></td>
                                <td><input type="text" id="email" name="email" required></td>
                            </tr>
                            <tr>
                                <td><label for="MSU NetID">MSU NetID:</label></td>
                                <td><input type="text" id="MSU NetID" name="MSU NetID" required></td>
                            </tr>
                            <tr>
                                <td>
                                    <input type = hidden name="page" value="applicationLoginGenes.jsp">
                                    <input type="submit" class ="button btn-primary" value="Login">
                                </td>
                            </tr>

                    </table>
                    </form>
                </td>
                <td style="vertical-align: top;width: 50%">
                    <h2>Application Information</h2>
                    In order to access the Rodent Records and submit
                    rodents to the database, you must first fill out an application.
                    <br>
                    This application can be found by following the button below, or
                    clicking on the "Database Application" tab in the navigation bar.
                    <br>
                    <br>
                    <a href="application.jsp"><button class = "btn btn-success">Database Appication</button></a>
                </td>
            </tr>
        </table>
    </div>
</script>

<div id="page_content">

</div>

<script>
    var access_status = <%=LoginServlet.getAccess_granted()%>;
    var granted = document.getElementById("access_granted").innerHTML;
    var denied = document.getElementById("access_denied").innerHTML;

    if (access_status == 1) {
        document.getElementById("page_content").innerHTML = granted;
    } else {
        document.getElementById("page_content").innerHTML = denied;
    }
    <%LoginServlet.setAccess_granted(0);%>;
</script>
