package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

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

	private BaseDialog.Builder baseDialogBuilder2;
	private BaseDialog baseDialog2;

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

		queryRank();

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
			// 保证评论弹层和升级弹层只出现一次
			if (baseDialog2 != null) {
				baseDialog2.show();
			} else {
				checkUpdate();
			}
		} else {
			// 保证评论弹层和升级弹层只出现一次
			if (baseDialog2 != null) {
				baseDialog2.show();
			} else {
				AppUtil.fetchUserFromServer(getApplicationContext(), this);
			}
		}
	}

	/**
	 * 评价提示
	 */
	private void queryRank() {
		byte[] tipPl = StorageUtil.readInternalStoragePrivate(this,
				C.DIRS.tipPL);

		String tipPlVal = null;

		if (tipPl.length > 0 && tipPl[0] != 0) {
			try {
				tipPlVal = new String(tipPl, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.CHINA);

		try {
			cal.setTime(simpleDateFormat.parse(AppUtil.getCurrentDateTime()));
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int d = cal.get(Calendar.DAY_OF_MONTH);

		// 每月5号，15号，25号，提示用户给评价
		if (d % 5 == 0) {
			if (tipPlVal == null) {
				baseDialogBuilder2 = new BaseDialog.Builder(this);

				LinearLayout view = (LinearLayout) LinearLayout.inflate(this,
						R.layout.page_pinglun, null);
				baseDialogBuilder2.setContentView(view, UIPortal.this);
				baseDialogBuilder2.setPositiveButton(R.string.PINGLUN_YES,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface di, int which) {
								// TODO Auto-generated method stub
								baseDialog2.dismiss();
								StorageUtil.writeInternalStoragePrivate(
										UIPortal.this, C.DIRS.tipPL, "true");
								AppUtil.gotoMarket(UIPortal.this);
							}
						});
				baseDialogBuilder2.setNegativeButton(R.string.PINGLUN_NO,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface di, int which) {
								baseDialog2.dismiss();
								StorageUtil.writeInternalStoragePrivate(
										UIPortal.this, C.DIRS.tipPL, "true");
								AppUtil.showLoadingPopup(UIPortal.this,
										R.string.COMMON_DATAASYNC_TXT);

								AppUtil.fetchUserFromServer(
										getApplicationContext(), UIPortal.this);
							}
						});
				baseDialog2 = baseDialogBuilder2.create();
				baseDialog2.setCanceledOnTouchOutside(false);
			}
		} else {
			StorageUtil.deleteInternalStoragePrivate(this, C.DIRS.tipPL);
			baseDialog2 = null;
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
				//更新最后登录时间
				AppUtil.updateLSID(this);
				
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
		}else if(taskId == C.TASK.updatelsid){
			
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
