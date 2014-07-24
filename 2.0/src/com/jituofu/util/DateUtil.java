package com.jituofu.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static String getBefore7(String specifiedDay){
		Calendar c = Calendar.getInstance();
		Date date = null;
		String style = "yyyy-MM-dd";
		
	    try {
			date = new SimpleDateFormat(style).parse(specifiedDay);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    c.setTime(date);
	    int day = c.get(Calendar.DATE);
	    c.set(Calendar.DATE, day-7);
	    
	    String dayBefore7 = new SimpleDateFormat(style).format(c.getTime());
	    return dayBefore7;
	}
	
	public static String getAfter7(String specifiedDay){
		Calendar c = Calendar.getInstance();
		Date date = null;
		String style = "yyyy-MM-dd";
		
	    try {
			date = new SimpleDateFormat(style).parse(specifiedDay);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    c.setTime(date);
	    int day = c.get(Calendar.DATE);
	    c.set(Calendar.DATE, day+7);
	    
	    String dayBefore7 = new SimpleDateFormat(style).format(c.getTime());
	    return dayBefore7;
	}
	
	public static String getAfter1Month(String specifiedDay){
		Calendar c = Calendar.getInstance();
		Date date = null;
		String style = "yyyy-MM-dd";
		
	    try {
			date = new SimpleDateFormat(style).parse(specifiedDay);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    c.setTime(date);
	    int month = c.get(Calendar.MONTH);
	    c.set(Calendar.MONTH, month+1);
	    
	    String after1Month = new SimpleDateFormat(style).format(c.getTime());
	    return after1Month;
	}
	
	public static String getBefore1Month(String specifiedDay){
		Calendar c = Calendar.getInstance();
		Date date = null;
		String style = "yyyy-MM-dd";
		
	    try {
			date = new SimpleDateFormat(style).parse(specifiedDay);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    c.setTime(date);
	    int month = c.get(Calendar.MONTH);
	    c.set(Calendar.MONTH, month-1);
	    
	    String after1Month = new SimpleDateFormat(style).format(c.getTime());
	    return after1Month;
	}
	
	public static String[] getDateArray(String specifiedDay){
		String[] date = specifiedDay.split("-");
		
		return date;
	}
}
