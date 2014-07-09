package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.ui.UIParentType.CustomAdapter;
import com.jituofu.util.AppUtil;

public class UIWareHouse extends BaseUiAuth implements OnClickListener,
		BaseListViewListener {
	//定义相关的view
	private LinearLayout topbarView, noDataView, againView;
	private ImageButton addView, editView;
	private TextView titleView, noDataAddTypeView;
	private BaseListView lv;
	private EditText searchView;

	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;// 是首次查询还是分页查询
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private int limit;
	private CustomAdapter customAdapter;

	private boolean isRefresh, isLoadMore, isLoading, isEditing;

	private ArrayList<String> typesId = new ArrayList<String>();// 存储所有分类的id，以免重复加载

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_warehouse);

		initView();
		updateView();
		onBind();
	}
	
	private void onBind(){
		searchView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forward(UIProductSearchList.class);
			}});
	}
	
	private void initView(){
		topbarView = (LinearLayout) this.findViewById(R.id.topbar3);

		addView = (ImageButton) topbarView.findViewById(R.id.rightbtn_2);
		editView = (ImageButton) topbarView.findViewById(R.id.rightbtn_1);
		titleView = (TextView) topbarView.findViewById(R.id.title);
		
		searchView = (EditText) findViewById(R.id.search);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);

		lv = (BaseListView) this.findViewById(R.id.listView);
		lv.setPullLoadEnable(true);
		lv.setPullRefreshEnable(true);
		lv.setXListViewListener(this);

		noDataAddTypeView = (TextView) noDataView.findViewById(R.id.action_btn);
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void updateView(){
		editView.setVisibility(View.GONE);
		titleView.setText(R.string.WAREHOUSE_TITLE);
		if (Build.VERSION.SDK_INT < 16) {
			addView.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.add_small));
		} else {
			addView.setBackground(this.getResources().getDrawable(
					R.drawable.add_small));
		}
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

}
