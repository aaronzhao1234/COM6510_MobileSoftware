package com.example.mobiledev.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mobiledev.R;

public class PhotoDetailsAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private Context context;
    private int[] sliderImage = {
            R.drawable.image,
            R.drawable.image2,
            R.drawable.image2,
            R.drawable.image
    };

    //---------------------------------------------------------------------------------------------
    public PhotoDetailsAdapter(Context context) {
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
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
        //return view == ((LinearLayout) object);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View v = layoutInflater
                .inflate(R.layout.photo_details_item, container, false);

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
