package com.jituofu.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
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
	}

	private void updateView() throws JSONException {
		if (detail != null) {
			titleView.setText(detail.getString("name"));
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
