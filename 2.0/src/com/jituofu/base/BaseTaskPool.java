package com.jituofu.base;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import com.jituofu.ui.UIFeedback;
import com.jituofu.util.AppClientUtil;
import com.jituofu.util.AppUtil;

import android.content.Context;
import android.os.Looper;

public class BaseTaskPool {
	static private ExecutorService taskPool;

	private Context context;

	public BaseTaskPool(BaseUi ui) {
		this.context = ui.getContext();
		taskPool = Executors.newCachedThreadPool();
	}

	/**
	 * 增加一个POST带参数的任务
	 * 
	 * @param taskId
	 * @param taskUrl
	 * @param data
	 * @param baseTask
	 * @param delayTime
	 */
	public void addTask(int taskId, String taskUrl,
			HashMap<String, JSONObject> data, BaseTask baseTask, int delayTime) {
		baseTask.setId(taskId);
		try {
			taskPool.execute(new TaskThread(context, taskUrl, data, baseTask,
					delayTime));
		} catch (Exception e) {
			taskPool.shutdown();
		}
	}

	/**
	 * 添加一个上传文件的任务
	 * 
	 * @param taskId
	 * @param taskUrl
	 * @param filesPath
	 * @param data
	 * @param baseTask
	 * @param delayTime
	 */
	public void addTask(int taskId, String taskUrl,
			HashMap<String, JSONObject> data, ArrayList<String> filesPath,
			BaseTask baseTask, int delayTime) {
		baseTask.setId(taskId);
		try {
			taskPool.execute(new TaskThread(context, taskUrl, data, filesPath,
					baseTask, delayTime));
		} catch (Exception e) {
			taskPool.shutdown();
		}
	}

	private class TaskThread implements Runnable {
		private String url;
		private HashMap<String, JSONObject> data;
		private BaseTask baseTask;
		private int delayTime = 0;
		private ArrayList<String> files;
		private Context ctx;

		public TaskThread(Context context, String taskUrl,
				HashMap<String, JSONObject> data, BaseTask baseTask,
				int delayTime) {
			this.url = taskUrl;
			this.data = data;
			this.baseTask = baseTask;
			this.delayTime = delayTime;
			this.ctx = context;
		}

		public TaskThread(Context context, String taskUrl,
				HashMap<String, JSONObject> data, ArrayList<String> filesPath,
				BaseTask baseTask, int delayTime) {
			this.url = taskUrl;
			this.data = data;
			this.baseTask = baseTask;
			this.delayTime = delayTime;
			this.files = filesPath;
		}

		@Override
		public void run() {
			Looper.prepare();
			try {
				baseTask.onStart();
				String httpResult = null;
				// 线程等待
				if (delayTime > 0) {
					Thread.sleep(delayTime);
				}
				try {
					AppClientUtil client = new AppClientUtil(url);
					if (files == null) {
						httpResult = client.post(data);
					} else {// 上传文件
						httpResult = client.post(data, files);
					}
					if (httpResult != null) {
						if (files != null) {
							baseTask.onComplete(httpResult, files);
						} else {
							baseTask.onComplete(httpResult);
						}

					} else {
						baseTask.onServerError();
					}
				} catch (ConnectTimeoutException e) {
					baseTask.onNetworkTimeout();
				} catch (SocketTimeoutException e) {
					baseTask.onNetworkTimeout();
				} catch (Exception e) {
					baseTask.onError(e.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					baseTask.onStop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			Looper.loop();
		}

	}
}