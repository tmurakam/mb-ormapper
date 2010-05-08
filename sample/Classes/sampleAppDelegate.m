//
//  sampleAppDelegate.m
//  sample
//
//  Created by 村上 卓弥 on 10/05/08.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
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
    [db open];
    
    // Migrate tables
    [Person migrate];
    
    //[Person delete_all];
    
    NSMutableArray *persons = [Person find_all];
    if ([persons count] == 0) {
        // add test data
        Person *person = [[[Person alloc] init] autorelease];
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

- (void)dealloc {
    [navigationController release];
    [window release];
    [super dealloc];
}


@end

