package com.jituofu.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseNotification;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.base.BaseUi;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

public class UIFeedback extends BaseUi implements BaseUiBuilder,
		BaseUiFormBuilder {
	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;

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

	// 消息通知
	private boolean isStop = false;// 如果当前activity没有显示，那么截图上传完毕后就调用notification通知
	private int notificationId = 0;
	private BaseNotification baseNoti;
	private String notificationTitle;
	private String notificationTickerText;
	private int autoCancelTime = 3000;// 自动取消的时间

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
			iv.setTag(imgPreview.getPath());// 保存当前图片的路径
			bind2ShotView(view);

			// 最大允许3个截图
			screenShotCounter++;
			if (screenShotCounter >= maxScreenShotCounter) {
				addBtnView.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void bind2ShotView(final RelativeLayout view) {
		final ImageView icon = (ImageView) view.findViewById(R.id.iv);
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

				// 从imgFiles中删除截图
				String path = (String) icon.getTag();
				if (path != null && imgFiles.indexOf(path) != -1) {
					imgFiles.remove(path);
					StorageUtil.deleteFile(path);
				}
			}
		});
	}

	private RelativeLayout getScreenShotLayout() {
		RelativeLayout tem = (RelativeLayout) LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.screenshot_item_layout, null);
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
			if (this.imgFiles.size() > 0) {
				this.closePopupDialog();

				// 清除表单内容
				this.screenShotCounter = 0;
				this.screenshotsBoxView.removeAllViews();
				this.textareaView.setText(null);

				baseDialogBuilder
						.setMessage(R.string.FEEDBACK_SAVE2LOCAL_SUCCESS);
				baseDialog = baseDialogBuilder.create();
				baseDialog.show();
				AppUtil.timer(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							saveData2LocalAndSend();
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						baseDialog.dismiss();
					}
				}, 3000);
			} else {
				this.doTaskAsync(C.TASK.feedbackcreate, C.API.host
						+ C.API.feedbackcreate, urlParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存数据到本地然后发送
	 * 
	 * @throws JSONException
	 * @throws UnsupportedEncodingException
	 */
	private void saveData2LocalAndSend() throws JSONException,
			UnsupportedEncodingException {
		JSONArray fileList = new JSONArray();// 截图路径列表
		JSONObject data = new JSONObject();// 每一组反馈内容的数据
		JSONArray feedbacks = null;// 所有反馈内容

		for (int i = 0; i < this.imgFiles.size(); i++) {
			fileList.put(imgFiles.get(i));
		}
		data.put("content", this.feedbackContent);
		data.put("files", fileList);

		// 从本地获取feedback数据
		byte[] fdc = StorageUtil.readInternalStoragePrivate(this,
				C.DIRS.feedbackCacheFileName);
		String fdcVal = "";
		if (fdc.length > 0 && fdc[0] != 0) {
			fdcVal = new String(fdc, "UTF-8");
		}
		if (fdcVal != null && fdcVal.length() > 0) {
			feedbacks = new JSONArray(fdcVal);
		} else {
			feedbacks = new JSONArray();
		}

		if (feedbacks != null) {
			feedbacks.put(data);
		}
		StorageUtil.writeInternalStoragePrivate(this,
				C.DIRS.feedbackCacheFileName, feedbacks.toString());

		// 发送本地保存的反馈数据
		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("dir", C.DIRS.feedbackDir);
		for (int j = 0; j < feedbacks.length(); j++) {
			JSONObject fd = feedbacks.getJSONObject(j);
			if (fd != null) {
				String content = fd.getString("content");
				JSONArray files = fd.getJSONArray("files");
				ArrayList<String> filesArrayList = AppUtil
						.jsonArray2ArrayList(files);

				if (filesArrayList.size() > 0) {
					urlParams.put("content", content);
					this.doUploadTaskAsync(C.TASK.feedbackcreate, C.API.host
							+ C.API.feedbackcreate, urlParams, filesArrayList);
				}
			}
		}
	}

	private void resetForm(ArrayList<String> filesPath)
			throws UnsupportedEncodingException, JSONException {
		this.imgFiles = new ArrayList<String>();
		this.screenShotCounter = 0;
		this.screenshotsBoxView.removeAllViews();
		this.textareaView.setText(null);

		JSONArray feedbacks = null;
		JSONArray newFeedbacks = new JSONArray();
		// 从本地获取feedback数据
		byte[] fdc = StorageUtil.readInternalStoragePrivate(this,
				C.DIRS.feedbackCacheFileName);
		String fdcVal = "";
		if (fdc.length > 0 && fdc[0] != 0) {
			fdcVal = new String(fdc, "UTF-8");
		}
		if (fdcVal != null && fdcVal.length() > 0) {
			feedbacks = new JSONArray(fdcVal);
		} else {
			feedbacks = new JSONArray();
		}

		for (int i = 0; i < filesPath.size(); i++) {
			String path = filesPath.get(i);

			if (path != null) {
				StorageUtil.deleteFile(path);

				// 从本地缓存数据中删除已经提交的截图

				for (int j = feedbacks.length() - 1; j >= 0; j--) {
					JSONObject fd = feedbacks.getJSONObject(j);
					if (fd != null) {
						JSONArray files = fd.getJSONArray("files");
						ArrayList<String> filesArrayList = AppUtil
								.jsonArray2ArrayList(files);
						for (int k = filesArrayList.size() - 1; k >= 0; k--) {
							if (filesPath.indexOf(filesArrayList.get(k)) != -1) {
								filesArrayList.remove(k);
							}
						}
						fd.put("files",
								AppUtil.arrayList2JSONArray(filesArrayList));

						if (filesArrayList.size() > 0) {
							newFeedbacks.put(fd);
						}
					}
				}

			}
		}
		StorageUtil.writeInternalStoragePrivate(this,
				C.DIRS.feedbackCacheFileName, newFeedbacks.toString());
	}

	@Override
	public void onStop() {
		super.onStop();
		isStop = true;
	}

	private void resetForm() {
		this.imgFiles = new ArrayList<String>();
		this.screenShotCounter = 0;
		this.screenshotsBoxView.removeAllViews();
		this.textareaView.setText(null);
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message,
			ArrayList<String> filesPath) throws Exception {
		super.onTaskComplete(taskId, message, filesPath);

		int resultStatus = message.getResultStatus();
		if (resultStatus == 100) {
			resetForm(filesPath);

			if (!isStop) {
				this.isStop = false;
				baseDialogBuilder.setNegativeButton(R.string.COMMON_IKNOW,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface di, int which) {
								// TODO Auto-generated method stub
								baseDialog.dismiss();
							}
						});
				baseDialogBuilder.setMessage(message.getMemo());
				baseDialog = baseDialogBuilder.create();
				baseDialog.show();
			} else {
				baseNoti.create(UIFeedback.class, notificationTickerText,
						notificationTitle, message.getMemo(), R.drawable.ic_launcher, R.drawable.ic_launcher);

				AppUtil.timer(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						baseNoti.cancel();
					}
				}, autoCancelTime);
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		if (resultStatus == 100) {
			resetForm();

			if (!isStop) {
				this.isStop = false;
				baseDialogBuilder.setNegativeButton(R.string.COMMON_IKNOW,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface di, int which) {
								// TODO Auto-generated method stub
								baseDialog.dismiss();
							}
						});
				baseDialogBuilder.setMessage(message.getMemo());
				baseDialog = baseDialogBuilder.create();
				baseDialog.show();
			} else {
				baseNoti.create(UIFeedback.class, notificationTickerText,
						notificationTitle, message.getMemo(), R.drawable.ic_launcher, R.drawable.sucess_small);

				AppUtil.timer(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						baseNoti.cancel();
					}
				}, autoCancelTime);
			}
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

		baseDialogBuilder = new BaseDialog.Builder(this);

		// 消息通知
		baseNoti = new BaseNotification(this, this.notificationId);
		this.notificationTitle = this
				.getString(R.string.FEEDBACK_SAVE2LOCAL_SUCCESS);
		this.notificationTickerText = this
				.getString(R.string.FEEDBACK_SAVE2LOCAL_TIKERTEXT);
	}
}