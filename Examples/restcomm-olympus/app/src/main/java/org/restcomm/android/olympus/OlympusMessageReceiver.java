package org.restcomm.android.olympus;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.hyperether.kokoda.CustomPushNotification;
import com.hyperether.kokoda.KokodaLogger;
import com.hyperether.kokoda.KokodaMessageReceiver;
import com.hyperether.kokoda.NotificationHandler;

/**
 * Kokoda message receiver. Class just {@link RemoteMessage} send to {@link
 * com.hyperether.kokoda.KokodaManager}
 *
 * @author Nebojsa Brankovic
 * @version 1.0 - 4/27/2017
 */
public class OlympusMessageReceiver extends KokodaMessageReceiver {

    private static final String TAG = OlympusMessageReceiver.class.getSimpleName();

    public OlympusMessageReceiver() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        //call super method which will show campaign messages
        super.onMessageReceived(message);
        CustomPushNotification customPushNotification = null;
        try {
            if (message != null) {
                KokodaLogger.d(TAG,
                        "parse message: from = " + message.getFrom() + ", message ID = " +
                                message.getMessageId() + ", send Time = " + message.getSentTime() +
                                ", data = " + message.getData() + ", from = " + message.getFrom());
                if (message.getData() != null && message.getData().get("message") != null)
                    try {
                        customPushNotification = new Gson().fromJson(
                                message.getData().get("message"),
                                CustomPushNotification.class);
                    } catch (Exception e) {
                        KokodaLogger.e(TAG, "onMessageReceived", e);
                    }
            }
        } catch (Exception e) {
            KokodaLogger.e(TAG, "parse message ", e);
        }

        if (customPushNotification != null && CustomPushNotification.CUSTOM_MESSAGE
                .equalsIgnoreCase(customPushNotification.getMessageType())) {
            String msg = customPushNotification.getData().getMessage();
            if (!AppStateManager.getInstance().isCallInProgress() &&
                    AppStateManager.getInstance().isAppBackgrounded()) {
                try {
                    CallNotification callNot = new Gson().fromJson(msg, CallNotification.class);
                    if ("call".equals(callNot.getType())) {
                        Intent startMainActivity = new Intent(this, SigninActivity.class);
                        startMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startMainActivity.setAction(Intent.ACTION_MAIN);
                        startActivity(startMainActivity);
                        AppStateManager.getInstance().showIncomingCallNotification(
                                getApplicationContext(), callNot.getFrom());
                    } else {
                        NotificationHandler.getInstance().showCustomNotification(getApplicationContext(),
                                "", customPushNotification.getData().getMessage());
                    }
                } catch (Exception e) {
                    KokodaLogger.e(TAG, "parse call notification ", e);
                    NotificationHandler.getInstance().showCustomNotification(getApplicationContext(),
                            "", customPushNotification.getData().getMessage());
                }
            }
        }
    }
}
