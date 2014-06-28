package com.jituofu.base;

import com.jituofu.R;
import com.jituofu.ui.UIFeedback;
import com.jituofu.util.AppUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

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
	
	public BaseNotification(Context context, int id){
		this.context = context;
		this.id = id;
	}

	/**
	 * 添加一个notification
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void create(Class<?> obj, String tickerText, String title, String content) {
		this.obj = obj;
		this.tickerText = tickerText;
		this.content = content;
		
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(context, obj);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 低版本
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			Notification notification = new Notification();

			notification.icon = R.drawable.ic_launcher;
			notification.tickerText = tickerText;
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.audioStreamType = android.media.AudioManager.ADJUST_LOWER;

			notification.setLatestEventInfo(context, title, content,
					pendingIntent);
			manager.notify(id, notification);

		} else {
			NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(
					context);
			notificationCompatBuilder
			        .setTicker(tickerText)
					.setLargeIcon(
							AppUtil.getBitmapFromResources(context,
									R.drawable.ic_launcher))
					.setContentTitle(title).setContentText(content)
					.setContentIntent(pendingIntent)
					.setSmallIcon(R.drawable.sucess_small)
					.setDefaults(Notification.DEFAULT_SOUND)
					.setAutoCancel(true).getNotification();
			manager.notify(id, notificationCompatBuilder.build());
		}
	}

	public void cancel() {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(id);
	}

}
