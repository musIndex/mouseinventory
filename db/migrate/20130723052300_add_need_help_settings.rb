class AddNeedHelpSettings < ActiveRecord::Migration
  def up
  execute <<-EOF

INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`,`position`)
VALUES
(11,'','Request a demonstration','<a href="mailto:admin.mousedatabase@ucsf.edu">admin.mousedatabase@ucsf.edu</a>',0),
(11,'','Ask a Neighbor',"A number of people have volunteered to provide their neighbors with assistance in navigating the database, submitting change requests and entering new submissions.<br><br>To find the name of someone in your neighborhood who can help you with database questions, go to the <a href='/mouseinventory/FacilityReport.jsp'>Facilities List.</a>",1)

EOF
  end

 def down
 end
end
