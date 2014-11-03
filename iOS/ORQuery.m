// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-

#import "Database.h"
#import "ORRecord.h"
#import "ORQuery.h"

@interface ORQuery()
{
    Class _targetClass;

    NSString *_tableName;
    NSString *_where;
    NSArray *_whereParams;
    NSString *_order;
    NSInteger _limit;
    NSInteger _offset;
}
@end

@implementation ORQuery

+ (ORQuery *)getWithClass:(Class)class tableName:(NSString *)tableName
{
    return [[ORQuery alloc] initWithClass:class tableName:tableName];
}

- (id)initWithClass:(Class)class tableName:(NSString *)tableName
{
    self = [super init];
    if (self != nil) {
        _targetClass = class;
        _tableName = tableName;
    }
    return self;
}
    
/**
 * Set 'WHERE' conditions
 * @param cond conditions
 * @param params parameters for each placeholders
 * @return this
 * @note You can set only 1 where condition.
 */
- (ORQuery *)where:(NSString *)where arguments:(NSArray *)args
{
    _where = where;
    _whereParams = args;
    return self;
}
    
/**
 * Set 'WHERE' conditions
 * @param column column name
 * @param param parameter (string)
 * @return this
 * @note You can set only 1 where condition.
 */
- (ORQuery *)where_eq:(NSString *)column arg:(NSString *)arg
{
    _where = [column stringByAppendingString:@" = ?"];
    _whereParams = @[arg];
    return self;
}

/**
 * set 'ORDER BY' parameter
 * @param order ORDER BY parameter string
 * @return this
 */
- (ORQuery *)order:(NSString *)order
{
    _order = order;
    return self;
}
    
/**
 * set 'LIMIT' parameter 
 * @param limit parameter
 * @return this
 */
- (ORQuery *)limit:(NSInteger)limit
{
    _limit = limit;
    return self;
}
    
/**
 * set 'OFFSET' parameter
 * @param offset offset parameter
 * @return this
 */
- (ORQuery*)offset:(NSInteger)offset
{
    _offset = offset;
    return self;
}

/**
 * Execute query and returns all elements
 * @return elements
 */
- (NSMutableArray *)all
{
    NSMutableArray *array = [NSMutableArray new];
    NSMutableString *sql = [self getSql];

    // bind arguments
    dbstmt *stmt = [[Database instance] prepare:sql];
    
    for (NSInteger i = 0; i < [_whereParams count]; i++) {
        [stmt bindString:i val:(NSString *)_whereParams[i]];
    }

    while ([stmt step] == SQLITE_ROW) {
        ORRecord *e = [_targetClass new];
        [e _loadRow:stmt];
        [array addObject:e];
    }
    return array;
}
    
/**
 * Execute query and get first element
 * @return first element
 */
- (id)first
{
    _limit = 1;
        
    NSMutableArray *ary = [self all];
    if ([ary count] == 0) {
        return nil;
    }
    return [ary objectAtIndex:0];
}

/**
 * get SQL statement
 * @return SQL statement
 * @note placeholder('?') are not be expanded
 */
- (NSMutableString *)getSql
{
    NSMutableString *sql = [NSMutableString new];
    
    [sql appendFormat:@"SELECT * FROM %@", _tableName];
    
    // Where 節
    if (_where != nil) {
        [sql appendString:@" WHERE "];
        [sql appendString:_where];
    }
    
    // Order
    if (_order != nil) {
        [sql appendString:@" ORDER BY "];
        [sql appendString:_order];
    }
    
    // Limit
    if (_limit > 0) {
        [sql appendFormat:@" LIMIT %ld", (long)_limit];
    }
    
    // Offset
    if (_offset > 0) {
        [sql appendFormat:@" OFFSET %ld", (long)_offset];
    }
    
    return sql;
}

@end

