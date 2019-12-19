package uk.ac.shef.oak.com4510.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import uk.ac.shef.oak.com4510.model.LocationTracking;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;

/**
 * This is a view model responsible for storing and modifying
 * entries in the database using MVVM architecture.
 */
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

    public LiveData<List<Path>> searchByTitle(String title) {
        return repository.getPathsByTitle(title);
    }

    public LiveData<List<LocationTracking>> getLocationsByPath(int pathId) {
        return repository.getAllLocationByPathId(pathId);
    }

    public LiveData<List<Path>> getAllPaths() {
        return allPaths;
    }

    public LiveData<List<Path>> getPathById(int id) {
        return repository.getPathById(id);
    }

    public LiveData<PathPhoto> getPhotoById(int id) {
        return repository.getPathPhotoById(id);
    }

    public LiveData<List<PathPhoto>> getAllPathPhotos() {
        return allPathPhotos;
    }

    public void removePhoto(PathPhoto pathPhoto) {
        repository.remove(pathPhoto);
    }

    public void removePath(Path path) {
        repository.remove(path);
    }

    public void removeLocation(LocationTracking location) {
        repository.remove(location);
    }

    public void insertPhoto(PathPhoto pathPhoto, GalleryRepository.InsertCallback callback) {
        repository.insert(pathPhoto, callback);
    }

    public void insertPath(Path path, GalleryRepository.InsertCallback callback) {
        repository.insert(path, callback);
    }

    public void insertLocationTracking(LocationTracking locationTracking) {
        repository.insert(locationTracking);
    }

    public LiveData<List<PathPhoto>> getPhotosByPath(int pathId) {
        return repository.getPathPhotosByPathId(pathId);
    }

}
