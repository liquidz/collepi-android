package com.uo.liquidz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.graphics.Bitmap;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
//import android.content.Intent;
import android.view.View.OnClickListener;


import android.widget.Toast;

//import com.uo.liquidz.adapter.CollectionAdapter;
import com.uo.liquidz.entity.Collection;
import com.uo.liquidz.util.BitmapLoader;

public class CollectionDetail extends Activity {
	final String gae_url = getString(R.string.appengine_url);

	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

		String id = getIntent().getStringExtra("ID");
		if(id == null){ finish(); }

		EntityLoader loader = new EntityLoader();
		Collection coll = (Collection)loader.get(gae_url + "/collection?id=" + id, Collection.class);

		ImageView thumbnail = (ImageView)findViewById(R.id.thumbnail);
		Bitmap thumb = BitmapLoader.load(coll.item.largeimage);
		if(thumb != null){ thumbnail.setImageBitmap(thumb); }

		TextView title = (TextView)findViewById(R.id.title);
		title.setText(coll.item.title);

		TextView author = (TextView)findViewById(R.id.author);
		author.setText(coll.item.author);

		((Button)findViewById(R.id.back)).setOnClickListener(new OnClickListener(){
			//@Override
			public void onClick(View v){
				finish();
			}
		});

	}
}




