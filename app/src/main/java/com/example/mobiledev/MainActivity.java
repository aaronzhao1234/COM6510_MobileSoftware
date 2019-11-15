package com.example.mobiledev;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private RecyclerView gallery;
    private RecyclerView.Adapter galleryAdapter;
    private GridLayoutManager layoutManager;
    private DisplayMetrics displayMetrics;

    private double widthInches;
    private double heightInches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        gallery = findViewById(R.id.gallery);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        gallery.setHasFixedSize(true);

        // use a grid layout manager
        layoutManager = new GalleryLayoutManager(this, 4);
        gallery.setLayoutManager(layoutManager);

        galleryAdapter = new GalleryAdapter(new int[] {R.drawable.image, R.drawable.image2, R.drawable.image, R.drawable.image2, R.drawable.image2});
        gallery.setAdapter(galleryAdapter);

        gallery.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        updateLayoutDirection(newConfig);
    }

    private void updateLayoutDirection(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("debug", Float.toString(((float) displayMetrics.heightPixels) / displayMetrics.ydpi));
            layoutManager.setSpanCount((int) (1.7f * ((float) displayMetrics.heightPixels) / displayMetrics.ydpi));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("debug", Float.toString(((float) displayMetrics.widthPixels) / displayMetrics.xdpi));
            layoutManager.setSpanCount((int) (1.7f * ((float) displayMetrics.widthPixels) / displayMetrics.xdpi));
        }
    }
}
