# output dir (Android only)
OUTDIR = "src"

# Package name (Android only)
PKGNAME = "com.example.person"

# Primary key name
#PKEY = "key"

# class definitions
create_table :people, :class => :Person, :base_class => :PersonBase do |t|
  t.text :name
  t.integer :sex
  t.integer :age
  t.date :birth_date
  t.text :phone_number
  t.integer :group_id

  t.belongs_to :group, :class => :Group, :field_name => :group_id
end

create_table :group, :class => :Group do |t|
  t.text :name

  t.has_many :people, :class => :Person, :field_name => :group_id
end
