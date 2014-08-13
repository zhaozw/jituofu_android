package com.jituofu.ui;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UIMore extends BaseUiAuth {
	private int sysVersion = Build.VERSION.SDK_INT;

	private TextView titleView;
	private LinearLayout topBarView, updateView, aboutView, plView,
			feedbackView;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		UIGlobalTabView.tabHost.setCurrentTab(1);
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_more);

		initView();

		if (sysVersion < 16) {
			topBarView.setVisibility(View.GONE);
		}

		updateView();
		onBind();
	}

	protected void onBind() {
		this.onCustomBack();

		updateView.setOnClickListener(new LinearLayoutClick());
		aboutView.setOnClickListener(new LinearLayoutClick());
		plView.setOnClickListener(new LinearLayoutClick());
		feedbackView.setOnClickListener(new LinearLayoutClick());
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		topBarView = (LinearLayout) this.findViewById(R.id.topBar);
		updateView = (LinearLayout) this.findViewById(R.id.update);
		aboutView = (LinearLayout) this.findViewById(R.id.about);
		plView = (LinearLayout) this.findViewById(R.id.pl);
		feedbackView = (LinearLayout) this.findViewById(R.id.feedback);
	}

	protected void updateView() {
		titleView.setText(R.string.moretitle);
		((LinearLayout) topBarView.findViewById(R.id.back)).setVisibility(View.GONE);

		ImageView updateViewIcon = (ImageView) updateView.findViewById(R.id.icon);
		TextView updateViewTxt = (TextView) updateView.findViewById(R.id.txt);
		updateViewIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.update_icon));
		updateViewTxt.setText(R.string.COMMON_CHECKUPDATE_TITLE);

		ImageView aboutViewIcon = (ImageView) aboutView.findViewById(R.id.icon);
		TextView aboutViewTxt = (TextView) aboutView.findViewById(R.id.txt);
		aboutViewIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.about));
		aboutViewTxt.setText(R.string.COMMON_ABOUT);

		ImageView plViewIcon = (ImageView) plView.findViewById(R.id.icon);
		TextView plViewTxt = (TextView) plView.findViewById(R.id.txt);
		plViewIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.pinglun));
		plViewTxt.setText(R.string.COMMON_PINGLUN_TITLE);

		ImageView feedbackViewIcon = (ImageView) feedbackView.findViewById(R.id.icon);
		TextView feedbackViewTxt = (TextView) feedbackView.findViewById(R.id.txt);
		feedbackViewIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.fankui));
		feedbackViewTxt.setText(R.string.feedbackTitle);
		feedbackView.findViewById(R.id.border).setVisibility(View.INVISIBLE);
	}

	class LinearLayoutClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.update:
				forward(UIUpdate.class);
				break;
			case R.id.about:
				forward(UIAbout.class);
				break;
			case R.id.pl:
				AppUtil.gotoMarket(UIMore.this);
				break;
			case R.id.feedback:
				forward(UIFeedback.class);
				break;
			default:
				break;
			}
		}
	}
}