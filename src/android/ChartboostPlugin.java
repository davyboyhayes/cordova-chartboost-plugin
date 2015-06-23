package com.spilgames.chartboost.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

public class ChartboostPlugin extends CordovaPlugin{
	
	
	private static final String ACTION_INI_CHARBOOST = "init";
	private static final String ACTION_SHOW_INTERSTITIAL = "showInterstitial";
	
	@Override
	public void onDestroy() {
		Chartboost.onDestroy(cordova.getActivity());
		super.onDestroy();
	}
	
	@Override
	public void onResume(boolean multitasking) {
		Chartboost.onResume(cordova.getActivity());
		super.onResume(multitasking);
	}
	
	@Override
	public void onPause(boolean multitasking) {
		Chartboost.onPause(cordova.getActivity());
		super.onPause(multitasking);
	}
	
    private CallbackContext callbackContext = null;
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException{
		
		if (action.equals(ACTION_INI_CHARBOOST)) {
			final String appId = args.getString(0);
			final String appSignature = args.getString(1);
			
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Chartboost.startWithAppId(cordova.getActivity(), appId , appSignature);
					Chartboost.setDelegate(delegate);
					Chartboost.onCreate(cordova.getActivity());
					Chartboost.onStart(cordova.getActivity());
				}
			});
			
			return true;
		}else if(action.equals(ACTION_SHOW_INTERSTITIAL)){
			final String location = args.getString(0);
			callbackContext = callback;
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Chartboost.showInterstitial(location);
				}
			});
            
            return true;
		}
		
		return false;
	}

    private ChartboostDelegate delegate = new ChartboostDelegate() {
        @Override
        public void didDismissInterstitial(String location) {
            if (callbackContext != null) {
                callbackContext.success("{\"location\": \"" + location + "\"}");
            }
        }
        
        @Override
        public void didFailToLoadInterstitial(String location, CBImpressionError error) {
            if (callbackContext != null) {
                callbackContext.error("{\"location\": \"" + location + "\",\"Error\": \"" + error.name() +"\"}");
            }
        }
    };
}