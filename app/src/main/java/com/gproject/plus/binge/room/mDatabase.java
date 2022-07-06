package com.gproject.plus.binge.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {mEntity.class}, version = 1)
public abstract class mDatabase extends RoomDatabase {
    public abstract mDao moviesDao();


}
