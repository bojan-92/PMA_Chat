package com.pma.chat.pmaChat;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.pma.chat.pmaChat.auth.LoginScreen;
import com.pma.chat.pmaChat.users.FriendsListFragment;
import com.pma.chat.pmaChat.users.FriendsProfileFragment;
import com.pma.chat.pmaChat.users.ProfileSettingsFragment;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private ArrayAdapter<String> listAdapter;

    private String fragmentArray[] = {"FRIENDS LIST", "EDIT PROFILE SETTINGS", "FRIENDS PROFILE"};

    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginScreen.class));
        }

        listView = (ListView) findViewById(R.id.listview);
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,fragmentArray);
        listView.setAdapter(listAdapter);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment;
                switch (position){
                    case 0:
                        fragment = new FriendsListFragment();
                        break;
                    case 1:
                        fragment = new ProfileSettingsFragment();
                        break;
                    case 2:
                        fragment = new FriendsProfileFragment();
                        break;
                    default:
                        fragment = new FriendsListFragment();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();
                drawerLayout.closeDrawers();
            }
        });



    }
}
