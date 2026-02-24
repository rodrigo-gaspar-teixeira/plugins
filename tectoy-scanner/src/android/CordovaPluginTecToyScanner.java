package com.tectoyscanner;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;

import br.com.daruma.framework.mobile.exception.DarumaException;
import br.com.itfast.tectoy.Dispositivo;
import br.com.itfast.tectoy.TecToy;
import br.com.itfast.tectoy.TecToyScannerCallback;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginTecToyScanner extends CordovaPlugin {

  private static final String LOG_TAG = "CordovaPluginTecToyScanner";

  private TecToy tecToy = null;

  private static CallbackContext callbackContext = null;

  private Context context = null;

  @Override
  protected void pluginInitialize() {
    super.initialize(cordova, webView);

    context = this.cordova.getActivity().getApplicationContext();


    tecToy = new TecToy(Dispositivo.L2Ks, context);

    Thread thrIniciaScanner;
    try {
      thrIniciaScanner = new Thread(iniciarScanner);
      thrIniciaScanner.start();
      thrIniciaScanner.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
    }
    return false;
  }

  private TecToyScannerCallback scannerCallback = new TecToyScannerCallback() {
    @Override
    public void retornarCodigo(String strCodigo) {
      tecToy.pararScanner();
      new Handler(Looper.getMainLooper())
        .post(
          new Runnable() {
            @Override
            public void run() {
              if (callbackContext != null) {
                JSONObject json = new JSONObject();

                try {
                  json.put("barcode", strCodigo);
                } catch (Exception e) {
                  LOG.d(
                    LOG_TAG,
                    "Error sending urovo scanner receiver: " + e.getMessage(),
                    e
                  );
                }

                PluginResult result = new PluginResult(
                  PluginResult.Status.OK,
                  json
                );
                result.setKeepCallback(true);
                callbackContext.sendPluginResult(result);
              }
            }
          }
        );
    }
  };

  private Runnable iniciarScanner = new Runnable() {
    @Override
    public void run() {
      try {
        Looper.prepare();
        try {
          tecToy.iniciarScanner(scannerCallback);
        } catch (Exception e) {
          Log.e("ERRO", e.getMessage() != null ? e.getMessage() : e.toString());
        }
      } catch (DarumaException de) {
        throw de;
      }
    }
  };
}
