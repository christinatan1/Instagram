package com.example.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // register parse models
        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("LjFGuT5UwNGQkduJg3Kb6bdjCKnaP8hGT7gBro9R")
                .clientKey("7JJ66b3JT11ELLozc192bKYG6cOOWIPeIglaFW6p")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
