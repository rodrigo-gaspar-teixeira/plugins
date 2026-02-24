package com.chainwayscanner;

import android.os.AsyncTask;

import android.content.Context;

import com.rscja.barcode.BarcodeDecoder;
import com.rscja.barcode.BarcodeFactory;
import com.rscja.barcode.BarcodeUtility;
import com.rscja.deviceapi.entity.BarcodeEntity;
import com.rscja.scanner.utility.ScannerUtility;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginChainway extends CordovaPlugin {

  private static CallbackContext callbackContext = null;
  private static BarcodeDecoder barcodeDecoder = null;
  private static Context contextFromPluginInitialize = null;

  @Override
  protected void pluginInitialize() {
    super.initialize(cordova, webView);
    this.barcodeDecoder = BarcodeFactory.getInstance().getBarcodeDecoder();
    this.contextFromPluginInitialize = this.webView.getContext();
    new AsyncDataUpdate().execute();
  }

  @Override
  public boolean execute(
    String action,
    JSONArray args,
    CallbackContext callbackContext
  ) throws JSONException {
    if (action.equals("scan")) {
      this.callbackContext = callbackContext;
      return true;
    } else if (action.equals("cancel")) {
      this.stop();
      if (this.callbackContext != null) {
        PluginResult result = new PluginResult(
          PluginResult.Status.ERROR,
          "USER_CANCEL"
        );
        result.setKeepCallback(true);
        this.callbackContext.sendPluginResult(result);
        this.callbackContext = null;
      }
      return true;
    } 
    return false;
  }

  @Override
  public void onDestroy() {
    close();
    super.onDestroy();
  }

  private class AsyncDataUpdate extends AsyncTask<String, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(String... params) {
      // TODO Auto-generated method stub
      open();
      return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {
      // TODO Auto-generated method stub
      super.onPreExecute();
    }
  }

  private void start() {
    barcodeDecoder.startScan();
  }

  private void stop() {
    barcodeDecoder.stopScan();
  }

  private void open() {
    barcodeDecoder.open(this.contextFromPluginInitialize);
    BarcodeUtility.getInstance().enablePlaySuccessSound(this.contextFromPluginInitialize, true); //success Sound
    BarcodeUtility.getInstance().setReleaseScan(this.contextFromPluginInitialize, true); // should release scan
    

    /*TODO: Will stay commented in case any of the features bellow are needed
        BarcodeUtility.getInstance().setPrefix(this,"");
        BarcodeUtility.getInstance().setSuffix(this,"");
        BarcodeUtility.getInstance().enablePlaySuccessSound(this,true); //success Sound
        BarcodeUtility.getInstance().enableVibrate(this,true);//vibrate
        BarcodeUtility.getInstance().enableEnter(this,true);//addition enter

        BarcodeUtility.getInstance().enableContinuousScan(this,true);//Continuous scanning
        BarcodeUtility.getInstance().setContinuousScanIntervalTime(this,100);//Unit: milliseconds
        BarcodeUtility.getInstance().setContinuousScanTimeOut(this,9999);//Unit: milliseconds
        */

    barcodeDecoder.setDecodeCallback(
      new BarcodeDecoder.DecodeCallback() {
        @Override
        public void onDecodeComplete(BarcodeEntity barcodeEntity) {
          JSONObject json = new JSONObject();
          if (callbackContext != null) {
            if (
              barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS
            ) {
                try {
                    json.put("barcode", barcodeEntity.getBarcodeData());
                } catch(Exception e) {
                    LOG.d("CordovaPluginChainway", "Error sending chainway scanner receiver: " + e.getMessage(), e);
                }
              PluginResult result = new PluginResult(
                PluginResult.Status.OK,
                json
              );
              result.setKeepCallback(true);
              callbackContext.sendPluginResult(result);
            } else {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, "FAIL");
				        result.setKeepCallback(true);
				        callbackContext.sendPluginResult(result);
            }
          }
        }
      }
    );
  }

  private void close() {
    barcodeDecoder.close();
  }
}
