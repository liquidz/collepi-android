package com.uo.liquidz.util;

public class AsyncHttpPost extends AsyncHttpClient {
	public AsyncHttpPost(String url){
		super("POST", url);
	}
}
