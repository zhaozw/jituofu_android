package com.jituofu.ui;

import java.util.HashMap;

import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiForm;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UIRegister extends BaseUiForm {
	private String usernameVal, emailVal, passwordVal, REGISTER_CPASSWORDVal;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		onCustomBack();
		onUpdateUi();
		onBindUi();
	}

	@Override
	protected void onUpdateUi() {
		super.onUpdateUi();

		TextView title = (TextView) findViewById(R.id.title);
		TextView topBar2Right = (TextView) findViewById(R.id.topBar2Right);

		title.setText(R.string.COMMON_GOTOREGISTER);
		topBar2Right.setText(R.string.help);
	}

	@Override
	protected void onBindUi() {
		super.onBindUi();
		TextView topBar2Right = (TextView) findViewById(R.id.topBar2Right);
		Button btn = (Button) findViewById(R.id.submitRegister);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				beforeSubmit();
			}
		});

		topBar2Right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forward(UIHelp.class);
			}
		});
	}

	@Override
	protected void doSubmit() {
		AppUtil.showLoadingPopup(UIRegister.this, R.string.REGISTER_ZCZ);
		doTaskRegister();
	}

	@Override
	protected boolean validation() {
		boolean result = false;
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		EditText emailEditText = (EditText) findViewById(R.id.email);
		EditText passwordEditText = (EditText) findViewById(R.id.password);
		EditText REGISTER_CPASSWORDEditText = (EditText) findViewById(R.id.REGISTER_CPASSWORD);

		String username = AppUtil.trimAll(getEditValue(usernameEditText));
		String email = AppUtil.trimAll(getEditValue(emailEditText));
		String password = AppUtil.trimAll(getEditValue(passwordEditText));
		String REGISTER_CPASSWORD = AppUtil
				.trimAll(getEditValue(REGISTER_CPASSWORDEditText));

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

		// 验证邮箱
		if (result) {
			if (email != null) {
				if (AppUtil.getStrLen(email) < 5 || email.indexOf("@") < 0) {
					result = false;
					showToast(R.string.COMMON_EMAIL_ERROR);
				}
			} else {
				result = false;
				showToast(R.string.COMMON_EMAIL_HINT);
			}
		}

		// 验证密码
		if (result) {
			if (password != null) {
				if (AppUtil.getStrLen(password) < C.PASSWORDLENGTH.MIN
						|| AppUtil.getStrLen(password) > C.PASSWORDLENGTH.MAX) {
					result = false;
					showToast(R.string.COMMON_PASSWORD_ERROR);
				}
			} else {
				result = false;
				showToast(R.string.LOGIN_PASSWORD_HINT);
			}
		}
		if (result) {
			if (REGISTER_CPASSWORD != null
					&& !password.equals(REGISTER_CPASSWORD)) {
				showToast(R.string.REGISTER_CPASSWORD_INVALID);
				result = false;
			} else if (REGISTER_CPASSWORD == null) {
				result = false;
				showToast(R.string.REGISTER_CPASSWORD_TIPS);
			}
		}

		this.usernameVal = username;
		this.emailVal = email;
		this.passwordVal = password;
		this.REGISTER_CPASSWORDVal = REGISTER_CPASSWORD;

		return result;
	}

	private void doTaskRegister() {
		String deviceId = AppUtil.getDeviceId(this);

		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("username", this.usernameVal);
		urlParams.put("email", this.emailVal);
		urlParams.put("password", this.passwordVal);
		urlParams.put("cpassword", this.REGISTER_CPASSWORDVal);
		urlParams.put("clientId", deviceId);

		try {
			this.doTaskAsync(C.TASK.register, C.API.host + C.API.register,
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
			boolean hasId = operation.has("id");
			boolean hasCookie = operation.has("cookie");

			this.finish();

			if (!hasId || !hasCookie) {
				Bundle bundle = new Bundle();
				bundle.putString("username", this.usernameVal);
				this.showToast(R.string.register_success);
				this.forward(UILogin.class, bundle);
			} else {
				String userId = operation.getString("id");
				String cookie = operation.getString("cookie");

				StorageUtil.writeInternalStoragePrivate(this,
						C.DIRS.userCookieFileName, cookie);
				StorageUtil.writeInternalStoragePrivate(this,
						C.DIRS.userIdFileName, userId);

				//发送注册成功的广播
				Intent broadcast = new Intent();
				broadcast.setAction("com.jituofu.ui.RegisterAndLoginSuccess");
				broadcast.putExtra("type", "RegisterAndLoginSuccess");
				this.sendBroadcast(broadcast);
				
				this.forward(UIHome.class);
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}
