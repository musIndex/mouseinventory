# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20130205061729) do

  create_table "changerequest", :force => true do |t|
    t.integer "mouse_id"
    t.string  "firstname",               :limit => 128
    t.string  "lastname",                :limit => 128
    t.string  "email",                   :limit => 128
    t.string  "status",                  :limit => 7,   :default => "new"
    t.text    "user_comment"
    t.text    "admin_comment"
    t.date    "requestdate"
    t.date    "lastadmindate"
    t.text    "properties"
    t.integer "facility_id"
    t.string  "facility_name"
    t.string  "holder_email"
    t.string  "holder_name"
    t.integer "holder_id"
    t.integer "action_requested"
    t.string  "request_source"
    t.string  "cryo_live_status"
    t.text    "genetic_background_info"
  end

  create_table "email_templates", :force => true do |t|
    t.string    "name"
    t.string    "emailType"
    t.text      "subject"
    t.text      "body"
    t.string    "category"
    t.timestamp "date_updated", :null => false
  end

  create_table "emails", :force => true do |t|
    t.text      "recipients"
    t.text      "ccs"
    t.text      "bccs"
    t.string    "emailType"
    t.text      "subject"
    t.text      "body"
    t.string    "status"
    t.timestamp "date_created",     :null => false
    t.string    "category"
    t.string    "template_name"
    t.text      "attachment_names"
  end

  create_table "expressedsequence", :force => true do |t|
    t.string "expressedsequence"
  end

  create_table "facility", :force => true do |t|
    t.string  "facility"
    t.text    "description"
    t.string  "code",        :limit => 50
    t.integer "position"
  end

  create_table "flattened_mouse_search", :force => true do |t|
    t.integer "mouse_id"
    t.text    "searchtext"
  end

  add_index "flattened_mouse_search", ["searchtext"], :name => "searchtext"

  create_table "gene", :force => true do |t|
    t.string  "fullname",       :limit => 250
    t.string  "symbol",         :limit => 25
    t.string  "entrez_gene_id", :limit => 32,  :default => ""
    t.integer "mgi"
  end

  create_table "holder", :force => true do |t|
    t.string "firstname",          :limit => 80
    t.string "lastname",           :limit => 80
    t.string "department",         :limit => 80
    t.string "email",              :limit => 80
    t.string "tel",                :limit => 80
    t.date   "datevalidated"
    t.text   "validation_comment"
    t.string "alternate_email",    :limit => 80
    t.string "alternate_name",     :limit => 80
    t.string "status",                           :default => "active"
  end

  create_table "import_new_objects", :force => true do |t|
    t.integer "import_report_id", :limit => 8, :default => 0
    t.integer "object_id",        :limit => 8, :default => 0
    t.text    "object_data"
  end

  create_table "import_reports", :force => true do |t|
    t.string  "name"
    t.date    "creationdate"
    t.text    "reporttext",   :limit => 16777215
    t.integer "report_type"
  end

  create_table "literature", :force => true do |t|
    t.string "pmid",     :limit => 32
    t.text   "abstract"
  end

  create_table "mouse", :force => true do |t|
    t.string  "name"
    t.string  "strain_comment"
    t.string  "modification_type"
    t.integer "transgenictype_id",                         :default => -1
    t.text    "regulatory_element_comment"
    t.integer "expressedsequence_id",                      :default => -1
    t.string  "reporter_comment"
    t.text    "strain"
    t.integer "gene_id",                                   :default => -1
    t.integer "target_gene_id",                            :default => -1
    t.text    "general_comment"
    t.string  "other_comment"
    t.text    "source"
    t.integer "inbred_strain_id",                          :default => -1
    t.integer "mousetype_id",                              :default => -1
    t.string  "mta_required",               :limit => 1
    t.integer "repository_id",                             :default => 1
    t.string  "repository_catalog_number",                 :default => ""
    t.integer "submittedmouse_id"
    t.string  "holder_lastname_for_sort",   :limit => 32,  :default => ""
    t.string  "gensat",                     :limit => 100
    t.string  "cryopreserved",              :limit => 20
    t.string  "status",                     :limit => 20
    t.integer "endangered",                 :limit => 1
    t.string  "official_name"
  end

  add_index "mouse", ["submittedmouse_id"], :name => "submittedmouse_id"

  create_table "mouse_holder_facility", :force => true do |t|
    t.integer "mouse_id"
    t.integer "holder_id"
    t.integer "facility_id"
    t.integer "covert",           :limit => 1
    t.string  "cryo_live_status", :limit => 20
  end

  create_table "mouse_literature", :force => true do |t|
    t.integer "literature_id"
    t.integer "mouse_id"
  end

  create_table "mousetype", :force => true do |t|
    t.string "mousetype"
  end

  create_table "reference", :force => true do |t|
    t.string "reference",               :default => ""
    t.string "pmid",      :limit => 16
  end

  create_table "repository", :force => true do |t|
    t.string "repository"
  end

  create_table "settings", :force => true do |t|
    t.integer   "category_id"
    t.string    "name"
    t.string    "label"
    t.text      "setting_value"
    t.timestamp "date_updated",                  :null => false
    t.integer   "text_area_rows", :default => 0
  end

  create_table "source", :force => true do |t|
    t.string "source"
  end

  create_table "strain", :force => true do |t|
    t.string "strain"
  end

  create_table "submittedmouse", :force => true do |t|
    t.string "firstname",    :limit => 128
    t.string "lastname",     :limit => 128
    t.string "dept",         :limit => 256
    t.string "address",      :limit => 80
    t.string "email",        :limit => 128
    t.string "tel",          :limit => 32,  :default => ""
    t.text   "properties"
    t.date   "date"
    t.string "status",       :limit => 14,  :default => "new"
    t.text   "admincomment"
    t.string "entered",      :limit => 1,   :default => "N"
  end

  add_index "submittedmouse", ["status"], :name => "status"

  create_table "transgenictype", :force => true do |t|
    t.string "transgenictype"
  end

end
