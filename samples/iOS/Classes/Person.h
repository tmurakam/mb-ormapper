// Generated by O/R mapper generator ver 1.2

#import <UIKit/UIKit.h>
#import "ORRecord.h"

@class Person;

@interface Person : ORRecord {
    NSString* mName;
    int mSex;
    int mAge;
    NSDate* mBirthDate;
    NSString* mPhoneNumber;
}

@property(nonatomic,strong) NSString* name;
@property(nonatomic,assign) int sex;
@property(nonatomic,assign) int age;
@property(nonatomic,strong) NSDate* birthDate;
@property(nonatomic,strong) NSString* phoneNumber;

+ (BOOL)migrate;

// CRUD (Create/Read/Update/Delete) operations

// Create/update operations
// Note: You should use 'save' method
- (void)_insert;
- (void)_update;

// Read operations (Finder)
+ (Person *)find:(int)pid;

+ (Person *)find_by_name:(NSString*)key cond:(NSString*)cond;
+ (Person *)find_by_name:(NSString*)key;
+ (Person *)find_by_sex:(int)key cond:(NSString*)cond;
+ (Person *)find_by_sex:(int)key;
+ (Person *)find_by_age:(int)key cond:(NSString*)cond;
+ (Person *)find_by_age:(int)key;
+ (Person *)find_by_birth_date:(NSDate*)key cond:(NSString*)cond;
+ (Person *)find_by_birth_date:(NSDate*)key;
+ (Person *)find_by_phone_number:(NSString*)key cond:(NSString*)cond;
+ (Person *)find_by_phone_number:(NSString*)key;

+ (NSMutableArray *)find_all:(NSString *)cond;

+ (dbstmt *)gen_stmt:(NSString *)cond;
+ (Person *)find_first_stmt:(dbstmt *)stmt;
+ (NSMutableArray *)find_all_stmt:(dbstmt *)stmt;

// Delete operations
- (void)delete;
+ (void)delete_cond:(NSString *)cond;
+ (void)delete_all;

// Dump SQL
+ (void)getTableSql:(NSMutableString *)s;
- (void)getInsertSql:(NSMutableString *)s;

// internal functions
+ (NSString *)tableName;
- (void)_loadRow:(dbstmt *)stmt;

@end
