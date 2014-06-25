package com.jituofu.ui;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.HttpUtil;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class UIHelpdetail extends BaseUi {
	private String id = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_detail);

		onUpdateUi();
		onBindUi();
	}

	@Override
	protected void onBindUi() {
		super.onBindUi();

		this.onCustomBack();

		ImageButton yes = (ImageButton) this.findViewById(R.id.yes);
		ImageButton no = (ImageButton) this.findViewById(R.id.no);

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doTaskTask("yes");

			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				doTaskTask("no");

			}
		});
	}

	@Override
	protected void onUpdateUi() {
		super.onUpdateUi();

		Bundle bundle = this.getIntent().getExtras();
		TextView title = (TextView) findViewById(R.id.title);
		TextView detail = (TextView) findViewById(R.id.detail);

		title.setText(bundle.getString("title"));
		id = bundle.getString("id");
		detail.setText(bundle.getString("content"));
	}

	private void doTaskTask(String type) {
		AppUtil.showLoadingPopup(this, R.string.cl);

		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("id", id + "");

		try {
			if (type.equals("yes")) {
				this.doTaskAsync(C.TASK.helpyes, C.API.host + C.API.helpyes,
						urlParams);
			} else if (type.equals("no")) {
				this.doTaskAsync(C.TASK.helpno, C.API.host + C.API.helpno,
						urlParams);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		if (resultStatus == 100) {
			JSONObject operation = message.getOperation();

			switch (taskId) {
			case C.TASK.helpyes:
				this.showToast(R.string.helpok);
				break;
			case C.TASK.helpno:
				this.showConfirmDialog(R.string.helpCenter, R.string.helpno, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						forward(UIFeedback.class);
						
					}}, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}});
				break;
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
}