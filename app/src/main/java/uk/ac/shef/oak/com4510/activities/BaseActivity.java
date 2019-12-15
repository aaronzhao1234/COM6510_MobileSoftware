package uk.ac.shef.oak.com4510.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import uk.ac.shef.oak.com4510.R;

public class BaseActivity extends AppCompatActivity {

    protected FrameLayout baseLayout;
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
