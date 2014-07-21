package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseUiAuth;

public class UICashierSuccess extends BaseUiAuth{
	private TextView titleView, xseView, xsspView, xsslView, xsrqView;
	
	private String productNum, totalSellingCount, totalSellingPrice, date;
	
	private Bundle extraBundle;
	
	private void sendDeleteBroadCase(){
		Intent broadcast = new Intent();
		broadcast.setAction("com.jituofu.ui.ClearForm");
		broadcast.putExtra("type", "ClearForm");
		this.sendBroadcast(broadcast);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier_success);

		sendDeleteBroadCase();
		
		initView();
		updateView();
		onBind();
	}
	
	private void onBind(){
		this.onCustomBack();
	}
	
	private void updateView(){
		titleView.setText(R.string.CASHIER_JZCG);
		
		if(extraBundle != null){
			productNum = extraBundle.getString("productNum");
			totalSellingCount = extraBundle.getString("totalSellingCount");
			totalSellingPrice = extraBundle.getString("totalSellingPrice");
			date = extraBundle.getString("date");
			
			xseView.setText(totalSellingPrice+" 元");
			xsspView.setText(productNum+" 种");
			xsslView.setText(totalSellingCount);
			xsrqView.setText(date);
		}
	}
	
	private void initView(){
		titleView = (TextView) this.findViewById(R.id.title);
		xseView = (TextView) this.findViewById(R.id.xse);
		xsspView = (TextView) this.findViewById(R.id.xssp);
		xsslView = (TextView) this.findViewById(R.id.xssl);
		xsrqView = (TextView) this.findViewById(R.id.xsrq);
		
		extraBundle = this.getIntent().getExtras();
	}
}
