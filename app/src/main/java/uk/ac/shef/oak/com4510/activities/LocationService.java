package uk.ac.shef.oak.com4510.activities;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.LocationTracking;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This is a foreground service that keeps track of the
 * current active path and updates it with gps data every
 * few seconds. It works in background, meaning that the app does
 * not need to be open once path tracking has started. A notification
 * is displayed to the user while the service is active.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationService extends IntentService {

    // location service ids
    private static final String CHANNEL_ID = "9876543";
    private static final int FOREGROUND_ID = 123456;

    // location handlers
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    // gallery view model linked to the database
    private GalleryViewModel galleryViewModel;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocationService(String name) {
        super(name);
    }

    /**
     * Required constructor for service instantiation
     */
    public LocationService() {
        this("PathTracking");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // registering channel required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildChannel(CHANNEL_ID);
        }

        // initialize pending intent to handle clicks on the notification
        Intent intent = new Intent(this, PathTrackingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // build notification and run service in backgorund
        Notification notification = mBuilder
                .setContentIntent(pendingIntent)
                .build();
        startForeground(FOREGROUND_ID, notification);

        // initialize location handlers
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(15000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // initialize gallery view model
        galleryViewModel = new GalleryViewModel(getApplication());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // start location tracking
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        // stop location tracking
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    /**
     * Location callback handler that is triggered when
     * new location is available.
     */
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            // get fps data
            Location mCurrentLocation = locationResult.getLastLocation();
            String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            // Save location to shared prefs for other activities to access
            SharedPreferences prefs = getSharedPreferences("PathTracking", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("currentLocation", "" +
                    mCurrentLocation.getLatitude() + ',' +
                    mCurrentLocation.getLongitude());
            editor.commit();

            Log.i("MAP", "new location " + mCurrentLocation.toString());

            // store location to the database
            storeLocation(mCurrentLocation, mLastUpdateTime);
        }
    };

    // initialize notification builder and assign options
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_local_library_24px)
            .setContentTitle("Path Tracking")
            .setContentText("Path Tracking is running in background...")
            .setPriority(NotificationCompat.PRIORITY_LOW);

    /**
     * Required foreground service initialization step for
     * Android ) and above.
     * @param channelId channel id
     */
    private void buildChannel(String channelId) {
        String channelName = "LocationService";
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        assert manager != null;
        manager.createNotificationChannel(chan);
    }

    /**
     * Store the location into the database using the
     * the gallery view model
     * @param location location to be stored
     * @param date date for the moment of tracking
     */
    private void storeLocation(final Location location, final String date) {
        final LocationTracking locationTracking = new LocationTracking();

        // set fields to location entry
        locationTracking.setTime(date);
        locationTracking.setLatitude(location.getLatitude());
        locationTracking.setLongitude(location.getLongitude());

        // get active path id
        int pathId = (int) getSharedPreferences("PathTracking", MODE_PRIVATE).getLong("pathId", -1);
        locationTracking.setPathId(pathId);

        // store action to database
        galleryViewModel.insertLocationTracking(locationTracking);
    }

}
