package com.jituofu.ui;

import com.jituofu.R;
import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class UIHome extends UIGlobalTabView {
	int sysVersion = Build.VERSION.SDK_INT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (sysVersion >= 16) {
			setContentView(R.layout.home);

		}
		super.onCreate(savedInstanceState);
		setCurrentTab(1);
	}
}