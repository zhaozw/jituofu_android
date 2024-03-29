package com.jituofu.ui;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
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
import com.jituofu.ui.UISalesReportLR.SortByProfit;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.CustomProductsAdapter;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.ProductsViewHolder;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.SortByDate;
import com.jituofu.util.AppUtil;
import com.jituofu.util.DateUtil;

public class UISalesReportProductList extends BaseUiAuth implements
		BaseListViewListener {
	BaseGetProductImageTask bpit;

	private Double sdkVersion = 0.0;

	private TextView titleView, subTitleView;
	private LinearLayout noDataView, againView, sortContainerView, borderView,
			rqView, slView;
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
	private String start, end;

	SimpleDateFormat simpleDateFormat;

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
		setContentView(R.layout.page_salesreport_splist);

		hasRoot = AppUtil.mkdir(AppUtil.getExternalStorageDirectory()
				+ C.DIRS.rootdir);
		hasProductDirPath = AppUtil.mkdir(productDirPath);
		try {
			sdkVersion = Double.parseDouble(Build.VERSION.RELEASE.substring(0,
					3));
		} catch (Exception e) {

		}

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			start = extraBundle.getString("start");
			end = extraBundle.getString("end");
		}

		simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINA);

		initView();
		updateView();
		onBind();

		initQuery = true;
		AppUtil.showLoadingPopup(this, R.string.COMMON_CXZ);
		doQueryTask();
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

	private void renderList(JSONArray salesList) throws JSONException {
		// 初始化加载没有数据
		if (initQuery && salesList.length() <= 0) {
			lvView.setVisibility(View.GONE);
			againView.setVisibility(View.GONE);
			noDataView.setVisibility(View.VISIBLE);
			((TextView) noDataView.findViewById(R.id.txt))
					.setText(R.string.SALESREPORT_QUERYPRODUCTLIST_NODATA);
			sortContainerView.setVisibility(View.GONE);
			borderView.setVisibility(View.GONE);
			return;
		}
		if (initQuery) {
			initQuery = false;
		}

		sortContainerView.setVisibility(View.VISIBLE);
		borderView.setVisibility(View.VISIBLE);
		lvView.setVisibility(View.VISIBLE);

		for (int i = 0; i < salesList.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = salesList.getJSONObject(i);
			String id = json.getString("id");
			map.put("metaData", json.toString());
			map.put("id", id);
			map.put("date", json.getString("date"));
			map.put("isMerge", json.getString("isMerge"));

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

		customAdapter = customAdapter == null ? new CustomProductsAdapter(this,
				dataList) : customAdapter;

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
		
		AppUtil.timer(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				loadProductsImage();
			}}, 2000);
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

				String isMerge = map.get("isMerge");
				Bundle bundle = new Bundle();
				bundle.putString("id", map.get("id"));
				bundle.putString("detail", map.get("metaData"));

				if (isMerge.equals("0")) {
					forward(UISaleDetail.class, bundle);
				} else {
					forward(UISaleHBDetail.class, bundle);
				}
			}
		});

		((TextView) againView.findViewById(R.id.again_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						initQuery = true;
						AppUtil.showLoadingPopup(UISalesReportProductList.this,
								R.string.COMMON_CXZ);
						doQueryTask();
					}
				});

		rqView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				slView.setBackgroundResource(0);
				TextView slViewTxt = (TextView) slView.findViewById(R.id.txt);
				ImageView slViewArrow = (ImageView) slView
						.findViewById(R.id.arrow);
				slViewTxt.setTextColor(Color.rgb(153, 153, 153));
				slViewArrow.setImageResource(R.drawable.icon_arrow_up);

				// TODO Auto-generated method stub
				if (sort.equals("1")) {
					sort = "2";
					if (sdkVersion < 4) {
						v.setBackgroundResource(R.drawable.base_lt_lb_round_3_0);
					} else {
						v.setBackgroundResource(R.drawable.base_lt_lb_round);
					}

					TextView txt = (TextView) rqView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) rqView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_up_white);
				} else {
					sort = "1";
					if (sdkVersion < 4) {
						v.setBackgroundResource(R.drawable.base_lt_lb_round_3_0);
					} else {
						v.setBackgroundResource(R.drawable.base_lt_lb_round);
					}

					TextView txt = (TextView) rqView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) rqView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_down_white);
				}

				Collections.sort(dataList, new SortByDate());
				customAdapter.notifyDataSetChanged();
			}
		});

		slView.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				if (customAdapter == null) {
					return;
				}

				rqView.setBackgroundResource(0);
				TextView rqViewTxt = (TextView) rqView.findViewById(R.id.txt);
				ImageView rqViewArrow = (ImageView) rqView
						.findViewById(R.id.arrow);
				rqViewTxt.setTextColor(Color.rgb(153, 153, 153));
				rqViewArrow.setImageResource(R.drawable.icon_arrow_down);

				// TODO Auto-generated method stub
				if (sort.equals("4")) {
					sort = "3";
					if (sdkVersion < 4) {
						v.setBackgroundResource(R.drawable.base_rt_rb_round_3_0);
					} else {
						v.setBackgroundResource(R.drawable.base_rt_rb_round);
					}

					TextView txt = (TextView) slView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) slView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_down_white);
				} else {
					sort = "4";
					if (sdkVersion < 4) {
						v.setBackgroundResource(R.drawable.base_rt_rb_round_3_0);
					} else {
						v.setBackgroundResource(R.drawable.base_rt_rb_round);
					}

					TextView txt = (TextView) slView.findViewById(R.id.txt);
					ImageView arrow = (ImageView) slView
							.findViewById(R.id.arrow);
					txt.setTextColor(Color.rgb(255, 255, 255));
					arrow.setImageResource(R.drawable.icon_arrow_up_white);
				}

				Collections.sort(dataList, new SortByCount());
				customAdapter.notifyDataSetChanged();
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

	private void updateView() {
		if (start != null && end != null) {
			String[] startSplit = start.split("-");
			String[] endSplit = end.split("-");
			subTitleView.setText(startSplit[1] + "-" + startSplit[2] + "到"
					+ endSplit[1] + "-" + endSplit[2]);
			titleView.setText(R.string.SALESREPORT_SPMX_TITLE);
		}
		if (sort.equals("1")) {
			if (sdkVersion < 4) {
				rqView.setBackgroundResource(R.drawable.base_lt_lb_round_3_0);
			} else {
				rqView.setBackgroundResource(R.drawable.base_lt_lb_round);
			}

			TextView txt = (TextView) rqView.findViewById(R.id.txt);
			ImageView arrow = (ImageView) rqView.findViewById(R.id.arrow);
			txt.setTextColor(Color.rgb(255, 255, 255));
			arrow.setImageResource(R.drawable.icon_arrow_down_white);
		}
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		subTitleView = (TextView) this.findViewById(R.id.subtitle);
		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);
		lvView = (BaseListView) this.findViewById(R.id.listView);
		lvView.setPullLoadEnable(true);
		lvView.setPullRefreshEnable(true);
		lvView.setXListViewListener(this);

		sortContainerView = (LinearLayout) this
				.findViewById(R.id.sortContainer);
		borderView = (LinearLayout) this.findViewById(R.id.border);
		rqView = (LinearLayout) this.findViewById(R.id.rq);
		slView = (LinearLayout) this.findViewById(R.id.sl);
	}

	private void doQueryTask() {
		// TODO Auto-generated method stub
		HashMap<String, String> urlParams = new HashMap<String, String>();

		int[] screenDisplay = AppUtil.getScreen(this);
		limit = (screenDisplay[1] / 80) + 10;

		urlParams.put("pageNum", pageNum + "");
		urlParams.put("reportType", "products");
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
			renderList(operation.getJSONArray("products"));
			onLoad();
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
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

	private void onLoad() {
		lvView.stopRefresh();
		lvView.stopLoadMore();
		lvView.setRefreshTime(AppUtil.getCurrentDateTime());
		isLoading = false;
	}

	@Override
	protected void onTaskAsyncStop() {
		super.onTaskAsyncStop();
		// onLoad();
	}

	public void onNetworkError(int taskId) {
		if (initQuery) {
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

			String metaData = map.get("metaData");
			String isMerge = map.get("isMerge");
			JSONObject saleData = null;
			try {
				saleData = new JSONObject(metaData);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 单笔记录
			if (isMerge.equals("0")) {
				holder.getMergeImgsView().setVisibility(View.GONE);
				holder.getPicView().setVisibility(View.VISIBLE);

				holder.getPicView().setImageURI(null);
				holder.getPicView().setBackgroundResource(
						R.drawable.default_img_placeholder);

				try {
					holder.getCountView().setText(
							getString(R.string.SALESREPORT_SORT_SLTXT) + "："
									+ saleData.getString("selling_count"));
					holder.getNameView().setText(saleData.getString("name"));
					holder.getPriceView().setVisibility(View.VISIBLE);
					holder.getPriceView()
							.setText(
									getString(R.string.CASHIER_SELLING_PRICE)
											+ "："
											+ AppUtil.toFixed(Double.parseDouble(saleData
													.getString("selling_price"))));

					JSONObject loadImg = new JSONObject();
					try {
						loadImg.put("position", position);
						loadImg.put("view", holder.getPicView());
						loadImg.put("pic", saleData.get("pic"));
						loadImg.put("id", saleData.get("pid"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Uri src = null;
					holder.getPicView().setTag(saleData.get("id"));

					if (hasRoot && hasProductDirPath) {
						String oldFileName = productDirPath + "/"
								+ saleData.get("id") + ".png";
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
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				holder.getMergeImgsView().setVisibility(View.VISIBLE);
				holder.getPicView().setVisibility(View.GONE);

				JSONArray products;
				try {
					holder.getCountView().setText(
							getString(R.string.SALESREPORT_SORT_SLTXT) + "："
									+ saleData.getString("totalCount"));
					holder.getNameView().setText(
							"合并记账("
									+ saleData.getJSONArray("cashierList")
											.length() + ")");
					holder.getPriceView().setVisibility(View.GONE);
					products = saleData.getJSONArray("products");
					ImageView one = (ImageView) holder.getMergeImgsView()
							.findViewById(R.id.one);
					ImageView two = (ImageView) holder.getMergeImgsView()
							.findViewById(R.id.two);
					ImageView three = (ImageView) holder.getMergeImgsView()
							.findViewById(R.id.three);
					ImageView four = (ImageView) holder.getMergeImgsView()
							.findViewById(R.id.four);
					int mergeMaxImgCount = 1;

					one.setImageDrawable(getResources().getDrawable(
							R.drawable.default_img_placeholder));
					two.setImageDrawable(getResources().getDrawable(
							R.drawable.default_img_placeholder));
					three.setImageDrawable(getResources().getDrawable(
							R.drawable.default_img_placeholder));
					four.setImageDrawable(getResources().getDrawable(
							R.drawable.default_img_placeholder));
					for (int i = 0; i < products.length(); i++) {
						JSONObject product = (JSONObject) products.get(i);

						// 加载合并记账中的商品图片
						if (mergeMaxImgCount < 5) {
							String pic = product.getString("pic");
							ImageView img;

							if (mergeMaxImgCount == 1) {
								img = one;
							} else if (mergeMaxImgCount == 2) {
								img = two;
							} else if (mergeMaxImgCount == 3) {
								img = three;
							} else {
								img = four;
							}

							if (pic != null && pic.length() > 0) {

								mergeMaxImgCount++;
								bpit = new BaseGetProductImageTask(img, pic,
										product.getString("pid")) {
									@Override
									protected void onPostExecute(Uri result) {
										super.onPostExecute(result);
										if (this.isCancelled()) {
											return;
										}
										if (imageViewReference != null) {
											ImageView view = (ImageView) imageViewReference.get();
											if (view != null) {
												if (result != null) {
													view.setImageURI(result);
													view.setBackgroundResource(0);
												}
											}

										}
									}
								};
								bpit.execute();
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			String date = map.get("date");
			holder.getDateView().setText(
					getString(R.string.SALESREPORT_SORT_DATETXT) + "："
							+ date.substring(5));

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
					return -1;
				} else if (h1d.getTime() == h2d.getTime()) {
					return 0;
				} else {
					return 1;
				}
			} else if (sort.equals("2")) {
				if (h1d.getTime() > h2d.getTime()) {
					return 1;
				} else if (h1d.getTime() == h2d.getTime()) {
					return 0;
				} else {
					return -1;
				}
			}

			return 0;
		}
	}

	class SortByCount implements Comparator {
		public int compare(Object o1, Object o2) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> h1 = (HashMap<String, String>) o1;
			@SuppressWarnings("unchecked")
			HashMap<String, String> h2 = (HashMap<String, String>) o2;

			Double olCount = null, o2Count = null;
			JSONObject saleData1 = null, saleData2 = null;
			try {
				saleData1 = new JSONObject(h1.get("metaData"));
				saleData2 = new JSONObject(h2.get("metaData"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String olIsMerge = h1.get("isMerge");
			String o2IsMerge = h2.get("isMerge");

			if (olIsMerge.equals("0")) {
				try {
					olCount = Double.parseDouble(saleData1
							.getString("selling_count"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (olIsMerge.equals("1")) {
				try {
					olCount = Double.parseDouble(saleData1
							.getString("totalCount"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (o2IsMerge.equals("0")) {
				try {
					o2Count = Double.parseDouble(saleData2
							.getString("selling_count"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (o2IsMerge.equals("1")) {
				try {
					o2Count = Double.parseDouble(saleData2
							.getString("totalCount"));
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (olCount == null || o2Count == null) {
				return 0;
			}

			if (sort.equals("3")) {
				if (olCount > o2Count) {
					return -1;
				} else if (olCount == o2Count) {
					return 0;
				} else {
					return 1;
				}
			} else if (sort.equals("4")) {
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
