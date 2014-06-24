package cn.com.jizhangbao.ui;

import org.json.JSONObject;

import android.os.Bundle;

import cn.com.jizhangbao.R;
import cn.com.jizhangbao.base.BaseMessage;
import cn.com.jizhangbao.base.BaseUi;
import cn.com.jizhangbao.base.C;
import cn.com.jizhangbao.util.AppUtil;

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

				//自动登录异常
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
