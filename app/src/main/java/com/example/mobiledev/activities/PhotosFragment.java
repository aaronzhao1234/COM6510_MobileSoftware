package com.example.mobiledev.activities;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobiledev.model.PathPhoto;
import com.example.mobiledev.utils.GalleryLayoutManager;
import com.example.mobiledev.R;
import com.example.mobiledev.adapter.PhotosAdapter;
import com.example.mobiledev.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotosFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

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

        photosRecycler = v.findViewById(R.id.gallery_recycler);

        // use a grid layout manager
        layoutManager = new GalleryLayoutManager(getContext(), 4);
        photosRecycler.setLayoutManager(layoutManager);

        // use this setting to improve performance of recycler view
        photosRecycler.setHasFixedSize(true);

        photosRecycler.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if (adapter == null) {
            adapter = new PhotosAdapter(new ArrayList<Integer>(), PhotosAdapter.GRID_LAYOUT);
            adapter.setHasStableIds(true);
        }

        photosRecycler.setAdapter(adapter);

        if (galleryViewModel == null) {
            galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
            galleryViewModel.getAllPathPhotos().observe(this, new Observer<List<PathPhoto>>() {
                @Override
                public void onChanged(@Nullable List<PathPhoto> pathPhotos) {
                    List<Integer> images = new ArrayList<>();

                    for (int i = 0; i < 32; i++) {
                        images.add(R.drawable.image);
                    }

                    adapter.setPhotoList(images);
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

    private void updateLayoutDirection(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.setSpanCount((int) (1.7f * ((float) displayMetrics.heightPixels) / displayMetrics.ydpi));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount((int) (1.7f * ((float) displayMetrics.widthPixels) / displayMetrics.xdpi));
        }
    }

}
