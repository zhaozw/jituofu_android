package com.jituofu.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.CharArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.C;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.widget.TextView;

public class AppUtil {
	/**
	 * 强制转换个位到十位
	 * @param value
	 * @return
	 */
	public static String to2bit(int value){
		if(value < 10){
			return "0"+value;
		}
		
		return value+"";
	}
	
	/**
	 * 是否是24小时制
	 * @param context
	 * @return
	 */
	public static boolean is24(Context context){
		ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                                           android.provider.Settings.System.TIME_12_24);
        
        if(strTimeFormat != null && strTimeFormat.equals("24"))       {
              return true;
        }
        
        return false;
	}
	
	/**
	 * 获取当前时间：date time
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDateTime() {
		// 24小时制
		SimpleDateFormat dateFormat24 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat24.format(Calendar.getInstance().getTime());
	}

	/**
	 * 获取屏幕分辨率
	 * 
	 * @param activity
	 * @return
	 */
	public static int[] getScreen(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels; // 当前分辨率 宽度
		int heigth = dm.heightPixels; // 当前分辨率高度

		return new int[] { width, heigth };
	}

	/**
	 * drawable转bipmap
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap getBitmapFromResources(Context context, int resId) {
		Resources res = context.getResources();
		return BitmapFactory.decodeResource(res, resId);
	}

	/**
	 * 转换JSONArray字符串到ArrayList<String>
	 * 
	 * @param jsonArray
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<String> jsonArray2ArrayList(JSONArray jsonArray)
			throws JSONException {
		ArrayList<String> arrayList = new ArrayList<String>();

		for (int k = 0; k < jsonArray.length(); k++) {

			arrayList.add(jsonArray.getString(k));
		}

		return arrayList;
	}

	/**
	 * 转换ArrayList<String>到 JSONArray
	 * 
	 * @param arrayList
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray arrayList2JSONArray(ArrayList<String> arrayList)
			throws JSONException {
		JSONArray jsonArray = new JSONArray();

		for (int k = 0; k < arrayList.size(); k++) {

			jsonArray.put(arrayList.get(k));
		}

		return jsonArray;
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 * @return degree
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
	 * 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static String trimAll(String str) {
		if (str == null) {
			return str;
		}
		return str.replaceAll("\\s*", "");
	}

	/**
	 * 打开图片选择器
	 * 
	 * @param ui
	 * @param listener
	 */
	@SuppressLint("InlinedApi")
	public static void openImagePicker(final BaseUi ui,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder dialog = null;
		String[] items = new String[] {
				ui.getString(R.string.COMMON_IMAGEPICKERCAMMERA),
				ui.getString(R.string.COMMON_IMAGEPICKERGALLERY) };

		ContextThemeWrapper themedContext;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(ui,
					android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(ui,
					android.R.style.Theme_Light_NoTitleBar);
		}
		dialog = new AlertDialog.Builder(themedContext);
		dialog.setTitle(R.string.COMMON_IMAGEPICKERTITLE);
		dialog.setItems(items, listener);
		dialog.create().show();
	}

	/**
	 * 打开相机
	 * 
	 * @param ui
	 * @param dir
	 * @param temFileName
	 */
	public static void openCamera(BaseUi ui, String dir, String temFileName) {
		if (SDUtil.isOkSDCard()) {
			int fs = SDUtil.getFreeSpace();

			// 没有剩余空间
			if (fs < 1) {
				ui.showToast(R.string.COMMON_SDCARDNOSPACE);
			} else {
				// 目录检查
				String sdcardDir = AppUtil.getExternalStorageDirectory();
				if (AppUtil.mkdir(sdcardDir + C.DIRS.rootdir)) {
					if (!AppUtil.mkdir(sdcardDir + C.DIRS.rootdir + dir)) {
						return;
					}
				} else {
					return;
				}
				String fileName = sdcardDir + C.DIRS.rootdir + dir + "/"
						+ temFileName;
				Uri imageUri = null;

				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.TITLE, fileName);

				imageUri = Uri.fromFile(new File(fileName));
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
				ui.startActivityForResult(intent, C.COMMON.camera);
			}
		} else {
			ui.showToast(R.string.COMMON_SDCARDERROR);
		}
	}

	/**
	 * 打开图片库
	 * 
	 * @param ui
	 */
	public static void openGallery(BaseUi ui) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		ui.startActivityForResult(intent, C.COMMON.gallery);
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 * @return
	 */
	public static boolean mkdir(String path) {
		File file = new File(path);

		if (!file.exists()) {
			if (file.mkdir()) {
				return true;
			} else {
				return false;
			}
		} else if (file.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取存储空间目录
	 * 
	 * @return
	 */
	public static String getExternalStorageDirectory() {
		String path = Environment.getExternalStorageDirectory().getPath();
		return path;
	}

	/**
	 * 压缩一个已存的bitmap
	 * 
	 * @param srcPath
	 * @return
	 */
	public static Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;
		float ww = 480f;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		return bitmap;
	}

	/**
	 * 获取Uri的真实物理路径
	 * 
	 * @param ui
	 * @param uri
	 * @return
	 */
	public static String getRealPathFromURI(BaseUi ui, Uri uri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = ui.getContentResolver().query(uri, proj, null, null,
				null);
		if (cursor.moveToFirst()) {
			;
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}

	/**
	 * 执行延时任务
	 * 
	 * @param t
	 */
	public static void timer(TimerTask t, int time) {
		new Timer().schedule(t, time);
	}

	/**
	 * 执行延时任务
	 * 
	 * @param t
	 */
	public static void timer(TimerTask t) {
		new Timer().schedule(t, 3000);
	}

	/**
	 * 发送一个清除activities堆栈广播
	 * 
	 * @param ctx
	 */
	public static void sendClearActivitiesBr(Context ctx) {
		Intent broadcast = new Intent();
		broadcast.setAction("com.jituofu.util.ClearActivitiesBroadcast");
		broadcast.putExtra("type", "ClearActivitiesBroadcast");
		ctx.sendBroadcast(broadcast);
	}

	/**
	 * 显示一个R.layout.popup_contentview的弹出层
	 * 
	 * @param txt
	 */
	public static void showLoadingPopup(BaseUi ui, int txt) {
		Dialog dialog = ui.getPopupDialog(ui);

		ui.setPopupView(R.layout.popup_contentview);

		TextView popupTxt = (TextView) dialog.findViewById(R.id.popupTxt);

		popupTxt.setText(txt);
		ui.showPopupDialog(false);
	}

	public static void showLoadingPopup(BaseUi ui, String txt) {
		Dialog dialog = ui.getPopupDialog(ui);

		ui.setPopupView(R.layout.popup_contentview);

		TextView popupTxt = (TextView) dialog.findViewById(R.id.popupTxt);

		popupTxt.setText(txt);
		ui.showPopupDialog(false);
	}

	/**
	 * 去应用市场
	 */
	public void gotoMarket(BaseUi ui) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + ui.getPackageName()));
		ui.startActivity(intent);
	}

	/**
	 * 转换gzip到string
	 * 
	 * @param entity
	 * @param defaultCharset
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String gzipToString(final HttpEntity entity,
			final String defaultCharset) throws IOException, ParseException {
		if (entity == null) {
			throw new IllegalArgumentException("HttpEntity entity 不能为null");
		}
		InputStream instream = entity.getContent();
		if (instream == null) {
			return "";
		}
		if (entity.getContentEncoding().getValue().contains("gzip")) {
			instream = new GZIPInputStream(instream);
		}
		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("HttpEntity entity 太长");
		}
		int i = (int) entity.getContentLength();
		if (i < 0) {
			i = 4096;
		}
		String charset = EntityUtils.getContentCharSet(entity);
		if (charset == null) {
			charset = defaultCharset;
		}
		if (charset == null) {
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
		}
		Reader reader = new InputStreamReader(instream, charset);
		CharArrayBuffer buffer = new CharArrayBuffer(i);
		try {
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
		} finally {
			reader.close();
		}
		return buffer.toString();
	}

	/**
	 * 转换gzip到string
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static String gzipToString(final HttpEntity entity)
			throws IOException, ParseException {
		return gzipToString(entity, null);
	}

	/**
	 * 检查字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 四舍五入
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String toFixed(double value){
		String result = String.format("%.2f", value);
		
		//去除小数点后为0的数字
		if(result.indexOf(".") != -1){
			String[] resultSplit = result.split("\\.");
			if(resultSplit.length > 1){
				if(Integer.parseInt(resultSplit[1]) <= 0){
					result = resultSplit[0];
				}
			}
		}
		return result;
	}

	/**
	 * 是否是个有效的商品价格
	 * 
	 * @param price
	 * @return
	 */
	public static boolean isAvailablePrice(String price) {
		boolean result = true;

		if (price == null) {
			result = false;
		} else if ((price.indexOf(".") == 0)
				|| (price.indexOf(".") == price.length() - 1)) {
			result = false;
		} else if (price.indexOf(".") != -1){
			String[] priceSplit = price.split("\\.");
			
			if(!AppUtil.isNumeric(priceSplit[0]) || !AppUtil.isNumeric(priceSplit[1])){
				result = false;
			}
		}else if(!AppUtil.isNumeric(price)){
			result = false;
		}

			return result;
	}

	/**
	 * 获取字符串长度
	 * 
	 * @param value
	 * @return
	 */
	public static int getStrLen(String value) {
		int valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			if (temp.matches(chinese)) {
				valueLength += 1;// 2;
			} else {
				valueLength += 1;
			}
		}
		return valueLength;
	}

	public static BaseMessage getMessage(String result) throws Exception,
			JSONException {
		BaseMessage message = new BaseMessage();
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			if (jsonObject != null) {
				message.setResult(result);
			}
		} catch (JSONException e) {
			throw new JSONException("JSON格式错误： " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return message;
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersion(Context context) {
		String name = "";
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_PERMISSIONS);
			name = pi.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			name = C.COMMON.versionName;
		}

		return name;
	}

	/**
	 * 获取分辨率
	 * 
	 * @param context
	 * @return String low/middle/high
	 */
	public static String getDisplay(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int densityDpi = dm.densityDpi;
		String display = null;

		switch (densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			display = "low";
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			display = "middle";
			break;
		case DisplayMetrics.DENSITY_HIGH:
			display = "high";
			break;
		default:
			display = "middle";
			break;
		}
		return display;
	}

	/**
	 * 获取当时时间戳
	 * 
	 * @return
	 */
	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	/**
	 * MD5 32位加密方法一 小写
	 * 
	 * @param str
	 * @return
	 */

	public final static String get32MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// System.out.println((int)b);
				// 将没个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 判断int是否为空
	 * 
	 * @param v
	 * @return
	 */
	@SuppressLint("UseValueOf")
	static public boolean isEmptyInt(int v) {
		Integer t = new Integer(v);
		return t == null ? true : false;
	}

	/**
	 * 获取device id
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String id;

		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		id = deviceUuid.toString();

		return id;
	}

	/**
	 * 从服务器上获取用户信息
	 * 
	 * @param ctx
	 * @param ui
	 */
	public static void fetchUserFromServer(Context ctx, BaseUi ui) {
		String deviceId = AppUtil.getDeviceId(ctx);
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("clientId", deviceId);
		try {
			ui.doTaskAsync(C.TASK.getuser, C.API.host + C.API.getuser,
					urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从服务器上获取商户信息
	 * 
	 * @param ctx
	 * @param ui
	 */
	public static void fetchStoreSettingsFromServer(Context ctx, BaseUi ui) {
		String deviceId = AppUtil.getDeviceId(ctx);
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("clientId", deviceId);
		try {
			ui.doTaskAsync(C.TASK.storesettingsget, C.API.host + C.API.storesettingsget,
					urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据当前设备的密度,获取真实的尺寸值
	 * 
	 * @param measure
	 * @return
	 */
	public static int getActualMeasure(Context ctx, int measure) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				measure, ctx.getResources().getDisplayMetrics());
	}
}
