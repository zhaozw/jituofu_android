package cn.com.jizhangbao.ui;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.com.jizhangbao.R;
import cn.com.jizhangbao.base.BaseMessage;
import cn.com.jizhangbao.base.BaseUi;
import cn.com.jizhangbao.base.C;
import cn.com.jizhangbao.util.AppUtil;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UIRegister extends BaseUi {
	Dialog dialog = null;
	boolean validated = false;

	private String usernameVal, emailVal, passwordVal, cpasswordVal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		globalBackLogic();
		updateUI();
		bindUI();
	}

	@Override
	protected void updateUI() {
		super.updateUI();

		TextView title = (TextView) findViewById(R.id.title);
		TextView actionTopRight = (TextView) findViewById(R.id.actionTopRight);

		title.setText(R.string.goregister);
		actionTopRight.setText(R.string.help);
	}

	@Override
	protected void bindUI() {
		super.bindUI();
		TextView actionTopRight = (TextView) findViewById(R.id.actionTopRight);
		Button btn = (Button) findViewById(R.id.submitRegister);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 防止重复点击
				if (validated) {
					return;
				}

				validated = true;

				if (validation()) {
					AppUtil.showPopup(UIRegister.this, R.string.register_zcz);
					doTaskRegister();
				}
			}
		});

		actionTopRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				forward(UIHelp.class);
			}
		});
	}

	private boolean validation() {
		boolean result = false;
		EditText usernameEditText = (EditText) findViewById(R.id.username);
		EditText emailEditText = (EditText) findViewById(R.id.email);
		EditText passwordEditText = (EditText) findViewById(R.id.password);
		EditText cpasswordEditText = (EditText) findViewById(R.id.cpassword);

		String username = getEditValue(usernameEditText);
		String email = getEditValue(emailEditText);
		String password = getEditValue(passwordEditText);
		String cpassword = getEditValue(cpasswordEditText);

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

		// 验证邮箱
		if (result) {
			if (email != null) {
				if (AppUtil.getStrLen(email) < 5 || email.indexOf("@") < 0) {
					result = false;
					showToast(R.string.email_error);
				}
			} else {
				result = false;
				showToast(R.string.email_hint);
			}
		}

		// 验证密码
		if (result) {
			if (password != null) {
				if (AppUtil.getStrLen(password) < 6
						|| AppUtil.getStrLen(password) > 50) {
					result = false;
					showToast(R.string.password_error);
				}
			} else {
				result = false;
				showToast(R.string.password_hint);
			}
		}
		if (result) {
			if (cpassword != null && !password.equals(cpassword)) {
				showToast(R.string.cpassword_error);
				result = false;
			} else if (cpassword == null) {
				result = false;
				showToast(R.string.cpassword_tips);
			}
		}

		validated = false;

		this.usernameVal = username;
		this.emailVal = email;
		this.passwordVal = password;
		this.cpasswordVal = cpassword;

		return result;
	}

	private void doTaskRegister() {
		String deviceId = AppUtil.getDeviceId(this);

		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("username", this.usernameVal);
		urlParams.put("email", this.emailVal);
		urlParams.put("password", this.passwordVal);
		urlParams.put("cpassword", this.cpasswordVal);
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
				bundle.putString("username",
						this.usernameVal.replaceAll(" ", ""));
				this.showToast(R.string.register_success);
				this.forward(UILogin.class, bundle);
			} else {
				String userId = operation.getString("id");
				String cookie = operation.getString("cookie");

				AppUtil.writeInternalStoragePrivate(this, "ck", cookie);
				AppUtil.writeInternalStoragePrivate(this, "ud", userId);

				this.forward(UIIhome.class);
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}