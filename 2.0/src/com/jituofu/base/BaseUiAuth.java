package com.jituofu.base;

import java.io.UnsupportedEncodingException;

import com.jituofu.ui.UILogin;

import android.os.Bundle;

public class BaseUiAuth extends BaseUi{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		isLogin();
	}
	
	private void isLogin(){
		try {
			//如果没有登录
			if(!BaseAuth.isLogin(this)){
				Bundle bundle = new Bundle();
				bundle.putBoolean("hideBack", true);
				
				this.forward(UILogin.class, bundle);
				this.finish();
				this.onStop();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		isLogin();
	}
}