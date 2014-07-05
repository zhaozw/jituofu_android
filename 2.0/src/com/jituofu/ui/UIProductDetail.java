package com.jituofu.ui;

import android.os.Bundle;

import com.jituofu.R;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;

public class UIProductDetail extends BaseUi implements BaseUiBuilder,
BaseUiFormBuilder{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_product_detail);

		prepare();
		onUpdate();
		onBind();
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

	@Override
	public void onBind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		
	}
}
