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
<%=HTMLGeneration.getNavBar("about.jsp", false)%>
<style type="text/css">
</style>
<div class="site_container">
 <p class="main_header">History</p>
 <p class="description_text">The MSU Rodent Database project was created to provide a searchable online resource
  of mouse and rat research strains and stocks that are available at
  Michigan State University (MSU),
  including inbred, mutant, and genetically engineered strains.
  <br><br>
  The hope is that the MSU Rodent Database will be of great scientific
  assistance to the biomedical community in locating and obtaining rodent
  resources for research. Ideally, this will save time in procuring rodents,
  foster collaborations across laboratories, and be economically advantageous
  through cost savings.
  <br><br>
  Databases of this type have been developed at only a few institutions to our knowledge,
  one of which exists at University of California – San Francisco (UCSF).
  Originally, the UCSF resource took shape in 2009 from collaborative conversations between
  Dr. Gail Martin (UCSF) and Dr. Mary Elizabeth Hatten (Rockefeller),
  when both were serving on The Jackson Laboratory Board of Scientific Overseers.
  Martin, together with Nick Didkovsky, a developer from Rockefeller University,
  created the first version of the Mouse Web App in 2009 with resources provided by Hatten.
  In 2018, the UCSF database representatives, Ms. Estelle Wall and Dr. Carlo Quiñónez,
  graciously shared their expertise, in addition to providing MSU access to their
  database code, provided as an open source on GitHub.
  <br><br>


  This project was made possible by a grant funded through CVM, CHM, CANR, CNS/Nat Sci,
  CSS and the individual MSU departments of Pharmacology & Toxicology, Psychology and
  Physiology. We appreciate the intellectual contributions of our advisory committee,
  including Drs. Amy Ralston, Bryan Copple, Ripla Arora, Greg Fink and Kathy Steece-Collier.
  <br><br>
  In 2020, critical effort was provided by a talented team of MSU
  undergraduates (E. Begalka, J. Willinger, H. Poster, A. Kohl, J. Butler, and C. Cardimen)
  who largely were recruited as part of a Capstone project in course ITM444, under the
  project direction of Dr. F. Claire Hankenson. Development efforts are continuing with
  support from Campus Animal Resources and the Office of Regulatory Affairs.
  MSU and UCSF continue to collaborate on development and features that are designed to
  augment database use at both institutions.</p>
 <br><br>




 <div class="category">
  <div class="three_column_left">
   <img src="/img/Download.svg" class="image-center" style="width: 50%;">
   <br>
   <p class="button_header">Rodent Mutants Explained</p>
   <p class="button_body_text">Learn more about how to use the Michigan State University Rodent Database.</p>
   <div class="MSU_green_button">
    <a class="anchor_no_underline" href="https://msurodentdatabasefiles.s3.us-east-2.amazonaws.com/Mouse+Mutants+Explained.pdf">
     <p class="MSU_green_button_Text">Download</p>
    </a>
   </div>
  </div>


  <div class="three_column_center">
   <img src="/img/Download.svg" class="image-center" style="width: 50%;">
   <br>
   <p class="button_header">Rodent Database User Manual</p>
   <p class="button_body_text">Dr. Martin created these presentations to explain the differences between mutant allele and transgene mice. </p>
   <div class="MSU_green_button">
    <a class="anchor_no_underline" href="https://msurodentdatabasefiles.s3.us-east-2.amazonaws.com/Rodent+Database+User+Manual+2-13-21.pdf">
     <p class="MSU_green_button_Text">Download</p>
    </a>
   </div>
  </div>


  <div class="three_column_right">
   <img src="/img/Github.svg" class="image-center" style="width: 50%;">
   <br>
   <p class="button_header">GitHub Repository</p>
   <p class="button_body_text">Check out the open-source code created in collaboration by MSU and UCSF.</p>
   <div class="MSU_green_button">
    <a class="anchor_no_underline" href="https://github.com/musIndex/mouseinventory/issues">
     <p class="MSU_green_button_Text">GitHub</p>
    </a>
   </div>
  </div>
 </div>

</div>

<%=HTMLGeneration.getWebsiteFooter()%>


