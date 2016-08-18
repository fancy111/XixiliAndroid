package com.etc.entity;

import android.app.Application;

/**
 * Created by fancy on 2016/8/16.
 */
public class MyApp extends Application{
    //定义私有全局变量
    private Users users;

    //getters and setters
    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
