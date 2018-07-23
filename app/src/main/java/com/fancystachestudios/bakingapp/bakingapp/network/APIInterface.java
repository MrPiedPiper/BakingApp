package com.fancystachestudios.bakingapp.bakingapp.network;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

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

public interface APIInterface {

    @GET("baking.json")
    Call<ArrayList<Recipe>> doGetRecipe();
}
