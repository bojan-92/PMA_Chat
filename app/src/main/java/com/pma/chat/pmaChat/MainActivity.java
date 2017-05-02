package com.pma.chat.pmaChat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pma.chat.pmaChat.users.FragmentOne;
import com.pma.chat.pmaChat.users.FragmentThree;
import com.pma.chat.pmaChat.users.FragmentTwo;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> listAdapter;
    String fragmentArray[] = {"FRIENDS LIST", "EDIT PROFILE SETTINGS", "FRIENDS PROFILE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,fragmentArray);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment;
                switch (position){
                    case 0:
                        fragment = new FragmentOne();
                        break;
                    case 1:
                        fragment = new FragmentTwo();
                        break;
                    case 2:
                        fragment = new FragmentThree();
                        break;
                    default:
                        fragment = new FragmentOne();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();

            }
        });
    }
}
