package org.restcomm.android.olympus;

import android.content.Context;

import com.hyperether.kokoda.KokodaManager;
import com.hyperether.kokoda.NotificationHandler;

/**
 * Created by slobo on 5/16/2017.
 */

public class AppStateManager {

    private static AppStateManager instance;
    private boolean callInProgress = false;
    private int callNotificationId;

    private AppStateManager() {

    }

    public static AppStateManager getInstance() {
        if (instance == null)
            instance = new AppStateManager();
        return instance;
    }

    public boolean isCallInProgress() {
        return callInProgress;
    }

    public void setCallInProgress(boolean callInProgress) {
        this.callInProgress = callInProgress;
    }

    public boolean isActivityStackEmpty() {
        return KokodaManager.isInitiated() && KokodaManager.getInstance().isActivityStackEmpty();
    }

    public boolean isAppBackgrounded() {
        return !KokodaManager.isInitiated() || !KokodaManager.getInstance().isAppForegrounded();
    }

    public void showIncomingCallNotification(Context context, String from) {
        callNotificationId = NotificationHandler.getInstance().showCustomNotification(context,
                "Incoming call", from + " is calling...");
    }

    public void removeIncomingCallNotification(Context context) {
        NotificationHandler.getInstance().removeNotification(context, callNotificationId);
        callNotificationId = 0;
    }
}
