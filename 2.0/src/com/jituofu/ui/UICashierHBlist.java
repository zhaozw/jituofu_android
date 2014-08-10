package com.jituofu.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.CustomProductsAdapter;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.ProductsViewHolder;
import com.jituofu.util.AppUtil;

public class UICashierHBlist extends BaseUiAuth implements BaseUiFormBuilder {
	private LinearLayout noDataView, borderView, border2View, totalBoxView;
	private Button okBtnView;
	private TextView products_numView, hjView, countView, titleView;

	private BaseGetProductImageTask bpit;
	private BaseListView lv;
	private CustomProductsAdapter customProductsAdapter;

	private boolean isDeleting = false;

	private double totalPrice = 0;
	private double totalCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier_hebinglist);

		initView();
		updateView();
		onBind();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isDeleting) {
			isDeleting = false;
			if (customProductsAdapter != null) {
				customProductsAdapter.notifyDataSetChanged();
				return false;
			}

		}

		return super.onKeyDown(keyCode, event);

	}

	private void onBind() {
		this.onCustomBack();

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int pos, long row) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent
						.getItemAtPosition(pos);
				isDeleting = true;

				Button deleteView = (Button) v.findViewById(R.id.delete);
				deleteView.setVisibility(View.VISIBLE);

				LinearLayout itemView = (LinearLayout) v
						.findViewById(R.id.item);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(
						AppUtil.getActualMeasure(getApplicationContext(), -70),
						0, 0, 0);
				itemView.setLayoutParams(lp);
				return false;
			}
		});

		okBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doSubmit();
			}
		});
	}

	private void updateView() {
		customProductsAdapter = customProductsAdapter == null ? new CustomProductsAdapter(
				this, UICashier.hbList) : customProductsAdapter;
		lv.setAdapter(customProductsAdapter);
		updateTotal();

		titleView.setText(R.string.CASHIER_HBTITLE);
	}

	private void updateTotal() {
		if (UICashier.hbList.size() <= 0) {
			noData();
			return;
		}

		totalCount = 0;
		totalPrice = 0;

		products_numView.setText("共计 " + UICashier.hbList.size() + " 种商品");

		for (int i = 0; i < UICashier.hbList.size(); i++) {
			HashMap<String, String> map = (HashMap<String, String>) UICashier.hbList
					.get(i);
			double sellingPrice = Double.parseDouble(map.get("sellingPrice"));
			double sellingCount = Double.parseDouble(map.get("sellingCount"));

			totalPrice += Double.parseDouble(AppUtil.toFixed(sellingCount
					* sellingPrice));
			totalCount += sellingCount;
		}
		hjView.setText(AppUtil.toFixed(totalPrice) + "");
		countView.setText("数量 " + AppUtil.toFixed(totalCount));
	}

	private void initView() {
		lv = (BaseListView) findViewById(R.id.listView);
		lv.setPullLoadEnable(false);
		lv.setPullRefreshEnable(false);

		products_numView = (TextView) findViewById(R.id.products_num);
		hjView = (TextView) findViewById(R.id.hj);
		countView = (TextView) findViewById(R.id.count);
		noDataView = (LinearLayout) findViewById(R.id.noData);

		titleView = (TextView) this.findViewById(R.id.title);

		borderView = (LinearLayout) this.findViewById(R.id.border);
		border2View = (LinearLayout) this.findViewById(R.id.border2);
		totalBoxView = (LinearLayout) this.findViewById(R.id.totalBox);

		okBtnView = (Button) this.findViewById(R.id.okBtn);

		if (UICashier.hbList.size() <= 0) {
			noData();
		}
	}

	private void noData() {
		noDataView.setVisibility(View.VISIBLE);
		((TextView) noDataView.findViewById(R.id.txt))
				.setText(R.string.CASHIER_NO_HBLIST);
		((TextView) noDataView.findViewById(R.id.action_btn))
				.setVisibility(View.GONE);
		lv.setVisibility(View.GONE);
		okBtnView.setVisibility(View.GONE);
		borderView.setVisibility(View.GONE);
		border2View.setVisibility(View.GONE);
		totalBoxView.setVisibility(View.GONE);
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();
		if (resultStatus == 100) {
			submitSuccess(operation);
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void submitSuccess(JSONObject operation) {
		UICashier.submitSuccess(this, operation);
		
		updateTotal();
		
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub
		UICashier.showPreview(this, UICashierHBlist.this);
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
	
	private void sendDeleteBroadCase(){
		Intent broadcast = new Intent();
		broadcast.setAction("com.jituofu.ui.ClearForm");
		broadcast.putExtra("type", "ClearForm");
		this.sendBroadcast(broadcast);
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
			final HashMap<String, String> map = (HashMap<String, String>) data
					.get(position);

			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.template_cashier_hblist_item, null);
				holder = new ProductsViewHolder(convertView);
				holder.setCountView((TextView) convertView
						.findViewById(R.id.count));
				holder.setNameView((TextView) convertView
						.findViewById(R.id.name));
				holder.setPriceView((TextView) convertView
						.findViewById(R.id.xj));
				holder.setDjView((TextView) convertView.findViewById(R.id.dj));
				holder.setPicView((ImageView) convertView
						.findViewById(R.id.pic));
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ProductsViewHolder) convertView.getTag();
			}

			holder.getNameView().setText(map.get("name"));
			holder.getCountView().setText("数量：" + map.get("sellingCount"));
			holder.getDjView().setText("销售单价：" + map.get("sellingPrice"));

			double sellingPrice = Double.parseDouble(map.get("sellingPrice"));
			double sellingCount = Double.parseDouble(map.get("sellingCount"));
			holder.getPriceView()
					.setText(
							"小计："
									+ AppUtil.toFixed(sellingCount
											* sellingPrice) + " 元");

			holder.getPicView().setImageURI(null);
			holder.getPicView().setBackgroundResource(
					R.drawable.default_img_placeholder);

			if (map.get("pid") != null) {
				getProductImg(holder.getPicView(), map.get("pic"),
						map.get("pid"));
			}

			if (!isDeleting) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 0, 0, 0);
				holder.getItemView().setLayoutParams(lp);
				holder.getDeleteView().setVisibility(View.GONE);
			}

			holder.getDeleteView().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (UICashier.hbList.remove(map)) {
						isDeleting = false;
						updateTotal();
						customProductsAdapter.notifyDataSetChanged();
						sendDeleteBroadCase();
					}
				}
			});

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
		private TextView nameView, priceView, countView, djView;

		private LinearLayout itemView;

		private Button deleteView;

		public ProductsViewHolder(View baseView) {
			this.baseView = baseView;
		}

		public LinearLayout getItemView() {
			if (itemView == null) {
				itemView = (LinearLayout) baseView.findViewById(R.id.item);
			}

			return itemView;
		}

		public Button getDeleteView() {
			if (deleteView == null) {
				deleteView = (Button) baseView.findViewById(R.id.delete);
			}

			return deleteView;
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

		public TextView getDjView() {
			if (djView == null) {
				djView = (TextView) baseView.findViewById(R.id.dj);
			}

			return djView;
		}

		public void setDjView(TextView view) {
			djView = view;
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
