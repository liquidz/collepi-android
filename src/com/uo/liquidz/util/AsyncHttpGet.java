package com.uo.liquidz.util;

public class AsyncHttpGet extends AsyncHttpClient {
	public AsyncHttpGet(String url){
		super("GET", url);
	}
}
