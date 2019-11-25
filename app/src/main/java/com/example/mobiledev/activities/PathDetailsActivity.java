package com.example.mobiledev.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledev.R;

public class PathDetailsActivity extends BaseActivity {

    private PhotosFragment photosFragment;
    private PathMapFragment pathMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.path_details_activity);

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

}
