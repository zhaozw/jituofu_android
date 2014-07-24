package com.jituofu.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseDateTimePicker;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.util.AppUtil;
import com.jituofu.util.DateUtil;

public class UISalesReport extends BaseUiAuth {
	private TextView titleView, currentdateView, startView, endView, lrView, cbView;
	private LinearLayout datetypeView;
	
	private String start, end;
	private BaseDateTimePicker dateTimePicker;
	private int startyear, startmonth, startday, endyear, endmonth, endday;
	private int whichDate;
	
	private String totalCost, totalPrice, totalCount, salesList;

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

		currentDateType = this.getString(R.string.SALESREPORT_TODAY);

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
		
		startView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				whichDate = 0;
				// TODO Auto-generated method stub
				if (startyear > 0 && startmonth > 0 && startday > 0) {
					dateTimePicker.showDateDialogOnly(startyear, startmonth, startday);
				} else {
					dateTimePicker.showDateDialogOnly();
				}
			}});
		endView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				whichDate = 1;
				// TODO Auto-generated method stub
				if (endyear > 0 && endmonth > 0 && endday > 0) {
					dateTimePicker.showDateDialogOnly(endyear, endmonth, endday);
				} else {
					dateTimePicker.showDateDialogOnly();
				}
			}});
	}

	private void updateView() {
		titleView.setText(R.string.SALESREPORT_TITLE);
		currentdateView.setText(currentDateType);
		
		LinearLayout lr = (LinearLayout) findViewById(R.id.lr);
		TextView lrKey = (TextView) lr.findViewById(R.id.key);
		lrView = (TextView) lr.findViewById(R.id.value);
		lrKey.setText(R.string.CASHIER_TODAY_ZLR);
		
		LinearLayout cb = (LinearLayout) findViewById(R.id.cb);
		TextView cbKey = (TextView) cb.findViewById(R.id.key);
		cbView = (TextView) cb.findViewById(R.id.value);
		cbKey.setText(R.string.CASHIER_TODAY_ZCB);
		cb.findViewById(R.id.border).setVisibility(View.GONE);
		
		LinearLayout fl = (LinearLayout) findViewById(R.id.fl);
		TextView flKey = (TextView) fl.findViewById(R.id.key);
		flKey.setText(R.string.CASHIER_SPFLMX);
		
		LinearLayout sp = (LinearLayout) findViewById(R.id.sp);
		TextView spKey = (TextView) sp.findViewById(R.id.key);
		spKey.setText(R.string.CASHIER_TODAY_ZCB);
		sp.findViewById(R.id.border).setVisibility(View.GONE);
		
		String[] time = AppUtil.getCurrentDateTime().split("\\s");
		start = time[0];
		end = time[0];
		
		String[] timeArray = DateUtil.getDateArray(time[0]);
		startyear = Integer.parseInt(timeArray[0]);
		startmonth = Integer.parseInt(timeArray[1]);
		startday = Integer.parseInt(timeArray[2]);
		
		endyear = Integer.parseInt(timeArray[0]);
		endmonth = Integer.parseInt(timeArray[1]);
		endday = Integer.parseInt(timeArray[2]);
		
		startView.setText(start);
		endView.setText(end);
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		titleView.setPadding(0, 0, AppUtil.getActualMeasure(this, 20), 0);
		
		datetypeView = (LinearLayout) this.findViewById(R.id.datetype);
		currentdateView = (TextView) this.findViewById(R.id.currentdate);
		
		startView = (TextView) this.findViewById(R.id.start);
		endView = (TextView) this.findViewById(R.id.end);
		
		dateTimePicker = new BaseDateTimePicker(this);
		dateTimePicker.setDateDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface di) {
				if(!dateTimePicker.isSetDate){
					return;
				}else{
					dateTimePicker.isSetDate = false;
				}
				
				// TODO Auto-generated method stub
				int[] ymd = dateTimePicker.getYMD();

				int year = ymd[0];
				int month = ymd[1];
				int day = ymd[2];
				
				String time = year + "-" + AppUtil.to2bit(month) + "-"
						+ AppUtil.to2bit(day);
				
				if(whichDate == 0){
					startyear = year;
					startmonth = month;
					startday = day;
					
					start = time;
					startView.setText(start);
				}else if(whichDate == 1){
					endyear = year;
					endmonth = month;
					endday = day;
					
					end = time;
					endView.setText(end);
				}
			}
		});
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
			dateType.add(this.getString(R.string.SALESREPORT_TODAY));
			dateType.add(this.getString(R.string.SALESREPORT_BEFOREDAY7));
			dateType.add(this.getString(R.string.SALESREPORT_AFTERDAY7));
			dateType.add(this.getString(R.string.SALESREPORT_BEFOREMONTH1));
			dateType.add(this.getString(R.string.SALESREPORT_AFTERMONTH1));
			
			MenuAdapter adapter = new MenuAdapter(this, dateType);
			popupViewListView.setAdapter(adapter);
			popupViewListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View view,
						int position, long id) {
					String txt = dateType.get(position);
					
					String[] currentDateTime = AppUtil.getCurrentDateTime().split("\\s");
					
					currentDateType = txt;
					currentdateView.setText(currentDateType);

					if (popupWindow != null) {
						popupWindow.dismiss();
					}
					
					start = currentDateTime[0];
					
					//今天
					if(txt.equals(getString(R.string.SALESREPORT_TODAY))){
						end = start;
					}else if(txt.equals(getString(R.string.SALESREPORT_BEFOREDAY7))){
						end = start;
						start = DateUtil.getBefore7(start);
					}else if(txt.equals(getString(R.string.SALESREPORT_AFTERDAY7))){
						end = DateUtil.getAfter7(start);
					}else if(txt.equals(getString(R.string.SALESREPORT_BEFOREMONTH1))){
						end = start;
						start = DateUtil.getBefore1Month(start);
					}else if(txt.equals(getString(R.string.SALESREPORT_AFTERMONTH1))){
						end = DateUtil.getAfter1Month(start);
					}else{
						end =start;
					}
					
					String[] startDate = DateUtil.getDateArray(start);
					String[] endDate = DateUtil.getDateArray(end);
					
					startyear = Integer.parseInt(startDate[0]);
					startmonth = Integer.parseInt(startDate[1]);
					startday = Integer.parseInt(startDate[2]);
					
					endyear = Integer.parseInt(endDate[0]);
					endmonth = Integer.parseInt(endDate[1]);
					endday = Integer.parseInt(endDate[2]);
					
					startView.setText(start);
					endView.setText(end);
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
