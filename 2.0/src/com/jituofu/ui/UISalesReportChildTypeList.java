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

public class UISalesReportChildTypeList extends BaseUiAuth {
	private TextView titleView,subTitleView;
	private LinearLayout topbarView, noDataView, againView;
	private BaseListView lvView;
	private LinearLayout borderView, sortContainerView, slView;

	private Bundle extraBundle;
	// 查询分类相关
	private boolean initQuery = false;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private String sort = "1";
	private CustomAdapter customAdapter;
	private JSONArray child;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_salesreport_child_typelist);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			title = extraBundle.getString("title");
			try {
				String extraChild = extraBundle.getString("child");
				child = new JSONArray(extraChild);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		initView();
		updateView();
		onBind();
		try {
			renderList(child);
			Collections.sort(dataList, new SortByCount());
			customAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void onBind() {
		this.onCustomBack();

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

	private void renderList(JSONArray data) throws JSONException {
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

		for (int i = 0; i < data.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = data.getJSONObject(i);
			
			map.put("typeName", json.getString("typeName"));
			map.put("count", AppUtil.toFixed(Double.parseDouble(json.getString("count"))));
			dataList.add(map);
		}

		customAdapter = customAdapter == null ? new CustomAdapter(this,
				dataList) : customAdapter;

		lvView.setAdapter(customAdapter);
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar2);
		titleView = (TextView) topbarView.findViewById(R.id.title);
		subTitleView = (TextView) topbarView.findViewById(R.id.subtitle);

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
		titleView.setText(title);
		subTitleView.setText(R.string.SALESREPORT_CHILDTYPE_TITLE);

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

			holder.arrow.setVisibility(View.GONE);
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
