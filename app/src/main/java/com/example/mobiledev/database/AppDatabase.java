package com.example.mobiledev.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.example.mobiledev.model.Path;
import com.example.mobiledev.model.PathPhoto;
import com.example.mobiledev.utils.Converters;

@Database(entities = {Path.class, PathPhoto.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract PathDao pathDao();
    public abstract PathPhotoDao pathPhotoDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_db").build();
                }
            }
        }
        return INSTANCE;
    }

}
