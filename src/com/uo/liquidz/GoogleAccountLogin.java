package com.uo.liquidz;

import java.io.*;

import android.os.Bundle;
import android.content.Intent;
import android.context.Context;
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
	//static final String GAE_URL = "http://colle-pi.appspot.com";

	AccountManager accountManager = null;
	Context context = null;
	Runnable callback = null;
	String authCookie = null;
	Handler handler = null;

	public GoogleAccountLogin(){
		this(null);
	}

	public GoogleAccountLogin(Context context){
		this.context = context;
		accountManager = AccountManager.get(context);
	}

	public String getAuthCookie(){
		return authCookie;
	}

	public void login(Handler handler, Runnable callback){
		login(null, callback);
	}

	public void login(Handler handler, Runnable callback){
		this.callback = callback;
		this.handler = handler;
		//(new LoginThread()).start();

		(new Thread(){
			public void run(){
				Accounts[] accounts = accountManager.getAccountsByType("com.google");
				if(accounts.length > 0){
					accountManager.getAuthToken(accounts[0], "ah", false, new GetAuthTokenCallback(), null);
				}
			}
		}).start();
	}

	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
		@Override
		public void run(AccountManagerFuture<Bundle> amf){
			Bundle bundle = null;

			try {
				bundle = amf.getResult();
				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
				if(intent != null){
					// user input required
					startActivity(intent);
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
			//HttpGet get = new HttpGet("https://www.google.com/accounts/TokenAuth?auth=" + token + "&continue=http://www.google.com/calendar/");
			http.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
			//HttpGet get = new HttpGet(GAE_URL + "/_ah/login?continue=/test&auth=" + token);
			HttpGet get = new HttpGet("https://www.google.com/accounts/TokenAuth?continue=/&auth=" + token);
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
	}
}

// {{{
//public class GoogleLoginService extends IntentService {
//	static final String GAE_URL = "http://colle-pi.appspot.com";
//
//	AccountManager accountManager = null;
//	Context context = null;
//	Runnable callback = null;
//	String authCookie = null;
//
//	public GoogleLoginService(String name){
//		super(name);
//	}
//
//	public GoogleLoginService(){
//		super("GoogleLoginService");
//	}
//
//	public GoogleAccountLogin(Context context){
//		super("GoogleAccountLogin");
//		this.context = context;
//		accountManager = AccountManager.get(context);
//	}
//
//	public String getAuthCookie(){
//		return authCookie;
//	}
//
//	public void setCallback(Runnable callback){
//		this.callback = callback;
//	}
//
//	@Override
//	protected void onHandleIntent(Intent intent){
//		Accounts[] accounts = accountManager.getAccountsByType("com.google");
//		if(accounts.length > 0){
//			accountManager.getAuthToken(accounts[0], "ah", false, new GetAuthTokenCallback(), null);
//		}
//	}
//
//	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
//		@Override
//		public void run(AccountManagerFuture<Bundle> amf){
//			Bundle bundle = null;
//
//			try {
//				bundle = amf.getResult();
//				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
//				if(intent != null){
//					// user input required
//					startActivity(intent);
//				} else {
//					loginGoogle(bundle.getString(AccountManager.KEY_AUTHTOKEN));
//				}
//			} catch(OperationCanceledException e){
//				e.printStackTrace();
//			} catch(AuthenticatorException e){
//				e.printStackTrace();
//			} catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//
//		private void loginGoogle(String token){
//			DefaultHttpClient http = new DefaultHttpClient();
//			//HttpGet get = new HttpGet("https://www.google.com/accounts/TokenAuth?auth=" + token + "&continue=http://www.google.com/calendar/");
//			http.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
//			HttpGet get = new HttpGet(GAE_URL + "/_ah/login?continue=/test&auth=" + token);
//			HttpResponse res = null;
//
//			try {
//				res = http.execute(get);
//			} catch(ClientProtocolException e){
//				e.printStackTrace();
//			} catch(IOException e){
//				e.printStackTrace();
//			}
//
//			authCookie = null;
//			for(Cookie cookie : http.getCookieStore().getCookies()){
//				if(cookie.getName().equals("SACSID") || cookie.getName().equals("ACSID")){
//					authCookie = cookie.getName() + "=" + cookie.getValue();
//					if(callback != null){
//						callback.run();
//					}
//					break;
//				}
//			}
//		}
//	}
//}
// }}}

