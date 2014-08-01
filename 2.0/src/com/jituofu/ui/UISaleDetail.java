package com.jituofu.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.jituofu.R;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.util.AppUtil;

public class UISaleDetail extends BaseUiAuth {
	private TextView titleView, sellingCountView, typeView, sellingPrice,
			dateView, hjView, remarkView;
	private Button thBtnView;
	private ImageView picView;

	private Bundle extraBundle;
	private String id;
	private JSONObject detail;
	
	private BaseGetProductImageTask bpit;// 异步获取商品图片

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_sale_detail);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			id = extraBundle.getString("id");
			try {
				detail = new JSONObject(extraBundle.getString("detail"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		initView();
		try {
			updateView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		onBind();
	}
	
	private void onBind(){
		this.onCustomBack();
	}

	private void updateView() throws JSONException {
		if (detail != null) {
			titleView.setText(detail.getString("name"));
			
			//商品图片处理
			String pic = detail.getString("pic");
			if (pic != null && pic.length()>0) {
				bpit = new BaseGetProductImageTask(picView, pic, detail.getString("pid"));
				bpit.execute();
			}else{
				picView.setScaleType(ScaleType.CENTER);

				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
				lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
				lp.alignWithParent = true;
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				lp.addRule(RelativeLayout.CENTER_VERTICAL);
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				picView.setLayoutParams(lp);
				picView.setImageDrawable(this.getResources().getDrawable(R.drawable.default_img_placeholder));
			}
			
			try{
				JSONObject type = detail.getJSONObject("typeName");
				String typeName = "";
				if (type.has("child")) {
					typeName = type.getString("parent")
							+ this.getString(R.string.SPFL_SPEARATOR)
							+ type.getString("child");
				} else {
					typeName = type.getString("parent");
				}
				typeView.setText(typeName);
			}catch(JSONException e){
				//没有分类数据的话，就隐藏分类
				((LinearLayout) typeView.getParent().getParent()).setVisibility(View.GONE);
			}
			dateView.setText(detail.getString("date"));
			remarkView.setText(detail.getString("remark"));
			sellingCountView.setText(detail.getString("selling_count"));
			sellingPrice.setText(AppUtil.toFixed(Double.parseDouble(detail.getString("selling_price"))));
			Double hj = Double.parseDouble(detail.getString("selling_count")) * Double.parseDouble(detail.getString("selling_price"));
			hjView.setText(AppUtil.toFixed(hj));
		}
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		sellingCountView = (TextView) this.findViewById(R.id.selling_count);
		sellingPrice = (TextView) this.findViewById(R.id.selling_price);
		dateView = (TextView) this.findViewById(R.id.date);
		hjView = (TextView) this.findViewById(R.id.hj);
		remarkView = (TextView) this.findViewById(R.id.remark);
		typeView = (TextView) this.findViewById(R.id.type);

		thBtnView = (Button) this.findViewById(R.id.thBtn);
		picView = (ImageView) this.findViewById(R.id.imageView);
	}
}
