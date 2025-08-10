package com.example.onlineshop;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dq8wxstwb");
        config.put("api_key", "516278749937659");
        config.put("api_secret", "Q-ymsDKoIPgxWWXnRCpn2LY7xuE");
        MediaManager.init(this, config);
    }
}