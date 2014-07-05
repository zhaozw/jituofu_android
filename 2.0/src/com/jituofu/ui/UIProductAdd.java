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
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseDateTimePicker;
import com.jituofu.base.BaseMenuBottom2Top;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

public class UIProductAdd extends BaseUi implements BaseUiBuilder,
		BaseUiFormBuilder {
	// view对象
	private TextView titleView, timeView, typeView, deletePicView, defaultTxtView;
	private EditText nameView, priceView, countView, remarkView;
	private Button okBtnView;
	private LinearLayout imgPreviewView;
	private ImageView imgPicView;

	// 表单值
	private String name, price, count, remark, typeId, typeName, picPath, time;
	private int year, month, day, hour, minute, second;

	// 表单提交
	private boolean validated = false;

	public static int selectTypeRequestCode = 1;// 分类选择

	private BaseDateTimePicker dateTimePicker;
	private BaseMenuBottom2Top baseBottomMenu;
	
	private Uri imgPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_product_add);

		prepare();
		onUpdate();
		onBind();
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub
		HashMap<String, String> urlParams = new HashMap<String, String>();
		
		String price = priceView.getText()+"";
		
		urlParams.put("name", name);
		urlParams.put("count", count);
		urlParams.put("price", price);
		urlParams.put("type", typeId);
		urlParams.put("date", time);
		urlParams.put("remark", remark);
		if(picPath != null){
			ArrayList<String> filesList = new ArrayList<String>();
			filesList.add(picPath);
			try {
				this.doUploadTaskAsync(C.TASK.productcreate, C.API.host
						+ C.API.productcreate, urlParams, filesList);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				this.doTaskAsync(C.TASK.productcreate, C.API.host
							+ C.API.productcreate, urlParams);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message,
			ArrayList<String> filesPath) throws Exception {
		super.onTaskComplete(taskId, message, filesPath);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();
		if (resultStatus == 100) {
			File img = new File(picPath);
			if(img != null){
				String productDirPath = AppUtil.getExternalStorageDirectory()
						+ C.DIRS.rootdir + C.DIRS.productDir;
				String newFileName = productDirPath+"/"+operation.getString("id")+".png";
				img.renameTo(new File(newFileName));
				submitSuccess(operation);
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message) throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();
		if (resultStatus == 100) {
			submitSuccess(operation);
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
	
	private void submitSuccess(JSONObject data){
		
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

		name = getEditValue(nameView);
		price = AppUtil.trimAll(getEditValue(priceView));
		count = AppUtil.trimAll(getEditValue(countView));
		remark = getEditValue(remarkView);

		if (name == null) {
			this.showToast(R.string.PRODUCT_PNAME_HINT);
		} else if (AppUtil.getStrLen(name) < 2 || AppUtil.getStrLen(name) > 15) {
			this.showToast(R.string.PRODUCT_PNAME_ERROR);
		} else {
			result = true;
		}

		if (result) {
			if (price == null) {
				this.showToast(R.string.PRODUCT_PPRICE_HINT);
				result = false;
			} else if (!AppUtil.isAvailablePrice(price)) {
				this.showToast(R.string.PRODUCT_PPRICE_ERROR);
				result = false;
			} else {
				double dp = Double.parseDouble(price);
				priceView.setText(AppUtil.toFixed(dp));
			}
		}

		if (result) {
			if (count == null) {
				this.showToast(R.string.PRODUCT_PCOUNT_HINT);
				result = false;
			} else if (!AppUtil.isAvailablePrice(count)) {
				this.showToast(R.string.PRODUCT_PCOUNT_ERROR);
				result = false;
			}
		}

		if (result) {
			if (typeId == null) {
				this.showToast(R.string.PRODUCT_PTYPE_ERROR);
				result = false;
			}
		}

		if (result) {
			if (time == null) {
				this.showToast(R.string.PRODUCT_PTIME_ERROR);
				result = false;
			}
		}

		return result;
	}

	@Override
	public void onBind() {
		// TODO Auto-generated method stub
		this.onCustomBack();

		okBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beforeSubmit();
			}
		});

		typeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("from", C.COMMON.productSubmit);
				forwardForResult(UIParentType.class, selectTypeRequestCode,
						bundle);
			}
		});
		((LinearLayout) timeView.getParent())
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (year > 0 && month > 0 && day > 0) {
							if (hour > 0 && minute > 0 && second > 0) {
								dateTimePicker.setHMS(hour, minute, second);
							}
							dateTimePicker.showDateDialog(year, month, day);
						} else {
							dateTimePicker.showDateDialog();
						}
					}
				});

		imgPreviewView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(picPath != null){
					return;
				}
				
				baseBottomMenu.showAtLocation(
						UIProductAdd.this.findViewById(R.id.imgPreview),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		
		deletePicView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteImg();
			}
			
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 选择分类回来
		if (requestCode == selectTypeRequestCode) {
			if (data != null) {
				String parentTypeId = data.getStringExtra("parentTypeId");
				String parentTypeName = data.getStringExtra("parentTypeName");
				String childTypeId = data.getStringExtra("childTypeId");
				String childTypeName = data.getStringExtra("childTypeName");

				if (childTypeId != null) {
					typeId = childTypeId;
				} else {
					typeId = parentTypeId;
				}

				if (childTypeName != null) {
					typeName = parentTypeName
							+ this.getString(R.string.SPFL_SPEARATOR)
							+ childTypeName;
				} else {
					typeName = parentTypeName;
				}

				typeView.setText(typeName);
			}
		}else if (requestCode == C.COMMON.camera) {
			String path = AppUtil.getExternalStorageDirectory()
					+ C.DIRS.rootdir + C.DIRS.productDir + "/"
					+ C.DIRS.productFileName;

			int degree = AppUtil.readPictureDegree(path);

			Bitmap bitmap = AppUtil.compressImageFromFile(path);

			bitmap = AppUtil.rotaingImageView(degree, bitmap);

			if (bitmap == null) {
				this.showToast(R.string.no_camera_image);
				return;
			}

			String productDirPath = AppUtil.getExternalStorageDirectory()
					+ C.DIRS.rootdir + C.DIRS.productDir;
			if (AppUtil.mkdir(productDirPath)) {
				FileOutputStream fos = null;
				String fileName = productDirPath + "/" + AppUtil.getCurrentTime()
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

				picPath = fileName;
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
						+ C.DIRS.productDir)) {
					this.showToast(R.string.save_image_error);
					return;
				}
			} else {
				this.showToast(R.string.save_image_error);
				return;
			}

			String productDirPath = AppUtil.getExternalStorageDirectory()
					+ C.DIRS.rootdir + C.DIRS.productDir;

			if (AppUtil.mkdir(productDirPath)) {
				FileOutputStream fos = null;
				String fileName = productDirPath + "/" + AppUtil.getCurrentTime()
						+ ".png";

				try {
					fos = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

					picPath = fileName;
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
			showImgPreview(imgPreview);
		}
	}
	
	private void deleteImg(){
		StorageUtil.deleteFile(picPath);
		picPath = null;
		defaultTxtView.setVisibility(View.VISIBLE);
		deletePicView.setVisibility(View.GONE);
		imgPicView.setVisibility(View.GONE);
		
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.height = AppUtil.getActualMeasure(this, 42);
		LinearLayout imgPreviewViewParent = (LinearLayout) imgPreviewView.getParent();
		imgPreviewViewParent.setLayoutParams(lp);
		imgPreviewViewParent.setPadding(AppUtil.getActualMeasure(this, 15), 0, 0, 0);
	}
	
	private void showImgPreview(Uri uri){
		defaultTxtView.setVisibility(View.GONE);
		deletePicView.setVisibility(View.VISIBLE);
		imgPicView.setVisibility(View.VISIBLE);
		imgPicView.setImageURI(uri);
		
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.height = AppUtil.getActualMeasure(this, 60);
		LinearLayout imgPreviewViewParent = (LinearLayout) imgPreviewView.getParent();
		imgPreviewViewParent.setLayoutParams(lp);
		imgPreviewViewParent.setPadding(AppUtil.getActualMeasure(this, 15), AppUtil.getActualMeasure(this, 10), 0, AppUtil.getActualMeasure(this, 10));
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		titleView.setText(R.string.PRODUCT_ADD_TITLE);

		time = AppUtil.getCurrentDateTime();
		timeView.setText(time);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		okBtnView = (Button) this.findViewById(R.id.okBtn);

		titleView = (TextView) findViewById(R.id.title);
		typeView = (TextView) findViewById(R.id.type);
		timeView = (TextView) findViewById(R.id.time);
		deletePicView = (TextView) findViewById(R.id.deletePic);
		
		imgPicView = (ImageView) findViewById(R.id.imgPic);

		nameView = (EditText) findViewById(R.id.name);
		priceView = (EditText) findViewById(R.id.price);
		countView = (EditText) findViewById(R.id.count);
		remarkView = (EditText) findViewById(R.id.remark);

		imgPreviewView = (LinearLayout) findViewById(R.id.imgPreview);
		defaultTxtView = (TextView) imgPreviewView.findViewById(R.id.defaultTxt);

		dateTimePicker = new BaseDateTimePicker(this);
		dateTimePicker.setTimeDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface di) {
				// TODO Auto-generated method stub
				int[] ymd = dateTimePicker.getYMD();
				int[] hms = dateTimePicker.getHMS();

				year = ymd[0];
				month = ymd[1];
				day = ymd[2];

				hour = hms[0];
				minute = hms[1];
				second = hms[2];

				time = year + "-" + AppUtil.to2bit(month) + "-"
						+ AppUtil.to2bit(day) + " " + AppUtil.to2bit(hour)
						+ ":" + AppUtil.to2bit(minute) + ":"
						+ AppUtil.to2bit(second);
				timeView.setText(time);
			}
		});

		baseBottomMenu = new BaseMenuBottom2Top(this);
		baseBottomMenu.setBtn1(R.string.COMMON_IMAGEPICKERGALLERY,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AppUtil.openGallery(UIProductAdd.this);
						baseBottomMenu.dismiss();
					}
				});
		baseBottomMenu.setBtn2(R.string.COMMON_IMAGEPICKERCAMMERA,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AppUtil.openCamera(UIProductAdd.this,
								C.DIRS.productDir, C.DIRS.productFileName);
						baseBottomMenu.dismiss();
					}
				});
	}

}