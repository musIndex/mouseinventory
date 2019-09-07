<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditHolderChooser.jsp", true) %>
<%@include file='SendMailForm.jspf' %>
<%

    //todo Order by links
%>
<div class="site_container">
<%
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  
  String[] orderOpts = new String[]{"firstname,lastname","lastname,firstname","department","count","datevalidated","datevalidated desc","primary_mouse_location"};
  String[] orderOptLabels = new String[]{"First name","Last name","Department","Mice held","Last review date","Reverse last review date","Primary location of Colony"};
  
  if (command == null || command.isEmpty() || command.equals("edit"))
  {
      ArrayList<Holder> holders = DBConnect.getAllHolders(false,orderby);
      String table = HTMLGeneration.getHolderTable(holders,true);
    %>
    <h2>Edit Holders:</h2>

	<a class='btn btn-success'href="EditHolderChooser.jsp?command=add"><i class='icon-plus icon-white'></i> Add Holder...</a>
    <br>
    <br>
    <form style='display: inline-block' class='view_opts' action='EditHolderChooser.jsp'>
   	 Sort by <%= genSelect("orderby",orderOpts,orderOptLabels,orderby,null) %>
    </form>
    <form style='margin-left: 5px;display:inline-block' id='search_form'>
    <input type='text'></input><input type='submit' class='btn' value='Search'>
    <a style='display:none' class='btn clear_btn' href='#'>clear search</a></form>
    <%= table %>
    <%
  }
  else if (command.equals("add"))
  {
    %>
    <h2>Add Holder</h2>
    <form action="UpdateHolder.jsp" method="post">
        <table>
            <tr>
                <td>First name</td>
                <td><input type=text name="firstname" size=20></td>
            </tr>
            <tr>
                <td>Last name</td>
                <td><input type=text name="lastname" size=20></td>
            </tr>
            <tr>
                <td>Department</td>
                <td><input type=text name="dept" size="40"></td>
            </tr>
            <tr>
                <td>Email</td>
                <td><input type=text name="email" size=40></td>
            </tr>
            
            <tr>
                <td>Primary Contact Name</td>
                <td><input type=text name="alternateName" size=40 >
                <br><i>The primary contact for communications about the database, appointed by the holder</i></td>
            </tr>
            <tr>
                <td>Primary Contact Email</td>
                <td><input type=text name="alternateEmail" size=40>
                <br><i>This email will be the recipient for email links for this holder in the mouse list,
                       with the holder's email address added as a cc.</i><br>
                       <b>Leave this field blank if the holder is the primary contact.</b></td>
            </tr>
            <tr>
              <td>Primary location of colony</td>
              <td><input type=text name="primaryMouseLocation" size=20 ></td>
            </tr>
            <tr><td colspan=2 align="center">
            <input type="hidden" name="command" value="Insert">
            <input type="submit" class="btn btn-success" value="Create Holder"></td></tr>
        </table>
    </form>
    <%
  }
%>
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
