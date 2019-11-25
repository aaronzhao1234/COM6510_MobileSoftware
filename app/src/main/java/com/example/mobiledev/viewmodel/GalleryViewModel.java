package com.example.mobiledev.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobiledev.model.Path;
import com.example.mobiledev.model.PathPhoto;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private GalleryRepository repository;

    private LiveData<List<Path>> allPaths;
    private LiveData<List<PathPhoto>> allPathPhotos;

    public GalleryViewModel(Application application) {
        super(application);

        repository = new GalleryRepository(application);

        allPaths = repository.getAllPaths();
        allPathPhotos = repository.getAllPathPhotos();
    }

    public LiveData<List<Path>> getAllPaths() {
        return allPaths;
    }

    public LiveData<List<PathPhoto>> getAllPathPhotos() {
        return allPathPhotos;
    }

}
