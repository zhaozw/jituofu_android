package com.jituofu.ui;

import com.jituofu.R;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class UICashier extends BaseUiAuth implements BaseUiFormBuilder {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier);
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