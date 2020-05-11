package MonitorService;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.monitorapp_v1.R;
import com.example.monitorapp_v1.ShopIndividualProductDisplay;

public class NotificationManager {

    private static int bundleNotificationId = 100;
    private static int singleNotificationId = 100;

    //Used for testing purposes
    static void showNotificationSingle(Context context){
        String notificationChannelID = "Channel";

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelID, "inducesmile", android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context,notificationChannelID)
                .setLights(Color.RED, 1, 1)
                .setContentTitle("Service started")
                .setContentText("Started")
                .setSmallIcon(R.mipmap.ic_launcher);
        if (notificationManager != null) {
            notificationManager.notify(1, notification.build());
        }
    }

    public static void sendNotification(Context context,String shopName, String image,String itemTitle, String URL, String newPrice, int index) {

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String bundle_notification_id = "bundle_notification_" + bundleNotificationId;

        //Start an activity with specific data when a notification is clicked
        Intent resultIntent = new Intent(context, ShopIndividualProductDisplay.class);
        resultIntent.putExtra("ShopNameAttribute", shopName);
        resultIntent.putExtra("LinkOfIndividualProduct", URL);
        resultIntent.putExtra("ImageOfIndividualProduct", image);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, index, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //We need to update the bundle notification every time a new notification comes up.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannels().size() < 2) {
                NotificationChannel groupChannel = new NotificationChannel("bundle_channel_id", "bundle_channel_name", android.app.NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(groupChannel);
                NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", android.app.NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder summaryNotificationBuilder = new NotificationCompat.Builder(context, "bundle_channel_id")
                .setGroup(bundle_notification_id)
                .setGroupSummary(true)
                .setContentTitle("MonitorApp " + bundleNotificationId)
                .setContentText("Content Text for group summary")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        if (singleNotificationId == bundleNotificationId) singleNotificationId = bundleNotificationId + 1;
        else singleNotificationId++;

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "channel_id")
                .setGroup(bundle_notification_id)
                .setContentTitle(itemTitle)
                .setContentText("Noul pret: "+ newPrice+" Lei")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setGroupSummary(false)
                .setContentIntent(resultPendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(singleNotificationId, notification.build());
            notificationManager.notify(bundleNotificationId, summaryNotificationBuilder.build());
        }
    }
}
