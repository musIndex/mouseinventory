class PopulateSubmissionSourceColumnForOldSubmissions < ActiveRecord::Migration
  def change
    ['MGI Submission tool',
	 'Purchase Data Upload (PDU)',
	 'Purchase Data Import'].each do |source|
      execute <<-EOF
	  	  update submittedmouse
		  set submission_source='#{source}'
		  where admincomment like '%Auto-generated from #{source}%' 
	  EOF
	end
	execute "update submittedmouse set submission_source='Submission form' where submission_source is NULL";
  end
end
