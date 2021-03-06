package com.fancystachestudios.bakingapp.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Ingredient;
import com.fancystachestudios.bakingapp.bakingapp.room.AppDatabase;
import com.fancystachestudios.bakingapp.bakingapp.singletons.RecipeRoomSingleton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class WidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    private int appWidgetId;
    ArrayList<Ingredient> ingredientsArray = new ArrayList<>();

    public WidgetAdapter(@NonNull Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        refreshData();
    }

    private void refreshData() {
        try {
            ingredientsArray = new refreshDataAsyncTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class refreshDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Ingredient>>{

        @Override
        protected ArrayList<Ingredient> doInBackground(Void... voids) {
            AppDatabase myDatabase = RecipeRoomSingleton.getInstance(context);
            return myDatabase.daoAccess().getAll().get(0).getIngredients();
        }
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        refreshData();
    }

    public void setDataset(ArrayList<Ingredient> newDataset){
        ingredientsArray = newDataset;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return ingredientsArray.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if(i > ingredientsArray.size()-1)return null;
        Ingredient ingredient = ingredientsArray.get(i);

        //Find views
        RemoteViews widgetItem = new RemoteViews(context.getPackageName(), R.layout.baking_widget_item);

        double quantity = ingredient.getQuantity();
        String measure = ingredient.getMeasure();
        String ingredientText = ingredient.getIngredient();
        if(quantity == (int) quantity){
            if(quantity == 1){
                widgetItem.setTextViewText(R.id.widget_item_text, context.getString(R.string.ingredient_one_or_less, (int) quantity, measure, ingredientText));
            }else{
                widgetItem.setTextViewText(R.id.widget_item_text, context.getString(R.string.ingredient_other, (int) quantity, measure, ingredientText));
            }
        }else{
            if(quantity < 1){
                widgetItem.setTextViewText(R.id.widget_item_text, context.getString(R.string.ingredient_one_or_less, quantity, measure, ingredientText));
            }else{
                widgetItem.setTextViewText(R.id.widget_item_text, context.getString(R.string.ingredient_other, quantity, measure, ingredientText));
            }
        }
        return widgetItem;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

