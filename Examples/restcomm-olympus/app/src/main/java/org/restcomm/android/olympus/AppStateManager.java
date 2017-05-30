package org.restcomm.android.olympus;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import com.hyperether.kokoda.KokodaManager;
import com.hyperether.kokoda.notification.NotificationHandler;

/**
 * Created by slobo on 5/16/2017.
 */

public class AppStateManager {

    private static AppStateManager instance;
    private boolean callInProgress = false;
    private int callNotificationId;
    private static PowerManager.WakeLock wakeLock = null;

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

    public void wakeScreen(Context context) {

        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                .newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP), "WAKE_UP-DEVICE");
        wakeLock.acquire();
        releaseScreenLock();
    }

    public void turnScreenOn(final Activity activity) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Window w = activity.getWindow();
                    w.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                    w.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                    w.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                }
            });
        }
    }

    private void releaseScreenLock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

}
