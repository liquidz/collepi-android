package com.uo.liquidz.util;

import java.net.URLEncoder;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;

import android.os.Handler;

public class AsyncHttpClient {
	DefaultHttpClient client;
	HttpRequestBase request;
	List<NameValuePair> params;
	String method;
	String url;
	Handler handler;
	AsyncHttpCallback<HttpResponse> callback;

	public AsyncHttpClient(String method, String url){
		client = new DefaultHttpClient();
		params = new ArrayList<NameValuePair>();
		this.method = method.toLowerCase();
		this.url = url;

		if(this.method.equals("get")){
			request = new HttpGet(url);
		} else if(this.method.equals("post")){
			request = new HttpPost(url);
		}
	}

	public AsyncHttpClient addCookie(String value){
		if(request != null){
			request.addHeader("Cookie", value);
		}
		return this;
	}

	public AsyncHttpClient addParameter(String key, String value){
		params.add(new BasicNameValuePair(key, value));
		return this;
	}

	public AsyncHttpClient setHandler(Handler handler){
		this.handler = handler;
		return this;
	}

	public AsyncHttpClient setCallback(AsyncHttpCallback<HttpResponse> callback){
		this.callback = callback;
		return this;
	}

    public AsyncHttpClient setRedirect(boolean flag){
        client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, flag);
        return this;
    }

    public AbstractHttpClient getClient(){
        return client;
    }

	private boolean canCallback(){
		return (callback != null && handler != null);
	}

	private String makeUrlWithQuery() throws Exception {
		StringBuffer sb = new StringBuffer(url + "?");
		for(NameValuePair pair : params){
			sb.append(pair.getName()).append("=")
				.append(URLEncoder.encode(pair.getValue(), "UTF-8")).append("&");
		}

		String newUrl = sb.toString();
		return newUrl.substring(0, newUrl.length() - 1);
	}

	public boolean execute() throws Exception {
		if(request == null){
			return false;
		} else {
			if(method.equals("get")){
				request.setURI(new URI(makeUrlWithQuery()));
			} else {
				((HttpPost)request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}

			(new Thread(){
				public void run(){
					System.out.println("running thread");
					try {
						final HttpResponse res = client.execute(request);
						if(canCallback()){
							handler.post(new Runnable(){
								public void run(){
									callback.success(res);
								}
							});
						}
					} catch(Exception e){
						if(canCallback()){
							handler.post(new Runnable(){
								public void run(){
									callback.fail();
								}
							});
						}
					}

					if(canCallback()){
						handler.post(new Runnable(){
							public void run(){
								callback.complete();
							}
						});
					}
				}
			}).start();
		}
		return true;
	}
}

