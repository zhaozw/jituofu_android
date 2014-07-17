package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.util.AppUtil;

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

	private boolean isRefresh, isLoadMore, isLoading;

	private ArrayList<String> productsId = new ArrayList<String>();// 存储所有商品的id，以免重复加载

	private String keyword;

	private BaseGetProductImageTask bpit;

	private Bundle extraBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_products_searchlist);

		extraBundle = this.getIntent().getExtras();

		initView();
		updateView();
		onBind();
	}

	private void onBind() {
		this.onCustomBack();

		searchBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = getEditValue(searchView);

				if (name == null) {
					showToast(R.string.WAREHOUSE_SPSS_PRODUCTNAME_SPECIFY);
					return;
				}
				keyword = name;
				searchBefore();
				doSearchTask();
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

				// 记账台
				if (extraBundle != null
						&& extraBundle.getString("from").equals(
								C.COMMON.cashier)) {
					Intent intent = new Intent();
					intent.putExtra("from", extraBundle.getString("from"));
					intent.putExtra("data", map.get("metaData"));
					
					backForResult(UICashier.TAG, intent);
					finish();
				} else {//商品详情页
					bundle.putString("id", map.get("id"));
					forward(UIProductDetail.class, bundle);
				}
			}
		});
	}

	private void searchBefore() {
		AppUtil.showLoadingPopup(this, R.string.WAREHOUSE_SPSS_QUERYLOADING);
		lv.setVisibility(View.GONE);
		noDataView.setVisibility(View.GONE);
		againView.setVisibility(View.GONE);
		dataList.clear();
		if (customAdapter != null) {
			customAdapter.notifyDataSetChanged();
		}
		pageNum = 1;
		initQuery = true;
		productsId.clear();
	}

	private void doSearchTask() {
		HashMap<String, String> urlParams = new HashMap<String, String>();

		int[] screenDisplay = AppUtil.getScreen(this);
		int limit = screenDisplay[1] / 42 + 10;
		this.limit = limit;

		urlParams.put("keyword", keyword);
		urlParams.put("limit", limit + "");
		urlParams.put("pageNum", pageNum + "");

		try {
			this.doTaskAsync(C.TASK.productsearch, C.API.host
					+ C.API.productsearch, urlParams);
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
		JSONArray productsList = operation.getJSONArray("productsList");
		if (resultStatus == 100) {
			if (productsList.length() > 0) {
				pageNum++;
				showSearchList(productsList);
			} else {
				noData();
			}
			onLoad();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	public void onNetworkError(int taskId) {
		if (initQuery) {
			lv.setVisibility(View.GONE);
			noDataView.setVisibility(View.GONE);
			againView.setVisibility(View.VISIBLE);
			((TextView) againView.findViewById(R.id.txt))
					.setText(C.ERROR.networkException);
			((TextView) againView.findViewById(R.id.again_btn))
					.setVisibility(View.GONE);

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
			((TextView) againView.findViewById(R.id.again_btn))
					.setVisibility(View.GONE);

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
			((TextView) againView.findViewById(R.id.again_btn))
					.setVisibility(View.GONE);

			initQuery = false;
			return;
		}
	}

	private void noData() {
		noDataView.setVisibility(View.VISIBLE);
		((TextView) noDataView.findViewById(R.id.txt))
				.setText(R.string.WAREHOUSE_SPSS_NODATA);
	}

	private void prepareSearchList(JSONArray productsList) throws JSONException {
		lv.setVisibility(View.VISIBLE);
		for (int i = 0; i < productsList.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = productsList.getJSONObject(i);
			String id = json.getString("id");
			map.put("metaData", json.toString());
			map.put("name", json.getString("name"));
			map.put("id", id);
			map.put("price", "进价：" + json.getString("price") + " 元");
			map.put("count", "库存：" + json.getString("count") + " 个");
			map.put("pic", json.getString("pic"));
			map.put("date", "时间：" + json.getString("date"));

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

		customAdapter = customAdapter == null ? new CustomAdapter(this,
				dataList) : customAdapter;

		if (isRefresh || isLoadMore) {
			customAdapter.notifyDataSetChanged();
		} else {
			lv.setAdapter(customAdapter);
		}

		this.isLoadMore = false;
		this.isRefresh = false;

		if (productsList.length() < limit) {
			lv.setPullLoadEnable(false);
			lv.setPullRefreshEnable(false);
		}
	}

	private void showSearchList(JSONArray productsList) throws JSONException {
		prepareSearchList(productsList);
	}

	private void initView() {
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
		noDataAddTypeView.setVisibility(View.GONE);
	}

	private void updateView() {
		titleView.setText(R.string.WAREHOUSE_SPSS);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if (isLoading) {
			return;
		}
		// TODO Auto-generated method stub
		this.isRefresh = true;
		this.isLoading = true;
		doSearchTask();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (isLoading) {
			return;
		}
		// TODO Auto-generated method stub
		this.isLoadMore = true;
		this.isLoading = true;
		doSearchTask();
	}

	private void onLoad() {
		lv.stopRefresh();
		lv.stopLoadMore();
		lv.setRefreshTime(AppUtil.getCurrentDateTime());
		isLoading = false;
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

	/**
	 * 自定义适配器
	 * 
	 * @author zhuqi
	 * 
	 */
	class CustomAdapter extends BaseAdapter {

		private Context context;
		private List<? extends Map<String, ?>> data;

		public CustomAdapter(Context context,
				List<? extends Map<String, ?>> data) {
			this.context = context;
			this.data = data;
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			HashMap<String, String> map = (HashMap<String, String>) data
					.get(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.template_product_searchlist_item, null);
				holder = new ViewHolder(convertView);
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
				holder = (ViewHolder) convertView.getTag();
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

	public class ViewHolder {
		private View baseView;
		private ImageView picView;
		private TextView nameView, priceView, countView, dateView;

		public ViewHolder(View baseView) {
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
