package com.jituofu.ui;

import com.jituofu.R;
import android.os.Build;
import android.os.Bundle;

@SuppressWarnings("deprecation")
public class UIHome extends UIGlobalTabView {
	private int sysVersion = Build.VERSION.SDK_INT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (sysVersion >= 16) {
			setContentView(R.layout.home);

		}
		super.onCreate(savedInstanceState);
		setCurrentTab(1);
	}
}