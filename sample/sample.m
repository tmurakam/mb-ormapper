#import "Person.h"

@class Sample

- (void)sample;

@end

@implementation Sample

- (void)sample
{
    // Open & create database
    Database *db = [Database instance];
    [db open];
    
    // Migrate tables
    [Person migrate];

    // load data
    NSMutableArray *persons = [Person find_all];

    // add data
    Person *person = [[[Person alloc] init] autorelease];
    person.name = "John Doe";
    person.age = 26;
    person.sex = 0;
    [person save];

    // update data
    person.age = 27;
    [person save];

    // delete data
    [person delete];
}

@end


    
