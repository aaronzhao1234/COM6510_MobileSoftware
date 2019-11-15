package com.example.imageslider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private int[] sliderImage = {
            R.drawable.img1647,
            R.drawable.img1685,
            R.drawable.img1694,
            R.drawable.img1701
    };

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    //---------------------------------------------------------------------------------------------
    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public int getCount() {
        return sliderImage.length;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public boolean isViewFromObject( View view,  Object object) {
        return view == ((ScrollView) object);
        //return view == ((LinearLayout) object);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public Object instantiateItem( ViewGroup container, int position) {

        //---------------------------------------------------------------------------------------------
        ScrollView scrollView = new ScrollView(context);
        ImageView imageView = new ImageView(context);
        TextView textView = new TextView(context);
        LinearLayout linearLayout = new LinearLayout(context);

        //---------------------------------------------------------------------------------------------
        linearLayout.addView(textView,0);
        linearLayout.addView(imageView,0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        //linearLayout.setRotation(-90);

        //---------------------------------------------------------------------------------------------
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(sliderImage[position]);

        //---------------------------------------------------------------------------------------------
        textView.setText("test text " + position);
        textView.setTextSize(30);
        textView.setBackgroundColor(0xffffffff);

        //---------------------------------------------------------------------------------------------
        scrollView.addView(linearLayout,0);

        //((ViewPager) container).addView(imageView,0);//add imageView
        //((ViewPager) container).addView(linearLayout,0);//add linearView
        ((ViewPager) container).addView(scrollView,0);//add scrollView

        //----------FullScreen-------------------------------------------------------------------
        mVisible = true;
        //mControlsView = findViewById(R.id.viewPager_id);
        mContentView = findViewById(R.id.viewPager_id);

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        return scrollView;
        //return linearLayout;
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((ScrollView) object);
        //((ViewPager) container).removeView((LinearLayout) object);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
