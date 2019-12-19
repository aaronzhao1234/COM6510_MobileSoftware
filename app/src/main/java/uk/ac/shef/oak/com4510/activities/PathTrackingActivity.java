package uk.ac.shef.oak.com4510.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.Barometer;
import uk.ac.shef.oak.com4510.utils.Thermometer;
import uk.ac.shef.oak.com4510.utils.Utilities;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This is the main activity responsible for initializing, tracking
 * and storing information for the active path. It also handles the sensor
 * readings.
 */
public class PathTrackingActivity extends BaseActivity {


    /**
     * Permission status identifier
     */
    public static final int PERMISSION_STATUS = 1234;

    // sensor tracking helpers
    private Barometer barometer;
    private Thermometer thermometer;

    // view model variables
    private GalleryViewModel galleryViewModel;
    private Path trackingPath;

    private Intent serviceIntent;
    private PathMapFragment pathMapFragment;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_tracking_activity);

        checkPermission();

        // return if path id is not defined in shared preferences
        final int id = (int) getSharedPreferences("PathTracking", MODE_PRIVATE).getLong("pathId", -1);
        if (id == -1) {
            finish();
            return;
        }

        // initialize view model and retrieve path tracking
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        galleryViewModel.getPathById(id).observe(this, new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> paths) {
                if (paths.size() > 0) {
                    trackingPath = paths.get(0);
                }
            }
        });

        // initialize and start barometer recordings
        barometer = new Barometer(this);
        barometer.startSensingPressure();

        // initialize and start thermometer recordings
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

        // initialize path map fragment
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
        // return to home activity when back button pressed
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restart sensors
        barometer.startSensingPressure();
        thermometer.startSensingTempertaure();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // pause sensors
        barometer.stopBarometer();
        thermometer.stopThermometer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // stop sensors
        barometer.stopBarometer();
        thermometer.stopThermometer();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void checkPermission() {
        // check and request essential permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {

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
        // initialize ans start foreground service for
        // location tracking
        serviceIntent = new Intent(context, LocationService.class);
        context.startForegroundService(serviceIntent);

        // start timer
        startTimer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handle photo capturing and retrieval from gallery
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
                // Cancel handling
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
                    // permission was granted
                    startLocationUpdates(getApplicationContext());
                    if (pathMapFragment.getMap() != null) {
                        pathMapFragment.getMap().setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, stop path tracking
                    finishTrackingPath();

                    // delete tracked path
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            galleryViewModel.removePath(trackingPath);
                        }
                    });

                    // show reason for stopping
                    Toast.makeText(this, "Unable to create path without permissions.", Toast.LENGTH_SHORT).show();

                    // return to previous activity
                    finish();
                }
            }
        }
    }

    /**
     * Set listeners for user buttons
     */
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
     * Handle photo captured or received from the gallery
     * @param returnedPhotos
     */
    private void onPhotosReturned(List<File> returnedPhotos) throws IOException {
        int id = 0;
        for (File photoFile: returnedPhotos) {
            // create image file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_" + id;
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = new File(storageDir, imageFileName + ".jpg");

            // copy content of selected image to the final image destination
            copyFile(photoFile, image);

            // create thumbnail
            File thumbnailFile = new File(storageDir, imageFileName + "_thumb.jpg");
            FileOutputStream out = new FileOutputStream(thumbnailFile);
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(image.getAbsolutePath()), 256, 256);
            ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance

            // store the image to the database
            storeImage(image);
            id++;
        }

        if (returnedPhotos.size() > 1) {
            Toast.makeText(this, "Photos added to path.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Photo added to path.", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFile(File file1, File file2) throws IOException {
        FileInputStream is = new FileInputStream(file1);
        FileOutputStream  os = new FileOutputStream(file2);
        FileChannel inChannel = is.getChannel();
        FileChannel outChannel = os.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        is.close();
        os.close();
    }

    /**
     * Initialize {@link EasyImage}
     */
    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }

    /**
     * Finish tracking and update path with end tine.
     * Stop the tracking service.
     */
    private void finishTrackingPath() {
        trackingPath.setEndTime(new Date());
        galleryViewModel.insertPath(trackingPath, null);
        stopService(PathTrackingActivity.this.serviceIntent);
    }

    /**
     * Store the image to the database using view model
     * @param image image to be stored
     */
    private void storeImage(File image) {
        SharedPreferences prefs = getSharedPreferences("PathTracking", MODE_PRIVATE);

        // initialize photo model with data
        PathPhoto photo = new PathPhoto(
                prefs.getString("currentLocation", ""),
                thermometer.getmTemperatureValue(),
                barometer.getmPressureValue(),
                image.toURI().toString(),
                trackingPath.getId()
        );

        // insert into database and show prompt of finish
        galleryViewModel.insertPhoto(photo, null);
    }

    /**
     * Show timer for the tracking updating every second.
     */
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
