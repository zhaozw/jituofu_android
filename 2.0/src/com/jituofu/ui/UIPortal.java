package com.jituofu.ui;

import org.json.JSONObject;

import android.os.Bundle;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;
 
public class UIPortal extends BaseUi {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portal);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		AppUtil.fetchUserFromServer(getApplicationContext(), this);
	}
	
	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);
		
		if(taskId == C.TASK.getuser){
			int resultStatus = message.getResultStatus();
			
			Bundle bundle = new Bundle();
			bundle.putBoolean("hideBack", true);
			
			if (resultStatus == 100) {
				JSONObject operation = message.getOperation();
				boolean hasId = operation.has("id");

				if (!hasId) {
					forward(UILogin.class, bundle);
					this.finish();
				} else {
					this.forward(UIHome.class);
					this.finish();
				}
			} else {
				forward(UILogin.class, bundle);
				this.finish();
			}
		}
	}
}
