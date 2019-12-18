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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;

import uk.ac.shef.oak.com4510.R;


@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationService extends IntentService {

    private static final String CHANNEL_ID = "9876543";
    private static final int FOREGROUND_ID = 123456;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;

    private Location mCurrentLocation;
    private String mLastUpdateTime;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocationService(String name) {
        super(name);
    }

    public LocationService() {
        super("PathTracking");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildChannel(CHANNEL_ID);
        }

        Intent intent = new Intent(this, PathTrackingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_NO_CREATE);

        Notification notification = mBuilder
                .setContentIntent(pendingIntent)
                .build();
        startForeground(FOREGROUND_ID, notification);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        //code running in the service
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /* Looper */);
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mCurrentLocation = locationResult.getLastLocation();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

            // Save location to shared prefs for other activities to access
            SharedPreferences prefs = getSharedPreferences("PathTracking", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("currentLocation", "" +
                    mCurrentLocation.getLatitude() + ',' +
                    mCurrentLocation.getLongitude());
            editor.commit();

            Log.i("MAP", "new location " + mCurrentLocation.toString());
        }
    };

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_local_library_24px)
            .setContentTitle("Path Tracking")
            .setContentText("Path Tracking is running in background...")
            .setPriority(NotificationCompat.PRIORITY_LOW);

    private void buildChannel(String channelId) {
        String channelName = "LocationService";
        NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        assert manager != null;
        manager.createNotificationChannel(chan);
    }

}
