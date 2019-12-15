package uk.ac.shef.oak.com4510.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.activities.PhotoDetailsActivity;
import uk.ac.shef.oak.com4510.model.PathPhoto;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    public static final int GRID_LAYOUT = 0;
    static final int HORIZONTAL_LAYOUT = 1;

    private List<PathPhoto> photoList;
    private int layoutType;
    private int pathId = -1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PhotosAdapter(List<PathPhoto> photoList, int layoutType) {
        this.photoList = photoList;
        this.layoutType = layoutType;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        PathPhoto photo = photoList.get(position);
        holder.imageView.setImageResource(Integer.parseInt(photo.getPhotoPath()));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.imageView.getContext(), PhotoDetailsActivity.class);
                intent.putExtra("startPosition", position);
                intent.putExtra("pathId", pathId);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void setPhotoList(List<PathPhoto> photoList) {
        if (photoList != null) {
            this.photoList = photoList;
            notifyDataSetChanged();
        }
    }

    public void setPhotoList(List<PathPhoto> photoList, int pathId) {
        setPhotoList(photoList);
        this.pathId = pathId;
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
