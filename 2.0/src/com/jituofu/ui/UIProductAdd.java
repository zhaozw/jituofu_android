package com.jituofu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseUiBuilder;
import com.jituofu.base.BaseUiFormBuilder;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

public class UIProductAdd extends BaseUi implements BaseUiBuilder,
		BaseUiFormBuilder {
	//view对象
	private TextView titleView, timeView, typeView;
	private EditText nameView, priceView, countView, remarkView;
	private Button okBtnView;
	
	//表单值
	private String name, price, count, remark, typeId, typeName, picPath, time;
	
	//表单提交
	private boolean validated = false;
	
	public static int selectTypeRequestCode = 1;//分类选择

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_product_add);

		prepare();
		onUpdate();
		onBind();
	}

	@Override
	public void doSubmit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeSubmit() {
		// TODO Auto-generated method stub
		// 防止重复点击
		if (validated) {
			return;
		}

		validated = true;

		if (validation()) {

			doSubmit();
		}
		validated = false;
	}

	@Override
	public boolean validation() {
		// TODO Auto-generated method stub
		boolean result = false;
		
		name = getEditValue(nameView);
		price = AppUtil.trimAll(getEditValue(priceView));
		count = AppUtil.trimAll(getEditValue(countView));
		remark = getEditValue(remarkView);
		
		if(name == null){
			this.showToast(R.string.PRODUCT_PNAME_HINT);
		}else if(AppUtil.getStrLen(name) < 2 || AppUtil.getStrLen(name) > 15){
			this.showToast(R.string.PRODUCT_PNAME_ERROR);
		}else{
			result = true;
		}
		
		if(result){
			if(price == null){
				this.showToast(R.string.PRODUCT_PPRICE_HINT);
				result = false;
			}else if(!AppUtil.isAvailablePrice(price)){
				this.showToast(R.string.PRODUCT_PPRICE_ERROR);
				result = false;
			}else{
				double dp = Double.parseDouble(price);
				priceView.setText(AppUtil.toFixed(dp));
			}
		}
		
		if(result){
			if(count == null){
				this.showToast(R.string.PRODUCT_PCOUNT_HINT);
				result = false;
			}else if(!AppUtil.isAvailablePrice(count)){
				this.showToast(R.string.PRODUCT_PCOUNT_ERROR);
				result = false;
			}
		}
		
		if(result){
			if(typeId == null){
				this.showToast(R.string.PRODUCT_PTYPE_ERROR);
				result = false;
			}
		}
		
		if(result){
			if(time == null){
				this.showToast(R.string.PRODUCT_PTIME_ERROR);
				result = false;
			}
		}
		
		return result;
	}

	@Override
	public void onBind() {
		// TODO Auto-generated method stub
		this.onCustomBack();

		okBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				beforeSubmit();
			}
		});
		
		typeView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("from", C.COMMON.productSubmit);
				forwardForResult(UIParentType.class, selectTypeRequestCode, bundle);
			}});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		//选择分类回来
		if(requestCode == selectTypeRequestCode){
			if(data != null){
				String parentTypeId = data.getStringExtra("parentTypeId");
				String parentTypeName = data.getStringExtra("parentTypeName");
				String childTypeId = data.getStringExtra("childTypeId");
				String childTypeName = data.getStringExtra("childTypeName");
				
				if(childTypeId != null){
					typeId = childTypeId;
				}else{
					typeId = parentTypeId;
				}
				
				if(childTypeName != null){
					typeName = parentTypeName + this.getString(R.string.SPFL_SPEARATOR) + childTypeName;
				}else{
					typeName = parentTypeName;
				}
				
				typeView.setText(typeName);
			}
		}
	}

	@Override
	public void onUpdate() {
		// TODO Auto-generated method stub
		titleView.setText(R.string.PRODUCT_ADD_TITLE);
		
		time = AppUtil.getCurrentDateTime();
		timeView.setText(time);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		okBtnView = (Button) this.findViewById(R.id.okBtn);

		titleView = (TextView) findViewById(R.id.title);
		typeView = (TextView) findViewById(R.id.type);
		timeView = (TextView) findViewById(R.id.time);

		nameView = (EditText) findViewById(R.id.name);
		priceView = (EditText) findViewById(R.id.price);
		countView = (EditText) findViewById(R.id.count);
		remarkView = (EditText) findViewById(R.id.remark);
	}

}