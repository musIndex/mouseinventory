<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("about.jsp", false) %>
  
<div class="pagecontent">
  <div class="whatsnew">
    <h3>Recent site updates:</h3>
    <div class="alert alert-info" data-date='5/10/2012' data-name='search-updates'>
      <b>Improved searches.</b>
      <br> The search page has been redesigned to be easier to use and more responsive.  
      Additionally, search results should be more accurate.  You'll also see your search terms <span class='highlight-searchterm'>highlighted</span>.
    </div>
    <div class="alert alert-lovely" data-date='4/21/2012' data-name='pdf-downloads'>
      <b>PDF mouse lists:</b>
      <br>
       It is now possible to download a PDF showing the records for all the
       mice listed for an individual holder.  To do this, go to the "Holder List," find the name of the holder,
       and click on the number on the extreme right ("Mice Held"). The page
       that comes up, showing the list of all the mice held, has a button that
       can be clicked to download the list.
    </div>
    <div class='alert alert-success'>
    <b>Site Feedback:</b>
    <br>
    We'd like to hear from you!  Please use the 'Submit Feedback' link at the top of the page
    if you would like to report an issue or have any other comments regarding the database.
    </div>
    <div class='alert alert-warning'>
    <b>Database accuracy:</b><br>
    If you contact a holder and find that s/he is no longer keeping a particular mouse, please inform admin
    by using the 'Submit feedback' link.
    </div>
  </div>
<div class='about'>
<div class='about-summary'>
<h2>About the UCSF Mouse Inventory Database</h2>
<ol>
  <li><a href="#introduction">Introduction</a></li>
  <li><a href="#purpose">Purpose</a></li>
  <li><a href="#how">How mice are listed</a></li>
  <li><a href="#info">Information provided about each mouse</a></li>
  <li><a href="#search">Search and sort functions</a></li>
  <li><a href="#submitting">Submitting mice, adding/removing
  holders, and other changes to information in the inventory</a></li>
  <li><b><a href="#faq">FAQ</a></b></li>
  <li><a href="#disclaimer">Disclaimer</a></li>
</ol>
</div>

<h3 id="introduction">Introduction</h3>
<p>The Mouse Inventory Database is a web application consisting of
Java Server Pages (JSP) running under Nginx/Tomcat, custom Java
libraries, and a MySQL database.  JavaScript must be enabled to access
all features of the database.</p>
<p>The code behind the database is open source.  See the
<a href="licenses.jsp">licenses</a> page for details.</p>
<ul>
  <li><b>Version 1</b>, completed in April 2009, was programmed by
  Nick Didkovsky (Rockefeller University) as specified by Gail Martin
  (UCSF). <br>
  The resources to develop this database were generously provided by Mary
  Elizabeth Hatten, Rockefeller University. <br>
  </li>
  <li><b>Version 2</b> has been developed by Jonathan Scoles, San
  Francisco, CA. <br>
  The most recent update was published in May, 2012.</li>
</ul>
<p></p>



<h3 id="purpose">Purpose</h3>

<p>The purpose of the Mouse Inventory Database is to provide the
university community with a central online resource that describes mice
currently housed at UCSF. <br>
Users can go online with a web browser to determine if mice carrying a
particular genetic alteration or mice of a particular inbred strain are
available in the colony of one or more investigators at the university
and to find out whom to contact about the possibility of obtaining the
mice. Access to this information should save investigators considerable
time and money in acquiring mice, as well as stimulate collaboration
between investigators.</p>
<p>
In addition, for each investigator listed as a 'holder' in the database, a
description of all the mice in his/her colony can be readily obtained
</p>
<br>

<p id="how"><b><font size="3">How mice are
listed in the database</font></b></p>
<p>Each entry (generically referred to as a 'mouse') in the database
is classified in one of three categories:</p>

<dl>
  <dt><b>1) Mutant Allele </b></dt>

  <dd>This category comprises all types of mutations in a known gene or sequence.
  The type of modification is described as: targeted
  disruption, conditional allele, targeted knock-in, gene trap insertion,
  Chemically induced (ENU), spontaneous mutation or other (description
  provided). <br>
  </dd>

  <dt><b>2) Transgene</b></dt>

  <dd>This category comprises transgenes that express a
  particular sequence, and that have been <b>randomly inserted</b> in the genome.
  <br>
  The expressed sequence is described as Cre, Reporter
  (e.g. lacZ, GFP, etc.), mouse gene, or other (e.g. human gene, rat gene, etc.)</dd>
  <dt><b>3) Inbred Strain</b>
  <dd>This category comprises mice whose major genetic characteristic is that they
  are members of a particular inbred strain (mice that are genetically nearly
  identical as a result of extensive inbreeding - usually at least 13 generations).
  These strains are generally purchased from commercial suppliers.  <br></dd>

  </dt>

  <dt>Each entry is for an <span class=red><b>individual</b></span>
  mutant allele or transgene (or inbred strain) - irrespective of whether
  the allele or transgene is maintained in combination with other mutant
  alleles or transgenes. The fact that the mutant allele or transgene is
  not maintained on its own may be noted in the (optional) background field
  or explained to anyone who requests the mice.</dt>
</dl>

<br>
<h3 id="info">Information provided about each mouse</h3>

<p>All entries (records) in the Inventory can be viewed by clicking on 'Mouse
List'. Each record includes the 'informal' mouse name provided
by the person who submitted the entry, the mouse category (Mutant
Allele, Transgene, or Inbred Strain) and the name of the 'holder'
(investigator with an approved protocol in whose colony the mouse line
is housed) as well as the UCSF animal facility in which the mouse is
housed.</p>

<p>When a mutant allele, transgene, or inbred strain is being
maintained by two or more holders, there is only <b>one</b> record for
it, which lists all holders.</p>

<p>For mutant alleles, the type of modification is shown, as well as
the official name and symbol of the gene that is modified. In addition,
the ID number for that gene in the Mouse Genome Informatics (MGI)
database is shown. Clicking on the <span class=red>MGI gene ID</span>
will automatically bring up the page on the MGI site that describes the
gene.</p>

<p>If the mutant allele is a knock-in, the expressed sequence is
described; if it is a mouse gene, the official name, symbol and MGI ID of
that gene is shown, with a link to MGI.</p>

<p>For transgenes, the expressed sequence and regulatory
elements are described; if the expressed sequence is a mouse gene, the
official name, symbol and MGI ID of the gene is shown, with a link to
MGI.</p>

<p>A comment on or brief description of the mutant allele/transgene
may be provided.</p>

<p>For inbred strains, the name of the commercial supplier is
provided, For those obtained from JAX Mice, there is a link to the
description of that strain on the JAX Mice website.</p>

<p>If the mutant allele or transgene has been published, the
record also provides the MGI allele/transgene ID and official symbol for
the mouse, along with the Pubmed ID for the most relevant publication(s)
describing the genetic modifications in the allele/transgene. Clicking
on the <span class=red>MGI allele/transgene ID</span> or the
<span class=red>Pubmed ID</span> will bring up the relevant pages on MGI
or Pubmed, respectively.</p>

<p>When it has been provided, there is information about the
background strain of the mutant allele/transgene.</p>
<br>
<h3 id="search">Sort and Search functions</h3>
<p>On the '<b>Mouse List</b>' page, the mode of display of
records can be selected: view all, or view records in only one of the
three categories. The records can be sorted by Mouse Inventory
Database ID (record) number or Mouse Name.&nbsp;&nbsp;</p>

<p>The '<b>Gene List</b>' provides a list of all mouse genes that
have been entered in the database. There is a link to mice currently in
the Inventory in which that gene is modified (mutant alleles) or in
which that gene is expressed (transgenes).</p>

<p>The '<b>Holder List</b>' provides the names and contact
information for investigators with approved animal protocols who are
maintaining the mice listed in the Inventory in their colonies. The
total number of mice in the Inventory currently being maintained by each
holder is shown. Clicking on that number provides a report showing only
the mice maintained by that holder.</p>

<p>There is a flexible 'Search' function.</p>

<br>
<br>
<h3 id="submitting">Submitting mice, adding or deleting a
holder for a particular mouse, or making other modifications to
information in the Inventory</h3>
<p>Only the administrator(s) of the Mouse Inventory Database can
make changes to the data that are on display in the database.</p>

<p>If you want to add a mouse that is not already in the inventory,
click on 'Submit Mice' at the top of this page, and complete the <b>submission
form</b>.</p>

<p>If a mouse is already in the inventory and you want to add the
name of an additional holder, go to 'Mouse List' and find the record for
that mouse, click on 'Request change in record' (under the mouse name)
and complete the <b>request change form</b>.</p>


<p>If a mouse is no longer being maintained by a particular holder,
the name of that holder should be deleted from the record using the same
procedure as for adding a holder. Note, that if the holder to be deleted
is the only investigator maintaining the mouse, the record for that
mouse will be deleted from the database.</p>

<!-- Uncomment and revise once the endangered list is finalized
<p>"If a holder is considering eliminating a mouse from his/her
colony, it can be marked as "endangered" on the "request change form."
If that holder is the only one who maintains the mouse, or if there is
only one other holder, the mouse will be added to the "endangered mouse"
list, which is a selection on the "Mouse List." The mouse will remain on
the "endangered list" until the holder who classified it as endangered
asks to be deleted as a holder.</p>
-->
<p>Requests for other changes to a record can also be
submitted using the request change form.</p>

<p>When a completed submission or request change form is submitted,
the Inventory Administrator will generate a record for the mouse in the
database or make the requested alterations in holders or other
information about a mouse.</p>
<br>
<br>

<h3 id="faq">Frequently Asked Questions</h3>

<ul>
  <li><a href="faq.jsp">Who has access to the UCSF mouse inventory database?</a></li>
  <li><a href="faq.jsp">Are investigators required to list all the mice in their colonies?</a></li>
  <li><a href="faq.jsp">Does listing a mouse in the database obligate an investigator to provide
  it to other investigators at UCSF?</a></li>
  <li><a href="faq.jsp">Should unpublished mice be entered?</a></li>
  <li><a href="faq.jsp">If a mouse was originally obtained pursuant to a material transfer agreement (MTA),
  or if a mouse was produced at UCSF using materials obtained pursuant to an MTA, can the mouse
  (or its descendants) be passed to another investigator at UCSF without completing a new MTA?</a></li>
</ul>

<br><br>

<h3 id="disclaimer">Disclaimer:</h3>

<p>The accuracy of all information about a mouse is the
responsibility of the individual who submitted the data. Information
provided on submission forms is not vetted by the Administrator.</p>

</div>
</div>