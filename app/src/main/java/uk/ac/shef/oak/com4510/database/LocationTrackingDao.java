package uk.ac.shef.oak.com4510.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import uk.ac.shef.oak.com4510.model.LocationTracking;

@Dao
public interface LocationTrackingDao {
    @Insert
    public void addLocation(LocationTracking location);

    @Query("select * from locations")
    public List<LocationTracking> getLocation();

    @Query("select * from locations where path_id = (:id)")
    public LiveData<List<LocationTracking>> getAllByPathId(int id);

    @Delete
    void delete(LocationTracking location);
}
