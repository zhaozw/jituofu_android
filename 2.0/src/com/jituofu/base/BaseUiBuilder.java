package com.jituofu.base;

public interface BaseUiBuilder {
	/**
	 * 绑定UI事件
	 */
	void onBind();

	/**
	 * 更新UI
	 */
	void onUpdate();
	
	/**
	 * 准备UI
	 */
	void prepare();
}
