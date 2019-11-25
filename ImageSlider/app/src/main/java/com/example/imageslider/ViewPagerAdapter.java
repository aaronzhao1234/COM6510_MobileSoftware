package com.example.imageslider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.Layout;
import android.view.LayoutInflater;
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
        //ImageView imageView = new ImageView(context);
        //imageView = imageView.findViewById(R.id.imageView_id);
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

        return scrollView;
        //return linearLayout;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((ScrollView) object);
        //((ViewPager) container).removeView((LinearLayout) object);
    }

}
