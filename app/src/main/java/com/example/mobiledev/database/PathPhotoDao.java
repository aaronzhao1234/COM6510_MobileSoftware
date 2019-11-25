package com.example.mobiledev.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobiledev.model.PathPhoto;

import java.util.List;

@Dao
public interface PathPhotoDao {

    @Query("SELECT * FROM pathphoto")
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
