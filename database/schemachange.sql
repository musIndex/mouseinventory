-- This is a work in progress on re-architecting the database schema.

-- database schema update

-- create property tables

CREATE TABLE `property_definitions` (
  `property_definitionId` int(10) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  `typeId` int(10) NOT NULL default '0',
  `accessLevel` int(10) NOT NULL default '0',
  `validation` text,
  `allow_custom_listvalue` tinyint(1) NOT NULL default '0',
  `listvalues` text,
  PRIMARY KEY  (`property_definitionId`)
);





-- populate property definitions
-- property types (enum in code) 1=int,2=string,3=list,4=text,5=boolean
-- access levels (enum in code) 0=hidden,1=user,2=admin,3=owner,4=developer
-- id name type accessLevel validation allowcustomlist listvalues

LOCK TABLES `property_definitions` WRITE;
/*!40000 ALTER TABLE `property_definitions` DISABLE KEYS */;

INSERT INTO `property_definitions` VALUES
(1,'Modification Type',		3,1,NULL,1,	'targeted disruption|conditional allele (loxP/frt)|gene trap insertion|Chemically induced (ENU)|spontaneous mutation'),
(2,'Expressed Sequence',	3,1,NULL,1,	'mouse gene|Cre'),
(3,'Regulatory Element',	2,1,NULL,0,	NULL),
(4,'Gensat',				2,1,NULL,0,	NULL),
(5,'Endangered',			5,0,NULL,0,	NULL),
(6,'MTA Required',			3,0,NULL,0,	'Yes|No|Unknown');

/*!40000 ALTER TABLE `property_definitions` ENABLE KEYS */;
UNLOCK TABLES;


CREATE TABLE `property_categories` (
  `property_categoryId` int(10) NOT NULL auto_increment,
  `name` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`property_categoryId`)
);

LOCK TABLES `property_categories` WRITE;
/*!40000 ALTER TABLE `property_categories` DISABLE KEYS */;
INSERT INTO `property_categories` VALUES
(1,'Category'),
(2,'Details'),
(3,'Comments');
/*!40000 ALTER TABLE `property_categories` ENABLE KEYS */;
UNLOCK TABLES;



CREATE TABLE `property_definition_categories` (
  `property_definition_categoryId` int(10) NOT NULL auto_increment,
  `property_categoryId` int(10) NOT NULL default '0',
  `property_definitionId` int(10) NOT NULL default '0',
  PRIMARY KEY  (`property_definition_categoryId`)
);

LOCK TABLES `property_definition_categories` WRITE;
/*!40000 ALTER TABLE `property_definition_categories` DISABLE KEYS */;
INSERT INTO `property_definition_categories` VALUES
(1,1,1),
(2,2,1),
(3,3,2),
(4,4,2),
(5,5,2);
/*!40000 ALTER TABLE `property_definition_categories` ENABLE KEYS */;
UNLOCK TABLES;

-- populate property values from exisiting mice
CREATE TABLE `mouse_property_values` (
  `property_valueId` bigint(20) NOT NULL auto_increment,
  `mouseId` bigint(20) NOT NULL default '0',
  `property_definitionId` int(10) NOT NULL default '0',
  `valueText` text NOT NULL,
  PRIMARY KEY  (`property_valueId`)
);
-- modification type

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	SELECT id, 1, modification_type
	from mouse
	where mousetype_id=1
	and modification_type is not null;

-- expressed sequence

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	SELECT mouse.id, 2, expressedsequence
	from mouse left join expressedsequence on mouse.expressedsequence_id=expressedsequence.id
	where mouse.expressedsequence_id = 1 or mouse.expressedsequence_id = 2;

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	SELECT mouse.id, 2, other_comment
	from mouse
	where mouse.expressedsequence_id = 4
	and other_comment is not null;

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	SELECT mouse.id, 2, reporter_comment
	from mouse
	where mouse.expressedsequence_id = 3
	and reporter_comment is not null;

-- regulatory element

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	select mouse.id,3,regulatory_element_comment from mouse
	where regulatory_element_comment is not null
	and regulatory_element_comment <> '';

-- gensat

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	select mouse.id,4,gensat from mouse
	where gensat is not null
	and gensat <> '';

-- endangered

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	select mouse.id,5,1 from mouse
	where endangered is not null
	and endangered = 1;

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	select mouse.id,5,0 from mouse
	where endangered is not null
	and endangered = 0;


-- mta status
INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	select mouse.id,6,'Yes' from mouse
	where mta_required = 'Y';

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	select mouse.id,6,'No' from mouse
	where mta_required = 'N';

INSERT INTO mouse_property_values (mouseId,property_definitionId,valueText)
	select mouse.id,6,'Unknown' from mouse
	where mta_required = 'D';


-- create mouse gene table
CREATE TABLE `mouse_gene` (
  `mouse_geneId` bigint(20) unsigned NOT NULL auto_increment,
  `mouseId` bigint(20) unsigned NOT NULL default '0',
  `geneId` bigint(20) unsigned NOT NULL default '0',
  PRIMARY KEY  (`mouse_geneId`)
);

-- populate mouse gene table

INSERT INTO mouse_gene (mouseId,geneId)
	select id,gene_id
	from mouse
	where gene_id > 0;

INSERT INTO mouse_gene (mouseId,geneId)
	select id,target_gene_id
	from mouse
	where target_gene_id > 0;


-- create mouse repository table

CREATE TABLE `mouse_repository` (
  `mouse_repositoryId` bigint(20) NOT NULL auto_increment,
  `mouseId` bigint(20) NOT NULL default '0',
  `repositoryId` int(10) NOT NULL default '0',
  `catalog_number` varchar(255) NOT NULL default '',
  `symbol` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`mouse_repositoryId`)
);

ALTER TABLE `repository`
	ADD COLUMN displayformat text;

-- populate mouse repository table

INSERT INTO mouse_repository (mouseId,repositoryId,catalog_number,symbol)
SELECT id,5,repository_catalog_number,source
	from mouse
	where (mousetype_id=1 or mousetype_id=2)
	and repository_catalog_number is not null
	and repository_catalog_number <> ''
	and repository_catalog_number <> 'none';

-- create suppliers table


CREATE TABLE `suppliers` (
  `supplierId` int(11) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `displayformat` text,
  PRIMARY KEY  (`supplierId`)
);

-- populate suppliers table

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES
(1,'JAX Mice','http://jaxmice.jax.org/strain/${catalog_number}.html'),
(2,'Charles River',''),
(3,'Taconic',''),
(4,'Harlan',''),
(5,'Not commercial',''),
(6,'Other (name in comment)','');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;

-- create mouse suppliers table

CREATE TABLE `mouse_suppliers` (
  `mouse_supplierId` bigint(20) NOT NULL auto_increment,
  `mouseId` bigint(20) NOT NULL default '0',
  `supplierId` int(10) NOT NULL default '0',
  `catalogNumber` varchar(255),
  `catalogUrl` text,
  PRIMARY KEY  (`mouse_supplierId`)
);

-- populate mouse suppliers table


-- JAX
-- NOTE - need to manually update jax sources to common format (ends with 6 digit catalog number)

insert into mouse_suppliers (mouseId,supplierId,catalogNumber)
	select mouse.id,1,left(substring(source,6),6)
	from mouse
	where mousetype_id=3
	and source regexp('^JAX, [0-9]{6}');

insert into mouse_suppliers (mouseId,supplierId,catalogNumber)
	select mouse.id,1,left(substring(source,5),6)
	from mouse
	where mousetype_id=3
	and source regexp('^JAX [0-9]{6}');

insert into mouse_suppliers (mouseId,supplierId,catalogNumber)
	select mouse.id,1,left(substring(source,11),6)
	from mouse
	where mousetype_id=3
	and source regexp('^JAX Mice, [0-9]{6}');

insert into mouse_suppliers (mouseId,supplierId,catalogNumber)
	select mouse.id,1,concat('000',left(substring(source,6),3))
	from mouse
	where mousetype_id=3
	and source regexp('^JAX, [0-9]{3}, ');

-- Charles River

insert into mouse_suppliers (mouseId,supplierId,catalogUrl)
	select mouse.id,2,substring(source,instr(source,'||')+2)
	from mouse
	where mousetype_id=3
	and source regexp('Charles River')
	and instr(source,'||') > 0;

insert into mouse_suppliers (mouseId,supplierId)
	select mouse.id,2
	from mouse
	where mousetype_id=3
	and source regexp('Charles River')
	and instr(source,'||') = 0;

-- Taconic

insert into mouse_suppliers (mouseId,supplierId,catalogUrl)
	select mouse.id,3,substring(source,instr(source,'||')+2)
	from mouse
	where mousetype_id=3
	and source regexp('Taconic')
	and instr(source,'||') > 0;

insert into mouse_suppliers (mouseId,supplierId)
	select mouse.id,3
	from mouse
	where mousetype_id=3
	and source regexp('Taconic')
	and instr(source,'||') = 0;

-- Harlan

insert into mouse_suppliers (mouseId,supplierId,catalogUrl)
	select mouse.id,4,substring(source,instr(source,'||')+2)
	from mouse
	where mousetype_id=3
	and source regexp('Harlan')
	and instr(source,'||') > 0;

insert into mouse_suppliers (mouseId,supplierId)
	select mouse.id,4
	from mouse
	where mousetype_id=3
	and source regexp('Harlan')
	and instr(source,'||') = 0;

-- Not Commerical

insert into mouse_suppliers (mouseId,supplierId,catalogUrl)
	select mouse.id,5,substring(source,instr(source,'||')+2)
	from mouse
	where mousetype_id=3
	and source regexp('Not commercial')
	and instr(source,'||') > 0;

insert into mouse_suppliers (mouseId,supplierId)
	select mouse.id,5
	from mouse
	where mousetype_id=3
	and source regexp('Not commercial')
	and instr(source,'||') = 0;



-- create status table

CREATE TABLE `status` (
  `statusId` int(10) NOT NULL auto_increment,
  `status` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`statusId`)
) ;

-- populate status table

LOCK TABLES `status` WRITE;
/*!40000 ALTER TABLE `status` DISABLE KEYS */;
INSERT INTO `status` VALUES
(1,'live'),
(2,'deleted'),
(3,'incomplete');
/*!40000 ALTER TABLE `status` ENABLE KEYS */;
UNLOCK TABLES;

-- update mouse status to use statusId

ALTER TABLE mouse
	ADD COLUMN statusId int(10) NOT NULL;

UPDATE mouse
	SET statusId=1
	WHERE status='live';
UPDATE mouse
	SET statusId=2
	WHERE status='deleted';
UPDATE mouse
	SET statusId=3
	WHERE status='incomplete';



-- mouse_holder_status table

CREATE TABLE `mouse_holder_status` (
  `mouse_holder_statusId` int(10) NOT NULL auto_increment,
  `status` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`mouse_holder_statusId`)
) ;

LOCK TABLES `mouse_holder_status` WRITE;
/*!40000 ALTER TABLE `mouse_holder_status` DISABLE KEYS */;
INSERT INTO `mouse_holder_status` VALUES
(1,''),
(2,'Live only'),
(3,'Cryo only'),
(4,'Live and Cryo');
/*!40000 ALTER TABLE `mouse_holder_status` ENABLE KEYS */;
UNLOCK TABLES;

ALTER TABLE `mouse_holder_facility`
	ADD COLUMN `mouse_holder_statusId` int (10) NOT NULL;

-- populate mouse_holder_facility with status id

UPDATE `mouse_holder_facility`
	SET mouse_holder_statusId=1
	WHERE cryo_live_status='Live only';

UPDATE `mouse_holder_facility`
	SET mouse_holder_statusId=2
	WHERE cryo_live_status='Cryo only';

UPDATE `mouse_holder_facility`
	SET mouse_holder_statusId=3
	WHERE cryo_live_status='Live and Cryo';

ALTER TABLE `mouse_holder_facility`
	DROP COLUMN cryo_live_status;

-- removed unused tables
DROP TABLE IF EXISTS `expressedsequence`;
DROP TABLE IF EXISTS `modification_type`;
DROP TABLE IF EXISTS `reference`;
DROP TABLE IF EXISTS `source`;
DROP TABLE IF EXISTS `strain`;
DROP TABLE IF EXISTS `transgenictype`;

-- remove unused columns from tables;
ALTER TABLE mouse
	DROP COLUMN modification_type,
	DROP COLUMN expressedsequence_id,
	drop column strain_comment,
	drop column transgenictype_id,
	drop column regulatory_element_comment,
	drop column reporter_comment,
	drop column strain,
	drop column gene_id,
	drop column target_gene_id,
	drop column other_comment,
	drop column source,
	drop column inbred_strain_id,
	DROP COLUMN mta_required,
	drop column repository_id,
	drop column repository_catalog_number,
	drop column holder_lastname_for_sort,
	drop column gensat,
	drop column cryopreserved,
	drop column status,
	DROP COLUMN endangered;


