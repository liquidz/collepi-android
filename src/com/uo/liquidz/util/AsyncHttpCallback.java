package com.uo.liquidz.util;

//public interface Callback<V> {
//	void run(V arg) throws Exception;
//}

public class AsyncHttpCallback<V> {
	public void complete(){}
	public void success(){}
	public void success(V arg){}
	public void fail(){}

	public Runnable getComplete(){
		return new Runnable(){
			public void run(){ complete(); }
		};
	}

	public Runnable getSuccess(){
		return new Runnable(){
			public void run(){ success(); }
		};
	}

	public Runnable getSuccess(V arg){
		final V _arg = arg;
		return new Runnable(){
			public void run(){ success(_arg); }
		};
	}

	public Runnable getFail(){
		return new Runnable(){
			public void run(){ fail(); }
		};
	}
}

