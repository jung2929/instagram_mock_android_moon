package com.example.myinstagram;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.graphics.Color;
import android.os.PowerManager;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.myinstagram.activitys.FeedActivty;
import com.example.myinstagram.activitys.LoginActivity;

import android.app.NotificationManager;

import android.app.PendingIntent;

import android.content.Intent;

import android.media.RingtoneManager;

import android.net.Uri;

import android.os.Build;

import android.os.Vibrator;

import android.support.v4.app.NotificationCompat;

import android.util.Log;



import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.messaging.RemoteMessage;



public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebase";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("토큰",s);
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {  //data payload로 보내면 실행
        // ...
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //여기서 메세지의 두가지 타입(1. data payload 2. notification payload)에 따라 다른 처리를 한다.
        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
//                handleNow();
//            }
//
//        }



        // Check if message contains a notification payload.
        //앱이 구동중일대도
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
            //        .setSmallIcon(R.mipmap.group_7) // 알림 영역에 노출 될 아이콘.
            //        .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
            //        .setContentText(remoteMessage.getNotification().getBody()); // Firebase Console 에서 사용자가 전달한 메시지내용

            //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            //notificationManagerCompat.notify(0x1001, notificationBuilder.build());

            sendNotification(title, body);
        }
        else{
            sendNotification(title, body);
        }



        //String title = remoteMessage.getNotification().getTitle();
        //String body = remoteMessage.getNotification().getBody();

        //sendNotification(title, body);
    }

    private void scheduleJob() {
        //이건 아직 나중에 알아 볼것.
        Log.d(TAG, "이것에 대해서는 나중에 알아 보자.");
    }

    private void handleNow() {
        Log.d(TAG, "10초이내 처리됨");
    }

    private void sendNotification(String title, String messageBody) {
        if (title == null){
            //제목이 없는 payload이면
            title = "댓글 작성"; //기본제목을 적어 주자.
        }
        Intent intent = new Intent(this, FeedActivty.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.group_7)
                .setTicker("알람 간단한 설명")
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000})
                .setLights(Color.BLUE, 1,1)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //화면을 깨운다.(이방법은 Deprecated 되었다고 한다. 당장 작동은 되지만 나중에 어떻게 될지 모른다?)
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK  | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakeLock.acquire(3000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}