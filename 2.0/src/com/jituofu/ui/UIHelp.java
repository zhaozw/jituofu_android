package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jituofu.R;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUi;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class UIHelp extends BaseUi {
	List<HashMap<String,String>> data;
	ListView lv;

	@Override
	protected void onBrReceive(String type){
		if(type.equals("RegisterAndLoginSuccess")){
			this.finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		onCustomBack();

		onUpdateUi();
		doTaskHelps();
		onBindUi();
	}
	
	@Override
	protected void onBindUi(){
		super.onBindUi();
		
		Button goQuickHelpBtn = (Button) this.findViewById(R.id.goQuickHelp);
		
		goQuickHelpBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				forward(UIFeedback.class);
			}});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void doTaskHelps() {
		AppUtil.showLoadingPopup(this, R.string.helquering);
		
		String deviceId = AppUtil.getDeviceId(this);

		HashMap<String, String> urlParams = new HashMap<String, String>();
		urlParams.put("pageNum", 1+"");
		urlParams.put("limit", 5+"");
		urlParams.put("sort", 1+"");

		try {
			this.doTaskAsync(C.TASK.gethelp, C.API.host + C.API.gethelp,
					urlParams);
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
			JSONArray helps = operation.getJSONArray("helps");
			
			if(helps.length() > 0){
				renderList(helps);
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	private void renderList(JSONArray helps) throws JSONException {
		lv = (ListView) findViewById(R.id.listView);
		data = new ArrayList<HashMap<String,String>>();
		
		for(int i=0; i<helps.length(); i++){
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = helps.getJSONObject(i);
			map.put("title", json.getString("title"));
			map.put("content", json.getString("content"));
			map.put("id", json.getString("id"));
			data.add(map);
		}

		SimpleAdapter sa = new SimpleAdapter(this, data, R.layout.helps_list_tem, new String[]{"title", "content", "id"}, new int[]{R.id.title, R.id.content, R.id.self});

		// 添加列表头部的线条
		final View border = getLayoutInflater().inflate(R.layout.border, null);
		lv.addHeaderView(border);
		lv.setAdapter(sa);
		// 修改最后一条数据的线条样式
		lv.post(new Runnable() {
			@Override
			public void run() {
				updateLastItemBorder();
				closePopupDialog();
			}
		});
		
		onBindUi2list();
	}
	
	private void onBindUi2list(){
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HashMap<String, String> map = (HashMap<String, String>) lv.getItemAtPosition(arg2);
				Bundle bundle = new Bundle();
				bundle.putString("id", map.get("id"));
				bundle.putString("title", map.get("title"));
				bundle.putString("content", map.get("content"));
				
				forward(UIHelpdetail.class, bundle);
			}});
	}

	@Override
	protected void onUpdateUi() {
		TextView title = (TextView) findViewById(R.id.title);

		title.setText(R.string.helpCenter);
	}

	// 更新列表最后一项边框样式
	private void updateLastItemBorder() {
		ListView lv = (ListView) findViewById(R.id.listView);
		int count = lv.getCount();
		View last = (View) lv.getChildAt(count - 1);
		LinearLayout border = (LinearLayout) last.findViewById(R.id.border);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 1);
		params.setMargins(0, 0, 0, 0);
		border.setLayoutParams(params);
	}
}