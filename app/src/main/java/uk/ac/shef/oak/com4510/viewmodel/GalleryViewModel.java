package uk.ac.shef.oak.com4510.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;

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

    public LiveData<List<Path>> getPathById(int id) {
        return repository.getPathById(id);
    }

    public LiveData<List<PathPhoto>> getAllPathPhotos() {
        return allPathPhotos;
    }

    public LiveData<List<PathPhoto>> GetPhotosByPath(int pathId) {
        return repository.getPathPhotosByPathId(pathId);
    }

    public void removePhoto(PathPhoto pathPhoto) {
        repository.remove(pathPhoto);
    }

    public void removePath(Path path) {
        repository.remove(path);
    }

    public void insertPhoto(PathPhoto pathPhoto, GalleryRepository.InsertCallback callback) {
        repository.insert(pathPhoto, callback);
    }

    public void insertPath(Path path, GalleryRepository.InsertCallback callback) {
        repository.insert(path, callback);
    }

    public LiveData<List<PathPhoto>> getPhotosByPath(int pathId) {
        return repository.getPathPhotosByPathId(pathId);
    }
}
