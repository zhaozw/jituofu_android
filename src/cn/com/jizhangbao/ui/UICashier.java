package cn.com.jizhangbao.ui;

import cn.com.jizhangbao.R;
import cn.com.jizhangbao.base.BaseUi;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

public class UICashier extends BaseUi {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cashier);
	}
}