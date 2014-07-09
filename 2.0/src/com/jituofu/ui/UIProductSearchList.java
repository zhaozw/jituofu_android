package com.jituofu.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.BaseListView.BaseListViewListener;

public class UIProductSearchList extends BaseUiAuth implements OnClickListener,
		BaseListViewListener, BaseUiFormBuilder {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_products_searchlist);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

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
