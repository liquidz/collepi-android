package com.uo.liquidz;

import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;

class Login {
    public String loggedin, url, avatar, nickname;
    public boolean isLoggedin(){
        return loggedin.equals("true");
    }
}

class User {
    public String nickname, avatar, date;
    public String key;

    public List<Collection> collection;
    public List<History> history;
}

class Item {
    public String isbn, title, author, smallimage, mediumimage, largeimage;

    public List<Collection> collection;
    public List<History> history;
}

class Collection {
    public String id, point, read, date;

    public Item item;
    public User user;
}

class History {
    public String point, read, comment, date;
    public Item item;
    public User user;
}

public class Model {
	Gson gson = null;

	public Model(){
		gson = new Gson();
	}

	public Object get(String url, Class<?> klass){
		try {
			URL url = new URL(urlStr);
			Reader reader = new InputStreamReader(url.openStream());
			return gson.fromJson(reader, klass);
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
