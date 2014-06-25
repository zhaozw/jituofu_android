package com.jituofu.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class BaseMessage {
	private String resultSrc;
	private JSONObject pub;
	private JSONObject ope;

	public BaseMessage() {
	}

	/**
	 * 获取服务端返回的源数据
	 * 
	 * @return
	 */
	public String getResult() {
		return this.resultSrc;
	}

	/**
	 * 获取公共数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPublic() throws Exception {
		if (pub == null) {
			throw new Exception("没有public数据");
		}

		return pub;
	}

	/**
	 * 获取业务数据
	 * 
	 * @return
	 * @throws Exception
	 */
	public JSONObject getOperation() throws Exception {
		if (ope == null) {
			throw new Exception("没有operation数据");
		}

		return ope;
	}

	/**
	 * 设置result.把服务端返回的result转换为JSONObject
	 * 
	 * @param result
	 * @throws Exception
	 */
	public void setResult(String result) throws Exception {
		this.resultSrc = result;
		if (result.length() > 0) {
			JSONObject jsonObject = null;
			jsonObject = new JSONObject(result);
			this.pub = (JSONObject) jsonObject.get("public");
			this.ope = (JSONObject) jsonObject.get("operation");
		} else {
			throw new Exception("没有result数据");
		}
	}

	/**
	 * 获取resultStatus
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getResultStatus() throws Exception {
		JSONObject pub = this.getPublic();
		return pub.getInt("resultStatus");
	}

	/**
	 * 获取memo
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getMemo() throws Exception {
		JSONObject pub = this.getPublic();
		return pub.getString("memo");
	}

	/**
	 * 获取所有业务数据相关的错误消息
	 * 
	 * @return Array
	 * @throws Exception
	 */
	public JSONArray getOperationErrorsMessage() throws Exception {
		JSONObject operation = this.getOperation();

		JSONArray errorsArray = this.getErrors(operation);

		return errorsArray;
	}

	/**
	 * 获取所有业务数据相关的第一条错误消息
	 * @return
	 * @throws Exception
	 */
	public String getFirstOperationErrorMessage() throws Exception {
		JSONArray errorsArray = this.getOperationErrorsMessage();
		String msg;
		if (errorsArray.length() <= 0) {
			msg = this.getPublic().getString("memo");
		}else{
			msg = errorsArray.getString(0);
		}
		return msg;
	}

	private JSONArray getErrors(JSONObject data) throws JSONException {
		JSONArray errorsArray = new JSONArray();
		Iterator<String> it = data.keys();

		if (it != null) {
			while (it.hasNext()) {
				String key = it.next();
				JSONArray errorArray = data.optJSONArray(key);
				JSONObject errorObject = data.optJSONObject(key);

				if (errorArray != null) {
					for (int i = 0; i < errorArray.length(); i++) {
						errorsArray.put(errorArray.get(i));
					}

				}
				if (errorObject != null) {
					errorsArray.put(this.getErrors(errorObject));
				}
			}
		}

		return errorsArray;
	}
}