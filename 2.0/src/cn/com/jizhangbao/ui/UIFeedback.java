package cn.com.jizhangbao.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.com.jizhangbao.R;
import cn.com.jizhangbao.base.C;
import cn.com.jizhangbao.base.BaseUi;
import cn.com.jizhangbao.util.AppUtil;
import cn.com.jizhangbao.util.SDUtil;

@SuppressWarnings("deprecation")
public class UIFeedback extends BaseUi {
	ListView lv;
	SimpleAdapter adapter;

	int screenShotCounter = 0;
	int maxScreenShotCounter = 3;

	LinearLayout screenshotsBox = null;
	ImageButton addBtn = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);

		this.screenshotsBox = (LinearLayout) this
				.findViewById(R.id.screenshots);
		this.addBtn = (ImageButton) this.findViewById(R.id.add);

		updateUI();
		bindUI();
	}

	@Override
	protected void bindUI() {
		super.bindUI();

		this.globalBackLogic();
		addBtn.setOnClickListener(new AddScreenShotListener());
	}

	class AddScreenShotListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			AppUtil.openImagePicker(UIFeedback.this,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								AppUtil.openCamera(UIFeedback.this,
										C.JZB.feedbackDir);
							} else if (which == 1) {
								AppUtil.openGallery(UIFeedback.this);
							}
						}
					});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Uri imgPreview = null;
		
		// 拍照返回
		if (requestCode == C.JZB.camera) {
			String path = AppUtil.getExternalStorageDirectory() + C.JZB.rootdir
					+ C.JZB.feedbackDir + "/" + C.JZB.feedbackFileName;
			Bitmap bitmap = AppUtil.compressImageFromFile(path);

			if (bitmap == null) {
				this.showToast(R.string.no_camera_image);
				return;
			}

			String wufdPath = AppUtil.getExternalStorageDirectory()
					+ C.JZB.rootdir + C.JZB.feedbackDir
					+ C.JZB.waitUploadFeedbackDir;
			if (AppUtil.mkdir(wufdPath)) {
				FileOutputStream fos = null;
				String fileName = wufdPath + "/" + AppUtil.getCurrentTime()
						+ ".png";
				try {
					fos = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						fos.flush();
						fos.close();
						bitmap.recycle();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				imgPreview = Uri.parse(fileName);
			}
			
			File oldScreenShot = new File(path);
			oldScreenShot.delete();
		} else if (requestCode == C.JZB.gallery) {//图片库选择
			if(data == null){
				this.showToast(R.string.no_gallery_image);
				return;
			}
			Uri originalUri = data.getData();
			ContentResolver resolver = getContentResolver();

			String path = AppUtil.getRealPathFromURI(this, originalUri);
			Bitmap bitmap = AppUtil.compressImageFromFile(path);

			if (bitmap == null) {
				this.showToast(R.string.no_gallery_image);
				return;
			}

			String sdcardDir = AppUtil.getExternalStorageDirectory();
			if (AppUtil.mkdir(sdcardDir + C.JZB.rootdir)) {
				if (!AppUtil.mkdir(sdcardDir + C.JZB.rootdir + C.JZB.feedbackDir)) {
					this.showToast(R.string.save_image_error);
					return;
				}
			} else {
				this.showToast(R.string.save_image_error);
				return;
			}

			String wufdPath = AppUtil.getExternalStorageDirectory()
					+ C.JZB.rootdir + C.JZB.feedbackDir
					+ C.JZB.waitUploadFeedbackDir;

			if (AppUtil.mkdir(wufdPath)) {
				FileOutputStream fos = null;
				String fileName = wufdPath + "/" + AppUtil.getCurrentTime()
						+ ".png";
				try {
					fos = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						fos.flush();
						fos.close();
						bitmap.recycle();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				this.showToast(R.string.save_image_error);
			}
			imgPreview = originalUri;
		}
		
		if(imgPreview != null){
			RelativeLayout view = this.addScreenShotView();
			ImageView iv = (ImageView) view.findViewById(R.id.iv);
			iv.setImageURI(imgPreview);
			bind2ShotView(view);
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(C.API.host+C.API.fileadd);
			ResponseHandler responseHandler = new BasicResponseHandler();
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			File file = new File(AppUtil.getRealPathFromURI(this, imgPreview));
			FileBody fileBody = new FileBody(file, "images/png");
			reqEntity.addPart("file", fileBody);
			
			try {
				reqEntity.addPart("username", new StringBody("朱琦"));
				reqEntity.addPart("password", new StringBody("1234"));
				
				postRequest.setEntity(reqEntity);
				try {
					httpClient.execute(postRequest, responseHandler);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//最大允许3个截图
			screenShotCounter++;
			if(screenShotCounter >= maxScreenShotCounter){
				addBtn.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void bind2ShotView(final RelativeLayout view) {
		ImageView icon = (ImageView) view.findViewById(R.id.iv);
		ImageButton delete = (ImageButton) view.findViewById(R.id.delete);

		icon.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				ImageButton delete = (ImageButton) view
						.findViewById(R.id.delete);
				delete.setVisibility(View.VISIBLE);
				return true;
			}
		});
		icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageButton delete = (ImageButton) view
						.findViewById(R.id.delete);
				delete.setVisibility(View.INVISIBLE);
			}
		});
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				screenshotsBox.removeView(view);
				if (screenShotCounter >= 1) {
					screenShotCounter--;
				}
				if (screenShotCounter < maxScreenShotCounter) {
					addBtn.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private RelativeLayout addScreenShotView() {
		RelativeLayout tem = (RelativeLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.screenshot, null);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		p.width = AppUtil.getActualMeasure(getApplicationContext(), 50);
		p.setMargins(0, 0,
				AppUtil.getActualMeasure(getApplicationContext(), 10), 0);
		tem.setLayoutParams(p);

		screenshotsBox.addView(tem);
		return tem;
	}

	@Override
	protected void updateUI() {
		super.updateUI();
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(R.string.feedbackTitle);

		renderListView();
	}

	private void renderListView() {
		// 客服电话
		LinearLayout phone = (LinearLayout) findViewById(R.id.phone);
		ImageView phoneIcon = (ImageView) phone.findViewById(R.id.icon);
		TextView phoneTxt = (TextView) phone.findViewById(R.id.txt);
		phoneIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.phone));
		phoneTxt.setText(C.FEEDBACK.phone);

		// 微信
		LinearLayout wechat = (LinearLayout) findViewById(R.id.wechat);
		ImageView wechatIcon = (ImageView) wechat.findViewById(R.id.icon);
		TextView wechatTxt = (TextView) wechat.findViewById(R.id.txt);
		wechatIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.weixin));
		wechatTxt.setText(C.FEEDBACK.wechat);
		wechat.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);

		// qq
		LinearLayout qq = (LinearLayout) findViewById(R.id.qq);
		ImageView qqIcon = (ImageView) qq.findViewById(R.id.icon);
		TextView qqTxt = (TextView) qq.findViewById(R.id.txt);
		qqIcon.setImageDrawable(this.getResources().getDrawable(R.drawable.qq));
		qqTxt.setText(C.FEEDBACK.qq);
		qq.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);

		// weibo
		LinearLayout weibo = (LinearLayout) findViewById(R.id.weibo);
		ImageView weiboIcon = (ImageView) weibo.findViewById(R.id.icon);
		TextView weiboTxt = (TextView) weibo.findViewById(R.id.txt);
		weiboIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.weibo));
		weiboTxt.setText(C.FEEDBACK.weibo + this.getString(R.string.app_name));
		weibo.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);

		// mail
		LinearLayout mail = (LinearLayout) findViewById(R.id.email);
		ImageView mailIcon = (ImageView) mail.findViewById(R.id.icon);
		TextView mailTxt = (TextView) mail.findViewById(R.id.txt);
		mailIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.mail));
		mailTxt.setText(C.FEEDBACK.email);
		mail.findViewById(R.id.border).setVisibility(View.INVISIBLE);
		mail.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
	}
}