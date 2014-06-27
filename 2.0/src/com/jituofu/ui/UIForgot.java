package com.jituofu.ui;

import java.util.HashMap;

import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UIForgot extends BaseUi implements BaseUiFormBuilder,
		BaseUiBuilder {
	// 验证结果
	public boolean validated = false;

	private TextView titleView, topBarRightView, gotostep2View;
	private Button okBtnView;
	private EditText usernameView, emailView;

	private String username, email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot);

		this.prepare();
		this.onUpdate();
		this.onBind();
	}

	@Override
	public void onBind() {
		// TODO Auto-generated method stub
		this.onCustomBack();

		okBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beforeSubmit();
			}
		});
		
		gotostep2View.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				forward(UIForgot2.class);
			}});
		
		topBarRightView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				forward(UIHelp.class);
			}});
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub

		titleView.setText(R.string.FORGOT_TITLE);
		topBarRightView.setText(R.string.help);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		titleView = (TextView) findViewById(R.id.title);
		topBarRightView = (TextView) findViewById(R.id.topBar2Right);

		usernameView = (EditText) findViewById(R.id.username);
		emailView = (EditText) findViewById(R.id.email);

		okBtnView = (Button) findViewById(R.id.okBtn);
		gotostep2View = (TextView) findViewById(R.id.gotostep2);
	}

	@Override
	public void beforeSubmit() {
		// 防止重复点击
		if (validated) {
			return;
		}

		validated = true;

		if (validation()) {

			doSubmit();
		}
		validated = false;
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub
		AppUtil.showLoadingPopup(this, R.string.COMMON_CLZ);
		doTaskForgot();
	}

	@Override
	public boolean validation() {
		// TODO Auto-generated method stub
		boolean result = false;

		this.username = AppUtil.trimAll(this.getEditValue(this.usernameView));
		this.email = AppUtil.trimAll(this.getEditValue(this.emailView));

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

		return result;
	}
	
	private void doTaskForgot() {

		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("username", this.username);
		urlParams.put("email", this.email);

		try {
			this.doTaskAsync(C.TASK.forgotpw, C.API.host + C.API.forgotpw,
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

			if (!hasId) {
				this.showToast(R.string.FORGOT_ERROR);
			} else {
				//保存获取到的user id
				StorageUtil.writeInternalStoragePrivate(this, C.DIRS.userIdFileName, operation.getString("id"));
				Bundle bundle = new Bundle();
				bundle.putString("username", this.username);
				this.forward(UIForgot2.class);
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}
