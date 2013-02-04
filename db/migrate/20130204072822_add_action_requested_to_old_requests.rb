class AddActionRequestedToOldRequests < ActiveRecord::Migration
  def change
    execute <<-EOF
		update changerequest
		set action_requested=1
		where action_requested is NULL 
		and properties like '%Add Holder=%' 
		or properties like '%Add Holder Name=%'
		or user_comment like '%Please add an additional holder%'
		or user_comment like '%Additional holder: %'
		or user_comment like '%ADD HOLDER: %'
	EOF
	 execute <<-EOF
		update changerequest
		set action_requested=2
		where action_requested is NULL 
		and properties like '%Remove Holder=%' 
		or properties like '%Delete Holder Name=%'
		or user_comment like '%DELETE HOLDER: %'
	EOF
	execute <<-EOF
		update changerequest set action_requested=4 where action_requested is NULL;
	
	EOF
  end
end
