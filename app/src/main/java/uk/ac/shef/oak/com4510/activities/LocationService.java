package uk.ac.shef.oak.com4510.activities;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;


public class LocationService extends Service {
    
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    public LocationService() {
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //code running in the service
                if (LocationResult.hasResult(intent)) {
                    LocationResult locResults = LocationResult.extractResult(intent);
                    if (locResults != null) {
                        for (Location location : locResults.getLocations()) {
                            if (location == null) continue;
                            //do something with the location
                            mCurrentLocation = location;
                            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                            Log.i("MAP_bg", "new location " + mCurrentLocation.toString());

                            SharedPreferences prefs = getSharedPreferences("PathTracking", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("currentLocation", mCurrentLocation.toString());
                            editor.commit();

                            // check if the activity has not been closed in the meantime
                            if (PathTrackingActivity.getLocationActivity()!=null)
                                // any modification of the user interface must be done on the UI Thread. The Intent Service is running
                                // in its own thread, so it cannot communicate with the UI.
                                PathTrackingActivity.getLocationActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            if (PathTrackingActivity.getMap() != null)
                                                PathTrackingActivity.getMap().addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))
                                                        .title(mLastUpdateTime));
                                            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                                            // it centres the camera around the new location
                                            //PathTrackingActivity.getMap().moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())));
                                            // it moves the camera to the selected zoom
                                            //PathTrackingActivity.getMap().animateCamera(zoom);
                                        } catch (Exception e ){
                                            Log.e("LocationService", "Error cannot write on map "+e.getMessage());
                                        }
                                    }
                                });
                        }
                    }

                }

            }
        };

        Thread locationThread = new Thread(runnable);
        locationThread.start();
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
