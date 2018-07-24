package com.fancystachestudios.bakingapp.bakingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fancystachestudios.bakingapp.bakingapp.adapters.RecipeAdapter;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.network.APIClient;
import com.fancystachestudios.bakingapp.bakingapp.network.APIInterface;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        getJson();

        recipeClickListener = this;
        recipeAdapter = new RecipeAdapter(this, recipes, recipeClickListener);
        recipeRecyclerview.setAdapter(recipeAdapter);

        gridLayoutManager = new GridLayoutManager(this, 1);
        recipeRecyclerview.setLayoutManager(gridLayoutManager);

    }

    private void getJson(){
        Call<ArrayList<Recipe>> call = apiInterface.doGetRecipe();
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                recipes.addAll(response.body());
                recipeAdapter.setDataset(recipes);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d("napuResponse", t.toString());
                call.cancel();
            }
        });
    }


    @Override
    public void onRecipeClick(int clickedItemIndex) {
        Intent stepsIntent = new Intent(this, RecipeActivity.class);
        stepsIntent.putExtra(getString(R.string.recipe_to_steps_key), recipes.get(clickedItemIndex));

        startActivity(stepsIntent);
    }
}
