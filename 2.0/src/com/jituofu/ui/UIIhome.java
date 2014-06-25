package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.base.BaseUi;
import com.jituofu.util.AppUtil;
import com.jituofu.util.StorageUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class UIIhome extends BaseUiAuth {
	int sysVersion = Build.VERSION.SDK_INT;
	
	@Override
	protected void onBrReceive(String type){
		if(type.equals("ClearActivitiesBroadcast")){
			this.finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ihome);

		if(sysVersion < 16){
			LinearLayout globalTop = (LinearLayout) this.findViewById(R.id.globalTop);
			globalTop.setVisibility(View.GONE);
		}
		
		setView();

		try {
			updateView();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		onBindUi();
	}

	@Override
	protected void onBindUi() {
		super.onBindUi();
		
		LinearLayout security = (LinearLayout) this.findViewById(R.id.security);

		security.setOnClickListener(new LinearLayoutClick());
	}

	private void setView() {
		// 记账台
		LinearLayout jzt = (LinearLayout) findViewById(R.id.jzt);
		ImageView jztIcon = (ImageView) jzt.findViewById(R.id.icon);
		TextView jztTxt = (TextView) jzt.findViewById(R.id.txt);
		jztIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.jizhangtai_icon));
		jztTxt.setText(R.string.cashiertitle);

		// 商品管理
		LinearLayout spgl = (LinearLayout) findViewById(R.id.spgl);
		ImageView spglIcon = (ImageView) spgl.findViewById(R.id.icon);
		TextView spglTxt = (TextView) spgl.findViewById(R.id.txt);
		spglIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.shangpin));
		spglTxt.setText(R.string.shangpinguanli);

		// 历史销售记录
		LinearLayout lsxsjl = (LinearLayout) findViewById(R.id.lsxsjl);
		ImageView lsxsjlIcon = (ImageView) lsxsjl.findViewById(R.id.icon);
		TextView lsxsjlTxt = (TextView) lsxsjl.findViewById(R.id.txt);
		lsxsjlIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.clock));
		lsxsjlTxt.setText(R.string.lsxsjl);
		lsxsjl.findViewById(R.id.border).setVisibility(View.INVISIBLE);

		// 小店设置
		LinearLayout settings = (LinearLayout) findViewById(R.id.settings);
		ImageView settingsIcon = (ImageView) settings.findViewById(R.id.icon);
		TextView settingsTxt = (TextView) settings.findViewById(R.id.txt);
		settingsIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.setting));
		settingsTxt.setText(R.string.storesettings);

		// 安全中心
		LinearLayout security = (LinearLayout) findViewById(R.id.security);
		ImageView securityIcon = (ImageView) security.findViewById(R.id.icon);
		TextView securityTxt = (TextView) security.findViewById(R.id.txt);
		securityIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.security));
		securityTxt.setText(R.string.securitysettings);
		security.findViewById(R.id.border).setVisibility(View.INVISIBLE);
	}

	protected void updateView() throws UnsupportedEncodingException {
		TextView username = (TextView) findViewById(R.id.username);
		TextView email = (TextView) findViewById(R.id.email);

		byte[] userInfo = StorageUtil.readInternalStoragePrivate(this, C.DIRS.userInfoFileName);
		String userInfoVal = null;
		if (userInfo.length > 0 && userInfo[0] != 0) {
			userInfoVal = new String(userInfo, "UTF-8");
		} else {
			// 如果本地没有保存用户信息,就从本地读取userId后,再发送一次获取用户信息的请求
			byte[] userId = StorageUtil.readInternalStoragePrivate(this, C.DIRS.userIdFileName);

			String userIdVal = null;

			if (userId.length > 0 && userId[0] != 0) {
				userIdVal = new String(userId, "UTF-8");
			}

			if (userIdVal != null) {
				AppUtil.fetchUserFromServer(getApplicationContext(), this);
			} else {
				// 如果本地没有userId,就跳转登录页面
				this.forward(UILogin.class);
				this.finish();
			}
		}
		if (userInfoVal != null) {
			try {
				JSONObject ui = new JSONObject(userInfoVal);
				username.setText(ui.getString("username"));
				email.setText(ui.getString("email"));

				int sysVersion = Build.VERSION.SDK_INT;
				if (sysVersion >= 16) {
					TextView title = (TextView) findViewById(R.id.title);
					title.setText(ui.getString("username"));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onTaskComplete(int taskId, BaseMessage message)
			throws Exception {
		super.onTaskComplete(taskId, message);

		// 如果获取到用户信息就保存到本地
		if (taskId == C.TASK.getuser) {
			int resultStatus = message.getResultStatus();

			if (resultStatus == 100) {
				JSONObject operation = message.getOperation();
				boolean hasId = operation.has("id");

				if (hasId) {
					try {
						updateView();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	class LinearLayoutClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.security:
				forward(UISecurity.class);
				break;
			default:
				break;
			}
		}
	}
}