package com.pma.chat.pmaChat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.auth.LoginActivity;


public class ProfileSettingsFragment extends Fragment {

    private TextView tvLogout;

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        tvLogout = (TextView) view.findViewById(R.id.tvLogout);

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        return view;
    }
}


