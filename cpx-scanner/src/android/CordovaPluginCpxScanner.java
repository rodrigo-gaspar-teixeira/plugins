package com.cpxscanner;

import android.content.Context;
import android.os.RemoteException;
import android.os.IBinder;
import android.util.Log;

import vendor.kozen.hardware.scan.V1_0.IScan;
import vendor.kozen.hardware.scan.V1_0.IScanCallback;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginCpxScanner extends CordovaPlugin {

    private static final String LOG_TAG = "CordovaPluginCpxScanner";

    private static volatile IScan mScanService = null;

    private CallbackContext callbackContext = null;

    private Context context = null;

    @Override
    protected void pluginInitialize() {
        super.initialize(cordova, webView);

        context = this.cordova.getActivity().getApplicationContext();

        try {
            mScanService = IScan.getService();
            if (mScanService == null) {
                Log.e(LOG_TAG, "Failed to initialize mScanService: Service is null.");
            }
        } catch (java.util.NoSuchElementException e) {
            Log.e(LOG_TAG, "Failed to get scan service", e);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Failed to initialize mScanService: " + e.getMessage(), e);
            e.printStackTrace();
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


    private JavaScanCallback mScanCalback = new JavaScanCallback() {
        @Override
        public void onScanResult(String barcode,String result) {
            //super.onScanResult(barcode, result);
            Log.d(LOG_TAG, "barcode = " + barcode);
            Log.d(LOG_TAG, "result = " + result);
            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (callbackContext != null) {
                        JSONObject json = new JSONObject();

                        try {
                            json.put("barcode", barcode);
                            json.put("result", result);
                        } catch (Exception e) {
                            LOG.d(
                                LOG_TAG,
                                "Error sending cpx scanner receiver: " + e.getMessage(),
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
    };

    private class JavaScanCallback extends IScanCallback.Stub {
        private final String TAG = "JavaScanCallback";

        @Override
        public void onScanResult(String barcode, String result) {
            Log.d(TAG, "barcode = " + barcode);
            Log.d(TAG, "result = " + result);
        }

        @Override
        public void onTimeOut(){

        }
}

}

