package com.sleepsense.snoozebud.service;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sleepsense.snoozebud.activity.AlarmActivity;

import java.util.Map;

/**
 * Created by shayne on 2017-12-03.
 */

public class SnoozebudFcmService extends FirebaseMessagingService {

    private static final String TAG = "FCM_SERVICE";
    private static final String MESSAGE_RECEIVED = "";

    /**
     * Open the alarm activity when an alarm notification is received.
     * Add this in the service so we can launch the app without having it active
     * or in the background.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> messageData = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + messageData);

            //if (messageData.get("type").equals("alarm")) {
                Intent intent = new Intent(this, AlarmActivity.class);
                startActivity(intent);
            //}
        }
    }
}
