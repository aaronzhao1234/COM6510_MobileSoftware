package uk.ac.shef.oak.com4510.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * This is a model responsible for storing information relating
 * to location tracking (GPS data).
 */
@Entity(tableName = "locations",
        indices = {@Index("path_id")},
        foreignKeys = @ForeignKey(entity = Path.class,
                parentColumns = "id",
                childColumns = "path_id"))
public class LocationTracking {

    @PrimaryKey(autoGenerate = true) @NonNull
    private int locationID;

    private String pathName;

    private String time;

    private double latitude;

    private double longitude;

    @ColumnInfo(name = "path_id")
    private int pathId;

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPathId() {
        return pathId;
    }

    public void setPathId(int pathId) {
        this.pathId = pathId;
    }
}
