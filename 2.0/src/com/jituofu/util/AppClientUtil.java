package com.jituofu.util;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
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

public class AppClientUtil {
	// 压缩配置
	final private static int CS_NONE = 0;
	final private static int CS_GZIP = 1;

	// 必要类属性
	private String apiUrl;
	private HttpParams httpParams;
	private HttpClient httpClient;
	private int timeoutConnection = 1000*60;
	private int timeoutSocket = 1000*60;
	private int compress = CS_NONE;

	// 默认字符集
	private String charset = HTTP.UTF_8;

	public AppClientUtil(String url) {
		initClient(url);
	}

	public AppClientUtil(String url, String charset, int compress) {
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
	
	public String post(HashMap<String, JSONObject> urlParams) throws ConnectTimeoutException,SocketTimeoutException,Exception{
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
			throw new ConnectTimeoutException(C.ERROR.networkTimeout);
		}catch (SocketTimeoutException e){
			throw new SocketTimeoutException(C.ERROR.networkTimeout);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 上传文件
	 * @param urlParams
	 * @return
	 * @throws ConnectTimeoutException
	 * @throws SocketTimeoutException
	 * @throws Exception
	 */
	public String post(HashMap<String, JSONObject> urlParams, ArrayList<String> files) throws ConnectTimeoutException,SocketTimeoutException,Exception{
		try{
			MultipartEntity reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			
			for(int i=files.size()-1; i>=0; i--){
				String path = files.get(i);
				if(path != null){
					File file = new File(path);
					FileBody fileBody = new FileBody(file, "images/png");
					reqEntity.addPart("file"+i, fileBody);
				}
			}
			
			//初始化post请求
			HttpPost httpPost = headerFilter(new HttpPost(this.apiUrl));
			
			//构建数据
			JSONObject postDataJSON = new JSONObject(urlParams);
			String postData = postDataJSON.getJSONObject("requestData").toString();
			
			
			//设置编码 
			if(this.charset != null){
				reqEntity.addPart("requestData", new StringBody(postData, Charset.forName(this.charset)));
			}else{
				reqEntity.addPart("requestData", new StringBody(postData));
			}
			
			httpPost.setEntity(reqEntity);
			
			Log.w("POST请求接口:", this.apiUrl);
			Log.w("POST请求参数:", postData);
			
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
			throw new ConnectTimeoutException(C.ERROR.networkTimeout);
		}catch (SocketTimeoutException e){
			throw new SocketTimeoutException(C.ERROR.networkTimeout);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}