package uk.ac.shef.oak.com4510.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.database.AppDatabase;

/**
 * This activity class is the main activity. It is used to
 * handle the transitions between gallery view modes (i.e.
 * grid or path list).
 */
public class HomeActivity extends BaseActivity {

    /**
     * The database of the app
     */
    public static AppDatabase appDatabase;

    // fragments visible in the activity
    private PhotosFragment photosFragment;
    private PathListFragment pathListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.home_content_main);

        // initialize photos and path list fragments
        photosFragment = PhotosFragment.newInstance();
        pathListFragment = PathListFragment.newInstance();

        // set the title of the activity
        toolbar.setTitle(R.string.title_photos);

        initializeButtons();

        // initialize bottom navigation
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // disable home icon
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        // set initial gallery fragment to activity
        setGalleryFragment(photosFragment);

        // initialize database
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"app_db").build();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();

        // update buttons based on the tracking status
        FloatingActionButton fab = findViewById(R.id.fab);
        Button btn = findViewById(R.id.continueTracking);

        if (isLocationServiceRunning()) {
            btn.setVisibility(View.VISIBLE);
            fab.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // initialize input listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                pathListFragment.search(query);
                return true;
            }
        });

        menu.findItem(R.id.action_search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                pathListFragment.search("");
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        if (item.getItemId() == R.id.action_view_mode) {
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

    /**
     * Add and resume path button initializer
     */
    private void initializeButtons() {
        // initialize floating add button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // redirect to create path activity
                Intent intent = new Intent(HomeActivity.this, CreatePathActivity.class);
                startActivity(intent);
            }
        });

        // initialize continue tracking button
        Button btn = findViewById(R.id.continueTracking);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PathTrackingActivity.class);
                startActivity(intent);
            }
        });
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
        // get visible fragment
        Fragment lastFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        if (!fragment.equals(lastFragment)) {
            MenuItem viewMode = toolbar.getMenu().findItem(R.id.action_view_mode);
            MenuItem search = toolbar.getMenu().findItem(R.id.action_search);

            // update toolbar based on the visible fragment
            if (fragment.getClass().equals(PhotosFragment.class)) {
                if (viewMode != null) viewMode.setVisible(false);
                if (search != null) search.setVisible(false);

                toolbar.setTitle(R.string.title_photos);
            } else {
                if (viewMode != null) viewMode.setVisible(true);
                if (search != null) search.setVisible(true);

                toolbar.setTitle(R.string.title_paths);
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            getIntent().putExtra("pathId", -1);
            fragment.setArguments(getIntent().getExtras());

            // begin fragment transaction
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in_short, R.anim.fade_out_short)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            // scroll to top if photo tab pressed when the photo
            // fragment is active
            ((RecyclerView) fragment.getView().findViewById(R.id.gallery_recycler))
                    .scrollToPosition(0);
        }
    }

    /**
     * Check if the service responsible for handling location
     * tracking is enabled.
     * @return whether the service is active or not
     */
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals(LocationService.class.getName())) {
                return true;
            }
        }

        return false;
    }

}
