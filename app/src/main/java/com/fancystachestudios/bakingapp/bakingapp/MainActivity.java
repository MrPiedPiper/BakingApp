package com.fancystachestudios.bakingapp.bakingapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;
import com.fancystachestudios.bakingapp.bakingapp.network.APIClient;
import com.fancystachestudios.bakingapp.bakingapp.network.APIInterface;

import java.util.ArrayList;

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

public class MainActivity extends AppCompatActivity {

    APIInterface apiInterface;
    ArrayList<Recipe> recipies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        getJson();
    }

    private void getJson(){
        Call<ArrayList<Recipe>> call = apiInterface.doGetRecipe();
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                Log.d("napuResponse", "onResponse");
                recipies.addAll(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.d("napuResponse", t.toString());
                call.cancel();
            }
        });
    }


}
