package uk.ac.shef.oak.com4510.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import uk.ac.shef.oak.com4510.R;

/**
 * This is a base activity used to build the toolbar and
 * enable activities which inherit it to place content
 * without adding new toolbar every time
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * Container layout to place content
     */
    protected FrameLayout baseLayout;


    /**
     * The main toolbar of the app visible on all activities
     */
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.base_layout);

        baseLayout = findViewById(R.id.base_layout);

        // initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void setContentView(int id) {
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(id, baseLayout);
    }

}
