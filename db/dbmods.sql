#1 - new gensat column
alter table mouse add column gensat varchar(100);
update mouse set gensat=repository_catalog_number where repository_id =(select id from repository where repository='gensat');

#2 - use text datatype for comments to allow long comments
alter table mouse modify general_comment text;


#3 - new column for change requests
alter table changerequest add column properties text;


#4 - change transgenic knock-in mice to mutant allele mice with a modification type of 'knock-in'
#change mouse type to mutant allele and set modification type to knock-in
update mouse
set modification_type='targeted knock-in',
mousetype_id=(select id from mousetype where mousetype.mousetype = 'Mutant Allele')
where transgenictype_id = (select id from transgenictype where transgenictype.transgenictype = 'knock-in');
#remove transgenic type
update mouse
set transgenictype_id = -1
where mousetype_id=(select id from mousetype where mousetype.mousetype = 'Mutant Allele')
and modification_type='targeted knock-in';
#swap target gene id and gene id
UPDATE mouse SET gene_id=target_gene_id, target_gene_id=@temp WHERE (@temp:=gene_id) IS NOT NULL
and mousetype_id=(select id from mousetype where mousetype.mousetype = 'Mutant Allele')
and modification_type='targeted knock-in';

#5 - add cryopreserved column
alter table mouse add column cryopreserved varchar(20);

#6 - add mouse record status column
alter table mouse add column status varchar(20);
update mouse set status='live';

#7 - add covert holders column
alter table mouse_holder_facility add column covert tinyint;
update mouse_holder_facility set covert=0;

#8 - move accepted not-yet-entered submissions back to new
update submittedmouse set status='new' where status='accepted' and entered='N';

#9 - move need more info submisisons back to new
update submittedmouse set status='new',admincomment=concat(admincomment,' NEEDS MORE INFO') where status='need more info';

#10 - add endangered column
alter table mouse add column endangered tinyint;
update mouse set endangered=0;

#11 - underscores -> apostrophes
update mouse set regulatory_element_comment=REPLACE(regulatory_element_comment,"_","'") where regulatory_element_comment like '%\_%';
update mouse set general_comment=REPLACE(general_comment,"_","'") where general_comment like '%\_%';

#12 - longer field lengths
alter table changerequest modify column firstname varchar(128);
alter table changerequest modify column lastname varchar(128);
alter table changerequest modify column email varchar(128);

alter table submittedmouse modify column firstname varchar(128);
alter table submittedmouse modify column lastname varchar(128);
alter table submittedmouse modify column dept varchar(256);
alter table submittedmouse modify column email varchar(128);

#13 - facilities description
alter table facility add column description text;

#14 - change admin comment to text
alter table submittedmouse modify admincomment text;

#15 - cryo on per-mouse holder basis
alter table mouse_holder_facility add column cryo_live_status varchar(20);

#16 - loxp sites to loxp/frt
update mouse set modification_type='conditional allele (loxP/frt)' where modification_type='conditional allele (loxP sites)';

#17 - enu induced point to chemically induced
update mouse set modification_type='Chemically induced (ENU)' where modification_type='ENU induced point mutation';

#18 - allow dont know for MTA
alter table mouse modify column mta_required ENUM('Y','N','D');

#19 - longer source entries
alter table mouse modify column source text;

#20 - int for mgi numbers
alter table gene modify column mgi int(20);


#21 - table for storing auto-import output
CREATE TABLE `import_reports` (
  `id` int(11) NOT NULL auto_increment,
  `name` int(11) default NULL,
  `creationdate` date default NULL,
  `reporttext` mediumtext,
 PRIMARY KEY  (`id`));

#22 - holder last validation date
alter table holder add column datevalidated date,add column validation_comment text;

#23 - update expressed sequences
update expressedsequence set expressedsequence='Mouse gene (unmodified)' where id=1;
update expressedsequence set expressedsequence='Modified mouse gene or other' where id=4;

#24 - update expressed sequence again
update expressedsequence set expressedsequence='Mouse Gene (unmodified)' where id=1;
update expressedsequence set expressedsequence='Modified mouse gene or Other' where id=4;

#25 - add official mouse name column
alter table mouse add column official_name varchar(255);

#26 - track import report changes
alter table import_reports add column report_type int(20);

CREATE TABLE `import_new_objects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `import_report_id` bigint(20) DEFAULT '0',
  `object_id` bigint(20) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#27 - facility codes for imports
alter table facility add column code varchar(50);

#28 - text field for regulatory element
alter table mouse modify column regulatory_element_comment text;

#29 - text field for background strain
alter table mouse modify column strain text;

#30 - alternate lab manager email for holders
alter table holder add column alternate_email varchar(80);

#31 - add fulltext index to mouse search
#IMPORTANT - make sure you set ft_min_word_len=2 in my.cnf under [mysqld]
#IMPORTANT - also set ft_stopword_file='' in my.cnf under [mysqld]
#            These two options make searching more useful for scientific searches
alter table flattened_mouse_search add fulltext(searchtext);
repair table flattened_mouse_search;

#32 - add object data table to import_new_objects
alter table import_new_objects add column object_data text;

#33 - add alternate lab manager name
alter table holder add column alternate_name varchar(80);

#34 - Transgenic -> Transgene
update mousetype set mousetype='Transgene' where mousetype='Transgenic';

#35 - new tables - settings and sent emails

CREATE TABLE `settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` varchar(255),
  `name` varchar(255),
  `label` varchar(255),
  `setting_value` text,
  `date_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE `emails` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recipients` text,
  `ccs` text,
  `bccs` text,
  `emailType` varchar(255),
  `subject` text,
  `body` text,
  `status` varchar(255),
  `date_created` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

#36 add category field to emails
alter table `emails` add column `category` varchar(255);

#37 add table email_templates
CREATE TABLE `email_templates` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255),
  `emailType` varchar(255),
  `subject` text,
  `body` text,
  `category` varchar(255),
  `date_updated` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


#38 add template_name field to emails
alter table `emails` add column `template_name` varchar(255);

#39 add indexes to speed up submission loading
create index submittedmouse_id on mouse (submittedmouse_id);
create index status on submittedmouse (status);

#40 change setting category to category id
update `settings` set category=1 where category='admin_notes';

alter table `settings` CHANGE category category_id int(10);

#41 add about page content as setting
# <<<Load external file settings_content_1.sql >>>

#42 add position column to facilities
alter table `facility` add column position int(10);

#43 add attachment names column to emails
alter table `emails` add column attachment_names text;

#44 add download manual setting
INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
VALUES (8,'download_files_manual','Download database manual', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/++Client+side+Database+manual-rev1.docx');
	
#45 add download help docs settings
INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
VALUES (8,'download_files_allele_id','How to find the MGI allele detail page (PDF)', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGIAlleleID.pdf'),
	   (8,'download_files_gene_id','How to find the MGI gene ID (PDF)', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGIGeneID.pdf'),
	   (8,'download_files_transgene_id','How to find the MGI transgene detail page (PDF)', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGITransgeneID.pdf');
	
#46 add site hostname setting
INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
VALUES (5,'general_site_hostname','Site protocol and hostname', 'https://mousedatabase.ucsf.edu');

#47 add text_area_rows column to settings
alter table `settings` add column text_area_rows int(10) default 0;
update `settings` set text_area_rows=20, label='Ignored JAX Catalog numbers, one per line.  Blank lines are OK.' where name='import_ignored_jax_numbers';
	
#48 clean up orphaned entries in mouse_holder_facility
create temporary table orphaned_mice as 
	(select mouse_holder_facility.id 
	from  mouse_holder_facility left join mouse on mouse_holder_facility.mouse_id=mouse.id 
	where mouse.id is null);
delete from mouse_holder_facility where id in (select id from orphaned_mice);
drop table orphaned_mice;

#49 add version tracking table
CREATE TABLE `schema_migrations` (
    `version` varchar(255) NOT NULL,
      UNIQUE KEY `unique_schema_migrations` (`version`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

insert into `schema_migrations` (`version`) values ('20121126012149'), ('20121126022718'), ('20121126022831');

	

