package com.jituofu.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseListView.BaseListViewListener;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

public class UIParentType extends BaseUiAuth implements OnClickListener,
		BaseListViewListener {
	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;

	private Bundle extraBundle;

	private LinearLayout topbarView, noDataView, againView;

	private ImageButton addView, editView;

	private TextView titleView, noDataAddTypeView;

	private BaseListView lv;

	// 页面来源
	private String from;

	// 查询分类相关
	private int pageNum = 1;
	private boolean initQuery = false;// 是首次查询还是分页查询
	private ArrayList<HashMap<String, String>> datList = new ArrayList<HashMap<String, String>>();

	private CustomAdapter customAdapter;

	private boolean isRefresh, isLoadMore, isLoading, isEditing;

	private ArrayList<String> typesId = new ArrayList<String>();// 存储所有分类的id，以免重复加载
	
	private int whichRadio;//删除对话框中选择了哪一项
	private String whichOtherTypeId;//要移动到的分类id

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isEditing) {
			isEditing = false;
			if(customAdapter != null){
				customAdapter.notifyDataSetChanged();
				return false;
			}

		}

		return super.onKeyDown(keyCode, event);

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_parent_type);

		initView();
		updateView();
		bindListener2View();

		// 查询分类
		initQuery = true;// 标记初始化查询为true
		AppUtil.showLoadingPopup(this, R.string.SPFL_QUERY_LOADING);
		doQueryTask();
	}

	private void addType() {
		LinearLayout view = (LinearLayout) LinearLayout.inflate(this,
				R.layout.template_add_type, null);
		baseDialogBuilder.setContentView(view);
		baseDialogBuilder.setTitle(R.string.SPFL_QUERY_ADDPARENTTXT);
		baseDialogBuilder.setPositiveButton(R.string.COMMON_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// TODO Auto-generated method stub
						EditText addTypeEditView = (EditText) baseDialogBuilder.contentView
								.findViewById(R.id.editText);
						String typeName = AppUtil
								.trimAll(getEditValue(addTypeEditView));

						if (typeName == null) {
							showToast(R.string.SPFL_PARENT_SPECIFY);
							return;
						} else if ((AppUtil.getStrLen(typeName) <= 1)
								|| (AppUtil.getStrLen(typeName) > 10)) {
							showToast(R.string.SPFL_PARENT_INVALID);
							return;
						} else {
							doAddTask(typeName);
						}
					}
				});
		baseDialogBuilder.setNegativeButton(R.string.COMMON_CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						baseDialog.dismiss();
					}
				});
		baseDialog = baseDialogBuilder.create();
		baseDialog.show();

		// 修改dialog宽度
		Window dialogWindow = baseDialog.getWindow();
		WindowManager.LayoutParams wmlp = dialogWindow.getAttributes();
		int[] screenDisplay = AppUtil.getScreen(this);
		wmlp.width = screenDisplay[0] - 20;
		dialogWindow.setAttributes(wmlp);

	}

	private void doDeleteTask(HashMap<String, String> map){
		String id = map.get("id");
		String name = map.get("name");
		whichRadio = 1;
		
		LinearLayout view = (LinearLayout) LinearLayout.inflate(this,
				R.layout.template_delete_type, null);
		baseDialogBuilder.setContentView(view);
		baseDialogBuilder.setTitle("删除"+name);
		
		RadioGroup rg = (RadioGroup) baseDialogBuilder.contentView.findViewById(R.id.radioGroup);
		Spinner spinner = (Spinner) baseDialogBuilder.contentView.findViewById(R.id.spinner);
		final LinearLayout spinnerParent = (LinearLayout) spinner.getParent();
		
		SimpleAdapter adapter = new SimpleAdapter(this, datList, R.layout.template_spinner_item, new String[]{"name"}, new int[]{R.id.txt});
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int pos, long row) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(pos);
				whichOtherTypeId = map.get("id");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup rg, int id) {
				// TODO Auto-generated method stub
				if(id == R.id.radio0){
					whichRadio = 0;
					spinnerParent.setVisibility(View.GONE);
				}else if(id == R.id.radio1){
					whichRadio = 1;
					spinnerParent.setVisibility(View.VISIBLE);
				}
			}});
		
		baseDialogBuilder.setPositiveButton(R.string.COMMON_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// TODO Auto-generated method stub
						Log.w("JZB", whichRadio+" "+whichOtherTypeId);
					}
				});
		baseDialogBuilder.setNegativeButton(R.string.COMMON_CANCEL,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						baseDialog.dismiss();
					}
				});
		baseDialog = baseDialogBuilder.create();
		baseDialog.show();

		// 修改dialog宽度
		Window dialogWindow = baseDialog.getWindow();
		WindowManager.LayoutParams wmlp = dialogWindow.getAttributes();
		int[] screenDisplay = AppUtil.getScreen(this);
		wmlp.width = screenDisplay[0] - 20;
		dialogWindow.setAttributes(wmlp);
	}
	
	private void doAddTask(String typeName) {
		AppUtil.showLoadingPopup(this, R.string.COMMON_CLZ);
		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("name", typeName);

		try {
			doTaskAsync(C.TASK.typeadd, C.API.host + C.API.typeadd, urlParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				pageNum++;
				JSONArray types = operation.getJSONArray("types");

				// 初始化加载时，没有数据显示无数据提示
				if (initQuery && types.length() <= 0) {
					lv.setVisibility(View.GONE);
					noDataView.setVisibility(View.VISIBLE);
					((TextView) noDataView.findViewById(R.id.txt))
							.setText(R.string.SPFL_QUERY_NODATA);
					((TextView) noDataView.findViewById(R.id.action_btn))
							.setText(R.string.SPFL_QUERY_ADDTXT);
					return;
				}
				if (initQuery) {
					initQuery = false;
				}

				lv.setVisibility(View.VISIBLE);
				againView.setVisibility(View.GONE);
				noDataView.setVisibility(View.GONE);

				renderList(types);
				onLoad();
				break;
			case C.TASK.typeadd:
				JSONArray type = new JSONArray();
				type.put(operation);
				addList(type);
				baseDialog.dismiss();
				break;
			}
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}

	/**
	 * 添加一条分类
	 * 
	 * @param type
	 * @throws JSONException
	 */
	private void addList(JSONArray type) throws JSONException {
		if (customAdapter == null) {
			for (int i = 0; i < type.length(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject json = type.getJSONObject(i);
				String id = json.getString("id");
				map.put("name", json.getString("name"));
				map.put("id", id);

				datList.add(map);
			}

			noDataView.setVisibility(View.GONE);
			againView.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);

			customAdapter = new CustomAdapter(this, datList,
					R.layout.template_types_item, new String[] { "name" },
					new int[] { R.id.txt });
			lv.setAdapter(customAdapter);

			lv.setPullLoadEnable(false);
			lv.setPullRefreshEnable(false);
		} else {
			for (int i = 0; i < type.length(); i++) {
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject json = type.getJSONObject(i);
				String id = json.getString("id");
				map.put("name", json.getString("name"));
				map.put("id", id);

				datList.add(0, map);
			}
			customAdapter.notifyDataSetChanged();
		}

	}

	private void renderList(JSONArray data) throws JSONException {
		for (int i = 0; i < data.length(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			JSONObject json = data.getJSONObject(i);
			String id = json.getString("id");
			map.put("name", json.getString("name"));
			map.put("id", id);

			// 避免重复加载
			if (typesId.indexOf(id) < 0) {
				typesId.add(id);
				if (isRefresh) {
					datList.add(0, map);
				} else {
					datList.add(map);
				}
			}
		}

		customAdapter = customAdapter == null ? new CustomAdapter(this,
				datList, R.layout.template_types_item, new String[] { "name" },
				new int[] { R.id.txt }) : customAdapter;

		if (isRefresh || isLoadMore) {
			customAdapter.notifyDataSetChanged();
		} else {
			lv.setAdapter(customAdapter);
		}

		this.isLoadMore = false;
		this.isRefresh = false;

		if (data.length() <= 0) {
			lv.setPullLoadEnable(false);
			lv.setPullRefreshEnable(false);
		}
	}

	private void bindListener2View() {
		this.onCustomBack();
		noDataAddTypeView.setOnClickListener(this);
		addView.setOnClickListener(this);
		editView.setOnClickListener(this);

		((TextView) againView.findViewById(R.id.again_btn))
				.setOnClickListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) lv
						.getItemAtPosition(position);
				Log.w("JZB", map.toString());
			}
		});
	}

	private void initView() {
		topbarView = (LinearLayout) this.findViewById(R.id.topbar3);

		editView = (ImageButton) topbarView.findViewById(R.id.rightbtn_1);
		addView = (ImageButton) topbarView.findViewById(R.id.rightbtn_2);
		titleView = (TextView) topbarView.findViewById(R.id.title);

		noDataView = (LinearLayout) this.findViewById(R.id.noData);
		againView = (LinearLayout) this.findViewById(R.id.again);

		lv = (BaseListView) this.findViewById(R.id.listView);
		lv.setPullLoadEnable(true);
		lv.setPullRefreshEnable(true);
		lv.setXListViewListener(this);

		noDataAddTypeView = (TextView) noDataView.findViewById(R.id.action_btn);

		baseDialogBuilder = new BaseDialog.Builder(this);
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

		titleView.setText(R.string.SPFL_PARENT_TITLE);
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

	/**
	 * 自定义适配器
	 * 
	 * @author zhuqi
	 * 
	 */
	class CustomAdapter extends SimpleAdapter {

		public CustomAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		@SuppressWarnings("unchecked")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			final HashMap<String, String> map = (HashMap<String, String>) customAdapter
					.getItem(position);

			if (convertView == null) {
				convertView = super.getView(position, convertView, parent);
				holder = new ViewHolder();
				holder.txt = (TextView) convertView.findViewById(R.id.txt);
				holder.delete = (ImageView) convertView
						.findViewById(R.id.delete);
				holder.edit = (ImageView) convertView.findViewById(R.id.edit);
				// 保存视图项
				convertView.setTag(holder);
			} else {
				// 取出现有的视图项
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txt.setText(map.get("name"));
			
			if(isEditing){
				holder.delete.setVisibility(View.VISIBLE);
				holder.edit.setVisibility(View.VISIBLE);
			}else{
				holder.delete.setVisibility(View.GONE);
				holder.edit.setVisibility(View.GONE);
			}
			
			holder.delete.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					doDeleteTask(map);
				}});

			return convertView;
		}
	}

	static class ViewHolder {
		ImageView delete, edit;
		TextView txt;
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();

		switch (vId) {
		case R.id.action_btn:
			addType();
			break;
		case R.id.again_btn:
			AppUtil.showLoadingPopup(this, R.string.SPFL_QUERY_LOADING);
			initQuery = true;
			pageNum = 1;
			doQueryTask();
			break;
		case R.id.rightbtn_2:
			addType();
			break;
		case R.id.rightbtn_1:
			isEditing = true;
			if(customAdapter != null){
				customAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	public void onNetworkError(int taskId) {
		if (initQuery) {
			lv.setVisibility(View.GONE);
			againView.setVisibility(View.VISIBLE);
			((TextView) againView.findViewById(R.id.txt))
					.setText(C.ERROR.networkException);

			initQuery = false;
			return;
		}
	}

	public void onNetworkTimeout(int taskId) {
		if (initQuery) {
			lv.setVisibility(View.GONE);
			againView.setVisibility(View.VISIBLE);
			((TextView) againView.findViewById(R.id.txt))
					.setText(C.ERROR.networkTimeout);

			initQuery = false;
			return;
		}
	}

	public void onServerException(int taskId) {
		if (initQuery) {
			lv.setVisibility(View.GONE);
			againView.setVisibility(View.VISIBLE);
			((TextView) againView.findViewById(R.id.txt))
					.setText(C.ERROR.serverException);

			initQuery = false;
			return;
		}
	}

	@Override
	public void onRefresh() {
		if (isLoading) {
			return;
		}
		// TODO Auto-generated method stub
		this.isRefresh = true;
		this.isLoading = true;
		doQueryTask();
	}

	@Override
	public void onLoadMore() {
		if (isLoading) {
			return;
		}
		// TODO Auto-generated method stub
		this.isLoadMore = true;
		this.isLoading = true;
		doQueryTask();
	}

	private void onLoad() {
		lv.stopRefresh();
		lv.stopLoadMore();
		lv.setRefreshTime(AppUtil.getCurrentDateTime());
		isLoading = false;
	}
}
