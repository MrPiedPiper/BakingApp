package com.fancystachestudios.bakingapp.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.fancystachestudios.bakingapp.bakingapp.R;

/**
 * Listview implemented with help from
 * Article: https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
 * Github link: https://github.com/laaptu/appwidget-listview/tree/appwidget-listview1
 */
public class BakingWidget extends AppWidgetProvider {

    private RemoteViews updateWidgetListView(Context context, int appWidgetId){
        Log.d("naputest", "Made widget log");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        Intent serviceIntent = new Intent(context, WidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.appwidget_listview, serviceIntent);
        //remoteViews.setEmptyView(R.id.appwidget_listview, R.id.widget_empty_view);
        return remoteViews;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int N = appWidgetIds.length;
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
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
}

