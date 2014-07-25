package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

public class UICashierToday extends BaseUiAuth {
	private TextView titleView, lrView, cbView, xseView, countView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_cashier_today);
		
		initView();
		updateView();
		onBind();
		doQueryTask();
	}
	
	private void initView(){
		titleView = (TextView) this.findViewById(R.id.title);
		xseView = (TextView) this.findViewById(R.id.zxs);
		countView = (TextView) this.findViewById(R.id.zbs);
		lrView = (TextView) this.findViewById(R.id.zlr);
		cbView = (TextView) this.findViewById(R.id.zcb);
	}
	
	private void updateView(){
		titleView.setText(R.string.SALESREPORT_TODAY_TITLE);
	}
	
	private void onBind(){
		this.onCustomBack();
	}
	
	private void doQueryTask() {
		String[] todayDate = AppUtil.getCurrentDateTime().split("\\s");
		
		AppUtil.showLoadingPopup(this, R.string.COMMON_CXZ);

		// TODO Auto-generated method stub
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("pageNum", 1 + "");
		urlParams.put("limit", 1 + "");
		urlParams.put("start", todayDate[0]);
		urlParams.put("end", todayDate[0]);
		
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
			xseView.setText(operation.getString("totalPrice"));
			countView.setText(operation.getString("totalCount"));
			cbView.setText(operation.getString("totalCost")+" 元");
			
			Double lr = (Double.parseDouble(operation.getString("totalPrice"))-Double.parseDouble(operation.getString("totalCost")));
			if(lr < 0){
				lrView.setTextColor(Color.parseColor("#ff5500"));
			}else{
				lrView.setTextColor(Color.parseColor("#000000"));
			}
			lrView.setText(AppUtil.toFixed(lr)+" 元");
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}
