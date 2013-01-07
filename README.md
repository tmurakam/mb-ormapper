O/R mapper for iOS / Android
============================

This is a O/R mapper tool for both iOS and Android.
This tool create model source code based on database schema.

Note: ARC is mandatory for iOS.

Prerequisite
------------

Ruby is needed to execute the tool.

How to create models
--------------------

A sample database schema is 'sample-schama.rb'.
To generate model source code based on the schema,

for iOS:

    $ ./ormapper-ios sample-schema.rb

for Android:

    $ ./ormapper-android sample-schema.rb

Add generated files to your application project.
Also you need to add source files in 'iOS' or 'Android' directory.


How to use : for iOS
--------------------

At first, get singleton instance of database and open database.

    Database *db = [Database instance];
    [db open:@"MyDatabase.db"];

Then call 'migrate' method of a model class to create and
migrate tables. For example, model class name is Person:

    [Person migrate];

To load data use 'finder' methods. These methods returns
NSMutableArray array of a model entities.

    NSMutableArray *people = [Person find_all];

To load specified record, use find:(int)pid method.

    Person *person = [Person find:1];

To save a record, just call 'save' method of the model.


How to use : for Android
------------------------
TBD

Schema definition
-----------------

Schema syntax is similar to RoR migration syntax.
Sample:

    create_table :people, :class => :Person, :base_class => :PersonBase do |t|
      t.text :name
      t.integer :sex
      t.integer :age
      t.integer :group_id

      t.belongs_to :group, :class => :Group, :field_name => :group_id
    end

Use create_table to define table and model.
With this example, table name is 'people' and base class name
is 'PersonBase'. You must implement class 'Person' derived from
PersonBase.

A base model class file name is decided with base_class name.
For iOS, file name will be 'base_class_name.h' and 'base_class_name.m'
For Android, it will be 'base_class_name.java'

All fields must be declared in the block.
Use text, integer, long, real, date method to define a field.
Supporting types and corresponding types of SQLite, Objective-C and
Java are:

    type       SQL type   Obj-C type    Java type
    ----------------------------------------------
    integer    INTEGER    int           int
    long       INTEGER    long          long
    real       REAL       double        double
    text       TEXT       NSString *    String
    date       DATE (*1)  NSDate *      long (*2)

    *1: 14 characters string ("yyyyMMddHHmmss") internally
    *2: Elapsed time from 1970/1/1 0:00 UTC in milliseconds.

Relationships
-------------

Relationships must be defined in the create_table block.
For 1 to many relationship, use belongs_to, has_many method.

For child table, use belongs_to with method name, parent 
class name, and field name.

    t.belongs_to :group, :class => :Group, :field_name => :group_id

For parent table, use has_many with method name, child
class name, and field name of child table.

    t.has_many :people, :class => :Person, :field_name => :group_id

Use has_one for 1 to 1 relationship.

Table migration
---------------

If new field is added in schema, new column is automatically
generated when migrate method is called.

Note: You can't modify or delete existing fields.


License
------------------

BSD license for library source code.

All generated source code has no license.

TODO
----

- Generate test class.

