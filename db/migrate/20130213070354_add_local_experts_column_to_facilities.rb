class AddLocalExpertsColumnToFacilities < ActiveRecord::Migration
  def up
    add_column :facility, :local_experts, :text
  end
  
  def down
    remove_column :facility, :local_experts
  end
end
