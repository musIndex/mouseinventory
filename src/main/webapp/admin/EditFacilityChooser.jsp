<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("EditFacilityChooser.jsp", true) %>
<div class="site_container" style='padding-bottom: 100px'>
<%
  String command = request.getParameter("command");
  String orderby = request.getParameter("orderby");
  
  String[] orderOpts = new String[]{"position","facility","count"};
  String[] orderOptLabels = new String[]{"Public-facing","Name","Record count"};
  
  if (orderby == null) {
	  orderby = orderOpts[0];
  }
  
  if (command == null || command.isEmpty() || command.equals("edit"))
  {
    ArrayList<Facility> facilities = DBConnect.getAllFacilities(false,orderby);
    String table = HTMLGeneration.getFacilityTable(facilities,true);
    %>
    <h2>Edit Facilities:</h2>
    <div class='alert' id='top_status_message' style='display:none'></div>
    <form class='view_opts' action='EditFacilityChooser.jsp'>
    	Sort by <%= HTMLGeneration.genSelect("orderby",orderOpts,orderOptLabels,orderby,null,true)  %>
	</form>
	<br>
	<a class='btn' href='#' id='sort_button'>Change public sort order</a>&nbsp;&nbsp;
    <a class='btn btn-success' href="EditFacilityChooser.jsp?command=add"><i class='icon-plus icon-white'></i> Add Facility</a>
    <div class='sort-instructions' style='display:none'><h3>Click and drag rows to reorder them.  Click 'Save changes' when done</h3></div>
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
                <td style='vertical-align:top'>Local experts:<br><i>email address first (if any),<br>one expert per line.</i></td>
                <td><textarea name="localExperts" rows="5" cols="50"></textarea></td>
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
<style>
.dndPlaceHolder {
    background-color:Red ;
    color:Red;
    height: 20px; 
    line-height:30px;
    border: solid 2px black;
  }    

</style>
<script>
!function($){
	var order = '<%=orderby%>';
	var sorting = false;
	var table_body = $("div.facilityTable table tbody");
	var sort_button = $("#sort_button");
	var instructions = $(".sort-instructions");
	var positions = "";
	sort_button.click(function(){
		if (order != "position"){
			updateStatus("To change the public-facing sort order, please choose 'Public-facing' from the sort menu first.");
			return false;
		}
		if (sort_button.hasClass('disabled')){
			return false;
		}
		sorting = !sorting;
		
		if (sorting) {
			clearStatus();
			table_body.addClass('sorting');
			sort_button.text('Save changes');
			instructions.show();
			table_body.sortable({
				placeholder: 'dndPlaceHolder',
				distance:15,
		      	items:'tr', 
		      	forcePlaceholderSize:true, 
		      	change : dndChange,
		      	update : dndUpdate
			}).disableSelection();
		}
		else {
			sort_button.addClass('disabled');
			sort_button.text('Saving...');
			table_body.removeClass('sorting');
			$.ajax({type: 'post',
		          	url: '<%=HTMLGeneration.adminRoot %>UpdateFacilityOrder',
		         	dataType: 'json',
		          	success: updateOrderSuccess,
		         	error: updateOrderFailed,
		          	data: 'positions=' + positions,
		          	async: true
			});
			
		}
		
		
	});
	
	function updateOrderSuccess(data) {
		if (data.success) {
			updateStatus('Order saved',true);
		}
		else{
			updateOrderFailed(data);
		}
		sort_button.removeClass('disabled');
		sort_button.text('Change public sort order');
		instructions.hide();
		
	}
	function updateOrderFailed(data){
		updateStatus('Failed to save changes: ' + data.message,false);
	}
	
	function updateStatus(message,success) {
		$("#top_status_message").text(message)
			.removeClass('alert-success').removeClass('alert-error')
			.addClass(success ? 'alert-success' : 'alert-error')
			.text(message)
			.show();
	}
	
	function clearStatus() {
		$("#top_status_message").hide();
	}
	
	
	function dndChange(event,ui){
		positions = "";
  	}

  	function dndUpdate(event,ui){
  		positions = getSortOrder();
  	}
  	
  	function getSortOrder() {
  		var order = "";
  		$("tbody tr td:first-child").each(function(i,column){
  			if (i > 0) {
  				order += ',';
  			}
  			order += $(column).text() + '-' + i;
  		});
  		return order;
  	}

	dndUpdate();
}(jQuery);
</script>