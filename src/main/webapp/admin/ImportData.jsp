<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%@page import="edu.ucsf.mousedatabase.servlets.*"%>
<%@page import="edu.ucsf.mousedatabase.dataimport.*"%>
<%=HTMLGeneration.getPageHeader(null,false,true) %>
<%=HTMLGeneration.getNavBar("ImportData.jsp", true) %>

<script type='text/javascript'>
$(document).ready(function(){
  $(".site_container").on('click','.import_section',function(){
    $(this).removeClass("obscured").siblings().addClass("obscured");
  });
  $(".site_container").on('click','input[type=submit]',function(){
    var form = $(this).closest('form');
    var errors = "";
    form.find('input').each(function(i,f) {
      var field = $(f);
      if (field.data('validate') === 'notempty' && !field.val()) {
      	errors += field.data('title') + ' is a required field\n';
      }
    });
  	if (errors) {
  	  alert(errors);
  	}
    return errors.length == 0;
  });
});

</script>


<div class="site_container">

<%
for(ImportDefinition importReportDefinition : ImportHandler.getImportDefinitions() )
{
  %>
  <div class='import_section'>
  <form action="<%=HTMLGeneration.adminRoot %>Import"
    enctype="multipart/form-data" method="post">
    <h3 class='upload-<%=importReportDefinition.ShortName%>'><%=importReportDefinition.Name %></h3>
    <p><%=importReportDefinition.Description %></p>
    <table>
      <tr>
        <td>Report Name:</td>
        <td><input type="text" data-validate='notempty' data-title='Report Name' name="<%=ImportServlet.importDescriptionFieldName %>" size="30"></td>
      </tr>
      <tr>
        <td>Input file:</td>
        <td><input type="file" data-validate='notempty' data-title='Input file' name="<%=ImportServlet.fileFieldName %>" size="75">
        </td>
      </tr>
      <tr>
        <td colspan="2">
        <input type="hidden" name="<%=ImportServlet.importDefinitionIdFieldName %>" size="30" value="<%=importReportDefinition.Id %>">
        <input type="submit" class="btn btn-primary" value="Upload"></td>
      </tr>
    </table>
  </form>
  </div>
<%
}
%>

</div>
