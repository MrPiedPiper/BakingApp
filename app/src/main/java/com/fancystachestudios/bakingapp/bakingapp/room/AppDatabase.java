package com.fancystachestudios.bakingapp.bakingapp.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.fancystachestudios.bakingapp.bakingapp.customClasses.Recipe;

@Database(entities = {Recipe.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase{
    public abstract DaoAccess daoAccess();
}
