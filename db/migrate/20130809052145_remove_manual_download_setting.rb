class RemoveManualDownloadSetting < ActiveRecord::Migration
  def up
    execute "delete from settings where name='download_files_manual' and category_id=8"
  end

  def down
  execute <<-EOF
    INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`)
		VALUES 
		(8,'download_files_manual','Download database manual', 'https://s3-us-west-1.amazonaws.com/mousedatabase-files/++Client+side+Database+manual-rev1.docx');
  EOF
  end
end
