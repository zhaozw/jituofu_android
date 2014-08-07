package com.jizhangbao.xiaodian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Portal extends Activity{

	private static final long SPLASH_DISPLAY_LENGHT = 0;
	
	public static String version = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.portal);
		          
        //全屏            
        //getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN, WindowManager.LayoutParams. FLAG_FULLSCREEN); 
   	
    	try {
			this.version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			
		}

        if(NetworkCheck.checkNet(Portal.this) == false){	
        	goNoConnectPage();
    	}else{
    		new GetAppInfo(this).execute("");
    	}
    	
    }
    
    @Override  
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {  
        if (keyCode == KeyEvent.KEYCODE_BACK )  
        {  
            finish(); 
        }  
          
        return false;  
    }  
    
    @SuppressLint("NewApi")
	public static void goHome(final Portal portal) {
    	Intent intent = new Intent(portal, MainActivity.class);
    	intent.setClass(portal, MainActivity.class);
    	portal.startActivity(intent);
    	portal.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    	portal.finish();
    }
    
    public static void prepareGoHome(final Portal portal){
    	new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				goHome(portal);
			}
    		
    	}, SPLASH_DISPLAY_LENGHT);
    }
    
    public static void showUpdateLog(String log, final Portal portal){
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(portal);
			alertDialogBuilder.setTitle("发现有更新");
			alertDialogBuilder
				.setMessage(log)
				.setCancelable(false)
				.setPositiveButton("下载新版本", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Intent updateIntent = new Intent(portal, DownloadService.class); 
				    	updateIntent.putExtra("titleId", R.string.update_app_name); 
						portal.startService(updateIntent);
						portal.finish();
					}
				  })
				.setNegativeButton("取消",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
						prepareGoHome(portal);
					}
				});
 
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
    }
    @SuppressLint("NewApi")
	public void goNoConnectPage(){
    	Intent intent = new Intent(Portal.this, NoConnect.class);
    	intent.setClass(Portal.this, NoConnect.class);
    	this.startActivity(intent);
    	overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    	this.finish();
    }
}

class GetAppInfo extends AsyncTask<String, String, String> {

    InputStream inputStream = null;
    String result = ""; 
    
    private static final String	host = "http://10.0.2.2/client.php"; 
    private Portal portal;
    
    public GetAppInfo(Portal p){
    	this.portal = p;
    }

    protected void onPreExecute() {
    	
    }

    protected String doInBackground(String... arg0) {
        try {
            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(host);
            
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("api", "getProductInfo.php")); 
            params.add(new BasicNameValuePair("action", "query")); 
            params.add(new BasicNameValuePair("username", "username")); 
            HttpEntity httpentity = new UrlEncodedFormEntity(params, "utf8");
            httpPost.setEntity(httpentity);
            
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            inputStream = httpEntity.getContent();
        } catch (UnsupportedEncodingException e1) {
            Log.e("UnsupportedEncodingException", e1.toString());
            e1.printStackTrace();
        } catch (ClientProtocolException e2) {
            Log.e("ClientProtocolException", e2.toString());
            e2.printStackTrace();
        } catch (IllegalStateException e3) {
            Log.e("IllegalStateException", e3.toString());
            e3.printStackTrace();
        } catch (IOException e4) {
            Log.e("IOException", e4.toString());
            e4.printStackTrace();
        }
        // Convert response to string using String Builder
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder sBuilder = new StringBuilder();

            String line = null;
            while ((line = bReader.readLine()) != null) {
                sBuilder.append(line + "\n");
            }

            inputStream.close();
            result = sBuilder.toString();

        } catch (Exception e) {
            Log.e("StringBuilding & BufferedReader", "Error converting result " + e.toString());
        }
		return result;
	}
    
    protected void onPostExecute(String result) {
        try {
        	JSONObject data = new JSONObject(result);
        	int bizCode = data.getInt("bizCode");
        	if(Portal.version != null && bizCode == 1){
        		String serverVersion = data.getJSONObject("data").getString("android_version");
        		String updateLog = data.getJSONObject("data").getString("android_update_log");
        		
        		if(!Portal.version.equals(serverVersion) && updateLog != null){
        			System.out.println(serverVersion);
        			System.out.println(Portal.version);
        			Portal.showUpdateLog(updateLog, portal);
        		}else{
        			Portal.prepareGoHome(portal);
        		}
        	}else{
        		Portal.prepareGoHome(portal);
        	}
        } catch (JSONException e) {
        	Portal.prepareGoHome(portal);
        }
    }
    
}