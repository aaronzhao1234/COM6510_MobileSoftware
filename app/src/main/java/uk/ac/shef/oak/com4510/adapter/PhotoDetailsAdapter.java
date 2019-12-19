package uk.ac.shef.oak.com4510.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import uk.ac.shef.oak.com4510.activities.PhotoDetailsFragment;
import uk.ac.shef.oak.com4510.model.PathPhoto;

/**
 * This adapter handles the recycler view of the photo details activity.
 * It is responsible for properly displaying the photo details views
 * in scrollable pages.
 */
public class PhotoDetailsAdapter extends FragmentPagerAdapter {

    private List<PathPhoto> photoList;

    public PhotoDetailsAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new PhotoDetailsFragment(photoList.get(position));
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    public void setPhotoList(List<PathPhoto> photoList) {
        this.photoList = photoList;
    }

}
