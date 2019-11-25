package com.example.mobiledev.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryLayoutManager extends GridLayoutManager {

    public GalleryLayoutManager(Context context, int columnWidth) {
        super(context, columnWidth);
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, widthSpec);
    }
}
