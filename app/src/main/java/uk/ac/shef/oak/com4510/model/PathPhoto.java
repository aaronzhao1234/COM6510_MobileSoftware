package uk.ac.shef.oak.com4510.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import uk.ac.shef.oak.com4510.utils.EntityInterface;

/**
 * This is a model responsible for storing the photo details
 */
@Entity(indices = {@Index("path_id")},
        foreignKeys = @ForeignKey(entity = Path.class,
        parentColumns = "id",
        childColumns = "path_id"))

public class PathPhoto implements EntityInterface {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "coordinates")
    private String coordinates;

    @ColumnInfo(name = "temperature")
    private float temperature;

    @ColumnInfo(name = "pressure")
    private float pressure;

    @ColumnInfo(name = "photo_path")
    private String photoPath;

    @ColumnInfo(name = "path_id")
    private int pathId;

    @ColumnInfo(name = "time")
    private Date date;

    public PathPhoto(String coordinates, float temperature, float pressure, String photoPath, int pathId) {
        this.coordinates = coordinates;
        this.temperature = temperature;
        this.pressure = pressure;
        this.photoPath = photoPath;
        this.pathId = pathId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public int getPathId() {
        return pathId;
    }

    public void setPathId(int pathId) {
        this.pathId = pathId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
