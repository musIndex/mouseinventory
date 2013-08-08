class FixHomePageTextInfoAboutEachMouseCategory < ActiveRecord::Migration
  def up
	execute "update settings set category_id=2 where name='home_page_text_information_about_each_mouse'"
  end

  def down
	execute "update settings set category_id=12 where name='home_page_text_information_about_each_mouse'"
  end
end
