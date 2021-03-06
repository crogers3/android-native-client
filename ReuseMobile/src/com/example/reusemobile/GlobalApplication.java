package com.example.reusemobile;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

import com.example.reusemobile.model.Item;
import com.roscopeco.ormdroid.Entity;
import com.roscopeco.ormdroid.ORMDroidApplication;

public class GlobalApplication extends Application {
    
//    private Integer[] ids = {1, 2, 3};
//    private String[] items = {"Dell Desktop", "Random electronic stuff", "Free Dogecoin!!!"};
//    private String[] descriptions = {"An old dell desktop. Missing HDD. Please take all.",
//                                     "Box of random electric things. Floppy disks galore!",
//                                     "Come see me if you want some free dogecoin. Much currency. Such value. Wow! This is an arbitrarily long string. AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHhhhh"};
//    private String[] tags = {"computer desktop dell",
//                             "capacitors floppy disks wires",
//                             "dogecoin wow"};
//    private String[] locations = {"32-123", "10-250", "16-676"};
//    private Boolean[] available = {true, true, true};
    
    public static String filterPreferences = "com.example.reuse.filters";
    public static boolean logging = true;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        ORMDroidApplication.initialize(this);
//        // Empty db
//        List<Item> previousEntries = Entity.query(Item.class).executeMulti();
//        if (previousEntries.size() == 0) {
//            // Insert new items
//            for (int i = 0; i < 3; i++) {
//                (new Item(ids[i], items[i], descriptions[i], new Date(), locations[i], tags[i], available[i])).save();
//            }
//        }
        
        // Create db cleaner
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                Log.i("Cleaning", "Cleaning old entries in db");
                for(Item item : Entity.query(Item.class).executeMulti()) {
                    long elapsedMins = (new Date().getTime() - item.date.getTime()) / 60000;
                    if(!item.isAvailable && elapsedMins > 30) {
                        item.delete();
                    }
                }
            }
        };
        timer.schedule(task, 0, 15 * 60 * 1000); // Check every 15 minutes
        context = this;
    }
    
    public static String getServerPort() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isUnstable = pref.getBoolean("debug_unstable_server", false);
        if(isUnstable) {
            return "8001";
        } else {
            return "8000";
        }
    }
    
    public static boolean isDebug() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean("debug_verbose", false);
    }
    
    public static boolean isDebugLogging() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean("debug_logging_verbose", false);
    }
}