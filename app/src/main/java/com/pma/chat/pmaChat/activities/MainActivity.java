package com.pma.chat.pmaChat.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.adapters.DrawerListAdapter;
import com.pma.chat.pmaChat.adapters.StickerAdapter;
import com.pma.chat.pmaChat.auth.AuthService;
import com.pma.chat.pmaChat.auth.AuthServiceImpl;
import com.pma.chat.pmaChat.auth.LoginActivity;
import com.pma.chat.pmaChat.fragments.ChatListFragment;
import com.pma.chat.pmaChat.model.NavItem;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.fragments.ChatContactListFragment;
import com.pma.chat.pmaChat.fragments.ChatContactProfileFragment;
import com.pma.chat.pmaChat.fragments.ProfileSettingsFragment;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.RemoteConfig;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity  {

    private ListView mListView;
    private List<NavItem> mNavItems = new ArrayList<NavItem>();

    private DrawerLayout mDrawerLayout;
    private DrawerListAdapter mListAdapter;

    // Drawer header Views
    private ImageView mDrawerHeaderAvatar;
    private TextView mDrawerHeaderName;
    private TextView mDrawerHeaderEmail;

    private ImageButton mPhotoPickerButton;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference mUserReference;

    private AuthService mAuthService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuthService = new AuthServiceImpl();

        if(mAuthService.getUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;
        }

        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());

        db.getWritableDatabase();

        initDrawerListItems(mNavItems);

        mUserReference = MyFirebaseService.getCurrentUserDatabaseReference();

        mListView = (ListView) findViewById(R.id.listview);

        initDrawerListHeader();

        mListAdapter = new DrawerListAdapter(this, mNavItems);
        mListView.setAdapter(mListAdapter);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        Fragment fragment = new ChatListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();
        mDrawerLayout.closeDrawers();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment;
                switch (position){
                    case 1:
                        fragment = new ChatListFragment();
                        break;
                    case 2:
                        fragment = new ChatContactListFragment();
                        break;
                    case 3:
                        fragment = new ProfileSettingsFragment();
                        break;
                    case 4:
                        mAuthService.logoutUser();
                        Intent i = new Intent(MainActivity.this.getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        return;
                    default:
                        fragment = new ChatListFragment();
                        break;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();
                mDrawerLayout.closeDrawers();
            }
        });

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userInfo = dataSnapshot.getValue(User.class);

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("name", userInfo.getName());
                editor.putString("email", userInfo.getEmail());
                editor.putString("phoneNumber", userInfo.getPhoneNumber());
                editor.apply();

                if(userInfo.getProfileImageUri() != null) {
                    Glide.with(mDrawerHeaderAvatar.getContext())
                            .load(userInfo.getProfileImageUri())
                            .into(mDrawerHeaderAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        mDrawerHeaderName = (TextView) listHeaderView.findViewById(R.id.drawer_header_name);
        mDrawerHeaderEmail = (TextView) listHeaderView.findViewById(R.id.drawer_header_email);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
        if(pref != null) {
            mDrawerHeaderName.setText(pref.getString("name", null));
            mDrawerHeaderEmail.setText(pref.getString("email", null));
        }

        mListView.addHeaderView(listHeaderView);
    }

    /**
     * helper method, that initialises navigation drawer list items.
     *
     * @param mNavItems The list of items in the navigation drawer.
     */
    private void initDrawerListItems(List<NavItem> mNavItems ){
        mNavItems.add(new NavItem(getString(R.string.chatList), R.drawable.ic_startchat));
        mNavItems.add(new NavItem(getString(R.string.friendsList), R.drawable.ic_two_users));
        mNavItems.add(new NavItem(getString(R.string.edit_profile_settings), R.drawable.ic_settings));
        mNavItems.add(new NavItem(getString(R.string.logout), R.drawable.ic_cancel));
    }

}
