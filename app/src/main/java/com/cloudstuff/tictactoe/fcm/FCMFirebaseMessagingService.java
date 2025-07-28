package com.cloudstuff.tictactoe.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.TicTacToe;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.model.GamePlayRequest;
import com.cloudstuff.tictactoe.utils.Constants;
import com.cloudstuff.tictactoe.utils.PreferenceUtils;

import java.util.Map;

import timber.log.Timber;

public class FCMFirebaseMessagingService extends FirebaseMessagingService {

    //region #Singleton Instance
    private Gson gson = TicTacToe.getInstance().getAppComponent().provideGson();
    private PreferenceUtils preferenceUtils = TicTacToe.getInstance().getAppComponent().providePreferenceUtils();
    //endregion

    //region #InBuilt Methods

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Check Notification is on or off
        if (!preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_NOTIFICATION_ON)) {
            return;
        }

        if (remoteMessage != null) {
            Timber.e("From: %s", remoteMessage.getFrom());
            Timber.e("FCM : %s", remoteMessage.toString());

            //Manage Remote Config Instant Update
            if (remoteMessage.getData().size() > 0) {
                Timber.e("FCM 1: %s", remoteMessage.getData());
                if (Boolean.parseBoolean(remoteMessage.getData().get("instant_update_remote_config"))) {
                    preferenceUtils.setBoolean(Constants.PreferenceConstant.INSTANT_UPDATE_REMOTE_CONFIG, true);
                }
            }

            if (remoteMessage.getData().size() > 0) {
                Timber.e("FCM 1: %s", remoteMessage.getData());
                Map<String, String> data = remoteMessage.getData();
                String[] values = data.values().toArray(new String[0]);
                if (values.length == 5) {
                    if (TicTacToe.isAppInBackground()) {
                        String title;
                        String message;
                        if (values[2].equals("100")) {
                            title = "New Request Received";
                            message = values[4] + " has sent you request to play TicTacToe game.";
                        } else {
                            title = "Request Accepted";
                            message = values[1] + " has accepted your request to play TicTacToe game.";
                        }
                        sendGamePlayRequestNotification(title, message);
                    } else {
                        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
                        Intent intent = new Intent();
                        GamePlayRequest request = new GamePlayRequest();
                        if (values[2].equals("100")) {
                            request.playerOneId = values[3];
                            request.playerOneName = values[4];
                            intent.setAction("ACTION_GAME_PLAY_REQUEST");
                        } else {
                            request.playerOneId = values[0];
                            request.playerOneName = values[1];
                            request.playerTwoId = values[3];
                            request.playerTwoName = values[4];
                            intent.setAction("ACTION_START_GAME");
                        }
                        intent.putExtra("request", request);
                        broadcastManager.sendBroadcast(intent);

                    }
                }
            } else if (remoteMessage.getNotification() != null) {
                Timber.e("FCM 2: %s", gson.toJson(remoteMessage));
                sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Timber.i("Refreshed token: %s", token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
        preferenceUtils.setString(Constants.PreferenceConstant.FCM_TOKEN, token);
    }
    //endregion

    //region #Custom Methods

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(String title, String message) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int notificationId = (int) System.currentTimeMillis();
        String channelId = getString(R.string.notification_channel_tictactoe);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentIntent(getNotificationContentIntent())
                .setSmallIcon(R.drawable.ic_notifications)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(defaultSoundUri)
                .setVibrate(null)
                .setOngoing(false);

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }

    private void sendGamePlayRequestNotification(String title, String message) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int notificationId = (int) System.currentTimeMillis();
        String channelId = getString(R.string.notification_channel_tictactoe);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentIntent(getNotificationContentIntent())
                .setSmallIcon(R.drawable.ic_notifications)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(defaultSoundUri)
                .setVibrate(null)
                .setOngoing(false);

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notificationBuilder.build());
        }
    }


    private PendingIntent getNotificationContentIntent() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        Intent parentIntent = new Intent(this, MainActivity.class);
        stackBuilder.addNextIntent(parentIntent);
        stackBuilder.addNextIntent(notificationIntent);
        return stackBuilder.getPendingIntent((int) System.currentTimeMillis(), PendingIntent.FLAG_UPDATE_CURRENT);
    }
    //endregion
}