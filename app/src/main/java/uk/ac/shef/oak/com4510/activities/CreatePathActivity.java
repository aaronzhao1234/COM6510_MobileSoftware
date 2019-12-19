package uk.ac.shef.oak.com4510.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProviders;

import java.util.Date;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.viewmodel.GalleryRepository;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This activity handles the creation of paths with
 * associated fields and initializes the process of
 * tracking
 */
public class CreatePathActivity extends BaseActivity {

    private GalleryViewModel galleryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_path_activity);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        final EditText titleInput = findViewById(R.id.titleInput);
        final Button button = findViewById(R.id.button);

        titleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = titleInput.getText().toString().trim();
                button.setEnabled(!title.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText descriptionInput = findViewById(R.id.descriptionInput);

                Path newPath = new Path(
                        titleInput.getText().toString(),
                        descriptionInput.getText().toString(),
                        new Date(), null
                );

                galleryViewModel.insertPath(newPath, new GalleryRepository.InsertCallback() {
                    @Override
                    public void call(long id) {
                        Intent intent = new Intent(CreatePathActivity.this, PathTrackingActivity.class);
                        storeTrackingPathId(id);
                        startActivity(intent);
                    }
                });
            }
        });

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Create new path");

        // Set back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private void storeTrackingPathId(long id) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("PathTracking", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong("pathId", id);
        editor.commit();
    }

}
