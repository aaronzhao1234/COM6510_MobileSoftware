package uk.ac.shef.oak.com4510.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.adapter.PathsAdapter;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;

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
            adapter = new PathsAdapter(getContext(), new ArrayList<Path>());
            adapter.setHasStableIds(true);
        }

        pathsRecycler.setAdapter(adapter);

        if (galleryViewModel == null) {
            galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
            galleryViewModel.getAllPaths().observe(this, new Observer<List<Path>>() {
                @Override
                public void onChanged(@Nullable List<Path> paths) {
                    adapter.setPathList(paths);
                }
            });
        }
    }

}
