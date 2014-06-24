package cn.com.jizhangbao.ui;

import cn.com.jizhangbao.R;
import cn.com.jizhangbao.base.BaseUi;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UIForgot2 extends BaseUi{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot2);
		
		TextView title = (TextView) findViewById(R.id.title);
		TextView actionTopRight = (TextView) findViewById(R.id.actionTopRight);
		title.setText(R.string.forgottitle2);
		actionTopRight.setText(R.string.help);
	}
}