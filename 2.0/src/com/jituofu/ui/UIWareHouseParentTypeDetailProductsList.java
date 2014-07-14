package com.jituofu.ui;

import android.os.Bundle;

import com.jituofu.R;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseListView.BaseListViewListener;

public class UIWareHouseParentTypeDetailProductsList extends BaseUiAuth implements
BaseListViewListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_warehouse_parenttype_detail_productlist);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}

}
