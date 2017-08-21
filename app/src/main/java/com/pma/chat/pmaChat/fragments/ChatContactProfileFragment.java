package com.pma.chat.pmaChat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.activities.ChatActivity;


public class ChatContactProfileFragment extends Fragment {

    private TextView mFirstName;
    private TextView mLastName;
    private TextView mEmail;
    private TextView mPhoneNumber;
    private FirebaseAuth mFirebaseAuth;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_contact_profile, container, false);
        Button chatButton = (Button) view.findViewById(R.id.chatButton);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mFirstName = (TextView) view.findViewById(R.id.txtFirstName);
        mLastName = (TextView) view.findViewById(R.id.txtLastName);
        mEmail = (TextView) view.findViewById(R.id.txtSignUpEmail);
        mPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
        mFirstName.setText("david");
        mLastName.setText("prezime");
        mEmail.setText(mFirebaseAuth.getCurrentUser().getEmail());
        mPhoneNumber.setText("123");


        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ChatActivity.class);
                startActivity(i);
            }
        });
        return view;

    }



}
