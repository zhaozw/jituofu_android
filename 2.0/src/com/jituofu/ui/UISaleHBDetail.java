package com.jituofu.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;

import com.jituofu.R;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.ui.UISalesReportProductList.CustomProductsAdapter;
import com.jituofu.ui.UISalesReportProductList.ProductsViewHolder;
import com.jituofu.util.AppUtil;

public class UISaleHBDetail extends BaseUiAuth {
	private BaseGetProductImageTask bpit;

	private TextView titleView;
	private BaseListView lvView;
	private LinearLayout noDataView;

	private CustomProductsAdapter customAdapter;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();

	private Bundle extraBundle;
	private String id;
	private JSONObject detail;
	private JSONArray products;
	private JSONArray cashierList;

	// 待加载的商品图片
	JSONArray waitLoadProductsImg = new JSONArray();

	// 所有下载图片的异步任务
	private JSONArray loadImgTasks = new JSONArray();
	private String productDirPath = AppUtil.getExternalStorageDirectory()
			+ C.DIRS.rootdir + C.DIRS.productDir;
	private boolean hasRoot = false;
	private boolean hasProductDirPath = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_sale_hb_detail);

		hasRoot = AppUtil.mkdir(AppUtil.getExternalStorageDirectory()
				+ C.DIRS.rootdir);
		hasProductDirPath = AppUtil.mkdir(productDirPath);
		
		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			id = extraBundle.getString("id");
			try {
				detail = new JSONObject(extraBundle.getString("detail"));
				products = detail.getJSONArray("products");
				cashierList = detail.getJSONArray("cashierList");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		initView();
		try {
			updateView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		onBind();
	}

	private void loadProductsImage() {
		int start = lvView.getFirstVisiblePosition();
		int end = lvView.getLastVisiblePosition();
		if (end >= lvView.getCount()) {
			end = lvView.getCount() - 1;
		}

		for (int i = 0; i < waitLoadProductsImg.length(); i++) {
			try {
				JSONObject loadImg = (JSONObject) waitLoadProductsImg.get(i);
				int position = loadImg.getInt("position");
				String pic = loadImg.getString("pic");
				String id = loadImg.getString("id");

				if ((position + 1) >= start && position <= end && pic != null
						&& pic.length() > 0) {
					ImageView view = (ImageView) loadImg.get("view");
					if (view != null) {
						getProductImg(view, pic, id);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		waitLoadProductsImg = new JSONArray();
	}
	
	private void onBind() {
		this.onCustomBack();

		lvView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
					clearAllLoadImgTasks();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
					loadProductsImage();
					break;
				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					clearAllLoadImgTasks();
					break;

				default:
					break;
				}

			}
		});
		
		lvView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) lvView
						.getItemAtPosition(position);

				JSONObject detail = new JSONObject();
				try {
					detail.put("pid", map.get("pid"));
					detail.put("pic", map.get("pic"));
					detail.put("name", map.get("name"));
					detail.put("selling_count", map.get("selling_count"));
					detail.put("selling_price", map.get("selling_price"));
					detail.put("remark", map.get("remark"));
					try {
						detail.put("typeName",
								new JSONObject(map.get("typeName")));
					} catch (JSONException e) {
						detail.put("typeName", "");
					}
					detail.put("date", map.get("date"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Bundle bundle = new Bundle();
				bundle.putString("id", map.get("id"));
				bundle.putString("detail", detail.toString());

				forward(UISaleDetail.class, bundle);
			}
		});
	}
	
	private void clearAllLoadImgTasks() {
		for (int i = 0; i < loadImgTasks.length(); i++) {
			BaseGetProductImageTask bpit;
			try {
				bpit = (BaseGetProductImageTask) loadImgTasks.get(i);
				if (bpit != null) {
					bpit.cancel(true);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void updateView() throws JSONException {
		if (detail != null) {
			titleView.setText("合并记账(" + cashierList.length() + ")");
			if (cashierList.length() <= 0) {
				lvView.setVisibility(View.GONE);
				noDataView.setVisibility(View.VISIBLE);
				((TextView) noDataView.findViewById(R.id.txt))
						.setText(R.string.SALESREPORT_QUERYPRODUCTLIST_NODATA);
				((TextView) noDataView.findViewById(R.id.action_btn))
						.setVisibility(View.GONE);
				return;
			}
			lvView.setVisibility(View.VISIBLE);
			for (int i = 0; i < cashierList.length(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject json = cashierList.getJSONObject(i);
				String id = json.getString("id");
				map.put("remark", json.getString("remark"));
				map.put("id", id);
				map.put("date", json.getString("date"));
				map.put("selling_count", json.getString("selling_count"));
				map.put("selling_price", json.getString("selling_price"));

				for (int k = 0; k < products.length(); k++) {
					JSONObject product = products.getJSONObject(k);
					if (product.getString("pid").equals(json.get("pid"))) {
						map.put("name", product.getString("name"));
						map.put("pid", product.getString("pid"));
						map.put("pic", product.getString("pic"));

						try {
							JSONObject typeName = product
									.getJSONObject("typeName");
							map.put("typeName", typeName.toString());
						} catch (JSONException e) {
							map.put("typeName", "");
						}
					}
				}

				dataList.add(map);
			}

			customAdapter = customAdapter == null ? new CustomProductsAdapter(
					this, dataList) : customAdapter;

			lvView.setAdapter(customAdapter);
			
			AppUtil.timer(new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					loadProductsImage();
				}}, 2000);
		}
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		lvView = (BaseListView) this.findViewById(R.id.listView);
		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		lvView.setPullLoadEnable(false);
		lvView.setPullRefreshEnable(false);
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
				holder.setPicBoxViewView((LinearLayout) convertView
						.findViewById(R.id.pic));
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ProductsViewHolder) convertView.getTag();
			}

			// 单笔记录
			holder.getMergeImgsView().setVisibility(View.GONE);
			holder.getPicView().setVisibility(View.VISIBLE);
			holder.getPicView().setImageURI(null);
			holder.getPicView().setBackgroundResource(
					R.drawable.default_img_placeholder);
			holder.getCountView().setText("总数量：" + map.get("selling_count"));
			holder.getNameView().setText(map.get("name"));
			holder.getPriceView().setVisibility(View.VISIBLE);
			holder.getPriceView().setText(
					getString(R.string.CASHIER_SELLING_PRICE)
							+ "："
							+ AppUtil.toFixed(Double.parseDouble(map
									.get("selling_price"))));
			
			JSONObject loadImg = new JSONObject();
			try {
				loadImg.put("position", position);
				loadImg.put("view", holder.getPicView());
				loadImg.put("pic", map.get("pic"));
				loadImg.put("id", map.get("pid"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Uri src = null;
			holder.getPicView().setTag(map.get("pid"));

			if (hasRoot && hasProductDirPath) {
				String oldFileName = productDirPath + "/" + map.get("pid")
						+ ".png";
				File file = new File(oldFileName);
				// 如果图片存在本地缓存目录，则不去服务器下载
				if (file.exists()) {
					src = Uri.fromFile(file);
				}
			}

			if (src != null) {
				holder.getPicView().setBackgroundResource(0);
				holder.getPicView().setImageURI(src);
			} else {
				holder.getPicView().setImageURI(null);
				holder.getPicView().setBackgroundResource(
						R.drawable.default_img_placeholder);
				waitLoadProductsImg.put(loadImg);
			}
			

			String date = map.get("date");
			holder.getDateView().setText("时间：" + date.substring(5));

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
			BaseGetProductImageTask bpit = new BaseGetProductImageTask(view,
					imgPath, id);
			bpit.execute();
			loadImgTasks.put(bpit);
		}
	}

	public class ProductsViewHolder {
		private View baseView;
		private ImageView picView;
		private TextView nameView, priceView, countView, dateView;
		private LinearLayout picBoxView;

		public ProductsViewHolder(View baseView) {
			this.baseView = baseView;
		}

		public RelativeLayout getMergeImgsView() {
			if (picBoxView == null) {
				return null;
			}
			RelativeLayout view = (RelativeLayout) picBoxView
					.findViewById(R.id.merger_imgs);
			return view;
		}

		public LinearLayout getPicBoxViewView() {
			if (picBoxView == null) {
				picBoxView = (LinearLayout) baseView.findViewById(R.id.pic);
			}

			return picBoxView;
		}

		public void setPicBoxViewView(LinearLayout view) {
			picBoxView = view;
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
