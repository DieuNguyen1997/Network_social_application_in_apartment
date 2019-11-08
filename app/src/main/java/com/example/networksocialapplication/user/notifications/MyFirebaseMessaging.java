package com.example.networksocialapplication.user.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.networksocialapplication.user.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    //gọi khi nhận được tin nhắn
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sentid = remoteMessage.getData().get("sentid");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null && sentid.equals(firebaseUser.getUid())) {
            sendNotificationMessage(remoteMessage);
        }
    }

    private void sendNotificationMessage(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
//Tin nhắn có thể có phiên bản RemoteMessage.Notification nếu chúng được nhận trong khi ứng dụng ở phía trước, nếu không chúng sẽ tự động được đăng lên khay thông báo.
    //Sử dụng lớp RemoteMessage.Builder để xây dựng các thể hiện thông báo để gửi qua gửi (RemoteMessage).
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));

        //tạo intent khi click vào thông báo
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("otherUserId",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        //chọn âm thanh thông báo
        Uri soundNotify = RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundNotify)
                .setContentIntent(pendingIntent);

        NotificationManager notificationMess = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if (i > 0){
            j = i;
        }

        notificationMess.notify(j, builder.build());
    }
}
