//
//  CDVMWBarcodeScanner.m
//  CameraDemo
//
//  Created by vladimir zivkovic on 5/8/13.
//
//

#import "CDVMWBarcodeScanner.h"
#import "BarcodeScanner.h"
#import "MWScannerViewController.h"
#import <Cordova/CDV.h>


@implementation CDVMWBarcodeScanner


NSString *callbackId;
NSMutableDictionary *customParams = nil;
MWScannerViewController *scannerViewController;

- (void)initDecoder:(CDVInvokedUrlCommand*)command
{
    [MWScannerViewController initDecoder];
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    
}

- (void)startScanner:(CDVInvokedUrlCommand*)command
{
    scannerViewController = [[MWScannerViewController alloc] initWithNibName:@"MWScannerViewController" bundle:nil];
    scannerViewController.delegate = self;
    scannerViewController.customParams = customParams;
    [self.viewController presentViewController:scannerViewController animated:YES completion:^{}];
#if !__has_feature(objc_arc)
    callbackId= [command.callbackId retain];
#else
    callbackId= command.callbackId;
#endif
}


- (void)scanningFinished:(NSString *)result withType:(NSString *)lastFormat isGS1: (bool) isGS1 andRawResult: (NSData *) rawResult locationPoints:(MWLocation *)locationPoints imageWidth:(int)imageWidth imageHeight:(int)imageHeight
{
    CDVPluginResult* pluginResult = nil;
    
    NSMutableArray *bytesArray = [[NSMutableArray alloc] init];
    unsigned char *bytes = (unsigned char *) [rawResult bytes];
    for (int i = 0; i < rawResult.length; i++){
        [bytesArray addObject:[NSNumber numberWithInt: bytes[i]]];
    }
    NSMutableDictionary *resultDict;
    if (locationPoints) {
        NSArray *xyArray = [NSArray arrayWithObjects:@"x",@"y", nil];
        
        NSDictionary *p1 = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:[NSNumber numberWithFloat:locationPoints.p1.x],[NSNumber numberWithFloat:locationPoints.p1.y], nil]
                                                       forKeys:xyArray];
        NSDictionary *p2 = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:[NSNumber numberWithFloat:locationPoints.p2.x],[NSNumber numberWithFloat:locationPoints.p2.y], nil]
                                                       forKeys:xyArray];
        NSDictionary *p3 = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:[NSNumber numberWithFloat:locationPoints.p3.x],[NSNumber numberWithFloat:locationPoints.p3.y], nil]
                                                       forKeys:xyArray];
        NSDictionary *p4 = [NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:[NSNumber numberWithFloat:locationPoints.p4.x],[NSNumber numberWithFloat:locationPoints.p4.y], nil]
                                                       forKeys:xyArray];
        
        NSDictionary *location =[NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:p1,p2,p3,p4 ,nil]
                                                        forKeys:[NSArray arrayWithObjects:@"p1",@"p2",@"p3",@"p4",nil]];
        resultDict = [[NSMutableDictionary alloc] initWithObjects:[NSArray arrayWithObjects:result, lastFormat, bytesArray, [NSNumber numberWithBool:isGS1], location, [NSNumber numberWithInt:imageWidth],[NSNumber numberWithInt:imageHeight],nil]
                                                          forKeys:[NSArray arrayWithObjects:@"code", @"type",@"bytes", @"isGS1",@"location",@"imageWidth",@"imageHeight", nil]];

    }else{
        resultDict = [[NSMutableDictionary alloc] initWithObjects:[NSArray arrayWithObjects:result, lastFormat, bytesArray, [NSNumber numberWithBool:isGS1], [NSNumber numberWithBool:NO], [NSNumber numberWithInt:imageWidth],[NSNumber numberWithInt:imageHeight],nil]
                                                          forKeys:[NSArray arrayWithObjects:@"code", @"type",@"bytes", @"isGS1",@"location",@"imageWidth",@"imageHeight", nil]];
    }
    
    
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDict];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:callbackId];
    
}

- (void)registerCode:(CDVInvokedUrlCommand*)command
{
    int codeMask = [[command.arguments objectAtIndex:0] intValue];
    char * userName = (char *) [[command.arguments objectAtIndex:1] UTF8String];
    char * key =(char *) [[command.arguments objectAtIndex:2] UTF8String];
    MWB_registerCode(codeMask, userName, key);
}

- (void)setActiveCodes:(CDVInvokedUrlCommand*)command
{
    int codeMask = [[command.arguments objectAtIndex:0] intValue];
    MWB_setActiveCodes(codeMask);
}

- (void)setActiveSubcodes:(CDVInvokedUrlCommand*)command
{
    int codeMask = [[command.arguments objectAtIndex:0] intValue];
    int subCodeMask = [[command.arguments objectAtIndex:1] intValue];
    MWB_setActiveSubcodes(codeMask, subCodeMask);
}

- (int)getLastType:(CDVInvokedUrlCommand*)command
{
    return MWB_getLastType();
}

- (void)setFlags:(CDVInvokedUrlCommand*)command
{
    int codeMask = [[command.arguments objectAtIndex:0] intValue];
    int flags = [[command.arguments objectAtIndex:1] intValue];
    MWB_setFlags(codeMask, flags);
}

- (void)setMinLength:(CDVInvokedUrlCommand*)command
{
    int codeMask = [[command.arguments objectAtIndex:0] intValue];
    int minLength = [[command.arguments objectAtIndex:1] intValue];
    MWB_setMinLength(codeMask, minLength);
}

- (void)setDirection:(CDVInvokedUrlCommand*)command
{
    int direction = [[command.arguments objectAtIndex:0] intValue];
    MWB_setDirection(direction);
}

- (void)setScanningRect:(CDVInvokedUrlCommand*)command
{
    int codeMask = [[command.arguments objectAtIndex:0] intValue];
    int left = [[command.arguments objectAtIndex:1] intValue];
    int top = [[command.arguments objectAtIndex:2] intValue];
    int width = [[command.arguments objectAtIndex:3] intValue];
    int height = [[command.arguments objectAtIndex:4] intValue];
    
    MWB_setScanningRect(codeMask, left, top, width, height);
}

- (void)setLevel:(CDVInvokedUrlCommand*)command
{
    int level = [[command.arguments objectAtIndex:0] intValue];
    MWB_setLevel(level);
}

- (void)setInterfaceOrientation:(CDVInvokedUrlCommand*)command
{
    NSString *orientation = [command.arguments objectAtIndex:0];
    UIInterfaceOrientationMask interfaceOrientation = UIInterfaceOrientationMaskLandscapeLeft;
    
    if ([orientation isEqualToString:@"Portrait"]){
        interfaceOrientation = UIInterfaceOrientationMaskPortrait;
    }
    if ([orientation isEqualToString:@"LandscapeLeft"]){
        interfaceOrientation = UIInterfaceOrientationMaskLandscapeLeft;
    }
    if ([orientation isEqualToString:@"LandscapeRight"]){
        interfaceOrientation = UIInterfaceOrientationMaskLandscapeRight;
    }
    
    [MWScannerViewController setInterfaceOrientation:interfaceOrientation];
    
}

- (void)setOverlayMode:(CDVInvokedUrlCommand*)command{
    [MWScannerViewController setOverlayMode:[[command.arguments objectAtIndex:0] intValue]];
}

- (void)enableHiRes:(CDVInvokedUrlCommand*)command
{
    bool hiRes = [[command.arguments objectAtIndex:0] boolValue];
    [MWScannerViewController enableHiRes:hiRes];
}

- (void)enableFlash:(CDVInvokedUrlCommand*)command
{
    bool flash = [[command.arguments objectAtIndex:0] boolValue];
    [MWScannerViewController enableFlash:flash];
}

- (void)enableZoom:(CDVInvokedUrlCommand*)command
{
    bool zoom = [[command.arguments objectAtIndex:0] boolValue];
    [MWScannerViewController enableZoom:zoom];
}

- (void)closeScannerOnDecode:(CDVInvokedUrlCommand*)command
{
    BOOL shouldClose =[[command.arguments objectAtIndex:0] boolValue];
    [MWScannerViewController closeScannerOnDecode:shouldClose];
}


- (void)turnFlashOn:(CDVInvokedUrlCommand*)command
{
    bool flash = [[command.arguments objectAtIndex:0] boolValue];
    [MWScannerViewController turnFlashOn:flash];
}

- (void)setZoomLevels:(CDVInvokedUrlCommand*)command
{
    [MWScannerViewController setZoomLevels:[[command.arguments objectAtIndex:0] intValue] zoomLevel2:[[command.arguments objectAtIndex:1] intValue] initialZoomLevel:[[command.arguments objectAtIndex:2] intValue]];
}

- (void)setMaxThreads:(CDVInvokedUrlCommand*)command
{
    [MWScannerViewController setMaxThreads:[[command.arguments objectAtIndex:0] intValue]];
}

- (void)setCustomParam:(CDVInvokedUrlCommand*)command
{
    NSString *key = [command.arguments objectAtIndex:0];
    NSObject *value = [command.arguments objectAtIndex:1];
    
    if (customParams == nil){
        customParams = [[NSMutableDictionary alloc] init];
    }
    
    [customParams setObject:value forKey:key];
    
}
- (void)setParam:(CDVInvokedUrlCommand*)command
{
    MWB_setParam([[command.arguments objectAtIndex:0] intValue], [[command.arguments objectAtIndex:1] intValue], [[command.arguments objectAtIndex:2] intValue]);
}
- (void)resumeScanning:(CDVInvokedUrlCommand*)command
{
    scannerViewController.state = CAMERA;
}
- (void)use60fps:(CDVInvokedUrlCommand*)command
{
    [MWScannerViewController use60fps:[[command.arguments objectAtIndex:0] boolValue]];;
}
- (void)closeScanner:(CDVInvokedUrlCommand*)command
{
    if (scannerViewController) {
        [scannerViewController dismissViewControllerAnimated:YES completion:nil];
    }
}
- (void)scanImage:(CDVInvokedUrlCommand*)command
{
    [MWScannerViewController initDecoder];
    callbackId = command.callbackId;
    
    NSString *prefixToRemove = @"file://";
    
    NSString *filePath = [command.arguments objectAtIndex:0];
    

    if ([filePath hasPrefix:prefixToRemove])
        
        filePath = [filePath substringFromIndex:[prefixToRemove length]];
    
    UIImage * image = [UIImage imageWithContentsOfFile:filePath];
    
    if (image!=nil) {
        
        int newWidth;
        int newHeight;
        
        uint8_t *bytes = [CDVMWBarcodeScanner UIImageToGrayscaleByteArray:image newWidth: &newWidth newHeight: &newHeight];
        
        unsigned char *pResult=NULL;
        
        if (bytes) {
            
            int resLength = MWB_scanGrayscaleImage(bytes, newWidth, newHeight, &pResult);
            
            free(bytes);
            
            MWResults *mwResults = nil;
            MWResult *mwResult = nil;
            
            if (resLength > 0){
                
                mwResults = [[MWResults alloc] initWithBuffer:pResult];
                if (mwResults && mwResults.count > 0){
                    mwResult = [mwResults resultAtIntex:0];
                }
                free(pResult);
                
            }
            if (mwResult)
            {
                [self scanningFinished:mwResult.text withType: mwResult.typeName isGS1:mwResult.isGS1  andRawResult: [[NSData alloc] initWithBytes: mwResult.bytes length: mwResult.bytesLength] locationPoints:mwResult.locationPoints imageWidth:mwResult.imageWidth imageHeight:mwResult.imageHeight];
                
            }else{
                [self scanningFinished:@"" withType: @"NoResult" isGS1:NO  andRawResult: nil locationPoints:nil imageWidth:0 imageHeight:0];
            }
        }
        
    }
    
}



#define MAX_IMAGE_SIZE 1280

+ (unsigned char*)UIImageToGrayscaleByteArray:(UIImage*)image newWidth: (int*)newWidth newHeight: (int*)newHeight; {
    
    int targetWidth = image.size.width;
    int targetHeight = image.size.height;
    float scale = 1.0;
    
    if (targetWidth > MAX_IMAGE_SIZE || targetHeight > MAX_IMAGE_SIZE){
        targetWidth /= 2;
        targetHeight /= 2;
        scale *= 2;
        
    }
    
    *newWidth = targetWidth;
    
    *newHeight = targetHeight;
    
    unsigned char *imageData = (unsigned char*)(malloc( targetWidth*targetHeight));
    
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceGray();
    
    CGImageRef imageRef = [image CGImage];
    CGContextRef bitmap = CGBitmapContextCreate( imageData,
                                                targetWidth,
                                                targetHeight,
                                                8,
                                                targetWidth,
                                                colorSpace,
                                                0);
    
    CGContextDrawImage( bitmap, CGRectMake(0, 0, targetWidth, targetHeight), imageRef);
    
    CGContextRelease( bitmap);
    
    CGColorSpaceRelease( colorSpace);
    
    return imageData;
    
}




@end