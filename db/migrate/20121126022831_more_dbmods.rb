class MoreDbmods < ActiveRecord::Migration
  def up

    execute <<-EOF
ALTER TABLE flattened_mouse_search ENGINE = MYISAM;
    EOF

    execute <<-EOF
alter table flattened_mouse_search add fulltext(searchtext);
    EOF



    execute <<-EOF
INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
VALUES (8,'download_files_manual','Download database manual', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/++Client+side+Database+manual-rev1.docx');
    EOF

    execute <<-EOF
INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
VALUES (8,'download_files_allele_id','How to find the MGI allele detail page (PDF)', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGIAlleleID.pdf'),
     (8,'download_files_gene_id','How to find the MGI gene ID (PDF)', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGIGeneID.pdf'),
     (8,'download_files_transgene_id','How to find the MGI transgene detail page (PDF)', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/HelpTextFindingTheMGITransgeneID.pdf');
    EOF

    execute <<-EOF
INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
VALUES (5,'general_site_hostname','Site protocol and hostname', 'https://mousedatabase.ucsf.edu');
    EOF

    execute <<-EOF
alter table `settings` add column text_area_rows int(10) default 0;
    EOF

    execute <<-EOF
update `settings` set text_area_rows=20, label='Ignored JAX Catalog numbers, one per line.  Blank lines are OK.' where name='import_ignored_jax_numbers';
    EOF
  end

  def down
  end
end
