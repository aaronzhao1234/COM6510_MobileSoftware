package uk.ac.shef.oak.com4510.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.Barometer;
import uk.ac.shef.oak.com4510.utils.Thermometer;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

public class PathTrackingActivity extends BaseActivity implements OnMapReadyCallback {
    private Barometer barometer;
    private Thermometer thermometer;

    private static GoogleMap mMap;

    private Button stopButton;

    private GalleryViewModel galleryViewModel;
    private Path trackingPath;

    private Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.path_tracking_activity);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PathTracking", MODE_PRIVATE);
        int id = (int) prefs.getLong("pathId", -1);

        if (id == -1) {
            finish();
        } else {
            galleryViewModel.getPathById(id).observe(this, new Observer<List<Path>>() {
                @Override
                public void onChanged(List<Path> paths) {
                    trackingPath = paths.get(0);
                }
            });
        }

        stopButton = (Button) findViewById(R.id.StopButton_id);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishTrackingPath();

                Intent intent= new Intent(PathTrackingActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        barometer = new Barometer(this);
        barometer.startSensingPressure();

        thermometer = new Thermometer(this);
        thermometer.startSensingTempertaure();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initEasyImage();

        startLocationUpdates(getApplicationContext());

        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fab_camera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openCamera(PathTrackingActivity.this, 0);
            }
        });

        FloatingActionButton fabGallery = (FloatingActionButton) findViewById(R.id.fab_gallery);
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EasyImage.openGallery(PathTrackingActivity.this, 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barometer.stopBarometer();
        thermometer.stopThermometer();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startLocationUpdates(Context context) {
        intent = new Intent(context, LocationService.class);
        context.startForegroundService(intent);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        boolean permission =  ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        mMap.setMyLocationEnabled(permission);
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

/*    @SuppressLint("MissingPermission")
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

                } else {
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }*/

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

    /**
     * given a list of photos, it creates a list of myElements
     * @param //returnedPhotos
     * @return
     */
/*    private List<ImageElement> getImageElements(List<File> returnedPhotos) {
        List<ImageElement> imageElementList= new ArrayList<>();
        for (File file: returnedPhotos){
            ImageElement element= new ImageElement(file);
            imageElementList.add(element);
        }
        return imageElementList;
    }*/

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
    }

}
