package com.jituofu.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseUiAuth;

public class UIProductManager extends BaseUiAuth implements OnClickListener {
	private LinearLayout ckglView, flglView, sprkView;
	private TextView titleView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_spgl);
		
		initView();
		updateView();
		bindListener2View();
	}
	
	private void bindListener2View(){
		this.onCustomBack();
		this.ckglView.setOnClickListener(this);
		this.flglView.setOnClickListener(this);
		this.sprkView.setOnClickListener(this);
	}

	private void updateView() {
		titleView.setText(R.string.SPGL_TITLE);
		((TextView) ckglView.findViewById(R.id.txt))
				.setText(R.string.SPGL_MENU1_TXT1);
		((TextView) flglView.findViewById(R.id.txt))
				.setText(R.string.SPGL_MENU1_TXT2);
		((TextView) sprkView.findViewById(R.id.txt))
				.setText(R.string.SPGL_MENU1_TXT3);
		((LinearLayout) sprkView.findViewById(R.id.border))
				.setVisibility(View.GONE);
	}

	private void initView() {
		ckglView = (LinearLayout) this.findViewById(R.id.ckgl);
		flglView = (LinearLayout) this.findViewById(R.id.flgl);
		sprkView = (LinearLayout) this.findViewById(R.id.sprk);

		titleView = (TextView) this.findViewById(R.id.title);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.flgl:
			this.forward(UIParentType.class);
			break;
		case R.id.sprk:
			this.forward(UIProductAdd.class);
			break;
		}
	}
}
