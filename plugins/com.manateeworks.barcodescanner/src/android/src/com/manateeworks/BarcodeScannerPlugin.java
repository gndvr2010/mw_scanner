package com.manateeworks;

import java.io.File;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;

import com.manateeworks.BarcodeScanner.MWResult;
import com.manateeworks.BarcodeScanner.MWResults;
import com.manateeworks.ScannerActivity.State;

public class BarcodeScannerPlugin extends CordovaPlugin {

	public static class ImageInfo {
		byte[] pixels;
		int width;
		int height;
		ImageInfo(int width, int height){
			this.width = width;
			this.height = height;
			pixels = new byte[width * height];
		}
	}
	
	// !!! Rects are in format: x, y, width, height !!!
	public static final Rect RECT_LANDSCAPE_1D = new Rect(2, 20, 96, 60);
	public static final Rect RECT_LANDSCAPE_2D = new Rect(20, 2, 60, 96);
	public static final Rect RECT_PORTRAIT_1D = new Rect(20, 2, 60, 96);
	public static final Rect RECT_PORTRAIT_2D = new Rect(20, 2, 60, 96);
	public static final Rect RECT_FULL_1D = new Rect(2, 2, 96, 96);
	public static final Rect RECT_FULL_2D = new Rect(20, 2, 60, 96);
	public static final Rect RECT_DOTCODE = new Rect(30, 20, 40, 60);
	private static CallbackContext cbc;
	private static String lastType;

	@Override
	public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

		if ("initDecoder".equals(action)) {

			initDecoder();
			callbackContext.success();
			return true;

		} else if ("startScanner".equals(action)) {

			cbc = callbackContext;
			ScannerActivity.cbc = cbc;
			Context context = this.cordova.getActivity().getApplicationContext();
			Intent intent = new Intent(context, com.manateeworks.ScannerActivity.class);
			this.cordova.startActivityForResult(this, intent, 1);
			return true;

		} else if ("getLastType".equals(action)) {

			callbackContext.success(lastType);
			return true;

		} else if ("setLevel".equals(action)) {

			BarcodeScanner.MWBsetLevel(args.getInt(0));
			return true;

		}

		else if ("setActiveCodes".equals(action)) {

			BarcodeScanner.MWBsetActiveCodes(args.getInt(0));
			return true;

		}

		else if ("setActiveSubcodes".equals(action)) {

			BarcodeScanner.MWBsetActiveSubcodes(args.getInt(0), args.getInt(1));
			return true;

		}

		else if ("setFlags".equals(action)) {

			callbackContext.success(BarcodeScanner.MWBsetFlags(args.getInt(0), args.getInt(1)));
			return true;

		}

		else if ("setMinLength".equals(action)) {

			callbackContext.success(BarcodeScanner.MWBsetMinLength(args.getInt(0), args.getInt(1)));
			return true;

		}

		else if ("setDirection".equals(action)) {

			BarcodeScanner.MWBsetDirection(args.getInt(0));
			return true;

		}

		else if ("setScanningRect".equals(action)) {

			BarcodeScanner.MWBsetScanningRect(args.getInt(0), args.getInt(1), args.getInt(2), args.getInt(3), args.getInt(4));
			return true;

		} else if ("registerCode".equals(action)) {

			BarcodeScanner.MWBregisterCode(args.getInt(0), args.getString(1), args.getString(2));
			return true;

		}

		else if ("setInterfaceOrientation".equals(action)) {

			String orientation = args.getString(0);
			if (orientation.equalsIgnoreCase("Portrait")) {
				ScannerActivity.param_Orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			if (orientation.equalsIgnoreCase("LandscapeLeft")) {
				ScannerActivity.param_Orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			}
			if (orientation.equalsIgnoreCase("LandscapeRight")) {
				ScannerActivity.param_Orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
			}

			return true;

		}

		else if ("setOverlayMode".equals(action)) {

			ScannerActivity.param_OverlayMode = args.getInt(0);
			return true;

		}

		else if ("enableHiRes".equals(action)) {

			ScannerActivity.param_EnableHiRes = args.getBoolean(0);
			return true;

		}

		else if ("enableFlash".equals(action)) {
			ScannerActivity.param_EnableFlash = args.getBoolean(0);
			return true;

		}

		else if ("turnFlashOn".equals(action)) {
			ScannerActivity.param_DefaultFlashOn = args.getBoolean(0);
			return true;

		}

		else if ("enableZoom".equals(action)) {
			ScannerActivity.param_EnableZoom = args.getBoolean(0);
			return true;

		}

		else if ("setMaxThreads".equals(action)) {
			ScannerActivity.param_maxThreads = args.getInt(0);
			return true;

		}

		else if ("setZoomLevels".equals(action)) {

			ScannerActivity.param_ZoomLevel1 = args.getInt(0);
			ScannerActivity.param_ZoomLevel2 = args.getInt(1);
			ScannerActivity.zoomLevel = args.getInt(2);
			if (ScannerActivity.zoomLevel > 2) {
				ScannerActivity.zoomLevel = 2;
			}
			if (ScannerActivity.zoomLevel < 0) {
				ScannerActivity.zoomLevel = 0;
			}
			return true;

        } else if ("setCustomParam".equals(action)) {
            
            if (ScannerActivity.customParams == null) {
                ScannerActivity.customParams = new HashMap<String, Object>();
            }
            
            ScannerActivity.customParams.put((String) args.get(0), args.get(1));
            return true;
            
        } else if ("setParam".equals(action)) {
            
            BarcodeScanner.MWBsetParam(args.getInt(0), args.getInt(1), args.getInt(2));
            return true;
            
        } else if ("resumeScanning".equals(action)) {

			ScannerActivity.state = State.PREVIEW;
			return true;

		} else if ("closeScannerOnDecode".equals(action)) {
			ScannerActivity.param_closeOnSuccess = args.getBoolean(0);
			return true;

		} else if ("closeScanner".equals(action)) {
			if (ScannerActivity.activity != null) {
				ScannerActivity.activity.finish();
			}
			return true;

		}else if ("scanImage".equals(action)) {

					
			
			String imageURI = args.getString(0);
			if (imageURI.startsWith("file://")) {
				imageURI = imageURI.substring(7);
			}
			
			ImageInfo imageInfo = bitmapToGrayscale(imageURI);
			
			if (imageInfo != null){
				//initDecoder();
				byte[] result = BarcodeScanner.MWBscanGrayscaleImage(imageInfo.pixels, imageInfo.width, imageInfo.height);
				
				if (result != null){
					MWResults mwResults = new MWResults(result);
					if (mwResults != null && mwResults.count > 0){
						MWResult mwResult = mwResults.getResult(0);
						
						String typeName = "";
						switch (mwResult.type) {
						case BarcodeScanner.FOUND_25_INTERLEAVED:
							typeName = "Code 25";
							break;
						case BarcodeScanner.FOUND_25_STANDARD:
							typeName = "Code 25 Standard";
							break;
						case BarcodeScanner.FOUND_128:
							typeName = "Code 128";
							break;
						case BarcodeScanner.FOUND_39:
							typeName = "Code 39";
							break;
						case BarcodeScanner.FOUND_93:
							typeName = "Code 93";
							break;
						case BarcodeScanner.FOUND_AZTEC:
							typeName = "AZTEC";
							break;
						case BarcodeScanner.FOUND_DM:
							typeName = "Datamatrix";
							break;
						case BarcodeScanner.FOUND_EAN_13:
							typeName = "EAN 13";
							break;
						case BarcodeScanner.FOUND_EAN_8:
							typeName = "EAN 8";
							break;
						case BarcodeScanner.FOUND_NONE:
							typeName = "None";
							break;
						case BarcodeScanner.FOUND_RSS_14:
							typeName = "Databar 14";
							break;
						case BarcodeScanner.FOUND_RSS_14_STACK:
							typeName = "Databar 14 Stacked";
							break;
						case BarcodeScanner.FOUND_RSS_EXP:
							typeName = "Databar Expanded";
							break;
						case BarcodeScanner.FOUND_RSS_LIM:
							typeName = "Databar Limited";
							break;
						case BarcodeScanner.FOUND_UPC_A:
							typeName = "UPC A";
							break;
						case BarcodeScanner.FOUND_UPC_E:
							typeName = "UPC E";
							break;
						case BarcodeScanner.FOUND_PDF:
							typeName = "PDF417";
							break;
						case BarcodeScanner.FOUND_QR:
							typeName = "QR";
							break;
						case BarcodeScanner.FOUND_CODABAR:
							typeName = "Codabar";
							break;
						case BarcodeScanner.FOUND_128_GS1:
							typeName = "Code 128 GS1";
							break;
						case BarcodeScanner.FOUND_ITF14:
							typeName = "ITF 14";
							break;
						case BarcodeScanner.FOUND_11:
							typeName = "Code 11";
							break;
						case BarcodeScanner.FOUND_MSI:
							typeName = "MSI Plessey";
							break;
						case BarcodeScanner.FOUND_25_IATA:
							typeName = "IATA Code 25";
							break;
						}
						
						JSONObject jsonResult = new JSONObject();
						try {
							jsonResult.put("code", mwResult.text);
							jsonResult.put("type", typeName);
							jsonResult.put("isGS1", mwResult.isGS1);
							jsonResult.put("imageWidth", mwResult.imageWidth);
							jsonResult.put("imageHeight", mwResult.imageHeight);

							if (mwResult.locationPoints != null) {
								jsonResult.put(
										"location",
										new JSONObject().put("p1", new JSONObject().put("x", mwResult.locationPoints.p1.x).put("y", mwResult.locationPoints.p1.y))
												.put("p2", new JSONObject().put("x", mwResult.locationPoints.p2.x).put("y", mwResult.locationPoints.p2.y))
												.put("p3", new JSONObject().put("x", mwResult.locationPoints.p3.x).put("y", mwResult.locationPoints.p3.y))
												.put("p4", new JSONObject().put("x", mwResult.locationPoints.p4.x).put("y", mwResult.locationPoints.p4.y)));
							} else {
								jsonResult.put("location", false);
							}

							JSONArray rawArray = new JSONArray();
							
							for (int i = 0; i < mwResult.bytesLength; i++) {
								rawArray.put((int) (0xff & mwResult.bytes[i]));
							}
						

							jsonResult.put("bytes", rawArray);

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						PluginResult pr = new PluginResult(PluginResult.Status.OK, jsonResult);
						
						callbackContext.sendPluginResult(pr);
						
					} else {
						callbackContext.error(-1);
					}
				} else {
					callbackContext.error(-1);
				}
				
			} else {
				callbackContext.error(-1);
			}
			
			return true;

		}
		return false;
	}

	public static int MAX_IMAGE_SIZE = 1280; 	
	
	public static ImageInfo bitmapToGrayscale(String imageUri){
		
		File image = new File(imageUri);
		if (image == null){
			return null;
		}
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
		
		if (bmOptions.outHeight <= 0 || bmOptions.outWidth <= 0){
			return null;
		}
		
		int height = bmOptions.outHeight;
	    int width = bmOptions.outWidth;
	    int inSampleSize = 1;

	    while  (height > MAX_IMAGE_SIZE || width > MAX_IMAGE_SIZE) {

	        height = height / 2;
	        width = width / 2;
	        inSampleSize *= 2;
	    }
	    
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = inSampleSize;
	    bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
	    
	    if (bitmap == null){
	    	return null;
	    }
	    //convert bitmap to ARGB8888 format for any case
	    Bitmap argbBitmap = Bitmap.createBitmap  (width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(argbBitmap);
	    Paint paint = new Paint();
	    canvas.drawBitmap(bitmap, 0, 0, paint);
	   
	    int[] pixels = new int[width*height] ;
	    
	    argbBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
	    
	    ImageInfo imageInfo = new ImageInfo(width, height);
	    
	    for (int i = 0; i < width * height; i++){
	    	int color = pixels[i];
	    	int B = (int) (color & 0xff);
	    	int G = (int) ((color >> 8) & 0xff);
	    	int R = (int) ((color >> 16) & 0xff);
	    	
	    	int fgray = (int)(0.299 * R + 0.587 * G + 0.114 * B);
	    	if (fgray < 0){
	    		fgray = 0;
	    	}
	    	if (fgray > 255){
	    		fgray = 255;
	    	}
	    	
	    	imageInfo.pixels[i] = (byte) (fgray);
	    }
        argbBitmap.recycle();
        bitmap.recycle();
        bitmap = null;
        argbBitmap = null;
        canvas = null;
        paint = null;
        
		
		return imageInfo;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == 1) {

			if (resultCode == 1 && ScannerActivity.param_closeOnSuccess) {
				JSONObject jsonResult = new JSONObject();
				try {
					jsonResult.put("code", intent.getStringExtra("code"));
					jsonResult.put("type", intent.getStringExtra("type"));
					jsonResult.put("isGS1", (BarcodeScanner.MWBisLastGS1() == 1));

					JSONArray rawArray = new JSONArray();
					byte[] bytes = intent.getByteArrayExtra("bytes");
					if (bytes != null) {
						for (int i = 0; i < bytes.length; i++) {
							rawArray.put((int) (0xff & bytes[i]));
						}
					}

					jsonResult.put("bytes", rawArray);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cbc.success(jsonResult);

			} else if (resultCode == 0) {
				JSONObject jsonResult = new JSONObject();
				try {
					jsonResult.put("code", "");
					jsonResult.put("type", "Cancel");
					jsonResult.put("bytes", "");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				cbc.success(jsonResult);

			}

		}
	}

	public static void initDecoder() {

		// //You should perform registering calls from MWBScanner.js!

		/*
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_25,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_39,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_93,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_128,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_AZTEC,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_DM,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_EANUPC,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_PDF,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_QR,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_RSS,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_CODABAR
		 * ,"username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_DOTCODE
		 * ,"username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_11,
		 * "username", "key");
		 * BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_MSI,
		 * "username", "key");
		 */
		// choose code type or types you want to search for

		// Our sample app is configured by default to search all supported
		// barcodes...
		BarcodeScanner.MWBsetActiveCodes(BarcodeScanner.MWB_CODE_MASK_25 | BarcodeScanner.MWB_CODE_MASK_39
				| BarcodeScanner.MWB_CODE_MASK_93 | BarcodeScanner.MWB_CODE_MASK_128 | BarcodeScanner.MWB_CODE_MASK_AZTEC
				| BarcodeScanner.MWB_CODE_MASK_DM | BarcodeScanner.MWB_CODE_MASK_EANUPC | BarcodeScanner.MWB_CODE_MASK_PDF
				| BarcodeScanner.MWB_CODE_MASK_QR | BarcodeScanner.MWB_CODE_MASK_CODABAR | BarcodeScanner.MWB_CODE_MASK_11
				| BarcodeScanner.MWB_CODE_MASK_MSI | BarcodeScanner.MWB_CODE_MASK_RSS);

		// But for better performance, only activate the symbologies your
		// application requires...
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_25 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_39 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_93 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_128 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_AZTEC
		// );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_DM );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_EANUPC
		// );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_PDF );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_QR );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_RSS );
		// BarcodeScanner.MWBsetActiveCodes(
		// BarcodeScanner.MWB_CODE_MASK_CODABAR );
		// BarcodeScanner.MWBsetActiveCodes(
		// BarcodeScanner.MWB_CODE_MASK_DOTCODE );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_11 );
		// BarcodeScanner.MWBsetActiveCodes( BarcodeScanner.MWB_CODE_MASK_MSI );

		// Our sample app is configured by default to search both directions...
		BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL | BarcodeScanner.MWB_SCANDIRECTION_VERTICAL);
		// set the scanning rectangle based on scan direction(format in pct: x,
		// y, width, height)
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC, RECT_FULL_2D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM, RECT_FULL_2D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR, RECT_FULL_2D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DOTCODE, RECT_DOTCODE);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_11, RECT_FULL_1D);
		BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MSI, RECT_FULL_1D);

		// But for better performance, set like this for PORTRAIT scanning...
		// BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_VERTICAL);
		// set the scanning rectangle based on scan direction(format in pct: x,
		// y, width, height)
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC,
		// RECT_PORTRAIT_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM,
		// RECT_PORTRAIT_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR,
		// RECT_PORTRAIT_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR,RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DOTCODE,RECT_DOTCODE);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_11,
		// RECT_PORTRAIT_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MSI,
		// RECT_PORTRAIT_1D);

		// or like this for LANDSCAPE scanning - Preferred for dense or wide
		// codes...
		// BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL);
		// set the scanning rectangle based on scan direction(format in pct: x,
		// y, width, height)
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_25,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_39,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_93,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_128,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_AZTEC,
		// RECT_LANDSCAPE_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DM,
		// RECT_LANDSCAPE_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_EANUPC,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_QR,
		// RECT_LANDSCAPE_2D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_RSS,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_CODABAR,RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_DOTCODE,RECT_DOTCODE);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_11,
		// RECT_LANDSCAPE_1D);
		// BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_MSI,
		// RECT_LANDSCAPE_1D);

		// Set minimum result length for low-protected barcode types

		BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_25, 5);
		BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_MSI, 5);
		BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_39, 5);
		BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_CODABAR, 5);
		BarcodeScanner.MWBsetMinLength(BarcodeScanner.MWB_CODE_MASK_11, 5);

		// set decoder effort level (1 - 5)
		// for live scanning scenarios, a setting between 1 to 3 will suffice
		// levels 4 and 5 are typically reserved for batch scanning
		BarcodeScanner.MWBsetLevel(2);

		BarcodeScanner.MWBsetResultType(BarcodeScanner.MWB_RESULT_TYPE_MW);

	}

}