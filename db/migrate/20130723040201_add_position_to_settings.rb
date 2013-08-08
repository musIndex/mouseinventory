class AddPositionToSettings < ActiveRecord::Migration
  def up
	  add_column :settings, :position, :int
  end
  
  def down
	  remove_column :settings, :position
  end
end
