package com.jituofu.base;

import java.util.Calendar;

import com.jituofu.util.AppUtil;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class BaseDateTimePicker {
	private Context context;
	private Calendar cal;
	private Dialog dateDialog, timeDialog;
	
	private int year, month, day, hour, minute, second;
	
	private boolean is24;
	
	private OnDismissListener dateDismissListener, timeDismissListener;
	
	ContextThemeWrapper themedContext;
	
	public BaseDateTimePicker(Context context){
		this.context = context;
		
		is24 = AppUtil.is24(context);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			themedContext = new ContextThemeWrapper(context,
					android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
		} else {
			themedContext = new ContextThemeWrapper(context,
					android.R.style.Theme_Light_NoTitleBar);
		}
	}
	
	public void setDateDismissListener(OnDismissListener listener){
		dateDismissListener = listener;
	}
	public void setTimeDismissListener(OnDismissListener listener){
		timeDismissListener = listener;
	}
	
	public void showDateDialog(){
		cal = Calendar.getInstance();
		dateDialog = new DatePickerDialog(
				themedContext,
            new DatePickerDialog.OnDateSetListener() {
            	@Override
                public void onDateSet(DatePicker dp, int y, int m, int d) {
            		year = y;
            		month = m+1;
            		day = d;
            		
            		showTimeDialog();
                }
            }, 
            cal.get(Calendar.YEAR), // 传入年份
            cal.get(Calendar.MONTH), // 传入月份
            cal.get(Calendar.DAY_OF_MONTH) // 传入天数
        );
		
		if(dateDismissListener != null){
			dateDialog.setOnDismissListener(dateDismissListener);
		}
		
		setHMS(Calendar.HOUR_OF_DAY, Calendar.MINUTE, 00);
		dateDialog.show();
	}
	
	public void showDateDialog(int y, int m, int d){
		cal = Calendar.getInstance();
		dateDialog = new DatePickerDialog(
				themedContext,
            new DatePickerDialog.OnDateSetListener() {
            	@Override
                public void onDateSet(DatePicker dp, int y, int m, int d) {
            		year = y;
            		month = m+1;
            		day = d;
            		
            		showTimeDialog();
                }
            }, 
            y, // 传入年份
            m-1, // 传入月份
            d // 传入天数
        );
		dateDialog.show();
	}
	
	public void setHMS(int h, int m, int s){
		hour = h;
		minute = m;
		second = s;
	}
	
	public int[] getYMD(){
		return new int[]{year, month, day};
	}
	
	public int[] getHMS(){
		return new int[]{hour, minute, 00};
	}
	
	public void showTimeDialog(){
		cal = Calendar.getInstance();
		timeDialog = new TimePickerDialog(
				themedContext, new TimePickerDialog.OnTimeSetListener(){

					@Override
					public void onTimeSet(TimePicker tp, int h, int m) {
						// TODO Auto-generated method stub
						hour = h;
						minute = m;
						
					}}, hour, minute, is24
        );
		
		if(timeDismissListener != null){
			timeDialog.setOnDismissListener(timeDismissListener);
		}
		
		timeDialog.show();
	}
}
