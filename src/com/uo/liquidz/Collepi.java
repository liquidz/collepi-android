package com.uo.liquidz;

// imports {{{
import java.io.*;
import java.util.*;

// jdbm
import java.util.Properties;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
import jdbm.helper.StringComparator;
import jdbm.btree.BTree;

import org.apache.http.HttpResponse;
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
import android.widget.EditText;

import android.widget.Toast;

import com.uo.liquidz.util.*;

// }}}

public class Collepi extends Activity
{
	static String DATABASE = "test";
	static String BTREE_NAME = "value";

	static GoogleAccountLogin login = null;
	Handler handler = null;
	//final String gae_url = getString(R.string.appengine_url);
	String gae_url = null;

	RecordManager recman;
	long recid;
	BTree tree;
	Properties props = new Properties();

	private void initJDBM(){
		try {
			recman = RecordManagerFactory.createRecordManager(DATABASE, props);
			recid = recman.getNamedObject(BTREE_NAME);
	
			if(recid != 0){
				tree = BTree.load(recman, recid);
			} else {
				tree = BTree.createInstance(recman, new StringComparator());
				recman.setNamedObject(BTREE_NAME, tree.getRecid());
			}
		} catch(Exception e){
			recman = null;
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		handler = new Handler();
		initJDBM();

		gae_url = getString(R.string.appengine_url);

		Button btn = (Button)findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener(){
			//@Override
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

		((Button)findViewById(R.id.recentlist)).setOnClickListener(new OnClickListener(){
			//@Override
			public void onClick(View v){
				Intent i = new Intent(Collepi.this, MyCollection.class);
				startActivity(i);
			}
		});
		//((Button)findViewById(R.id.recentlist)).setOnClickListener(new OnClickListener(){

		login = new GoogleAccountLogin(this);
		login.doAuth(handler, new AsyncHttpCallback(){
			public void success(){
				TextView text = (TextView)findViewById(R.id.auth);
				text.setText("login successful");
				Toast.makeText(Collepi.this, "login successful hoge", Toast.LENGTH_LONG).show();

				//EntityLoader loader = new EntityLoader(gal);
				//Collection[] coll = (Collection[])loader.get("/my/collection", Collection[].class);
			}
			public void fail(){
		      TextView text = (TextView)findViewById(R.id.auth);
		      text.setText("login failed");
			}
		});
    }

	@Override
	public void onResume(){
		super.onResume();

		//gal = new GoogleAccountLogin(this);
		//gal.login(handler, new AsyncHttpCallback(){
		//	public void success(){
		//		TextView text = (TextView)findViewById(R.id.auth);
		//		text.setText("login successful");
		//		Toast.makeText(Collepi.this, "login successful hoge", Toast.LENGTH_LONG).show();
		//	}
		//	public void fail(){
		//      TextView text = (TextView)findViewById(R.id.auth);
		//      text.setText("login failed")
		//	}
		//});

		////login = new GoogleAccountLogin(this, GAE_URL);
		//login = new GoogleAccountLogin(this, getString(R.string.appengine_url));
		//login.doAuth(handler, new Runnable(){
		//	public void run(){
		//		TextView text = (TextView)findViewById(R.id.auth);
		//		text.setText("login successful");
	
		//		Toast.makeText(Collepi.this, "login successful hoge", Toast.LENGTH_LONG).show();
		//	}
		//}, new Runnable(){
		//  public void run(){
		//      TextView text = (TextView)findViewById(R.id.auth);
		//      text.setText("login failed")
		//  }
		//});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(requestCode == 0){
			TextView text = (TextView)findViewById(R.id.text);
			if(resultCode == RESULT_OK){
				String barcode = intent.getStringExtra("SCAN_RESULT");
				text.setText(barcode);

				// post
				//postCollection(barcode);
				Intent i = new Intent(Collepi.this, PostCollection.class);
				i.putExtra("ISBN", barcode);
				startActivity(i);

				//if(login != null){
				//	//// post
				//	//DefaultHttpClient http = new DefaultHttpClient();
				//	////HttpPost post = new HttpPost(GAE_URL + "/update/collection");
				//	//String url = getString(R.string.appengine_url);
				//	//HttpPost post = new HttpPost(url + "/update/collection");
				//	//post.setHeader("Cookie", gal.getAuthCookie());
				//	//List<NameValuePair> params = new ArrayList<NameValuePair>();
				//	//params.add(new BasicNameValuePair("isbn", barcode));

				//	//try {
				//	//	post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				//	//	HttpResponse res = http.execute(post);
				//	//	Toast.makeText(Collepi.this, "post successful", Toast.LENGTH_LONG).show();
				//	//} catch(Exception e){
				//	//	Toast.makeText(Collepi.this, "post failed..", Toast.LENGTH_LONG).show();
				//	//}
				//}

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		boolean ret = super.onCreateOptionsMenu(menu);

		menu.add(0, Menu.FIRST, Menu.NONE, "Reload");//.setIcon(R.drawable.ic_menu_help);
		menu.add(0, Menu.FIRST + 1, Menu.NONE, "Exit");//.setIcon(R.drawable.ic_menu_add);

		return ret;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(Menu.FIRST - item.getItemId()){
		case 0:
			// load
			break;
		case 1 :
			finish();
			break;
		}
		//System.out.println(item.getGroupId());
		//System.out.println(item.getItemId());

		return super.onOptionsItemSelected(item);
	}


	private void postCollection(String isbn){
		if(login == null){ return; }

		AsyncHttpPost post = new AsyncHttpPost(gae_url + "/update/collection");
		post.addCookie(login.getAuthCookie()).addParameter("isbn", isbn).setHandler(handler)
			.setCallback(new AsyncHttpCallback<HttpResponse>(){
				public void success(HttpResponse res){
					Toast.makeText(Collepi.this, "post successful!", Toast.LENGTH_SHORT).show();
				}
				public void fail(){
					Toast.makeText(Collepi.this, "post failed...", Toast.LENGTH_SHORT).show();
				}
			});
		try {
			post.execute();
		} catch(Exception e){}
	}
}

