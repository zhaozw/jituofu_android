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

public class UIUserSettings extends BaseUiAuth implements BaseUiFormBuilder{
	private String emailVal;
	private TextView emailView, titleView;
	private Button submitLoginView;
	
	private JSONObject userInfo;

	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;
	
	private String oldEmail = "";//旧的邮箱名
	private String newEmail = "";//新的邮箱名
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_user_settings);

		initView();
		onUpdate();
		onBind();
	}
	
	private void initView(){
		titleView = (TextView) findViewById(R.id.title);
		emailView = (TextView) findViewById(R.id.email);
		submitLoginView = (Button) findViewById(R.id.submitLogin);
		
		byte[] userinfo = StorageUtil.readInternalStoragePrivate(this, C.DIRS.userInfoFileName);
		String userinfoVal = null;
		if (userinfo.length > 0 && userinfo[0] != 0) {
			try {
				userinfoVal = new String(userinfo, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(userinfoVal != null){
			try {
				userInfo = new JSONObject(userinfoVal);
				oldEmail = userInfo.getString("email");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void onUpdate() {
		titleView.setText(this.getString(R.string.USERSETTINGS_TITLE));
		if(userInfo != null){
			try {
				emailView.setText(userInfo.getString("email"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void onBind() {
		this.onCustomBack();
		
		RelativeLayout emailViewBox = (RelativeLayout) emailView.getParent();
		emailViewBox.setOnClickListener(new OnClickListener(){

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
		view.setMinimumWidth((int) (UIUserSettings.this.getWindowManager()  
                .getDefaultDisplay().getWidth() * 0.8));
		baseDialogBuilder = new BaseDialog.Builder(this);
		baseDialogBuilder.setContentView(view);
		baseDialogBuilder.setTitle("更新" + this.getResources().getString(R.string.COMMON_EMAIL));
		
		final EditText newEmailView = (EditText) baseDialogBuilder.contentView.findViewById(R.id.editText);
		
		newEmailView.setText(oldEmail);
		newEmailView.setSelected(true);
		
		baseDialogBuilder.setPositiveButton(R.string.COMMON_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// TODO Auto-generated method stub
						newEmail = AppUtil.trimAll(getEditValue(newEmailView));
						
						if (newEmail != null) {
							if (AppUtil.getStrLen(newEmail) < 5 || newEmail.indexOf("@") < 0) {
								showToast(R.string.USERSETTINGS_EMAIL_INVALID);
								return;
							}
						} else {
							showToast(R.string.USERSETTINGS_EMAIL_SPECIFY);
							return;
						}
						
						doUpdateNameTask(newEmail);
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
	
	private void doUpdateNameTask(String newEmail){
		AppUtil.showLoadingPopup(this, R.string.COMMON_CLZ);
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("email", newEmail);

		try {
			doTaskAsync(C.TASK.update, C.API.host
					+ C.API.update, urlParams);
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
			//更新邮箱
			if(taskId == C.TASK.update){
				emailView.setText(newEmail);
				//更新本地缓存的商户信息数据
				userInfo.put("email", newEmail);
				oldEmail = newEmail;
				StorageUtil.writeInternalStoragePrivate(this, C.DIRS.userInfoFileName, userInfo.toString());
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
