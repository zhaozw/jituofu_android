package cn.com.jizhangbao.util;

import android.os.Environment;
import android.os.StatFs;

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
	@SuppressWarnings("deprecation")
	public static int getFreeSpace() {
		StatFs fs = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());// 修改过的Android OS获取的永远都是内置存储空间

		double sdFreeMB = ((double) fs.getAvailableBlocks() * (double) fs
				.getBlockSize()) / MB / MB;

		return (int) sdFreeMB;
	}
}
