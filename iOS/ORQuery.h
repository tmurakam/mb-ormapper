// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-

#import <UIKit/UIKit.h>
#import "Database.h"

/**
   O/R query
 */
@interface ORQuery : NSObject

+ (ORQuery *)getWithClass:(Class)class tableName:(NSString *)tableName;

- (id)initWithClass:(Class)class tableName:(NSString *)tableName;

- (ORQuery *)where:(NSString *)where arguments:(NSArray *)args;
- (ORQuery *)order:(NSString *)order;
- (ORQuery *)limit:(NSInteger)limit;
- (ORQuery *)offset:(NSInteger)limit;

- (NSMutableArray *)all;
- (id)first;

@end


