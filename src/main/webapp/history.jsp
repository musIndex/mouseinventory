<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.HashMap"%>
<%@page import="edu.ucsf.mousedatabase.DBConnect"%>
<%@page import="java.util.ArrayList"%>
<%@page import="edu.ucsf.mousedatabase.objects.*"%>
<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="static edu.ucsf.mousedatabase.HTMLGeneration.*"%>
<%@ page import="edu.ucsf.mousedatabase.HTMLGeneration" %>


<%=getPageHeader(null, false, false, "onload=\"setFocus('quickSearchForm', 'searchterms')\"")%>
<%=HTMLGeneration.getNavBar("history.jsp", false)%>

<style type="text/css">
</style>
<div class="site_container">
<div>
<h2>UCSF Mouse Inventory Database - a brief history</h2>
<p>The idea for a mutant mouse sharing resource came from a “wouldn’t it be great if” conversation between Dr. Gail Martin (UCSF) 
and Dr. Mary Elizabeth Hatten (Rockefeller University) that took place in 2007, when both were serving on 
The Jackson Laboratory Board of Scientific Overseers.  Martin together with Nick Didkovsky, a developer from Rockefeller Univ., created 
the first version of the Mouse Web App in 2009 with resources provided by Hatten. </p>
<p>
It quickly became apparent that more programming was required to make the site useful. Much gratitude is owed to Dr. Clifford Roberts, then 
head of the UCSF Animal Care Facility, who understood the value of the application for the mouse user
 community. He made funds available to pay Jonathan Scoles, a software engineer with ties to UCSF, for building and maintaining the original database. As participation in
 the Mouse Inventory was voluntary it took an email campaign and active engagement by Dr. Martin presenting the utility of the web app to researchers. All the while
 she coordinated the development of a catalog of mouse mutants at UCSF to populate the database. </p>
 <p>After Martin retired in 2012, the torch was passed to Adriane Joo to be the Mouse Inventory administrator with oversight from faculty member Ophir Klein. She would further
  expand the mouse record list and recruit researchers to participate in this collaborative resource. In 2015 guidance 
  for the Mouse Inventory was handed to Diana Laird and Jeff Bush, as faculty advisors and Estelle Wall took over as administrator. </p>
<p>Throughout the years former UCSF researchers and colleagues from other institutes have inquired about developing their own
 institutional mouse sharing resource. In response, the faculty advisors, administrators, and founders disseminated the design and open source code for this valuable resource on Github and in an accompanying publication in <a href="https://www.g3journal.org/content/10/5/1503">G3Journal-UCSF Mouse Inventory Web App</a></p>
<p>Wall and her team have implemented improvements to the code base (version 2.1).
 In collaboration with Campus Animal Resource at Michigan State University, a rat inventory is being developed on this open source project.
 Suggestions for improvements, new features, or issues may be posted on our repository.</p>
<a href="https://github.com/musIndex/mouseinventory/issues">Mouse Inventory Github Repository</a>
<div>
<h2>Mouse Inventory Resources</h2>
<p>Dr. Martin created these presentations to explain the differences between mutant allele and transgene mice. </p>
<a class="btn btn-success" href="https://mousespace.blob.core.windows.net/$web/MouseMutantsPrimer.pdf">DOWNLOAD MOUSE MUTANTS (pdf)</a>
<p>User Manual that describes Mouse Inventory features.</p>
<a class="btn btn-success" href="https://msurodentdatabasefiles.s3.us-east-2.amazonaws.com/Rodent+Database+User+Manual+2-13-21.pdf">DOWNLOAD USER MANUAL(pdf)</a>
 <h4>Logos Designed by Marta Dansa 2019</h4>
 <div class="search-box search-box-small">
      <img src="<%=imageRoot %>target_mice.png" class="woodmouse"/>
      </div>



</div>
</div>