<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>
<div class="site_container">
<%
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  
  String[] orderOpts = new String[]{"facility","description","count"};
  String[] orderOptLabels = new String[]{"Name","Location","Record count"};
  
  
  if (command == null || command.isEmpty() || command.equals("edit"))
  {
    ArrayList<Facility> facilities = DBConnect.getAllFacilities(false,orderby);
    String table = HTMLGeneration.getFacilityTable(facilities,true);
    %>
    <h2>Edit Facilities:</h2>
    
    <form class='view_opts' action='EditFacilityChooser.jsp'>
    	Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderby,null)  %>
	</form>
	<br>
	<a class='btn' href='#' id='sort_button'>Change public sort order</a>&nbsp;&nbsp;
    <a class='btn btn-success' href="EditFacilityChooser.jsp?command=add"><i class='icon-plus icon-white'></i> Add Facility</a>
    <div class='sort-instructions' style='display:none'><h3>Click and drag rows to reorder them.  Click save when done</h3></div>
    <%= table %>
    <%
  }
  else if (command.equals("add"))
  {
    %>
    <h2>Add New Facility</h2>

    <form action="UpdateFacility.jsp" method="post">
        <table>
            <tr>
                <td>Facility Name: </td>
                <td><input type=text name="facilityName" size=50></td>
            </tr>
            <tr>
                <td>Facility Description: </td>
                <td><input type=text name="facilityDescription" size=50></td>
            </tr>
            <tr>
                <td>Facility Code: </td>
                <td><input type=text name="facilityCode" size=10></td>
            </tr>
            <tr>
                <td colspan="2">
                <input type="hidden" name="command" value="Insert">
                <input type="submit" class="btn btn-success" value="Create Facility"></td>
            </tr>
        </table>
    </form>
    <%
  }
%>
</div>
<script>
!function($){
	var sorting = false;
	var table_body = $("div.facilityTable table tbody");
	var sort_button = $("#sort_button");
	var instructions = $(".sort-instructions");
	sort_button.click(function(){
		sorting = !sorting;
		
		if (sorting) {
			sort_button.text('Save changes');
			instructions.show();
			table_body.sortable().disableSelection();
		}
		else {
			alert("saving order");
			alert("saved");
			window.location.reload();
		}
		
		
	});
	
	


}(jQuery);
</script>