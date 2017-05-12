package org.restcomm.android.olympus;

import android.app.Activity;

import java.util.HashMap;

/**
 * Created by Slobodan on 5/12/2017.
 */

public class ActivityHandler {


    private static ActivityHandler instance;

    private HashMap<Activity, Integer> activities = new HashMap<>();

    public static ActivityHandler getInstance() {
        if (instance == null)
            instance = new ActivityHandler();
        return instance;
    }

    public void addActivity(Activity a) {
        activities.put(a, 1);
    }

    public void removeActivity(Activity a) {
        activities.remove(a);
    }

    public int getStackCount() {
        return activities.size();
    }
}
