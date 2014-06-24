package com.phonegap.plugins;

import android.content.Intent;
import android.net.Uri;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class Comment extends CordovaPlugin {

	public boolean execute(String action, JSONArray args, CallbackContext callbackContext)  throws JSONException {
		if ("show".equals(action)) {
			show();
			callbackContext.success();
			return true;
		}
		
		return false;
	}
	
	public void show(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + this.cordova.getActivity().getPackageName()));
		this.cordova.getActivity().startActivity(intent);
	}
}
