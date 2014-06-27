package com.jituofu.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.base.BaseUi;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

public class UIFeedback extends BaseUi implements BaseUiBuilder,
		BaseUiFormBuilder {
	private boolean validated = false;// 验证结果
	private int screenShotCounter = 0, // 截图计数器
			maxScreenShotCounter = 3;// 最大截图数量

	private LinearLayout screenshotsBoxView = null;
	private ImageButton addBtnView = null;

	Uri imgPreview = null;// 最终的截图预览资源

	private Button okBtnView;
	private TextView titleView;
	private EditText textareaView;

	private String feedbackContent;

	private ArrayList<String> imgFiles = new ArrayList<String>();// 存储所有截图的路径

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);

		prepare();

		onUpdate();
		onBind();
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
										C.DIRS.feedbackDir,
										C.DIRS.feedbackFileName);
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

		// 拍照返回
		if (requestCode == C.COMMON.camera) {
			String path = AppUtil.getExternalStorageDirectory()
					+ C.DIRS.rootdir + C.DIRS.feedbackDir + "/"
					+ C.DIRS.feedbackFileName;

			int degree = AppUtil.readPictureDegree(path);

			Bitmap bitmap = AppUtil.compressImageFromFile(path);

			bitmap = AppUtil.rotaingImageView(degree, bitmap);

			if (bitmap == null) {
				this.showToast(R.string.no_camera_image);
				return;
			}

			String wufdPath = AppUtil.getExternalStorageDirectory()
					+ C.DIRS.rootdir + C.DIRS.feedbackDir
					+ C.DIRS.waitUploadFeedbackDir;
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

				imgFiles.add(fileName);
				imgPreview = Uri.parse(fileName);
			} else {
				return;
			}

			File oldScreenShot = new File(path);
			oldScreenShot.delete();
		} else if (requestCode == C.COMMON.gallery) {// 图片库选择
			if (data == null) {
				this.showToast(R.string.no_gallery_image);
				return;
			}
			Uri originalUri = data.getData();
			String path = AppUtil.getRealPathFromURI(this, originalUri);

			int degree = AppUtil.readPictureDegree(path);
			Bitmap bitmap = AppUtil.compressImageFromFile(path);
			bitmap = AppUtil.rotaingImageView(degree, bitmap);

			if (bitmap == null) {
				this.showToast(R.string.no_gallery_image);
				return;
			}

			String sdcardDir = AppUtil.getExternalStorageDirectory();
			if (AppUtil.mkdir(sdcardDir + C.DIRS.rootdir)) {
				if (!AppUtil.mkdir(sdcardDir + C.DIRS.rootdir
						+ C.DIRS.feedbackDir)) {
					this.showToast(R.string.save_image_error);
					return;
				}
			} else {
				this.showToast(R.string.save_image_error);
				return;
			}

			String wufdPath = AppUtil.getExternalStorageDirectory()
					+ C.DIRS.rootdir + C.DIRS.feedbackDir
					+ C.DIRS.waitUploadFeedbackDir;

			if (AppUtil.mkdir(wufdPath)) {
				FileOutputStream fos = null;
				String fileName = wufdPath + "/" + AppUtil.getCurrentTime()
						+ ".png";

				try {
					fos = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

					imgFiles.add(fileName);
					imgPreview = Uri.parse(fileName);
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
		}

		if (imgPreview != null) {
			RelativeLayout view = this.getScreenShotLayout();
			ImageView iv = (ImageView) view.findViewById(R.id.iv);
			iv.setImageURI(imgPreview);
			bind2ShotView(view);

			// 最大允许3个截图
			screenShotCounter++;
			if (screenShotCounter >= maxScreenShotCounter) {
				addBtnView.setVisibility(View.INVISIBLE);
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
				screenshotsBoxView.removeView(view);
				if (screenShotCounter >= 1) {
					screenShotCounter--;
				}
				if (screenShotCounter < maxScreenShotCounter) {
					addBtnView.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private RelativeLayout getScreenShotLayout() {
		RelativeLayout tem = (RelativeLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.screenshot, null);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		p.width = AppUtil.getActualMeasure(getApplicationContext(), 50);
		p.setMargins(0, 0,
				AppUtil.getActualMeasure(getApplicationContext(), 10), 0);
		tem.setLayoutParams(p);

		screenshotsBoxView.addView(tem);
		return tem;
	}

	private void doTaskSubmit() {
		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("content", this.feedbackContent);
		urlParams.put("dir", C.DIRS.feedbackDir);

		try {
			this.doUploadTaskAsync(C.TASK.feedbackcreate,
					C.API.host + C.API.feedbackcreate, urlParams, imgFiles);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message,
			ArrayList<String> filesPath) throws Exception {
		super.onTaskComplete(taskId, message, filesPath);

		int resultStatus = message.getResultStatus();
		if (resultStatus == 100) {
			JSONObject operation = message.getOperation();

		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void initListView() {
		// 客服电话
		LinearLayout phone = (LinearLayout) findViewById(R.id.phone);
		ImageView phoneIcon = (ImageView) phone.findViewById(R.id.icon);
		TextView phoneTxt = (TextView) phone.findViewById(R.id.txt);
		phoneIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.phone));
		phoneTxt.setText(R.string.SERVICE_PHONE);

		// 微信
		LinearLayout wechat = (LinearLayout) findViewById(R.id.wechat);
		ImageView wechatIcon = (ImageView) wechat.findViewById(R.id.icon);
		TextView wechatTxt = (TextView) wechat.findViewById(R.id.txt);
		wechatIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.weixin));
		wechatTxt.setText(R.string.SERVICE_WEIXIN);
		wechat.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);

		// qq
		LinearLayout qq = (LinearLayout) findViewById(R.id.qq);
		ImageView qqIcon = (ImageView) qq.findViewById(R.id.icon);
		TextView qqTxt = (TextView) qq.findViewById(R.id.txt);
		qqIcon.setImageDrawable(this.getResources().getDrawable(R.drawable.qq));
		qqTxt.setText(R.string.SERVICE_QQ);
		qq.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);

		// weibo
		LinearLayout weibo = (LinearLayout) findViewById(R.id.weibo);
		ImageView weiboIcon = (ImageView) weibo.findViewById(R.id.icon);
		TextView weiboTxt = (TextView) weibo.findViewById(R.id.txt);
		weiboIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.weibo));
		weiboTxt.setText(R.string.SERVICE_WEIBO);
		weibo.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);

		// mail
		LinearLayout mail = (LinearLayout) findViewById(R.id.email);
		ImageView mailIcon = (ImageView) mail.findViewById(R.id.icon);
		TextView mailTxt = (TextView) mail.findViewById(R.id.txt);
		mailIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.mail));
		mailTxt.setText(R.string.SERVICE_EMAIL);
		mail.findViewById(R.id.border).setVisibility(View.INVISIBLE);
		mail.findViewById(R.id.arrow).setVisibility(View.INVISIBLE);
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub
		AppUtil.showLoadingPopup(this, R.string.COMMON_CLZ);
		doTaskSubmit();
	}

	@Override
	public void beforeSubmit() {
		// TODO Auto-generated method stub
		// 防止重复点击
		if (validated) {
			return;
		}

		validated = true;

		if (validation()) {

			doSubmit();
		}
		validated = false;
	}

	@Override
	public boolean validation() {
		// TODO Auto-generated method stub
		boolean result = false;

		feedbackContent = getEditValue(textareaView);

		if (feedbackContent == null) {
			this.showToast(R.string.FEEDBACK_CONTENT_SPECIFY);
		} else if (AppUtil.getStrLen(feedbackContent) < 2) {
			this.showToast(R.string.FEEDBACK_CONTENT_INVALID);
		} else if (screenShotCounter > maxScreenShotCounter) {
			this.showToast(R.string.FEEDBACK_SCREENSHOT_OVERMAX);
		} else {
			result = true;
		}

		return result;
	}

	@Override
	public void onBind() {
		// TODO Auto-generated method stub
		this.onCustomBack();
		addBtnView.setOnClickListener(new AddScreenShotListener());

		okBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beforeSubmit();
			}
		});
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		titleView.setText(R.string.feedbackTitle);
		initListView();
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		okBtnView = (Button) this.findViewById(R.id.okBtn);
		this.screenshotsBoxView = (LinearLayout) this
				.findViewById(R.id.screenshots);
		this.addBtnView = (ImageButton) this.findViewById(R.id.add);

		titleView = (TextView) findViewById(R.id.title);
		textareaView = (EditText) findViewById(R.id.textarea);
	}
}