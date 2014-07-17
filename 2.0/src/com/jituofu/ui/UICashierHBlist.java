package com.jituofu.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.CustomProductsAdapter;
import com.jituofu.ui.UIWareHouseParentTypeDetailProductsList.ProductsViewHolder;
import com.jituofu.util.AppUtil;

public class UICashierHBlist extends BaseUiAuth implements BaseUiFormBuilder {
	private TextView products_numView, hjView, countView;

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
	
	private void onBind(){
		lv.setOnItemLongClickListener(new OnItemLongClickListener(){

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
				return false;
			}});
	}

	private void updateView() {
		customProductsAdapter = customProductsAdapter == null ? new CustomProductsAdapter(
				this, UICashier.hbList) : customProductsAdapter;
		lv.setAdapter(customProductsAdapter);
		updateTotal();
	}

	private void updateTotal() {
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
		hjView.setText(totalPrice + "");
		countView.setText("数量 " + AppUtil.toFixed(totalCount));
	}

	private void initView() {
		lv = (BaseListView) findViewById(R.id.listView);
		lv.setPullLoadEnable(false);
		lv.setPullRefreshEnable(false);

		products_numView = (TextView) findViewById(R.id.products_num);
		hjView = (TextView) findViewById(R.id.hj);
		countView = (TextView) findViewById(R.id.count);
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
			holder.getDjView().setText("单价：" + map.get("sellingPrice"));

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
			getProductImg(holder.getPicView(), map.get("pic"), map.get("pid"));

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
