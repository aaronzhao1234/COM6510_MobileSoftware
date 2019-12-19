package uk.ac.shef.oak.com4510.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.adapter.PhotosAdapter;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.GalleryLayoutManager;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This is a fragment responsible with displaying the photos in either
 * grid or scrollable horizontal list.
 */
public class PhotosFragment extends Fragment {

    // view model variables
    private GalleryViewModel galleryViewModel;

    // RecyclerView variables
    private RecyclerView photosRecycler;
    private PhotosAdapter adapter;
    private GridLayoutManager layoutManager;
    private DisplayMetrics displayMetrics;

    public static PhotosFragment newInstance() {
        return new PhotosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.photos_fragment, container, false);

        // initialize recycler view
        photosRecycler = v.findViewById(R.id.gallery_recycler);

        // use a grid layout manager
        layoutManager = new GalleryLayoutManager(getContext(), 4);
        photosRecycler.setLayoutManager(layoutManager);

        // improve performance of recycler view
        photosRecycler.setHasFixedSize(true);
        photosRecycler.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initialize display metrics
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // initialize recycler adapter
        if (adapter == null) {
            adapter = new PhotosAdapter(new ArrayList<PathPhoto>(), PhotosAdapter.GRID_LAYOUT);
            adapter.setHasStableIds(true);
        }

        // initialize view model
        if (galleryViewModel == null) {
            galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
        }

        // populate photo gallery from view model
        final int pathId = getArguments().getInt("pathId", -1);
        if (pathId == -1) {
            // retrieve all photos from database
            galleryViewModel.getAllPathPhotos().observe(this, new Observer<List<PathPhoto>>() {
                @Override
                public void onChanged(@Nullable List<PathPhoto> pathPhotos) {
                    photosRecycler.setAdapter(adapter);
                    adapter.setPhotoList(pathPhotos, pathId);
                    onEmptyPhotoList(pathPhotos);
                }
            });
        } else {
            // retrieve photos linked to path id from database
            galleryViewModel.getPhotosByPath(pathId).observe(this, new Observer<List<PathPhoto>>() {
                @Override
                public void onChanged(@Nullable List<PathPhoto> pathPhotos) {
                    photosRecycler.setAdapter(adapter);
                    adapter.setPhotoList(pathPhotos, pathId);
                    onEmptyPhotoList(pathPhotos);
                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        updateLayoutDirection(newConfig);
    }

    /**
     * Update the layout of the gallery based on the orientation.
     * @param newConfig new config
     */
    private void updateLayoutDirection(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount((int) (1.7f * ((float) displayMetrics.heightPixels) / displayMetrics.ydpi));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount((int) (1.7f * ((float) displayMetrics.widthPixels) / displayMetrics.xdpi));
        }
    }

    /**
     * Handle case when there is no photo to display
     * @param pathPhotos list of photos
     */
    private void onEmptyPhotoList(List<PathPhoto> pathPhotos) {
        TextView text = getView().findViewById(R.id.empty);

        if (pathPhotos.isEmpty()) {
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }

}
