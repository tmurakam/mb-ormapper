# output dir (Android only)
OUTDIR = src

# Package name (Android only)
PKGNAME = com.example.person

# Primary key name (iOS only)
#PKEY = key

# class definitions
create_table :person, :class => :Person, :base_class => :PersonBase do |t|
  t.text :name
  t.integer :sex
  t.integer :age
  t.date :birth_date
  t.phone_number :text

  t.belongs_to :group, :class => :Group, :foreign_key => :group_id
end

create_table :group, :class => :Group do |t|
  t.text :name

  t.has_many :people, :class => :Person, :foreign_key => :group_id
end
