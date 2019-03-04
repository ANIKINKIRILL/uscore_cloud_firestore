//package com.example.admin.uscore001;
//
//import android.app.NotificationManager;
//import android.support.v4.app.NotificationCompat;
//
//import com.google.firebase.messaging.RemoteMessage;
//
///**
// * Service for Notifications
// */
//
//public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
////        String title = remoteMessage.getNotification().getTitle();
////        String body = remoteMessage.getNotification().getBody();
//
//        createNotification("Title", "Body");
//
//    }
//
//    /**
//     * Create Notification
//     * @param title notification title
//     * @param body  notification body
//     */
//
//    private void createNotification(String title, String body){
//        NotificationCompat.Builder builder =
//        new NotificationCompat.Builder(this, getString(R.string.teacher_confirm_request_notification_channel_id));
//        builder
//            .setSmallIcon(R.drawable.app_image)
//            .setContentTitle(title)
//            .setContentText(body);
//
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(1, builder.build());
//    }
//
//}
