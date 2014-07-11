package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.util.AppUtil;

public class UIWareHouse extends BaseUiAuth implements BaseListViewListener {
	// 定义相关的view
	private LinearLayout topbarView, noDataView, againView;
	private ImageButton addView, editView;
	private TextView titleView;
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

		// 查询分类
		initQuery = true;// 标记初始化查询为true
		AppUtil.showLoadingPopup(this, R.string.SPFL_QUERY_LOADING);
		doQueryTask();
	}

	private void doQueryTask() {
		HashMap<String, String> urlParams = new HashMap<String, String>();

		int[] screenDisplay = AppUtil.getScreen(this);
		int limit = screenDisplay[1] / 42 + 10;
		this.limit = limit;

		urlParams.put("pageNum", pageNum + "");
		urlParams.put("limit", limit + "");

		try {
			doTaskAsync(C.TASK.gettypes, C.API.host + C.API.gettypes, urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();

		if (resultStatus == 100) {
			switch (taskId) {
			case C.TASK.gettypes:
				pageNum++;
				JSONArray types = operation.getJSONArray("types");

				// 初始化加载时，没有数据显示无数据提示
				if (initQuery && types.length() <= 0) {
					lv.setVisibility(View.GONE);
					againView.setVisibility(View.GONE);
					noDataView.setVisibility(View.VISIBLE);
					((TextView) noDataView.findViewById(R.id.txt))
							.setText(R.string.WAREHOUSE_NOPARENT);
					return;
				}
				if (initQuery) {
					initQuery = false;
				}

				lv.setVisibility(View.VISIBLE);
				againView.setVisibility(View.GONE);
				noDataView.setVisibility(View.GONE);

				renderList(types);
				onLoad();
				break;
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void renderList(JSONArray data) throws JSONException {
		for (int i = 0; i < data.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = data.getJSONObject(i);
			String id = json.getString("id");
			map.put("name", json.getString("name"));
			map.put("id", id);

			// 避免重复加载
			if (typesId.indexOf(id) < 0) {
				typesId.add(id);
				if (isRefresh) {
					dataList.add(0, map);
				} else {
					dataList.add(map);
				}
			}
		}

		customAdapter = customAdapter == null ? new CustomAdapter(this,
				dataList, R.layout.template_types_item,
				new String[] { "name" }, new int[] { R.id.txt })
				: customAdapter;

		if (isRefresh || isLoadMore) {
			customAdapter.notifyDataSetChanged();
		} else {
			lv.setAdapter(customAdapter);
		}

		this.isLoadMore = false;
		this.isRefresh = false;

		if (data.length() < limit) {
			lv.setPullLoadEnable(false);
			lv.setPullRefreshEnable(false);
		}
	}

	private void onLoad() {
		lv.stopRefresh();
		lv.stopLoadMore();
		lv.setRefreshTime(AppUtil.getCurrentDateTime());
		isLoading = false;
	}

	private void onBind() {
		onCustomBack();
		searchView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forward(UIProductSearchList.class);
			}
		});
		addView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forward(UIProductAdd.class);
			}
		});
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) lv
						.getItemAtPosition(position);
				
				Bundle bundle = new Bundle();
				bundle.putString("from", C.COMMON.warehouse);
				bundle.putString("data", map.toString());
				
				forward(UIChildType.class, bundle);
			}
		});
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar3);

		addView = (ImageButton) topbarView.findViewById(R.id.rightbtn_2);
		editView = (ImageButton) topbarView.findViewById(R.id.rightbtn_1);
		titleView = (TextView) topbarView.findViewById(R.id.title);

		searchView = (EditText) findViewById(R.id.search);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);
		((TextView) noDataView.findViewById(R.id.action_btn))
				.setVisibility(View.GONE);

		lv = (BaseListView) this.findViewById(R.id.listView);
		lv.setPullLoadEnable(true);
		lv.setPullRefreshEnable(true);
		lv.setXListViewListener(this);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void updateView() {
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
		if (isLoading) {
			return;
		}
		// TODO Auto-generated method stub
		this.isRefresh = true;
		this.isLoading = true;
		doQueryTask();
	}

	@Override
	public void onLoadMore() {
		if (isLoading) {
			return;
		}
		// TODO Auto-generated method stub
		this.isLoadMore = true;
		this.isLoading = true;
		doQueryTask();
	}

	/**
	 * 自定义适配器
	 * 
	 * @author zhuqi
	 * 
	 */
	class CustomAdapter extends SimpleAdapter {

		public CustomAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			final HashMap<String, String> map = (HashMap<String, String>) customAdapter
					.getItem(position);

			if (convertView == null) {
				convertView = super.getView(position, convertView, parent);
				holder = new ViewHolder();
				holder.txt = (TextView) convertView.findViewById(R.id.txt);
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txt.setText(map.get("name"));

			return convertView;
		}
	}

	static class ViewHolder {
		TextView txt;
	}

}
