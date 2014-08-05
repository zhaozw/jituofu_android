package com.jituofu.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jituofu.R;
import com.jituofu.base.BaseUi;
import com.jituofu.base.C;

public class UIAbout extends BaseUi {
	private TextView titleView;
	private WebView webView;
	private ProgressBar progressBarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_about);

		titleView = (TextView) findViewById(R.id.title);
		progressBarView = (ProgressBar) findViewById(R.id.progressBar);

		WebChromeClient wvcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				titleView.setText(title);
			}

		};
		// 创建WebViewClient对象
		WebViewClient wvc = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 使用自己的WebView组件来响应Url加载事件，而不是使用默认浏览器器加载页面
				webView.loadUrl(url);
				// 消耗掉这个事件。Android中返回True的即到此为止吧,事件就会不会冒泡传递了，我们称之为消耗掉
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progressBarView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBarView.setVisibility(View.GONE);

			}
		};

		webView = (WebView) this.findViewById(R.id.webView1);
		webView.setBackgroundColor(0);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.setWebChromeClient(wvcc);
		webView.setWebViewClient(wvc);
		webView.loadUrl(C.COMMON.aboutUrl);
		onBind();
	}

	private void onBind() {
		this.onCustomBack();
	}
}
