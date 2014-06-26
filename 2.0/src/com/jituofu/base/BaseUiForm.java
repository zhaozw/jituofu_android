package com.jituofu.base;

public class BaseUiForm extends BaseUi {
	// 验证结果
	public boolean validated = false;

	public void beforeSubmit() {
		// 防止重复点击
		if (validated) {
			return;
		}

		validated = true;

		if (validation()) {

			doSubmit();
		}
		validated = false;
	}

	protected void doSubmit() {

	}

	protected boolean validation() {
		return true;
	}
}
