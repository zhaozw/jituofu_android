package com.jituofu.ui;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUpdateService;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

public class UIUpdate extends BaseUi{
	private TextView titleView;
	private LinearLayout noUpdateView, hasUpdateView;
	private Button updateBtnView;
	
	private String currentVersion;
	private String url, newVersion, logs, date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_update);

		currentVersion = AppUtil.getVersion(this);
		
		initView();
		onUpdateView();
		onBind();
		
		AppUtil.showLoadingPopup(this, R.string.SOFTWAREUPDATE_LOADING);
		checkUpdate();
	}
	
	private void checkUpdate() {
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("platform", "1");

		try {
			doTaskAsync(C.TASK.softwareupdate, C.API.host + C.API.softwareupdate, urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();

		if (resultStatus == 100) {
			if(operation.has("update")){
				JSONObject update = operation.getJSONObject("update");
				if(update.getString("version").equals(currentVersion)){
					noUpdate();
				}else{
					url = update.getString("url");
					newVersion = update.getString("version");
					date = update.getString("date");
					logs = update.getString("log");
					hasUpdate();
				}
			}else{
				noUpdate();
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
	
	private void onUpdateView(){
		titleView.setText(R.string.COMMON_CHECKUPDATE_TITLE);
	}
	
	private void initView(){
		titleView = (TextView) findViewById(R.id.title);
		noUpdateView = (LinearLayout) this.findViewById(R.id.noUpdate);
		hasUpdateView = (LinearLayout) this.findViewById(R.id.hasUpdate);
		updateBtnView = (Button) findViewById(R.id.okBtn);
	}
	
	private void noUpdate(){
		noUpdateView.setVisibility(View.VISIBLE);
		hasUpdateView.setVisibility(View.GONE);
	}
	
	private void hasUpdate(){
		noUpdateView.setVisibility(View.GONE);
		hasUpdateView.setVisibility(View.VISIBLE);
		((TextView) hasUpdateView.findViewById(R.id.version)).setText(newVersion);
		((TextView) hasUpdateView.findViewById(R.id.log)).setText(Html.fromHtml(logs));
		
		String[] dateArray = date.split("\\s");
		((TextView) hasUpdateView.findViewById(R.id.date)).setText(this.getString(R.string.SOFTWAREUPDATE_UPDATE_DATE)+dateArray[0]);
	}
	
	private void onBind(){
		this.onCustomBack();
		updateBtnView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(AppUtil.isServiceStarted(UIUpdate.this, "com.jituofu.base.BaseUpdateService")){
					return;
				}
				Intent updateIntent = new Intent(UIUpdate.this, BaseUpdateService.class); 
				updateIntent.putExtra("title", getString(R.string.app_name)); 
		    	updateIntent.putExtra("fileName", getString(R.string.app_name)+newVersion); 
		    	updateIntent.putExtra("apkUrl", url); 
		    	UIUpdate.this.startService(updateIntent);
			}});
	}
}
