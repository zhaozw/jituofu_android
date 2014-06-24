package cn.com.jizhangbao.base;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.jizhangbao.R;
import cn.com.jizhangbao.ui.UIHome;
import cn.com.jizhangbao.ui.UILogin;
import cn.com.jizhangbao.util.AppUtil;
import cn.com.jizhangbao.util.HttpUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BaseUi extends Activity {
	private Dialog popupDialog;
	private AlertDialog.Builder confirmDialog;

	protected BaseTaskPool taskPool;
	protected BaseHandler handler;

	// 一个基础的广播对象
	private BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			onBrReceive(bundle.getString("type"));
		}
	};

	/**
	 * 广播对象的接收器
	 * 
	 * @param type
	 *            广播类型
	 */
	protected void onBrReceive(String type) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(this.getString(R.string.app_name), "onCreate");

		super.onCreate(savedInstanceState);

		this.taskPool = new BaseTaskPool(this);
		this.handler = new BaseHandler(this);

		// 注册一个监听清除Activities堆栈的广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("cn.com.jizhangbao.util.ClearActivitiesBroadcast");
		BaseUi.this.registerReceiver(br, filter);
	}

	@Override
	protected void onResume() {
		Log.i(this.getString(R.string.app_name), "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(this.getString(R.string.app_name), "onPause");
		super.onPause();
	}

	@Override
	public void onStart() {
		Log.i(this.getString(R.string.app_name), "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.i(this.getString(R.string.app_name), "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(this.getString(R.string.app_name), "onDestroy");
		
		// 取消监听广播
		BaseUi.this.unregisterReceiver(br);
		super.onDestroy();
	}

	protected void bindUI() {
	}

	protected void updateUI() {
	}

	/**
	 * 跳转activity
	 * 
	 * @param obj
	 *            Activity class
	 */
	public void forward(Class<?> obj) {
		Intent intent = new Intent();
		intent.setClass(this, obj);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);
	}

	/**
	 * 绑定数据 跳转activity
	 * 
	 * @param obj
	 *            Activity class
	 * @param params
	 *            数据
	 */
	public void forward(Class<?> obj, Bundle params) {
		Intent intent = new Intent();
		intent.setClass(this, obj);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtras(params);
		this.startActivity(intent);
	}

	/**
	 * 生成弹出层实例
	 * 
	 * @param ctx
	 *            应用上下文
	 * @return Dialog实例
	 */
	public Dialog getPopupDialog(Context ctx) {
		if (this.popupDialog != null) {
			return this.popupDialog;
		}

		Dialog popupDialog = new Dialog(ctx, R.style.popupDialog);

		this.popupDialog = popupDialog;

		return popupDialog;
	}

	/**
	 * 设置弹层内容
	 * 
	 * @param layoutResId
	 *            布局资源
	 */
	public void setPopupView(int layoutResId) {
		if (this.popupDialog != null) {
			this.popupDialog.setContentView(layoutResId);
		}
	}

	/**
	 * 显示一个confirm dialog
	 * 
	 * @param titleId
	 * @param mesId
	 * @param firstListener
	 * @param secondListener
	 */
	public void showConfirmDialog(int titleId, int mesId,
			DialogInterface.OnClickListener firstListener,
			DialogInterface.OnClickListener secondListener) {
		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(this,
					android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(this,
					android.R.style.Theme_Light_NoTitleBar);
		}
		confirmDialog = new AlertDialog.Builder(themedContext);
		confirmDialog.setTitle(titleId);
		confirmDialog.setMessage(mesId);

		confirmDialog.setPositiveButton("确认", firstListener);
		confirmDialog.setNegativeButton("取消", secondListener);
		confirmDialog.create().show();
	}

	/**
	 * 显示一个不带title的confirm dialog
	 * 
	 * @param mesId
	 * @param firstListener
	 * @param secondListener
	 */
	@SuppressLint("NewApi")
	public void showConfirmDialog(int mesId,
			DialogInterface.OnClickListener firstListener,
			DialogInterface.OnClickListener secondListener) {
		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(this,
					android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(this,
					android.R.style.Theme_Light_NoTitleBar);
		}
		confirmDialog = new AlertDialog.Builder(themedContext);
		confirmDialog.setMessage(mesId);

		confirmDialog.setPositiveButton("确认", firstListener);
		confirmDialog.setNegativeButton("取消", secondListener);
		confirmDialog.create().show();
	}

	/**
	 * 显示弹出层
	 * 
	 * @param setCanceledOnTouchOutside
	 *            是否允许点击外部区域关闭弹层
	 */
	public void showPopupDialog(boolean setCanceledOnTouchOutside) {
		if (this.popupDialog != null) {
			this.popupDialog.show();
			this.popupDialog
					.setCanceledOnTouchOutside(setCanceledOnTouchOutside);
		}
	}

	/**
	 * 关闭弹出层
	 */
	public void closePopupDialog() {
		if (this.popupDialog != null) {
			this.popupDialog.dismiss();
			this.popupDialog = null;
		}
	}

	/**
	 * 全局返回按钮逻辑
	 */
	public void globalBackLogic() {
		Boolean hideBack = null;
		Bundle bundle = this.getIntent().getExtras();

		if (bundle != null) {
			hideBack = bundle.getBoolean("hideBack");
		}

		LinearLayout back = (LinearLayout) findViewById(R.id.back);

		// 是否需要隐藏返回按钮
		if (hideBack != null && hideBack == true) {
			back.setVisibility(View.GONE);
		} else if (back != null) {
			back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	/**
	 * 屏幕正居中显示toast
	 * 
	 * @param text
	 */
	public void showToast(int text) {
		Toast toast = Toast.makeText(getApplicationContext(), text,
				Toast.LENGTH_SHORT);

		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public void showToast(String text) {
		Toast toast = Toast.makeText(getApplicationContext(), text,
				Toast.LENGTH_SHORT);

		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * 获取文本输入框的trim后的值
	 * 
	 * @param obj
	 * @return
	 */
	public String getEditValue(EditText obj) {
		String value = obj.getText().toString();

		if ("".equals(value)) {
			return null;
		}

		if (value != null) {
			return value.trim();
		}

		return null;
	}

	/**
	 * 发送异步post请求
	 * 
	 * @param taskId
	 * @param taskUrl
	 * @param data
	 * @param cookie
	 * @param userId
	 * @throws UnsupportedEncodingException
	 */
	public void doTaskAsync(int taskId, String taskUrl,
			HashMap<String, String> data) throws UnsupportedEncodingException {
		if (HttpUtil.getType(this) == 0) {
			this.showToast(C.ERROR.networkNone);
		} else {

			JSONObject urlParams = new JSONObject();

			long timestamp = AppUtil.getCurrentTime();

			HashMap<String, JSONObject> requestData = new HashMap<String, JSONObject>();
			HashMap<String, String> publicData = new HashMap<String, String>();

			JSONObject operationData = new JSONObject(data);

			String operationDataStr = operationData.toString();
			String signSrc = operationDataStr + timestamp + C.JZB.localKey;

			// 构建public数据
			publicData.put("productVersion", AppUtil.getVersion(this));
			publicData.put("productId", "android");
			publicData.put("channelId", C.JZB.channelId);
			publicData.put("network", HttpUtil.getType(this) + "");
			publicData.put("display", AppUtil.getDisplay(this));
			publicData.put("time", timestamp + "");
			publicData.put("sign", AppUtil.get32MD5(signSrc));
			publicData.put("pushToken", "");

			// 从本地获取userId和cookie
			byte[] cookie = AppUtil.readInternalStoragePrivate(this, "ck");
			byte[] userId = AppUtil.readInternalStoragePrivate(this, "ud");
			String cookieVal = "";
			String userIdVal = "";
			if (cookie.length > 0 && cookie[0] != 0) {
				cookieVal = new String(cookie, "UTF-8");
			}
			if (userId.length > 0 && userId[0] != 0) {
				userIdVal = new String(userId, "UTF-8");
			}
			publicData.put("cookie", cookieVal);
			publicData.put("userId", userIdVal);
			try {
				urlParams.put("public", new JSONObject(publicData));
				urlParams.put("operation", operationData);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// 构建requestData数据
			requestData.put("requestData", urlParams);

			taskPool.addTask(taskId, taskUrl, requestData, new BaseTask() {
				@Override
				public void onComplete(String httpResult) {
					sendMessage(BaseTask.TASK_COMPLETE, this.getId(),
							httpResult);
				}

				@Override
				public void onError(String error) {
					super.onError(error);
					sendMessage(BaseTask.NETWORK_ERROR, this.getId(), null);
				}

				@Override
				public void onServerError() {
					super.onServerError();
					sendMessage(BaseTask.SERVER_ERROR, this.getId(), null);
				}

				@Override
				public void onStop() throws Exception {
					super.onStop();
					closePopupDialog();
				}

				@Override
				public void onNetworkTimeout() {
					super.onNetworkTimeout();
					sendMessage(BaseTask.NETWORKTIMEOUT, this.getId(), null);
				}
			}, 0);
		}
	}

	public void onNetworkError(int taskId) {
		this.showToast(C.ERROR.networkException + taskId);
	}

	public void onNetworkTimeout(int taskId) {
		this.showToast(C.ERROR.networkTimeout + taskId);
	}

	public void onServerException(int taskId) {
		this.showToast(C.ERROR.serverException + taskId);
	}

	/**
	 * 发送异步消息
	 * 
	 * @param what
	 * @param taskId
	 * @param data
	 */
	public void sendMessage(int what, int taskId, String data) {
		Bundle b = new Bundle();

		b.putInt("task", taskId);
		b.putString("data", data);

		Message m = new Message();
		m.what = what;
		m.setData(b);

		handler.sendMessage(m);
	}

	/**
	 * 获取上下文
	 * 
	 * @return
	 */
	public Context getContext() {
		return this;
	}

	/**
	 * 异步请求任务完成后的回调方法
	 * 
	 * @param taskId
	 * @param message
	 * @throws Exception
	 */
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		this.closePopupDialog();

		// 如果获取到用户信息就保存到本地
		if (taskId == C.TASK.getuser) {
			int resultStatus = message.getResultStatus();

			if (resultStatus == 100) {
				JSONObject operation = message.getOperation();
				boolean hasId = operation.has("id");

				if (hasId) {
					String userInfo = operation.toString();

					AppUtil.writeInternalStoragePrivate(this, "usi", userInfo);
				}
			}
		}
	}
}
