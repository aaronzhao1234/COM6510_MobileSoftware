package com.example.mobiledev.viewmodel;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.mobiledev.database.AppDatabase;
import com.example.mobiledev.database.PathDao;
import com.example.mobiledev.database.PathPhotoDao;
import com.example.mobiledev.model.Path;
import com.example.mobiledev.model.PathPhoto;

import java.util.List;

public class GalleryRepository {

    private PathDao pathDao;
    private PathPhotoDao pathPhotoDao;

    private LiveData<List<Path>> allPaths;
    private LiveData<List<PathPhoto>> allPathPhotos;

    public GalleryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);

        pathDao = db.pathDao();
        pathPhotoDao = db.pathPhotoDao();

        allPaths = pathDao.getAll();
        allPathPhotos = pathPhotoDao.getAll();
    }

    public LiveData<List<Path>> getAllPaths() {
        return allPaths;
    }

    public LiveData<List<PathPhoto>> getAllPathPhotos() {
        return allPathPhotos;
    }

}
