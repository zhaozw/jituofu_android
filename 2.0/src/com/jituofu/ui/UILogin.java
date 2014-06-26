package com.jituofu.ui;

import java.util.HashMap;

import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiForm;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UILogin extends BaseUiForm {
	private String usernameVal, passwordVal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		onUpdateUi();
		onBindUi();
	}

	@Override
	protected void onUpdateUi() {
		super.onUpdateUi();
		
		TextView title = (TextView) findViewById(R.id.title);
		EditText usernameEditText = (EditText) findViewById(R.id.username);

		Bundle bundle = this.getIntent().getExtras();

		title.setText(this.getString(R.string.LOGIN_TITLE));

		if (bundle != null) {
			String extraUsername = bundle.getString("username");
			usernameEditText.setText(extraUsername);
		}
	}

	@Override
	protected void onBindUi() {
		super.onBindUi();
		
		TextView gotoRegisterBtn = (TextView) this
				.findViewById(R.id.goregister);
		TextView forgotBtn = (TextView) this.findViewById(R.id.forgot);
		Button loginBtn = (Button) this.findViewById(R.id.submitLogin);
		
		forgotBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				forward(UIForgot.class);
			}});
		gotoRegisterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forward(UIRegister.class);
			}
		});
		loginBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				beforeSubmit();
			}});
	}
	
	@Override
	protected void doSubmit(){
		AppUtil.showLoadingPopup(this, R.string.LOGIN_LOGINNING);
		doTaskLogin();
	}
	
	@Override
	protected boolean validation() {
		boolean result = false;
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		EditText passwordEditText = (EditText) findViewById(R.id.password);

		String username = getEditValue(usernameEditText);
		String password = getEditValue(passwordEditText);

		// 验证用户名
		if (username != null) {
			if (AppUtil.getStrLen(username) < C.USERNAMELENGTH.MIN
					|| AppUtil.getStrLen(username) > C.USERNAMELENGTH.MAX) {
				showToast(R.string.COMMON_USERNAME_ERROR);
			} else {
				result = true;
			}
		} else {
			showToast(R.string.COMMON_USERNAME_HINT);
		}

		// 验证密码
		if (result) {
			if (password != null) {
				if (AppUtil.getStrLen(password) < C.PASSWORDLENGTH.MIN
						|| AppUtil.getStrLen(password) > C.PASSWORDLENGTH.MAX) {
					result = false;
					showToast(R.string.COMMON_PASSWORD_ERROR);
				}else{
					result = true;
				}
			} else {
				result = false;
				showToast(R.string.LOGIN_PASSWORD_HINT);
			}
		}

		

		this.usernameVal = username;
		this.passwordVal = password;

		return result;
	}

	private void doTaskLogin() {
		String deviceId = AppUtil.getDeviceId(this);

		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("username", this.usernameVal);
		urlParams.put("password", this.passwordVal);
		urlParams.put("clientId", deviceId);

		try {
			this.doTaskAsync(C.TASK.login, C.API.host + C.API.login,
					urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		if (resultStatus == 100) {
			JSONObject operation = message.getOperation();
			
			String userId = operation.getString("id");
			String cookie = operation.getString("cookie");

			StorageUtil.writeInternalStoragePrivate(this, C.DIRS.userCookieFileName, cookie);
			StorageUtil.writeInternalStoragePrivate(this, C.DIRS.userIdFileName, userId);

			this.forward(UIHome.class);
			this.finish();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}
