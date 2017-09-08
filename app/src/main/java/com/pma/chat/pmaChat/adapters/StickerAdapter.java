package com.pma.chat.pmaChat.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class StickerAdapter extends BaseAdapter {

    private Context mContext;

    public String[] stickerNames = {
            "ic_cat",
            "ic_bear",
            "ic_owl",
            "ic_dog",
            "ic_fox",
            "ic_teddy"
    };

    public StickerAdapter(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return stickerNames.length;
    }

    @Override
    public String getItem(int position) {
        return stickerNames[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        Resources resources = mContext.getResources();
        final int resourceId = resources.getIdentifier(stickerNames[position], "drawable",
                mContext.getPackageName());
        imageView.setImageResource(resourceId);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
        return imageView;
    }
}
