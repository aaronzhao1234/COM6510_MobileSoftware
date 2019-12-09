package com.example.mobiledev.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledev.R;
import com.google.android.material.tabs.TabLayout;

public class PathDetailsActivity extends BaseActivity {

    private PhotosFragment photosFragment;
    private PathMapFragment pathMapFragment;

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.path_details_activity);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });

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

    private void setDetailsFragment(Fragment fragment) {
        Fragment lastFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (!fragment.equals(lastFragment)) {

            if (fragment.getClass().equals(PhotosFragment.class)) {
                toolbar.setTitle(R.string.title_photos);
            } else {
                toolbar.setTitle(R.string.title_paths);
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in_short, R.anim.fade_out_short)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

}
