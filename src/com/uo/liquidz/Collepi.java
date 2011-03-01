package com.uo.liquidz;

// imports {{{
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import android.widget.Toast;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
// }}}

public class Collepi extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Button btn = (Button)findViewById(R.id.button);
		if(btn == null){
			Toast.makeText(Collepi.this, "btn is null", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(Collepi.this, "btn is NOT null", Toast.LENGTH_LONG).show();
		}
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
    }

	@Override
	public void onResume(){
		AccountManager am = AccountManager.get(this);
		Account[] accounts = am.getAccountsByType("com.google");
		if(accounts.length > 0){
			am.getAuthToken(accounts[0], "ah", false, new GetAuthTokenCallback(), null); // ah = appengine
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(requestCode == 0){
			TextView text = (TextView)findViewById(R.id.text);
			if(resultCode == RESULT_OK){
				String barcode = intent.getStringExtra("SCAN_RESULT");
				text.setText(barcode);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		boolean ret = super.onCreateOptionsMenu(menu);

		menu.add(0, Menu.FIRST, Menu.none, "menu1").setIcon(R.drawable.ic_menu_help);
		menu.add(0, Menu.FIRST + 1, Menu.none, "menu2").setIcon(R.drawable.ic_menu_add);

		return ret;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		System.out.println(item.getGroupId());
		System.out.println(item.getItemId());

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
			HttpGet get = new HttpGet("https://www.google.com/accounts/TokenAuth?auth=" + token + "&continue=http://www.google.com/calendar/");
			HttpResponse res = null;

			try {
				res = http.execute(get);
			} catch(ClientProtocolException e){
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}

			if(res.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				try {
					String entity = EntityUtils.toString(response.getEntity);
					if(entity.contains("invalid")){
						am.invalidateAuthToken("com.google", token);
					}
				} catch(IllegalStatusException e){
					e.printStackTrace();
				} catch(IOException e){
					e.printStackTrace();
				}
			} else {
				System.out.println("login failure");
			}
		}
	}
}
