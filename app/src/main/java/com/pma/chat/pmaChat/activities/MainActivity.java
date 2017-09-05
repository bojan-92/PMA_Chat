package com.pma.chat.pmaChat.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.adapters.DrawerListAdapter;
import com.pma.chat.pmaChat.adapters.StickerAdapter;
import com.pma.chat.pmaChat.auth.LoginActivity;
import com.pma.chat.pmaChat.model.NavItem;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.fragments.ChatContactListFragment;
import com.pma.chat.pmaChat.fragments.ChatContactProfileFragment;
import com.pma.chat.pmaChat.fragments.ProfileSettingsFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity  {

    private ListView mListView;
    private List<NavItem> mNavItems = new ArrayList<NavItem>();

    private DrawerLayout mDrawerLayout;
    private DrawerListAdapter mListAdapter;
    // Drawer header Views
    private ImageView mDrawerHeaderAvatar;
    private TextView mDrawerHeaderUsername;
    private TextView mDrawerHeaderEmail;

    private ImageButton mPhotoPickerButton;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());

        db.getWritableDatabase();

        initDrawerListItems(mNavItems);

        mFirebaseAuth = FirebaseAuth.getInstance();

        if(mFirebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        mListView = (ListView) findViewById(R.id.listview);

        initDrawerListHeader();

        mListAdapter = new DrawerListAdapter(this, mNavItems);
        mListView.setAdapter(mListAdapter);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment;
                switch (position){
                    case 1:
                        fragment = new ChatContactListFragment();
                        break;
                    case 2:
                        fragment = new ProfileSettingsFragment();
                        break;
                    case 3:
                        fragment = new ChatContactProfileFragment();
                        break;
                    case 4:
                        mFirebaseAuth.signOut();
                        Intent i = new Intent(MainActivity.this.getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        return;
                    default:
                        fragment = new ChatContactListFragment();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();
                mDrawerLayout.closeDrawers();
            }
        });

    }

    /**
     * helper method, which initialises the navigation drawer header.
     */
    private void initDrawerListHeader() {
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.partial_navigation_drawer_header,null, false);

        mDrawerHeaderAvatar = (ImageView) listHeaderView.findViewById(R.id.drawer_header_avatar);
        mDrawerHeaderUsername = (TextView) listHeaderView.findViewById(R.id.drawer_header_username);
        mDrawerHeaderEmail = (TextView) listHeaderView.findViewById(R.id.drawer_header_email);

        // TODO dodelish vrednosti ovim treju varijablama, npr. mDrawerHeaderUsername.setText("David Milivojev")
        mDrawerHeaderUsername.setText(mFirebaseAuth.getCurrentUser().getEmail());

        mListView.addHeaderView(listHeaderView);
    }

    /**
     * helper method, that initialises navigation drawer list items.
     *
     * @param mNavItems The list of items in the navigation drawer.
     */
    private void initDrawerListItems(List<NavItem> mNavItems ){
        mNavItems.add(new NavItem(getString(R.string.friendsList), R.drawable.ic_startchat));
        mNavItems.add(new NavItem(getString(R.string.edit_profile_settings), R.drawable.ic_settings));
        mNavItems.add(new NavItem(getString(R.string.myProfile), R.drawable.ic_avatar));
        mNavItems.add(new NavItem(getString(R.string.logout), R.drawable.ic_cancel));

    }

}
