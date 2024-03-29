package com.jituofu.ui;

import java.util.HashMap;

import com.jituofu.R;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

public class UIUpdatepw extends BaseUiAuth {
	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;
	
	Dialog dialog;
	boolean validated = false;

	private String currentPw, newPw, cnewPw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatepw);

		onUpdateUi();
		onBindUi();
	}

	@Override
	protected void onUpdateUi() {
		super.onUpdateUi();
		
		TextView title = (TextView) this.findViewById(R.id.title);
		Bundle bundle = this.getIntent().getExtras();
		String extraTitle = bundle.getString("title");

		title.setText(extraTitle);
	}

	@Override
	protected void onBindUi() {
		super.onBindUi();
		this.onCustomBack();

		Button updateBtn = (Button) this.findViewById(R.id.updateBtn);
		updateBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validated) {
					return;
				}
				validated = true;
				if (validation()) {
					showPopup(R.string.COMMON_CLZ);
					doTaskUpdate();
				}
			}
		});
	}

	private void doTaskUpdate() {
		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("opassword", this.currentPw);
		urlParams.put("password", this.newPw);
		urlParams.put("cpassword", this.cnewPw);

		try {
			this.doTaskAsync(C.TASK.update, C.API.host + C.API.update,
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
							
							StorageUtil.deleteInternalStoragePrivate(getApplicationContext(), C.DIRS.userCookieFileName);
							StorageUtil.deleteInternalStoragePrivate(getApplicationContext(), C.DIRS.userIdFileName);
							StorageUtil.deleteInternalStoragePrivate(getApplicationContext(), C.DIRS.userInfoFileName);
							
							forward(UILogin.class);
							finish();
						}
					});
			baseDialogBuilder
					.setMessage(message.getMemo(), UIUpdatepw.this);
			baseDialog = baseDialogBuilder.create();
			baseDialog.setCanceledOnTouchOutside(false);
			baseDialog.show();
			
			AppUtil.sendClearActivitiesBr(getApplicationContext());
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
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

		EditText currentPwEditText = (EditText) findViewById(R.id.currentpw);
		EditText newPwEditText = (EditText) findViewById(R.id.newpw);
		EditText cnewPwEditText = (EditText) findViewById(R.id.cnewpw);

		String currentPw = getEditValue(currentPwEditText);
		String newPw = getEditValue(newPwEditText);
		String cnewPw = getEditValue(cnewPwEditText);

		if (currentPw == null) {
			this.showToast(R.string.current_password_hint);
		} else if (AppUtil.getStrLen(currentPw) < 6
				|| AppUtil.getStrLen(currentPw) > 50) {
			showToast(R.string.COMMON_PASSWORD_ERROR);
		} else {
			result = true;
		}

		if (result) {
			if (newPw == null) {
				this.showToast(R.string.new_password_hint);
				result = false;
			} else if (AppUtil.getStrLen(newPw) < 6
					|| AppUtil.getStrLen(newPw) > 50) {
				showToast(R.string.new_COMMON_PASSWORD_ERROR);
				result = false;
			}
		}

		if (result) {
			if (cnewPw == null) {
				this.showToast(R.string.cnew_password_hint);
				result = false;
			} else if (!newPw.equals(cnewPw)) {
				showToast(R.string.REGISTER_CPASSWORD_INVALID);
				result = false;
			}
		}

		validated = false;

		this.currentPw = currentPw;
		this.newPw = newPw;
		this.cnewPw = cnewPw;

		return result;
	}
}
