package org.restcomm.android.olympus;

import com.hyperether.kokoda.KokodaManager;

/**
 * Created by slobo on 5/16/2017.
 */

public class AppStateManager {

    private static AppStateManager instance;
    private boolean callInProgress = false;

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
}
