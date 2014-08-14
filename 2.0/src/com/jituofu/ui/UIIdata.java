package com.jituofu.ui;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiAuth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UIIdata extends BaseUiAuth {
	private TextView titleView;
	private LinearLayout topBarView;

	private int sysVersion = Build.VERSION.SDK_INT;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			UIGlobalTabView.tabHost.setCurrentTab(1);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idata);

		initView();

		if (sysVersion < 16) {
			topBarView.setVisibility(View.GONE);
		}
		updateView();
	}

	private void updateView() {
		titleView.setText(R.string.IDATA_TITLE);
	}

	private void initView() {
		topBarView = (LinearLayout) this.findViewById(R.id.topbar2);
		titleView = (TextView) findViewById(R.id.title);

		((LinearLayout) topBarView.findViewById(R.id.back))
				.setVisibility(View.GONE);
	}
}