<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>

    
<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=getNavBar("history.jsp", false)%>

<style type="text/css">
</style>
<div class="site_container">
<div>
<h3>UCSF Mouse Inventory Database - a brief history</h3>
<p>The idea for a mutant mouse sharing resource came from a “wouldn’t it be great if” conversation between Dr. Gail Martin (UCSF) 
and Dr. Mary Elizabeth Hatten (Rockefeller University) that took place in 2007, when both were serving on 
The Jackson Laboratory Board of Scientific Overseers.  Martin together with Nick Didkovsky, a developer from Rockefeller Univ., created 
the first version of the Mouse Web App in 2009 with resources provided by Hatten. </p>
<p>
It quickly became apparent that more programming was required to make the site useful. Much gratitude is owed to Dr. Clifford Roberts, then 
head of the UCSF Animal Care Facility, who understood the value of the application for the mouse user
 community. He made funds available to pay Jonathan Scoles, a software engineer with ties to UCSF, for 1000 hours between 2009 -2015. As participation in
 the Mouse Inventory was voluntary it took an email campaign and active engagement by Gail presenting the utility of the web app to researchers. All the while
 she coordinated the development of an institutional mouse mutation catalog. </p>
 <p>After Martin retired in 2012, the torch was passed to Adriane Joo to be the Mouse Inventory administrator. She would further
  expand the mouse record list and recruit researchers to participate in this collaborative resource. In 2015 the guidance 
  for the Mouse Inventory was handed to mouse genetics profs, Diana Laird and Jeff Bush, as faculty advisors. </p>
<p>Throughout the years former UCSF researchers and colleagues from other institutes have inquired about developing their own
 institutional mouse sharing resource. Answering this need the Mouse Inventory advisors, administrators, and Gail set out to 
 disseminate the design and open source code for this valuable resource. In May 2020 the details of this successful Mouse 
 Inventory was published in <a href="https://www.g3journal.org/content/10/5/1503">G3Journal-UCSF Mouse Inventory Web App</a></p>
<p>During the manuscript process the code base underwent improvements and further documentation, version 2.1.
 In collaboration with Campus Animal Resource at Michigan State University, a rat inventory is being developed on this open source project.
 If you have suggestions on improving features post an issue on our repository.</p>
<a href="https://github.com/musIndex/mouseinventory/issues">Mouse Inventory Github Repository</a>
<div>

<img src="<%=imageRoot %>target_mice.png" style="margin-left:150px" class="woodmouse"/>
<h4>Logos Designed by Marta Dansa 2019</h4>
</div>

</div>
</div>