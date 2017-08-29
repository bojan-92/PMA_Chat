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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
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
    private FirebaseAuth mFirebaseAuth;


    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_contact_profile, container, false);
        //Button changePictureButton = (Button) view.findViewById(R.id.chatButton);

        mFirebaseAuth = FirebaseAuth.getInstance();
        String userId = mFirebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserRef = mRootDatabaseReference.child(RemoteConfig.USER).child(userId);

        mFirstName = (TextView) view.findViewById(R.id.txtFirstName);
        mLastName = (TextView) view.findViewById(R.id.txtLastName);
        mEmail = (TextView) view.findViewById(R.id.txtSignUpEmail);
        mPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);

        cpBtn  = (Button) view.findViewById(R.id.changePictureButton);
        ivProfile  = (ImageView) view.findViewById(R.id.imageViewProfile);

        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                mFirstName.setText("First name: " + user.getFirstName());
                mLastName.setText("Last name: " +user.getLastName());
                mEmail.setText("Email: " + mFirebaseAuth.getCurrentUser().getEmail());
                mPhoneNumber.setText("Number: " +user.getPhoneNumber());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), GALLERY_CODE);
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && null != data) {
            Uri selectedImage = data.getData();
            ivProfile.setImageURI(selectedImage);
        }
    }


}
