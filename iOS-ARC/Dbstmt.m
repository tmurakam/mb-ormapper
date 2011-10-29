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

@implementation dbstmt

/**
   Initialize with sqlite3_stmt
*/
- (id)initWithStmt:(sqlite3_stmt *)stmt
{
    self = [super init];
    if (self != nil) {
        mStmt = stmt;
        mDb = [Database instance];
    }
    return self;
}

- (void)dealloc
{
    if (mStmt) {
        sqlite3_finalize(mStmt);
    }
}

/**
   Execute step (sqlite3_step)
*/
- (int)step
{
    int ret = sqlite3_step(mStmt);
    if (ret != SQLITE_OK && ret != SQLITE_ROW && ret != SQLITE_DONE) {
        NSLog(@"sqlite3_step error:%d (%s)", ret, sqlite3_errmsg(mDb.handle));
    }
    return ret;
}

/**
   Reset statement (sqlite3_reset)
*/
- (void)reset
{
    sqlite3_reset(mStmt);
}

/**
   Bind integer value
*/
- (void)bindInt:(int)idx val:(int)val
{
    sqlite3_bind_int(mStmt, idx+1, val);
}

/**
   Bind double value
*/
- (void)bindDouble:(int)idx val:(double)val
{
    sqlite3_bind_double(mStmt, idx+1, val);
}

/**
   Bind C-string value
*/
- (void)bindCString:(int)idx val:(const char *)val
{
    sqlite3_bind_text(mStmt, idx+1, val, -1, SQLITE_TRANSIENT);
}

/**
   Bind stringvalue
*/
- (void)bindString:(int)idx val:(NSString*)val
{
    sqlite3_bind_text(mStmt, idx+1, [val UTF8String], -1, SQLITE_TRANSIENT);
}

/**
   Bind date value
*/
- (void)bindDate:(int)idx val:(NSDate*)date
{
    NSString *str;
    
    if (date != NULL) {
        str = [mDb stringFromDate:date];
        sqlite3_bind_text(mStmt, idx+1, [str UTF8String], -1, SQLITE_TRANSIENT);
    }
}

/**
   Get integer value
*/
- (int)colInt:(int)idx
{
    return sqlite3_column_int(mStmt, idx);
}

/**
   Get double value
*/
- (double)colDouble:(int)idx
{
    return sqlite3_column_double(mStmt, idx);
}

/**
   Get C-string value
*/
- (const char *)colCString:(int)idx
{
    const char *s = (const char*)sqlite3_column_text(mStmt, idx);
    return s;
}

/**
   Get stringvalue
*/
- (NSString*)colString:(int)idx
{
    const char *s = (const char*)sqlite3_column_text(mStmt, idx);
    if (!s) {
        return @"";
    }
    NSString *ns = [NSString stringWithCString:s encoding:NSUTF8StringEncoding];
    return ns;
}

/**
   Get date value
*/
- (NSDate*)colDate:(int)idx
{
    NSDate *date = nil;
    NSString *ds = [self colString:idx];
    if (ds && [ds length] > 0) {
        date = [mDb dateFromString:ds];
    }
    return date;
}

@end
