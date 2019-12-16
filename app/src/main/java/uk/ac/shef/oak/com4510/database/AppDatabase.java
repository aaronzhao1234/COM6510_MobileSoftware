package uk.ac.shef.oak.com4510.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.Converters;

import java.util.Date;

@Database(entities = {Path.class, PathPhoto.class}, version = 12, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract PathDao pathDao();
    public abstract PathPhotoDao pathPhotoDao();

    public static Path[] paths = new Path[] {
            new Path("Path 1", "Path 1 Description", new Date(), new Date()),
            new Path("Path 2", "Path 2 Description", new Date(), new Date()),
            new Path("Path 3", "Path 3 Description", new Date(), new Date()),
            new Path("Path 4", "Path 4 Description", new Date(), new Date()),
            new Path("Path 5", "Path 5 Description", new Date(), new Date())
    };

    public static PathPhoto[] photos = new PathPhoto[] {
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 1),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 1),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 1),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 1),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 1),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 2),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 2),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 1),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 4),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 4),
            new PathPhoto("none", -1, -1, Integer.toString(R.drawable.image), 4)
    };

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_db")
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }

        return INSTANCE;
    }

}
