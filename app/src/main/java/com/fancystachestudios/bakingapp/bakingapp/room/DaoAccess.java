package com.fancystachestudios.bakingapp.bakingapp.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface DaoAccess {
    @Insert
    void insertSingleRecipe (Recipe recipe);
    @Insert
    void insertMultipleRecipies(List<Recipe> recipeList);
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAll();
    @Query("SELECT * FROM Recipe WHERE id = :recipeId")
    Recipe fetchSingleMovieById (int recipeId);
    @Update
    void updateRecipe (Recipe recipe);
    @Delete
    void deleteRecipe (Recipe recipe);
    @Query("DELETE FROM Recipe")
    void deleteAll();
}
