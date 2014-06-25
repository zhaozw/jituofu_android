package com.jituofu.base;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import com.jituofu.util.AppUtil;

public class BaseAuth{
	/**
	 * 当前用户是否已登录
	 * @param ctx
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static boolean isLogin(Context ctx) throws UnsupportedEncodingException{
		byte[] cookie = AppUtil.readInternalStoragePrivate(ctx, "ck");
		byte[] userId = AppUtil.readInternalStoragePrivate(ctx, "ud");
		
		String cookieVal = null;
		String userIdVal = null;
		
		if (cookie.length > 0 && cookie[0] != 0) {
			cookieVal = new String(cookie, "UTF-8");
		}
		if (userId.length > 0 && userId[0] != 0) {
			userIdVal = new String(userId, "UTF-8");
		}
		
		if(userIdVal == null || cookieVal == null){
			return false;
		}else if(userIdVal != null && cookieVal != null){
			return true;
		}
		
		return false;
	}
}