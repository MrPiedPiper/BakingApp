package com.fancystachestudios.bakingapp.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Ingredient;

import java.util.ArrayList;

/**
 * Listview implemented with help from
 * Article: https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
 * Github link: https://github.com/laaptu/appwidget-listview/tree/appwidget-listview1
 */
public class BakingWidget extends AppWidgetProvider {

    private static RemoteViews updateWidgetListView(Context context, int appWidgetId, ArrayList<Ingredient> ingredients){
        //Get the layout
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        //Get the adapter
        Intent serviceIntent = new Intent(context, WidgetService.class);
        //Attach the WidgetID
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //Set the adapter
        remoteViews.setRemoteAdapter(appWidgetId, R.id.appwidget_listview, serviceIntent);
        //Set the empty view
        remoteViews.setEmptyView(R.id.appwidget_listview, R.id.widget_empty_view);
        //Return layout
        return remoteViews;
    }
    public static void updateAppWidget(Context context, int[] appWidgetIds, AppWidgetManager appWidgetManager, ArrayList<Ingredient> ingredients){
        //For each widget,
        for(int appWidgetId : appWidgetIds){
            //Update said widget
            appWidgetManager.updateAppWidget(appWidgetId, updateWidgetListView(context, appWidgetId, ingredients));
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Trigger update
        BakingWidgetService.startActionUpdateBakingWidgets(context);
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

