package com.uo.liquidz.util;

import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapLoader {
	public static Bitmap load(String url){
		try {
			URL imageUrl = new URL(url);
			Bitmap thumb = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
			return thumb;
		} catch(Exception e){
			return null;
		}
	}
}
