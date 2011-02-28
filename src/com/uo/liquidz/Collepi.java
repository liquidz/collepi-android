package com.uo.liquidz;

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
	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		if(requestCode == 0){
			TextView text = (TextView)findViewById(R.id.text);
			if(resultCode == RESULT_OK){
				String barcode = intent.getStringExtra("SCAN_RESULT");
				text.setText(barcode);
			}
		}
	}
}
