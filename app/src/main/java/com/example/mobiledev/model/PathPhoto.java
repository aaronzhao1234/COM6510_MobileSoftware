package com.example.mobiledev.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("path_id")},
        foreignKeys = @ForeignKey(entity = Path.class,
        parentColumns = "id",
        childColumns = "path_id"))
public class PathPhoto {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "coordinates")
    private String coordinates;

    @ColumnInfo(name = "temperature")
    private float temperature;

    @ColumnInfo(name = "pressure")
    private float pressure;

    @ColumnInfo(name = "data")
    private byte[] data;

    @ColumnInfo(name = "path_id")
    private int pathId;

    public PathPhoto(String coordinates, float temperature, float pressure, byte[] data, int pathId) {
        this.coordinates = coordinates;
        this.temperature = temperature;
        this.pressure = pressure;
        this.data = data;
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getPathId() {
        return pathId;
    }

    public void setPathId(int pathId) {
        this.pathId = pathId;
    }

}
