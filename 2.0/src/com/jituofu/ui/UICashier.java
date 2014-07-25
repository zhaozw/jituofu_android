package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseDateTimePicker;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class UICashier extends BaseUiAuth implements BaseUiFormBuilder {
	static BaseDialog.Builder baseDialogBuilder;
	static BaseDialog baseDialog;

	// 表单提交
	private boolean validated = false;

	private LinearLayout productpreviewView;
	private TextView topBar2RightView, gotojrxsView;
	private EditText nameView, priceView, sellingCountView, sellingPriceView,
			remarkView;
	private RelativeLayout flyView, hbView;
	private Button okBtnView, gotohblbBtnView;

	// 表单值
	private static String name, price, sellingCount, sellingPrice, remark,
			date;

	// 合并记账列表
	public static ArrayList<HashMap<String, String>> hbList;
	// 当前销售的商品
	private HashMap<String, String> currentProduct;

	private BaseGetProductImageTask bpit;// 异步获取商品图片

	private int sysVersion = Build.VERSION.SDK_INT;
	private ImageButton cangkuBtnView;
	public static int TAG = 110;

	// 飞动画
	Runnable flyRunnable;
	private int fromX, fromY;
	private boolean isFly = false;

	private static BaseDateTimePicker dateTimePicker;
	private static int year, month, day, hour, minute, second;
	private static TextView dateView;
	private static String time;

	protected void onBrReceive(String type) {
		if (type.equals("ClearForm")) {
			resetForm();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateHBListCount();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier);

		IntentFilter filter = new IntentFilter();
		filter.addAction("com.jituofu.ui.ClearForm");
		this.registerReceiver(br, filter);
		
		hbList = new ArrayList<HashMap<String, String>>();

		initView();
		updateView();
		onBind();
	}

	public static void showPreview(final BaseUi context) {
		dateTimePicker = new BaseDateTimePicker(context);
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
				if (dateView != null) {
					dateView.setText(time);
				}
			}
		});
		
		double totalPrice = 0;
		double totalCount = 0;

		LinearLayout view = (LinearLayout) LinearLayout.inflate(context,
				R.layout.template_cashier_preview, null);
		LinearLayout productsBoxView = (LinearLayout) view
				.findViewById(R.id.productBox);
		dateView = (TextView) view.findViewById(R.id.date);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				AppUtil.getActualMeasure(context, 100),
				AppUtil.getActualMeasure(context, 130));
		lp.setMargins(0, 0, AppUtil.getActualMeasure(context, 30), 0);
		for (int i = 0; i < hbList.size(); i++) {
			RelativeLayout productItemView = (RelativeLayout) RelativeLayout
					.inflate(context,
							R.layout.template_cashier_preview_product_item,
							null);
			productItemView.setLayoutParams(lp);
			HashMap<String, String> map = (HashMap<String, String>) UICashier.hbList
					.get(i);

			ImageView picView = (ImageView) productItemView
					.findViewById(R.id.pic);
			TextView nameView = (TextView) productItemView
					.findViewById(R.id.name);
			TextView sellingcountView = (TextView) productItemView
					.findViewById(R.id.sellingcount);

			sellingcountView.setText("数量：" + map.get("sellingCount"));
			nameView.setText(map.get("name"));
			picView.setImageURI(null);
			picView.setBackgroundResource(R.drawable.default_img_placeholder);

			if (map.get("pid") != null) {
				BaseGetProductImageTask bpit = new BaseGetProductImageTask(
						picView, map.get("pic"), map.get("pid"));
				bpit.execute();
			}
			productsBoxView.addView(productItemView);

			double sellingPrice = Double.parseDouble(map.get("sellingPrice"));
			double sellingCount = Double.parseDouble(map.get("sellingCount"));

			totalPrice += Double.parseDouble(AppUtil.toFixed(sellingCount
					* sellingPrice));
			totalCount += sellingCount;
		}

		((TextView) view.findViewById(R.id.product_num)).setText(hbList.size()
				+ " 种商品");
		((TextView) view.findViewById(R.id.count)).setText("总数量 "
				+ AppUtil.toFixed(totalCount));
		((TextView) view.findViewById(R.id.xse)).setText(AppUtil
				.toFixed(totalPrice) + " 元");
		time = AppUtil.getCurrentDateTime();
		dateView.setText(time);
		dateView.setOnClickListener(null);
		dateView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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

		baseDialogBuilder = new BaseDialog.Builder(context);
		baseDialogBuilder.setContentView(view);

		baseDialogBuilder.setPositiveButton(R.string.COMMON_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// TODO Auto-generated method stub
						doSubmit(context);
					}
				});
		baseDialogBuilder.setNegativeButton(R.string.COMMON_CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						baseDialog.dismiss();
					}
				});
		baseDialog = baseDialogBuilder.create();
		baseDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == UICashier.TAG) {
			String product = data.getStringExtra("data");

			remarkView.setText("");
			sellingPriceView.setText("");
			sellingCountView.setText("");

			if (product != null) {
				JSONObject productData;
				try {
					productData = new JSONObject(product);

					String id = productData.getString("id");
					String name = productData.getString("name");
					String count = productData.getString("count");
					String price = productData.getString("price");
					String pic = productData.getString("pic");

					currentProduct = new HashMap<String, String>();
					currentProduct.clear();

					currentProduct.put("pid", id);
					currentProduct.put("name", name);
					currentProduct.put("count", count);
					currentProduct.put("price", price);
					currentProduct.put("pic", pic);

					nameView.setText(name);
					priceView.setText(price);

					disableEditText();
					showProductPreivew(currentProduct);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	private void showProductPreivew(HashMap<String, String> currentProduct) {
		productpreviewView.setVisibility(View.VISIBLE);
		ImageView pic = (ImageView) productpreviewView
				.findViewById(R.id.productpreview_img);
		TextView count = (TextView) productpreviewView.findViewById(R.id.count);
		TextView price = (TextView) productpreviewView.findViewById(R.id.price);

		if (currentProduct.get("pic") != null
				&& currentProduct.get("pic").length() > 0) {
			bpit = new BaseGetProductImageTask(pic, currentProduct.get("pic"),
					currentProduct.get("pid"));
			bpit.execute();
		} else {
			pic.setImageURI(null);
			pic.setBackgroundResource(R.drawable.default_img_placeholder);
		}

		count.setText("库存：" + currentProduct.get("count"));
		price.setText("进价：" + currentProduct.get("price") + " 元");

		if (Double.parseDouble(currentProduct.get("count")) <= 0) {
			this.showToast(R.string.CASHIER_COUNT_ERROR);
		}
	}

	private void disableEditText() {
		nameView.setFocusableInTouchMode(false);
		nameView.setFocusable(false);

		priceView.setFocusableInTouchMode(false);
		priceView.setFocusable(false);
	}

	private void enableEditText() {
		nameView.setFocusableInTouchMode(true);
		priceView.setFocusableInTouchMode(true);
	}

	private void updateView() {
		if (sysVersion < 16) {
			LinearLayout topBarView = (LinearLayout) findViewById(R.id.cashier_topbar);
			topBarView.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.sdk8))
					.setVisibility(View.VISIBLE);
		}

		ViewTreeObserver vto = gotohblbBtnView.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				// TODO Auto-generated method stub
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						AppUtil.getActualMeasure(getApplicationContext(), 30),
						AppUtil.getActualMeasure(getApplicationContext(), 30));

				int[] gotohblbBtnViewLocation = new int[2];

				gotohblbBtnView.getLocationOnScreen(gotohblbBtnViewLocation);
				if (sysVersion >= 16) {
					fromX = gotohblbBtnViewLocation[0]
							+ gotohblbBtnView.getWidth()
							- AppUtil.getActualMeasure(getApplicationContext(),
									30);
					fromY = gotohblbBtnViewLocation[1]
							- AppUtil.getActualMeasure(getApplicationContext(),
									30);
				} else {
					fromX = gotohblbBtnViewLocation[0]
							+ gotohblbBtnView.getWidth() / 2;
					fromY = gotohblbBtnViewLocation[1]
							- AppUtil.getActualMeasure(getApplicationContext(),
									80);
				}
				lp.setMargins(fromX, fromY, 0, 0);
				flyView.setLayoutParams(lp);
				return true;
			}
		});

	}

	private void onBind() {
		gotojrxsView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forwardForResult(UICashierToday.class, TAG);
			}
		});
		topBar2RightView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forwardForResult(UICashierToday.class, TAG);
			}
		});
		cangkuBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forwardForResult(UICashierProductSearch.class, TAG);
			}
		});

		hbView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forwardForResult(UICashierHBlist.class, TAG);
			}
		});
		((TextView) findViewById(R.id.gotohblb))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						forwardForResult(UICashierHBlist.class, TAG);
					}
				});

		gotohblbBtnView.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (validation(true)) {
					if (currentProduct != null) {
						currentProduct.put("name", name);
						currentProduct.put("sellingCount", sellingCount);
						currentProduct.put("price", price);
						currentProduct.put("sellingPrice", sellingPrice);
						currentProduct.put("remark", remark);
					} else {
						currentProduct = new HashMap<String, String>();
						currentProduct.put("name", name);
						currentProduct.put("sellingCount", sellingCount);
						currentProduct.put("price", price);
						currentProduct.put("sellingPrice", sellingPrice);
						currentProduct.put("remark", remark);
					}

					if (hbList.indexOf(currentProduct) < 0) {
						hbList.add(currentProduct);
						fly();
					}else{
						fly();
					}
				}
			}
		});
		okBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beforeSubmit();
			}
		});
	}

	public void updateHBListCount() {
		TextView hblistCountView = (TextView) findViewById(R.id.hblistcount);

		if (hblistCountView == null) {
			return;
		}

		if (hbList.size() <= 0) {
			hblistCountView.setVisibility(View.GONE);
		} else if (hbList.size() < 100) {
			hblistCountView.setVisibility(View.VISIBLE);
			hblistCountView.setText(hbList.size() + "");
		} else {
			hblistCountView.setVisibility(View.VISIBLE);
			hblistCountView.setText("...");
		}
	}

	private void fly() {
		if (isFly) {
			return;
		}

		flyView.setVisibility(View.VISIBLE);

		int[] flyViewLocation = new int[2];
		flyView.getLocationOnScreen(flyViewLocation);
		int[] hbViewLocation = new int[2];
		Animation am;

		if (sysVersion >= 16) {

			hbView.findViewById(R.id.hbicon)
					.getLocationOnScreen(hbViewLocation);

			int toX = AppUtil.getActualMeasure(getApplicationContext(), 15);
			int toY = hbViewLocation[1];
			am = new TranslateAnimation(0,
					-(fromX - toX - AppUtil.getActualMeasure(
							getApplicationContext(), 21)), 0, -(fromY - (toY
							- AppUtil.getActualMeasure(getApplicationContext(),
									15) - AppUtil.getActualMeasure(
							getApplicationContext(), 21))));
		} else {
			((TextView) findViewById(R.id.gotohblb))
					.getLocationOnScreen(hbViewLocation);

			int toX = AppUtil.getActualMeasure(getApplicationContext(), 15);
			int toY = hbViewLocation[1]
					- AppUtil.getActualMeasure(getApplicationContext(), 15);
			am = new TranslateAnimation(0, (toX - fromX), 0, (toY - fromY));
		}

		am.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation am) {
				// TODO Auto-generated method stub
				flyView.setVisibility(View.GONE);
				resetForm();
				isFly = false;
				updateHBListCount();
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				isFly = true;
			}
		});
		am.setDuration(500);
		flyView.setAnimation(am);
		flyView.startAnimation(am);
	}

	private void resetForm() {
		currentProduct = null;
		productpreviewView.setVisibility(View.GONE);
		nameView.setText("");
		priceView.setText("");
		sellingCountView.setText("");
		sellingPriceView.setText("");
		remarkView.setText("");

		name = null;
		price = null;
		sellingCount = null;
		sellingPrice = null;
		date = null;
		remark = null;

		enableEditText();
	}

	private void initView() {
		cangkuBtnView = (ImageButton) this.findViewById(R.id.cangku);
		hbView = (RelativeLayout) findViewById(R.id.hb);
		gotohblbBtnView = (Button) findViewById(R.id.add2hebinglist);
		okBtnView = (Button) findViewById(R.id.okBtn);
		productpreviewView = (LinearLayout) findViewById(R.id.productpreview);
		topBar2RightView = (TextView) findViewById(R.id.topBar2Right);

		nameView = (EditText) findViewById(R.id.name);
		priceView = (EditText) findViewById(R.id.jjprice);
		sellingCountView = (EditText) findViewById(R.id.sellingcount);
		sellingPriceView = (EditText) findViewById(R.id.sellingprice);
		remarkView = (EditText) findViewById(R.id.remark);

		flyView = (RelativeLayout) findViewById(R.id.fly);
		
		gotojrxsView = (TextView) findViewById(R.id.gotojrxs);
	}

	public static void doSubmit(BaseUi ui) {
		HashMap<String, String> urlParams = new HashMap<String, String>();
		JSONArray list = new JSONArray();
		JSONObject sale = new JSONObject();

		if (hbList.size() <= 0) {
			try {
				sale.put("price", price);
				sale.put("sellingPrice", sellingPrice);
				sale.put("sellingCount", sellingCount);
				sale.put("name", name);
				sale.put("remark", remark);

				list.put(sale);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < hbList.size(); i++) {
				HashMap<String, String> map = hbList.get(i);
				JSONObject saleObj = new JSONObject();
				try {
					if (map.get("pid") != null) {
						saleObj.put("pid", map.get("pid"));
					}
					saleObj.put("price", map.get("price"));
					saleObj.put("sellingPrice", map.get("sellingPrice"));
					saleObj.put("sellingCount", map.get("sellingCount"));
					saleObj.put("name", map.get("name"));
					saleObj.put("remark", map.get("remark"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				list.put(saleObj);
			}
		}
		AppUtil.showLoadingPopup(ui, R.string.CASHIER_JZ_LOADING);

		urlParams.put("list", list.toString());
		urlParams.put("date", time);

		try {
			ui.doTaskAsync(C.TASK.cashiercreate, C.API.host
					+ C.API.cashiercreate, urlParams);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();
		if (resultStatus == 100) {
			submitSuccess(this, operation);
			resetForm();
			updateHBListCount();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	public static void submitSuccess(BaseUi ui, JSONObject operation) {
		Bundle bundle = new Bundle();
		bundle.putString("productNum", hbList.size() + "");

		double totalPrice = 0;
		double totalCount = 0;

		for (int i = 0; i < hbList.size(); i++) {
			HashMap<String, String> map = (HashMap<String, String>) UICashier.hbList
					.get(i);

			double sellingPrice = Double.parseDouble(map.get("sellingPrice"));
			double sellingCount = Double.parseDouble(map.get("sellingCount"));

			totalPrice += Double.parseDouble(AppUtil.toFixed(sellingCount
					* sellingPrice));
			totalCount += sellingCount;
		}
		bundle.putString("totalSellingCount", AppUtil.toFixed(totalCount));
		bundle.putString("totalSellingPrice", AppUtil.toFixed(totalPrice));
		bundle.putString("date", time);

		ui.forward(UICashierSuccess.class, bundle);

		hbList.clear();
		baseDialog.dismiss();
	}

	@Override
	public void beforeSubmit() {
		// TODO Auto-generated method stub
		// 防止重复点击
		if (validated) {
			return;
		}

		validated = true;

		if (hbList.size() > 0) {
			if (validation(false)) {
				addProduct2HBlist();
			}
			showPreview(this);
		} else {
			if (validation(true)) {
				addProduct2HBlist();
				showPreview(this);
			}
		}
		validated = false;
	}

	private void addProduct2HBlist() {
		if (currentProduct != null) {
			currentProduct.put("name", name);
			currentProduct.put("sellingCount", sellingCount);
			currentProduct.put("price", price);
			currentProduct.put("sellingPrice", sellingPrice);
			currentProduct.put("remark", remark);
		} else {
			currentProduct = new HashMap<String, String>();
			currentProduct.put("name", name);
			currentProduct.put("sellingCount", sellingCount);
			currentProduct.put("price", price);
			currentProduct.put("sellingPrice", sellingPrice);
			currentProduct.put("remark", remark);
		}

		if (hbList.indexOf(currentProduct) < 0) {
			hbList.add(currentProduct);
		}
	}

	public boolean validation(boolean isShowToast) {
		// TODO Auto-generated method stub
		boolean result = false;

		name = AppUtil.trimAll(getEditValue(nameView));
		price = AppUtil.trimAll(getEditValue(priceView));
		sellingCount = AppUtil.trimAll(getEditValue(sellingCountView));
		sellingPrice = AppUtil.trimAll(getEditValue(sellingPriceView));
		remark = getEditValue(remarkView);

		if (remark == null) {
			remark = "";
		}

		if (name == null) {
			if (isShowToast) {
				this.showToast(R.string.PRODUCT_PNAME_HINT);
			}
		} else if (AppUtil.getStrLen(name) < 2 || AppUtil.getStrLen(name) > 15) {
			if (isShowToast) {
				this.showToast(R.string.PRODUCT_PNAME_ERROR);
			}
		} else {
			result = true;
		}

		if (result) {
			if (price == null) {
				if (isShowToast) {
					this.showToast(R.string.PRODUCT_PPRICE_HINT);
				}
				result = false;
			} else if (!AppUtil.isAvailablePrice(price)) {
				if (isShowToast) {
					this.showToast(R.string.PRODUCT_PPRICE_ERROR);
				}
				result = false;
			} else {
				double dp = Double.parseDouble(price);
				priceView.setText(AppUtil.toFixed(dp));
			}
		}

		if (result) {
			if (sellingCount == null) {
				if (isShowToast) {
					this.showToast(R.string.CASHIER_SELLINGCOUNT_HINT);
				}
				result = false;
			} else if (!AppUtil.isAvailablePrice(sellingCount)) {
				if (isShowToast) {
					this.showToast(R.string.CASHIER_SELLINGCOUNT_ERROR);
				}
				result = false;
			} else {
				double dp = Double.parseDouble(sellingCount);
				sellingCount = AppUtil.toFixed(dp) + "";
				sellingCountView.setText(sellingCount);
			}
		}

		if (result) {
			if (sellingPrice == null) {
				if (isShowToast) {
					this.showToast(R.string.CASHIER_SELLINGPRICE_HINT);
				}
				result = false;
			} else if (!AppUtil.isAvailablePrice(sellingPrice)) {
				if (isShowToast) {
					this.showToast(R.string.CASHIER_SELLINGPRICE_ERROR);
				}
				result = false;
			} else {
				double dp = Double.parseDouble(sellingPrice);
				sellingPrice = AppUtil.toFixed(dp) + "";
				sellingPriceView.setText(sellingPrice);
			}
		}

		return result;
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub
		doSubmit(this);
	}

	@Override
	public boolean validation() {
		// TODO Auto-generated method stub
		return false;
	}
}