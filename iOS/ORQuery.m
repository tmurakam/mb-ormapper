// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-

#import "Database.h"
#import "ORRecord.h"

@interface ORQuery()
{
    Class mTargetClass;

    NSString *mTableName;
    NSString *mWhere;
    NSArray *mWhereParams;
    NSString *mOrder;
    int mLimit;
    int mOffset;
}
@end

@implementation ORQuery

+ (ORQuery *)get:(Class)class tableName:(NSString *)tableName
{
    return [[ORQuery alloc] initWithTableName:tableName];
}

- (id)init:(Class)class tableName:(NSSTring *)tableName
{
    self = [super init];
    if (self != nil) {
        mTargetClass = class;
        mTableName = tableName;
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
- (ORQuery *)where:(NSString *)where args:(NSArray *)args
{
    mWhere = where;
    mWhereParams = args;
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
    mWhere = [column stringByAppendingString:@" = ?"];
    mWhereParams = @[arg];
    return self;
}

/**
 * set 'ORDER BY' parameter
 * @param order ORDER BY parameter string
 * @return this
 */
- (ORQuery *)order:(NSString *)order
{
    mOrder = order;
    return self;
}
    
/**
 * set 'LIMIT' parameter 
 * @param limit parameter
 * @return this
 */
- (ORQuery *)limit:(int)limit
{
    mLimit = limit;
    return self;
}
    
/**
 * set 'OFFSET' parameter
 * @param offset offset parameter
 * @return this
 */
- (ORQuery*)offset:(int)offset
{
    mOffset = offset;
    return self;
}

/**
 * Execute query and returns all elements
 * @return elements
 */
- (NSMutableArray *)all
{
    NSMutableArray *array = [NSMutableArray new];
    NSMutableString *sql = [NSMutableString new];

    [sql appendFormat:@"SELECT FROM %@", mTableName];

    // Where 節
    if (mWhere != nil) {
        [sql appendString:@" WHERE "];
        [sql appendString:mWhere];
    }

    // Order
    if (mOrder != nil) {
        [sql appendString:@" ORDER BY "];
        [sql appendString:mOrder];
    }

    // Offset
    if (mOffset > 0) {
        [sql appendFormat:@" OFFSET %d", mOffset];
    }
    // Limit
    if (mLimit > 0) {
        [sql appendFormat:@" LIMIT %d", mLimit];
    }

    // bind arguments
    dbstmt *stmt = [[Database instance] prepare:sql];
    
    for (int i = 0; i < [mWhereParam count]; i++) {
        [stmt bindString:i val:(NSString *)[mWhereParam objectAtIndex:n]];
    }

    while ([stmt step] == SQLITE_ROW) {
        id e = [mTargetClass new];
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
    mLimit = 1;
        
    NSMutableArray *ary = [self all];
    if ([ary count] == 0) {
        return nil;
    }
    return [ary objectAtIndex:0];
}

    
    /**
     * Execute query
     * @return Cursor
     */
    private Cursor execQuery() {
        String sql = getSql();

        SQLiteDatabase db = ORDatabase.getDB();
        Cursor cursor = db.rawQuery(sql.toString(), mWhereParams);

        return cursor;
    }

    /**
     * get SQL statement
     * @return SQL statement
     * @note placeholder('?') are not be expanded
     */
    public String getSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ");
        sql.append(mTableName);
        
        // where conditions
        if (mWhere != null) {
            sql.append(" WHERE ");
            sql.append(mWhere);
        }
        if (mOrder != null) {
            sql.append(" ORDER BY ");
            sql.append(mOrder);
        }
        if (mLimit > 0) {
            sql.append(" LIMIT ");
            sql.append(mLimit);
        }
        if (mOffset > 0) {
            sql.append(" OFFSET ");
            sql.append(mOffset);
        }
        return sql.toString();
    }
}
