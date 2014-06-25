package com.jituofu.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

import com.jituofu.base.C;

public class AppClient {
	// 压缩配置
	final private static int CS_NONE = 0;
	final private static int CS_GZIP = 1;

	// 必要类属性
	private String apiUrl;
	private HttpParams httpParams;
	private HttpClient httpClient;
	private int timeoutConnection = 10000;
	private int timeoutSocket = 10000;
	private int compress = CS_NONE;

	// 默认字符集
	private String charset = HTTP.UTF_8;

	public AppClient(String url) {
		initClient(url);
	}

	public AppClient(String url, String charset, int compress) {
		initClient(url);
		this.charset = charset;
		this.compress = compress;
	}

	private void initClient(String url) {
		this.apiUrl = url;
		httpParams = new BasicHttpParams();
		HttpConnectionParams
				.setConnectionTimeout(httpParams, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

		httpClient = new DefaultHttpClient(httpParams);
	}

	private HttpGet headerFilter(HttpGet httpGet) {
		switch (this.compress) {
		case CS_GZIP:
			httpGet.addHeader("Accept-Encoding", "gzip");
			break;
		default:
			break;
		}
		return httpGet;
	}

	private HttpPost headerFilter(HttpPost httpPost) {
		switch (this.compress) {
		case CS_GZIP:
			httpPost.addHeader("Accept-Encoding", "gzip");
			break;
		default:
			break;
		}
		return httpPost;
	}

	private String resultFilter(HttpEntity httpEntity) {
		String result = null;
		try{
			switch (this.compress) {
			case CS_GZIP:
				result = AppUtil.gzipToString(httpEntity);
				break;
			default:
				result = EntityUtils.toString(httpEntity);
				break;
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}

	public String get() throws Exception {
		try{
			HttpGet httpGet = headerFilter(new HttpGet(this.apiUrl));
			Log.w("客户端GET请求:", this.apiUrl);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String httpResult = resultFilter(httpResponse.getEntity());
				Log.w("GET请求获取数据:", httpResult);
				return httpResult;
			} else {
				return null;
			}
		}catch(ConnectTimeoutException e){
			throw new Exception(C.ERROR.networkTimeout);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String post(HashMap urlParams) throws Exception{
		try{
			//初始化post请求
			HttpPost httpPost = headerFilter(new HttpPost(this.apiUrl));
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			
			//构建数据
			JSONObject postDataJSON = new JSONObject(urlParams);
			String postData = postDataJSON.getJSONObject("requestData").toString();
			postParams.add(new BasicNameValuePair("requestData", postData));
			
			//设置编码 
			if(this.charset != null){
				httpPost.setEntity(new UrlEncodedFormEntity(postParams, this.charset));
			}else{
				httpPost.setEntity(new UrlEncodedFormEntity(postParams));
			}
			Log.w("POST请求接口:", this.apiUrl);
			Log.w("POST请求参数:", postParams.toString());
			//发送请求
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == HttpStatus.SC_OK){
				String result = resultFilter(httpResponse.getEntity());
				Log.w("获取到的数据:", result);
				return result;
			}else{
				String result = resultFilter(httpResponse.getEntity());
				Log.e("服务端返回异常", result);
				return null;
			}
		}catch(ConnectTimeoutException e){
			throw new Exception(C.ERROR.networkTimeout);
		}catch (SocketTimeoutException e){
			throw new Exception(C.ERROR.networkTimeout);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}