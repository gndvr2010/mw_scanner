using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Tasks;
using System.Windows;

using WPCordovaClassLib.Cordova;
using WPCordovaClassLib.Cordova.Commands;
using WPCordovaClassLib.Cordova.JSON;

using BarcodeLib;
using BarcodeScanners;

namespace Cordova.Extension.Commands
{
    class MWBarcodeScanner : BaseCommand
    {


         public void initDecoder (string options)
            {

                BarcodeScanners.BarcodeHelper.initDecoder();
                DispatchCommandResult(new PluginResult(PluginResult.Status.OK));

            }

         public void startScanner(string options)
         {

             BarcodeHelper.resultAvailable = false;

             Deployment.Current.Dispatcher.BeginInvoke(delegate()
             {
                 
                 var root = Application.Current.RootVisual as PhoneApplicationFrame;

                 root.Navigate(new System.Uri("/Plugins/com.manateeworks.barcodescanner/ScannerPage.xaml", UriKind.Relative));

                  root.Navigated += new System.Windows.Navigation.NavigatedEventHandler(root_Navigated);
             });
         }

         public void registerCode(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             Scanner.MWBregisterCode(Convert.ToInt32(paramsList[0]), paramsList[1], paramsList[2]);
         }

         public void setActiveCodes(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             Scanner.MWBsetActiveCodes(Convert.ToInt32(paramsList[0]));
         }

         public void setActiveSubcodes(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             Scanner.MWBsetActiveSubcodes(Convert.ToInt32(paramsList[0]), Convert.ToInt32(paramsList[1]));
         }

         public void setFlags(string options)
         {
              string[] paramsList = JsonHelper.Deserialize<string[]>(options);
              Scanner.MWBsetFlags(Convert.ToInt32(paramsList[0]), Convert.ToInt32(paramsList[1]));

         }

		public void setMinLength(string options)
		{
			string[] paramsList = JsonHelper.Deserialize<string[]>(options);
			Scanner.MWBsetMinLength(Convert.ToInt32(paramsList[0]), Convert.ToInt32(paramsList[1]));

		}

         public void setDirection(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             Scanner.MWBsetDirection((uint)Convert.ToInt32(paramsList[0]));
         }

         public void setScanningRect(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             Scanner.MWBsetScanningRect(Convert.ToInt32(paramsList[0]), Convert.ToInt32(paramsList[1]), 
                 Convert.ToInt32(paramsList[2]), Convert.ToInt32(paramsList[3]), Convert.ToInt32(paramsList[4]));

         }

         public void setLevel(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             Scanner.MWBsetLevel(Convert.ToInt32(paramsList[0]));
         }

         public void setInterfaceOrientation(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             String orientation = Convert.ToString(paramsList[0]);

             if (orientation.Equals("Portrait"))
             {
                 BarcodeScannerPage.ScannerPage.param_Orientation = SupportedPageOrientation.Portrait;
             } else
             {
                BarcodeScannerPage.ScannerPage.param_Orientation = SupportedPageOrientation.Landscape;
             } 


             
         }

         public void setOverlayMode(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             BarcodeScannerPage.ScannerPage.param_OverlayMode = (BarcodeScannerPage.ScannerPage.OverlayMode)Convert.ToInt32(paramsList[0]);
         }

         public void enableHiRes(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             BarcodeScannerPage.ScannerPage.param_EnableHiRes = Convert.ToBoolean(paramsList[0]);
         }

         public void enableFlash(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             BarcodeScannerPage.ScannerPage.param_EnableFlash = Convert.ToBoolean(paramsList[0]);
         }

         public void turnFlashOn(string options)
         {
             string[] paramsList = JsonHelper.Deserialize<string[]>(options);
             BarcodeScannerPage.ScannerPage.param_DefaultFlashOn = Convert.ToBoolean(paramsList[0]);
         }

         public void enableZoom(string options)
        {
             //not supported currently on WP8 (technical limitation)
        }

        public void setZoomLevels(string options)
        {
            //not supported currently on WP8 (technical limitation)
        }
        public void setParam(string options)
        {
            string[] paramsList = JsonHelper.Deserialize<string[]>(options);
//            Scanner.MWBsetParam(Convert.ToInt32(paramsList[0]), Convert.ToInt32(paramsList[1]),Convert.ToInt32(paramsList[1]));

        }
        public int getAvailableCores()
        {
            return BarcodeHelper.getCPUCores();
        }

        public void setMaxThreads(string options)
        {
            string[] paramsList = JsonHelper.Deserialize<string[]>(options);
            BarcodeScannerPage.ScannerPage.param_maxThreads = Convert.ToInt32(paramsList[0]);

            if (BarcodeScannerPage.ScannerPage.param_maxThreads > BarcodeScannerPage.ScannerPage.CPU_CORES && BarcodeScannerPage.ScannerPage.CPU_CORES > 0 )
            {
                BarcodeScannerPage.ScannerPage.param_maxThreads = BarcodeScannerPage.ScannerPage.CPU_CORES;
            }

        }

        

         void root_Navigated(object sender, System.Windows.Navigation.NavigationEventArgs e)
         {
             if ((e.Content is BarcodeScannerPage.ScannerPage)) return;

             (Application.Current.RootVisual as PhoneApplicationFrame).Navigated -= root_Navigated;

             if (BarcodeHelper.resultAvailable)
             {
                
                // string resultString = JsonHelper.Serialize (BarcodeHelper.scannerResult);
                 string resultString = "{\"code\":" +JsonHelper.Serialize ( BarcodeHelper.scannerResult.code) +","
                     +"\"type\":" + JsonHelper.Serialize (BarcodeHelper.scannerResult.type) +","
                     + "\"bytes\":" + JsonHelper.Serialize (BarcodeHelper.scannerResult.bytes) + ","
                     + "\"isGS1\":" + JsonHelper.Serialize (BarcodeHelper.scannerResult.isGS1) + ","
                     + "\"location\":" + BarcodeHelper.scannerResult.location + ","
                     + "\"imageWidth\":" + BarcodeHelper.scannerResult.imageWidth + ","
                     + "\"imageHeight\":" + BarcodeHelper.scannerResult.imageHeight
                     +"}";

                 DispatchCommandResult(new PluginResult(PluginResult.Status.OK, resultString));
             }

             BarcodeScannerPage.ScannerPage.cameraDevice.Dispose();

          
         }
    }
}
