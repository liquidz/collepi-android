package com.uo.liquidz.adapter;

// imports {{{
import com.uo.liquidz.R;
import com.uo.liquidz.entity.Collection;
import com.uo.liquidz.util.BitmapLoader;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
// }}}

public class CollectionAdapter extends ArrayAdapter<Collection> {
	List<Collection> items;
	LayoutInflater inflater;

	public CollectionAdapter(Context context, int resourceId, Collection[] items){
		super(context, resourceId, items);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public CollectionAdapter(Context context, int resourceId, List<Collection> items){
		super(context, resourceId, items);

		this.items = items;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	//@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v == null){
			v = inflater.inflate(R.layout.collection_row, null);
		}

		//Collection coll = (Collection)items.get(position);
		Collection coll = getItem(position);
		//final Collection fColl = coll;
		TextView text = (TextView)v.findViewById(R.id.title);
		text.setText(coll.item.title);

		TextView point = (TextView)v.findViewById(R.id.point);
		text.setText(coll.point);

		ImageView image = (ImageView)v.findViewById(R.id.thumbnail);
		Bitmap thumb = BitmapLoader.load(coll.item.smallimage);
		if(thumb != null){
			image.setImageBitmap(thumb);
		}

		return v;
	}
}
