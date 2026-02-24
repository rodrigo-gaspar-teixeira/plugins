package com.xcscanner;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xcheng.scanner.XcBarcodeScanner;
import com.xcheng.scanner.ScannerResult;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginXcScanner extends CordovaPlugin {

    private static final String LOG_TAG = "CordovaPluginXcScanner";

    private Context context; // Declared context
    private CallbackContext callbackContext; // Declared callbackContext

    @Override
    protected void pluginInitialize() {
        super.initialize(cordova, webView);

        context = this.cordova.getActivity().getApplicationContext();
        try {
            this.initScan(context);
        } catch (Exception e) { // Catch only generic exceptions
            LOG.e(LOG_TAG, "Unexpected error during scanner initialization: " + e.getMessage(), e);
        }
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("scan")) {
            this.callbackContext = callbackContext;
            return true;
        }
        return false;
    }

    private void initScan(Context context) {
        XcBarcodeScanner.init(context, new ScannerResult() {
            @Override
            public void onResult(String barcode) {
                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (callbackContext != null) {
                            JSONObject json = new JSONObject();

                            try {
                                json.put("barcode", barcode);
                            } catch (Exception e) {
                                LOG.d(
                                    LOG_TAG,
                                    "Error sending xc scanner receiver: " + e.getMessage(),
                                    e
                                );
                                callbackContext.error("Failed to process scan result.");
                                return;
                            }

                        PluginResult result = new PluginResult(
                            PluginResult.Status.OK,
                            json
                        );

                        result.setKeepCallback(true);
                        callbackContext.sendPluginResult(result);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        try {
            XcBarcodeScanner.deInit(context);
        } catch (Exception e) { // Catch only generic exceptions
            LOG.e(LOG_TAG, "Unexpected error during scanner deInitialization: " + e.getMessage(), e);
        }
    }
}
