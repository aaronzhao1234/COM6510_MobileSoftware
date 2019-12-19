package uk.ac.shef.oak.com4510.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.shef.oak.com4510.R;
import uk.ac.shef.oak.com4510.activities.PathDetailsActivity;
import uk.ac.shef.oak.com4510.model.Path;
import uk.ac.shef.oak.com4510.model.PathPhoto;
import uk.ac.shef.oak.com4510.utils.Utilities;
import uk.ac.shef.oak.com4510.viewmodel.GalleryViewModel;

import java.util.ArrayList;
import java.util.List;

public class PathsAdapter extends RecyclerView.Adapter<PathsAdapter.PathViewHolder> {

    private Context context;
    private List<Path> pathList;

    private boolean collapseAction;

    private GalleryViewModel galleryViewModel;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PathsAdapter(Context context, List<Path> myDataset) {
        this.context = context;

        pathList = myDataset;
        collapseAction = false;

        galleryViewModel = ViewModelProviders.of((FragmentActivity) context).get(GalleryViewModel.class);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final PathViewHolder holder, final int position) {
        galleryViewModel.getPhotosByPath(pathList.get(position).getId()).observe((LifecycleOwner) context,
                new Observer<List<PathPhoto>>() {
                    @Override
                    public void onChanged(List<PathPhoto> pathPhotos) {
                        if (pathPhotos.size() == 0) {
                            holder.empty.setVisibility(View.VISIBLE);
                        } else {
                            if (pathList.size() != 0) {
                                holder.adapter.setPhotoList(pathPhotos, pathList.get(position).getId());
                            }
                        }
                    }
                });

        holder.dateText.setText(Utilities.dateToStringSimple(pathList.get(position).getStartTime()));
        holder.titleText.setText(pathList.get(position).getTitle());

        if (collapseAction) {
            holder.show.setText(">");
            collapse(holder);
        } else {
            holder.show.setText(R.string.show_more_text);
            expand(holder);
        }

        holder.pathHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), PathDetailsActivity.class);
                intent.putExtra("pathId", pathList.get(position).getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pathList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setPathList(List<Path> pathList) {
        this.pathList = pathList;
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    public class PathViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        RecyclerView gallery;
        PhotosAdapter adapter;

        View pathHeading;
        View galleryContanier;

        TextView titleText;
        TextView dateText;
        TextView empty;
        TextView show;

        public PathViewHolder(View v) {
            super(v);

            empty = v.findViewById(R.id.noPhoto);
            pathHeading = v.findViewById(R.id.pathHeading);
            show = v.findViewById(R.id.show);
            gallery = v.findViewById(R.id.gallery_recycler);
            galleryContanier = v.findViewById(R.id.gallery_container);

            titleText = v.findViewById(R.id.titleText);
            dateText = v.findViewById(R.id.dateText);

            // use a grid layout manager
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(gallery.getContext(), LinearLayoutManager.HORIZONTAL, false);
            gallery.setLayoutManager(layoutManager);

            adapter = new PhotosAdapter(new ArrayList<PathPhoto>(), PhotosAdapter.HORIZONTAL_LAYOUT);

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

    private void collapse(PathViewHolder holder) {
        holder.galleryContanier.setVisibility(View.GONE);
    }

    private void expand(PathViewHolder holder) {
        holder.galleryContanier.setVisibility(View.VISIBLE);
    }

    public void setCollapsed(boolean collapseAction) {
        this.collapseAction = collapseAction;
        notifyDataSetChanged();
    }

    public boolean isCollapsed() {
        return collapseAction;
    }

}
