package cn.com.jizhangbao.ui;

import java.util.HashMap;

import org.json.JSONObject;

import cn.com.jizhangbao.R;
import cn.com.jizhangbao.base.BaseMessage;
import cn.com.jizhangbao.base.BaseUi;
import cn.com.jizhangbao.base.C;
import cn.com.jizhangbao.util.AppUtil;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UILogin extends BaseUi {
	Dialog dialog = null;
	boolean validated = false;

	private String usernameVal, passwordVal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		updateUI();
		bindUI();
	}

	@Override
	protected void updateUI() {
		super.updateUI();
		
		TextView title = (TextView) findViewById(R.id.title);
		EditText usernameEditText = (EditText) findViewById(R.id.username);

		Bundle bundle = this.getIntent().getExtras();

		title.setText(this.getString(R.string.login));

		if (bundle != null) {
			String extraUsername = bundle.getString("username");
			usernameEditText.setText(extraUsername);
		}
	}

	@Override
	protected void bindUI() {
		super.bindUI();
		
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
				// 防止重复点击
				if (validated) {
					return;
				}

				validated = true;

				if (validation()) {
					showPopup(R.string.loginning);
					doTaskLogin();
				}
			}});
	}
	
	private void showPopup(int txt) {
		dialog = getPopupDialog(this);

		this.setPopupView(R.layout.popup_contentview);

		TextView popupTxt = (TextView) dialog.findViewById(R.id.popupTxt);

		popupTxt.setText(txt);
		showPopupDialog(false);
	}
	
	private boolean validation() {
		boolean result = false;
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		EditText passwordEditText = (EditText) findViewById(R.id.password);

		String username = getEditValue(usernameEditText);
		String password = getEditValue(passwordEditText);

		// 验证用户名
		if (username != null) {
			if (AppUtil.getStrLen(username) < 2
					|| AppUtil.getStrLen(username) > 20) {
				showToast(R.string.username_error);
			} else {
				result = true;
			}
		} else {
			showToast(R.string.username_hint);
		}

		// 验证密码
		if (result) {
			if (password != null) {
				if (AppUtil.getStrLen(password) < 6
						|| AppUtil.getStrLen(password) > 50) {
					result = false;
					showToast(R.string.password_error);
				}else{
					result = true;
				}
			} else {
				result = false;
				showToast(R.string.password_hint);
			}
		}

		validated = false;

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

			AppUtil.writeInternalStoragePrivate(this, "ck", cookie);
			AppUtil.writeInternalStoragePrivate(this, "ud", userId);

			this.forward(UIHome.class);
			this.finish();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}