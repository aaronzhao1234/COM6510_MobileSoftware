package uk.ac.shef.oak.com4510.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.database.AppDatabase;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends BaseActivity {

    private PhotosFragment photosFragment;
    private PathListFragment pathListFragment;
    private static final int PERMISSION_STATUS = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.home_content_main);

        photosFragment = PhotosFragment.newInstance();
        pathListFragment = PathListFragment.newInstance();

        toolbar.setTitle(R.string.title_photos);

        // initialize floating add button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Make the button do something
                Intent intent = new Intent(HomeActivity.this, CreatePathActivity.class);
                startActivity(intent);
            }
        });

        Button btn = findViewById(R.id.continueTracking);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PathTrackingActivity.class);
                startActivity(intent);
            }
        });

        // initialize bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // set initial gallery fragment to activity
        setGalleryFragment(photosFragment);

        checkPermission();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();

        FloatingActionButton fab = findViewById(R.id.fab);
        Button btn = findViewById(R.id.continueTracking);

        if (isLocationServiceRunnint()) {
            btn.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // TODO: Make the button do something

            return true;
        } else if (id == R.id.action_view_mode) {
            if (pathListFragment.adapter.isCollapsed()) {
                pathListFragment.adapter.setCollapsed(false);
                item.setIcon(R.drawable.ic_view_list_24px);
            } else {
                pathListFragment.adapter.setCollapsed(true);
                item.setIcon(R.drawable.ic_photo_size_select_large_24px);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_photos:
                    setGalleryFragment(photosFragment);
                    return true;
                case R.id.navigation_paths:
                    setGalleryFragment(pathListFragment);
                    return true;
            }
            return false;
        }
    };

    private void setGalleryFragment(Fragment fragment) {
        Fragment lastFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (!fragment.equals(lastFragment)) {
            MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_view_mode);

            if (fragment.getClass().equals(PhotosFragment.class)) {
                if (menuItem != null) {
                    menuItem.setVisible(false);
                }
                toolbar.setTitle(R.string.title_photos);
            } else {
                if (menuItem != null) {
                    menuItem.setVisible(true);
                }
                toolbar.setTitle(R.string.title_paths);
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            getIntent().putExtra("pathId", -1);
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in_short, R.anim.fade_out_short)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            ((RecyclerView) fragment.getView().findViewById(R.id.gallery_recycler))
                    .scrollToPosition(0);
        }
    }

    private boolean isLocationServiceRunnint() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(LocationService.class.getName())) {
                return true;
            }
        }
        return false;
    }

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.FOREGROUND_SERVICE}, PERMISSION_STATUS);
            }
            return;
        }
    }

}
