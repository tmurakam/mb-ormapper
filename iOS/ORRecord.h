// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-
#import <UIKit/UIKit.h>
#import "Database.h"

@interface ORRecord : NSObject
{
    int pid;
    BOOL isInserted;
}

@property(nonatomic,assign) int pid;

+ (BOOL)migrate:(NSArray *)array primaryKey:(NSString*)key;
+ (NSMutableArray *)find_all;
+ (NSMutableArray *)find_all:(NSString *)cond;
+ (id)find:(int)id;

- (void)save;
- (void)insert;
- (void)update;
- (void)delete;
+ (void)delete_all;

+ (NSString *)tableName;

@end


