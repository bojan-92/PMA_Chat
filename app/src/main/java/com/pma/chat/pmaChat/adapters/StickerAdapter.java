package com.pma.chat.pmaChat.adapters;

/**
 * Created by david on 8/17/17.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.pma.chat.pmaChat.R;


public class StickerAdapter extends BaseAdapter {
    private Context context;
    public Integer[] images = {
            R.drawable.ic_cat,
            R.drawable.ic_bear,
            R.drawable.ic_owl,
            R.drawable.ic_dog,
            R.drawable.ic_fox,
            R.drawable.ic_teddy

    };

    public StickerAdapter(Context c){
        context = c;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(images[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new GridView.LayoutParams(200,200));
        return imageView;
    }
}
