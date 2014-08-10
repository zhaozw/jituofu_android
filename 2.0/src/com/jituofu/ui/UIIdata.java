package com.jituofu.ui;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseUi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UIIdata extends BaseUi{
	private TextView titleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.idata);
		
		initView();
		updateView();
	}
	
	private void updateView(){
		titleView.setText(R.string.IDATA_TITLE);
	}
	
	private void initView() {
		titleView = (TextView) findViewById(R.id.title);
	}
}