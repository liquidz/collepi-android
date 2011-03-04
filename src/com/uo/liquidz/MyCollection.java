package com.uo.liquidz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.widget.Toast;

public class MyCollection extends Activity {
	//static final String GAE_URL = "http://colle-pi.appspot.com";

	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

		Model model = new Model();
		//Collection[] coll = (Collection[])model.get("/my/collection", Collection[].class);
		String url = getString(R.string.appengine_url);
		//Collection[] coll = (Collection[])model.get(GAE_URL + "/collection/list", Collection[].class);
		Collection[] coll = (Collection[])model.get(url + "/collection/list", Collection[].class);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		//Toast.makeText(this, "len = " + coll.length, Toast.LENGTH_SHORT).show();

		for(Collection c : coll){
			String tmp = c.item.title + " <" + c.point + "> ("+ c.user.nickname +")";
			adapter.add(tmp);
		}


		ListView listView = (ListView)findViewById(R.id.listview);
		listView.setAdapter(adapter);


	}
}

