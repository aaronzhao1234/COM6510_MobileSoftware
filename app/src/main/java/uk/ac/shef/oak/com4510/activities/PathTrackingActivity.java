package uk.ac.shef.oak.com4510.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.LocationTracking;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.Barometer;
import uk.ac.shef.oak.com4510.utils.Thermometer;
import uk.ac.shef.oak.com4510.utils.Utilities;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

public class PathTrackingActivity extends BaseActivity {

    public static final int PERMISSION_STATUS = 1234;

    private Barometer barometer;
    private Thermometer thermometer;

    private GalleryViewModel galleryViewModel;
    private Path trackingPath;

    private Intent intent;
    private PathMapFragment pathMapFragment;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_tracking_activity);

        checkPermission();

        final int id = (int) getSharedPreferences("PathTracking", MODE_PRIVATE).getLong("pathId", -1);
        if (id == -1) {
            finish();
            return;
        }

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        galleryViewModel.getPathById(id).observe(this, new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> paths) {
                if (paths.size() > 0) {
                    trackingPath = paths.get(0);
                }
            }
        });

        barometer = new Barometer(this);
        barometer.startSensingPressure();

        thermometer = new Thermometer(this);
        thermometer.startSensingTempertaure();

        initEasyImage();

        startLocationUpdates(getApplicationContext());
        setButtonListeners();

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Path Tracking");

        // Set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pathMapFragment = PathMapFragment.newInstance();
        Bundle args = new Bundle();
        args.putInt("pathId", id);
        pathMapFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in_short, R.anim.fade_out_short)
                .replace(R.id.fragment_container, pathMapFragment)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        barometer.startSensingPressure();
        thermometer.startSensingTempertaure();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barometer.stopBarometer();
        thermometer.stopThermometer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barometer.stopBarometer();
        thermometer.stopThermometer();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.FOREGROUND_SERVICE}, PERMISSION_STATUS);
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startLocationUpdates(Context context) {
        intent = new Intent(context, LocationService.class);
        context.startForegroundService(intent);
        startTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                try {
                    onPhotosReturned(imageFiles);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(PathTrackingActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STATUS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startLocationUpdates(getApplicationContext());
                    if (pathMapFragment.getmMap() != null) {
                        pathMapFragment.getmMap().setMyLocationEnabled(true);
                    }
                } else {
                    finishTrackingPath();

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            galleryViewModel.removePath(trackingPath);
                        }
                    });

                    Toast.makeText(this, "Unable to create path without permissions.", Toast.LENGTH_SHORT).show();

                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void setButtonListeners() {
        FloatingActionButton fabCamera = findViewById(R.id.fab_camera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openCamera(PathTrackingActivity.this, 0);
            }
        });

        FloatingActionButton fabGallery = findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(PathTrackingActivity.this, 0);
            }
        });

        Button stopButton = findViewById(R.id.StopButton_id);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTrackingPath();

                Intent intent= new Intent(PathTrackingActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    /**
     * add to the grid
     * @param returnedPhotos
     */
    private void onPhotosReturned(List<File> returnedPhotos) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");

        FileInputStream is = new FileInputStream(returnedPhotos.get(0));
        FileOutputStream  os = new FileOutputStream(image);
        FileChannel inChannel = is.getChannel();
        FileChannel outChannel = os.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        is.close();
        os.close();

        storeImage(image);

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(image);
        intent.setData(contentUri);
        this.sendBroadcast(intent);
    }

    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }

    private void finishTrackingPath() {
        trackingPath.setEndTime(new Date());
        galleryViewModel.insertPath(trackingPath, null);
        stopService(PathTrackingActivity.this.intent);
    }

    private void storeImage(File image) {
        SharedPreferences prefs = getSharedPreferences("PathTracking", MODE_PRIVATE);

        PathPhoto photo = new PathPhoto(
                prefs.getString("currentLocation", ""),
                thermometer.getmTemperatureValue(),
                barometer.getmPressureValue(),
                image.toURI().toString(),
                trackingPath.getId()
        );

        galleryViewModel.insertPhoto(photo, null);
        Toast.makeText(this, "Photo added to path.", Toast.LENGTH_SHORT).show();
    }

    private void startTimer() {
        final Handler h = new Handler();
        final TextView timerText = findViewById(R.id.timer);

        h.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                long timeElapsed = new Date().getTime() - trackingPath.getStartTime().getTime();
                timerText.setText(Utilities.mSecsToString(timeElapsed));
                h.postDelayed(this, 1000);
            }
        }, 1000);
    }

}
