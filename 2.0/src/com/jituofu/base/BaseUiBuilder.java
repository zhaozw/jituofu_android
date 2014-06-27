package com.jituofu.base;

public interface BaseUiBuilder {
	/**
	 * 初始化绑定UI事件
	 */
	void onBind();

	/**
	 * 初始化更新UI
	 */
	void onUpdate();
	
	/**
	 * 初始化准备UI
	 */
	void prepare();
}
