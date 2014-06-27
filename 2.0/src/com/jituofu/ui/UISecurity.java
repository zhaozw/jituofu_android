package com.jituofu.ui;

import java.util.HashMap;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

public class UISecurity extends BaseUiAuth {
	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;

	private boolean isLogout;// 是否已经退出了

	Dialog dialog = null;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isLogout) {
				logouted();
			}

		}

		return false;

	}

	@Override
	protected void onBrReceive(String type) {
		if (type.equals("ClearActivitiesBroadcast")) {
			this.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security);

		this.onCustomBack();
		onUpdateUi();
		onBindUi();
	}

	@Override
	protected void onBindUi() {
		super.onBindUi();

		Button submitLogoutBtn = (Button) this.findViewById(R.id.submitLogout);
		LinearLayout updatepwBtn = (LinearLayout) this
				.findViewById(R.id.updatepw);

		updatepwBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView title = (TextView) v.findViewById(R.id.txt);
				Bundle bundle = new Bundle();
				bundle.putString("title", title.getText() + "");
				forward(UIUpdatepw.class, bundle);
			}
		});

		submitLogoutBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showConfirmDialog(R.string.securitysettings,
						R.string.areyousurelogout,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								logout();
							}

						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}

						});

			}
		});
	}

	private void logout() {
		AppUtil.showLoadingPopup(this, R.string.logoutting);

		String deviceId = AppUtil.getDeviceId(this);
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("clientId", deviceId);
		try {
			doTaskAsync(C.TASK.logout, C.API.host + C.API.logout, urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		int resultStatus = message.getResultStatus();
		if (resultStatus == 100 || resultStatus == 300) {
			isLogout = true;

			baseDialogBuilder = new BaseDialog.Builder(this);
			baseDialogBuilder.setNegativeButton(R.string.COMMON_IKNOW,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface di, int which) {
							// TODO Auto-generated method stub
							logouted();
						}
					});
			baseDialogBuilder.setMessage(message.getMemo());
			baseDialog = baseDialogBuilder.create();
			baseDialog.setCanceledOnTouchOutside(false);
			baseDialog.show();
		} else {
			showToast(message.getMemo());
		}
	}

	private void logouted() {
		baseDialog.dismiss();

		StorageUtil.deleteInternalStoragePrivate(getApplicationContext(),
				C.DIRS.userCookieFileName);
		StorageUtil.deleteInternalStoragePrivate(getApplicationContext(),
				C.DIRS.userIdFileName);
		StorageUtil.deleteInternalStoragePrivate(getApplicationContext(),
				C.DIRS.userInfoFileName);

		AppUtil.sendClearActivitiesBr(getApplicationContext());
		forward(UILogin.class);
		finish();
	}

	@Override
	protected void onUpdateUi() {
		super.onUpdateUi();

		LinearLayout updatepw = (LinearLayout) this.findViewById(R.id.updatepw);
		LinearLayout border = (LinearLayout) updatepw.findViewById(R.id.border);
		TextView title = (TextView) this.findViewById(R.id.title);
		TextView updatepwTxt = (TextView) updatepw.findViewById(R.id.txt);

		updatepwTxt.setText(R.string.updatepw);
		border.setVisibility(View.INVISIBLE);
		title.setText(R.string.securitysettings);
	}

}