package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.util.AppUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UICashier extends BaseUiAuth implements BaseUiFormBuilder {
	// 表单提交
	private boolean validated = false;

	private LinearLayout hbView, productpreviewView;
	private TextView gotohblbBtnView;
	private EditText nameView, priceView, sellingCountView, sellingPriceView,
			remarkView;

	// 表单值
	private String name, price, sellingCount, sellingPrice, remark;

	// 合并记账列表
	public static ArrayList<HashMap<String, String>> hbList;
	// 当前销售的商品
	private HashMap<String, String> currentProduct;

	private BaseGetProductImageTask bpit;// 异步获取商品图片

	private int sysVersion = Build.VERSION.SDK_INT;
	private ImageButton cangkuBtnView;
	public static int TAG = 110;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier);

		hbList = new ArrayList<HashMap<String, String>>();

		initView();
		updateView();
		onBind();
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
		}else{
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
	}

	private void onBind() {
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

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentProduct != null && validation()) {
					currentProduct.put("name", name);
					currentProduct.put("sellingCount", sellingCount);
					currentProduct.put("price", price);
					currentProduct.put("sellingPrice", sellingPrice);
					currentProduct.put("remark", remark);

					if (hbList.indexOf(currentProduct) < 0) {
						hbList.add(currentProduct);
					}

					Log.w("JZB", hbList.toString());
				}
			}
		});
	}

	private void initView() {
		cangkuBtnView = (ImageButton) this.findViewById(R.id.cangku);
		hbView = (LinearLayout) findViewById(R.id.hb);
		gotohblbBtnView = (TextView) findViewById(R.id.add2hebinglist);
		productpreviewView = (LinearLayout) findViewById(R.id.productpreview);

		nameView = (EditText) findViewById(R.id.name);
		priceView = (EditText) findViewById(R.id.jjprice);
		sellingCountView = (EditText) findViewById(R.id.sellingcount);
		sellingPriceView = (EditText) findViewById(R.id.sellingprice);
		remarkView = (EditText) findViewById(R.id.remark);
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub

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
		sellingCount = AppUtil.trimAll(getEditValue(sellingCountView));
		sellingPrice = AppUtil.trimAll(getEditValue(sellingPriceView));
		remark = getEditValue(remarkView);
		
		if(remark == null){
			remark = "";
		}

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
			if (sellingCount == null) {
				this.showToast(R.string.CASHIER_SELLINGCOUNT_HINT);
				result = false;
			} else if (!AppUtil.isAvailablePrice(sellingCount)) {
				this.showToast(R.string.CASHIER_SELLINGCOUNT_ERROR);
				result = false;
			} else {
				double dp = Double.parseDouble(sellingCount);
				sellingCount = AppUtil.toFixed(dp) + "";
				sellingCountView.setText(sellingCount);
			}
		}

		if (result) {
			if (sellingPrice == null) {
				this.showToast(R.string.CASHIER_SELLINGPRICE_HINT);
				result = false;
			} else if (!AppUtil.isAvailablePrice(sellingPrice)) {
				this.showToast(R.string.CASHIER_SELLINGPRICE_ERROR);
				result = false;
			} else {
				double dp = Double.parseDouble(sellingPrice);
				sellingPrice = AppUtil.toFixed(dp) + "";
				sellingPriceView.setText(sellingPrice);
			}
		}

		return result;
	}
}