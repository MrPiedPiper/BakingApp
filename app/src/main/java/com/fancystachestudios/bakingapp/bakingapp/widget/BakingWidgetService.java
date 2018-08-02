package com.fancystachestudios.bakingapp.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Ingredient;
import com.fancystachestudios.bakingapp.bakingapp.room.AppDatabase;
import com.fancystachestudios.bakingapp.bakingapp.room.RecipeRoomSingleton;

import java.util.ArrayList;

public class BakingWidgetService extends IntentService {

    public static String ACTION_UPDATE_BAKING_WIDGETS = "com.fancystachestudios.bakingapp.bakingapp.widget.BakingWidgetService.action.update_baking_widgets";

    public BakingWidgetService() {
        super("BakingWidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            //Retrieve the action
            final String action = intent.getAction();
            //If it's to update the widget
            if(ACTION_UPDATE_BAKING_WIDGETS.equals(action)){
                //Call handleActionUpdateBakingWidgets
                handleActionUpdateBakingWidgets();
            }
        }
    }

    private void handleActionUpdateBakingWidgets(){
        //Get an instance of the database
        AppDatabase myDatabase = RecipeRoomSingleton.getInstance(this);
        //If it's empty, forget it.
        if(myDatabase.daoAccess().getAll().isEmpty())return;
        //Get the items
        ArrayList<Ingredient> ingredients = (myDatabase.daoAccess().getAll()).get(0).getIngredients();
        //Get the WidgetManager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        //Get the widget IDs
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingWidget.class));
        //Notify the manager that the data has changed for the listview
        //TODO is it here?
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_listview);

        //Update the appwidget
        BakingWidget.updateAppWidget(this, appWidgetIds, appWidgetManager, ingredients);
    }

    public static void startActionUpdateBakingWidgets(Context context){
        //Create a service intent to update the baking widget
        Intent intent = new Intent(context, BakingWidgetService.class);
        intent.setAction(ACTION_UPDATE_BAKING_WIDGETS);

        //Trigger it
        context.startService(intent);
    }
}
