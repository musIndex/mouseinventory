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

