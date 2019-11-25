package com.example.mobiledev.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.MenuItem;

import com.example.mobiledev.R;

public class HomeActivity extends BaseActivity {

    private PhotosFragment photosFragment;
    private PathListFragment pathListFragment;

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
            }
        });

        // initialize bottom navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // set initial gallery fragment to activity
        setGalleryFragment(photosFragment);
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
