package com.uo.liquidz;

import java.io.*;

import android.os.Handler;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountManagerCallback;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;

public class GoogleAccountLogin {
	AccountManager accountManager = null;
	Context context = null;
	Runnable callback = null;
	String authCookie = null;
	Handler handler = null;
	//String appengineUrl = null;

	public GoogleAccountLogin(){
		//this(null, appengineUrl);
		this(null);
	}

	public GoogleAccountLogin(Context context){
		this.context = context;
		//this.appengineUrl = appengineUrl;
		accountManager = AccountManager.get(context);
	}

	public String getAuthCookie(){
		return authCookie;
	}

	public void login(Runnable callback){
		login(null, callback);
	}

	public void login(Handler handler, Runnable callback){
		this.callback = callback;
		this.handler = handler;

		(new Thread(){
			public void run(){
				Account[] accounts = accountManager.getAccountsByType("com.google");
				if(accounts.length > 0){
					accountManager.getAuthToken(accounts[0], "ah", false, new GetAuthTokenCallback(), null);
				}
			}
		}).start();
	}

	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> { // {{{
		@Override
		public void run(AccountManagerFuture<Bundle> amf){
			Bundle bundle = null;

			try {
				bundle = amf.getResult();
				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
				if(intent != null){
					// user input required
					context.startActivity(intent);
				} else {
					loginGoogle(bundle.getString(AccountManager.KEY_AUTHTOKEN));
				}
			} catch(OperationCanceledException e){
				e.printStackTrace();
			} catch(AuthenticatorException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
		}

		private void loginGoogle(String token){
			DefaultHttpClient http = new DefaultHttpClient();
			http.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
			String url = context.getString(R.string.appengine_url);
			//HttpGet get = new HttpGet(appengineUrl + "/_ah/login?continue=/test&auth=" + token);
			HttpGet get = new HttpGet(url + "/_ah/login?continue=/test&auth=" + token);
			HttpResponse res = null;

			try {
				res = http.execute(get);
			} catch(ClientProtocolException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}

			authCookie = null;
			for(Cookie cookie : http.getCookieStore().getCookies()){
				if(cookie.getName().equals("SACSID") || cookie.getName().equals("ACSID")){
					authCookie = cookie.getName() + "=" + cookie.getValue();
					if(callback != null){
						handler.post(callback);
					}
					break;
				}
			}
		}
	} // }}}
}


// Experimental
//class GoogleLogin extends AsyncTask<Callback, Integer, String> {
//	Context context;
//	AccountManager accountManager;
//	Callback callback;
//
//	public GoogleLogin(Context context){
//		this.context = context;
//		accountManager = AccountManager.get(context);
//	}
//
//	@Override
//	protected String doInBackgroud(Callback... callback){
//		Account[] accounts = accountManager.getAccountsByType("com.google");
//		if(accounts.length > 0){
//			//accountManager.getAuthToken(accounts[0], "ah", false, new GetAuthTokenCallback(), null);
//			AccountManagerFuture<Bunble> amf = accountManager.getAuthToken(accounts[0], "ah", false, null, null);
//
//			try {
//				Bundle bundle = amf.getResult();
//				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
//
//				if(intent != null){
//					context.startActivity(intent);
//				} else {
//					loginGoogle(bundle.getString(AccountManager.KEY_AUTHTOKEN));
//				}
//			} catch(Exception e){}
//
//
//		}
//	}
//
//	@Override
//	protected void onPostExecute(String cookie){
//	}
//
//	private String loginGoogle(String token){
//		DefaultHttpClient http = new DefaultHttpClient();
//		http.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
//		String url = context.getString(R.string.appengine_url);
//		//HttpGet get = new HttpGet(appengineUrl + "/_ah/login?continue=/test&auth=" + token);
//		HttpGet get = new HttpGet(url + "/_ah/login?continue=/test&auth=" + token);
//		HttpResponse res = null;
//
//		try {
//			res = http.execute(get);
//		} catch(ClientProtocolException e){
//			e.printStackTrace();
//		} catch(IOException e){
//			e.printStackTrace();
//		}
//
//		String authCookie = null;
//		for(Cookie cookie : http.getCookieStore().getCookies()){
//			if(cookie.getName().equals("SACSID") || cookie.getName().equals("ACSID")){
//				authCookie = cookie.getName() + "=" + cookie.getValue();
//				if(callback != null){
//					handler.post(callback);
//				}
//				break;
//			}
//		}
//		return authCookie;
//	}
//}
