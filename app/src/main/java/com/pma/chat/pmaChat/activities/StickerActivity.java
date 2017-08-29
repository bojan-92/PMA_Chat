package com.pma.chat.pmaChat.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.adapters.StickerAdapter;


/**
 * Created by david on 8/17/17.
 */

public class StickerActivity extends AppCompatActivity {

    private AdapterView.OnItemClickListener mListener;

    private StickerAdapter mAdapter;

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_chat_stickers);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        mAdapter = new StickerAdapter(this);

        mListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("StickerActivity", "click event on sticker at position " + position);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("stickerId", (Integer) mAdapter.getItem(position));
                StickerActivity.this.setResult(Activity.RESULT_OK, intent);
                StickerActivity.this.finish();
            }
        };

        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(mListener);
    }

}


