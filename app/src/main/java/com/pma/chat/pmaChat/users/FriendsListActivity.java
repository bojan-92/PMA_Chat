package com.pma.chat.pmaChat.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pma.chat.pmaChat.R;


public class FriendsListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_list);

        String[] list = getResources().getStringArray(R.array.users_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.user_list_item,
                R.id.friendsListItem,
                list);
        ListView listView = (ListView) findViewById(R.id.friends_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             //   Object item = parent.getItemAtPosition(position);

                Intent intent = new Intent(FriendsListActivity.this, FriendsProfileActivity.class);
                //based on item add info to intent
                startActivity(intent);
            }
        });

    }
}
