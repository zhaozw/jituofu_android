package com.jituofu.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

@SuppressLint("UseValueOf")
public class SDUtil {
	private static double MB = 1024;

	/**
	 * 检查SD卡是否可以正常使用
	 * 
	 * @return
	 */
	public static boolean isOkSDCard() {
		String SDState = Environment.getExternalStorageState();

		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取剩余空间
	 * 
	 * @return MB
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static int getFreeSpace() {
		// 修改过的Android OS获取的永远都是内置存储空间
		StatFs fs = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());

		int sdFreeMB = 0;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

			long freeMB = (long) ((long) fs.getAvailableBlocksLong() * (long) fs.getBlockSizeLong() / MB / MB);
			sdFreeMB = new Long(freeMB).intValue();
		} else {
			double freeMB = ((double) fs.getAvailableBlocks() * (double) fs
					.getBlockSize()) / MB / MB;
			sdFreeMB = new Double(freeMB).intValue();
		}

		return (int) sdFreeMB;
	}
}
