package com.example.mobiledev;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class GalleryLayoutManager extends GridLayoutManager {

    public GalleryLayoutManager(Context context, int columnWidth) {
        super(context, columnWidth);
    }

    @Override
    public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, widthSpec);
    }
}
