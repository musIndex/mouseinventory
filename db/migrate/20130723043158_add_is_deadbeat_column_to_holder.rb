class AddIsDeadbeatColumnToHolder < ActiveRecord::Migration
  def up
	add_column :holder, :is_deadbeat, :tinyint
  end
  
  def down
    remove_column :holder, :is_deadbeat
  end
end
