package com.jituofu.ui;

import com.jituofu.R;
import com.jituofu.base.BaseUi;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UIForgot extends BaseUi{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot);
		
		TextView title = (TextView) findViewById(R.id.title);
		TextView topBar2Right = (TextView) findViewById(R.id.topBar2Right);
		title.setText(R.string.forgottitle);
		topBar2Right.setText(R.string.help);
	}
}