package com.cloudstuff.tictactoe.utils;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.cloudstuff.tictactoe.listener.OnInvitationSentListener;
import com.cloudstuff.tictactoe.model.FCMRequest;
import com.cloudstuff.tictactoe.model.GamePlayer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

public class FCMUtils {

    public static void sendFCMMessage(GamePlayer player, FCMRequest fcmRequest, OnInvitationSentListener listener) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.MILLISECONDS)
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Timber.d("JSON Request: %s", new Gson().toJson(fcmRequest));
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(fcmRequest));
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .method("POST", body)
                .addHeader("Authorization", "key=AAAAYFPAVC0:APA91bHf6Z20tvuI1V1JNg9wHOKMcuX9yc0PfRrmXGnylOsaWVIzO-TK51Szts0FZW9t4kXtAuDbqOguBaUPDZ2YhgpPdMqHNLF8UAtjbM4Mt5HB-56mHWilc0qLrLLLSj3aQJjGuThS")
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Timber.e("Error in sending invitation. %s", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Timber.d("Response: %s", response.body().string());
                if (listener != null) {
                    listener.onInvitationSent(player);
                }
            }
        });

    }

}
