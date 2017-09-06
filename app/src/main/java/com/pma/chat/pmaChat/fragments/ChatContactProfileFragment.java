package com.pma.chat.pmaChat.fragments;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.activities.ChatActivity;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.utils.RemoteConfig;

import java.io.File;

public class ChatContactProfileFragment extends Fragment {

    private static int GALLERY_CODE = 1;

    Button cpBtn;
    ImageView ivProfile;

    private TextView mFirstName;
    private TextView mLastName;
    private TextView mEmail;
    private TextView mPhoneNumber;
    private ImageView mProfilePhotoImageView;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseHelper mLocalDatabaseInstance;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_contact_profile, container, false);
        //Button changePictureButton = (Button) view.findViewById(R.id.chatButton);

        mFirebaseAuth = FirebaseAuth.getInstance();
        String userId = mFirebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserRef = mRootDatabaseReference.child(RemoteConfig.USER).child(userId);

        mLocalDatabaseInstance = DatabaseHelper.getInstance(this.getContext().getApplicationContext());

        mFirstName = (TextView) view.findViewById(R.id.txtFirstName);
        mLastName = (TextView) view.findViewById(R.id.txtLastName);
       // mEmail = (TextView) view.findViewById(R.id.txtSignUpEmail);
        mPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);

        mProfilePhotoImageView = (ImageView) view.findViewById(R.id.imageViewProfile);

        mFirstName.setText(R.string.firstNameLabel);
        mLastName.setText(R.string.lastNameLabel);
        mPhoneNumber.setText(R.string.phoneNumberLabel);

        cpBtn  = (Button) view.findViewById(R.id.changePictureButton);
        ivProfile  = (ImageView) view.findViewById(R.id.imageViewProfile);

        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userInfo = dataSnapshot.getValue(User.class);

                mFirstName.append(userInfo.getFirstName());
                mLastName.append(userInfo.getLastName());
                mPhoneNumber.append(userInfo.getPhoneNumber());
                //mEmail.setText("Email: " + mFirebaseAuth.getCurrentUser().getEmail());

                if(userInfo.getProfileImageUri() != null) {
                    Glide.with(mProfilePhotoImageView.getContext())
                            .load(userInfo.getProfileImageUri())
                            .into(mProfilePhotoImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
//                ChatContact chatContact = mLocalDatabaseInstance.getChatContactById();
//                chatIntent.putExtra("CHAT_CONTACT", chatContact);
//                startActivity(chatIntent);
            }
        });


        return view;
    }

}
