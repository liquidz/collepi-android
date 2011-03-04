package com.uo.liquidz;

// imports {{{
import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;

// }}}

public class Collepi extends Activity
{
	//static final String GAE_URL = "http://colle-pi.appspot.com";
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
				Intent i = new Intent(Collepi.this, MyCollection.class);
				startActivity(i);
			}
		});

    }

	@Override
	public void onResume(){
		super.onResume();

		////gal = new GoogleAccountLogin(this, GAE_URL);
		//gal = new GoogleAccountLogin(this, getString(R.string.appengine_url));
		//gal.login(handler, new Runnable(){
		//	public void run(){
		//		TextView text = (TextView)findViewById(R.id.auth);
		//		text.setText("login successful");
	
		//		Toast.makeText(Collepi.this, "login successful hoge", Toast.LENGTH_LONG).show();
		//	}
		//});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(requestCode == 0){
			TextView text = (TextView)findViewById(R.id.text);
			if(resultCode == RESULT_OK){
				String barcode = intent.getStringExtra("SCAN_RESULT");
				text.setText(barcode);

				if(gal != null){

					HashMap<String, String> postParams = new HashMap<String, String>(),
						header = new HashMap<String, String>();
					postParams.put("isbn", barcode);
					header.put("Cookie", gal.getAuthCookie());

					AsyncHttp.post(url + "/update/collection", header, postParams, new Runnable(){
						public void run(){
							Toast.makeText(Collepi.this, "post successful", Toast.LENGTH_LONG).show();
						}
					}, new Runnable(){
						public void run(){
							Toast.makeText(Collepi.this, "post failed..", Toast.LENGTH_LONG).show();
						}
					});

					// post
					DefaultHttpClient http = new DefaultHttpClient();
					//HttpPost post = new HttpPost(GAE_URL + "/update/collection");
					String url = getString(R.string.appengine_url);
					HttpPost post = new HttpPost(url + "/update/collection");
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

	private boolean asyncPost(String path, List<NameValuePair> params){
		asyncPost(path, params, null, null);
	}
	private boolean asyncPost(String path, List<NameValuePair> params, Runnable success){
		asyncPost(path, params, success, null);
	}
	private boolean asyncPost(String path, List<NameValuePair> params, Runnable success, Runnable fail){
		final Runnable successCallback = success;
		final Runnable failCallback = fail;
		final List<NameValuePair> postParams = params;
		if(gal != null){
			(new Thread(){
				public void run(){
					// post
					DefaultHttpClient http = new DefaultHttpClient();
					//HttpPost post = new HttpPost(GAE_URL + "/update/collection");
					String url = getString(R.string.appengine_url);
					HttpPost post = new HttpPost(url + path);
					post.setHeader("Cookie", gal.getAuthCookie());

					try {
						post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						HttpResponse res = http.execute(post);
						if(successCallback != null){
							successCallback.run();
						}
					} catch(Exception e){
						if(failCallback != null){
							failCallback.run();
						}
					}
				}
			}).start();
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
		switch(Menu.FIRST - item.getItemId()){
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
}

