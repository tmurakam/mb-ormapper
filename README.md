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

A sample database schema is 'sample-schama.def'.
To generate model source code based on the schema,

for iOS:

    $ ./ormapper-ios sample-schema.def

for Android:

    $ ./ormapper-android sample-schema.def

Add generated files to your application project.
Also you need to add source files in 'iOS' or 'Android' directory.


How to use : for iOS
--------------------

At first, generate database instance and register as a singleton.

    Database *db = [Database new];
    [Database setInstance:db];

Then call 'open' to create and load a database, call 'migrate'
to create and migrate tables.

    [db open:@"MyDatabase.db"];
    [db migrate];

To load data use 'finder' methods. These methods returns
NSMutableArray array of a model entities.

To load specified record, use 'find(int pid)' method.

To save a record, just call 'save' method of the model.


How to use : for Android
------------------------
TBD

Schema definition
-----------------

Schema syntax is:

    model_name: class_name, base_class_name
            type: column_name
            ...

    model_name: class_name, base_class_name
            type: column_name
            ...

A base model class file name is decided with base_class_name.
For iOS, file name will be 'base_class_name.h' and 'base_class_name.m'
For Android, it will be 'base_class_name.java'

You need to implement model class with 'class_name' name derived
from base_class_name.
Or you can use base class directly. In this case omit base_class_name
like as:

    model_name: base_class_name

Also you can omit base_class_name. In this case class name is 
same as model name

    model_name

Note: SQL table name is same as model name.
(Not plural like as Ruby on Rails)

'type' should be SQLite data type. Supporting types and
corresponding types of Objective-C or Java are:

    SQL type   Obj-C type    Java type
    -----------------------------------
    INTEGER    int           int
    REAL       double        double
    TEXT       NSString *    String
    DATE (*1)  NSDate *      long (*2)

    *1: 14 characters string ("yyyyMMddHHmmss") internally
    *2: Elapsed time from 1970/1/1 0:00 UTC in milliseconds.

Properties of model class is automatically generated based on
'column_name'.


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

