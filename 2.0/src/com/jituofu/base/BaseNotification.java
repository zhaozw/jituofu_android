package com.jituofu.base;

import com.jituofu.R;
import com.jituofu.util.AppUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

/***
 * @description 状态栏通知相关
 * @author chenzheng_java
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class BaseNotification {
	private Context context;
	private int id;
	private Class<?> obj;
	private String tickerText, title, content;

	private Notification notification;
	private NotificationCompat.Builder notificationCompatBuilder;
	private NotificationManager manager;
	private PendingIntent pendingIntent;

	public BaseNotification(Context context, int id) {
		this.context = context;
		this.id = id;
	}

	/**
	 * 添加一个notification
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void create(Class<?> obj, String tickerText, String title,
			String content, int icon, int smallIcon) {
		this.obj = obj;
		this.tickerText = tickerText;
		this.content = content;

		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(context, obj);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		pendingIntent = pendingIntent != null ? pendingIntent : PendingIntent
				.getActivity(context, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		// 低版本
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			notification = new Notification();

			notification.icon = icon;
			notification.tickerText = tickerText;
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;

			notification.setLatestEventInfo(context, title, content,
					pendingIntent);
			manager.notify(id, notification);

		} else {
			notificationCompatBuilder = new NotificationCompat.Builder(context);
			notificationCompatBuilder
					.setTicker(tickerText)
					.setLargeIcon(AppUtil.getBitmapFromResources(context, icon))
					.setContentTitle(title).setContentText(content)
					.setContentIntent(pendingIntent).setSmallIcon(smallIcon)
					.setDefaults(Notification.DEFAULT_SOUND)
					.setAutoCancel(true).getNotification();
			manager.notify(id, notificationCompatBuilder.build());
		}
	}

	public void cancel() {
		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(id);
	}

	@SuppressWarnings("deprecation")
	public void setContentText(String content, int defaults, String tickerText) {
		this.content = content;
		this.tickerText = tickerText;
		if (notificationCompatBuilder != null) {
			notificationCompatBuilder.setTicker(tickerText);
			notificationCompatBuilder.setDefaults(defaults);
			notificationCompatBuilder.setContentText(content);
			manager.notify(id, notificationCompatBuilder.build());
		} else if (notification != null) {
			notification.tickerText = tickerText;
			notification.setLatestEventInfo(context, title, content,
					pendingIntent);
			notification.defaults = defaults;
			manager.notify(id, notification);
		}
	}

	@SuppressWarnings("deprecation")
	public void setContentText(String content, int defaults) {
		this.content = content;
		if (notificationCompatBuilder != null) {
			notificationCompatBuilder.setDefaults(defaults);
			notificationCompatBuilder.setContentText(content);
			manager.notify(id, notificationCompatBuilder.build());
		} else if (notification != null) {
			notification.setLatestEventInfo(context, title, content,
					pendingIntent);
			notification.defaults = defaults;
			manager.notify(id, notification);
		}
	}

	public void setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
		// 兼容高端机问题
		if (notificationCompatBuilder != null) {
			notificationCompatBuilder.setContentIntent(pendingIntent);
		}
	}
}
