package uk.ac.shef.oak.com4510.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.utils.DaoInterface;

/**
 * This is the database DAO for the {@link Path} entry.
 */
@Dao
public interface PathDao extends DaoInterface {

    @Query("SELECT * FROM path WHERE title LIKE '%' || :titleQuery || '%' ORDER BY id DESC")
    LiveData<List<Path>> getAllByTitle(String titleQuery);

    @Query("SELECT * FROM path ORDER BY id DESC")
    LiveData<List<Path>> getAll();

    @Query("SELECT * FROM path WHERE id == (:pathId)")
    LiveData<List<Path>> getById(int pathId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(Path... paths);

    @Delete
    void delete(Path path);

    @Query("SELECT COUNT(*) FROM path")
    int count();

}
