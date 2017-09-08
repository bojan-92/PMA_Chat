package com.pma.chat.pmaChat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.activities.ChatActivity;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.Converters;

public class ChatContactProfileFragment extends Fragment {

    private TextView mNameTextView;
    private TextView mFirebaseNameTextView;
    private TextView mEmailTextView;
    private TextView mPhoneNumberTextView;
    private ImageView mProfilePhotoImageView;

    private Button mStartChatButton;

    private ChatContact mChatContact;
    private DatabaseReference mChatContactRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_contact_profile, container, false);

        mChatContact = (ChatContact)getArguments().getSerializable("CHAT_CONTACT");

        mChatContactRef = MyFirebaseService.getUserDatabaseReferenceById(mChatContact.getFirebaseUserId());

        // initialize ui components

        mNameTextView = (TextView) view.findViewById(R.id.tvName);
        mFirebaseNameTextView = (TextView) view.findViewById(R.id.tvFirebaseName);
        mEmailTextView = (TextView) view.findViewById(R.id.tvSignUpEmail);
        mPhoneNumberTextView = (TextView) view.findViewById(R.id.tvPhoneNumber);
        mProfilePhotoImageView = (ImageView) view.findViewById(R.id.imageViewProfile);

        // fill components with chat contact data

        mNameTextView.setText(R.string.nameLabel);
        mFirebaseNameTextView.setText(R.string.firebaseNameLabel);
        mEmailTextView.setText(R.string.emailLabel);
        mPhoneNumberTextView.setText(R.string.phoneNumberLabel);

        String name = mChatContact.getName();
        // it is possible that you don't have some chat contact in phone contacts
        mNameTextView.append(name != null ? name : "/");
        mFirebaseNameTextView.append(mChatContact.getFirebaseName());
        mEmailTextView.append(mChatContact.getEmail());
        mPhoneNumberTextView.append(mChatContact.getPhoneNumber());

        mStartChatButton = (Button) view.findViewById(R.id.startChatButton);

        // load chat contact profile image

        mChatContactRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userInfo = dataSnapshot.getValue(User.class);

                if(userInfo != null && userInfo.getProfileImageUri() != null) {
                    Glide.with(mProfilePhotoImageView.getContext())
                            .load(userInfo.getProfileImageUri())
                            .into(mProfilePhotoImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
             //   Toast.makeText(ChatContactProfileFragment.this.getContext(), R.string.noInternetConnection, Toast.LENGTH_SHORT).show();
            }
        });

        mStartChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                chatIntent.putExtra("CHAT_CONTACT", mChatContact);
                startActivity(chatIntent);
            }
        });

        return view;
    }

}
