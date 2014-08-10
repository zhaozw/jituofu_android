package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiForm;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UIStoreSettings extends BaseUiAuth implements BaseUiFormBuilder{
	private String nameVal, emailVal;
	private TextView nameView, emailView, titleView;
	private Button submitLoginView;
	
	private JSONObject storeSettingsObj;

	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;
	
	private String oldName = "";//旧的商户名称
	private String newName = "";//新的商户名称
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_storesettings);

		initView();
		onUpdate();
		onBind();
	}
	
	private void initView(){
		titleView = (TextView) findViewById(R.id.title);
		nameView = (TextView) findViewById(R.id.name);
		emailView = (TextView) findViewById(R.id.email);
		submitLoginView = (Button) findViewById(R.id.submitLogin);
		
		byte[] storeSettings = StorageUtil.readInternalStoragePrivate(this, C.DIRS.storeSettingsCacheFileName);
		String storeSettingsVal = null;
		if (storeSettings.length > 0 && storeSettings[0] != 0) {
			try {
				storeSettingsVal = new String(storeSettings, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(storeSettingsVal != null){
			try {
				storeSettingsObj = new JSONObject(storeSettingsVal);
				oldName = storeSettingsObj.getString("name") != null ? storeSettingsObj.getString("name") : "";
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void onUpdate() {
		titleView.setText(this.getString(R.string.STORESETTINGS_TITLE));
		nameView.setText(oldName);
	}

	private void onBind() {
		this.onCustomBack();
		
		RelativeLayout nameViewBox = (RelativeLayout) nameView.getParent();
		nameViewBox.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doUpdateNameTask();
			}});
	}
	
	public void doSubmit(){
	}
	
	public boolean validation() {
		return false;
		
	}

	private void doUpdateNameTask(){
		LinearLayout view = (LinearLayout) LinearLayout.inflate(this,
				R.layout.template_edit_text, null);
		baseDialogBuilder = new BaseDialog.Builder(this);
		baseDialogBuilder.setContentView(view, UIStoreSettings.this);
		baseDialogBuilder.setTitle("更新" + this.getResources().getString(R.string.STORESETTINGS_NAME));
		
		final EditText newNameView = (EditText) baseDialogBuilder.contentView.findViewById(R.id.editText);
		
		newNameView.setText(oldName);
		newNameView.setSelected(true);
		
		baseDialogBuilder.setPositiveButton(R.string.COMMON_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// TODO Auto-generated method stub
						newName = AppUtil.trimAll(getEditValue(newNameView));
						
						if (newName == null) {
							showToast(R.string.STORESETTINGS_NAME_SPECIFY);
							return;
						}else if(newName.equals(oldName)){
							showToast(R.string.STORESETTINGS_NEW_NAME_EQUAL_OLD_NAME);
							return;
						} else if ((AppUtil.getStrLen(newName) < 2)
								|| (AppUtil.getStrLen(newName) > 25)) {
							showToast(R.string.STORESETTINGS_NAME_INVALID);
							return;
						}
						
						doUpdateNameTask(newName);
					}
				});
		baseDialogBuilder.setNegativeButton(R.string.COMMON_CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						baseDialog.dismiss();
					}
				});
		baseDialog = baseDialogBuilder.create();
		baseDialog.show();
	}
	
	private void doUpdateNameTask(String newName){
		AppUtil.showLoadingPopup(this, R.string.COMMON_CLZ);
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("name", newName);

		try {
			doTaskAsync(C.TASK.storesettingsupdate, C.API.host
					+ C.API.storesettingsupdate, urlParams);
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
			//更新商户名称
			if(taskId == C.TASK.storesettingsupdate){
				nameView.setText(newName);
				//更新本地缓存的商户信息数据
				storeSettingsObj.put("name", newName);
				oldName = newName;
				StorageUtil.writeInternalStoragePrivate(this, C.DIRS.storeSettingsCacheFileName, storeSettingsObj.toString());
				baseDialog.dismiss();
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	@Override
	public void beforeSubmit() {
		// TODO Auto-generated method stub
		
	}
}
