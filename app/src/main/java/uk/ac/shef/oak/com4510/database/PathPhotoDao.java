package uk.ac.shef.oak.com4510.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.DaoInterface;

import java.util.List;

@Dao
public interface PathPhotoDao extends DaoInterface {

    @Query("SELECT * FROM pathphoto ORDER BY path_id DESC")
    LiveData<List<PathPhoto>> getAll();

    @Query("SELECT * FROM pathphoto WHERE id = (:pathPhotoId)")
    LiveData<List<PathPhoto>> getById(int pathPhotoId);

    @Query("SELECT * FROM pathphoto WHERE path_id = (:pathId)")
    LiveData<List<PathPhoto>> getAllByPathId(int pathId);

    @Insert
    void insertAll(PathPhoto... pathPhotos);

    @Delete
    void delete(PathPhoto pathPhoto);

}
