package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.ui.UIParentType.CustomAdapter;

public class UIProductSearchList extends BaseUiAuth implements OnClickListener,
		BaseListViewListener, BaseUiFormBuilder {

	// 定义相关的view
	private LinearLayout topbarView, noDataView, againView;
	private TextView titleView, noDataAddTypeView;
	private BaseListView lv;
	private EditText searchView;
	private ImageView searchBtnView;

	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;// 是首次查询还是分页查询
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private int limit;
	private CustomAdapter customAdapter;

	private boolean isRefresh, isLoadMore, isLoading, isEditing;

	private ArrayList<String> productsId = new ArrayList<String>();// 存储所有商品的id，以免重复加载

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_products_searchlist);

		initView();
		updateView();
		onBind();
	}
	
	private void onBind(){
		searchBtnView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	private void initView(){
		topbarView = (LinearLayout) this.findViewById(R.id.topbar2);
		titleView = (TextView) topbarView.findViewById(R.id.title);
		
		searchView = (EditText) findViewById(R.id.search);
		searchBtnView = (ImageView) findViewById(R.id.searchBtn);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);

		lv = (BaseListView) this.findViewById(R.id.listView);
		lv.setPullLoadEnable(true);
		lv.setPullRefreshEnable(true);
		lv.setXListViewListener(this);

		noDataAddTypeView = (TextView) noDataView.findViewById(R.id.action_btn);
	}
	
	private void updateView(){
		titleView.setText(R.string.WAREHOUSE_SPSS);
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
