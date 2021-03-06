package com.fancystachestudios.bakingapp.bakingapp.singletons;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.fancystachestudios.bakingapp.bakingapp.R;
import com.fancystachestudios.bakingapp.bakingapp.room.AppDatabase;

public class RecipeRoomSingleton {

    private static AppDatabase myDatabase;

    private RecipeRoomSingleton(Context context){

    }

    public static AppDatabase getInstance(Context currContext){
        if(myDatabase == null){
            myDatabase = Room.databaseBuilder(currContext, AppDatabase.class, currContext.getString(R.string.room_name)).build();
        }
        return myDatabase;
    }
}
