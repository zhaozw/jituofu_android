package com.jituofu.ui;

import org.json.JSONObject;

import android.os.Bundle;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

public class UIPortal extends BaseUi {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portal);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.fetchUserFromServer(getApplicationContext(), this);
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();

		if (taskId == C.TASK.getuser) {
			Bundle bundle = new Bundle();
			bundle.putBoolean("hideBack", true);

			if (resultStatus == 100) {
				boolean hasId = operation.has("id");
				if (!hasId) {
					forward(UILogin.class, bundle);
					this.finish();
				} else {
					AppUtil.fetchStoreSettingsFromServer(
							getApplicationContext(), this);
				}
			} else {
				forward(UILogin.class, bundle);
				this.finish();
			}
		} else if (taskId == C.TASK.storesettingsget) {
			if (resultStatus == 100) {
				String storeSettings = operation.getString("storeSettings");
				StorageUtil.writeInternalStoragePrivate(this, C.DIRS.storeSettingsCacheFileName, storeSettings);
				this.forward(UIHome.class);
				this.finish();
			}
		}
	}
}
