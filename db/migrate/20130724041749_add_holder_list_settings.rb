class AddHolderListSettings < ActiveRecord::Migration
  def up
	execute <<-EOF
INSERT INTO `settings` (`category_id`, `name`, `label`, `setting_value`,`position`)
VALUES
(12,'','First paragraph',"Provides the name of each PI whose mice are listed in the database (Holder) with contact information, including - when designated - the name and contact information for the person who serves as the Primary Contact for that Holder's laboratory.",0),
(12,'','Second paragraph',"<b>'Last review date' shows when the most recent update of the list of mice held by each investigator was carried out.  Holders who have not updated their list for an extended period of time are <span class='deadbeat_holder'>highlighted</span></b>",1),
(12,'','Third paragraph',"To perform a review and notify admin that it has been done, go to the page listing the mice held by an individual investigator (click on the number in the last column on the right).",2)
EOF
  end

  def down
  end
end
