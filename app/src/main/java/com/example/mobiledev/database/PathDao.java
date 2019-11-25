package com.example.mobiledev.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mobiledev.model.Path;

import java.util.List;

@Dao
public interface PathDao {

    @Query("SELECT * FROM path")
    LiveData<List<Path>> getAll();

    @Query("SELECT * FROM path WHERE id = (:pathId)")
    LiveData<List<Path>> getById(int pathId);

    @Insert
    void insertAll(Path... paths);

    @Delete
    void delete(Path path);

}
