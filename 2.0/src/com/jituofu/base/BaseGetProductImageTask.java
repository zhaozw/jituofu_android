package com.jituofu.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.jituofu.R;
import com.jituofu.util.AppClientUtil;
import com.jituofu.util.AppUtil;

public class BaseGetProductImageTask extends AsyncTask<String, String, Uri> {
	private String productId, productImgPath;

	private final WeakReference<ImageView> imageViewReference;

	public BaseGetProductImageTask(ImageView view, String productImgPath,
			String productId) {
		this.productId = productId;
		this.productImgPath = productImgPath;
		imageViewReference = new WeakReference<ImageView>(view);
	}

	// 后台运行的子线程子线程
	@Override
	protected Uri doInBackground(String... params) {
		try {
			return getProductImgUri(productImgPath, productId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 这个放在在ui线程中执行
	@Override
	protected void onPostExecute(Uri result) {
		super.onPostExecute(result);
		if (this.isCancelled()) {
			return;
		}
		if (imageViewReference != null) {
			ImageView view = (ImageView) imageViewReference.get();
			if (view != null) {
				if (result != null) {
					view.setImageURI(result);
					view.setBackgroundResource(0);
				} else {
					view.setImageURI(null);
					view.setBackgroundResource(R.drawable.default_img_placeholder);
				}
			}

		}
	}

	/**
	 * 从网络上获取商品图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片
	 * 
	 * @param path
	 * @param id
	 *            商品id
	 * @return
	 * @throws Exception
	 */
	private Uri getProductImgUri(String path, String id) throws Exception {
		String productDirPath = AppUtil.getExternalStorageDirectory()
				+ C.DIRS.rootdir + C.DIRS.productDir;

		if (AppUtil.mkdir(AppUtil.getExternalStorageDirectory()
				+ C.DIRS.rootdir)) {
			if (AppUtil.mkdir(productDirPath)) {
				String oldFileName = productDirPath + "/" + id + ".png";
				File file = new File(oldFileName);
				// 如果图片存在本地缓存目录，则不去服务器下载
				if (file.exists()) {
					return Uri.fromFile(file);
				} else {
					// 从网络上获取图片
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(AppClientUtil.timeoutConnection);
					conn.setRequestMethod("GET");
					conn.setDoInput(true);
					if (conn.getResponseCode() == HttpStatus.SC_OK) {

						InputStream is = conn.getInputStream();
						FileOutputStream fos = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = is.read(buffer)) != -1) {
							fos.write(buffer, 0, len);
						}
						is.close();
						fos.close();
						// 返回一个URI对象
						return Uri.fromFile(file);
					}
				}
			}
		}

		return null;
	}
}
