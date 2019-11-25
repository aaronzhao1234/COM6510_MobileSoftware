package com.example.mobiledev.activities;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobiledev.R;
import com.example.mobiledev.adapter.PathsAdapter;
import com.example.mobiledev.model.PathPhoto;
import com.example.mobiledev.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PathListFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    private RecyclerView pathsRecycler;
    public PathsAdapter adapter;

    public static PathListFragment newInstance() {
        return new PathListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.path_list_fragment, container, false);

        pathsRecycler = v.findViewById(R.id.gallery_recycler);

        // use a grid layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        pathsRecycler.setLayoutManager(layoutManager);
        pathsRecycler.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (adapter == null) {
            adapter = new PathsAdapter(new ArrayList<List<Integer>>());
            adapter.setHasStableIds(true);
        }

        pathsRecycler.setAdapter(adapter);

        if (galleryViewModel == null) {
            galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
            galleryViewModel.getAllPathPhotos().observe(this, new Observer<List<PathPhoto>>() {
                @Override
                public void onChanged(@Nullable List<PathPhoto> pathPhotos) {
                    List<List<Integer>> paths = new ArrayList<>();

                    Random r = new Random();
                    for (int i = 0; i < 50; i++) {
                        paths.add(new ArrayList<Integer>());

                        for (int j = 0; j < 32; j++) {

                            if (r.nextBoolean()) {
                                paths.get(i).add(R.drawable.image);
                            } else {
                                paths.get(i).add(R.drawable.image2);
                            }
                        }
                    }

                    adapter.setPathList(paths);
                }
            });
        }
    }

}
