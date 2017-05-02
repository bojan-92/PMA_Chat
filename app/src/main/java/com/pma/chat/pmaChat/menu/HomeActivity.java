package com.pma.chat.pmaChat.menu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pma.chat.pmaChat.MainActivity;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.chat.ChatActivity;
import com.pma.chat.pmaChat.users.ProfileSettingsActivity;
import com.pma.chat.pmaChat.users.FriendsListActivity;

public class HomeActivity extends AppCompatActivity {

    Button btnFriendsList;
    Button btnChat;
    Button btnProfileSettings;
    Button btnExit;
;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        btnFriendsList = (Button)findViewById(R.id.friendsListBtn);
        btnFriendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FriendsListActivity.class);
                startActivity(i);
            }
        });

        btnProfileSettings = (Button)findViewById(R.id.profileSettingsBtn);
        btnProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfileSettingsActivity.class);
                startActivity(i);
            }
        });



        btnExit = (Button)findViewById(R.id.exitBtn);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
}
