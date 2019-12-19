package uk.ac.shef.oak.com4510.activities;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.shef.oak.com4510.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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


        getSupportActionBar().setDisplayShowHomeEnabled(false);
        // set initial gallery fragment to activity
        setGalleryFragment(photosFragment);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onResume() {
        super.onResume();

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
            MenuItem viewMode = toolbar.getMenu().findItem(R.id.action_view_mode);
            MenuItem search = toolbar.getMenu().findItem(R.id.action_search);

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

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in_short, R.anim.fade_out_short)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } else {
            ((RecyclerView) fragment.getView().findViewById(R.id.gallery_recycler))
                    .scrollToPosition(0);
        }
    }

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
