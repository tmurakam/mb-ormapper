// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-
/*
  O/R Mapper library for iOS

  Copyright (c) 2010-2011, Takuya Murakami. All rights reserved.

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

@implementation ORRecord

#define UNASSIGNED_PID -1

@synthesize pid = mPid;

/** Constructor */ 
- (id)init
{
    self = [super init];
    if (self != nil) {
        mPid = UNASSIGNED_PID;
    }
    return self;
}

/**
   Migrate database table

   @param array Array of (field name, type).
   @param key Primary key name
   @return YES - Table was newly created, NO - Table already exists.
*/

+ (BOOL)migrate:(NSArray *)array primaryKey:(NSString*)key
{
    Database *db = [Database instance];
    dbstmt *stmt;
    BOOL ret;

    // check if table exists.
    NSString *sql, *tablesql;
    sql = [NSString stringWithFormat:@"SELECT sql FROM sqlite_master WHERE type='table' AND name='%@';", [self tableName]];
    stmt = [db prepare:sql];

    // create table
    if ([stmt step] != SQLITE_ROW) {
        sql = [NSString stringWithFormat:@"CREATE TABLE %@ ( %@ INTEGER PRIMARY KEY);",
			[self tableName], key];
        [db exec:sql];
        tablesql = sql;
	ret = YES;
    } else {
        tablesql = [stmt colString:0];
	ret = NO;
    }

    // add columns
    int count = [array count] / 2;

    for (int i = 0; i < count; i++) {
        NSString *column = [array objectAtIndex:i * 2];
        NSString *type = [array objectAtIndex:i * 2 + 1];

        NSRange range = [tablesql rangeOfString:[NSString stringWithFormat:@" %@ ", column]];
        if (range.location == NSNotFound) {
            sql = [NSString stringWithFormat:@"ALTER TABLE %@ ADD COLUMN %@ %@;",
                            [self tableName], column, type];
            [db exec:sql];
        }
    }
    return ret;
}

/**
   get all records
   @return array of all record
*/
+ (NSMutableArray *)find_all
{
    return [self find_all:nil];
}

/**
   Get all records matche the conditions

   @param cond Conditions (WHERE phrase and so on)
   @return array of records

   @note You must override this.
*/
+ (NSMutableArray *)find_all:(NSString *)cond
{
    return nil;
}

/**
   Get the record matchs the id

   @param id Primary key of the record
   @return record
*/
+ (id)find:(int)pid
{
    return nil;
}

/**
   Save record
*/
- (void)save
{
    if (mPid == UNASSIGNED_PID) {
        [self _insert];
    } else {
        [self _update];
    }
}

- (void)_insert
{
}

- (void)_update
{
}

+ (NSString *)tableName
{
    return nil; // must be override
}

/**
   Delete record
*/
- (void)delete
{
    return;
}

/**
   Delete all records

   @note You must override this
*/
+ (void)delete_all
{
    // must be override
}

/**
   Quote SQL string
*/
- (NSString *)quoteSqlString:(NSString *)string
{
    return [NSString stringWithFormat:@"'%@'", [string stringByReplacingOccurrencesOfString:@"'" withString:@"''"]];
}

@end
