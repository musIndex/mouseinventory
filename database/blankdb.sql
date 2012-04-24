# ************************************************************
# Sequel Pro SQL dump
# Version 3348
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.5.13)
# Database: mouse_inventory
# Generation Time: 2011-09-25 11:58:44 -0700
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
  `user_comment` text,
  `admin_comment` text,
  `requestdate` date DEFAULT NULL,
  `lastadmindate` date DEFAULT NULL,
  `properties` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table expressedsequence
# ------------------------------------------------------------

DROP TABLE IF EXISTS `expressedsequence`;

CREATE TABLE `expressedsequence` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `expressedsequence` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

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
  `description` text,
  `code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

LOCK TABLES `facility` WRITE;
/*!40000 ALTER TABLE `facility` DISABLE KEYS */;

INSERT INTO `facility` (`id`, `facility`, `description`, `code`)
VALUES
	(1,'NA',NULL,NULL);

/*!40000 ALTER TABLE `facility` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table flattened_mouse_search
# ------------------------------------------------------------

DROP TABLE IF EXISTS `flattened_mouse_search`;

CREATE TABLE `flattened_mouse_search` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mouse_id` int(11) DEFAULT NULL,
  `searchtext` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



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
  `validation_comment` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


LOCK TABLES `holder` WRITE;
/*!40000 ALTER TABLE `holder` DISABLE KEYS */;

INSERT INTO `holder` (`id`, `firstname`, `lastname`, `department`, `email`, `tel`, `datevalidated`, `validation_comment`)
VALUES
	(1,'','','','','',NULL,NULL);

/*!40000 ALTER TABLE `holder` ENABLE KEYS */;
UNLOCK TABLES;




# Dump of table import_new_objects
# ------------------------------------------------------------

DROP TABLE IF EXISTS `import_new_objects`;

CREATE TABLE `import_new_objects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `import_report_id` bigint(20) DEFAULT '0',
  `object_id` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table import_reports
# ------------------------------------------------------------

DROP TABLE IF EXISTS `import_reports`;

CREATE TABLE `import_reports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `creationdate` date DEFAULT NULL,
  `reporttext` mediumtext,
  `report_type` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table literature
# ------------------------------------------------------------

DROP TABLE IF EXISTS `literature`;

CREATE TABLE `literature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pmid` varchar(32) DEFAULT NULL,
  `abstract` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table mouse
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mouse`;

CREATE TABLE `mouse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `strain_comment` varchar(255) DEFAULT NULL,
  `modification_type` varchar(255) DEFAULT NULL,
  `transgenictype_id` int(11) DEFAULT '-1',
  `regulatory_element_comment` varchar(255) DEFAULT NULL,
  `expressedsequence_id` int(11) DEFAULT '-1',
  `reporter_comment` varchar(255) DEFAULT NULL,
  `strain` varchar(255) DEFAULT NULL,
  `gene_id` int(11) DEFAULT '-1',
  `target_gene_id` int(11) DEFAULT '-1',
  `general_comment` text,
  `other_comment` varchar(255) DEFAULT NULL,
  `source` text,
  `inbred_strain_id` int(11) DEFAULT '-1',
  `mousetype_id` int(11) DEFAULT '-1',
  `mta_required` enum('Y','N','D') DEFAULT NULL,
  `repository_id` int(11) DEFAULT '1',
  `repository_catalog_number` varchar(255) DEFAULT '',
  `submittedmouse_id` int(11) DEFAULT NULL,
  `holder_lastname_for_sort` varchar(32) DEFAULT '',
  `gensat` varchar(100) DEFAULT NULL,
  `cryopreserved` varchar(20) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `endangered` tinyint(4) DEFAULT NULL,
  `official_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



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



# Dump of table mouse_literature
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mouse_literature`;

CREATE TABLE `mouse_literature` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `literature_id` int(11) DEFAULT NULL,
  `mouse_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table mousetype
# ------------------------------------------------------------

DROP TABLE IF EXISTS `mousetype`;

CREATE TABLE `mousetype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mousetype` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;



LOCK TABLES `mousetype` WRITE;
/*!40000 ALTER TABLE `mousetype` DISABLE KEYS */;

INSERT INTO `mousetype` (`id`, `mousetype`)
VALUES
	(1,'Mutant Allele'),
	(2,'Transgenic'),
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
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

LOCK TABLES `repository` WRITE;
/*!40000 ALTER TABLE `repository` DISABLE KEYS */;

INSERT INTO `repository` (`id`, `repository`)
VALUES
	(1,'none'),
	(6,'GENSAT'),
	(5,'MGI');

/*!40000 ALTER TABLE `repository` ENABLE KEYS */;
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
  `properties` text,
  `date` date DEFAULT NULL,
  `status` enum('new','accepted','rejected','need more info') DEFAULT 'new',
  `admincomment` text,
  `entered` enum('Y','N') DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table transgenictype
# ------------------------------------------------------------

DROP TABLE IF EXISTS `transgenictype`;

CREATE TABLE `transgenictype` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transgenictype` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;


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
