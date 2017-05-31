package com.pma.chat.pmaChat;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pma.chat.pmaChat.adapters.DrawerListAdapter;
import com.pma.chat.pmaChat.auth.LoginScreen;
import com.pma.chat.pmaChat.model.NavItem;
import com.pma.chat.pmaChat.users.FriendsListFragment;
import com.pma.chat.pmaChat.users.FriendsProfileFragment;
import com.pma.chat.pmaChat.users.ProfileSettingsFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;

    private DrawerListAdapter listAdapter;

    private List<NavItem> mNavItems = new ArrayList<NavItem>();

    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;

    // Drawer header Views

    private ImageView mDrawerHeaderAvatar;

    private TextView mDrawerHeaderUsername;

    private TextView mDrawerHeaderEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawerListItems(mNavItems);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginScreen.class));
        }

        listView = (ListView) findViewById(R.id.listview);

        initDrawerListHeader();

        listAdapter = new DrawerListAdapter(this, mNavItems);
        listView.setAdapter(listAdapter);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment;
                Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                switch (position){
                    case 1:
                        fragment = new FriendsListFragment();
                        break;
                    case 2:
                        fragment = new ProfileSettingsFragment();
                        break;
                    case 3:
                        fragment = new FriendsProfileFragment();
                        break;
                    case 4:
                        firebaseAuth.signOut();
                        Intent i = new Intent(MainActivity.this.getApplicationContext(), LoginScreen.class);
                        startActivity(i);
                        return;
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

    /**
     * helper method, which initialises the navigation drawer header.
     */
    private void initDrawerListHeader() {
        LayoutInflater inflater = getLayoutInflater();
        View listHeaderView = inflater.inflate(R.layout.nav_header_main,null, false);

        mDrawerHeaderAvatar = (ImageView) listHeaderView.findViewById(R.id.drawer_header_avatar);
        mDrawerHeaderUsername = (TextView) listHeaderView.findViewById(R.id.drawer_header_username);
        mDrawerHeaderEmail = (TextView) listHeaderView.findViewById(R.id.drawer_header_email);

        // TODO dodelish vrednosti ovim treju varijablama, npr. mDrawerHeaderUsername.setText("David Milivojev")
        mDrawerHeaderUsername.setText("David Milivojev");

        listView.addHeaderView(listHeaderView);
    }

    /**
     * helper method, that initialises navigation drawer list items.
     *
     * @param mNavItems The list of items in the navigation drawer.
     */
    private void initDrawerListItems(List<NavItem> mNavItems ){
        mNavItems.add(new NavItem(getString(R.string.friendsList), R.drawable.ic_accessibility_black_24px));
        mNavItems.add(new NavItem(getString(R.string.edit_profile_settings), R.drawable.ic_settings_black_24px));
        mNavItems.add(new NavItem(getString(R.string.friends_profile), R.drawable.ic_account_circle_black_24px));
        mNavItems.add(new NavItem(getString(R.string.logout), R.drawable.ic_exit_to_app_black_24px));

    }
}
