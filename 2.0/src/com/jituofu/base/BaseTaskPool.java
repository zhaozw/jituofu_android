package com.jituofu.base;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import com.jituofu.util.AppClient;

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

	private class TaskThread implements Runnable {
		private Context context;
		private String url;
		private HashMap<String, JSONObject> data;
		private BaseTask baseTask;
		private int delayTime = 0;

		public TaskThread(Context context, String taskUrl,
				HashMap<String, JSONObject> data, BaseTask baseTask, int delayTime) {
			this.context = context;
			this.url = taskUrl;
			this.data = data;
			this.baseTask = baseTask;
			this.delayTime = delayTime;
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
					if (url != null) {
						AppClient client = new AppClient(url);
						if (data != null) {
							httpResult = client.post(data);
						}
					}
					if (httpResult != null) {
						baseTask.onComplete(httpResult);
					} else {
						baseTask.onServerError();
					}
				}catch(ConnectTimeoutException e){
					baseTask.onNetworkTimeout();
				}catch (SocketTimeoutException e){
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