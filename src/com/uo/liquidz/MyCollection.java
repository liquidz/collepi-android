package com.uo.liquidz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import android.content.Intent;
import android.widget.Toast;

import com.uo.liquidz.adapter.CollectionAdapter;
import com.uo.liquidz.entity.Collection;

public class MyCollection extends Activity {
	//final String gae_url = getString(R.string.appengine_url);
	String gae_url = null;
	Collection[] coll;

	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

		gae_url = getString(R.string.appengine_url);

		EntityLoader loader = new EntityLoader();
		coll = (Collection[])loader.get(gae_url + "/collection/list", Collection[].class);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		CollectionAdapter adapter = new CollectionAdapter(this, R.layout.collection_row, coll);

		//for(Collection c : coll){
		//	//String tmp = c.item.title + " <" + c.point + "> ("+ c.user.nickname +")";
		//	//adapter.add(tmp);
		//	adapter.add(c);
		//}

		ListView listView = (ListView)findViewById(R.id.listview);
		listView.setAdapter(adapter);

		listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			//@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
				ListView list = (ListView)parent;
				Collection c = (Collection)list.getSelectedItem();

				// start activity for collection detail
				Toast.makeText(MyCollection.this, c.item.title, Toast.LENGTH_SHORT).show();

				Intent i = new Intent(MyCollection.this, CollectionDetail.class);
				i.putExtra("ID", c.id);
				startActivity(i);
			}

			//@Override
			public void onNothingSelected(AdapterView<?> parent){
			}
		});
	}


}




