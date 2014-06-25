package com.jituofu.base;

import com.jituofu.util.AppUtil;
import com.jituofu.R;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class BaseHandler extends Handler {
	protected BaseUi ui;

	public BaseHandler(BaseUi ui) {
		this.ui = ui;
	}

	public BaseHandler(Looper looper) {
		super(looper);
	}

	@Override
	public void handleMessage(Message msg) {
		try {
			int taskId = msg.getData().getInt("task");
			String result;

			switch (msg.what) {
			case BaseTask.TASK_COMPLETE:
				result = msg.getData().getString("data");
				if (result != null) {
					ui.onTaskComplete(taskId, AppUtil.getMessage(result));
				} else {
					Log.e(ui.getResources().getString(R.string.app_name),
							"handleMessage没有获取到data");
				}
				break;
			case BaseTask.NETWORK_ERROR:
				ui.onNetworkError(taskId);
				break;
			case BaseTask.SERVER_ERROR:
				ui.onServerException(taskId);
				break;
			case BaseTask.NETWORKTIMEOUT:
				ui.onNetworkTimeout(taskId);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ui.showToast(e.getMessage());
		}
	}
}