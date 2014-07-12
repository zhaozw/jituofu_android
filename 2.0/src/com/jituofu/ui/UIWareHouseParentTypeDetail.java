package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.ui.UIWareHouse.CustomAdapter;
import com.jituofu.ui.UIWareHouse.ViewHolder;
import com.jituofu.util.AppUtil;

public class UIWareHouseParentTypeDetail extends BaseUiAuth implements
		BaseListViewListener {
	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private int limit;
	private CustomAdapter customAdapter;
	private boolean onlyChildTypes = false;
	private boolean onlyProducts = false;
	private boolean childTypesAndProducts = false;
	
	private boolean isRefresh, isLoadMore, isLoading, isEditing;
	
	private ArrayList<String> typesId = new ArrayList<String>();// 存储所有分类的id，以免重复加载

	private LinearLayout topbarView, noDataView, againView;
	private TextView titleView;
	private BaseListView lv;

	private Bundle extraBundle;
	private JSONObject parentTypeData;
	private String parentTypeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_warehouse_parenttype_detail);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			try {
				parentTypeData = new JSONObject(extraBundle.getString("data"));
				parentTypeId = parentTypeData.getString("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		initView();

		try {
			updateView();
			onBind();
			AppUtil.showLoadingPopup(this,
					R.string.WAREHOUSE_QUERYPARENTDETAIL_LOADING);
			initQuery = true;
			doQueryTask();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void onBind(){
		this.onCustomBack();
		
		((TextView) againView.findViewById(R.id.again_btn))
		.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AppUtil.showLoadingPopup(UIWareHouseParentTypeDetail.this,
						R.string.WAREHOUSE_QUERYPARENTDETAIL_LOADING);
				doQueryTask();
			}});
	}

	private void doQueryTask() {
		noDataView.setVisibility(View.GONE);
		againView.setVisibility(View.GONE);
		lv.setVisibility(View.VISIBLE);
		
		HashMap<String, String> urlParams = new HashMap<String, String>();

		int[] screenDisplay = AppUtil.getScreen(this);
		int limit = screenDisplay[1] / 42 + 10;
		this.limit = limit;

		urlParams.put("pageNum", pageNum + "");
		urlParams.put("limit", limit + "");
		urlParams.put("id", parentTypeId);

		try {
			doTaskAsync(C.TASK.parenttypedetail, C.API.host
					+ C.API.parenttypedetail, urlParams);
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
			pageNum++;
			
			//只有小分类列表
			if(operation.has("types") && !operation.has("products") ){
				onlyChildTypes = true;
			}
			
			renderList(operation);
			onLoad();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
	
	private void renderList(JSONObject operation) throws JSONException {
		JSONArray data = new JSONArray();
		if(onlyChildTypes){
			data = operation.getJSONArray("types");
		}
		
		//初始化加载没有数据
		if (initQuery && data.length() <= 0) {
			lv.setVisibility(View.GONE);
			againView.setVisibility(View.GONE);
			noDataView.setVisibility(View.VISIBLE);
			((TextView) noDataView.findViewById(R.id.txt))
					.setText(R.string.WAREHOUSE_QUERYPARENTDETAIL_NODATA);
			return;
		}
		if (initQuery) {
			initQuery = false;
		}
		
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

	private void updateView() throws JSONException {
		titleView.setText(this.parentTypeData.getString("name"));
		((TextView) noDataView.findViewById(R.id.action_btn)).setVisibility(View.GONE);
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar2);
		titleView = (TextView) topbarView.findViewById(R.id.title);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);

		lv = (BaseListView) this.findViewById(R.id.listView);
		lv.setPullLoadEnable(true);
		lv.setPullRefreshEnable(true);
		lv.setXListViewListener(this);
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

	private void onLoad() {
		lv.stopRefresh();
		lv.stopLoadMore();
		lv.setRefreshTime(AppUtil.getCurrentDateTime());
		isLoading = false;
	}

	public void onNetworkError(int taskId) {
		if (initQuery) {
			lv.setVisibility(View.GONE);
			noDataView.setVisibility(View.GONE);
			againView.setVisibility(View.VISIBLE);
			((TextView) againView.findViewById(R.id.txt))
					.setText(C.ERROR.networkException);

			initQuery = false;
			return;
		}
	}

	public void onNetworkTimeout(int taskId) {
		if (initQuery) {
			lv.setVisibility(View.GONE);
			noDataView.setVisibility(View.GONE);
			againView.setVisibility(View.VISIBLE);
			((TextView) againView.findViewById(R.id.txt))
					.setText(C.ERROR.networkTimeout);

			initQuery = false;
			return;
		}
	}

	public void onServerException(int taskId) {
		if (initQuery) {
			lv.setVisibility(View.GONE);
			noDataView.setVisibility(View.GONE);
			againView.setVisibility(View.VISIBLE);
			((TextView) againView.findViewById(R.id.txt))
					.setText(C.ERROR.serverException);

			initQuery = false;
			return;
		}
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
