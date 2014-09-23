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

// sqlite3 wrapper

#import <UIKit/UIKit.h>
#import <sqlite3.h>

@class Database;

/**
   Wrapper class of sqlite3_stmt
*/
@interface dbstmt : NSObject

- (id)initWithStmt:(sqlite3_stmt *)st;
- (NSInteger)step;
- (void)reset;

- (void)bindInt:(NSInteger)idx val:(NSInteger)val;
- (void)bindDouble:(NSInteger)idx val:(double)val;
- (void)bindCString:(NSInteger)idx val:(const char *)val;
- (void)bindString:(NSInteger)idx val:(NSString*)val;
- (void)bindDate:(NSInteger)idx val:(NSDate*)date;

- (NSInteger)colInt:(NSInteger)idx;
- (double)colDouble:(NSInteger)idx;
- (const char*)colCString:(NSInteger)idx;
- (NSString*)colString:(NSInteger)idx;
- (NSDate*)colDate:(NSInteger)idx;
@end
