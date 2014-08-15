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

public class UIIhome extends BaseUiAuth {
	private int sysVersion = Build.VERSION.SDK_INT;

	private LinearLayout userinfoView;

	private long preBackTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (preBackTime <= 0) {
				preBackTime = System.currentTimeMillis();
				this.showToast(R.string.COMMON_EXIT);
			} else {
				if (System.currentTimeMillis() - preBackTime <= 3000) {
					preBackTime = 0;
					System.exit(0);
					return super.onKeyDown(keyCode, event);
				} else {
					this.showToast(R.string.COMMON_EXIT);
					preBackTime = System.currentTimeMillis();
					return true;
				}
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onBrReceive(String type) {
		if (type.equals("ClearActivitiesBroadcast")) {
			this.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ihome);

		if (sysVersion < 16) {
			LinearLayout topBar = (LinearLayout) this.findViewById(R.id.topBar);
			topBar.setVisibility(View.GONE);
		}

		initView();

		try {
			updateView();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		onBindUi();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			updateView();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onBindUi() {
		super.onBindUi();

		userinfoView.setOnClickListener(new LinearLayoutClick());

		LinearLayout security = (LinearLayout) this.findViewById(R.id.security);
		LinearLayout spgl = (LinearLayout) this.findViewById(R.id.spgl);
		LinearLayout lsxsjl = (LinearLayout) this.findViewById(R.id.lsxsjl);
		LinearLayout settings = (LinearLayout) findViewById(R.id.settings);

		security.setOnClickListener(new LinearLayoutClick());
		spgl.setOnClickListener(new LinearLayoutClick());
		lsxsjl.setOnClickListener(new LinearLayoutClick());
		settings.setOnClickListener(new LinearLayoutClick());
	}

	private void initView() {
		userinfoView = (LinearLayout) findViewById(R.id.userinfo);

		// 记账台
		LinearLayout jzt = (LinearLayout) findViewById(R.id.jzt);
		ImageView jztIcon = (ImageView) jzt.findViewById(R.id.icon);
		TextView jztTxt = (TextView) jzt.findViewById(R.id.txt);
		jztIcon.setImageDrawable(this.getResources().getDrawable(
				R.drawable.jizhangtai_icon));
		jztTxt.setText(R.string.CASHIER_TITLE);
		jzt.setVisibility(View.GONE);

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

		byte[] userInfo = StorageUtil.readInternalStoragePrivate(this,
				C.DIRS.userInfoFileName);
		byte[] storeSetting = StorageUtil.readInternalStoragePrivate(this,
				C.DIRS.storeSettingsCacheFileName);

		String userInfoVal = null;
		String storeSettingVal = null;

		if (storeSetting.length > 0 && storeSetting[0] != 0) {
			storeSettingVal = new String(storeSetting, "UTF-8");
		} else {
			// 获取商户信息
			AppUtil.fetchStoreSettingsFromServer(getApplicationContext(), this);
		}

		if (userInfo.length > 0 && userInfo[0] != 0) {
			userInfoVal = new String(userInfo, "UTF-8");
		} else {
			// 如果本地没有保存用户信息,就从本地读取userId后,再发送一次获取用户信息的请求
			byte[] userId = StorageUtil.readInternalStoragePrivate(this,
					C.DIRS.userIdFileName);

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

					// 如果有设置了商户名称就使用商户名称做标题
					if (storeSettingVal != null) {
						JSONObject ss = new JSONObject(storeSettingVal);

						if (ss.has("name") && ss.getString("name").length() > 0) {
							title.setText(ss.getString("name"));
						} else {
							title.setText(ui.getString("username"));
						}
					} else {
						title.setText(ui.getString("username"));
					}
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
		
		int resultStatus = message.getResultStatus();
		JSONObject operation = message.getOperation();
		
		// 如果获取到用户信息就保存到本地
		if (taskId == C.TASK.getuser) {

			if (resultStatus == 100) {

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
		} else if (taskId == C.TASK.storesettingsget) {
			if (resultStatus == 100) {
				operation = message.getOperation();
				if (operation.has("storeSettings")) {
					String storeSettings = operation.getString("storeSettings");
					StorageUtil.writeInternalStoragePrivate(this,
							C.DIRS.storeSettingsCacheFileName, storeSettings);
					JSONObject ss = new JSONObject(storeSettings);
					TextView title = (TextView) findViewById(R.id.title);
					title.setText(ss.getString("name"));
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
			case R.id.spgl:
				forward(UIProductManager.class);
				break;
			case R.id.lsxsjl:
				forward(UISalesReport.class);
				break;
			case R.id.settings:
				forward(UIStoreSettings.class);
				break;
			case R.id.userinfo:
				forward(UIUserSettings.class);
				break;
			default:
				break;
			}
		}
	}
}