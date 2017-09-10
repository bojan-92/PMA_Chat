package com.pma.chat.pmaChat.activities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.adapters.DrawerListAdapter;
import com.pma.chat.pmaChat.auth.AuthCallback;
import com.pma.chat.pmaChat.auth.AuthService;
import com.pma.chat.pmaChat.auth.AuthServiceImpl;
import com.pma.chat.pmaChat.auth.LoginActivity;
import com.pma.chat.pmaChat.auth.SignupActivity;
import com.pma.chat.pmaChat.fragments.ChatListFragment;
import com.pma.chat.pmaChat.model.NavItem;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.fragments.ChatContactListFragment;
import com.pma.chat.pmaChat.fragments.ProfileSettingsFragment;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.ConnectionService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ConnectionService.AsyncResponse  {

    private ListView mDrawerListView;
    private List<NavItem> mDrawerItems = new ArrayList<NavItem>();

    private DrawerLayout mDrawerLayout;
    private DrawerListAdapter mDrawerAdapter;

    // Drawer header Views
    private ImageView mDrawerHeaderAvatar;
    private TextView mDrawerHeaderName;
    private TextView mDrawerHeaderPhoneNumber;

    private ImageButton mPhotoPickerButton;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference mUserReference;

    private DatabaseReference mUsersReference;

    private AuthService mAuthService;

    private ConnectionService mConnectionService;

    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerListView = (ListView) findViewById(R.id.listview);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        mDrawerAdapter = new DrawerListAdapter(this, mDrawerItems);
        mDrawerListView.setAdapter(mDrawerAdapter);

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = telephonyManager.getLine1Number();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mConnectionService = new ConnectionService(getApplicationContext(), this);
        try {
            mConnectionService.execute(new URL("https://firebase.google.com"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mAuthService = new AuthServiceImpl();
        mAuthService.logoutUser();
        if(mAuthService.getUser() == null) {
            mUsersReference = MyFirebaseService.getUsersDatabaseReference();
            finish();
            startActivity(new Intent(getApplicationContext(), SignupActivity.class));
        }

        initDrawerListHeader();
        initDrawerListItems(mDrawerItems);

        // start default fragment
        startDefaultFragment();

        mDrawerListView.setOnItemClickListener(navigationDrawerOnItemClickListener);
    }

    @Override
    public void onProcessFinish(Boolean hasInternetConnection) {
        // if there is internet connection refresh data and load user profile image
        if(hasInternetConnection && mAuthService.getUser() != null) {
            mUserReference = MyFirebaseService.getCurrentUserDatabaseReference();
            mUserReference.addValueEventListener(userInfoValueEventListener);
        }
    }

    private AdapterView.OnItemClickListener navigationDrawerOnItemClickListener = new AdapterView.OnItemClickListener() {
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
//                case 4:
//                    mAuthService.logoutUser();
//                    Intent i = new Intent(MainActivity.this.getApplicationContext(), LoginActivity.class);
//                    startActivity(i);
//                    return;
                default:
                    fragment = new ChatListFragment();
                    break;
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();
            mDrawerLayout.closeDrawers();
        }
    };

    private ValueEventListener userInfoValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            User userInfo = dataSnapshot.getValue(User.class);

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name", userInfo.getName());
            editor.putString("phoneNumber", userInfo.getPhoneNumber());
            editor.apply();

            if (userInfo.getProfileImageUri() != null) {
                Glide.with(mDrawerHeaderAvatar.getContext())
                        .load(userInfo.getProfileImageUri())
                        .into(mDrawerHeaderAvatar);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("MainActivity", "Firebase databaseError");
        }
    };

    /**
     * helper method, which initialises the navigation drawer header.
     */
    private void initDrawerListHeader() {
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.partial_navigation_drawer_header,null, false);

        mDrawerHeaderAvatar = (ImageView) listHeaderView.findViewById(R.id.drawer_header_avatar);
        mDrawerHeaderName = (TextView) listHeaderView.findViewById(R.id.drawer_header_name);
        mDrawerHeaderPhoneNumber = (TextView) listHeaderView.findViewById(R.id.drawer_header_phone_number);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MySharedPreferences", 0); // 0 - for private mode
        if(pref != null) {
            mDrawerHeaderName.setText(pref.getString("name", null));
            mDrawerHeaderPhoneNumber.setText(mPhoneNumber);
        }

        mDrawerListView.addHeaderView(listHeaderView);
    }

    private void startDefaultFragment() {
        Fragment fragment = new ChatListFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.relativeLayout, fragment).commit();
        mDrawerLayout.closeDrawers();
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
       // mNavItems.add(new NavItem(getString(R.string.logout), R.drawable.ic_cancel));
    }

}
