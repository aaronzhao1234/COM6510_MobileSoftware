package uk.ac.shef.oak.com4510.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.adapter.PhotoDetailsAdapter;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This activity handles the display of photo details including
 * fields and position on the map. It uses an viewpager adapter
 * which let the user browse by swiping left and rignht through
 * the photos.
 */
public class PhotoDetailsActivity extends BaseActivity {

    //---------------viewPager_declaration---------------------------------------------
    private ViewPager viewPager;
    private PhotoDetailsAdapter adapter;

    private GalleryViewModel galleryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_details_activity);

        //---------------------ViewPager------------------------------------------
        viewPager = (ViewPager) findViewById(R.id.viewPager_id);

        galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);

        int pathId = getIntent().getExtras().getInt("pathId", -1);
        if (pathId == -1) {
            galleryViewModel.getAllPathPhotos().observe(this, new Observer<List<PathPhoto>>() {
                @Override
                public void onChanged(@Nullable List<PathPhoto> pathPhotos) {
                    adapter = new PhotoDetailsAdapter(getSupportFragmentManager());
                    adapter.setPhotoList(pathPhotos);
                    viewPager.setAdapter(adapter);
                    viewPager.setCurrentItem(getIntent().getIntExtra("startPosition", 0));
                }
            });
        } else {
            galleryViewModel.getPhotosByPath(pathId).observe(this, new Observer<List<PathPhoto>>() {
                @Override
                public void onChanged(@Nullable List<PathPhoto> pathPhotos) {
                    adapter = new PhotoDetailsAdapter(getSupportFragmentManager());
                    adapter.setPhotoList(pathPhotos);
                    viewPager.setAdapter(adapter);
                    viewPager.setCurrentItem(getIntent().getIntExtra("startPosition", 0));
                }
            });
        }

        // Set back button
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
