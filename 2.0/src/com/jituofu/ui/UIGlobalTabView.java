package com.jituofu.ui;

import com.jituofu.R;
import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
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
public class UIGlobalTabView extends UITabActivity {
	private TabHost tabHost;
	int sysVersion = Build.VERSION.SDK_INT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tabHost = getTabHost();

		Intent cashierIntent = new Intent(this, UICashier.class);
		Intent ihomeIntent = new Intent(this, UIIhome.class);
		Intent idataIntent = new Intent(this, UIIdata.class);
		Intent moreIntent = new Intent(this, UIMore.class);

		tabHost.addTab(tabHost
				.newTabSpec(R.string.cashiertitle + "")
				.setContent(cashierIntent)
				.setIndicator(this.getString(R.string.cashiertitle),
						this.getResources().getDrawable(R.drawable.cashier_tab)));

		tabHost.addTab(tabHost
				.newTabSpec(R.string.ihometitle + "")
				.setContent(ihomeIntent)
				.setIndicator(this.getString(R.string.ihometitle),
						this.getResources().getDrawable(R.drawable.ihome_tab)));

		tabHost.addTab(tabHost
				.newTabSpec(R.string.idatatitle + "")
				.setContent(idataIntent)
				.setIndicator(this.getString(R.string.idatatitle),
						this.getResources().getDrawable(R.drawable.idata_tab)));

		tabHost.addTab(tabHost
				.newTabSpec(R.string.moretitle + "")
				.setContent(moreIntent)
				.setIndicator(this.getString(R.string.moretitle),
						this.getResources().getDrawable(R.drawable.more_tab)));

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String arg0) {
				updateTabStyle();
			}

		});
		updateTabStyle();
	}

	@Override
	public void onTabChanged(String arg0) {
	}

	/**
	 * 更新tab样式
	 */
	@SuppressLint({ "NewApi", "ResourceAsColor" })
	private void updateTabStyle() {

		tabHost.setFocusable(false);
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			View view = tabHost.getTabWidget().getChildAt(i);
			TextView title = (TextView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			ImageView icon = (ImageView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.icon);

			tabHost.getTabWidget().setStripEnabled(false);

			title.setTextSize(10);

			if (sysVersion < 16) {
				view.setBackgroundColor(R.color.tabBackground);
			} else {
				title.setPadding(0, 0, 0, 10);
				icon.setPadding(0, 10, 0, 0);
				view.setBackground(null);
			}

			if (tabHost.getCurrentTab() == i) {
				title.setTextColor(this.getResources().getColorStateList(
						R.color.tabActiveTextColor));
				if (i == 0) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.jizhangtai_click));
				}
				if (i == 1) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.store_click));
				}
				if (i == 2) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.shuju_click));
				}
				if (i == 3) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.more_click));
				}
			} else {
				title.setTextColor(this.getResources().getColorStateList(
						R.color.tabNormalTextColor));
				if (i == 0) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.jizhangtai));
				}
				if (i == 1) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.store));
				}
				if (i == 2) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.shuju));
				}
				if (i == 3) {
					icon.setImageDrawable(this.getResources().getDrawable(
							R.drawable.more));
				}
			}
		}
	}

	/**
	 * 设置高亮tab
	 * @param index
	 */
	public void setCurrentTab(int index) {
		tabHost.setCurrentTab(index);
	}
}