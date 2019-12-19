package uk.ac.shef.oak.com4510.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.concurrent.Executors;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.LocationTracking;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This activity handles the two details modes of the path
 * i.e. gallery view and map view. It also handles deletion of
 * path if requested by user.
 */
public class PathDetailsActivity extends BaseActivity {

    // fragments visible in the activity
    private PhotosFragment photosFragment;
    private PathMapFragment pathMapFragment;

    // details view showing the description of the path
    private View detailsView;

    // view model variables
    private Path path;
    private GalleryViewModel galleryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.path_details_activity);

        detailsView = findViewById(R.id.details);

        // initialize view model
        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        // initialize tabs listener
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(tabListener);

        // initialize fragments of the activity
        photosFragment = PhotosFragment.newInstance();
        pathMapFragment = PathMapFragment.newInstance();

        // get path by path id
        int pathId = getIntent().getExtras().getInt("pathId", -1);
        galleryViewModel.getPathById(pathId).observe(this, new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> paths) {
                if (paths.size() > 0) {
                    path = paths.get(0);
                    setDetailsView();
                }
            }
        });

        // set title to toolbar
        toolbar.setTitle(R.string.path_details);

        // Set back button
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // set initial gallery fragment to activity
        setDetailsFragment(photosFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.action_delete);
        if (menuItem != null) {
            menuItem.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle delete path action
        if (item.getItemId() == R.id.action_delete) {
            // disable access to delete when the requested
            // path is the currently being tracked
            int pathId = (int) getSharedPreferences("PathTracking", MODE_PRIVATE).getLong("pathId", -1);
            if (path.getEndTime() == null && pathId == path.getId()) {
                Toast.makeText(this, "Cannot delete actively tracking path.", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }

            // prompt dialog before delete action
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete path \"" + path.getTitle() + "\"?")
                    .setTitle("Delete path")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePath();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Tab listener responsible for switching between
     * fragments.
     */
    private TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getPosition() == 0) {
                setDetailsFragment(photosFragment);
            } else {
                setDetailsFragment(pathMapFragment);
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    /**
     * Method to delete the current path and return to previous
     * activity.
     */
    private void deletePath() {
        Toast.makeText(PathDetailsActivity.this, "Path removed successfully.", Toast.LENGTH_SHORT).show();
        galleryViewModel.getPhotosByPath(path.getId()).observe(PathDetailsActivity.this, new Observer<List<PathPhoto>>() {
            @Override
            public void onChanged(final List<PathPhoto> pathPhotos) {
                galleryViewModel.getLocationsByPath(path.getId()).observe(PathDetailsActivity.this, new Observer<List<LocationTracking>>() {
                    @Override
                    public void onChanged(final List<LocationTracking> locations) {
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                // delete all locations and photos associated with the path
                                for (LocationTracking location: locations) galleryViewModel.removeLocation(location);
                                for (PathPhoto photo: pathPhotos) galleryViewModel.removePhoto(photo);

                                // delete the path
                                galleryViewModel.removePath(path);

                                // return to previous activity
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Display a new fragment
     * @param fragment the fragment to display
     */
    private void setDetailsFragment(final Fragment fragment) {
        // get current fragment
        Fragment lastFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (!fragment.equals(lastFragment)) {
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

            // begin transaction to change the fragment
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in_short, R.anim.fade_out_short)
                    .replace(R.id.fragment_container, fragment)
                    .runOnCommit(new Runnable() {
                        @Override
                        public void run() {
                            if (fragment.getClass().equals(PhotosFragment.class)) {
                                setDetailsView();
                            } else {
                                detailsView.setVisibility(View.GONE);
                            }
                        }
                    })
                    .commit();
        }
    }

    /**
     * Set the details view to the path
     */
    private void setDetailsView() {
        if (path != null) {
            TextView description = findViewById(R.id.description);
            description.setText(path.getDescription().length() > 0 ? path.getDescription() : "No description available");

            assert getSupportActionBar() != null;
            getSupportActionBar().setTitle(path.getTitle());
            detailsView.setVisibility(View.VISIBLE);
        }
    }

}
