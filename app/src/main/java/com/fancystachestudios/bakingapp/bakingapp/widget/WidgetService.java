package com.fancystachestudios.bakingapp.bakingapp.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Ingredient;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.room.AppDatabase;
import com.fancystachestudios.bakingapp.bakingapp.room.RecipeRoomSingleton;

import java.util.ArrayList;
import java.util.List;

public class WidgetService extends RemoteViewsService {

    WidgetAdapter widgetAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        final AppDatabase myDatabase = RecipeRoomSingleton.getInstance(this.getApplicationContext());
        widgetAdapter = new WidgetAdapter(this.getApplicationContext(), intent);
        return (widgetAdapter);
    }

}
