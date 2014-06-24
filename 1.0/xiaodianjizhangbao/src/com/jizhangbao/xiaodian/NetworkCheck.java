package com.jizhangbao.xiaodian;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheck {
	public static boolean checkNet(Context context) {
		// 获得手机所有连接管理对象（包括对wi-fi等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获得网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已连接
					if (info.getState() == NetworkInfo.State.CONNECTED){
					  return true;
					}
				}else{
					return false;
				}
			}else{
				return false;
			}
		} catch (Exception e) {
		}
		return false;
	}
}