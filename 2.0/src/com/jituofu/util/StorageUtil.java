package com.jituofu.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class StorageUtil {
	/**
	 * 写入内部数据
	 * 
	 * @param filename
	 * @param content
	 */
	public static void writeInternalStoragePrivate(Context context,
			String filename, String content) {
		try {
			FileOutputStream fos = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			fos.write(content.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取内部数据
	 * 
	 * @param context
	 * @param filename
	 * @return
	 */
	public static byte[] readInternalStoragePrivate(Context context,
			String filename) {
		int len = 1024;
		byte[] buffer = new byte[len];
		try {
			FileInputStream fis = context.openFileInput(filename);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int nrb = fis.read(buffer, 0, len);
			while (nrb != -1) {
				baos.write(buffer, 0, nrb);
				nrb = fis.read(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}
}
