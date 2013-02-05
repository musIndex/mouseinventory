class AddHolderStatusColumn < ActiveRecord::Migration
  def up
    add_column :holder, :status, :string, :default => 'active'
  end

  def down
    remove_column :holder, :status
  end
end
