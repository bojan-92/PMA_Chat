package com.pma.chat.pmaChat.users;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.chat.ChatActivity;
import com.pma.chat.pmaChat.menu.HomeActivity;

import static android.R.attr.button;

/**
 * Created by david on 4/29/17.
 */

public class FragmentThree extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_profile, container, false);
        Button btnChat = (Button) view.findViewById(R.id.chatButton);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ChatActivity.class);
                startActivity(i);
            }
        });
        return view;


    }



}
