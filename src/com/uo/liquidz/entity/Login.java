package com.uo.liquidz.entity;

public class Login {
    public String loggedin, url, avatar, nickname;
    public boolean isLoggedin(){
        return loggedin.equals("true");
    }
}
