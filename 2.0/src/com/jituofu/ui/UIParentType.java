package com.jituofu.ui;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification.Builder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

public class UIParentType extends BaseUiAuth {
	private Bundle extraBundle;

	private LinearLayout topbarView, noDataView, againView;

	private ImageButton addView, editView;

	private TextView titleView;

	// 页面来源
	private String from;

	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;// 是首次查询还是分页查询

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_parent_type);

		initView();
		updateView();
		bindListener2View();

		// 查询分类
		initQuery = true;//标记初始化查询为true
		AppUtil.showLoadingPopup(this, R.string.SPFL_QUERY_LOADING);
		doQueryTask();
	}

	private void doQueryTask() {
		HashMap<String, String> urlParams = new HashMap<String, String>();

		int[] screenDisplay = AppUtil.getScreen(this);
		int limit = screenDisplay[1] / 42 + 10;

		urlParams.put("pageNum", pageNum + "");
		urlParams.put("limit", limit + "");

		try {
			doTaskAsync(C.TASK.gettypes, C.API.host + C.API.gettypes, urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();

		if (resultStatus == 100) {
			switch (taskId) {
			case C.TASK.gettypes:
				break;
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void bindListener2View() {
		this.onCustomBack();
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar3);

		editView = (ImageButton) topbarView.findViewById(R.id.rightbtn_1);
		addView = (ImageButton) topbarView.findViewById(R.id.rightbtn_2);
		titleView = (TextView) topbarView.findViewById(R.id.title);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void updateView() {
		extraBundle = this.getIntent().getExtras();

		if (extraBundle == null) {
			addView.setVisibility(View.VISIBLE);
			editView.setVisibility(View.VISIBLE);
		} else {
			from = extraBundle.getString("from");
		}

		titleView.setText(R.string.SPGL_MENU1_TXT2);
		if (Build.VERSION.SDK_INT < 16) {
			addView.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.add_small));
			editView.setBackgroundDrawable(this.getResources().getDrawable(
					R.drawable.edit_small));
		} else {
			addView.setBackground(this.getResources().getDrawable(
					R.drawable.add_small));
			editView.setBackground(this.getResources().getDrawable(
					R.drawable.edit_small));
		}

	}
}
