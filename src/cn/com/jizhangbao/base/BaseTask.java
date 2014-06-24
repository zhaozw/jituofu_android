package cn.com.jizhangbao.base;

import android.util.Log;

public class BaseTask {

	public static final int TASK_COMPLETE = 0;
	public static final int NETWORK_ERROR = 1;
	public static final int SHOW_LOADBAR = 2;
	public static final int HIDE_LOADBAR = 3;
	public static final int SHOW_TOAST = 4;
	public static final int LOAD_IMAGE = 5;
	public static final int SERVER_ERROR = 6;
	public static final int NETWORKTIMEOUT = 7;

	private int id = 0;
	private String name = "";

	public BaseTask() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void onStart() {
		 Log.w("BaseTask:onStart", "onStart");
	}

	public void onComplete() {
		 Log.w("BaseTask:onComplete", "onComplete");
	}

	public void onComplete(String httpResult) {
		 Log.w("BaseTask:onComplete", httpResult);
	}

	public void onError(String error) {
		 Log.e("BaseTask:onError", error);
	}

	public void onStop() throws Exception {
		 Log.w("BaseTask:onStop", "onStop");
	}

	public void onServerError(){
		Log.e("BaseTask:onServerError", "onServerError");
	}
	
	public void onNetworkTimeout(){
		Log.e("BaseTask:onNetworkTimeout", C.ERROR.networkTimeout);
	}
}