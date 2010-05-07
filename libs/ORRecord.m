#import "Database.h"

@implementation ORRecord

@synthesize id;

- (id)init
{
    self = [super init];
    if (self) {
        isInserted = NO;
        tablesql = nil;
    }
        
    return self;
}

- (void)dealloc
{
    
    [super dealloc];
}

/**
  @brief Migrate database table

  You must override this to add column.
*/

+ (void)migrate:(NSString *)tableName columnTypes:(NSArray *)array
{
    Database *db = [Database instance];
    dbstmt *stmt;
    
    // check if table exists.
    NSString *sql, *tablesql;
    sql = [NSString stringWithFormat::@"SELECT sql FROM sqlite_master WHERE type='table' AND name='%@';", tableName];
    stmt = [db prepare:sql];

    // create table
    if ([stmt step] != SQLITE_ROW) {
        sql = [NSString stringWithFormat:@"CREATE TABLE %@ (id INTEGER PRIMARY KEY);"];
        [db exec:sql];
        tablesql = sql;
    } else {
        tablesql = [stmt colString:0];
    }

    // add columns
    int count = array.size / 2;

    for (int i = 0; i < count; i++) {
        NSString *column = [array objectAtIndex:i * 2];
        NSString *type = [array objectAtIndex:i * 2 + 1];

        NSRange range = [tablesql rangeOfString:[NSString stringWithFormat:@" %@ ", column]];
        if (range.location == NSNotFound) {
            sql = [NSString stringWithFormat:@"ALTER TABLE %@ ADD COLUMN %@ %@;",
                            tableName, column, type];
            [db exec:sql];
        }
    }
}

/**
  @brief get all records
  @return array of all record
*/
+ (NSMutableArray *)find_all
{
    return [self find_cond:nil];
}

/**
  @brief get all records matche the conditions

  @param cond Conditions (WHERE phrase and so on)
  @return array of records
*/
+ (NSMutableArray *)find_cond:(NSString *)cond
{
    NSMutableArray *array = [[[NSMutableArray alloc] init] autorelease];
    Database *db = [Database instance];
    dbstmt *stmt;

    NSString *sql;
    if (cond == nil) {
        sql = @"SELECT * FROM Person;"
    } else {
        sql = [NSString stringWithFormat:@"SELECT * FROM Person %@;", cond];
    }  

    stmt = [db prepare:sql];
    while ([stmt step] == SQLITE_ROW) {
        Person e = [[[Person alloc] init] autorelease];
        [e _loadRow:stmt];
        [array addObject:e];
    }
    return array;
}

/**
  @brief get the record matchs the id

  @param id Primary key of the record
  @return record
*/
+ (Person *)find:(int)id
{
    Database *db = [Database instance];

    dbstmt *stmt = [db prepare:@"SELECT * FROM Person WHERE id = ?;"];
    [stmt bindInt:0 val:id];
    if ([stmt step] != SQLITE_ROW) {
        return nil;
    }

    Person e = [[[Person alloc] init] autorelease];
    [e _loadRow:stmt];
 
    return e;
}

- (void)_loadRow:(dbstmt *)stmt
{
    self.id = [stmt colInt:0];
    self.name = [stmt colString:1];
    self.sex = [stmt colInt:2];
    self.age = [stmt colInt:3];
    self.birth_date = [stmt colDate:4];
    self.phone_number = [stmt colString:5];

    isInserted = YES;
}

/**
  @brief Save record
*/
- (void)save
{
    if (isInserted) {
        [self _update];
    } else {
        [self _insert];
    }
}

- (void)_insert
{
    Database *db = [Database instance];
    dbstmt *stmt;
    
    [db beginTransaction];
    stmt = [db prepare:@"INSERT INTO Person VALUES(NULL,?,?,?,?,?);"];

    [stmt bindString:0 val:name];
    [stmt bindInt:1 val:sex];
    [stmt bindInt:2 val:age];
    [stmt bindDate:3 val:birth_date];
    [stmt bindString:4 val:phone_number];
    [stmt step];

    self.id = [db lastInsertRowId];

    [db commitTransaction];
    isInserted = YES;
}

- (void)_update
{
    Database *db = [Database instance];
    [db beginTransaction];

    dbstmt *stmt = [db prepare:@"UPDATE Person SET "
        "name = ?,"
        "sex = ?,"
        "age = ?,"
        "birth_date = ?,"
        "phone_number = ?,"
        " WHERE id = ?;"];
    [stmt bindString:0 val:name];
    [stmt bindInt:1 val:sex];
    [stmt bindInt:2 val:age];
    [stmt bindDate:3 val:birth_date];
    [stmt bindString:4 val:phone_number];
    [stmt bindInt:5 val:id];

    [stmt step];
    [db commitTransaction];
}

/**
  @brief Delete record
*/
- (void)delete
{
    Database *db = [Database instance];

    dbstmt *stmt = [db prepare:@"DELETE FROM Person WHERE id = ?;"];
    [stmt bindInt:0 val:id];
    [stmt step];
}

@end
