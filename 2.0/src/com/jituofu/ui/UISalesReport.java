package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseDateTimePicker;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.DateUtil;

public class UISalesReport extends BaseUiAuth {
	private TextView titleView, currentdateView, startView, endView, lrView, cbView, xseView, countView;
	private LinearLayout datetypeView;
	private Button queryBtnView;
	
	private String start, end;
	private BaseDateTimePicker dateTimePicker;
	private int startyear, startmonth, startday, endyear, endmonth, endday;
	private int whichDate;
	private int sort = 1;
	private int pageNum = 1;
	private int limit = 10;
	
	private String totalCost, totalPrice, totalCount, totalLr;

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
		doQueryTask();
	}

	private void onBind() {
		this.onCustomBack();
		
		queryBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doQueryTask();
			}
		});
		
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
		
		LinearLayout lrBox = (LinearLayout) findViewById(R.id.lr);
		lrBox.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("start", start);
				bundle.putString("end", end);
				
				forward(UISalesReportLR.class, bundle);
			}});
		LinearLayout cbBox = (LinearLayout) findViewById(R.id.cb);
		cbBox.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("start", start);
				bundle.putString("end", end);
				
				forward(UISalesReportCB.class, bundle);
			}});
		LinearLayout spBox = (LinearLayout) findViewById(R.id.sp);
		spBox.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("start", start);
				bundle.putString("end", end);
				
				forward(UISalesReportProductList.class, bundle);
			}});
		
		LinearLayout flBox = (LinearLayout) findViewById(R.id.fl);
		flBox.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("start", start);
				bundle.putString("end", end);
				
				forward(UISalesReportParentTypeList.class, bundle);
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
		spKey.setText(R.string.CASHIER_SCSPMX);
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
		
		queryBtnView = (Button) this.findViewById(R.id.queryBtn);
		
		xseView = (TextView) this.findViewById(R.id.xse);
		countView = (TextView) this.findViewById(R.id.count);
		
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

	@SuppressWarnings("deprecation")
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
					
					doQueryTask();
				}
			});

			// 创建一个PopuWidow对象
			popupWindow = new PopupWindow(popupView, 200, 350);

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
	
	private void doQueryTask() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.CHINA);
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = simpleDateFormat.parse(start);
			endDate = simpleDateFormat.parse(end);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(startyear != endyear){
			this.showToast(R.string.SALESREPORT_YEARERROR);
			return;
		}else if(endDate != null && startDate !=null && endDate.getTime() < startDate.getTime()){
			this.showToast(R.string.SALESREPORT_DATEERROR);
			return;
		}
		
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
			this.doTaskAsync(C.TASK.salesreport, C.API.host
					+ C.API.salesreport, urlParams);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
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
			totalCost = operation.getString("totalCost");
			totalCount = operation.getString("totalCount");
			totalPrice = operation.getString("totalPrice");
			
			xseView.setText(totalPrice);
			countView.setText(totalCount);
			cbView.setText(totalCost);
			
			Double lr = (Double.parseDouble(operation.getString("totalPrice"))-Double.parseDouble(operation.getString("totalCost")));
			if(lr < 0){
				lrView.setTextColor(Color.parseColor("#ff5500"));
			}else{
				lrView.setTextColor(Color.parseColor("#000000"));
			}
			totalLr = AppUtil.toFixed(lr);
			lrView.setText(totalLr);
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}
