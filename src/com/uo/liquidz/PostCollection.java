package com.uo.liquidz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.EditText;
//import android.content.Intent;
import android.view.View.OnClickListener;

import org.apache.http.HttpResponse;
import android.os.Handler;

import android.widget.Toast;

//import com.uo.liquidz.adapter.CollectionAdapter;
import com.uo.liquidz.entity.Collection;
import com.uo.liquidz.util.AsyncHttpCallback;
import com.uo.liquidz.util.AsyncHttpPost;

public class PostCollection extends Activity {
	final String gae_url = getString(R.string.appengine_url);
	String isbn = null;
	String comment = null;
	boolean isRead = false;
	Handler handler = new Handler();

	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);

		//handler = new Handler();

		isbn = getIntent().getStringExtra("ISBN");
		if(isbn == null){ finish(); }

		TextView title = (TextView)findViewById(R.id.isbn);
		title.setText(isbn);

		EditText edit = (EditText)findViewById(R.id.comment);
		comment = edit.getText().toString();

		CheckBox read = (CheckBox)findViewById(R.id.read);
		isRead = read.isChecked();

		((Button)findViewById(R.id.back)).setOnClickListener(new OnClickListener(){
			//@Override
			public void onClick(View v){
				postCollection(isbn, comment, isRead);
			}
		});
	}

	private void postCollection(String isbn, String comment, boolean isRead){
		if(Collepi.login == null){ return; }

		AsyncHttpPost post = new AsyncHttpPost(gae_url + "/update/collection");
		try {
			// set comment
			if(comment != null || !comment.equals("")){
				post.addParameter("comment", comment);
			}

			post.addCookie(Collepi.login.getAuthCookie())
				.addParameter("isbn", isbn).addParameter("read", (isRead ? "true" : "false"))
				.setHandler(handler).setCallback(new AsyncHttpCallback<HttpResponse>(){
					public void success(HttpResponse res){
						Toast.makeText(PostCollection.this, "post successful!", Toast.LENGTH_SHORT).show();
					}
					public void fail(){
						Toast.makeText(PostCollection.this, "post failed...", Toast.LENGTH_SHORT).show();
					}
					public void complete(){
						finish();
					}
				}).execute();
		} catch(Exception e){}
	}
}
