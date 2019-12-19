package uk.ac.shef.oak.com4510.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This layout manager forces the layout to be squared regardless.
 */
public class GalleryLayoutManager extends GridLayoutManager {

    public GalleryLayoutManager(Context context, int columnWidth) {
        super(context, columnWidth);
    }

}
