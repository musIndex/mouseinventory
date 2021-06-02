<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="edu.ucsf.mousedatabase.servlets.LoginServlet" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("HolderReport.jsp", false)%>
<%
    String orderby = request.getParameter("orderby");
    ArrayList<Holder> holders = DBConnect.getAllHolders(false,orderby);
    String table = HTMLGeneration.getHolderTable(holders,false);

    String[] orderOpts = new String[]{"firstname,lastname","lastname,firstname","department","count","datevalidated","primary_mouse_location"};
    String[] orderOptLabels = new String[]{"First name","Last name","Department","Rodents held","Last review date","Primary location of Colony"};


%>
<div class="site_container">
    <p class="main_header">Holder List</p>
<%--    <% for (Setting holderText : DBConnect.getCategorySettings(Setting.SettingCategory.HOLDER_LIST_TEXTS.Id)) { %>--%>
<%--    <p style='max-width:620px'><%=holderText.value %></p>--%>

<%--    <%} %>--%>
    <div style="padding-bottom: 15px">
        <form style="color:black;font-size: 16px;display: inline;" class='view_opts' action='HolderReport.jsp'>
            Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderby,null) %>
        </form>

        <form class="search_right" id='search_form'>
            <input type="text" placeholder="Search..." style='font-size:120%;vertical-align:top;margin-top: 0px' class="input-xlarge"></input>
            <input type="image" alt="Submit" src=/img/Eyeglass-black.svg style="height: 28px;margin: 0px">
            <a style='display:none' href='#' class="x_clear">
                <img src="/img/Clear.svg" style="height: 28px">
            </a>
        </form>
    </div>



    <%= table %>

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
<script>
    !function($){
        var search_form = $("form#search_form");
        var clear_btn = $("form#search_form a.x_clear");
        var search_input = $("form#search_form input[type=text]");
        search_form.submit(function(){
            var term = search_input.val();
            var expr = new RegExp(term,'i');
            $("div.holderTable tr").removeClass('hide');
            if (!term) {
                clear_btn.hide();
                return false;
            }
            clear_btn.show();
            $("div.holderTable tr.holderlist, div.holderTable tr.holderlistAlt").each(function(i,row){
                var $row = $(row);
                if (!$row.text().match(expr)) {
                    $row.addClass('hide');
                }
            });
            return false;
        });
        clear_btn.click(function(){
            search_input.val('');
            search_form.submit();
        });
    }(jQuery);

</script>

<%=HTMLGeneration.getWebsiteFooter()%>
