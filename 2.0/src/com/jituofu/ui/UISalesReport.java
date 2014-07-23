package com.jituofu.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.util.AppUtil;

public class UISalesReport extends BaseUiAuth {
	private TextView titleView, currentdateView;
	private LinearLayout datetypeView;

	// 自定义弹出窗口
	private PopupWindow popupWindow;
	private LinearLayout popupView;
	private ListView popupViewListView;
	private ArrayList<String> dateType;
	private String currentDateType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_salesreport);

		currentDateType = "今天";

		initView();
		updateView();
		onBind();
	}

	private void onBind() {
		datetypeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showWindow(v);
			}
		});
	}

	private void updateView() {
		titleView.setText(R.string.SALESREPORT_TITLE);
		currentdateView.setText(currentDateType);
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		titleView.setPadding(0, 0, AppUtil.getActualMeasure(this, 20), 0);
		
		datetypeView = (LinearLayout) this.findViewById(R.id.datetype);
		currentdateView = (TextView) this.findViewById(R.id.currentdate);
	}

	private void showWindow(View parent) {

		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			popupView = (LinearLayout) layoutInflater.inflate(
					R.layout.template_popupwindow_arrow_right, null);

			popupViewListView = (ListView) popupView
					.findViewById(R.id.listView);
			// 加载数据
			dateType = new ArrayList<String>();
			dateType.add("今天");
			dateType.add("最近7天");
			dateType.add("最近1个月");
			dateType.add("最近3个月");
			
			MenuAdapter adapter = new MenuAdapter(this, dateType);
			popupViewListView.setAdapter(adapter);
			popupViewListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long id) {

					currentDateType = dateType.get(position);
					currentdateView.setText(currentDateType);

					if (popupWindow != null) {
						popupWindow.dismiss();
					}
				}
			});

			// 创建一个PopuWidow对象
			popupWindow = new PopupWindow(popupView, 250, 350);

			// 使其聚集
			popupWindow.setFocusable(true);
			// 设置允许在外点击消失
			popupWindow.setOutsideTouchable(true);

			// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
		}

		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		int xPos = parent.getLeft();
		int yPos = (int) (parent.getTop() - parent.getHeight() / 2 + 10);

		popupWindow.showAsDropDown(parent, xPos, yPos);
	}

	public class MenuAdapter extends BaseAdapter {

		private Context context;

		private List<String> list;

		public MenuAdapter(Context context, List<String> list) {

			this.context = context;
			this.list = list;

		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {

			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {

			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.template_popupwindow_menus_item, null);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.txtView = (TextView) convertView.findViewById(R.id.txt);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String txt = list.get(position);
			holder.txtView.setText(txt);

			if (currentDateType.equals(txt)) {
				holder.txtView.setVisibility(View.GONE);
			}else{
				holder.txtView.setVisibility(View.VISIBLE);
			}

			return convertView;
		}
	}

	static class ViewHolder {
		TextView txtView;
	}
}
