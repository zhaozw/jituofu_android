package com.jituofu.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jituofu.R;
import com.jituofu.ui.UIUpdate;
import com.jituofu.util.AppUtil;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class BaseUpdateService extends Service {
	private String title, fileName, apkUrl;

	// 下载包存储目录
	private String apksDirPath;
	private File updateFile = null;

	// 消息通知
	private int notificationId = 0;
	private BaseNotification baseNoti;
	private String notificationTickerText;

	// 下载结果
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;

	private PendingIntent updatePendingIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		baseNoti = new BaseNotification(this, this.notificationId);
		super.onCreate();
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		title = intent.getStringExtra("title");
		fileName = intent.getStringExtra("fileName");
		apkUrl = intent.getStringExtra("apkUrl");

		apksDirPath = AppUtil.getExternalStorageDirectory() + C.DIRS.rootdir
				+ C.DIRS.apksDirName;
		if (AppUtil.mkdir(AppUtil.getExternalStorageDirectory()
				+ C.DIRS.rootdir)) {
			if (AppUtil.mkdir(apksDirPath)) {
				updateFile = new File(apksDirPath + "/" + fileName + ".apk");
			}
		}

		notificationTickerText = "正在下载更新...";
		baseNoti.create(UIUpdate.class, notificationTickerText, title, "0%",
				R.drawable.ic_launcher, R.drawable.ic_launcher);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程

		return super.onStartCommand(intent, flags, startId);
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String tip = "";
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				tip = "下载完成，点击安装更新";
				// 点击安装PendingIntent
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");
				updatePendingIntent = PendingIntent.getActivity(
						BaseUpdateService.this, 0, installIntent, 0);

				baseNoti.setPendingIntent(updatePendingIntent);
				baseNoti.setContentText(tip,
						Notification.DEFAULT_SOUND, tip);
				break;
			case DOWNLOAD_FAIL:
				tip = "下载失败";
				baseNoti.setContentText(tip, Notification.DEFAULT_SOUND, tip);
				break;
			default:
			}
		}
	};

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			try {
				downloadUpdateFile(apkUrl, updateFile);
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				updateHandler.sendMessage(message);
			}
		}
	}

	public long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(30000);
			httpConnection.setReadTimeout(30000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("404");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;

				int getedTotalSize = ((int) totalSize * 100 / updateTotalSize);
				String dowloadTip = "已下载 " + getedTotalSize + "%";
				baseNoti.setContentText(dowloadTip, 0);
			}

			if (totalSize >= updateTotalSize) {
				Message message = updateHandler.obtainMessage();
				message.what = DOWNLOAD_COMPLETE;

				if (httpConnection != null) {
					httpConnection.disconnect();
				}
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
				updateHandler.sendMessage(message);
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

}
