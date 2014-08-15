package com.jituofu.ui;

import java.util.ArrayList;
import java.util.List;

import com.jituofu.R;
import com.jituofu.base.BaseUi;
import com.jituofu.base.BaseViewPager;
import com.jituofu.base.C;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class UIGuide extends BaseUi implements OnClickListener, OnPageChangeListener{
	SharedPreferences preferences;
	Editor editor;
	
	private ViewPager vp;
	private BaseViewPager vpAdapter;
	private List<View> views;
	
	//引导图片资源
	private static final int[] pics = { R.drawable.step_1,
			R.drawable.step_2, R.drawable.step_3,
			R.drawable.step_4 };
	
	//底部小店图片
	private ImageView[] dots ;
	
	//记录当前选中位置
	private int currentIndex;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_guide);
        
        //标记第一次启动为true
        preferences = getSharedPreferences(C.COMMON.isLaunch,MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean(C.COMMON.isLaunch, true);
        editor.commit();
        
        views = new ArrayList<View>();
       
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        
        //初始化引导图片列表
        for(int i=0; i<pics.length; i++) {
        	ImageView iv = new ImageView(this);
        	iv.setLayoutParams(mParams);
        	iv.setImageResource(pics[i]);
        	if(i == 3){
        		iv.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View v) {
        				// TODO Auto-generated method stub
        				if(currentIndex == 3){
        					forward(UIHome.class);
        					finish();
        				}
        			}});
        	}
        	views.add(iv);
        }
        vp = (ViewPager) findViewById(R.id.viewpager);
        //初始化Adapter
        vpAdapter = new BaseViewPager(views);
        vp.setAdapter(vpAdapter);
        //绑定回调
        vp.setOnPageChangeListener(this);
        
        //初始化底部小点
        initDots();
        
    }
    
    private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[pics.length];

		//循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setEnabled(false);//都设为灰色
			dots[i].setOnClickListener(this);
			dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(true);//设置为白色，即选中状态
    }
    
    /**
     *设置当前的引导页 
     */
    private void setCurView(int position)
    {
		if (position < 0 || position >= pics.length) {
			return;
		}

		vp.setCurrentItem(position);
    }

    /**
     *这只当前引导小点的选中 
     */
    private void setCurDot(int positon)
    {
		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
			return;
		}

		dots[positon].setEnabled(true);
		dots[currentIndex].setEnabled(false);

		currentIndex = positon;
    }

    //当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	//当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	//当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		//设置底部小点选中状态
		setCurDot(arg0);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer)v.getTag();
		setCurView(position);
		setCurDot(position);
	}
}