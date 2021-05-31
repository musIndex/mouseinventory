<%@ page import="edu.ucsf.mousedatabase.objects.*" %>
<%@ page import="edu.ucsf.mousedatabase.*" %>
<%@ page import="java.util.ArrayList" %>
<%=HTMLGeneration.getPageHeader(null, false, false)%>
<%=HTMLGeneration.getNavBar("FacilityReport.jsp", false)%>

<%
  ArrayList<Facility> facilities = DBConnect.getAllFacilities(false);
  String table = HTMLGeneration.getFacilityTable(facilities,false);
  String adminEmail = DBConnect.loadSetting("admin_info_email").value;
%>
<div class="site_container">
<p class="main_header">Facility List</p>
<%=table%>
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

<div class="spacing_div"></div>

<div class="MSU_footer">
  <p class="MSU_footer_text">
    MSU Rodent Database 2021 Version 3.2 <br>
    Developed by University of California, San Francisco (UCSF)<br>

    <a class="anchor_no_underline" href="mailto:ORA.MSURodentDatabase@msu.edu" style="text-decoration: underline">Contact Administrator</a><br>
    <a class="anchor_no_underline" href="https://github.com/musIndex/mouseinventory" style="text-decoration: underline">View in GitHub</a><br>
  </p>
</div>