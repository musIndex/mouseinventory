class AddRequestSourceToOldRequests < ActiveRecord::Migration
  def change
    ['PI to PI Transfer Report',
	 'PI to PI Transfer Import',
	 'LARC Transfer Report',
	 'New Purchases Import'].each do |source|
	  execute <<-EOF
	    update changerequest set request_source='#{source}'
	    where properties like '%Request source=#{source}%' 
	    and request_source is NULL;
	  EOF
	end
	execute "update changerequest set request_source='Change request form' where request_source is NULL;"
  end
end
