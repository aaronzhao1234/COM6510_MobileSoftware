package com.example.mobiledev.adapter;

import android.content.Intent;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mobiledev.R;
import com.example.mobiledev.activities.PathDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class PathsAdapter extends RecyclerView.Adapter<PathsAdapter.PathViewHolder> {

    private List<List<Integer>> pathList;

    private boolean collapseAction;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PathsAdapter(List<List<Integer>> myDataset) {
        pathList = myDataset;
        collapseAction = false;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PathViewHolder holder, int position) {
        holder.adapter.setPhotoList(pathList.get(position));

        if (collapseAction) {
            holder.show.setText(">");
            collapse(holder.gallery);
        } else {
            holder.show.setText(R.string.show_more_text);
            expand(holder.gallery);
        }

        holder.pathHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), PathDetailsActivity.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pathList.size();
    }

    public void setPathList(List<List<Integer>> pathList) {
        this.pathList = pathList;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    public class PathViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        RecyclerView gallery;
        PhotosAdapter adapter;
        View pathHeading;
        TextView show;

        public PathViewHolder(View v) {
            super(v);

            pathHeading = v.findViewById(R.id.pathHeading);
            show = v.findViewById(R.id.show);
            gallery = v.findViewById(R.id.gallery_recycler);
            gallery.setHasFixedSize(false);

            // use a grid layout manager
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(gallery.getContext(), LinearLayoutManager.HORIZONTAL, false);
            gallery.setLayoutManager(layoutManager);

            adapter = new PhotosAdapter(new ArrayList<Integer>(), PhotosAdapter.HORIZONTAL_LAYOUT);

            gallery.setAdapter(adapter);
            gallery.setItemAnimator(new DefaultItemAnimator());
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PathViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.path_list_item, parent, false);

        return new PathViewHolder(v);
    }

    private void collapse(View v) {
        v.setVisibility(View.GONE);
    }

    private void expand(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public void setCollapsed(boolean collapseAction) {
        this.collapseAction = collapseAction;
        notifyDataSetChanged();
    }

    public boolean isCollapsed() {
        return collapseAction;
    }

}
