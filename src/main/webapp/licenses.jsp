<%@page contentType="text/html;charset=UTF-8" language="java"%>
<%@page import="edu.ucsf.mousedatabase.HTMLGeneration"%>
<%=HTMLGeneration.getPageHeader(null, false,false,"onload=\"setFocus('quickSearchForm', 'searchterms')\"") %>
<%=HTMLGeneration.getNavBar(null, false) %>

<div class="pagecontent">
<div class="textwrapper">
<h2>Licenses</h2>
The source code is available under the under the <a target="_blank" href="http://www.gnu.org/licenses/agpl-3.0.html">AGPL</a>.
and can be found on <a target="_blank" href="http://ucsf-mousedb.github.com/mouseinventory/">github</a>

<br>
<br>
<h3>Third-party inclusions</h3>
The database includes code from the following open source projects:
<ul>
<li>Apache commons codec</li>
<li>Apache commons fileupload</li>
<li>Apache commons io</li>
<li>Google gson</li>
<li>iText pdf</li>
<li>PostgreSQL</li>
<li>Javacsv</li>
</ul>
</div>
</div>
