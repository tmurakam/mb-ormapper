// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-

#import <UIKit/UIKit.h>
#import "Database.h"

/**
   O/R query
 */
@interface ORQuery : NSObject
{
}

+ (ORQuery *)get_with_table:(NSString *)tableName;

- (ORQuery *)where:(NSString *)cond arguments:(NSString *)args, ...;
- (ORQuery *)order:(NSString *)order;
- (ORQuery *)limit:(int)limit;
- (ORQuery *)offset:(int)limit;

- (NSMutableArray *)all;
- (id)first;

@end


