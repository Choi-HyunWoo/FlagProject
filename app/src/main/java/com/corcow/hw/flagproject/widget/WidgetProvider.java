package com.corcow.hw.flagproject.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.corcow.hw.flagproject.R;

/**
 * Created by multimedia on 2016-05-27.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String MyOnClick = "myOnClickTag";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        // Get all ids
        ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_download_provider);

            remoteViews.setOnClickPendingIntent(R.id.widget_btn_download, getPendingSelfIntent(context, MyOnClick));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void onReceive(Context context, Intent intent) {

        if (MyOnClick.equals(intent.getAction())){
            //your onClick action is here

        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
/*
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget_download_provider);
        views.setTextViewText(R.id.textView, "AppWidgetTest");
        views.
        Intent intent = new Intent(context, MainActivity.class);
        // PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // views.setOnClickPendingIntent(R.id.imageView, pi);
        appWidgetManager.updateAppWidget(appWidgetId, views);
*/
    }

    @TargetApi(17)
    static boolean isKeyguard(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        if (Build.VERSION.SDK_INT >= 17 ) {
            Bundle b = appWidgetManager.getAppWidgetOptions(appWidgetId);
            int category = b.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
            return category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;
        } else {
            return false;
        }
    }
}