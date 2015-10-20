var MWBSInitSpace = MWBSInitSpace || {};
/* Registration and settings are defined here, users will supply their own username and key depending on which platform they will use
    @params 
        mwbs - is the MWBScanner object, passed from the plugin function
        constants - the constants used for scanner settings
        dvc - the device on which it runs
 
 
 */
MWBSInitSpace.init = function(mwbs,constants,dvc){
    console.log('MWBSInitSpace.init Invoked at: '+ (new Date()).getTime());
    //change these registration settings to match your licence keys
    /* BEGIN Registration settings */

    //if your app doesn't work after setting license keys, try to uncomment the try-catch, and see what the error is
    
//    try{
        var mwregister = {
           'Android' : {
               'MWB_CODE_MASK_25' : {'username' : '', 'key' : ''},
               'MWB_CODE_MASK_39' : {'username':'','key':''},
               'MWB_CODE_MASK_93' : {'username':'','key':''},
               'MWB_CODE_MASK_128' : {'username':'','key':''},
               'MWB_CODE_MASK_AZTEC' : {'username':'','key':''},
               'MWB_CODE_MASK_DM' : {'username':'','key':''},
               'MWB_CODE_MASK_EANUPC' : {'username':'','key':''},
               'MWB_CODE_MASK_PDF' : {'username':'','key':''},
               'MWB_CODE_MASK_QR' : {'username':'','key':''},
               'MWB_CODE_MASK_RSS' : {'username':'','key':''},
               'MWB_CODE_MASK_CODABAR' : {'username':'','key':''},
               'MWB_CODE_MASK_DOTCODE' : {'username':'','key':''}
           },
           'iOS' :{
               'MWB_CODE_MASK_25' : {'username' : '', 'key' : ''},
               'MWB_CODE_MASK_39' : {'username':'','key':''},
               'MWB_CODE_MASK_93' : {'username':'','key':''},
               'MWB_CODE_MASK_128' : {'username':'','key':''},
               'MWB_CODE_MASK_AZTEC' : {'username':'','key':''},
               'MWB_CODE_MASK_DM' : {'username':'','key':''},
               'MWB_CODE_MASK_EANUPC' : {'username':'','key':''},
               'MWB_CODE_MASK_PDF' : {'username':'','key':''},
               'MWB_CODE_MASK_QR' : {'username':'','key':''},
               'MWB_CODE_MASK_RSS' : {'username':'','key':''},
               'MWB_CODE_MASK_CODABAR' : {'username':'','key':''},
               'MWB_CODE_MASK_DOTCODE' : {'username':'','key':''}
           },
           'Win32NT' : {
               'MWB_CODE_MASK_25' : {'username' : '', 'key' : ''},
               'MWB_CODE_MASK_39' : {'username':'','key':''},
               'MWB_CODE_MASK_93' : {'username':'','key':''},
               'MWB_CODE_MASK_128' : {'username':'','key':''},
               'MWB_CODE_MASK_AZTEC' : {'username':'','key':''},
               'MWB_CODE_MASK_DM' : {'username':'','key':''},
               'MWB_CODE_MASK_EANUPC' : {'username':'','key':''},
               'MWB_CODE_MASK_PDF' : {'username':'','key':''},
               'MWB_CODE_MASK_QR' : {'username':'','key':''},
               'MWB_CODE_MASK_RSS' : {'username':'','key':''},
               'MWB_CODE_MASK_CODABAR' : {'username':'','key':''},
               'MWB_CODE_MASK_DOTCODE' : {'username':'','key':''}
           }
        }
//    }
//    catch(e){
//        console.log(e);
//    }
    /* END registration settings */
    var platform = mwregister[dvc.platform];
    Object.keys(platform).forEach(function(reg_codes){
        mwbs['MWBregisterCode'](constants[reg_codes],platform[reg_codes]['username'],platform[reg_codes]['key']);
    });

    //settings portion, disable those that are not needed

    /* BEGIN settings CALLS */
        //if your code doesn't work after changing a few parameters, and there is no error output, uncomment the try-catch, the error will be output in your console
//    try{
        /*UNCOMMENT the lines you wish to include in the settings */
        //  mwbs['MWBsetInterfaceOrientation'] (constants.OrientationPortrait);
        //  mwbs['MWBsetOverlayMode'](constants.OverlayModeImage);
        //  mwbs['MWBenableHiRes'](true);
        //  mwbs['MWBenableFlash'](true);
        //  mwbs['MWBsetActiveCodes'](constants.MWB_CODE_MASK_128 | constants.MWB_CODE_MASK_39);
        //  mwbs['MWBsetLevel'](2);
        //  mwbs['MWBsetFlags'](constants.MWB_CODE_MASK_39, constants.MWB_CFG_CODE39_EXTENDED_MODE);
        //  mwbs['MWBsetDirection'](constants.MWB_SCANDIRECTION_VERTICAL | constants.MWB_SCANDIRECTION_HORIZONTAL);
        //  mwbs['MWBsetScanningRect'](constants.MWB_CODE_MASK_39, 20,20,60,60);
        //  mwbs['MWBenableZoom'](true);
        //  mwbs['MWBsetZoomLevels'](200, 400, 0);
        //  mwbs['MWBsetMinLength'](constants.MWB_CODE_MASK_39, 4);
        //  mwbs['MWBsetMaxThreads'](1);
        //  mwbs['MWBcloseScannerOnDecode'](false);
        //  mwbs['MWBuse60fps'](true);      
//    }
//    catch(e){
//        console.log(e);
//    }

    /* END settings CALLS */
    
    /* CUSTOM JAVASCRIPT CALLS */

};
//custom callback function, one that can be modified by the user
MWBSInitSpace.callback = function(result){
    console.log('MWBSInitSpace.callback Invoked at: '+ (new Date()).getTime());
    
     //result.code - string representation of barcode result
     //result.type - type of barcode detected
     //result.bytes - bytes array of raw barcode result
     
    console.log('Scan complete');
    if (result.type == 'Cancel'){
        //Perform some action on scanning canceled if needed
    } 
    else
        if (result && result.code){

              // setTimeout(function(){                  //
              //    BarcodeScanner.MWBresumeScanning();  // Use this sample when using mwbs['MWBcloseScannerOnDecode'](false);
              // },2000);                                //

             navigator.notification.alert(result.code, function(){}, result.type + (result.isGS1?" (GS1)":""), 'Close');
          }
}