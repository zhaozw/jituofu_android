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
import com.jituofu.ui.UIWareHouseParentTypeDetail.ViewHolder;
import com.jituofu.util.AppUtil;

public class UISalesReportLR extends BaseUiAuth implements BaseListViewListener {
	private TextView titleView;
	private LinearLayout topbarView, noDataView, againView;
	private BaseListView lvView;
	private LinearLayout borderView, sortContainerView, rqView, lrView;

	private Bundle extraBundle;
	private String start, end;

	SimpleDateFormat simpleDateFormat;

	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private int limit;
	private String sort = "1";
	private CustomAdapter customAdapter;
	private boolean isRefresh, isLoadMore, isLoading;
	private ArrayList<String> lrIds = new ArrayList<String>();// 存储所有利润列表的id，以免重复加载

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_salesreport_lr);

		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINA);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			start = extraBundle.getString("start");
			end = extraBundle.getString("end");

		}

		initView();
		updateView();
		onBind();

		AppUtil.showLoadingPopup(this, R.string.SALESREPORT_QUERYLR_LOADING);
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
						AppUtil.showLoadingPopup(UISalesReportLR.this,
								R.string.SALESREPORT_QUERYLR_LOADING);
						doQueryTask();
					}
				});

		rqView.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				lrView.setBackgroundResource(0);
				TextView jjViewTxt = (TextView) lrView.findViewById(R.id.txt);
				ImageView jjViewArrow = (ImageView) lrView
						.findViewById(R.id.arrow);
				jjViewTxt.setTextColor(Color.rgb(153, 153, 153));
				jjViewArrow.setImageResource(R.drawable.icon_arrow_up);

				Collections.sort(dataList, new SortByDate());
				customAdapter.notifyDataSetChanged();

				// TODO Auto-generated method stub
				if (sort.equals("1")) {
					sort = "2";
					v.setBackgroundResource(R.drawable.base_lt_lb_round);
					TextView txt = (TextView) rqView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) rqView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_up_white);
				} else {
					sort = "1";
					v.setBackgroundResource(R.drawable.base_lt_lb_round);
					TextView txt = (TextView) rqView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) rqView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_down_white);
				}
			}
		});
	}

	private void doQueryTask() {
		noDataView.setVisibility(View.GONE);
		againView.setVisibility(View.GONE);

		HashMap<String, String> urlParams = new HashMap<String, String>();

		int[] screenDisplay = AppUtil.getScreen(this);
		int limit = screenDisplay[1] / 42 + 10;
		this.limit = limit;

		urlParams.put("pageNum", pageNum + "");
		urlParams.put("limit", limit + "");
		urlParams.put("reportType", "profits");
		urlParams.put("sort", sort);
		urlParams.put("start", start);
		urlParams.put("end", end);

		try {
			doTaskAsync(C.TASK.salesreportlr, C.API.host + C.API.salesreport,
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
			pageNum++;
			renderList(operation);
			onLoad();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void renderList(JSONObject operation) throws JSONException {
		JSONArray data = new JSONArray();
		data = operation.getJSONArray("profits");

		// 初始化加载没有数据
		if (initQuery && data.length() <= 0) {
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
			String id = json.getString("id");
			map.put("date", json.getString("date"));
			map.put("lr", json.getString("profit"));
			map.put("id", id);

			// 避免重复加载
			if (lrIds.indexOf(id) < 0) {
				lrIds.add(id);
				if (isRefresh) {
					dataList.add(0, map);
				} else {
					dataList.add(map);
				}
			}
		}

		customAdapter = customAdapter == null ? new CustomAdapter(this,
				dataList) : customAdapter;

		if (isRefresh || isLoadMore) {
			customAdapter.notifyDataSetChanged();
		} else {
			lvView.setAdapter(customAdapter);
		}

		this.isLoadMore = false;
		this.isRefresh = false;

		if (data.length() < limit) {
			lvView.setPullLoadEnable(false);
			lvView.setPullRefreshEnable(false);
		}
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar2);
		titleView = (TextView) topbarView.findViewById(R.id.title);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);

		this.borderView = (LinearLayout) this.findViewById(R.id.border);
		this.sortContainerView = (LinearLayout) this
				.findViewById(R.id.sortContainer);
		this.rqView = (LinearLayout) this.findViewById(R.id.rq);
		this.lrView = (LinearLayout) this.findViewById(R.id.lr);

		lvView = (BaseListView) this.findViewById(R.id.listView);
		lvView.setPullLoadEnable(true);
		lvView.setPullRefreshEnable(true);
		lvView.setXListViewListener(this);
	}

	private void updateView() {
		if (start != null && end != null) {
			String[] startSplit = start.split("-");
			String[] endSplit = end.split("-");
			titleView.setText(startSplit[1] + "-" + startSplit[2] + "到"
					+ endSplit[1] + "-" + endSplit[2] + "的利润");
		}

		if (sort.equals("1")) {
			rqView.setBackgroundResource(R.drawable.base_lt_lb_round);
			TextView txt = (TextView) rqView.findViewById(R.id.txt);
			ImageView arrow = (ImageView) rqView.findViewById(R.id.arrow);
			txt.setTextColor(Color.rgb(255, 255, 255));
			arrow.setImageResource(R.drawable.icon_arrow_down_white);
		}
		((TextView) noDataView.findViewById(R.id.action_btn))
				.setVisibility(View.GONE);
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
		lvView.stopRefresh();
		lvView.stopLoadMore();
		lvView.setRefreshTime(AppUtil.getCurrentDateTime());
		isLoading = false;
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
						R.layout.template_lr_item, null);
				holder = new ViewHolder();
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.lr = (TextView) convertView.findViewById(R.id.lr);
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ViewHolder) convertView.getTag();
			}

			String[] date = map.get("date").split("\\s");
			holder.date.setText(date[0].substring(5));

			if (Double.parseDouble(map.get("lr")) < 0) {
				holder.lr.setTextColor(Color.parseColor("#cc0000"));
				holder.lr.setText("亏  " + map.get("lr"));
			} else if (Double.parseDouble(map.get("lr")) > 0) {
				holder.lr.setTextColor(Color.parseColor("#669933"));
				holder.lr.setText("赚  " + map.get("lr"));
			} else if (Double.parseDouble(map.get("lr")) == 0) {
				holder.lr.setText(0);
				holder.lr.setTextColor(Color.parseColor("#000000"));
			}

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
		TextView date, lr;
	}

	class SortByDate implements Comparator {
		public int compare(Object o1, Object o2) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			@SuppressWarnings("unchecked")
			HashMap<String, String> h2 = (HashMap<String, String>) o2;

			Date h1d = null;
			try {
				h1d = simpleDateFormat.parse(h1.get("date"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date h2d = null;
			try {
				h2d = simpleDateFormat.parse(h2.get("date"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (sort.equals("1")) {
				if (h1d.getTime() > h2d.getTime()) {
					return 1;
				} else if (h1d.getTime() == h2d.getTime()) {
					return 0;
				} else {
					return -1;
				}
			} else if (sort.equals("2")) {
				if (h1d.getTime() > h2d.getTime()) {
					return -1;
				} else if (h1d.getTime() == h2d.getTime()) {
					return 0;
				} else {
					return 1;
				}
			} else {
				if (h1d.getTime() > h2d.getTime()) {
					return -1;
				} else if (h1d.getTime() == h2d.getTime()) {
					return 0;
				} else {
					return 1;
				}
			}
		}
	}
}
