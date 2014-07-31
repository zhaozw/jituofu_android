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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.jituofu.R;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.util.AppUtil;

public class UIWareHouseParentTypeDetailProductsList extends BaseUiAuth
		implements BaseListViewListener {
	private BaseGetProductImageTask bpit;

	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private int limit;
	private String sort = "1";
	private CustomProductsAdapter customProductsAdapter;

	private boolean isRefresh, isLoadMore, isLoading;

	private ArrayList<String> productsId = new ArrayList<String>();// 存储所有商品的id，以免重复加载

	private LinearLayout topbarView, noDataView, againView;
	private TextView titleView;
	private BaseListView lv;

	private Bundle extraBundle;
	private JSONObject typeData, parentTypeData;
	private String typeId;

	private LinearLayout sortContainerView, jjView, rksjView, borderView;
	SimpleDateFormat simpleDateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_warehouse_parenttype_detail_productlist);

		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINA);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			try {
				typeData = new JSONObject(extraBundle.getString("data"));
				typeId = typeData.getString("id");

				String extraParentTypeData = extraBundle
						.getString("parentTypeData");

				if (extraParentTypeData != null) {
					parentTypeData = new JSONObject(extraParentTypeData);
				}

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
					R.string.WAREHOUSE_QUERYPARENTPRODUCTSLIST_LOADING);
			initQuery = true;
			doQueryTask();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void onBind() {
		this.onCustomBack();

		rksjView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jjView.setBackgroundResource(0);
				TextView jjViewTxt = (TextView) jjView.findViewById(R.id.txt);
				ImageView jjViewArrow = (ImageView) jjView
						.findViewById(R.id.arrow);
				jjViewTxt.setTextColor(Color.rgb(153, 153, 153));
				jjViewArrow.setImageResource(R.drawable.icon_arrow_up);

				Collections.sort(dataList, new SortByDate());
				customProductsAdapter.notifyDataSetChanged();

				// TODO Auto-generated method stub
				if (sort.equals("1")) {
					sort = "2";
					v.setBackgroundResource(R.drawable.base_rt_rb_round);
					TextView txt = (TextView) rksjView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) rksjView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_up_white);
				} else {
					sort = "1";
					v.setBackgroundResource(R.drawable.base_rt_rb_round);
					TextView txt = (TextView) rksjView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) rksjView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_down_white);
				}
			}
		});

		jjView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rksjView.setBackgroundResource(0);
				TextView rksjViewTxt = (TextView) rksjView
						.findViewById(R.id.txt);
				ImageView rksjViewArrow = (ImageView) rksjView
						.findViewById(R.id.arrow);
				rksjViewTxt.setTextColor(Color.rgb(153, 153, 153));
				rksjViewArrow.setImageResource(R.drawable.icon_arrow_up);

				// TODO Auto-generated method stub
				if (sort.equals("4")) {
					sort = "3";
					v.setBackgroundResource(R.drawable.base_lt_lb_round);
					TextView txt = (TextView) jjView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) jjView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_down_white);
				} else {
					sort = "4";
					v.setBackgroundResource(R.drawable.base_lt_lb_round);
					TextView txt = (TextView) jjView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) jjView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_up_white);
				}
				
				Collections.sort(dataList, new SortByPrice());
				customProductsAdapter.notifyDataSetChanged();
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

				if (extraBundle != null
						&& extraBundle.getString("from").equals(
								C.COMMON.cashier)) {//来自记账台查询商品页面
					Intent intent = new Intent();
					intent.putExtra("from", extraBundle.getString("from"));
					intent.putExtra("data", map.get("metaData"));

					backForResult(UICashier.TAG, intent);
					finish();
				} else {
					Bundle bundle = new Bundle();

					bundle.putString("id", map.get("id"));
					forward(UIProductDetail.class, bundle);
				}
			}
		});

		((TextView) againView.findViewById(R.id.again_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						initQuery = true;
						AppUtil.showLoadingPopup(
								UIWareHouseParentTypeDetailProductsList.this,
								R.string.WAREHOUSE_QUERYPARENTDETAIL_LOADING);
						doQueryTask();
					}
				});
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
		urlParams.put("type", typeId);
		urlParams.put("sort", sort);

		try {
			doTaskAsync(C.TASK.productquerybytype, C.API.host
					+ C.API.productquerybytype, urlParams);
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

			renderList(operation);
			onLoad();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void renderList(JSONObject operation) throws JSONException {
		JSONArray data = new JSONArray();
		data = operation.getJSONArray("products");

		// 初始化加载没有数据
		if (initQuery && data.length() <= 0) {
			lv.setVisibility(View.GONE);
			againView.setVisibility(View.GONE);
			noDataView.setVisibility(View.VISIBLE);
			((TextView) noDataView.findViewById(R.id.txt))
					.setText(R.string.WAREHOUSE_SPSS_NODATA);
			sortContainerView.setVisibility(View.GONE);
			borderView.setVisibility(View.GONE);
			return;
		}
		if (initQuery) {
			initQuery = false;
		}

		sortContainerView.setVisibility(View.VISIBLE);
		borderView.setVisibility(View.VISIBLE);

		for (int i = 0; i < data.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = data.getJSONObject(i);
			String id = json.getString("id");
			map.put("metaData", json.toString());
			map.put("name", json.getString("name"));
			map.put("id", id);
			map.put("price", "进价：" + json.getString("price") + " 元");
			map.put("count", "库存：" + json.getString("count") + " 个");
			map.put("pic", json.getString("pic"));
			map.put("date", "时间：" + json.getString("date"));
			map.put("time", json.getString("date"));
			map.put("money", json.getString("price"));

			// 避免重复加载
			if (productsId.indexOf(id) < 0) {
				productsId.add(id);
				if (isRefresh) {
					dataList.add(0, map);
				} else {
					dataList.add(map);
				}
			}
		}

		customProductsAdapter = customProductsAdapter == null ? new CustomProductsAdapter(
				this, dataList) : customProductsAdapter;

		if (isRefresh || isLoadMore) {
			customProductsAdapter.notifyDataSetChanged();
		} else {
			lv.setAdapter(customProductsAdapter);
		}

		this.isLoadMore = false;
		this.isRefresh = false;

		if (data.length() < limit) {
			lv.setPullLoadEnable(false);
			lv.setPullRefreshEnable(false);
		}
	}

	class SortByDate implements Comparator {
		public int compare(Object o1, Object o2) {
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;

			Date h1d = null;
			try {
				h1d = simpleDateFormat.parse(h1.get("time"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date h2d = null;
			try {
				h2d = simpleDateFormat.parse(h2.get("time"));
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

	class SortByPrice implements Comparator {
		public int compare(Object o1, Object o2) {
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			HashMap<String, String> h2 = (HashMap<String, String>) o2;

			Double h1money = Double.parseDouble(h1.get("money"));
			Double h2money = Double.parseDouble(h2.get("money"));

			if (sort.equals("4")) {
				if (h1money == h2money) {
					return 0;
				} else if (h1money > h2money) {
					return 1;
				} else {
					return -1;
				}
			} else if (sort.equals("3")) {
				if (h1money == h2money) {
					return 0;
				} else if (h1money > h2money) {
					return -1;
				} else {
					return 1;
				}
			} else {
				if (h1money == h2money) {
					return 0;
				} else if (h1money > h2money) {
					return -1;
				} else {
					return 1;
				}
			}
		}
	}

	private void updateView() throws JSONException {
		if (parentTypeData != null) {
			titleView.setText(parentTypeData.getString("name")
					+ this.getString(R.string.SPFL_SPEARATOR)
					+ this.typeData.getString("name") + " 商品列表");
		} else {
			titleView.setText(this.typeData.getString("name") + " 商品列表");
		}
		((TextView) noDataView.findViewById(R.id.action_btn))
				.setVisibility(View.GONE);
		if (sort.equals("1")) {
			rksjView.setBackgroundResource(R.drawable.base_rt_rb_round);
			TextView txt = (TextView) rksjView.findViewById(R.id.txt);
			ImageView arrow = (ImageView) rksjView.findViewById(R.id.arrow);
			txt.setTextColor(Color.rgb(255, 255, 255));
			arrow.setImageResource(R.drawable.icon_arrow_down_white);
		}
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar2);
		titleView = (TextView) topbarView.findViewById(R.id.title);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		// 来自记账台页面查询商品
		if (extraBundle != null && 
				extraBundle.getString("from") != null && extraBundle.getString("from").equals(C.COMMON.cashier)) {
			((TextView) noDataView.findViewById(R.id.action_btn))
					.setVisibility(View.GONE);
		}
		againView = (LinearLayout) this.findViewById(R.id.again);

		this.borderView = (LinearLayout) this.findViewById(R.id.border);
		this.sortContainerView = (LinearLayout) this
				.findViewById(R.id.sortContainer);
		this.jjView = (LinearLayout) this.findViewById(R.id.jj);
		this.rksjView = (LinearLayout) this.findViewById(R.id.rksj);

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
	class CustomProductsAdapter extends BaseAdapter {

		private Context context;
		private List<? extends Map<String, ?>> data;

		public CustomProductsAdapter(Context context,
				List<? extends Map<String, ?>> data) {
			this.context = context;
			this.data = data;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ProductsViewHolder holder;
			HashMap<String, String> map = (HashMap<String, String>) data
					.get(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.template_product_searchlist_item, null);
				holder = new ProductsViewHolder(convertView);
				holder.setCountView((TextView) convertView
						.findViewById(R.id.count));
				holder.setNameView((TextView) convertView
						.findViewById(R.id.name));
				holder.setPriceView((TextView) convertView
						.findViewById(R.id.price));
				holder.setDateView((TextView) convertView
						.findViewById(R.id.date));
				holder.setPicView((ImageView) convertView
						.findViewById(R.id.pic));
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ProductsViewHolder) convertView.getTag();
			}

			holder.getNameView().setText(map.get("name"));
			holder.getCountView().setText(map.get("count"));
			holder.getDateView().setText(map.get("date"));
			holder.getPriceView().setText(map.get("price"));

			holder.getPicView().setImageURI(null);
			holder.getPicView().setBackgroundResource(
					R.drawable.default_img_placeholder);
			getProductImg(holder.getPicView(), map.get("pic"), map.get("id"));

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

	private void getProductImg(final ImageView view, String imgPath, String id) {
		if (imgPath != null && imgPath.length() > 0) {
			bpit = new BaseGetProductImageTask(view, imgPath, id);
			bpit.execute();
		}
	}

	public class ProductsViewHolder {
		private View baseView;
		private ImageView picView;
		private TextView nameView, priceView, countView, dateView;

		public ProductsViewHolder(View baseView) {
			this.baseView = baseView;
		}

		public TextView getNameView() {
			if (nameView == null) {
				nameView = (TextView) baseView.findViewById(R.id.name);
			}

			return nameView;
		}

		public void setNameView(TextView view) {
			nameView = view;
		}

		public TextView getPriceView() {
			if (priceView == null) {
				priceView = (TextView) baseView.findViewById(R.id.price);
			}

			return priceView;
		}

		public void setPriceView(TextView view) {
			priceView = view;
		}

		public TextView getCountView() {
			if (countView == null) {
				countView = (TextView) baseView.findViewById(R.id.count);
			}

			return countView;
		}

		public void setCountView(TextView view) {
			countView = view;
		}

		public TextView getDateView() {
			if (dateView == null) {
				dateView = (TextView) baseView.findViewById(R.id.date);
			}

			return dateView;
		}

		public void setDateView(TextView view) {
			dateView = view;
		}

		public ImageView getPicView() {
			if (picView == null) {
				picView = (ImageView) baseView.findViewById(R.id.pic);
			}

			return picView;
		}

		public void setPicView(ImageView view) {
			picView = view;
		}
	}
}
