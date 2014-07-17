package com.jituofu.ui;

import android.os.Bundle;

import com.jituofu.R;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;

public class UICashierHBlist extends BaseUiAuth implements BaseUiFormBuilder{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier_hebinglist);
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
