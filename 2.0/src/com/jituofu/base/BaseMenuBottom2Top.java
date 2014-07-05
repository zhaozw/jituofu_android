package com.jituofu.base;

import com.jituofu.R;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

public class BaseMenuBottom2Top extends PopupWindow{
	private Context context;
	private TextView btn1View, btn2View, cancelView;
	private String btn1Txt, btn2Txt, cancelTxt;
	
	private OnClickListener btn1Listener, btn2Listener;
	
	private View menuView;
	
	public BaseMenuBottom2Top(Context context){
		super(context);
		this.context = context;
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		menuView = inflater.inflate(R.layout.template_bottom2top_menu, null);
		
		btn1View = (TextView) menuView.findViewById(R.id.btn1);
		btn2View = (TextView) menuView.findViewById(R.id.btn2); 
		cancelView = (TextView) menuView.findViewById(R.id.cancel); 
		
		cancelView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}});
		
		this.setContentView(menuView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.animBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		menuView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				int height = menuView.findViewById(R.id.container).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});
	}
	
	public void setBtn1(int txt, OnClickListener listener){
		btn1View.setText(txt);
		btn1View.setOnClickListener(listener);
	}
	
	public void setBtn2(int txt, OnClickListener listener){
		btn2View.setText(txt);
		btn2View.setOnClickListener(listener);
	}
}
