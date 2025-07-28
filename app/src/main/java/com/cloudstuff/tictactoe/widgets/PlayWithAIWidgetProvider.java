package com.cloudstuff.tictactoe.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.annotation.GameMode;
import com.cloudstuff.tictactoe.utils.Constants;

public class PlayWithAIWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName thisWidget = new ComponentName(context, PlayWithAIWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int currentWidgetId : allWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_play_with_ai);
            views.setTextViewText(R.id.tv_play_with_ai, context.getString(R.string.action_ai));

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Constants.BundleExtra.GAME_MODE, GameMode.AI);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(context.getString(R.string.action_ai));

            PendingIntent pending = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.tv_play_with_ai, pending);

            appWidgetManager.updateAppWidget(currentWidgetId, views);
        }
    }
}