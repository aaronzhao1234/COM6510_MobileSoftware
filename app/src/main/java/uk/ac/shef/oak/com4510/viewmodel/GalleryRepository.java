package uk.ac.shef.oak.com4510.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import uk.ac.shef.oak.com4510.database.AppDatabase;
import uk.ac.shef.oak.com4510.database.PathDao;
import uk.ac.shef.oak.com4510.database.PathPhotoDao;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.DaoInterface;
import uk.ac.shef.oak.com4510.utils.EntityInterface;

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

    public LiveData<List<Path>> getPathById(int id) {
        return pathDao.getById(id);
    }

    public LiveData<List<PathPhoto>> getAllPathPhotos() {
        return allPathPhotos;
    }

    public void insert(final PathPhoto pathPhoto, InsertCallback callback) {
        new InsertTaskAsync(pathPhotoDao, callback).execute((EntityInterface) pathPhoto);
    }

    public void insert(final Path path, InsertCallback callback) {
        new InsertTaskAsync(pathDao, callback).execute((EntityInterface) path);
    }

    public LiveData<List<PathPhoto>> getPathPhotosByPathId(int pathId) {
        return pathPhotoDao.getAllByPathId(pathId);
    }

    private static class InsertTaskAsync extends AsyncTask<EntityInterface, Void, Long> {

        private InsertCallback callback;
        private DaoInterface dao;

        public InsertTaskAsync(DaoInterface dao, InsertCallback callback) {
            this.dao = dao;
            this.callback = callback;
        }

        @Override
        protected Long doInBackground(EntityInterface... entities) {
            long id = -1;
            for (EntityInterface entry: entities) {
                if (entry instanceof PathPhoto) {
                    id = ((PathPhotoDao) dao).insertAll((PathPhoto) entry)[0];
                } else if (entry instanceof Path) {
                    id = ((PathDao) dao).insertAll((Path) entry)[0];
                }
            }

            return id;
        }

        @Override
        protected void onPostExecute(Long id) {
            if (callback != null) callback.call(id);
        }

    }

    public abstract static class InsertCallback {
        public abstract void call(long id);
    }

}
