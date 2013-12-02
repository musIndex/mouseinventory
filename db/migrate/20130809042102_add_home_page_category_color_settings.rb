class AddHomePageCategoryColorSettings < ActiveRecord::Migration
  def up
    execute <<-EOF
  INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`, `secondary_value`)
	VALUES
	(13, 'category_3_color','Category color: Recent Site Updates', 'D3ECD3','629962'),
	(13, 'category_7_color','Category color: Help keep the database up-to-date', 'ADD8E6','8585AF'),
	(13, 'category_9_color','Category color: Did you know?', 'DFD3EE','9363DB'),
	(13, 'category_11_color','Category color: Need help using the database?', 'FCFCC7','565656')
EOF
  end

  def down
    execute "DELETE FROM settings where category_id=13"
  end
end
