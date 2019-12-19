package uk.ac.shef.oak.com4510.activities;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.adapter.PathsAdapter;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

/**
 * This fragment handles the path list. It also handles search
 * queries to show more relevant paths.
 */
public class PathListFragment extends Fragment {

    // view model variables
    private GalleryViewModel galleryViewModel;

    // recycler view handlers
    private RecyclerView pathsRecycler;
    PathsAdapter adapter;

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
        updateList("");
    }

    /**
     * Search for path using title and update the view to show
     * result
     * @param query search query for path title
     */
    public void search(String query) {
        updateList(query);
    }

    /**
     * Updates the path list view based on the search entry showing
     * only paths that match the query requested.
     * @param title query for searching by title
     */
    private void updateList(String title) {
        if (title != null) {
            // initialize recycler adapter
            if (adapter == null) {
                adapter = new PathsAdapter(getContext(), new ArrayList<Path>());
                adapter.setHasStableIds(true);
            }

            // initialize view model
            if (galleryViewModel == null) {
                galleryViewModel = ViewModelProviders.of(this).get(GalleryViewModel.class);
            }

            // request path search to the database
            galleryViewModel.searchByTitle(title).observe(this, new Observer<List<Path>>() {
                @Override
                public void onChanged(@Nullable List<Path> paths) {
                    // update recycler view and adapter
                    pathsRecycler.setAdapter(adapter);
                    adapter.setPathList(paths);
                    onEmptyPhotoList(paths);
                }
            });
        }
    }

    /**
     * Handles the the empty path list case
     * @param paths the path list
     */
    private void onEmptyPhotoList(List<Path> paths) {
        TextView text = getView().findViewById(R.id.empty);

        if (paths.isEmpty()) {
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }

}
