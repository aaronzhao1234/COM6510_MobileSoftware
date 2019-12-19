package uk.ac.shef.oak.com4510.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.LocationTracking;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This fragment is able to show a map with tracking information
 * including journey and photos. It is used in both to show the
 * map of finished path and the active one. The map is updated
 * every time a new relevant entry on the database is inserted.
 */
public class PathMapFragment extends Fragment implements OnMapReadyCallback {

    // Google map handler
    private GoogleMap mMap;

    // view model variables
    private GalleryViewModel galleryViewModel;

    /**
     * Create a new instance of the class
     * @return new instance of the class
     */
    public static PathMapFragment newInstance() {
        return new PathMapFragment();
    }


    /**
     * Get the google map associated with this instance
     * @return the google path
     */
    public GoogleMap getMap() {
        return mMap;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.path_map_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // set map options
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // get photo and path id from the shared preferences
        final int pathId = getArguments().getInt("pathId", -1);
        final int photoId = getArguments().getInt("photoId", -1);

        if (photoId != -1) {
            // populate map based on the path id associated
            // with this fragment
            galleryViewModel.getLocationsByPath(pathId).observe(this, new Observer<List<LocationTracking>>() {
                @Override
                public void onChanged(final List<LocationTracking> locationTrackings) {
                    galleryViewModel.getPhotoById(photoId).observe(PathMapFragment.this, new Observer<PathPhoto>() {
                        @Override
                        public void onChanged(PathPhoto pathPhoto) {
                            mMap.clear();

                            populateWithLocation(locationTrackings);
                            populateMap(Collections.singletonList(pathPhoto));
                        }
                    });
                }
            });
        } else { // in the photo details view only
            // check if location permission is granted
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }

            // update map with tracking locations and
            // show the desired photo with a pin on the map
            galleryViewModel.getLocationsByPath(pathId).observe(this, new Observer<List<LocationTracking>>() {
                @Override
                public void onChanged(final List<LocationTracking> locationTrackings) {
                    galleryViewModel.getPhotosByPath(pathId).observe(PathMapFragment.this, new Observer<List<PathPhoto>>() {
                        @Override
                        public void onChanged(List<PathPhoto> pathPhotos) {
                            mMap.clear();

                            populateWithLocation(locationTrackings);
                            populateMap(pathPhotos);
                        }
                    });
                }
            });
        }
    }

    /**
     * Populate the map with pins on the place where the
     * photo was taken.
     * @param photos list of photos
     */
    private void populateMap(List<PathPhoto> photos) {
        for (PathPhoto photo: photos) {
            // get coordinates from photo
            String[] coordsString = photo.getCoordinates().split(",");
            double lat = Double.parseDouble(coordsString[0]);
            double lng = Double.parseDouble(coordsString[1]);

            // set pin on the map
            LatLng position = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,14.0f));
        }
    }

    /**
     * Populate the map with polyline showing the journey
     * during path tracking.
     * @param locations list of tracking locations
     */
    private ArrayList<LatLng> mPolyPoints = new ArrayList<LatLng>();
    private LatLng position;

    private void populateWithLocation(List<LocationTracking> locations) {
        List<LatLng> points = new ArrayList<>();
        for (LocationTracking location: locations) {
            position = new LatLng(location.getLatitude(), location.getLongitude());
            points.add(position);
        }

        Polyline polyline;
        polyline = mMap.addPolyline(new PolylineOptions().addAll(points));
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(12);
        polyline.setColor(0xff000000);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,14.0f));
    }

}
