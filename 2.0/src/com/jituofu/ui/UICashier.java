package com.jituofu.ui;

import com.jituofu.R;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class UICashier extends BaseUiAuth implements BaseUiFormBuilder {
	private ImageButton cangkuBtnView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier);
		
		initView();
		onBind();
	}
	
	private void onBind(){
		cangkuBtnView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	private void initView(){
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