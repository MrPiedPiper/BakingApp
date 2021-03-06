package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.fancystachestudios.bakingapp.bakingapp.IdlingResource.SimpleIdlingResource;
import com.fancystachestudios.bakingapp.bakingapp.adapters.RecipeAdapter;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.network.APIClient;
import com.fancystachestudios.bakingapp.bakingapp.network.APIInterface;
import com.fancystachestudios.bakingapp.bakingapp.room.AppDatabase;
import com.fancystachestudios.bakingapp.bakingapp.singletons.RecipeRoomSingleton;
import com.fancystachestudios.bakingapp.bakingapp.widget.BakingWidgetService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 * Retrofit implemented with help from ANUPAM CHUGH's article "Retrofit Android Example Tutorial"
 *
 * Author: Anupam Chugh
 * Article Title: Retrofit Android Example Tutorial
 * Website Name: JournalDev
 * Web Address: https://www.journaldev.com/13639/retrofit-android-example-tutorial
 *
 */

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener{

    @BindView(R.id.recipe_recyclerview)
    RecyclerView recipeRecyclerview;

    APIInterface apiInterface;
    ArrayList<Recipe> recipes = new ArrayList<>();
    RecipeAdapter.RecipeClickListener recipeClickListener;
    RecipeAdapter recipeAdapter;
    GridLayoutManager gridLayoutManager;

    AppDatabase myDatabase;

    Context context;

    @Nullable private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = this;

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getJson();

        recipeClickListener = this;
        recipeAdapter = new RecipeAdapter(this, recipes, recipeClickListener);
        recipeRecyclerview.setAdapter(recipeAdapter);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if(dpWidth >= 600){
            gridLayoutManager = new GridLayoutManager(this, 3);
        }else{
            gridLayoutManager = new GridLayoutManager(this, 1);
        }

        recipeRecyclerview.setLayoutManager(gridLayoutManager);

        myDatabase = RecipeRoomSingleton.getInstance(this);
    }

    private void getJson(){
        Call<ArrayList<Recipe>> call = apiInterface.doGetRecipe();
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                recipes.addAll(response.body());
                recipeAdapter.setDataset(recipes);

                if(mIdlingResource != null){
                    mIdlingResource.setIdleState(true);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                call.cancel();
            }
        });
    }


    @Override
    public void onRecipeClick(final int clickedItemIndex) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(myDatabase.daoAccess().fetchSingleMovieById(recipes.get(clickedItemIndex).getId()) == null){
                    myDatabase.daoAccess().insertSingleRecipe(recipes.get(clickedItemIndex));
                }
                myDatabase.daoAccess().deleteAll();
                myDatabase.daoAccess().insertSingleRecipe(recipes.get(clickedItemIndex));
                BakingWidgetService.startActionUpdateBakingWidgets(context);
            }
        }).start();
/*
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, BakingWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidget_listview);*/


        Intent stepsIntent = new Intent(this, RecipeActivity.class);
        stepsIntent.putExtra(getString(R.string.recipe_pass_key), recipes.get(clickedItemIndex));

        startActivity(stepsIntent);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource(){
        if(mIdlingResource == null){
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
