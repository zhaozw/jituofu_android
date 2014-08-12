package com.jituofu.ui;

import java.util.HashMap;

import com.jituofu.R;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UIForgot2 extends BaseUi implements BaseUiFormBuilder,
		BaseUiBuilder {
	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;

	// 验证结果
	public boolean validated = false;

	private TextView titleView, topBarRightView;
	private Button okBtnView;
	private EditText tokenView, passwordView, cpasswordView;

	private String token, password, cpassword;
	private String step = "2";

	@Override
	protected void onBrReceive(String type){
		if(type.equals("RegisterAndLoginSuccess")){
			this.finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot2);
		
		this.prepare();
		this.onUpdate();
		this.onBind();
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub
		AppUtil.showLoadingPopup(this, R.string.COMMON_CLZ);
		doTaskForgot();
	}

	@Override
	public void beforeSubmit() {
		// TODO Auto-generated method stub
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
	public boolean validation() {
		// TODO Auto-generated method stub
		boolean result = false;

		this.token = AppUtil.trimAll(this.getEditValue(this.tokenView));
		this.password = AppUtil.trimAll(this.getEditValue(this.passwordView));
		this.cpassword = AppUtil.trimAll(this.getEditValue(this.cpasswordView));

		if (this.token != null) {
			if (AppUtil.getStrLen(this.token) < 2) {
				showToast(R.string.FORGOT_TOKEN_INVALID);
			} else {
				result = true;
			}
		} else {
			showToast(R.string.FORGOT_TOKEN_SPECIFY);
		}

		if (result) {
			if (this.password != null) {
				if (AppUtil.getStrLen(this.password) < C.PASSWORDLENGTH.MIN
						|| AppUtil.getStrLen(this.password) > C.PASSWORDLENGTH.MAX) {
					result = false;
					showToast(R.string.COMMON_PASSWORD_ERROR);
				}
			} else {
				result = false;
				showToast(R.string.LOGIN_PASSWORD_HINT);
			}
		}

		if (result) {
			if (this.cpassword != null && !this.cpassword.equals(this.password)) {
				showToast(R.string.REGISTER_CPASSWORD_INVALID);
				result = false;
			} else if (this.cpassword == null) {
				result = false;
				showToast(R.string.REGISTER_CPASSWORD_TIPS);
			}
		}

		return result;
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

		topBarRightView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				forward(UIHelp.class);
			}
		});
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		titleView.setText(R.string.FORGOT_GOTOSTEP2);
		topBarRightView.setText(R.string.help);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		titleView = (TextView) findViewById(R.id.title);
		topBarRightView = (TextView) findViewById(R.id.topBar2Right);

		tokenView = (EditText) findViewById(R.id.token);
		passwordView = (EditText) findViewById(R.id.password);
		cpasswordView = (EditText) findViewById(R.id.cpassword);

		okBtnView = (Button) findViewById(R.id.okBtn);
	}

	private void doTaskForgot() {

		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("token", this.token);
		urlParams.put("password", this.password);
		urlParams.put("cpassword", this.cpassword);
		urlParams.put("step", this.step);

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
			baseDialogBuilder = new BaseDialog.Builder(this);
			baseDialogBuilder.setNegativeButton(R.string.COMMON_IKNOW,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface di, int which) {
							// TODO Auto-generated method stub
							baseDialog.dismiss();
							forward(UILogin.class);
						}
					});
			baseDialogBuilder
					.setMessage(message.getMemo());
			baseDialog = baseDialogBuilder.create();
			baseDialog.setCanceledOnTouchOutside(false);
			baseDialog.show();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}
