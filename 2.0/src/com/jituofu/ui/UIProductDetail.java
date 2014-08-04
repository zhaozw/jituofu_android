package com.jituofu.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

import com.jituofu.R;
import com.jituofu.base.BaseDateTimePicker;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMenuBottom2Top;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

public class UIProductDetail extends BaseUiAuth implements BaseUiBuilder,
		BaseUiFormBuilder {
	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;

	// view对象
	private TextView titleView, timeView, typeView, titleRightView;
	private EditText nameView, priceView, countView, remarkView;
	private Button deleteBtnView;
	private LinearLayout updateImgBtnView;
	private ImageView imgPicView;

	// 表单值
	private String name, price, count, remark, typeId, typeName, picPath, time,
			id;
	private int year, month, day, hour, minute, second;

	// 表单提交
	private boolean validated = false;

	private boolean isDeleted = false;

	public static int updateTypeRequestCode = 2;// 分类更新

	private BaseDateTimePicker dateTimePicker;
	private BaseMenuBottom2Top baseBottomMenu;

	private Uri imgPreviewUri;
	private BaseGetProductImageTask bpit;// 异步获取商品图片

	private boolean isEditing = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isEditing) {
			isEditing = false;
			updateFormStatus();
			return false;

		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_product_detail);

		prepare();

		AppUtil.showLoadingPopup(this, R.string.PRODUCT_DETAIL_QUERYLOADING);
		getProductInfo();

		onUpdate();
		onBind();
	}

	private void getProductInfo() {
		// TODO Auto-generated method stub
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("id", id);
		try {
			this.doTaskAsync(C.TASK.productquery, C.API.host
					+ C.API.productquery, urlParams);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			if (img != null) {
				String productDirPath = AppUtil.getExternalStorageDirectory()
						+ C.DIRS.rootdir + C.DIRS.productDir;
				String newFileName = productDirPath + "/"
						+ operation.getString("id") + ".png";
				img.renameTo(new File(newFileName));
				showUpdateResult(message);
			}
			picPath = null;
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();
		if (resultStatus == 100) {
			titleRightView.setVisibility(View.VISIBLE);
			
			if (taskId == C.TASK.productquery) {
				showProductInfo(operation);
			} else if (taskId == C.TASK.productupdate) {
				showUpdateResult(message);
			} else if (taskId == C.TASK.productsdelete) {
				isDeleted = true;
				showUpdateResult(message);
			}
		} else {
			// 更新商品信息失败时，标识编辑状态，让用户继续提交
			if (taskId == C.TASK.productupdate) {
				isEditing = true;
			}else if(taskId == C.TASK.productquery){
				titleRightView.setVisibility(View.GONE);
			}
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void showUpdateResult(BaseMessage message) throws Exception {
		updateFormStatus();

		baseDialogBuilder.setMessage(message.getMemo());
		baseDialog = baseDialogBuilder.create();
		baseDialog.setCanceledOnTouchOutside(false);
		baseDialog.show();
	}

	private void showProductInfo(JSONObject operation) throws JSONException {
		String pic = operation.getString("pic");
		String date = operation.getString("date");
		String[] dateSplit = date.split("\\s+");

		if (dateSplit != null) {
			String ymd = dateSplit[0];
			String hms = dateSplit[1];

			if (ymd != null) {
				String[] ymdSplit = ymd.split("-");
				if (ymdSplit != null) {
					this.year = Integer.parseInt(ymdSplit[0]);
					this.month = Integer.parseInt(ymdSplit[1]);
					this.day = Integer.parseInt(ymdSplit[2]);
				}
			}
			if (hms != null) {
				String[] hmsSplit = hms.split(":");
				if (hmsSplit != null) {
					this.hour = Integer.parseInt(hmsSplit[0]);
					this.minute = Integer.parseInt(hmsSplit[1]);
					this.second = Integer.parseInt(hmsSplit[2]);
				}
			}
		}

		String remark = operation.getString("remark");

		titleView.setText(operation.getString("name"));
		nameView.setText(operation.getString("name"));
		priceView.setText(operation.getString("price"));
		countView.setText(operation.getString("count"));
		timeView.setText(operation.getString("date"));
		remarkView.setText(remark != null && !remark.equals("null") && remark.length()>0 ? remark : "");

		typeId = operation.getString("typeId");
		time = operation.getString("date");
		remark = remark != null ? remark : "";
		name = operation.getString("name");

		JSONObject type = operation.getJSONObject("type");
		String typeName = "";
		if (type.has("child")) {
			typeName = type.getString("parent")
					+ this.getString(R.string.SPFL_SPEARATOR)
					+ type.getString("child");
		} else {
			typeName = type.getString("parent");
		}
		typeView.setText(typeName);

		if (pic != null) {
			bpit = new BaseGetProductImageTask(imgPicView, pic, id);
			bpit.execute();
		}

		if (pic == null || pic.length() <= 0) {
			imgPicView.setScaleType(ScaleType.CENTER);

			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
			lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
			lp.alignWithParent = true;
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
			lp.addRule(RelativeLayout.CENTER_VERTICAL);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			imgPicView.setLayoutParams(lp);
		}
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub
		AppUtil.showLoadingPopup(this, R.string.PRODUCT_DETAIL_UPDATINGLOADING);

		HashMap<String, String> urlParams = new HashMap<String, String>();

		String price = priceView.getText() + "";

		urlParams.put("id", id);
		urlParams.put("name", name);
		urlParams.put("count", count);
		urlParams.put("price", price);
		urlParams.put("type", typeId);
		urlParams.put("date", time);
		urlParams.put("remark", remark);
		if (picPath != null) {
			ArrayList<String> filesList = new ArrayList<String>();
			filesList.add(picPath);
			try {
				this.doUploadTaskAsync(C.TASK.productupdate, C.API.host
						+ C.API.productupdate, urlParams, filesList);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				this.doTaskAsync(C.TASK.productupdate, C.API.host
						+ C.API.productupdate, urlParams);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

		titleRightView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isEditing) {
					isEditing = false;
				} else {
					isEditing = true;
				}

				if (isEditing) {
					updateFormStatus();
				}

				if (!isEditing) {
					beforeSubmit();
				}
			}
		});
		((LinearLayout) timeView.getParent())
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!isEditing) {
							return;
						}
						if (year > 0 && month > 0 && day > 0) {
							if (hour > 0 && minute > 0) {
								dateTimePicker.setHMS(hour, minute, second);
							}
							dateTimePicker.showDateDialog(year, month, day);
						} else {
							dateTimePicker.showDateDialog();
						}
					}
				});
		((LinearLayout) typeView.getParent())
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (!isEditing) {
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("from", C.COMMON.productEdit);
						forwardForResult(UIParentType.class,
								updateTypeRequestCode, bundle);
					}
				});
		updateImgBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				baseBottomMenu.showAtLocation(
						UIProductDetail.this.findViewById(R.id.imageView),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			}
		});
		deleteBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String mes = "您确认删除 " + name;
				showConfirmDialog(mes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						doDeleteTask();
					}

				}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}

				});
			}
		});
	}

	private void doDeleteTask() {
		AppUtil.showLoadingPopup(this, R.string.PRODUCT_DETAIL_DELETINGLOADING);

		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("id", id);

		try {
			this.doTaskAsync(C.TASK.productsdelete, C.API.host
					+ C.API.productsdelete, urlParams);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 选择分类回来
		if (requestCode == updateTypeRequestCode) {
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
		} else if (requestCode == C.COMMON.camera) {
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
				String fileName = productDirPath + "/"
						+ AppUtil.getCurrentTime() + ".png";
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
				imgPreviewUri = Uri.parse(fileName);
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
				String fileName = productDirPath + "/"
						+ AppUtil.getCurrentTime() + ".png";

				try {
					fos = new FileOutputStream(fileName);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

					picPath = fileName;
					imgPreviewUri = Uri.parse(fileName);
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

		if (imgPreviewUri != null) {
			imgPicView.setImageURI(imgPreviewUri);
		}
	}

	private void updateFormStatus() {
		if (isEditing) {
			titleRightView.setText(R.string.COMMON_COMPLETETXT);
			updateImgBtnView.setVisibility(View.VISIBLE);
			deleteBtnView.setVisibility(View.VISIBLE);

			// 开启输入
			nameView.setFocusableInTouchMode(true);
			priceView.setFocusableInTouchMode(true);
			countView.setFocusableInTouchMode(true);
			remarkView.setFocusableInTouchMode(true);
		} else {
			titleRightView.setText(R.string.COMMON_EDITTXT);
			updateImgBtnView.setVisibility(View.GONE);
			deleteBtnView.setVisibility(View.GONE);

			// 关闭输入
			nameView.setFocusableInTouchMode(false);
			nameView.setFocusable(false);
			priceView.setFocusableInTouchMode(false);
			priceView.setFocusable(false);
			countView.setFocusableInTouchMode(false);
			countView.setFocusable(false);
			remarkView.setFocusableInTouchMode(false);
			remarkView.setFocusable(false);

			// 隐藏键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(titleRightView.getWindowToken(), 0);
		}
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		titleRightView.setText(R.string.COMMON_EDITTXT);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		deleteBtnView = (Button) this.findViewById(R.id.deleteBtn);

		titleView = (TextView) findViewById(R.id.title);
		titleRightView = (TextView) findViewById(R.id.topBar2Right);
		typeView = (TextView) findViewById(R.id.type);
		timeView = (TextView) findViewById(R.id.time);

		imgPicView = (ImageView) findViewById(R.id.imageView);

		nameView = (EditText) findViewById(R.id.name);
		priceView = (EditText) findViewById(R.id.price);
		countView = (EditText) findViewById(R.id.count);
		remarkView = (EditText) findViewById(R.id.remark);

		updateImgBtnView = (LinearLayout) findViewById(R.id.updateImgBtn);

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
						AppUtil.openGallery(UIProductDetail.this);
						baseBottomMenu.dismiss();
					}
				});
		baseBottomMenu.setBtn2(R.string.COMMON_IMAGEPICKERCAMMERA,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AppUtil.openCamera(UIProductDetail.this,
								C.DIRS.productDir, C.DIRS.productFileName);
						baseBottomMenu.dismiss();
					}
				});

		baseDialogBuilder = new BaseDialog.Builder(this);
		baseDialogBuilder.setNegativeButton(R.string.COMMON_IKNOW,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// TODO Auto-generated method stub
						baseDialog.dismiss();
						if (isDeleted) {
							finish();
						}
					}
				});

		Bundle extraBundle = this.getIntent().getExtras();
		id = extraBundle.getString("id");
	}
}
