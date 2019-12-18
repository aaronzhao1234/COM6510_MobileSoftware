package uk.ac.shef.oak.com4510.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class PathDetailsActivity extends BaseActivity {

    private PhotosFragment photosFragment;
    private PathMapFragment pathMapFragment;

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
        int pathId = getIntent().getExtras().getInt("pathId", -1);

        galleryViewModel.getPathById(pathId).observe(this, new Observer<List<Path>>() {
            @Override
            public void onChanged(List<Path> paths) {
                Path path = paths.get(0);

                TextView description = findViewById(R.id.description);
                description.setText(path.getDescription().length() > 0 ? path.getDescription() : "No description available");

                assert getSupportActionBar() != null;
                getSupportActionBar().setTitle(path.getTitle());

                detailsView.setVisibility(View.VISIBLE);
            }
        });
    }

}
