package com.uo.liquidz;

// imports {{{
import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;

//import android.accounts.Account;
//import android.accounts.AccountManager;
//import android.accounts.AccountManagerFuture;
//import android.accounts.AccountManagerCallback;
//import android.accounts.AuthenticatorException;
//import android.accounts.OperationCanceledException;
// }}}

public class Collepi extends Activity
{
	static final String GAE_URL = "http://colle-pi.appspot.com";
	AccountManager accountManager = null;
	String acsid = null;
	GoogleAccountLogin gal = null;
	Handler handler = null;
	Model model = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		handler = new Handler();
		model = new Model();

		Button btn = (Button)findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "ONE_D_MODE");
				try {
					startActivityForResult(intent, 0);
				} catch (ActivityNotFoundException e){
					Toast.makeText(Collepi.this, "error", Toast.LENGTH_LONG).show();
				}

			}
		});

		((Button)findViewById(R.id.showmylist)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				startActivity(new Intent(Collepi.this, MyCollection.class));
			}
		});
    }

	@Override
	public void onResume(){
		super.onResume();
		//accountManager = AccountManager.get(this);
		//Account[] accounts = accountManager.getAccountsByType("com.google");
		//if(accounts.length > 0){
		//	// ah = appengine
		//	accountManager.getAuthToken(accounts[0], "ah", false, new GetAuthTokenCallback(), null);
		//}

		gal = new GoogleAccountLogin(this);
		gal.login(handler, new Runnable(){
			public void run(){
				Toast.makeText(Collepi.this, "login successful", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(requestCode == 0){
			TextView text = (TextView)findViewById(R.id.text);
			if(resultCode == RESULT_OK){
				String barcode = intent.getStringExtra("SCAN_RESULT");
				text.setText(barcode);

				//if(acsid != null){
				if(gal != null){
					// post
					DefaultHttpClient http = new DefaultHttpClient();
					HttpPost post = new HttpPost(GAE_URL + "/update/collection");
					//post.setHeader("Cookie", acsid);
					post.setHeader("Cookie", gal.getAuthCookie());
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("isbn", barcode));

					try {
						post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						HttpResponse res = http.execute(post);
						Toast.makeText(Collepi.this, "post successful", Toast.LENGTH_LONG).show();
					} catch(Exception e){
						Toast.makeText(Collepi.this, "post failed..", Toast.LENGTH_LONG).show();
					}
				}

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		boolean ret = super.onCreateOptionsMenu(menu);

		menu.add(0, Menu.FIRST, Menu.NONE, "load");//.setIcon(R.drawable.ic_menu_help);
		menu.add(0, Menu.FIRST + 1, Menu.NONE, "menu2");//.setIcon(R.drawable.ic_menu_add);

		return ret;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(Menu.FIRST - item.getId()){
		case 0:
			// load
			Collection[] c = (Collection[])model.get("/my/collection", Collection[].class);


			break;
		case 1 :
			System.out.println("hogehoge");
			break;
		}
		//System.out.println(item.getGroupId());
		//System.out.println(item.getItemId());

		return super.onOptionsItemSelected(item);
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
			HttpGet get = new HttpGet(GAE_URL + "/_ah/login?continue=/test&auth=" + token);
			HttpResponse res = null;

			try {
				res = http.execute(get);
			} catch(ClientProtocolException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}

			for(Cookie cookie : http.getCookieStore().getCookies()){
				if(cookie.getName().equals("SACSID") || cookie.getName().equals("ACSID")){
					acsid = cookie.getName() + "=" + cookie.getValue();
					Toast.makeText(Collepi.this, "login successful", Toast.LENGTH_LONG).show();
					break;
				}
			}

			//get = new HttpGet(GAE_URL + "/test");
			//get.setHeader("Cookie", acsid);

			//try {
			//	res = http.execute(get);
			//	TextView text = (TextView)findViewById(R.id.auth);
			//	text.setText(EntityUtils.toString(res.getEntity()));
			//} catch(Exception e){
			//	e.printStackTrace();
			//}

			//if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			//	try {
			//		String entity = EntityUtils.toString(res.getEntity());
			//		if(entity.contains("invalid")){
			//			accountManager.invalidateAuthToken("com.google", token);
			//		}
			//			TextView text = (TextView)findViewById(R.id.auth);
			//			text.setText("now logged in: " + entity);
			//	} catch(IllegalStateException e){
			//		e.printStackTrace();
			//	} catch(IOException e){
			//		e.printStackTrace();
			//	}
			//} else {
			//	System.out.println("login failure");
			//}
		}
	}
}

