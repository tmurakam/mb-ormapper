// -*-  Mode:ObjC; c-basic-offset:4; tab-width:8; indent-tabs-mode:nil -*-

#import <UIKit/UIKit.h>
#import "Database.h"

NS_ASSUME_NONNULL_BEGIN

/**
   O/R query
 */
@interface ORQuery<T> : NSObject

+ (ORQuery *)getWithClass:(Class)class tableName:(NSString *)tableName;

- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithClass:(Class)class tableName:(NSString *)tableName NS_DESIGNATED_INITIALIZER;

- (ORQuery *)where:(NSString *)where arguments:(NSArray<NSString *> *)args;
- (ORQuery *)order:(NSString *)order;
- (ORQuery *)limit:(NSInteger)limit;
- (ORQuery *)offset:(NSInteger)limit;

@property (nonatomic, readonly, copy) NSMutableArray<T> *all;
@property (nonatomic, readonly, strong, nullable) T first;

@end

NS_ASSUME_NONNULL_END
