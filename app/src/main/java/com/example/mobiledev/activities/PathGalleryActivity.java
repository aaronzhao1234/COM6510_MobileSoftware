package com.example.mobiledev.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledev.R;

public class PathGalleryActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.path_gallery_activity);

        PhotosFragment photosFragment = PhotosFragment.newInstance();
        photosFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, photosFragment)
                .commit();
    }

}
