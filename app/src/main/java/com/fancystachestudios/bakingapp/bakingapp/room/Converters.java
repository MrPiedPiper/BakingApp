package com.fancystachestudios.bakingapp.bakingapp.room;

import android.arch.persistence.room.TypeConverter;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Ingredient;
import com.fancystachestudios.bakingapp.bakingapp.customClasses.Step;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class Converters {

    private static Gson gson = new Gson();

    @TypeConverter
    public static ArrayList<Ingredient> ingredientStringToArrayList(String data){
        if(data == null){
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList<Ingredient>>(){}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String ingredientArrayListToString(ArrayList<Ingredient> data){
        return gson.toJson(data);
    }

    @TypeConverter
    public static ArrayList<Step> StepStringToArrayList(String data){
        if(data == null){
            return new ArrayList<>();
        }
        Type listType = new TypeToken<ArrayList<Ingredient>>(){}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String StepArrayListToString(ArrayList<Step> data){
        return gson.toJson(data);
    }
}
