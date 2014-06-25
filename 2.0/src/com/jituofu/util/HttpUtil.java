package com.jituofu.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpUtil {
	static public int NONE_INT = 0;
	static public int WIFI_INT = 1;
	static public int G4_INT = 4;
	static public int G3_INT = 3;
	static public int G2_INT = 2;

	/**
	 * 获取当前网络类型
	 * @param ctx
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	static public int getType(Context ctx) {
		ConnectivityManager conn = null;
		try {
			conn = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 没有网络服务
		if (conn == null) {
			return NONE_INT;
		}
		// 没有网络服务
		NetworkInfo info = conn.getActiveNetworkInfo();
		if (info == null) {
			return NONE_INT;
		}
		boolean available = info.isAvailable();
		// 网络不可用
		if (!available) {
			return NONE_INT;
		}
		String type = info.getTypeName().toLowerCase();
		if (type.equals("wifi")) {
			return WIFI_INT;
		} else if (type.equals("4g")) {
			return G4_INT;
		} else if (type.equals("3g")) {
			return G3_INT;
		} else if (type.equals("2g")) {
			return G2_INT;
		}

		return G2_INT;
	}
}