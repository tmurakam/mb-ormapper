// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-
#import <UIKit/UIKit.h>
#import "Database.h"

@class ORRecord : NSObject
{
    int id;
}

@property(nonatomic,assign) int id;

+ (void)migrate:(NSString *)tableName columnTypes:(NSArray *)array;
+ (NSMutableArray *)find_all;
+ (NSMutableArray *)find_cond:(NSString *)cond;
+ (id)find:(int)id;

- (void)save;
- (void)insert;
- (void)update;
- (void)delete;

@end


