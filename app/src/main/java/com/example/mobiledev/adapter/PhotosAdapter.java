package com.example.mobiledev.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mobiledev.R;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    public static final int GRID_LAYOUT = 0;
    static final int HORIZONTAL_LAYOUT = 1;

    private List<Integer> photoList;
    private int layoutType;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PhotosAdapter(List<Integer> photoList, int layoutType) {
        this.photoList = photoList;
        this.layoutType = layoutType;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.imageView.setImageResource(photoList.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void setPhotoList(List<Integer> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView imageView;

        PhotoViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.imageView);
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        int layout = (layoutType == GRID_LAYOUT)
                ? R.layout.photos_image : R.layout.path_list_image;

        View v = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new PhotoViewHolder(v);
    }

}
