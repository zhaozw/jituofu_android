package cn.com.jizhangbao.base;

import cn.com.jizhangbao.util.AppUtil;
import cn.com.jizhangbao.R;
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
			int taskId;
			String result;

			switch (msg.what) {
			case BaseTask.TASK_COMPLETE:
				taskId = msg.getData().getInt("task");
				result = msg.getData().getString("data");
				if (result != null) {
					ui.onTaskComplete(taskId, AppUtil.getMessage(result));
				} else {
					Log.e(ui.getResources().getString(R.string.app_name),
							"handleMessage没有获取到data");
				}
				break;
			case BaseTask.NETWORK_ERROR:
				taskId = msg.getData().getInt("task");
				ui.onNetworkError(taskId);
				break;
			case BaseTask.SERVER_ERROR:
				taskId = msg.getData().getInt("task");
				ui.onServerException(taskId);
				break;
			case BaseTask.NETWORKTIMEOUT:
				taskId = msg.getData().getInt("task");
				ui.onNetworkTimeout(taskId);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ui.showToast(e.getMessage());
		}
	}
}