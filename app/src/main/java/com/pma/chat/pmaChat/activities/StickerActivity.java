package com.pma.chat.pmaChat.activities;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.adapters.StickerAdapter;


/**
 * Created by david on 8/17/17.
 */

public class StickerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_chat_stickers);

        GridView gridView = (GridView) findViewById(R.id.gridView);

        gridView.setAdapter(new StickerAdapter(this));
    }
}


