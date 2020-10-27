# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.5.5-10.5.6-MariaDB)
# Database: mouse_inventory
# Generation Time: 2020-10-27 16:39:12 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table changerequest
# ------------------------------------------------------------

DROP TABLE IF EXISTS `changerequest`;

CREATE TABLE `changerequest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mouse_id` int(11) DEFAULT NULL,
  `firstname` varchar(128) DEFAULT NULL,
  `lastname` varchar(128) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `status` enum('new','pending','done') DEFAULT 'new',
  `user_comment` text DEFAULT NULL,
  `admin_comment` text DEFAULT NULL,
  `requestdate` date DEFAULT NULL,
  `lastadmindate` date DEFAULT NULL,
  `properties` text DEFAULT NULL,
  `facility_id` int(11) DEFAULT NULL,
  `facility_name` varchar(255) DEFAULT NULL,
  `holder_email` varchar(255) DEFAULT NULL,
  `holder_name` varchar(255) DEFAULT NULL,
  `holder_id` int(11) DEFAULT NULL,
  `action_requested` int(11) DEFAULT NULL,
  `request_source` varchar(255) DEFAULT NULL,
  `cryo_live_status` varchar(255) DEFAULT NULL,
  `genetic_background_info` text DEFAULT NULL,
  `new_files` text DEFAULT NULL,
  `delete_files` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `changerequest` WRITE;
/*!40000 ALTER TABLE `changerequest` DISABLE KEYS */;

INSERT INTO `changerequest` (`id`, `mouse_id`, `firstname`, `lastname`, `email`, `status`, `user_comment`, `admin_comment`, `requestdate`, `lastadmindate`, `properties`, `facility_id`, `facility_name`, `holder_email`, `holder_name`, `holder_id`, `action_requested`, `request_source`, `cryo_live_status`, `genetic_background_info`, `new_files`, `delete_files`)
VALUES
	(1,2,'estelle','wall','adminDB@ucsf.edu','done','','','2019-09-10','2019-09-10','',4,'','','',2,2,'Change request form','Live only','',NULL,NULL),
	(2,4,'','','','pending','Auto-generated change request\nNew Holder Email: jbush@ucsf.edu\nRecipient lasntame: \ndueDateRaw: Mon Oct 07 01:12:13 PDT 2019\nDue Date: Monday, October 07\nRecipient email: \nRecipient: \nRecipient firstname: \nOriginal holder: Adriane Joo\n','','2019-09-23','2019-09-23','New Holder Email=jbush@ucsf.edu	Recipient lasntame=	dueDateRaw=Mon Oct 07 01:12:13 PDT 2019	Due Date=Monday, October 07	Recipient email=	Recipient=	Recipient firstname=	Original holder=Adriane Joo	',4,'','','',2,1,'Transfer Data Upload (TDU) Transfer Report','','',NULL,NULL),
	(3,1,'','','','pending','Auto-generated change request\nNew Holder Email: dlaird@ucsf.edu\nRecipient lasntame: \ndueDateRaw: Mon Oct 07 01:12:13 PDT 2019\nDue Date: Monday, October 07\nRecipient email: \nRecipient: \nRecipient firstname: \nOriginal holder: Jeff Bush\n','','2019-09-23','2019-09-23','New Holder Email=dlaird@ucsf.edu	Recipient lasntame=	dueDateRaw=Mon Oct 07 01:12:13 PDT 2019	Due Date=Monday, October 07	Recipient email=	Recipient=	Recipient firstname=	Original holder=Jeff Bush	',2,'','','',3,1,'Transfer Data Upload (TDU) Transfer Report','','',NULL,NULL),
	(4,5,'','','','pending','Auto-generated change request\nNew Holder Email: ajoo@ucsf.edu\nRecipient lasntame: \ndueDateRaw: Mon Oct 07 01:12:13 PDT 2019\nDue Date: Monday, October 07\nRecipient email: \nRecipient: \nRecipient firstname: \nOriginal holder: Diana Laird\n','','2019-09-23','2019-09-23','New Holder Email=ajoo@ucsf.edu	Recipient lasntame=	dueDateRaw=Mon Oct 07 01:12:13 PDT 2019	Due Date=Monday, October 07	Recipient email=	Recipient=	Recipient firstname=	Original holder=Diana Laird	',3,'','','',4,1,'Transfer Data Upload (TDU) Transfer Report','','',NULL,NULL),
	(6,6,'Estelle','Wall','ewall@ucsf.edu','new','','','2019-09-23',NULL,'',4,'','','',2,5,'Change request form','Cryo only','',NULL,NULL),
	(7,1,'Estelle','Wall','ewall@ucsf.edu','new','','','2019-09-23',NULL,'',2,'','','',5,2,'Change request form','Live only','',NULL,NULL),
	(9,3,'Jeff ','Bush','jbush@ucsf.edu','new','','','2019-10-17',NULL,'',4,'','','',2,5,'Change request form','Cryo only','',NULL,NULL);

/*!40000 ALTER TABLE `changerequest` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table email_templates
# ------------------------------------------------------------

DROP TABLE IF EXISTS `email_templates`;

CREATE TABLE `email_templates` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `emailType` varchar(255) DEFAULT NULL,
  `subject` text DEFAULT NULL,
  `body` text DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `date_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table emails
# ------------------------------------------------------------

DROP TABLE IF EXISTS `emails`;

CREATE TABLE `emails` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recipients` text DEFAULT NULL,
  `ccs` text DEFAULT NULL,
  `bccs` text DEFAULT NULL,
  `emailType` varchar(255) DEFAULT NULL,
  `subject` text DEFAULT NULL,
  `body` text DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `date_created` timestamp NOT NULL DEFAULT current_timestamp(),
  `category` varchar(255) DEFAULT NULL,
  `template_name` varchar(255) DEFAULT NULL,
  `attachment_names` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table expressedsequence
# ------------------------------------------------------------

DROP TABLE IF EXISTS `expressedsequence`;

CREATE TABLE `expressedsequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `expressedsequence` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `expressedsequence` WRITE;
/*!40000 ALTER TABLE `expressedsequence` DISABLE KEYS */;

INSERT INTO `expressedsequence` (`id`, `expressedsequence`)
VALUES
	(1,'Mouse Gene (unmodified)'),
	(2,'Cre'),
	(3,'reporter'),
	(4,'Modified mouse gene or Other');

/*!40000 ALTER TABLE `expressedsequence` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table facility
# ------------------------------------------------------------

DROP TABLE IF EXISTS `facility`;

CREATE TABLE `facility` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `facility` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `code` varchar(50) DEFAULT NULL,
  `position` int(10) DEFAULT NULL,
  `local_experts` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `facility` WRITE;
/*!40000 ALTER TABLE `facility` DISABLE KEYS */;

INSERT INTO `facility` (`id`, `facility`, `description`, `code`, `position`, `local_experts`)
VALUES
	(4,'Core 1','Main Campus Vivarium','C-1',NULL,'jbush@ucsf.edu'),
	(2,'Core 2','Hospital Vivarium','C-2',NULL,'ewall@ucsf.edu'),
	(3,'Core 3','Institute Vivarium','C-3',NULL,''),
	(1,'Core ','Vivarium','C',NULL,'');

/*!40000 ALTER TABLE `facility` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table flattened_mouse_search
# ------------------------------------------------------------

DROP TABLE IF EXISTS `flattened_mouse_search`;

CREATE TABLE `flattened_mouse_search` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mouse_id` int(11) DEFAULT NULL,
  `searchtext` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `searchtext` (`searchtext`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `flattened_mouse_search` WRITE;
/*!40000 ALTER TABLE `flattened_mouse_search` DISABLE KEYS */;

INSERT INTO `flattened_mouse_search` (`id`, `mouse_id`, `searchtext`)
VALUES
	(141,3,'Cre :: Cre recombinase is expressed in early embryo. A single copy of the transgene was inserted into intron 4, and resulted in intronic deletions of 2.2, 7.3, 3.2 kb around integration site. [URL]http://www.informatics.jax.org/marker/MGI:1919410[/URL][link]GeneMGI:1919410[/link] :: 3 :: beta-actin-Cre :: transgene insertion 2, Gail R Martin :: sequences from human beta-actin gene :: 2176050 :: Tmem163Tg(ACTB-cre)2Mrt :: Tmem163<Tg(ACTB-cre)2Mrt> :: Bush Core 1 :: Laird Core 2 :: 28053125  :: 9598348 '),
	(116,5,'87904 :: actin, beta :: LoxP sites flank exons 2 and 3. :: Actb :: conditional allele (loxP/frt) :: 5 :: Actb flox :: targeted mutation 1.1, James M Ervasti :: 4881824 :: Actbtm1.1Erv :: Actb<tm1.1Erv> :: Laird Core 2 :: 20976199 '),
	(143,2,'Maintained with 4get (Il4-GFP) Record #1 (Bush) :: 1914059 :: keratin 20 :: Cre/ERT2 knock-in mutation was inserted before the stop codon.\r\n[URL]https://www.gudmap.org/chaise/record/#2/Gene_Expression:Specimen/RID=N-GZ5G[/URL][link]www.gudmap.org specimen[/link] :: Krt20 :: endonuclease-mediated :: 2 :: Krt20-Cre/ERT2 :: endonuclease-mediated mutation 1, Andrew P McMahon :: Cre/ERT2 :: 5903002 :: Krt20em1(cre/ERT2)Amc :: Krt20<em1(cre/ERT2)Amc> :: Bush Core 1 :: Joo Core  :: unpublished'),
	(46,4,'Breed siblings to maintain inbred strain. :: 4 :: 129X1/SvJ :: JAX Mice, 000691 :: Joo Core 3'),
	(111,1,'C57BL/6 (Wall), maintained with Krt20-Cre/ERT2 Record #2 (Bush) :: 96556 :: interleukin 4 :: IRES-EGFP and BGH polyA sequences were inserted after the stop codon in exon 4.  :: Il4 :: targeted knock-in :: 1 :: 4get (Il4-GFP) :: targeted mutation 1, Richard M Locksley :: EGFP :: 2176574 :: Il4tm1Lky :: Il4<tm1Lky> :: Bush Core 1 :: Wall Core 2 :: 20023653 ');

/*!40000 ALTER TABLE `flattened_mouse_search` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table gene
# ------------------------------------------------------------

DROP TABLE IF EXISTS `gene`;

CREATE TABLE `gene` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fullname` varchar(250) DEFAULT NULL,
  `symbol` varchar(25) DEFAULT NULL,
  `entrez_gene_id` varchar(32) DEFAULT '',
  `mgi` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `gene` WRITE;
/*!40000 ALTER TABLE `gene` DISABLE KEYS */;

INSERT INTO `gene` (`id`, `fullname`, `symbol`, `entrez_gene_id`, `mgi`)
VALUES
	(2,'keratin 20','Krt20','',1914059),
	(3,'actin, beta','Actb','',87904),
	(5,'interleukin 4','Il4','',96556);

/*!40000 ALTER TABLE `gene` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table holder
# ------------------------------------------------------------

DROP TABLE IF EXISTS `holder`;

CREATE TABLE `holder` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(80) DEFAULT NULL,
  `lastname` varchar(80) DEFAULT NULL,
  `department` varchar(80) DEFAULT NULL,
  `email` varchar(80) DEFAULT NULL,
  `tel` varchar(80) DEFAULT NULL,
  `datevalidated` date DEFAULT NULL,
  `validation_comment` text DEFAULT NULL,
  `alternate_email` varchar(80) DEFAULT NULL,
  `alternate_name` varchar(80) DEFAULT NULL,
  `status` varchar(255) DEFAULT 'active',
  `primary_mouse_location` varchar(255) DEFAULT NULL,
  `is_deadbeat` tinyint(4) DEFAULT NULL,
  `validation_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `holder` WRITE;
/*!40000 ALTER TABLE `holder` DISABLE KEYS */;

INSERT INTO `holder` (`id`, `firstname`, `lastname`, `department`, `email`, `tel`, `datevalidated`, `validation_comment`, `alternate_email`, `alternate_name`, `status`, `primary_mouse_location`, `is_deadbeat`, `validation_status`)
VALUES
	(1,'','','','','',NULL,NULL,NULL,'','active','N/A',NULL,NULL),
	(2,'Jeff','Bush','Cell Biology','jbush@ucsf.edu','','2019-01-20',NULL,'','Holder','active','Core 1',0,''),
	(3,'Diana','Laird','Stem Cell','dlaird@ucsf.edu','','2019-06-15',NULL,'ewall@ucsf.edu','estelle wall','active','Core 2',0,''),
	(4,'Adriane','Joo','Medicine','ajoo@ucsf.edu','','2019-08-04',NULL,'','Holder','active','Core 3',0,''),
	(5,'Estelle','Wall','Animal Care','ewall@ucsf.edu','',NULL,NULL,'','Holder','active','Core 2',0,'new holder');

/*!40000 ALTER TABLE `holder` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table import_new_objects
# ------------------------------------------------------------

DROP TABLE IF EXISTS `import_new_objects`;

CREATE TABLE `import_new_objects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `import_report_id` bigint(20) DEFAULT 0,
  `object_id` bigint(20) DEFAULT 0,
  `object_data` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `import_new_objects` WRITE;
/*!40000 ALTER TABLE `import_new_objects` DISABLE KEYS */;

INSERT INTO `import_new_objects` (`id`, `import_report_id`, `object_id`, `object_data`)
VALUES
	(1,3,2,NULL),
	(2,3,3,NULL),
	(3,3,4,NULL),
	(4,4,3031,NULL),
	(5,4,3032,NULL),
	(6,4,3033,NULL),
	(7,4,3034,NULL),
	(8,4,3035,NULL),
	(9,4,3036,NULL),
	(10,4,3037,NULL),
	(11,5,5,NULL),
	(12,6,3039,NULL),
	(13,7,3040,NULL),
	(14,10,3042,NULL);

/*!40000 ALTER TABLE `import_new_objects` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table import_reports
# ------------------------------------------------------------

DROP TABLE IF EXISTS `import_reports`;

CREATE TABLE `import_reports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `creationdate` date DEFAULT NULL,
  `reporttext` mediumtext DEFAULT NULL,
  `report_type` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `import_reports` WRITE;
/*!40000 ALTER TABLE `import_reports` DISABLE KEYS */;

INSERT INTO `import_reports` (`id`, `name`, `creationdate`, `reporttext`, `report_type`)
VALUES
	(1,'Transfer Report','2019-09-23','<h3>Strain names without record number (3)</h3><div class=\'description\'></div><div class=\'reportBody\'>\r\n<div class=\'reportEntry\'><span class=\'importAction\'>Ignored strain -4, transfer from Adriane Joo to Jeff Bush</span><span class=\'rawRecord\'><br>Raw data:strain=-4 current status= notes= sender phone= recipient name= req no=T069755 recipient email= cryo= final destination=C-1 arrived= sender name=Adriane Joo recipient phone= pi (recipient)=Bush, Jeff info available= recipient pi email=jbush@ucsf.edu db consult= order placed=5/26/19 mgi id= qty=1 received at final destination=6/6/19 official symbol= sender email=ajoo@ucsf.edu pi (sender)=Joo, Adriane </span></div><div class=\'reportEntryAlt\'><span class=\'importAction\'>Ignored strain -1, transfer from Jeff Bush to Diana Laird</span><span class=\'rawRecord\'><br>Raw data:strain=-1 current status= notes= sender phone= recipient name= req no=T069691 recipient email= cryo= final destination=C-2 arrived= sender name=Jeff Bush recipient phone= pi (recipient)=Laird, Diana info available= recipient pi email=dlaird@ucsf.edu db consult= order placed=5/16/19 mgi id= qty=2 received at final destination=6/5/19 official symbol= sender email=jbush@ucsf.edu pi (sender)=Bush, Jeff </span></div><div class=\'reportEntry\'><span class=\'importAction\'>Ignored strain -5, transfer from Diana Laird to Adriane Joo</span><span class=\'rawRecord\'><br>Raw data:strain=-5 current status= notes= sender phone= recipient name= req no=T069513 recipient email= cryo= final destination=C-3 arrived= sender name=Diana Laird recipient phone= pi (recipient)=Joo, Adriane info available= recipient pi email=ajoo@ucsf.edu db consult= order placed=4/30/19 mgi id= qty=1 received at final destination=5/9/19 official symbol= sender email=dlaird@ucsf.edu pi (sender)=Laird, Diana </span></div></div>\r\n<br>',1),
	(2,'Transfer Report','2019-09-23','<h3>Strain names without record number (3)</h3><div class=\'description\'></div><div class=\'reportBody\'>\r\n<div class=\'reportEntry\'><span class=\'importAction\'>Ignored strain -4, transfer from Adriane Joo to Jeff Bush</span><span class=\'rawRecord\'><br>Raw data:strain=-4 current status= notes= sender phone= recipient name= req no=T069755 recipient email= cryo= final destination=C-1 arrived= sender name=Adriane Joo recipient phone= pi (recipient)=Bush, Jeff info available= recipient pi email=jbush@ucsf.edu db consult= order placed=5/26/19 mgi id= qty=1 received at final destination=6/6/19 official symbol= sender email=ajoo@ucsf.edu pi (sender)=Joo, Adriane </span></div><div class=\'reportEntryAlt\'><span class=\'importAction\'>Ignored strain -1, transfer from Jeff Bush to Diana Laird</span><span class=\'rawRecord\'><br>Raw data:strain=-1 current status= notes= sender phone= recipient name= req no=T069691 recipient email= cryo= final destination=C-2 arrived= sender name=Jeff Bush recipient phone= pi (recipient)=Laird, Diana info available= recipient pi email=dlaird@ucsf.edu db consult= order placed=5/16/19 mgi id= qty=2 received at final destination=6/5/19 official symbol= sender email=jbush@ucsf.edu pi (sender)=Bush, Jeff </span></div><div class=\'reportEntry\'><span class=\'importAction\'>Ignored strain -5, transfer from Diana Laird to Adriane Joo</span><span class=\'rawRecord\'><br>Raw data:strain=-5 current status= notes= sender phone= recipient name= req no=T069513 recipient email= cryo= final destination=C-3 arrived= sender name=Diana Laird recipient phone= pi (recipient)=Joo, Adriane info available= recipient pi email=ajoo@ucsf.edu db consult= order placed=4/30/19 mgi id= qty=1 received at final destination=5/9/19 official symbol= sender email=dlaird@ucsf.edu pi (sender)=Laird, Diana </span></div></div>\r\n<br>',1),
	(3,'Transfer Report','2019-09-23','<h3>Newly Created Change Requests (3) (<a class=\'view_link\' href=\'/admin/ManageChangeRequests.jsp?status=all&requestSource=Transfer Report\'>view requests</a>)</h3><div class=\'description\'></div><div class=\'reportBody\'>\r\n<div class=\'reportEntry\'><span class=\'importAction\'>Created change request <span class=\'changerequest_number\'>#2</span>: Add Jeff Bush to 129X1/SvJ #4.</span>  (Transferred from Adriane Joo; Recipient: )<span class=\'rawRecord\'><br>Raw data:strain=4 current status= notes= sender phone= recipient name= req no=T069755 recipient email= cryo= final destination=C-1 arrived= sender name=Adriane Joo recipient phone= pi (recipient)=Bush, Jeff info available= recipient pi email=jbush@ucsf.edu db consult= order placed=5/26/19 mgi id= qty=1 received at final destination=6/6/19 official symbol= sender email=ajoo@ucsf.edu pi (sender)=Joo, Adriane </span></div><div class=\'reportEntryAlt\'><span class=\'importAction\'>Created change request <span class=\'changerequest_number\'>#3</span>: Add Diana Laird to R26-LSL-ZsGreen (Ai6) #1.</span>  (Transferred from Jeff Bush; Recipient: )<span class=\'rawRecord\'><br>Raw data:strain=1 current status= notes= sender phone= recipient name= req no=T069691 recipient email= cryo= final destination=C-2 arrived= sender name=Jeff Bush recipient phone= pi (recipient)=Laird, Diana info available= recipient pi email=dlaird@ucsf.edu db consult= order placed=5/16/19 mgi id= qty=2 received at final destination=6/5/19 official symbol= sender email=jbush@ucsf.edu pi (sender)=Bush, Jeff </span></div><div class=\'reportEntry\'><span class=\'importAction\'>Created change request <span class=\'changerequest_number\'>#4</span>: Add Adriane Joo to Actb flox #5.</span>  (Transferred from Diana Laird; Recipient: )<span class=\'rawRecord\'><br>Raw data:strain=5 current status= notes= sender phone= recipient name= req no=T069513 recipient email= cryo= final destination=C-3 arrived= sender name=Diana Laird recipient phone= pi (recipient)=Joo, Adriane info available= recipient pi email=ajoo@ucsf.edu db consult= order placed=4/30/19 mgi id= qty=1 received at final destination=5/9/19 official symbol= sender email=dlaird@ucsf.edu pi (sender)=Laird, Diana </span></div></div>\r\n<br>',1),
	(4,'Purchase Report','2019-09-23','<h3>Newly Created Submissions (7) (<a class=\'view_link\' href=\'/admin/ListSubmissions.jsp?status=all&submissionSource=Purchase Report\'>view submissions</a>)</h3><div class=\'description\'>creates a draft submission for a mouse that is not yet listed in the database, with the name of a holder whose lab received the mouse</div><div class=\'reportBody\'>\r\n<div class=\'reportEntry\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3031</span>:   E2f1&lt;Tg(Wnt1-cre)2Sor&gt;</span> - Purchased from Jax Mice, catalog #22137 by Jeff Bush for Jeff Bush <span class=\'rawRecord\'><br>Raw data:strain=129S4.Cg-E2f1Tg(Wnt1-cre)2Sor/J stock no=22137 notes= req no=1 purchaser phone= cryo= purchaser name=Bush, Jeff final destination=Core 1 arrived=1/8/19 purchaser email=jbush@ucsf.edu pi (recipient)=Bush, Jeff recipient pi email=jbush@ucsf.edu db consult= vendor=Jackson Laboratory order placed=12/13/18 mgi id= qty=1 received at final destination=1/8/19 official symbol= </span></div><div class=\'reportEntryAlt\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3032</span>:   Tg(Nes-cre/ERT2)KEisc</span> - Purchased from Jax Mice, catalog #16261 by Jeff Bush for Jeff Bush <span class=\'rawRecord\'><br>Raw data:strain=C57BL/6-Tg(Nes-cre/ERT2)KEisc/J stock no=16261 notes= req no=5 purchaser phone= cryo= purchaser name=Bush, Jeff final destination=Core 1 arrived=1/8/19 purchaser email=jbush@ucsf.edu pi (recipient)=Bush, Jeff recipient pi email=jbush@ucsf.edu db consult= vendor=Jackson Laboratory order placed=12/13/18 mgi id= qty=1 received at final destination=1/8/19 official symbol= </span></div><div class=\'reportEntry\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3033</span>:   Vip&lt;tm1(cre)Zjh&gt;</span> - Purchased from Jax Mice, catalog #10908 by Estelle Wall for Estelle Wall <span class=\'rawRecord\'><br>Raw data:strain=Vip-IRES-cre stock no=10908 notes= req no=8 purchaser phone= cryo= purchaser name=Wall, Estelle final destination=Core 2 arrived=1/15/19 purchaser email=ewall@ucsf.edu pi (recipient)=Wall, Estelle recipient pi email=ewall@ucsf.edu db consult= vendor=Jackson Laboratory order placed=1/8/19 mgi id=4431361 qty=5 received at final destination=1/15/19 official symbol= </span></div><div class=\'reportEntryAlt\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3034</span>:   Cx3cr1&lt;tm1Litt&gt;</span> - Purchased from Jax Mice, catalog #5582 by Adriane Joo for Adriane Joo <span class=\'rawRecord\'><br>Raw data:strain=B6.129P-Cx3cr1tm1Litt/J stock no=5582 notes= req no=3 purchaser phone= cryo= purchaser name=Joo, Adriane final destination=Core 3 arrived=2/5/19 purchaser email=ajoo@ucsf.edu pi (recipient)=Joo, Adriane recipient pi email=ajoo@ucsf.edu db consult= vendor=Jackson Laboratory order placed=1/25/19 mgi id= qty=2 received at final destination=2/5/19 official symbol= </span></div><div class=\'reportEntry\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3035</span>:   Gt(ROSA)26Sor&lt;tm1(CAG-tdTomato*,-EGFP*)Ees&gt;</span> - Purchased from Jax Mice, catalog #23035 by Diana Laird for Diana Laird <span class=\'rawRecord\'><br>Raw data:strain=B6;129S6-Gt(ROSA)26Sortm1(CAG-tdTomato*,-EGFP*)Ees/J stock no=23035 notes= req no=2 purchaser phone= cryo= purchaser name=Laird, Diana final destination=Core 2 arrived=2/12/19 purchaser email=dlaird@ucsf.edu pi (recipient)=Laird, Diana recipient pi email=dlaird@ucsf.edu db consult= vendor=Jackson Laboratory order placed=1/31/19 mgi id= qty=1 received at final destination=2/12/19 official symbol= </span></div><div class=\'reportEntryAlt\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3036</span>:   Tg(Krt8-cre/ERT2)17Blpn</span> - Purchased from Jax Mice, catalog #17947 by Adriane Joo for Adriane Joo <span class=\'rawRecord\'><br>Raw data:strain=Tg(Krt8-cre/ERT2)17Blpn/J stock no=17947 notes= req no=7 purchaser phone= cryo= purchaser name=Joo, Adriane final destination=Core 3 arrived=1/22/19 purchaser email=ajoo@ucsf.edu pi (recipient)=Joo, Adriane recipient pi email=ajoo@ucsf.edu db consult= vendor=Jackson Laboratory order placed=1/3/19 mgi id=5305437 qty=1 received at final destination=1/22/19 official symbol= </span></div><div class=\'reportEntry\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3037</span>:   Gt(ROSA)26Sor&lt;tm14(CAG-tdTomato)Hze&gt;</span> - Purchased from Jax Mice, catalog #7914 by Estelle Wall for Estelle Wall <span class=\'rawRecord\'><br>Raw data:strain=B6.Cg-Gt(ROSA)26Sortm14(CAG-tdTomato)Hze/J stock no=7914 notes= req no=4 purchaser phone= cryo= purchaser name=Wall, Estelle final destination=Core 2 arrived=2/12/19 purchaser email=ewall@ucsf.edu pi (recipient)=Wall, Estelle recipient pi email=ewall@ucsf.edu db consult= vendor=Jackson Laboratory order placed=2/1/19 mgi id= qty=1 received at final destination=2/12/19 official symbol= </span></div></div>\r\n<br>',2),
	(5,'Purchase Report','2019-09-23','<h3>Newly Created Change Requests (1) (<a class=\'view_link\' href=\'/admin/ManageChangeRequests.jsp?status=all&requestSource=Purchase Report\'>view requests</a>)</h3><div class=\'description\'>adds the name of the holder whose lab received a mouse for which there is already a record</div><div class=\'reportBody\'>\r\n<div class=\'reportEntry\'><span class=\'importAction\'>Created change request <span class=\'changerequest_number\'>#5</span>: Add Adriane Joo to Actc1 null #6.</span>  (purchased by Adriane Joo) <span class=\'rawRecord\'><br>Raw data:strain=Actc1 null stock no=050656-UCD notes= req no=1/10/00 purchaser phone= cryo= purchaser name=Joo, Adriane final destination=Core 3 arrived=1/15/19 purchaser email=ewall@ucsf.edu pi (recipient)=Joo, Adriane recipient pi email=ajoo@ucsf.edu db consult= vendor=MMRRC-UCD order placed=1/8/19 mgi id=6276989 qty= received at final destination=1/15/19 official symbol= </span></div></div>\r\n<br>',3),
	(10,'Import Report','2019-10-22','<h3>Newly Created Submissions (1) (<a class=\'view_link\' href=\'/admin/ListSubmissions.jsp?status=all&submissionSource=Import Report\'>view submissions</a>)</h3><div class=\'description\'>creates a draft submission for a mouse that is not yet listed in the database, with the name of a holder whose lab received the mouse</div><div class=\'reportBody\'>\r\n<div class=\'reportEntry\'><span class=\'importAction\'>Created submission <span class=\'submission_number\'>#3042</span>:   Arl13b&lt;tm1.1Tc&gt;</span> - Imported from UCLA by Jeff Bush for Jeff Bush <span class=\'rawRecord\'><br>Raw data:strain=Arl13b-flox notes= recipient name=Bush, Jeff req no=15 recipient email=jbush@ucsf.edu sender institution=UCLA cryo= pmid (if published)=23014696 published y/n=Y final destination=C-1 arrived=2/27/19 recipient phone= pi (recipient)=Bush, Jeff recipient pi email=jbush@ucsf.edu db consult= order placed=1/22/19 mgi id=6107225 qty=1 received at final destination=4/15/19 official symbol= </span></div></div>\r\n<br>',4),
	(11,'Import Report','2019-10-22','',6),
	(12,'Import Report','2019-10-22','',6);

/*!40000 ALTER TABLE `import_reports` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table literature
# ------------------------------------------------------------

DROP TABLE IF EXISTS `literature`;

CREATE TABLE `literature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pmid` varchar(32) DEFAULT NULL,
  `abstract` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `literature` WRITE;
/*!40000 ALTER TABLE `literature` DISABLE KEYS */;

INSERT INTO `literature` (`id`, `pmid`, `abstract`)
VALUES
	(1085,'9861006',NULL),
	(1200,'9916135',NULL),
	(1330,'9930871',NULL),
	(1884,'9851932',NULL),
	(1932,'9891028',NULL),
	(1980,'9598348',NULL),
	(1981,'28053125',NULL),
	(1982,'20023653',NULL),
	(1983,'20976199',NULL);

/*!40000 ALTER TABLE `literature` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table mouse
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mouse`;

CREATE TABLE `mouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `strain_comment` varchar(255) DEFAULT NULL,
  `modification_type` varchar(255) DEFAULT NULL,
  `transgenictype_id` int(11) DEFAULT -1,
  `regulatory_element_comment` text DEFAULT NULL,
  `expressedsequence_id` int(11) DEFAULT -1,
  `reporter_comment` varchar(255) DEFAULT NULL,
  `strain` text DEFAULT NULL,
  `gene_id` int(11) DEFAULT -1,
  `target_gene_id` int(11) DEFAULT -1,
  `general_comment` text DEFAULT NULL,
  `other_comment` varchar(255) DEFAULT NULL,
  `source` text DEFAULT NULL,
  `inbred_strain_id` int(11) DEFAULT -1,
  `mousetype_id` int(11) DEFAULT -1,
  `mta_required` enum('Y','N','D') DEFAULT NULL,
  `repository_id` int(11) DEFAULT 1,
  `repository_catalog_number` varchar(255) DEFAULT '',
  `submittedmouse_id` int(11) DEFAULT NULL,
  `holder_lastname_for_sort` varchar(32) DEFAULT '',
  `gensat` varchar(100) DEFAULT NULL,
  `cryopreserved` varchar(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `endangered` tinyint(4) DEFAULT NULL,
  `official_name` varchar(255) DEFAULT NULL,
  `admin_comment` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `submittedmouse_id` (`submittedmouse_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `mouse` WRITE;
/*!40000 ALTER TABLE `mouse` DISABLE KEYS */;

INSERT INTO `mouse` (`id`, `name`, `strain_comment`, `modification_type`, `transgenictype_id`, `regulatory_element_comment`, `expressedsequence_id`, `reporter_comment`, `strain`, `gene_id`, `target_gene_id`, `general_comment`, `other_comment`, `source`, `inbred_strain_id`, `mousetype_id`, `mta_required`, `repository_id`, `repository_catalog_number`, `submittedmouse_id`, `holder_lastname_for_sort`, `gensat`, `cryopreserved`, `status`, `endangered`, `official_name`, `admin_comment`)
VALUES
	(5,'Actb flox',NULL,'conditional allele (loxP/frt)',2,NULL,-1,NULL,NULL,3,NULL,'LoxP sites flank exons 2 and 3.',NULL,'Actb<tm1.1Erv>',-1,1,'N',5,'4881824',3028,'',NULL,'0','live',0,'targeted mutation 1.1, James M Ervasti',''),
	(3,'beta-actin-Cre',NULL,NULL,2,'sequences from human beta-actin gene',2,NULL,NULL,NULL,NULL,'Cre recombinase is expressed in early embryo. A single copy of the transgene was inserted into intron 4, and resulted in intronic deletions of 2.2, 7.3, 3.2 kb around integration site. [URL]http://www.informatics.jax.org/marker/MGI:1919410[/URL][link]GeneMGI:1919410[/link]',NULL,'Tmem163<Tg(ACTB-cre)2Mrt>',-1,2,'N',5,'2176050',3026,'',NULL,'0','live',0,'transgene insertion 2, Gail R Martin',''),
	(1,'4get (Il4-GFP)',NULL,'targeted knock-in',2,NULL,3,'EGFP','C57BL/6 (Wall), maintained with Krt20-Cre/ERT2 Record #2 (Bush)',5,NULL,'IRES-EGFP and BGH polyA sequences were inserted after the stop codon in exon 4. ',NULL,'Il4<tm1Lky>',-1,1,'N',5,'2176574',3041,'',NULL,'0','live',0,'targeted mutation 1, Richard M Locksley',''),
	(4,'129X1/SvJ',NULL,NULL,2,NULL,-1,NULL,NULL,NULL,NULL,'Breed siblings to maintain inbred strain.',NULL,'JAX Mice, 000691',-1,3,'N',5,NULL,NULL,'',NULL,'0','live',0,NULL,''),
	(2,'Krt20-Cre/ERT2',NULL,'endonuclease-mediated',2,NULL,4,NULL,'Maintained with 4get (Il4-GFP) Record #1 (Bush)',2,NULL,'Cre/ERT2 knock-in mutation was inserted before the stop codon.\r\n[URL]https://www.gudmap.org/chaise/record/#2/Gene_Expression:Specimen/RID=N-GZ5G[/URL][link]www.gudmap.org specimen[/link]','Cre/ERT2','Krt20<em1(cre/ERT2)Amc>',-1,1,'N',5,'5903002',3025,'',NULL,'0','live',0,'endonuclease-mediated mutation 1, Andrew P McMahon','');

/*!40000 ALTER TABLE `mouse` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table mouse_files
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mouse_files`;

CREATE TABLE `mouse_files` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `filename` text DEFAULT NULL,
  `file` blob DEFAULT NULL,
  `mouseID` text DEFAULT NULL,
  `filestatus` text DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `mouse_files` WRITE;
/*!40000 ALTER TABLE `mouse_files` DISABLE KEYS */;

INSERT INTO `mouse_files` (`ID`, `filename`, `file`, `mouseID`, `filestatus`)
VALUES
	(2,'Krt20-CreERT2 genotyping',X'47656E6F747970696E67200D5461696C2073616D706C6573206F662074686520656D6272796F73207765726520636F6C6C656374656420616E6420696E6375626174656420696E207461696C20646967657374696F6E20627566666572206F7665726E696768742061742035356F432E205043522077617320706572666F726D656420617320706572207468652070726F746F636F6C2062656C6F7720616E6420746865205043522070726F647563747320776572652072756E206F6E206120312E352520616761726F73652067656C20284669677572652032292E0D4F6C69676F6E75636C656F74696465733A20666F722074617267657465642F7472616E7367656E696320616C6C656C652053697A653A203534386270200D444E412073657175656E63652028666F7277617264293A2035D52D2047415454545447474341434143434343544154472D33D520444E412073657175656E636520287265766572736520292035D52D2054434343544741414341544754434341544341472D33D520416D706C69666965732035D52061726D20696E746F204372652073657175656E63652E0D52786E2042756666657220616E6420436F6E646974696F6E733A202832353F6C207265616374696F6E29200D313058205043522042756666657220312E32356D4D20644E5450203130754D207072696D65722046203130754D207072696D6572205220357820637265736F6C20726564206479652054617120706F6C796D65726173652047656E6F6D696320444E4120546F74616C20766F6C756D65200D322E35756C0D34756C0D31756C0D31756C0D35756C0D302E32756C202835752F756C292031756C200D323520756C200D39346F4320336D696E200D37326F432031306D696E200D31206379636C65200D31206379636C65200D39346F432032307365632036306F432032307365632037326F43203435736563200D33356379636C6573200D0D','null',NULL),
	(3,'Krt20-CreERT2 genotyping',X'47656E6F747970696E67200D5461696C2073616D706C6573206F662074686520656D6272796F73207765726520636F6C6C656374656420616E6420696E6375626174656420696E207461696C20646967657374696F6E20627566666572206F7665726E696768742061742035356F432E205043522077617320706572666F726D656420617320706572207468652070726F746F636F6C2062656C6F7720616E6420746865205043522070726F647563747320776572652072756E206F6E206120312E352520616761726F73652067656C20284669677572652032292E0D4F6C69676F6E75636C656F74696465733A20666F722074617267657465642F7472616E7367656E696320616C6C656C652053697A653A203534386270200D444E412073657175656E63652028666F7277617264293A2035D52D2047415454545447474341434143434343544154472D33D520444E412073657175656E636520287265766572736520292035D52D2054434343544741414341544754434341544341472D33D520416D706C69666965732035D52061726D20696E746F204372652073657175656E63652E0D52786E2042756666657220616E6420436F6E646974696F6E733A202832353F6C207265616374696F6E29200D313058205043522042756666657220312E32356D4D20644E5450203130754D207072696D65722046203130754D207072696D6572205220357820637265736F6C20726564206479652054617120706F6C796D65726173652047656E6F6D696320444E4120546F74616C20766F6C756D65200D322E35756C0D34756C0D31756C0D31756C0D35756C0D302E32756C202835752F756C292031756C200D323520756C200D39346F4320336D696E200D37326F432031306D696E200D31206379636C65200D31206379636C65200D39346F432032307365632036306F432032307365632037326F43203435736563200D33356379636C6573200D0D','2','approved'),
	(6,'Krt20-CreERT2 guideRNA',X'4B727432302D5432412D4352452D455254320D477569646520524E412073657175656E636520646174612063616E20626520666F756E642061740D68747470733A2F2F7777772E6775646D61702E6F72672F6861747261632F7265736F75726365732F6D6F7573655F73747261696E2F323031382F36393661336436653365373937323236633366363535313332386234303035320D0D0D','2','approved');

/*!40000 ALTER TABLE `mouse_files` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table mouse_holder_facility
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mouse_holder_facility`;

CREATE TABLE `mouse_holder_facility` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mouse_id` int(11) DEFAULT NULL,
  `holder_id` int(11) DEFAULT NULL,
  `facility_id` int(11) DEFAULT NULL,
  `covert` tinyint(4) DEFAULT NULL,
  `cryo_live_status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `mouse_holder_facility` WRITE;
/*!40000 ALTER TABLE `mouse_holder_facility` DISABLE KEYS */;

INSERT INTO `mouse_holder_facility` (`id`, `mouse_id`, `holder_id`, `facility_id`, `covert`, `cryo_live_status`)
VALUES
	(85,4,4,3,0,'Live only'),
	(210,1,5,2,0,'Live only'),
	(209,1,2,4,0,'Live only'),
	(309,2,4,1,0,'Cryo only'),
	(308,2,2,4,0,'Live only'),
	(220,5,3,2,0,'Live only'),
	(305,3,3,2,0,'Live only'),
	(304,3,2,4,0,'Live only');

/*!40000 ALTER TABLE `mouse_holder_facility` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table mouse_literature
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mouse_literature`;

CREATE TABLE `mouse_literature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `literature_id` int(11) DEFAULT NULL,
  `mouse_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `mouse_literature` WRITE;
/*!40000 ALTER TABLE `mouse_literature` DISABLE KEYS */;

INSERT INTO `mouse_literature` (`id`, `literature_id`, `mouse_id`)
VALUES
	(122,1981,3),
	(121,1980,3),
	(80,1983,5),
	(76,1982,1);

/*!40000 ALTER TABLE `mouse_literature` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table mousetype
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mousetype`;

CREATE TABLE `mousetype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mousetype` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `mousetype` WRITE;
/*!40000 ALTER TABLE `mousetype` DISABLE KEYS */;

INSERT INTO `mousetype` (`id`, `mousetype`)
VALUES
	(1,'Mutant Allele'),
	(2,'Transgene'),
	(3,'Inbred Strain');

/*!40000 ALTER TABLE `mousetype` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table reference
# ------------------------------------------------------------

DROP TABLE IF EXISTS `reference`;

CREATE TABLE `reference` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reference` varchar(255) DEFAULT '',
  `pmid` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table repository
# ------------------------------------------------------------

DROP TABLE IF EXISTS `repository`;

CREATE TABLE `repository` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `repository` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `repository` WRITE;
/*!40000 ALTER TABLE `repository` DISABLE KEYS */;

INSERT INTO `repository` (`id`, `repository`)
VALUES
	(1,'none'),
	(6,'GENSAT'),
	(5,'MGI');

/*!40000 ALTER TABLE `repository` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table schema_migrations
# ------------------------------------------------------------

DROP TABLE IF EXISTS `schema_migrations`;

CREATE TABLE `schema_migrations` (
  `version` varchar(255) NOT NULL,
  UNIQUE KEY `unique_schema_migrations` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `schema_migrations` WRITE;
/*!40000 ALTER TABLE `schema_migrations` DISABLE KEYS */;

INSERT INTO `schema_migrations` (`version`)
VALUES
	('20121126012149'),
	('20121126022718'),
	('20121126022831'),
	('20121128063353'),
	('20130204070323'),
	('20130204072822'),
	('20130205061729'),
	('20130207071226'),
	('20130207071525'),
	('20130210080233'),
	('20130213070354'),
	('20130222074355'),
	('20130723021956'),
	('20130723040201'),
	('20130723043158'),
	('20130723052300'),
	('20130724041749'),
	('20130724043002'),
	('20130806171507'),
	('20130807052024'),
	('20130809042102'),
	('20130809052145');

/*!40000 ALTER TABLE `schema_migrations` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table settings
# ------------------------------------------------------------

DROP TABLE IF EXISTS `settings`;

CREATE TABLE `settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` int(10) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `setting_value` text DEFAULT NULL,
  `date_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `text_area_rows` int(10) DEFAULT 0,
  `position` int(11) DEFAULT NULL,
  `secondary_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;

INSERT INTO `settings` (`id`, `category_id`, `name`, `label`, `setting_value`, `date_updated`, `text_area_rows`, `position`, `secondary_value`)
VALUES
	(2,1,'database terms','GRM and AJ','<b>Editing a submission:</b><br><font size=\"2\"><u><br></u>&nbsp;&nbsp; <u>Preferred terminology for mouse names</u>:</font><br><blockquote><font size=\"2\"><b>For mouse gene symbols</b> the first letter should be in upper case and the rest in lower case; Greek symbols and hyphens are not used. <br>&nbsp;<br>Use \"<b>null</b>\" instead of phrases like \"KO,\" \"-\" </font><font size=\"2\"><br></font><blockquote><font size=\"2\">Note that a mouse\'s genotype (\"+/-\" or \"-/-\")</font> should not be given since what is shown is the name of the allele or transgene.<br></blockquote>Use \"<b>deletion</b>\" for cases in which part of the gene is deleted but it is not a null allele.<br><br>Use \"<b>flox</b>\" instead of phrases like \"floxed,\" \"fx\"<br><br>Outdated gene symbols may be used, but the use of current gene symbols is preferred.<br><br>Use \"<b>-LacZ (null)</b>\" and \"<b>-Cre (null)</b>\" for knock-ins that create null alleles.<br><br>Use \"<b>LSL</b>\" to denote the presence of a floxed stop cassette.<br><br></blockquote><font size=\"2\">&nbsp;&nbsp; </font><u><font size=\"2\"><u>Preferred terminology</u></font> for <b>inducible</b> Cre genes (according to terminology used by MGI):</u><br><blockquote>\'ERT\' and \'ERT2\' are highly specific modifications of the mouse estrogen receptor 1 (Esr1) made in Pierre Chambon\'s laboratory.<br><br>\'<b>Cre/ERT</b>\' is used in the official symbol when the Cre allele in the mutant allele/transgene contains ERT<br><br>\'<b>Cre/ERT2</b>\' is used in the official symbol when the Cre allele in the mutant allele/transgene contains ERT2<br><br>\'<b>Cre/ESR1</b>\' is used in the official symbol when the Cre allele in the mutant allele/transgene contains the unmodified Esr1. <br><br>\'<b>Cre/ESR1*</b>\' is used in the official symbol when the Cre allele in the mutant allele/transgene contains a modification that is different from those in ERT and ERT2.<br></blockquote><br>&nbsp;&nbsp; <u>Tetracycline-responsive promoter elements:<br><br></u><blockquote>There are two forms, \"TRE\" and \"TRE2\". The latter has less background activity than the original version.<br></blockquote><br>&nbsp;&nbsp; <u>Formatting the description:</u><br><blockquote>1. Do not describe modifications to a gene that have been removed (e.g. insertion of floxed Neo casette followed by Cre-mediated excision of same).<br><br>2. Remove text describing efforts to validate the nature of the modification (e.g. about assays for gene or protein expression).<br><br>3. Eliminate words in the description that are already included in the record&nbsp; (e.g., the phrase \"was disrupted by\" repeats info in the modification type)<br></blockquote><blockquote><br></blockquote><br><blockquote><br><br></blockquote>','2015-07-21 12:18:48',0,NULL,''),
	(51,11,'','REQUEST A DEMONSTRATION','Contact Estelle Wall<div><a href=\"mailto:admin.mousedatabase@ucsf.edu\">mousedatabase@ucsf.edu</a></div>','2019-10-15 11:55:19',0,3,'three_column_left'),
	(52,11,'','ASK A NEIGHBOR','Find your local Mouse Database volunteer for help using this web app&nbsp;<a href=\"http://mousedatabase.ucsf.edu/mouseinventory/FacilityReport.jsp\">Facility List</a>.<br>','2019-10-15 11:55:08',0,4,'three_column_center'),
	(145,11,'','NEW SITE FEATURES','<span style=\"font-size: 10pt;\">Files can be added to Mouse Records</span><br><div>Download CSV of Holder\'s Mouse Records</div>','2019-10-16 15:57:10',0,5,'three_column_right'),
	(9,2,'home_page_text_submitting_mice','Requesting changes to the Mouse Database','<p>Only the administrator(s) of the Mouse Inventory Database can make changes to the data that are on display in the database.</p><p>If you want to add a mouse that is not already in the inventory, click on \'Submit Mice\' at the top of this page, and complete the&nbsp;<b>submission form</b>.</p><p>If a mouse is already in the inventory and you want to add the name of an additional holder, go to \'Mouse Records\' and find the record for that mouse, click on \'Request change in record\' (under the mouse name) and complete the<b>&nbsp;request change form</b>.</p><p>If a mouse is no longer being maintained by a particular holder, the name of that holder should be deleted from the record using the same procedure as for adding a holder. Note, that if the holder to be deleted is the only investigator maintaining the mouse, the record for that mouse will be deleted from the database.</p><p>Requests for other changes to a record can also be submitted using the request change form.</p><p>When a completed submission or request change form is submitted, the Inventory Administrator will generate a record for the mouse in the database or make the requested alterations in holders or other information about a mouse.</p>','2019-09-12 21:32:44',0,NULL,''),
	(14,4,'admin_info_name','Administrator full name','admin holder','2019-10-20 22:44:10',0,NULL,''),
	(15,4,'admin_info_email','Administrator email','mousedatabase@ucsf.edu','2019-10-20 22:35:14',0,NULL,''),
	(16,4,'admin_info_auto_sub_firstname','Auto-fill submission first name','admin','2019-10-20 22:43:36',0,NULL,''),
	(17,4,'admin_info_auto_sub_lastname','Auto-fill submission last name','holder','2019-10-20 22:43:53',0,NULL,''),
	(18,4,'admin_info_auto_sub_email','Auto-fill submission email','mousedatabase@ucsf.edu','2019-10-20 22:35:29',0,NULL,''),
	(19,4,'admin_info_auto_sub_department','Auto-fill submission department','Database administration','2012-09-13 20:06:11',0,NULL,NULL),
	(20,4,'admin_info_auto_sub_telephone','Auto-fill submission telephone','123-456','2012-09-13 20:06:11',0,NULL,NULL),
	(21,4,'admin_info_auto_sub_holder','Auto-fill submission holder (Last, First)','holder, admin','2019-10-20 22:43:16',0,NULL,''),
	(22,4,'admin_info_auto_sub_facility','Auto-fill submission facility','core','2019-10-20 22:36:04',0,NULL,''),
	(23,5,'general_site_name','Website Name','UCSF Mouse Inventory Database','2012-09-13 20:06:11',0,NULL,NULL),
	(24,5,'general_site_alert','Alert (dispalyed at top of all pages unless blank)','','2012-11-27 22:27:54',0,NULL,NULL),
	(25,6,'import_ignored_jax_numbers','Ignored JAX Catalog numbers, one per line.  Blank lines are OK.','','2019-10-08 14:14:47',20,NULL,''),
	(31,8,'download_files_allele_id','How to find the MGI allele detail page (PDF)','https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGIAlleleID.pdf','2012-09-26 09:51:57',0,NULL,NULL),
	(32,8,'download_files_gene_id','How to find the MGI gene ID (PDF)','https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGIGeneID.pdf','2012-09-26 09:51:57',0,NULL,NULL),
	(33,8,'download_files_transgene_id','How to find the MGI transgene detail page (PDF)','https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGITransgeneID.pdf','2012-09-26 09:51:57',0,NULL,NULL),
	(34,5,'general_site_hostname','Site protocol and hostname','https://mousedatabase.ucsf.edu','2012-09-26 09:52:10',0,NULL,NULL),
	(36,1,'official nomenclature for KOMP alleles','Gail Martin','<u>Derivative alleles</u><br><br>\r\n<b>tm1a</b>:  KO first allele (reporter-tagged insertion allele) - knock-in of lacZ (presumed null)<br><br>\r\n<b>tm1b</b>:  post-Cre (reporter-tagged deletion allele) - lacZ still present, floxed sequence deleted.<br><br>\r\n<b>tm1c</b>:  post-Flp (conditional allele) - lacZ deleted, loxP sites flank portion of gene<br><br>\r\n<b>tm1d:</b>  post-Flp and Cre (deletion allele with no reporter)<br><br>\r\n<b>tm1e</b>:  targeted, non-conditional allele','2013-01-04 05:49:57',0,NULL,NULL),
	(42,1,'Adding a \'download button\' on the database','GRM','Go to \'Options\' and then \'Manage settings\'. Use the drop down menu under \'Settings\' to select the category in which the setting to which you want to add the \'download button\' is found. If necessary, create a new setting in that category. <br><br>Enter whatever it is you want to say, including the <font color=\"#ff0000\">text</font> you want in the \'download button.\' Select that <font color=\"#ff0000\">text</font>, and use \'insert hyperlink\' in the Rich Text \r\nEditor (seventh from the left) to create a link to whatever document you\r\n want downloaded (see note with instructions for \'uploading a file to Amazon \r\nS3\'). Note that this link will not function in the editor - only after the setting is saved.<br><br>Click \'Show Source\' in the Rich Text Editor (last on the right) and locate the link within the html source - something starting with \'open bracket a href=\' and ending with \'<font color=\"#ff0000\">text</font> open bracket /a close bracket\'. Add a class attribute, with value \'btn btn info\' to the link by inserting the text <b>class=\'btn btn-info\'</b> right before the term \'href=\' in the link. Click \'Show Source\' again, and save. You will only see the button on the setting list page, not in the editor.<br><br>To make the text in a button span multiple lines, you need to use the view source command, and insert HTML line breaks: <br> where you would like a new line to start.<br><br>The color of the button will be \'default turquoise\' with white lettering. If you want a different button color, use \'btn-primary\' (blue), \'btn-success\' (green), \'btn-warning\' (orange), \'btn-danger\' (red), or \'btn-brown\' instead of \'btn-info\'.<br><br>The color of whatever you have written in the setting is \'default turquoise\' when that text is flanked by \'open bracket font close bracket\' on the left and \'open bracket /font close bracket\' on the right. If you change the color of that text using \'Font Color\' in the Rich Text Editor, when you click \'Show Source\' it will say something like: open bracket font color=\"#ff0000\" close bracket, then the text you have colored followed by open bracket /font close bracket. If you want to revert to \'default turquoise,\' remove the \'color=xxxx\' command in the brackets on the left of the text that has been colored. <br><br>Colors of the settings and other button colors need to be set by the programmer. A good tool for picking colors is: <a href=\"http://www.w3schools.com/tags/ref_colorpicker.asp\">http://www.w3schools.com/tags/ref_colorpicker.asp</a><br><br> ','2013-08-09 12:45:34',0,NULL,NULL),
	(43,1,'Organizing Settings on the home page','GRM','Use the \'layout customization\' feature to create a custom arrangement of settings on the home page. Settings that are customizable include those under: \'Recent site updates\', \'Help keep the database up-to-date items\',\'Did you know? items,\' and \'Need help using the database? items\'.<br><br>The choices for placement of each setting in the layout are found in a drop down menu that is available when a setting is being edited. They are: \'one column, centered, full width\', \'one column, centered, 60% width\', (the \'none\' default is centered, 40% width);&nbsp; \'two columns, left\' or \'two columns, right\'; and \'three columns, left\', \'three columns, center\' or \'three columns, right\'.<br><br>The important point is that the settings that are intended to be in a particular grouping (e.g., three columns) <b>must be adjacent to one another in the public sort order</b> and in the correct vertical order. Thus, the ones to be on the left must be all together (in the order they are wanted on the vertical axis) followed by the ones to be in the center (in the order they are wanted on the vertical axis), and then the ones to be on the right (in the order\r\n they are wanted on the vertical axis).<br><br>The same is true for two column groupings. <br><br>The groupings do not need to be in a particular sequence. Thus, a three grouping can be followed by a one grouping, then a two grouping. For example,<br><br>A - 3 column, left<br>B - 3 column, left<br>C - 3 column, left<br>D - 3 column, center<br>E - 3 column, center<br>F - 3 column, right<br>G - one column, centered, full width<br>H - 2 column, left<br>I - 2 column, right<br><br>In which case, settings A-F will be arrayed in three columns of three, two, and one setting, from left to right. Below them, setting G will be in the center spanning the entire field horizontally, and then H and I will be in a two column array at the bottom, on the left and right, respectively. <br>','2013-08-09 13:03:44',0,NULL,NULL),
	(8,2,'home_page_text_search_and_sort','Seach and sort functions','<p>On the \'<b>Mouse Records</b>\' page, the mode of display of records can be selected: view all, or view records in only one of the three categories. The records can be sorted by Mouse Inventory Database ID (record) number or Mouse Name.&nbsp;&nbsp;</p><p>The \'<b>Gene List</b>\' provides a list of all mouse genes that have been entered in the database. There is a link to mice currently in the Inventory in which that gene is modified (mutant alleles) or in which that gene is expressed (transgenes).</p><p>The \'<b>Holder List</b>\' provides the names and contact information for investigators with approved animal protocols who are maintaining the mice listed in the Inventory in their colonies. The total number of mice in the Inventory currently being maintained by each holder is shown. Clicking on that number provides a report showing only the mice maintained by that holder.</p><p>There is a flexible \'Search\' function.</p>','2012-09-14 03:06:11',0,NULL,NULL),
	(7,2,'home_page_text_information_about_each_mouse','What is a Mouse Record?','<p>The Mouse Record contains 6 parts: Name, Category, Details, Comment, Files and Holders.</p><p>Each record&nbsp;<span style=\"font-size: 10pt;\">has a unique database Record # and</span><span style=\"font-size: 10pt;\">&nbsp;includes the \'informal\' mouse name provided by the person who submitted the entry, the mouse category (Mutant Allele, Transgene, or Inbred Strain) and the name of the \'holder\' (investigator with an approved protocol and colony location).</span></p><p><span style=\"font-size: 10pt;\">When a mutant allele, transgene, or inbred strain is being maintained by two or more holders, there is only&nbsp;</span><b style=\"font-size: 10pt;\">one</b><span style=\"font-size: 10pt;\">&nbsp;record for it, which lists all holders. &nbsp;It the mouse is maintained with another mutation, it can be listed in the comment field.</span></p><p>For mutant alleles, the type of modification is shown, as well as the official name and symbol of the gene that is modified. In addition, the ID number for that gene in the Mouse Genome Informatics (MGI) database is shown.&nbsp;</p><dl style=\"margin: 0px;\"><dt>Each entry is for an&nbsp;<span class=\"red\"><font color=\"#ff0000\">individual</font></span>&nbsp;mutant allele, transgene, or inbred strain irrespective of whether the allele or transgene is maintained in combination with other mutant alleles or transgenes. The \'Background Strain\' or \'Comment\' fields in the mouse record can be used to list mice maintained with other mutations.</dt></dl><p>If the mutant allele is a knock-in, the expressed sequence is described; if it is a mouse gene, the official name, symbol and MGI ID of that gene is shown, with a link to MGI.</p><p>For transgenes, the expressed sequence and regulatory elements are described; if the expressed sequence is a mouse gene, the official name, symbol and MGI ID of the gene is shown, with a link to MGI.</p><p>A comment on or brief description of the mutant allele/transgene may be provided.</p><p>For inbred strains, the name of the commercial supplier is provided, For those obtained from JAX Mice, there is a link to the description of that strain on the JAX Mice website.</p><p>If the mutant allele or transgene has been published, the record also provides the MGI allele/transgene ID and official symbol for the mouse, along with the Pubmed ID for the most relevant publication(s) describing the genetic modifications in the allele/transgene. Clicking on the&nbsp;<span class=\"red\"><font color=\"#ff0000\">MGI allele/transgene ID</font></span>&nbsp;or the&nbsp;<span class=\"red\"><font color=\"#ff0000\">Pubmed ID</font></span>&nbsp;will bring up the relevant pages on MGI or Pubmed, respectively.</p><p>Background strain of the mutant allele/transgene can be listed.</p>','2019-09-22 10:31:49',0,NULL,''),
	(4,2,'home_page_text_introduction','Technical Details about the database','<p>The Mouse Database is a web application consisting of Java Server Pages and&nbsp;<span style=\"font-size: 10pt; \">custom Java libraries&nbsp;</span><span style=\"font-size: 10pt; \">running on Tomcat, backed by a MySQL database. JavaScript must be enabled to access all features of the database.</span><span style=\"font-size: 10pt;\">The code behind the database is open source, and is available on the&nbsp;</span><a href=\"/mouseinventory/licenses.jsp\" style=\"font-size: 10pt;\">licenses</a><span style=\"font-size: 10pt;\">&nbsp;page.</span></p><ul style=\"margin: 0px; padding-left: 20px; \"><li>Version 1, Programmed by Nick Didkovsky, resources provided by Mary Elizabeth Hatten (Rockefeller University) &amp; specified by Gail Martin (UCSF) April 2009.<br></li><li>Version 2 Developed by Jonathan Scoles, San Francisco, CA.&nbsp;February, 2013.</li><li>Version 2.1 Updated by Estelle Wall, Douglas Johnston, Carlo Quinonez September 2019.</li><li>Logo designs created by Marta Dansa, Barcelona, Spain April 2019.</li></ul>','2019-09-22 10:18:41',0,NULL,''),
	(6,2,'home_page_text_how_mice_are_listed','Types of Mutant Mice in the Mouse Database','<p style=\"font-weight: normal;\">Each mouse entry in the database is classified in one of three categories.</p><p><span style=\"font-size: 10pt;\">1) <b>Mutant Allele</b>&nbsp;</span><span style=\"font-weight: normal; font-size: 10pt;\">Mutations are in a specific gene or sequence with a type of modification being targeted disruption, conditional allele, targeted knock-in, gene trap insertion, chemically induced (ENU), spontaneous mutation, endonuclease-mediated, or other (description provided).&nbsp;</span></p><p><span style=\"font-size: 10pt;\">2) </span><b style=\"font-size: 10pt;\">Transgene</b><span style=\"font-size: 10pt;\">&nbsp;</span><span style=\"font-size: 10pt;\">Mutations express randomly inserted sequences in the genome. It requires the insertion of a regulatory element and the&nbsp;expressed sequence is listed as Cre, Reporter, mouse gene, or other.</span></p><dl style=\"margin: 0px;\"><dt><span style=\"font-weight: normal;\">3) </span><b>Inbred Strain&nbsp;</b><span style=\"font-size: 10pt;\">These mice are isogenic as a result of extensive inbreeding of siblings, 20+ generations.</span></dt></dl>','2019-09-22 10:32:03',0,NULL,''),
	(5,2,'home_page_text_purpose','Purpose','<p style=\"font-family: Arial, Verdana; font-style: normal; font-variant-caps: normal; font-weight: normal;\"><font size=\"5\">Find Mutant Alleles, Transgenes and Inbred Strains housed on campus and collaborate with other investigators.</font></p>','2019-09-12 14:12:07',0,NULL,''),
	(55,12,'','Last paragraph','<b style=\"font-weight: normal;\">\'Last review date\' of when holder audited records.</b>','2019-09-17 14:20:57',0,2,''),
	(59,11,'','Mouse Database User Manual','<a class=\"btn btn-info\" href=\"https://s3-us-west-1.amazonaws.com/mousedatabase-files/++Database+User%27s+manual-11.26.13.pdf\">DOWNLOAD USER MANUAL (pdf)</a><br>','2019-10-15 11:53:56',0,0,'three_column_left'),
	(60,11,'','How to find mutant mice for your GOI','<font>.</font><a class=\"btn btn-success\" href=\"https://s3-us-west-1.amazonaws.com/mousedatabase-files/+Have+Gene%2C+Want+Mouse+(11.26.13).ppt\">DOWNLOAD MOUSE GENE&nbsp;(pdf)</a><br>','2019-10-15 11:51:38',0,2,'three_column_right'),
	(61,11,'','Mutant Alleles and Transgenes Explained','<a class=\"btn btn-success\" href=\"https://s3-us-west-1.amazonaws.com/mousedatabase-files/+Read+Me+First!+Mouse+Mutants-a+primer+(11.20.13).ppt\">DOWNLOAD MOUSE MUTANTS (pdf)</a><br>','2019-10-15 11:50:48',0,1,'three_column_center'),
	(62,13,'category_3_color','Category color: Recent Site Updates','D3ECD3','2013-08-09 05:56:28',0,NULL,'629962'),
	(63,13,'category_7_color','Category color: Help keep the database up-to-date','D7EBF5','2013-08-09 19:36:44',0,NULL,'8585AF'),
	(64,13,'category_9_color','Category color: Did you know?','E6D7D2','2013-08-09 19:57:14',0,NULL,'C29999'),
	(65,13,'category_11_color','Category color: Need help using the database?','FCFCC7','2013-08-09 19:28:37',0,NULL,'2B8E99');

/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table source
# ------------------------------------------------------------

DROP TABLE IF EXISTS `source`;

CREATE TABLE `source` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table strain
# ------------------------------------------------------------

DROP TABLE IF EXISTS `strain`;

CREATE TABLE `strain` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `strain` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table submittedmouse
# ------------------------------------------------------------

DROP TABLE IF EXISTS `submittedmouse`;

CREATE TABLE `submittedmouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `firstname` varchar(128) DEFAULT NULL,
  `lastname` varchar(128) DEFAULT NULL,
  `dept` varchar(256) DEFAULT NULL,
  `address` varchar(80) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `tel` varchar(32) DEFAULT '',
  `properties` text DEFAULT NULL,
  `date` date DEFAULT NULL,
  `status` enum('new','accepted','rejected','need more info') DEFAULT 'new',
  `admincomment` text DEFAULT NULL,
  `entered` enum('Y','N') DEFAULT 'N',
  `submission_source` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `status` (`status`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `submittedmouse` WRITE;
/*!40000 ALTER TABLE `submittedmouse` DISABLE KEYS */;

INSERT INTO `submittedmouse` (`id`, `firstname`, `lastname`, `dept`, `address`, `email`, `tel`, `properties`, `date`, `status`, `admincomment`, `entered`, `submission_source`)
VALUES
	(3022,'E','wall','admin','','amouse@ucsf.edu','','Dept=admin	Last=wall	First=E	supplierForInbredStrainCatalogUrl=	cryopreserved=Live only	Email=amouse@ucsf.edu	MouseType=Inbred Strain	comment=	rawMGIComment=	facility=Core 2	mouseName=129X1/SvJ	holder=Wall, Estelle	supplierForInbredStrain=JAX Mice	supplierForInbredStrainCatalogNumber=000691	','2019-09-03','accepted','','Y','Submission form'),
	(3023,'E','Wall','medicine','','adminDB@ucsf.edu','','comment=The targeting construct contains a a rox-flanked STOP, frt-flanked STOP and loxP-flanked tdTomato::STOP upstream of the eGFP fluorescent protein, all inserted into the Gt(ROSA)26Sor locus. Dre-mediated recombination removed the rox-flanked STOP.	mouse gene=	facility=Core 3	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	mouseName=Rosa26-CAG-FSF-Tomato Flox/GFP	geneValid=true	holder=Joo, Adriane	First=E	pmid=26586220	modificationType=targeted knock-in	geneOfMutantAlleleMouse=104735	Last=Wall	Dept=medicine	source=Gt(ROSA)26Sor<tm1.3(CAG-tdTomato,-EGFP)Pjen>	mta=	isPublished=Yes	ExpressedSequence=Reporter	strain=	reporter=tdTomato, eGFP	MouseType=Mutant Allele	repository=5617964	cryopreserved=Live and Cryo	Email=adminDB@ucsf.edu	rawMGIComment=The targeting construct contains a a rox-flanked STOP, frt-flanked STOP and loxP-flanked tdTomato::STOP upstream of the eGFP fluorescent protein, all inserted into the Gt(ROSA)26Sor locus. Dre-mediated recombination removed the rox-flanked STOP.	targetGeneValid=	','2019-09-10','accepted','','Y','Submission form'),
	(3024,'e','wall','stem cell','','adminDB@ucsf.edu','','comment=A construct containing the chicken beta-actin promoter (CAG) with a loxP-STOP-loxP cassette preceding a farnesylated tdTomato sequence was inserted into intron 1 of the Gt(ROSA)26Sor locus via homologous recombination. Cre-mediated recombination of this allele results in expression of the tdTomato reporter driven by the CAG promoter.	mouse gene=	facility=Core 2	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	mouseName=Rosa-LSL-Tomato	geneValid=true	holder=Laird, Diana	First=e	pmid=22123957	modificationType=targeted knock-in	geneOfMutantAlleleMouse=104735	Last=wall	Dept=stem cell	source=Gt(ROSA)26Sor<tm2(CAG-tdTomato)Fawa>	mta=	isPublished=Yes	ExpressedSequence=Reporter	strain=	reporter=tdTomato	MouseType=Mutant Allele	repository=5305341	cryopreserved=Live only	Email=adminDB@ucsf.edu	rawMGIComment=A construct containing the chicken beta-actin promoter (CAG) with a loxP-STOP-loxP cassette preceding a farnesylated tdTomato sequence was inserted into intron 1 of the Gt(ROSA)26Sor locus via homologous recombination. Cre-mediated recombination of this allele results in expression of the tdTomato reporter driven by the CAG promoter.	targetGeneValid=	','2019-09-13','accepted','','Y','Submission form'),
	(3025,'e','wall','stem cell','','adminDB@ucsf.edu','','comment=Plasmids encoding a signal guide RNA designed to introduce a cre/ERT2 knock-in mutation into immediately before the stop codon of the Krt20 gene and the cas9 nuclease were introduced into the cytoplasm of C57BL/6N-derived JM8.N4 ES cells	mouse gene=	facility=Core 3	producedInLabOfHolder=No	other=	geneValidationString=Krt20 - keratin 20	targetGeneValidationString=	mouseName=Krt20-Cre/ERT2	geneValid=true	holder=Joo, Adriane	First=e	pmid=	modificationType=endonuclease-mediated	geneOfMutantAlleleMouse=1914059	Last=wall	Dept=stem cell	source=Krt20<em1(cre/ERT2)Amc>	mta=	isPublished=No	ExpressedSequence=	strain=	reporter=	MouseType=Mutant Allele	repository=5903002	cryopreserved=Live only	Email=adminDB@ucsf.edu	rawMGIComment=	targetGeneValid=	','2019-09-13','accepted','','Y','Submission form'),
	(3026,'E','Wall','cell biology','','adminDB@ucsf.edu','','comment=This transgene expresses Cre recombinase under the control of a human beta-actin promoter, which is active in all embryonic cells at or before the blastocyst stage. Line number 2 contains a single copy of the transgene inserted into intron 4 of <A HREF=\"http://www.informatics.jax.org/accession/MGI:1919410\">Tmem163</a> (chr1:129,442,346; NCBI37/mm9) with three intronic deletions of 2.2, 7.3 and 3.2 kb, respectively, surrounding the transgene integration site.	mouse gene=	facility=Core 1	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	regulatoryElement=sequences from human beta-actin gene	gensatFounderLine=	mouseName=beta-actin-Cre	TransgenicType=Random insertion	geneValid=	holder=Bush, Jeff	First=E	pmid=9598348	Dept=cell biology	Last=Wall	source=Tmem163<Tg(ACTB-cre)2Mrt>	mta=	isPublished=Yes	ExpressedSequence=Cre	strain=	MouseType=Transgene	reporter=	repository=2176050	cryopreserved=Live only	Email=adminDB@ucsf.edu	rawMGIComment=This transgene expresses Cre recombinase under the control of a human beta-actin promoter, which is active in all embryonic cells at or before the blastocyst stage. Line number 2 contains a single copy of the transgene inserted into intron 4 of <A HREF=\"http://www.informatics.jax.org/accession/MGI:1919410\">Tmem163</a> (chr1:129,442,346; NCBI37/mm9) with three intronic deletions of 2.2, 7.3 and 3.2 kb, respectively, surrounding the transgene integration site.	knockedInGene=	targetGeneValid=	','2019-09-14','accepted','','Y','Submission form'),
	(3027,'E','Wall','animal care','','adminDB@ucsf.edu','','comment=The Rosa-CAG-LSL-ZsGreen1-WPRE targeting vector was designed with (from 5\' to 3\') a CMV-IE enhancer/chicken beta-actin/rabbit beta-globin hybrid promoter (CAG), an FRT site, a loxP-flanked STOP cassette (with stop codons in all 3 reading frames and a triple polyA signal), ZsGreen1 sequence (from pZsGreen1-N1 (Clontech); a human codon optimized/enhanced green fluorescent protein engineered for brighter fluorescence and higher expression in mammalian cells), a woodchuck hepatitis virus post-transcriptional regulatory element (WPRE; to enhance the mRNA transcript stability), a polyA signal, and an attB/attP-flanked PGK-FRT-Neo-polyA cassette. This entire construct was inserted between exons 1 and 2 of the Gt(ROSA)26Sor locus.	mouse gene=	facility=Core 3	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	mouseName=R26-LSL-ZsGreen (Ai6)	geneValid=true	holder=Wall, Estelle	First=E	pmid=20023653	modificationType=targeted knock-in	geneOfMutantAlleleMouse=104735	Last=Wall	Dept=animal care	source=Gt(ROSA)26Sor<tm6(CAG-ZsGreen1)Hze>	mta=	isPublished=Yes	ExpressedSequence=Reporter	strain=	reporter=ZsGreen	MouseType=Mutant Allele	repository=3809522	cryopreserved=Live only	Email=adminDB@ucsf.edu	rawMGIComment=The Rosa-CAG-LSL-ZsGreen1-WPRE targeting vector was designed with (from 5\' to 3\') a CMV-IE enhancer/chicken beta-actin/rabbit beta-globin hybrid promoter (CAG), an FRT site, a loxP-flanked STOP cassette (with stop codons in all 3 reading frames and a triple polyA signal), ZsGreen1 sequence (from pZsGreen1-N1 (Clontech); a human codon optimized/enhanced green fluorescent protein engineered for brighter fluorescence and higher expression in mammalian cells), a woodchuck hepatitis virus post-transcriptional regulatory element (WPRE; to enhance the mRNA transcript stability), a polyA signal, and an attB/attP-flanked PGK-FRT-Neo-polyA cassette. This entire construct was inserted between exons 1 and 2 of the Gt(ROSA)26Sor locus.	targetGeneValid=	','2019-09-16','accepted','','Y','Submission form'),
	(3028,'e','wall','cell biology','','adminDB@ucsf.edu','','comment=LoxP sites flank exons 2 and 3.	mouse gene=	facility=Core 3	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	mouseName=Actb flox	geneValid=true	holder=Joo, Adriane	First=e	pmid=20976199	modificationType=conditional allele (loxP/frt)	geneOfMutantAlleleMouse=87904	Last=wall	Dept=cell biology	source=Actb<tm1.1Erv>	mta=	isPublished=Yes	ExpressedSequence=	strain=	reporter=	MouseType=Mutant Allele	repository=4881824	cryopreserved=Live only	Email=adminDB@ucsf.edu	rawMGIComment=A floxed PGK-neomycin selection cassette in reverse orientation was inserted in intron 1 and a third loxP site was placed in intron 3.  The selection cassette was subsequently removed from properly targeted mice by crossing to transgenic mice carrying Tg(EIIa-cre)C5379Lmgd.	targetGeneValid=	','2019-09-17','accepted','','Y','Submission form'),
	(3029,'e','wall','cell biology','','adminDB@ucsf.edu','','comment=This allele from IMPC was generated at UC Davis by injecting CAS9 protein and 2 guide sequences which resulted in exon deletion.	mouse gene=	facility=Core 3	producedInLabOfHolder=No	other=	geneValidationString=Actc1 - actin, alpha, cardiac muscle 1	targetGeneValidationString=	mouseName=Actc1 null	geneValid=true	holder=Joo, Adriane	First=e	pmid=	modificationType=endonuclease-mediated	geneOfMutantAlleleMouse=87905	Last=wall	Dept=cell biology	source=Actc1<em1(IMPC)Mbp>	mta=	isPublished=No	ExpressedSequence=	strain=	reporter=	MouseType=Mutant Allele	repository=6276989	cryopreserved=Live only	Email=adminDB@ucsf.edu	rawMGIComment=	targetGeneValid=	','2019-09-17','accepted','','Y','Submission form'),
	(3030,'E','w','animal care','','adminDB@ucsf.edu','','comment=This transgene expresses Cre recombinase under the control of a human beta-actin promoter, which is active in all embryonic cells at or before the blastocyst stage. Line number 2 contains a single copy of the transgene inserted into intron 4 of <A HREF=\"http://www.informatics.jax.org/accession/MGI:1919410\">Tmem163</a> (chr1:129,442,346; NCBI37/mm9) with three intronic deletions of 2.2, 7.3 and 3.2 kb, respectively, surrounding the transgene integration site.	mouse gene=	facility=Core 1	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	regulatoryElement=human beta actin cre promoter	gensatFounderLine=	mouseName=beta-actin-Cre	TransgenicType=Random insertion	geneValid=	holder=Bush, Jeff	First=E	pmid=9598348	Dept=animal care	Last=w	source=Tmem163<Tg(ACTB-cre)2Mrt>	mta=	isPublished=Yes	ExpressedSequence=Cre	strain=	MouseType=Transgene	reporter=	repository=2176050	cryopreserved=Live only	Email=adminDB@ucsf.edu	rawMGIComment=This transgene expresses Cre recombinase under the control of a human beta-actin promoter, which is active in all embryonic cells at or before the blastocyst stage. Line number 2 contains a single copy of the transgene inserted into intron 4 of <A HREF=\"http://www.informatics.jax.org/accession/MGI:1919410\">Tmem163</a> (chr1:129,442,346; NCBI37/mm9) with three intronic deletions of 2.2, 7.3 and 3.2 kb, respectively, surrounding the transgene integration site.	knockedInGene=	targetGeneValid=	','2019-09-18','accepted','','Y','Submission form'),
	(3031,'Database','Administrator','database admin','','admin.mousedatabase@ucsf.edu',' ','pmid=23648512	other=	dueDateRaw=Mon Oct 07 01:27:54 PDT 2019	Purchaser lastname-0=Bush	Recipient PI Lastname-0=Bush	knockedInGene=	targetGeneValidationString=	regulatoryElement=	Purchaser email-0=jbush@ucsf.edu	Last=Administrator	producedInLabOfHolder=	Email=admin.mousedatabase@ucsf.edu	New Holder Email-0=jbush@ucsf.edu	gensatFounderLine=	ExpressedSequence=undetermined	targetGeneValid=	MouseType=Transgene	mouse gene=	isPublished=Yes	TransgenicType=undetermined	Recipient PI Name-0=Bush, Jeff	cryopreserved=	source=E2f1<Tg(Wnt1-cre)2Sor>	Dept=database admin	CatalogNumber=22137	Purchaser-0=Jeff Bush	NewMouseName=E2f1<Tg(Wnt1-cre)2Sor>, transgene insertion 2, Philippe Soriano	mta=	Vendor-0=Jax Mice	repository=5485027	facility=unassigned	comment=A transgenic insert containing cre recombinase, under the control of the 1.3 kb 5\' promoter and 5.5 kb 3\' enhancer of the mouse Wnt1, wingless-related MMTV integration site 1 was injected into fertilized B6C3 hybrid mouse eggs. Founder line 2 inserted into the gene at 154561346-154561603 (Build GRCm38/mm10) resulting in a 257 bp deletion and a 45 Kb inverted segment in exon 5 of the gene. The inversion contains all of exon 5, but deletes 23 Kb including exons 6 and 7. Founder line 2 has a copy number of  1-3. \n \n\n\n\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Inversion\r\n*mouseType:* Transgenic\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 	HolderFacilityList=2-1	geneValidationString=	Due Date=Monday, October 07	strain=	reporter=	Purchaser firstname-0=Jeff	holder=unassigned	officialMouseName=transgene insertion 2, Philippe Soriano	MouseMGIID=5485027	Recipient PI Firstname-0=Jeff	holderCount=1	First=Database	','2019-09-23','new','','N','Purchase Data Upload (PDU) Purchase Report'),
	(3032,'Database','Administrator','database admin','','admin.mousedatabase@ucsf.edu',' ','pmid=17166924	other=	dueDateRaw=Mon Oct 07 01:27:54 PDT 2019	Purchaser lastname-0=Bush	Recipient PI Lastname-0=Bush	knockedInGene=	targetGeneValidationString=	regulatoryElement=	Purchaser email-0=jbush@ucsf.edu	Last=Administrator	producedInLabOfHolder=	Email=admin.mousedatabase@ucsf.edu	New Holder Email-0=jbush@ucsf.edu	gensatFounderLine=	ExpressedSequence=undetermined	targetGeneValid=	MouseType=Transgene	mouse gene=	isPublished=Yes	TransgenicType=undetermined	Recipient PI Name-0=Bush, Jeff	cryopreserved=	source=Tg(Nes-cre/ERT2)KEisc	Dept=database admin	CatalogNumber=16261	Purchaser-0=Jeff Bush	NewMouseName=Tg(Nes-cre/ERT2)KEisc, transgene insertion K, Amelia Eisch	mta=	Vendor-0=Jax Mice	repository=3767432	facility=unassigned	comment=The Nestin-cre/ERT2 transgene was designed with the rat nestin (Nes) promoter driving expression of a CreERT2 fusion gene (cre/ERT2; Cre recombinase fused to a G400V/M543A/L544A triple mutation of the human estrogen receptor ligand binding domain). The transgene was microinjected into fertilized C57BL/6J oocytes, and line K was established.\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Insertion\r\n*mouseType:* Transgenic\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 	HolderFacilityList=2-1	geneValidationString=	Due Date=Monday, October 07	strain=	reporter=	Purchaser firstname-0=Jeff	holder=unassigned	officialMouseName=transgene insertion K, Amelia Eisch	MouseMGIID=3767432	Recipient PI Firstname-0=Jeff	holderCount=1	First=Database	','2019-09-23','new','','N','Purchase Data Upload (PDU) Purchase Report'),
	(3033,'Database','Administrator','database admin','','admin.mousedatabase@ucsf.edu',' ','pmid=	other=	dueDateRaw=Mon Oct 07 01:27:54 PDT 2019	Purchaser lastname-0=Wall	Recipient PI Lastname-0=Wall	targetGeneValidationString=	Purchaser email-0=ewall@ucsf.edu	Last=Administrator	producedInLabOfHolder=	Email=admin.mousedatabase@ucsf.edu	New Holder Email-0=ewall@ucsf.edu	modificationType=undetermined	ExpressedSequence=	targetGeneValid=	MouseType=Mutant Allele	mouse gene=	isPublished=No	Recipient PI Name-0=Wall, Estelle	cryopreserved=	source=Vip<tm1(cre)Zjh>	Dept=database admin	CatalogNumber=10908	Purchaser-0=Estelle Wall	NewMouseName=Vip<tm1(cre)Zjh>, targeted mutation 1, Z Josh Huang	mta=	Vendor-0=Jax Mice	repository=4431361	facility=unassigned	comment=A targeting vector was designed to insert an internal ribosome entry site (IRES), a cre recombinase sequence, an SV40 polyA signal, and an frt-flanked neo cassette into the 3\' untranslated region (after the translational termination site) of the vasoactive intestinal polypeptide locus (Vip). This construct was electroporated into C57BL/6 x 129S4/SvJae hybrid embryonic stem (ES) cells. Chimeric mice were bred with Actin-FLPe mice (on a C57BL/6 congenic background)  to generate the colony and remove the neo selection cassette. The FLPe transgene has been bred out of the line.\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Insertion\r\n*mouseType:* Targeted\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 	HolderFacilityList=5-1	geneValidationString=	Due Date=Monday, October 07	strain=	reporter=	Purchaser firstname-0=Estelle	geneOfMutantAlleleMouse=98933	holder=unassigned	officialMouseName=targeted mutation 1, Z Josh Huang	MouseMGIID=4431361	Recipient PI Firstname-0=Estelle	holderCount=1	First=Database	','2019-09-23','new','','N','Purchase Data Upload (PDU) Purchase Report'),
	(3034,'Database','Administrator','database admin','','admin.mousedatabase@ucsf.edu',' ','pmid=10805752	other=	dueDateRaw=Mon Oct 07 01:27:54 PDT 2019	Purchaser lastname-0=Joo	Recipient PI Lastname-0=Joo	targetGeneValidationString=	Purchaser email-0=ajoo@ucsf.edu	Last=Administrator	producedInLabOfHolder=	Email=admin.mousedatabase@ucsf.edu	New Holder Email-0=ajoo@ucsf.edu	modificationType=undetermined	ExpressedSequence=	targetGeneValid=	MouseType=Mutant Allele	mouse gene=	isPublished=Yes	Recipient PI Name-0=Joo, Adriane	cryopreserved=	source=Cx3cr1<tm1Litt>	Dept=database admin	CatalogNumber=5582	Purchaser-0=Adriane Joo	NewMouseName=Cx3cr1<tm1Litt>, targeted mutation 1, Dan R Littman	mta=	Vendor-0=Jax Mice	repository=2670351	facility=unassigned	comment=The endogenous locus was disrupted by the insertion of sequence encoding green fluourescent protein (GFP), replacing the first 390 bp of the coding exon (exon 2). The deleted region encoded an amino-terminal portion of the protein that is crucial for interaction with endogenous ligand, Cx3cl1. A floxed neo gene included in the targeting vector for selection was excised prior to germline transmission, leaving a single loxP site downstream of the GFP sequence. RT-PCR and flow cytometry indicated an absence of endogenous protein and the presence GFP expression in homozygous mutant mice.\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Insertion\r\n*mouseType:* Targeted\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 	HolderFacilityList=4-1	geneValidationString=	Due Date=Monday, October 07	strain=	reporter=	Purchaser firstname-0=Adriane	geneOfMutantAlleleMouse=1333815	holder=unassigned	officialMouseName=targeted mutation 1, Dan R Littman	MouseMGIID=2670351	Recipient PI Firstname-0=Adriane	holderCount=1	First=Database	','2019-09-23','new','','N','Purchase Data Upload (PDU) Purchase Report'),
	(3035,'Database','Administrator','database admin','','admin.mousedatabase@ucsf.edu',' ','pmid=	other=	dueDateRaw=Mon Oct 07 01:27:54 PDT 2019	Purchaser lastname-0=Laird	Recipient PI Lastname-0=Laird	targetGeneValidationString=	Purchaser email-0=dlaird@ucsf.edu	Last=Administrator	producedInLabOfHolder=	Email=admin.mousedatabase@ucsf.edu	New Holder Email-0=dlaird@ucsf.edu	modificationType=undetermined	ExpressedSequence=	targetGeneValid=	MouseType=Mutant Allele	mouse gene=	isPublished=No	Recipient PI Name-0=Laird, Diana	cryopreserved=	source=Gt(ROSA)26Sor<tm1(CAG-tdTomato*,-EGFP*)Ees>	Dept=database admin	CatalogNumber=23035	Purchaser-0=Diana Laird	NewMouseName=Gt(ROSA)26Sor<tm1(CAG-tdTomato*,-EGFP*)Ees>, targeted mutation 1, Edward E Schmidt	mta=	Vendor-0=Jax Mice	repository=5504463	facility=unassigned	comment=The nT-nG targeting vector was made by modifying the Rosa26 mT/mG plasmid (see JAX Stock No.\n007676). Modifications replaced the N-terminal membrane-targeting domains of tdTomato and EGFP with strong start codons, and added sequences at the C-termini of tdTomato and EGFP that encode amino acids 300-350 of the SRm160 nuclear assembly domain (to direct the stable integration of\neach fluorescent protein into the nucleus). The final nT-nG targeting vector contained (from 5\' to 3\') a CMV enhancer/chicken beta-actin core promoter (pCA), a loxP site, the nT cassette (tdTomato protein sequence [non-oligomerizing DsRed variant with a 12 residue linker fusing two copies of the protein (tandem dimer)] with C-terminal SRm160 nuclear targeting signal and a polyadenylation\nsignal), a second loxP site, the nG cassette (an enhanced green fluorescent protein [EGFP] sequence\nwith C-terminal SRm160 nuclear targeting signal and a polyadenylation signal), and an frt-flanked neo cassette. The SRm160 sequences attached to tdTomato and EGFP contain different synonymous mutations (and the SRm160 sequence in tdTomato also has a single non-synonymous mutation) to avoid potential homologous recombination in bacteria between the SRm160 sequences in the nT cassette and nG cassette.\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Insertion\r\n*mouseType:* Targeted\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 	HolderFacilityList=3-1	geneValidationString=	Due Date=Monday, October 07	strain=	reporter=	Purchaser firstname-0=Diana	geneOfMutantAlleleMouse=104735	holder=unassigned	officialMouseName=targeted mutation 1, Edward E Schmidt	MouseMGIID=5504463	Recipient PI Firstname-0=Diana	holderCount=1	First=Database	','2019-09-23','new','','N','Purchase Data Upload (PDU) Purchase Report'),
	(3036,'Database','Administrator','database admin','','admin.mousedatabase@ucsf.edu',' ','pmid=21983963	other=	dueDateRaw=Mon Oct 07 01:27:54 PDT 2019	Purchaser lastname-0=Joo	Recipient PI Lastname-0=Joo	knockedInGene=	targetGeneValidationString=	regulatoryElement=	Purchaser email-0=ajoo@ucsf.edu	Last=Administrator	producedInLabOfHolder=	Email=admin.mousedatabase@ucsf.edu	New Holder Email-0=ajoo@ucsf.edu	gensatFounderLine=	ExpressedSequence=undetermined	targetGeneValid=	MouseType=Transgene	mouse gene=	isPublished=Yes	TransgenicType=undetermined	Recipient PI Name-0=Joo, Adriane	cryopreserved=	source=Tg(Krt8-cre/ERT2)17Blpn	Dept=database admin	CatalogNumber=17947	Purchaser-0=Adriane Joo	NewMouseName=Tg(Krt8-cre/ERT2)17Blpn, transgene insertion 17, Cedric Blanpain	mta=	Vendor-0=Jax Mice	repository=5305437	facility=unassigned	comment=A transgene construct containing 3.5 kb of sequence upstream of the ATG start site of the mouse Krt8 gene (purified from BAC clone RP23-254K21), the beta globin intron, SV40 polyadenylation site and a cre/ERT2 segment was injected into C57Bl/6 fertilized eggs.  Four of seven founder mice displayed the expected pattern of expression and Founder line 17 was subsequently established. The transgenic animals were then bred to CD1 mice.\n\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Insertion\r\n*mouseType:* Transgenic\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 	HolderFacilityList=4-1	geneValidationString=	Due Date=Monday, October 07	strain=	reporter=	Purchaser firstname-0=Adriane	holder=unassigned	officialMouseName=transgene insertion 17, Cedric Blanpain	MouseMGIID=5305437	Recipient PI Firstname-0=Adriane	holderCount=1	First=Database	','2019-09-23','new','','N','Purchase Data Upload (PDU) Purchase Report'),
	(3037,'Database','Administrator','database admin','','admin.mousedatabase@ucsf.edu',' ','pmid=20023653	other=	dueDateRaw=Mon Oct 07 01:27:54 PDT 2019	Purchaser lastname-0=Wall	Recipient PI Lastname-0=Wall	targetGeneValidationString=	Purchaser email-0=ewall@ucsf.edu	Last=Administrator	producedInLabOfHolder=	Email=admin.mousedatabase@ucsf.edu	New Holder Email-0=ewall@ucsf.edu	modificationType=undetermined	ExpressedSequence=	targetGeneValid=	MouseType=Mutant Allele	mouse gene=	isPublished=Yes	Recipient PI Name-0=Wall, Estelle	cryopreserved=	source=Gt(ROSA)26Sor<tm14(CAG-tdTomato)Hze>	Dept=database admin	CatalogNumber=7914	Purchaser-0=Estelle Wall	NewMouseName=Gt(ROSA)26Sor<tm14(CAG-tdTomato)Hze>, targeted mutation 14, Hongkui Zeng	mta=	Vendor-0=Jax Mice	repository=3809524	facility=unassigned	comment=The Rosa-CAG-LSL-tdTomato-WPRE targeting vector was designed with (from 5\' to 3\') a CMV-IE enhancer/chicken beta-actin/rabbit beta-globin hybrid promoter (CAG), an FRT site, a loxP-flanked STOP cassette (with stop codons in all 3 reading frames and a triple polyA signal), tdTomato sequence (a non-oligomerizing DsRed fluorescent protein variant with a 12 residue linker fusing two copies of the protein (tandem dimer)), a woodchuck hepatitis virus post-transcriptional regulatory element (WPRE; to enhance the mRNA transcript stability), a polyA signal, and an attB/attP-flanked PGK-FRT-Neo-polyA cassette. This entire construct was inserted between exons 1 and 2 of the Gt(ROSA)26Sor locus. phiC31-recombination of <A HREF=\"http://www.informatics.jax.org/accession/MGI:3809523\">Gt(ROSA)26Sor</sup>tm9(CAG-tdTomato)Hze<sup></a> removed the neomycin resistance cassette.\n\n\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Insertion\r\n*mouseType:* Targeted\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 	HolderFacilityList=5-1	geneValidationString=	Due Date=Monday, October 07	strain=	reporter=	Purchaser firstname-0=Estelle	geneOfMutantAlleleMouse=104735	holder=unassigned	officialMouseName=targeted mutation 14, Hongkui Zeng	MouseMGIID=3809524	Recipient PI Firstname-0=Estelle	holderCount=1	First=Database	','2019-09-23','new','','N','Purchase Data Upload (PDU) Purchase Report'),
	(3038,'Diana','Laird','Stem Cell','','dlaird@ucsf.edu','','comment=The transgene comprises the cDNA encoding enhanced green fluorescent protein (EGFP),  from the jellyfish Aequoria victoria, downstream of the \"CAG promoter\" -- which consists of the cytomegalovirus immediate early (CMV-EI) enhancer followed by a 1.3-kb DNA segment including the promoter, first exon and first intron of the chicken beta-actin gene, with the 3\' splice junction sequence replaced by that of the rabbit hemoglobin beta gene -- and followed by the rabbit beta-hemoglobin polyadenylation signal and 3\' flanking sequence.	mouse gene=	facility=Core 2	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	regulatoryElement=CAG promoter	gensatFounderLine=	mouseName=Tg(CAG-EGFP)	TransgenicType=Random insertion	geneValid=	holder=Laird, Diana	First=Diana	pmid=9175875	Dept=Stem Cell	Last=Laird	source=Tg(CAG-EGFP)131Osb	mta=	isPublished=Yes	ExpressedSequence=Reporter	strain=	MouseType=Transgene	reporter=eGFP	repository=3055151	cryopreserved=Live only	Email=dlaird@ucsf.edu	rawMGIComment=The transgene comprises the cDNA encoding enhanced green fluorescent protein (EGFP),  from the jellyfish Aequoria victoria, downstream of the \"CAG promoter\" -- which consists of the cytomegalovirus immediate early (CMV-EI) enhancer followed by a 1.3-kb DNA segment including the promoter, first exon and first intron of the chicken beta-actin gene, with the 3\' splice junction sequence replaced by that of the rabbit hemoglobin beta gene -- and followed by the rabbit beta-hemoglobin polyadenylation signal and 3\' flanking sequence.	knockedInGene=	targetGeneValid=	','2019-09-23','new',NULL,'N','Submission form'),
	(3042,'Database','Administrator','database admin','','mousedatabase@ucsf.edu',' ','pmid=23014696	other=	dueDateRaw=Tue Nov 05 06:09:17 PST 2019	Recipient firstname-0=Jeff	Recipient PI Lastname-0=Bush	Recipient Email-0=jbush@ucsf.edu	targetGeneValidationString=	Last=Administrator	producedInLabOfHolder=	Email=mousedatabase@ucsf.edu	New Holder Email-0=jbush@ucsf.edu	Sender institution-0=UCLA	modificationType=undetermined	ExpressedSequence=	targetGeneValid=	MouseType=Mutant Allele	mouse gene=	isPublished=Yes	Recipient PI Name-0=Bush, Jeff	cryopreserved=	source=Arl13b<tm1.1Tc>	Dept=database admin	NewMouseName=Arl13b<tm1.1Tc>, targeted mutation 1.1, Tamara Caspary	mta=	Recipient lastname-0=Bush	Recipient-0=Jeff Bush	repository=6107225	facility=unassigned	comment=A targeting vector containing a loxP site and FRT site flanked PGK-puro deltaTK selection (pFlexible) cassette was inserted downstream of exon 2 of the targeted gene, and another loxP site was inserted upstream of exon 2. FLP-mediated recombination removed the neo cassette.\n\n\r\n\r\nRaw properties returned from MGI:\r\n*mutationType:* Insertion\r\n*mouseType:* Targeted\r\n	rawMGIComment=	geneValid=	Recipient Facility-0=Core 1	HolderFacilityList=2-4	geneValidationString=	Due Date=Tuesday, November 05	strain=	reporter=	Import notes-0=	geneOfMutantAlleleMouse=1915396	holder=unassigned	officialMouseName=targeted mutation 1.1, Tamara Caspary	MouseMGIID=6107225	Recipient PI Firstname-0=Jeff	holderCount=1	First=Database	','2019-10-22','new','','N','Import Data Upload (IDU) Import Report'),
	(3041,'e','w','animal care','','ewall@ucsf.edu','','comment=IRES-EGFP was inserted into the 3\' UTR in exon 4.	mouse gene=	facility=Core 1	producedInLabOfHolder=	other=	geneValidationString=	targetGeneValidationString=	mouseName=4get (Il4-GFP)	geneValid=true	holder=Bush, Jeff	First=e	pmid=11520464	modificationType=targeted knock-in	geneOfMutantAlleleMouse=96556	Last=w	Dept=animal care	source=Il4<tm1Lky>	mta=	isPublished=Yes	ExpressedSequence=Reporter	strain=	reporter=EGFP	MouseType=Mutant Allele	repository=2176574	cryopreserved=Live only	Email=ewall@ucsf.edu	rawMGIComment=A loxP-flanked neomycin cassette followed by an internal ribosomal entry site (IRES) and an EGFP gene was inserted into sequences corresponding to the 3\' untranslated region in exon 4.  The neomycin cassette was removed by Cre mediated germline recombination in chimeric mice to generate the final allele.  These mice express green fluorescent protein under the control of the Il4 promoter.	targetGeneValid=	','2019-10-16','accepted','','Y','Submission form');

/*!40000 ALTER TABLE `submittedmouse` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table transgenictype
# ------------------------------------------------------------

DROP TABLE IF EXISTS `transgenictype`;

CREATE TABLE `transgenictype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transgenictype` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

LOCK TABLES `transgenictype` WRITE;
/*!40000 ALTER TABLE `transgenictype` DISABLE KEYS */;

INSERT INTO `transgenictype` (`id`, `transgenictype`)
VALUES
	(1,'knock-in'),
	(2,'random insertion');

/*!40000 ALTER TABLE `transgenictype` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
