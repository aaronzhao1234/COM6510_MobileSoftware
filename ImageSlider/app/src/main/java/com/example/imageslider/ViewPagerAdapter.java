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
    private LayoutInflater layoutInflater;
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
        this.layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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
        return view == ((View) object);
        //return view == ((LinearLayout) object);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public Object instantiateItem( ViewGroup container, int position) {

        View v = layoutInflater
                .inflate(R.layout.view_pager, container, false);

        //---------------------------------------------------------------------------------------------
       ImageView imageView = v.findViewById(R.id.imageView_id);
       TextView textView = v.findViewById(R.id.textView_id);
       TextView textView2 = v.findViewById(R.id.textView2_id);
       TextView textView3 = v.findViewById(R.id.textView3_id);

        //---------------------------------------------------------------------------------------------
        imageView.setImageResource(sliderImage[position]);

        //---------------------------------------------------------------------------------------------
        textView.setText("test text 1" + position);
        textView2.setText("test text 2" + position);
        textView3.setText("test text 3" + position);

        //---------------------------------------------------------------------------------------------;

        container.addView(v);
        return v;
    }
    //---------------------------------------------------------------------------------------------
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //((ViewPager) container).removeView((ScrollView) object);
        //((ViewPager) container).removeView((LinearLayout) object);
        ((ViewPager) container).removeView((View) object);
    }

}
