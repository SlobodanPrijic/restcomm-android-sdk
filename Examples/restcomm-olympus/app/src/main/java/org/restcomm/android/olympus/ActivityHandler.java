package org.restcomm.android.olympus;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Slobodan on 5/12/2017.
 */

public class ActivityHandler implements Application.ActivityLifecycleCallbacks {
    private static final int IN_FOREGROUND = 2;
    private static final int IN_BACKGROUND = 1;
    private static final String TAG = ActivityHandler.class.getSimpleName();

    private static ActivityHandler instance;
    private volatile int applicationStatus = IN_BACKGROUND;

    private HashMap<Activity, Integer> activities = new HashMap<>();

    public static ActivityHandler getInstance() {
        if (instance == null)
            instance = new ActivityHandler();
        return instance;
    }

    private void addActivity(Activity a) {
        activities.put(a, 1);
    }

    private void removeActivity(Activity a) {
        activities.remove(a);
        Log.d(TAG, "removeActivity: " + " count = " + getStackCount() + "   activity: " +
                a.getClass().toString());
    }

    public int getStackCount() {
        return activities.size();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        addActivity(activity);
        applicationStatus = IN_FOREGROUND;
        Log.d(TAG, "applicationStatus = " + IN_FOREGROUND);
        Log.d(TAG, "onActivityStarted: " + " count = " + getStackCount() + "   activity: " +
                activity.getClass().toString());
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        removeActivity(activity);
        if (activities.size() == 0) {
            applicationStatus = IN_BACKGROUND;
            Log.d(TAG, "applicationStatus = " + IN_BACKGROUND);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public int getApplicationStatus() {
        return applicationStatus;
    }

    public boolean isAppForegrounded() {
        return applicationStatus == IN_FOREGROUND;
    }

    public boolean isAppBackgrounded() {
        return applicationStatus == IN_BACKGROUND;
    }
}
