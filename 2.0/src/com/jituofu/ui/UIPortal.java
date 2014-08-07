package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUpdateService;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.HttpUtil;
import com.jituofu.util.StorageUtil;

public class UIPortal extends BaseUi {
	private String currentVersion;
	private String url, newVersion, logs, date;

	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentVersion = AppUtil.getVersion(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.portal);
	}

	private void noUpdate() {
		AppUtil.fetchUserFromServer(getApplicationContext(), this);
	}

	private void hasUpdate() {
		// 记录一次提醒用户更新，防止重复提醒
		StorageUtil.writeInternalStoragePrivate(this, C.DIRS.tipUpdate, "true");

		String log = Html.fromHtml(logs) + "";

		baseDialogBuilder = new BaseDialog.Builder(this);
		baseDialogBuilder.setMessage(log);
		baseDialogBuilder.setTitle(R.string.SOFTWAREUPDATE_HAS_UPDATE);
		baseDialogBuilder.setPositiveButton(R.string.COMMON_XZ,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// 异步下载更新
						if (AppUtil.isServiceStarted(UIPortal.this,
								"com.jituofu.base.BaseUpdateService")) {
							return;
						}
						Intent updateIntent = new Intent(UIPortal.this,
								BaseUpdateService.class);
						updateIntent.putExtra("title",
								getString(R.string.app_name));
						updateIntent.putExtra("fileName",
								getString(R.string.app_name) + newVersion);
						updateIntent.putExtra("apkUrl", url);
						UIPortal.this.startService(updateIntent);
						// 数据同步
						AppUtil.showLoadingPopup(UIPortal.this,
								R.string.COMMON_DATAASYNC_TXT);
						baseDialog.dismiss();
						AppUtil.fetchUserFromServer(getApplicationContext(),
								UIPortal.this);
					}
				});
		baseDialogBuilder.setNegativeButton(R.string.COMMON_CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						AppUtil.showLoadingPopup(UIPortal.this,
								R.string.COMMON_DATAASYNC_TXT);
						baseDialog.dismiss();
						AppUtil.fetchUserFromServer(getApplicationContext(),
								UIPortal.this);
					}
				});
		baseDialog = baseDialogBuilder.create();
		baseDialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();

		byte[] tipUpdate = StorageUtil.readInternalStoragePrivate(this,
				C.DIRS.tipUpdate);

		String tipUpdateVal = null;

		if (tipUpdate.length > 0 && tipUpdate[0] != 0) {
			try {
				tipUpdateVal = new String(tipUpdate, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * 满足以下条件时，检查更新 1、本地没有已经提示用户更新的记录 2、有网络，并且不是2G网络 3、周1或周5
		 */
		String week = AppUtil.getWeekOfDate();

		// 如果不是周1或周5，请清除本地保存的提醒更新的记录
		if (!week.equals("星期一") && !week.equals("星期五")) {
			StorageUtil.deleteInternalStoragePrivate(this, C.DIRS.tipUpdate);
		}

		if (tipUpdateVal == null
				&& (HttpUtil.getType(this) != 0 && HttpUtil.getType(this) != 2)
				&& (week.equals("星期一") || week.equals("星期五"))) {
			checkUpdate();
		} else {
			AppUtil.fetchUserFromServer(getApplicationContext(), this);
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation;

		// 检查更新的任务
		if (taskId == C.TASK.softwareupdate) {
			operation = message.getOperation();
			if (resultStatus == 100) {
				if (operation.has("update")) {
					JSONObject update = operation.getJSONObject("update");
					if (update.getString("version").equals(currentVersion)) {
						noUpdate();
					} else {
						url = update.getString("url");
						newVersion = update.getString("version");
						date = update.getString("date");
						logs = update.getString("log");
						hasUpdate();
					}
				} else {
					noUpdate();
				}
			}
		} else if (taskId == C.TASK.getuser) {
			Bundle bundle = new Bundle();
			bundle.putBoolean("hideBack", true);

			if (resultStatus == 100) {
				operation = message.getOperation();
				boolean hasId = operation.has("id");
				if (!hasId) {
					forward(UILogin.class, bundle);
					this.finish();
				} else {
					AppUtil.fetchStoreSettingsFromServer(
							getApplicationContext(), this);
				}
			} else {
				forward(UILogin.class, bundle);
				this.finish();
			}
		} else if (taskId == C.TASK.storesettingsget) {
			if (resultStatus == 100) {
				operation = message.getOperation();
				if (operation.has("storeSettings")) {
					String storeSettings = operation.getString("storeSettings");
					StorageUtil.writeInternalStoragePrivate(this,
							C.DIRS.storeSettingsCacheFileName, storeSettings);
				}
				this.forward(UIHome.class);
				this.finish();

			}
		}
	}

	private void checkUpdate() {
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("platform", "1");

		try {
			doTaskAsync(C.TASK.softwareupdate, C.API.host
					+ C.API.softwareupdate, urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
