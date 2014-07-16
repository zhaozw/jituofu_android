package com.jituofu.ui;

import com.jituofu.R;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class UICashier extends BaseUiAuth implements BaseUiFormBuilder {
	private int sysVersion = Build.VERSION.SDK_INT;
	private ImageButton cangkuBtnView;
	public static int TAG = 110;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier);

		initView();
		updateView();
		onBind();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == UICashier.TAG) {
			String id = data.getStringExtra("id");
			Log.w("JZB", id);
		}
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
	}

	private void initView() {
		cangkuBtnView = (ImageButton) this.findViewById(R.id.cangku);
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeSubmit() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validation() {
		// TODO Auto-generated method stub
		return false;
	}
}