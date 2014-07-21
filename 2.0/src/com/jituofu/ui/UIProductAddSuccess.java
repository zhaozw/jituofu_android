package com.jituofu.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseUiAuth;

public class UIProductAddSuccess extends BaseUiAuth {
	
	private TextView titleView, nameView, countView, dateView, priceView, typeView;
	private Button jzrkBtnView , gotoJztBtnView;
	private ImageView picView;
	private LinearLayout productInfoView;
	
	private Bundle extraBundle;
	private String name, price, count, pic, id, typeName, date;
	
	private BaseGetProductImageTask bpit;// 异步获取商品图片
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_product_add_success);

		initView();
		onUpdate();
		onBind();
	}
	
	private void onBind(){
		this.onCustomBack();
		
		jzrkBtnView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forward(UIProductAdd.class);
			}});
		gotoJztBtnView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forward(UICashier.class);
			}});
		productInfoView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("id", id);
				forward(UIProductDetail.class, bundle);
			}});
	}
	
	private void onUpdate(){
		titleView.setText(R.string.PRODUCT_ADD_SUCCESS);
		
		if(extraBundle != null){
			name = extraBundle.getString("name");
			price = extraBundle.getString("price");
			count = extraBundle.getString("count");
			pic = extraBundle.getString("pic");
			id = extraBundle.getString("id");
			typeName = extraBundle.getString("typeName");
			date = extraBundle.getString("date");
			
			nameView.setText(name);
			priceView.setText("进价："+price+" 元");
			countView.setText("本次入库："+count);
			if(pic != null && pic.length() > 0){
				BaseGetProductImageTask bpit = new BaseGetProductImageTask(
						picView, pic, id);
				bpit.execute();
			}
			typeView.setText("分类："+typeName);
			dateView.setText("时间："+date);
		}
	}
	
	private void initView(){
		titleView = (TextView) findViewById(R.id.title);
		nameView = (TextView) findViewById(R.id.name);
		countView = (TextView) findViewById(R.id.count);
		dateView = (TextView) findViewById(R.id.date);
		priceView = (TextView) findViewById(R.id.price);
		typeView = (TextView) findViewById(R.id.type);
		
		productInfoView = (LinearLayout) findViewById(R.id.productInfo);
		
		jzrkBtnView = (Button) findViewById(R.id.jzrkBtn);
		gotoJztBtnView = (Button) findViewById(R.id.gotoJztBtn);
		
		picView = (ImageView) findViewById(R.id.pic);
		
		extraBundle = this.getIntent().getExtras();
	}
}
