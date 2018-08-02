package com.fancystachestudios.bakingapp.bakingapp.widget;

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Ingredient;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    private int appWidgetId;
    ArrayList<Ingredient> ingredientsArray = new ArrayList<>();

    public WidgetAdapter(@NonNull Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if(ingredientsArray.isEmpty()){
            ingredientsArray.add(new Ingredient(5, "CUPS", "Milk"));
            ingredientsArray.add(new Ingredient(2, "TBSPS", "Rainbow"));
            ingredientsArray.add(new Ingredient(1, "LITRE", "oie"));
            ingredientsArray.add(new Ingredient(1, "thing", ";lkj"));
            ingredientsArray.add(new Ingredient(1, "a", "asd"));
            ingredientsArray.add(new Ingredient(1, "ma", "klkj"));
            ingredientsArray.add(new Ingredient(1, "doodle", "afj"));
            ingredientsArray.add(new Ingredient(1, "hi", "rocks"));
        }
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

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

