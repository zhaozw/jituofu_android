package com.jituofu.ui;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.jituofu.R;
import com.jituofu.base.BaseDateTimePicker;
import com.jituofu.base.BaseDialog;
import com.jituofu.base.BaseGetProductImageTask;
import com.jituofu.base.BaseListView;
import com.jituofu.base.BaseMessage;
import com.jituofu.base.BaseUiAuth;
import com.jituofu.base.C;
import com.jituofu.util.AppUtil;

public class UISaleDetail extends BaseUiAuth {
	// 相关的控件
	private TextView titleView, sellingCountView, typeView, sellingPrice,
			dateView, hjView, remarkView;
	private Button thBtnView;
	private ImageView picView;

	// 外部参数
	private Bundle extraBundle;
	private String id;
	private JSONObject detail;

	// 异步获取商品图片
	private BaseGetProductImageTask bpit;

	// 退货相关的变量
	private String returnSaleYY;// 退货原因
	private BaseDialog.Builder baseDialogBuilder;
	private BaseDialog baseDialog;
	private BaseDialog.Builder baseMesDialogBuilder;
	private BaseDialog baseMesDialog;
	private BaseDateTimePicker dateTimePicker;
	private int year, month, day, hour, minute, second;
	private String returnSaleDate;// 退货日期
	private TextView returnSaleDateView;// 退货日期控件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_sale_detail);

		extraBundle = this.getIntent().getExtras();
		if (extraBundle != null) {
			id = extraBundle.getString("id");
			try {
				detail = new JSONObject(extraBundle.getString("detail"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		initView();
		try {
			updateView();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		onBind();
	}

	private void onBind() {
		this.onCustomBack();
		thBtnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				returnSale();
			}
		});
	}

	private void returnSale() {
		LinearLayout view = (LinearLayout) LinearLayout.inflate(this,
				R.layout.template_return_sale, null);
		baseDialogBuilder = new BaseDialog.Builder(this);
		baseDialogBuilder.setContentView(view, UISaleDetail.this);
		baseDialogBuilder.setTitle("退货");

		returnSaleDate = AppUtil.getCurrentDateTime();

		final RadioGroup rg = (RadioGroup) baseDialogBuilder.contentView
				.findViewById(R.id.radioGroup);
		final EditText slView = (EditText) baseDialogBuilder.contentView
				.findViewById(R.id.sl);
		final EditText yyView = (EditText) baseDialogBuilder.contentView
				.findViewById(R.id.yy);
		final EditText remarkView = (EditText) baseDialogBuilder.contentView
				.findViewById(R.id.remark);
		returnSaleDateView = (TextView) baseDialogBuilder.contentView
				.findViewById(R.id.date);

		slView.setFocusableInTouchMode(true);
		yyView.setFocusableInTouchMode(true);
		remarkView.setFocusableInTouchMode(true);

		returnSaleDateView.setText(returnSaleDate);
		returnSaleDateView.setOnClickListener(null);
		returnSaleDateView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (year > 0 && month > 0 && day > 0) {
					if (hour > 0 && minute > 0) {
						dateTimePicker.setHMS(hour, minute, second);
					}
					dateTimePicker.showDateDialog(year, month, day);
				} else {
					dateTimePicker.showDateDialog();
				}
			}
		});
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup rg, int id) {
				// TODO Auto-generated method stub
				if (id == R.id.radio3) {
					((LinearLayout) yyView.getParent())
							.setVisibility(View.VISIBLE);
				} else {
					((LinearLayout) yyView.getParent())
							.setVisibility(View.GONE);
				}
			}
		});

		baseDialogBuilder.setPositiveButton(R.string.COMMON_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface di, int which) {
						// TODO Auto-generated method stub
						RadioButton rb = (RadioButton) rg.findViewById(rg
								.getCheckedRadioButtonId());
						returnSaleYY = rb.getText().toString();
						String otherYY = AppUtil.trimAll(getEditValue(yyView));
						String sl = getEditValue(slView);
						Double slDouble;
						String remark = getEditValue(remarkView);
						Double totalSaleCount = null;
						try {
							// 总销售数量
							totalSaleCount = Double.parseDouble(detail
									.getString("selling_count"));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// 对其它退货原因做检验
						if (rb.getId() == R.id.radio3
								&& (otherYY == null || otherYY.length() <= 0)) {
							showToast(R.string.SALESREPORT_RETURNSALE_YY_SPECIFY);
							return;
						} else if (rb.getId() == R.id.radio3) {
							returnSaleYY = otherYY;
						}
						// 对退货数量做检验
						if (sl == null) {
							showToast(R.string.SALESREPORT_RETURNSALE_SL_SPECIFY);
							return;
						} else {
							slDouble = Double.parseDouble(sl);
							if (slDouble == 0) {
								showToast(R.string.SALESREPORT_RETURNSALE_SL_ERROR);
								return;
							} else if (totalSaleCount != null
									&& slDouble > totalSaleCount) {
								showToast(R.string.SALESREPORT_RETURNSALE_SL_MAX);
								return;
							}
						}
						
						doReturnSaleTask(returnSaleYY, AppUtil.toFixed(slDouble), remark, returnSaleDate);
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
	}
	
	private void doReturnSaleTask(String reason, String count, String remark, String date){
		AppUtil.showLoadingPopup(this, R.string.SALESREPORT_RETURNSALE_LOADING);

		HashMap<String, String> urlParams = new HashMap<String, String>();

		urlParams.put("id", id);
		urlParams.put("reason", reason);
		urlParams.put("count", count);
		urlParams.put("remark", remark);
		urlParams.put("date", date);

		try {
			this.doTaskAsync(C.TASK.returnsale, C.API.host
					+ C.API.returnsale, urlParams);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
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
			baseMesDialogBuilder = new BaseDialog.Builder(this);
			baseMesDialogBuilder.setMessage(message.getMemo(), UISaleDetail.this);
			baseMesDialog = baseMesDialogBuilder.create();
			baseMesDialog.show();
			
			AppUtil.timer(new TimerTask(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					baseDialog.dismiss();
					baseMesDialog.dismiss();
				}}, 3000);
			
			sellingCountView.setText(operation.getString("newSaleCount"));
			Double hj = Double.parseDouble(operation.getString("newSaleCount"))
					* Double.parseDouble(detail.getString("selling_price"));
			hjView.setText(AppUtil.toFixed(hj));
		} else {
			this.showToast(message.getFirstOperationErrorMessage());
		}
	}
	
	private void updateView() throws JSONException {
		if (detail != null) {
			titleView.setText(detail.getString("name"));

			// 商品图片处理
			String pic = detail.getString("pic");
			if (pic != null && pic.length() > 0) {
				bpit = new BaseGetProductImageTask(picView, pic,
						detail.getString("pid"));
				bpit.execute();
			} else {
				picView.setScaleType(ScaleType.CENTER);

				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
				lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
				lp.alignWithParent = true;
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				lp.addRule(RelativeLayout.CENTER_VERTICAL);
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				picView.setLayoutParams(lp);
				picView.setImageDrawable(this.getResources().getDrawable(
						R.drawable.default_img_placeholder));
			}

			try {
				JSONObject type = detail.getJSONObject("typeName");
				String typeName = "";
				if (type.has("child")) {
					typeName = type.getString("parent")
							+ this.getString(R.string.SPFL_SPEARATOR)
							+ type.getString("child");
				} else {
					typeName = type.getString("parent");
				}
				typeView.setText(typeName);
			} catch (JSONException e) {
				// 没有分类数据的话，就隐藏分类
				((LinearLayout) typeView.getParent().getParent())
						.setVisibility(View.GONE);
			}
			dateView.setText(detail.getString("date"));
			remarkView.setText(detail.getString("remark"));
			sellingCountView.setText(detail.getString("selling_count"));
			sellingPrice.setText(AppUtil.toFixed(Double.parseDouble(detail
					.getString("selling_price"))));
			Double hj = Double.parseDouble(detail.getString("selling_count"))
					* Double.parseDouble(detail.getString("selling_price"));
			hjView.setText(AppUtil.toFixed(hj));
		}
	}

	private void initView() {
		titleView = (TextView) this.findViewById(R.id.title);
		sellingCountView = (TextView) this.findViewById(R.id.selling_count);
		sellingPrice = (TextView) this.findViewById(R.id.selling_price);
		dateView = (TextView) this.findViewById(R.id.date);
		hjView = (TextView) this.findViewById(R.id.hj);
		remarkView = (TextView) this.findViewById(R.id.remark);
		typeView = (TextView) this.findViewById(R.id.type);

		thBtnView = (Button) this.findViewById(R.id.thBtn);
		picView = (ImageView) this.findViewById(R.id.imageView);

		dateTimePicker = new BaseDateTimePicker(this);
		dateTimePicker.setTimeDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface di) {
				// TODO Auto-generated method stub
				int[] ymd = dateTimePicker.getYMD();
				int[] hms = dateTimePicker.getHMS();

				year = ymd[0];
				month = ymd[1];
				day = ymd[2];

				hour = hms[0];
				minute = hms[1];
				second = hms[2];

				returnSaleDate = year + "-" + AppUtil.to2bit(month) + "-"
						+ AppUtil.to2bit(day) + " " + AppUtil.to2bit(hour)
						+ ":" + AppUtil.to2bit(minute) + ":"
						+ AppUtil.to2bit(second);
				if (returnSaleDateView != null) {
					returnSaleDateView.setText(returnSaleDate);
				}
			}
		});
	}
}
