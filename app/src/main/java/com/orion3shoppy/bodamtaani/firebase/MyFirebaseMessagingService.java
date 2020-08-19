/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.orion3shoppy.bodamtaani.firebase;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created  on 6/30/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    Context context;
    String ANDROID_CHANNEL_ID = "mikey_01";
    public MyFirebaseMessagingService() {
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Recieved", "From: " + remoteMessage.getFrom());
        Log.d("Recieved", "Message data payload: " + remoteMessage.getData());

//        addNotification();
    }
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d("MY_TOKEN", "Refreshed token: " + token);
    }


//    private void addNotification() {
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel();
//            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText("Mytrux Running")
//                    .setAutoCancel(true);
//            Notification notification = builder.build();
//            startForeground(NOTIFICATION_ID, notification);
//
//        } else {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText("Mytrux is Running...")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setAutoCancel(true);
//            Notification notification = builder.build();
//            startForeground(NOTIFICATION_ID, notification);
//
//        }
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    ANDROID_CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT
//            );
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(serviceChannel);
//        }
//    }
//



}
