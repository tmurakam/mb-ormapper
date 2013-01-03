//
//  sampleAppDelegate.m
//

#import "sampleAppDelegate.h"
#import "RootViewController.h"
#import "Person.h"

@implementation sampleAppDelegate

@synthesize window;
@synthesize navigationController;


#pragma mark -
#pragma mark Application lifecycle

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {    
    // Open & create database
    Database *db = [Database instance];
    [db open:@"sample.db"];
    
    // Migrate tables
    [Person migrate];
    
    //[Person delete_all];
    
    // add test data
    NSMutableArray *persons = [Person find_all];
    if ([persons count] == 0) {
        // add test data
        Person *person = [Person new];
        person.name = @"John Doe";
        person.age = 26;
        person.sex = 0;
        [person save];
    }
    
    [window addSubview:[navigationController view]];
    [window makeKeyAndVisible];
    return YES;
}


- (void)applicationWillTerminate:(UIApplication *)application {
    // Save data if appropriate
}


#pragma mark -
#pragma mark Memory management



@end

