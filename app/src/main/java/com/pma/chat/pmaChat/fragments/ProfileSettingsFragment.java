package com.pma.chat.pmaChat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.auth.LoginActivity;
import com.pma.chat.pmaChat.auth.SignupActivity;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.utils.RemoteConfig;


public class ProfileSettingsFragment extends Fragment {

    private TextView tvLogout;
    private EditText txtFirstName;
    private EditText txtLastName;
    private Button btnSave;

    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private User mUser;

    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        String userId = mFirebaseAuth.getCurrentUser().getUid();
        final DatabaseReference currentUserRef = mRootDatabaseReference.child(RemoteConfig.USER).child(userId);

        tvLogout = (TextView) view.findViewById(R.id.tvLogout);
        btnSave = (Button) view.findViewById(R.id.btnSaveProfileSettings);

        txtFirstName = (EditText) view.findViewById(R.id.txtProfileSettingsFirstName);
        txtLastName = (EditText) view.findViewById(R.id.txtProfileSettingsLastName);
//        String firstName = txtFirstName.getText().toString().trim();
//        String lastName = txtLastName.getText().toString().trim();

        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUser = dataSnapshot.getValue(User.class);

                txtFirstName.setText(mUser.getFirstName());
                txtLastName.setText(mUser.getLastName());



//                String firstName = .getText().toString().trim();
//                 String lastName = txtLastName.getText().toString().trim();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = txtFirstName.getText().toString().trim();
                String lastName = txtLastName.getText().toString().trim();

                if(!isFormValid(firstName, lastName) || mUser == null) {
                    return;
                }

                mUser.setFirstName(firstName);
                mUser.setLastName(lastName);

                currentUserRef.setValue(mUser, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        // TODO : add some notification (toast or something)
                        Log.i("TODO-MESSAGE", "TODO-MESSSAGE");
                    }
                });


            }
        });

        return view;
    }


    private boolean isFormValid(String firstName, String lastName) {
        if (TextUtils.isEmpty(firstName)) {

            return false;
        }
        if (TextUtils.isEmpty(lastName)) {

            return false;
        }

        return true;
    }
}


