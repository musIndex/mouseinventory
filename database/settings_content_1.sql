# ************************************************************
# Sequel Pro SQL dump
# Version 3348
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.5.13)
# Database: mouse_inventory
# Generation Time: 2012-09-01 06:08:31 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table settings
# ------------------------------------------------------------

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;

INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
VALUES
	(2,'home_page_text_introduction','Introduction','<p>The Mouse Inventory Database is a web application consisting of Java Server Pages (JSP) running under Nginx/Tomcat, custom Java libraries, and a MySQL database. JavaScript must be enabled to access all features of the database.</p><p>The code behind the database is open source. See the&nbsp;<a href=\"/mouseinventory/licenses.jsp\">licenses</a>&nbsp;page for details.</p><ul style=\"margin: 0px; padding-left: 20px; \"><li>Version 1, completed in April 2009, was programmed by Nick Didkovsky (Rockefeller University) as specified by Gail Martin (UCSF).&nbsp;<br>The resources to develop this database were generously provided by Mary Elizabeth Hatten, Rockefeller University.&nbsp;<br></li><li>Version 2&nbsp;has been developed by Jonathan Scoles, San Francisco, CA.&nbsp;<br>The most recent update was published in August, 2012.</li></ul>'),
	(2,'home_page_text_purpose','Purpose','<p>The purpose of the Mouse Inventory Database is to provide the university community with a central online resource that describes mice currently housed at UCSF.&nbsp;<br>Users can go online with a web browser to determine if mice carrying a particular genetic alteration or mice of a particular inbred strain are available in the colony of one or more investigators at the university and to find out whom to contact about the possibility of obtaining the mice. Access to this information should save investigators considerable time and money in acquiring mice, as well as stimulate collaboration between investigators.</p><p>In addition, for each investigator listed as a \'holder\' in the database, a description of all the mice in his/her colony can be readily obtained.</p>'),
	(2,'home_page_text_how_mice_are_listed','How mice are listed','<p>Each entry (generically referred to as a \'mouse\') in the database is classified in one of three categories:</p><dl style=\"margin: 0px; \"><dt>1) Mutant Allele</dt><dd>This category comprises all types of mutations in a known gene or sequence. The type of modification is described as: targeted disruption, conditional allele, targeted knock-in, gene trap insertion, Chemically induced (ENU), spontaneous mutation or other (description provided).&nbsp;<br></dd><dt>2) Transgene</dt><dd>This category comprises transgenes that express a particular sequence, and that have been&nbsp;randomly inserted&nbsp;in the genome.&nbsp;<br>The expressed sequence is described as Cre, Reporter (e.g. lacZ, GFP, etc.), mouse gene, or other (e.g. human gene, rat gene, etc.)</dd><dt>3) Inbred Strain</dt><dd>This category comprises mice whose major genetic characteristic is that they are members of a particular inbred strain (mice that are genetically nearly identical as a result of extensive inbreeding - usually at least 13 generations). These strains are generally purchased from commercial suppliers.</dd><dd>&nbsp;</dd><dt>Each entry is for an&nbsp;<span class=\"red\"><font color=\"#ff0000\">individual</font></span>&nbsp;mutant allele or transgene (or inbred strain) - irrespective of whether the allele or transgene is maintained in combination with other mutant alleles or transgenes. The fact that the mutant allele or transgene is not maintained on its own may be noted in the (optional) background field or explained to anyone who requests the mice.</dt></dl>'),
	(12,'home_page_text_information_about_each_mouse','Information provided about each mouse','<p>All entries (records) in the Inventory can be viewed by clicking on \'Mouse List\'. Each record includes the \'informal\' mouse name provided by the person who submitted the entry, the mouse category (Mutant Allele, Transgene, or Inbred Strain) and the name of the \'holder\' (investigator with an approved protocol in whose colony the mouse line is housed) as well as the UCSF animal facility in which the mouse is housed.</p><p>When a mutant allele, transgene, or inbred strain is being maintained by two or more holders, there is only&nbsp;<b>one</b>&nbsp;record for it, which lists all holders.</p><p>For mutant alleles, the type of modification is shown, as well as the official name and symbol of the gene that is modified. In addition, the ID number for that gene in the Mouse Genome Informatics (MGI) database is shown. Clicking on the&nbsp;<span class=\"red\"><font color=\"#ff0000\">MGI gene ID</font></span>&nbsp;will automatically bring up the page on the MGI site that describes the gene.</p><p>If the mutant allele is a knock-in, the expressed sequence is described; if it is a mouse gene, the official name, symbol and MGI ID of that gene is shown, with a link to MGI.</p><p>For transgenes, the expressed sequence and regulatory elements are described; if the expressed sequence is a mouse gene, the official name, symbol and MGI ID of the gene is shown, with a link to MGI.</p><p>A comment on or brief description of the mutant allele/transgene may be provided.</p><p>For inbred strains, the name of the commercial supplier is provided, For those obtained from JAX Mice, there is a link to the description of that strain on the JAX Mice website.</p><p>If the mutant allele or transgene has been published, the record also provides the MGI allele/transgene ID and official symbol for the mouse, along with the Pubmed ID for the most relevant publication(s) describing the genetic modifications in the allele/transgene. Clicking on the&nbsp;<span class=\"red\"><font color=\"#ff0000\">MGI allele/transgene ID</font></span>&nbsp;or the&nbsp;<span class=\"red\"><font color=\"#ff0000\">Pubmed ID</font></span>&nbsp;will bring up the relevant pages on MGI or Pubmed, respectively.</p><p>When it has been provided, there is information about the background strain of the mutant allele/transgene.</p>'),
	(2,'home_page_text_search_and_sort','Seach and sort functions','<p>On the \'<b>Mouse Records</b>\' page, the mode of display of records can be selected: view all, or view records in only one of the three categories. The records can be sorted by Mouse Inventory Database ID (record) number or Mouse Name.&nbsp;&nbsp;</p><p>The \'<b>Gene List</b>\' provides a list of all mouse genes that have been entered in the database. There is a link to mice currently in the Inventory in which that gene is modified (mutant alleles) or in which that gene is expressed (transgenes).</p><p>The \'<b>Holder List</b>\' provides the names and contact information for investigators with approved animal protocols who are maintaining the mice listed in the Inventory in their colonies. The total number of mice in the Inventory currently being maintained by each holder is shown. Clicking on that number provides a report showing only the mice maintained by that holder.</p><p>There is a flexible \'Search\' function.</p>'),
	(2,'home_page_text_submitting_mice','Submitting mice, adding or deleting a holder for a particular mouse, or making other modifications to information in the Inventory','<p>Only the administrator(s) of the Mouse Inventory Database can make changes to the data that are on display in the database.</p><p>If you want to add a mouse that is not already in the inventory, click on \'Submit Mice\' at the top of this page, and complete the&nbsp;<b>submission form</b>.</p><p>If a mouse is already in the inventory and you want to add the name of an additional holder, go to \'Mouse Records\' and find the record for that mouse, click on \'Request change in record\' (under the mouse name) and complete the<b>&nbsp;request change form</b>.</p><p>If a mouse is no longer being maintained by a particular holder, the name of that holder should be deleted from the record using the same procedure as for adding a holder. Note, that if the holder to be deleted is the only investigator maintaining the mouse, the record for that mouse will be deleted from the database.</p><p>Requests for other changes to a record can also be submitted using the request change form.</p><p>When a completed submission or request change form is submitted, the Inventory Administrator will generate a record for the mouse in the database or make the requested alterations in holders or other information about a mouse.</p>'),
	(3,'','Database accuracy','<div><span style=\"font-family: Arial, Verdana; font-size: small; \">If you contact a holder and find that s/he is no longer keeping a particular mouse, please inform admin&nbsp;</span><span style=\"font-family: Arial, Verdana; font-size: small; \">by using the \'Submit feedback\' link.</span></div>'),
	(3,'','Site Feedback',"We\'d like to hear from you! Please use the \'Submit Feedback\' link at the top of the page if you would like to report an issue or have any other comments regarding the database"),
	(3,'','PDF mouse lists','It is now possible to download a PDF showing the records for all the mice listed for an individual holder. To do this, go to the \"Holder List,\" find the name of the holder, and click on the number on the extreme right (\"Mice Held\"). The page that comes up, showing the list of all the mice held, has a button that can be clicked to download the list.'),
	(3,'','Improved searches',"<span style=\"font-weight: normal; \">The search page has been redesigned to be easier to use and more responsive. Additionally, search results should be more accurate. You\'ll also see your search terms </span><b><font color=\"#cc33cc\">highlighted</font></b>."),
	(4,'admin_info_name','Administrator full name','Adriane Joo, Ph.D'),
	(4,'admin_info_email','Administrator email','admin.mousedatabase@ucsf.edu'),
	(4,'admin_info_auto_sub_firstname','Auto-fill submission first name','Adriane'),
	(4,'admin_info_auto_sub_lastname','Auto-fill submission last name','Joo'),
	(4,'admin_info_auto_sub_email','Auto-fill submission email','admin.mousedatabase@ucsf.edu'),
	(4,'admin_info_auto_sub_department','Auto-fill submission department','Database administration'),
	(4,'admin_info_auto_sub_telephone','Auto-fill submission telephone','123-456'),
	(4,'admin_info_auto_sub_holder','Auto-fill submission holder (Last, First)','Martin, Gail'),
	(4,'admin_info_auto_sub_facility','Auto-fill submission facility','MB-RH'),
	(5,'general_site_name','Website Name','UCSF Mouse Inventory Database'),
	(5,'general_site_alert','Alert (dispalyed at top of all pages unless blank)',''),
	(6,'import_ignored_jax_numbers','Ignored JAX catalog numbers (comma separated, e.g. 000123,000456)','');

/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
