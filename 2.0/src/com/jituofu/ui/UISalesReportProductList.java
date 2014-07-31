package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseDateTimePicker;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.ui.UISalesReportCB.CustomAdapter;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.CustomProductsAdapter;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.ProductsViewHolder;
import com.jituofu.util.AppUtil;
import com.jituofu.util.DateUtil;

public class UISalesReportProductList extends BaseUiAuth
implements BaseListViewListener{
	private BaseGetProductImageTask bpit;
	
	private TextView titleView;
	private LinearLayout noDataView, againView, sortContainerView, borderView;
	private BaseListView lvView;

	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private int limit;
	private String sort = "1";
	private CustomProductsAdapter customAdapter;
	private boolean isRefresh, isLoadMore, isLoading;
	private ArrayList<String> productId = new ArrayList<String>();// 存储所有列表的id，以免重复加载

	private Bundle extraBundle;
	private JSONArray salesList;
	private String start, end;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_salesreport_splist);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			start = extraBundle.getString("start");
			end = extraBundle.getString("end");
		}

		initView();
		updateView();
		onBind();
		doQueryTask();
	}
	
	private void renderList(JSONArray salesList) throws JSONException {
		// 初始化加载没有数据
		if (initQuery && salesList.length() <= 0) {
			lvView.setVisibility(View.GONE);
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

		for (int i = 0; i < salesList.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = salesList.getJSONObject(i);
			String id = json.getString("id");
			map.put("metaData", json.toString());
			map.put("name", json.getString("name"));
			map.put("id", id);
			map.put("isMerge", json.getString("isMerge"));
			map.put("typeId", json.getString("typeId"));
			map.put("price", json.getString("price"));
			map.put("date", json.getString("date"));
			map.put("remark", json.getString("remark"));
			map.put("selling_count", json.getString("selling_count"));
			map.put("selling_price", json.getString("selling_price"));
			map.put("pic", json.getString("pic"));

			// 避免重复加载
			if (productId.indexOf(id) < 0) {
				productId.add(id);
				if (isRefresh) {
					dataList.add(0, map);
				} else {
					dataList.add(map);
				}
			}
		}

		customAdapter = customAdapter == null ? new CustomProductsAdapter(
				this, dataList) : customAdapter;

		if (isRefresh || isLoadMore) {
			customAdapter.notifyDataSetChanged();
		} else {
			lvView.setAdapter(customAdapter);
		}

		this.isLoadMore = false;
		this.isRefresh = false;

		if (salesList.length() < limit) {
			lvView.setPullLoadEnable(false);
			lvView.setPullRefreshEnable(false);
		}
	}

	private void onBind() {
		this.onCustomBack();
	}

	private void updateView() {
		if (start != null && end != null) {
			String[] startSplit = start.split("-");
			String[] endSplit = end.split("-");
			titleView.setText(startSplit[1] + "-" + startSplit[2] + "到"
					+ endSplit[1] + "-" + endSplit[2] + "的售出商品");
		}
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);
		lvView = (BaseListView) this.findViewById(R.id.listView);
		lvView.setPullLoadEnable(true);
		lvView.setPullRefreshEnable(true);
		lvView.setXListViewListener(this);
		
		sortContainerView = (LinearLayout) this.findViewById(R.id.sortContainer);
		borderView = (LinearLayout) this.findViewById(R.id.border);
	}

	private void doQueryTask() {
		AppUtil.showLoadingPopup(this, R.string.COMMON_CXZ);

		// TODO Auto-generated method stub
		HashMap<String, String> urlParams = new HashMap<String, String>();

		int[] screenDisplay = AppUtil.getScreen(this);
		limit = (screenDisplay[1] / 80) + 10;

		urlParams.put("pageNum", pageNum + "");
		urlParams.put("limit", limit + "");
		urlParams.put("sort", sort + "");
		urlParams.put("start", start);
		urlParams.put("end", end);

		try {
			this.doTaskAsync(C.TASK.salesreport,
					C.API.host + C.API.salesreport, urlParams);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		pageNum++;

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();
		if (resultStatus == 100) {
			
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
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
						R.layout.template_sales_product_item, null);
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
						.findViewById(R.id.img));
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ProductsViewHolder) convertView.getTag();
			}

			holder.getNameView().setText(map.get("name"));
			holder.getCountView().setText(map.get("selling_count"));
			holder.getDateView().setText(map.get("date"));
			holder.getPriceView().setText(map.get("selling_price"));

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
				picView = (ImageView) baseView.findViewById(R.id.img);
			}

			return picView;
		}

		public void setPicView(ImageView view) {
			picView = view;
		}
	}
}
