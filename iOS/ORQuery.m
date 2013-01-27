// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-
/*
  O/R Mapper library for iOS

  Copyright (c) 2010-2013, Takuya Murakami. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are
  met:

  1. Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer. 

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#import "Database.h"
#import "ORRecord.h"
#import "ORQuery.h"

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

+ (ORQuery *)getWithClass:(Class)class tableName:(NSString *)tableName
{
    return [[ORQuery alloc] initWithClass:class tableName:tableName];
}

- (id)initWithClass:(Class)class tableName:(NSString *)tableName
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
- (ORQuery *)where:(NSString *)where arguments:(NSArray *)args
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
    NSMutableString *sql = [self getSql];

    // bind arguments
    dbstmt *stmt = [[Database instance] prepare:sql];
    
    for (int i = 0; i < [mWhereParams count]; i++) {
        [stmt bindString:i val:(NSString *)mWhereParams[i]];
    }

    while ([stmt step] == SQLITE_ROW) {
        ORRecord *e = [mTargetClass new];
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
 * get SQL statement
 * @return SQL statement
 * @note placeholder('?') are not be expanded
 */
- (NSMutableString *)getSql
{
    NSMutableString *sql = [NSMutableString new];
    
    [sql appendFormat:@"SELECT * FROM %@", mTableName];
    
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
    
    // Limit
    if (mLimit > 0) {
        [sql appendFormat:@" LIMIT %d", mLimit];
    }
    
    // Offset
    if (mOffset > 0) {
        [sql appendFormat:@" OFFSET %d", mOffset];
    }
    
    return sql;
}

@end

