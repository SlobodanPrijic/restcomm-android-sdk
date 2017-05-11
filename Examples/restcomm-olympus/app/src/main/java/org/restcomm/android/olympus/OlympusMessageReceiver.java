package org.restcomm.android.olympus;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.hyperether.kokoda.KokodaLogger;
import com.hyperether.kokoda.KokodaManager;
import com.hyperether.kokoda.KokodaMessageReceiver;

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
        try {
            if (message != null)
                KokodaLogger.d(TAG,
                        "parse message: from = " + message.getFrom() + ", message ID = " +
                                message.getMessageId() + ", send Time = " + message.getSentTime() +
                                ", data = " + message.getData() + ", from = " + message.getFrom());
        } catch (Exception e) {
            KokodaLogger.e(TAG, "parse message ", e);
        }

        if (KokodaManager.isInitiated()) {
            super.onMessageReceived(message);
        } else {
            Intent startMainActivity = new Intent(this, MainActivity.class);
            startMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startMainActivity.setAction(Intent.ACTION_MAIN);
            startActivity(startMainActivity);
        }
    }
}
