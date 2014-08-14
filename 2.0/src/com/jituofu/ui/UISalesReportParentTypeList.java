package com.jituofu.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.ui.UIWareHouseParentTypeDetail.CustomAdapter;
import com.jituofu.ui.UIWareHouseParentTypeDetail.CustomProductsAdapter;
import com.jituofu.ui.UIWareHouseParentTypeDetail.SortByDate;
import com.jituofu.ui.UIWareHouseParentTypeDetail.SortByPrice;
import com.jituofu.ui.UIWareHouseParentTypeDetail.ViewHolder;
import com.jituofu.util.AppUtil;

public class UISalesReportParentTypeList extends BaseUiAuth {
	private TextView titleView;
	private LinearLayout topbarView, noDataView, againView;
	private BaseListView lvView;
	private LinearLayout borderView, sortContainerView, slView;

	private Bundle extraBundle;
	private String start, end;
	// 查询分类相关
	private boolean initQuery = false;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private String sort = "1";
	private CustomAdapter customAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_salesreport_parent_typelist);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			start = extraBundle.getString("start");
			end = extraBundle.getString("end");

		}

		initView();
		updateView();
		onBind();

		AppUtil.showLoadingPopup(this, R.string.SALESREPORT_QUERYTYPE_LOADING);
		initQuery = true;
		doQueryTask();
	}

	private void onBind() {
		this.onCustomBack();

		((TextView) againView.findViewById(R.id.again_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						initQuery = true;
						AppUtil.showLoadingPopup(UISalesReportParentTypeList.this,
								R.string.SALESREPORT_QUERYTYPE_LOADING);
						doQueryTask();
					}
				});
		
		slView.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				if(customAdapter == null){
					return;
				}

				// TODO Auto-generated method stub
				if (sort.equals("1")) {
					sort = "2";
					v.setBackgroundResource(R.drawable.base_rt_rb_lt_lb_round);
					TextView txt = (TextView) slView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) slView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_up_white);
				} else {
					sort = "1";
					v.setBackgroundResource(R.drawable.base_rt_rb_lt_lb_round);
					TextView txt = (TextView) slView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) slView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_down_white);
				}
				
				Collections.sort(dataList, new SortByCount());
				customAdapter.notifyDataSetChanged();
			}
		});
	}

	private void doQueryTask() {
		noDataView.setVisibility(View.GONE);
		againView.setVisibility(View.GONE);

		HashMap<String, String> urlParams = new HashMap<String, String>();
		
		urlParams.put("reportType", "type");
		urlParams.put("start", start);
		urlParams.put("end", end);

		try {
			doTaskAsync(C.TASK.salesreporttypelist, C.API.host + C.API.salesreport,
					urlParams);
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
			lvView.setVisibility(View.VISIBLE);
			sortContainerView.setVisibility(View.VISIBLE);
			borderView.setVisibility(View.VISIBLE);
			renderList(operation);
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void renderList(JSONObject operation) throws JSONException {
		JSONArray data = new JSONArray();
		data = operation.getJSONArray("saleTypeList");

		// 初始化加载没有数据
		if (initQuery && data.length() <= 0) {
			borderView.setVisibility(View.GONE);
			sortContainerView.setVisibility(View.GONE);
			lvView.setVisibility(View.GONE);
			againView.setVisibility(View.GONE);
			noDataView.setVisibility(View.VISIBLE);
			((TextView) noDataView.findViewById(R.id.txt))
					.setText(R.string.SALESREPORT_QUERYLR_NODATA);
			return;
		}
		if (initQuery) {
			initQuery = false;
		}

		for (int i = 0; i < data.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = data.getJSONObject(i);
			
			map.put("typeName", json.getString("typeName"));
			map.put("count", AppUtil.toFixed(Double.parseDouble(json.getString("count"))));
			if(json.has("child")){
				map.put("child", json.getJSONArray("child").toString());
			}
			
			dataList.add(map);
		}

		customAdapter = customAdapter == null ? new CustomAdapter(this,
				dataList) : customAdapter;

		lvView.setAdapter(customAdapter);
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar2);
		titleView = (TextView) topbarView.findViewById(R.id.title);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);

		this.borderView = (LinearLayout) this.findViewById(R.id.border);
		this.sortContainerView = (LinearLayout) this
				.findViewById(R.id.sortContainer);
		this.slView = (LinearLayout) this.findViewById(R.id.sl);

		lvView = (BaseListView) this.findViewById(R.id.listView);
		lvView.setPullLoadEnable(false);
		lvView.setPullRefreshEnable(false);
	}

	private void updateView() {
		if (start != null && end != null) {
			String[] startSplit = start.split("-");
			String[] endSplit = end.split("-");
//			titleView.setText(startSplit[1] + "-" + startSplit[2] + "到"
//					+ endSplit[1] + "-" + endSplit[2] + "的大分类销售");
			titleView.setText(R.string.SALESREPORT_PARENTTYPE_TITLE);
		}

		if (sort.equals("1")) {
			slView.setBackgroundResource(R.drawable.base_rt_rb_lt_lb_round);
			TextView txt = (TextView) slView.findViewById(R.id.txt);
			ImageView arrow = (ImageView) slView.findViewById(R.id.arrow);
			txt.setTextColor(Color.rgb(255, 255, 255));
			arrow.setImageResource(R.drawable.icon_arrow_down_white);
		}
		((TextView) noDataView.findViewById(R.id.action_btn))
				.setVisibility(View.GONE);
	}

	public void onNetworkError(int taskId) {
		if (initQuery) {
			sortContainerView.setVisibility(View.GONE);
			borderView.setVisibility(View.GONE);

			lvView.setVisibility(View.GONE);
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
			sortContainerView.setVisibility(View.GONE);
			borderView.setVisibility(View.GONE);

			lvView.setVisibility(View.GONE);
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
			sortContainerView.setVisibility(View.GONE);
			borderView.setVisibility(View.GONE);

			lvView.setVisibility(View.GONE);
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
	class CustomAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<HashMap<String, String>> data;

		public CustomAdapter(Context context,
				ArrayList<HashMap<String, String>> data) {
			this.context = context;
			this.data = data;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final HashMap<String, String> map = (HashMap<String, String>) data
					.get(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.template_salesreport_typelist_item, null);
				holder = new ViewHolder();
				holder.typeName = (TextView) convertView.findViewById(R.id.typeName);
				holder.sl = (TextView) convertView.findViewById(R.id.sl);
				holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ViewHolder) convertView.getTag();
			}

			final String child = map.get("child");
			if(child != null){
				try {
					JSONArray childs = new JSONArray(child);
					if(childs != null && childs.length() > 0){
						holder.arrow.setVisibility(View.VISIBLE);
						convertView.setOnClickListener(null);
						convertView.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Bundle bundle = new Bundle();
								bundle.putString("child", child);
								bundle.putString("title", map.get("typeName")+" 的小分类销售");
								forward(UISalesReportChildTypeList.class, bundle);
							}});
					}else{
						holder.arrow.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				holder.arrow.setVisibility(View.GONE);
			}
			
			holder.sl.setText(map.get("count"));
			holder.typeName.setText(map.get("typeName"));

			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return this.data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	}

	static class ViewHolder {
		TextView typeName, sl;
		ImageView arrow;
	}
	
	class SortByCount implements Comparator {
		public int compare(Object o1, Object o2) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			@SuppressWarnings("unchecked")
			HashMap<String, String> h2 = (HashMap<String, String>) o2;

			Double olCount, o2Count;
			
			olCount = Double.parseDouble(h1.get("count"));
			o2Count = Double.parseDouble(h2.get("count"));

			if (sort.equals("1")) {
				if (olCount > o2Count) {
					return -1;
				} else if (olCount == o2Count) {
					return 0;
				} else {
					return 1;
				}
			} else if (sort.equals("2")) {
				if (olCount > o2Count) {
					return 1;
				} else if (olCount == o2Count) {
					return 0;
				} else {
					return -1;
				}
			}
			
			return 0;
		}
	}
}
