package uk.ac.shef.oak.com4510.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.concurrent.Executors;

public class PathDetailsActivity extends BaseActivity {

    private PhotosFragment photosFragment;
    private PathMapFragment pathMapFragment;

    private Path path;
    private TabLayout tabLayout;
    private View detailsView;

    private GalleryViewModel galleryViewModel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.path_details_activity);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        detailsView = findViewById(R.id.details);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(tabListener);

        photosFragment = PhotosFragment.newInstance();
        pathMapFragment = PathMapFragment.newInstance();

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
        if (item.getItemId() == R.id.action_delete) {
            int pathId = (int) getSharedPreferences("PathTracking", MODE_PRIVATE).getLong("pathId", -1);
            if (path.getEndTime() == null && pathId == path.getId()) {
                Toast.makeText(this, "Cannot delete actively tracking path.", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }

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

    private void deletePath() {
        galleryViewModel.getPhotosByPath(path.getId()).observe(PathDetailsActivity.this, new Observer<List<PathPhoto>>() {
            @Override
            public void onChanged(final List<PathPhoto> pathPhotos) {
                if (path != null) {
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            for (PathPhoto photo: pathPhotos) galleryViewModel.removePhoto(photo);
                            galleryViewModel.removePath(path);
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void setDetailsFragment(final Fragment fragment) {
        Fragment lastFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (!fragment.equals(lastFragment)) {
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

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
