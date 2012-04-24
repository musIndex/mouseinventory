<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration" %>
<%=HTMLGeneration.getPageHeader(null,false,false, "onload=\"setFocus('searchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar("search.jsp", false) %>

<div class="pagecontent">
<p>
    <b><font size="3">Search for Mice</font></b>
  </p>

<font size="2">
    Enter search terms separated by spaces. <br>

</font>

<FORM  id="searchForm" ACTION="handlemousesearch.jsp" METHOD="POST">
    <INPUT TYPE="TEXT" size="50" NAME="searchterms" VALUE="">
    <INPUT class="searchButton" TYPE="SUBMIT" VALUE="Search">
</form>

Use "-" before a term to exclude it. Enclose exact phrase in quotes. Punctuation marks such as ,.?! will be ignored.<br><br>
<b>Examples:</b><br>
cre hedgehog
<dl><dd>Search for all records which <b>include both</b> cre <b>and</b> hedgehog</dd></dl>

cre -hedgehog
<dl><dd>Search for all records which <b>include</b> cre and do <b>NOT include</b> hedgehog</dd></dl>

"fibroblast growth factor 10"
<dl><dd>Search for all records which include the <b>exact phrase</b> fibroblast growth factor 10. Compare to unquoted search
    for same phrase which also returns <b>fibroblast growth factor</b> 9, MGI <b>10</b>4723 (Fgf9)
</dd></dl>

<b>IMPORTANT:</b> You may limit the search to mouse type by adding "Mutant Allele", "Transgenic", or "Inbred Strain" to your search terms.
</div>
