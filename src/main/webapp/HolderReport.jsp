<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("HolderReport.jsp", false) %>
<%
  String orderby = request.getParameter("orderby");
    ArrayList<Holder> holders = DBConnect.getAllHolders(false,orderby);
    String table = HTMLGeneration.getHolderTable(holders,false);

    String[] orderOpts = new String[]{"firstname,lastname","lastname,firstname","department","count","datevalidated"};
    String[] orderOptLabels = new String[]{"First name","Last name","Department","Mice held","Last review date"};
    
    
%>

<div class="site_container">
  <h2>Holder List:</h2>
  <p style='max-width:620px'>Provides the name of each PI whose mice are listed in the database (Holder) with contact 
  information, including - when designated - the name and contact information for the person who serves as the
  Primary Contact for that Holder's laboratory.</p>

<div style="width: 620px;"><b>
'Last review date' shows when the most recent update of the list of mice held by each investigator
was carried out.</b>
<br>
<br>To perform a review and notify admin that it has been done,
go to the page listing the mice held by an individual investigator (click on the
number in the last column on the right).
</div>
<br>
<form style='display:inline-block' class='view_opts' action='HolderReport.jsp'>
 Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderby,null) %>
</form>
<form style='margin-left: 5px;display:inline-block' id='search_form'>
    <input type='text'></input><input type='submit' class='btn' value='Search'>
    <a style='display:none' class='btn clear_btn' href='#'>clear search</a></form>
<%= table %>
</div>

<script>
!function($){
  var search_form = $("form#search_form");
  var clear_btn = $("form#search_form a.clear_btn");
  var search_input = $("form#search_form input[type=text]");
  search_form.submit(function(){
    var term = search_input.val();
    var expr = new RegExp(term,'i');
    $("div.facilityTable tr").removeClass('hide');
    if (!term) {
      clear_btn.hide();
      return false;
    }
    clear_btn.show();
    $("div.facilityTable tr.holderlist, div.facilityTable tr.holderlistAlt").each(function(i,row){
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
