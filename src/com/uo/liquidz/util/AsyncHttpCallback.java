package com.uo.liquidz.util;

//public interface Callback<V> {
//	void run(V arg) throws Exception;
//}

public class AsyncHttpCallback<V> {
	public void complete(){}
	public void success(V response){}
	public void fail(){}
}

