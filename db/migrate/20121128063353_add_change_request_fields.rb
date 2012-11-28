class AddChangeRequestFields < ActiveRecord::Migration
  def up
    add_column :changerequest, :facility_id, :integer;
    add_column :changerequest, :facility_name, :string, :limit => 255;
    add_column :changerequest, :holder_email, :string, :limit => 255;
    add_column :changerequest, :holder_name, :string, :limit => 255;
    add_column :changerequest, :holder_id , :integer;  
    add_column :changerequest, :action_requested, :integer;
    add_column :changerequest, :request_source, :string, :limit => 255;
    add_column :changerequest, :cryo_live_status, :string, :limit => 255;
    add_column :changerequest, :genetic_background_info, :text;
  end

  def down
    remove_column :changerequest, :facility_id;
    remove_column :changerequest, :facility_name;
    remove_column :changerequest, :holder_email;
    remove_column :changerequest, :holder_name;
    remove_column :changerequest, :holder_id;  
    remove_column :changerequest, :action_requested;
    remove_column :changerequest, :request_source;
    remove_column :changerequest, :cryo_live_status;
    remove_column :changerequest, :genetic_background_info;
  end
end




