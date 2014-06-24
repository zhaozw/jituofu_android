package com.jizhangbao.xiaodian;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NoConnect extends Activity{
	Button btn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.no_connect);
    	
    	btn = (Button) findViewById(R.id.button);
    	btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(NetworkCheck.checkNet(NoConnect.this) == false){	
					Toast.makeText(NoConnect.this, "好像还没有连接网络哦",Toast.LENGTH_LONG).show();  
		    	}else{
		    		goHome();
		    	}
			}});
    }
    
    @SuppressLint("NewApi")
	public void goHome(){
    	Intent intent = new Intent(NoConnect.this, MainActivity.class);
    	intent.setClass(NoConnect.this, MainActivity.class);
    	this.startActivity(intent);
    	this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    	this.finish();
    }
    
}
