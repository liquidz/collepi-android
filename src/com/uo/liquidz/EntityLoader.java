package com.uo.liquidz;

import java.io.*;
import java.util.List;
import java.net.URL;
import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

//abstract class AppengineEntity {}

public class EntityLoader {
	Gson gson = null;
	GoogleAccountLogin gal = null;

	public EntityLoader(){
		this(null);
	}
	public EntityLoader(GoogleAccountLogin gal){
		gson = new Gson();
		this.gal = gal;
	}

	public Object get(String urlStr, Class<?> klass){
		//AsyncHttpGet get = new AsyncHttpGet(urlStr);
		//if(gal != null && gal.getAuthCookie() != null){
		//	get.addCookie(gal.getAuthCookie());
		//}

		//try {
		//	get.setCallback(new AsyncHttpCallback<HttpResponse>(){
		//		public void success(HttpResponse res){
		//		}
		//	}).execute()
		//} catch(Exception e){}

		DefaultHttpClient http = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlStr);
		HttpResponse res = null;

		try {
			res = http.execute(get);

			Reader reader = new InputStreamReader(res.getEntity().getContent());
			return gson.fromJson(reader, klass);
		} catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public void asyncGet(String urlStr, Class<?> klass, AsyncHttpCallback callback){
	}
}

