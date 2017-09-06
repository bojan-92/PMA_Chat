package com.pma.chat.pmaChat.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.auth.LoginActivity;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.utils.RemoteConfig;


public class ProfileSettingsFragment extends Fragment {

    private static int GALLERY_CODE = 1;

    private TextView mLogoutTextView;
    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private ImageView mProfilePhotoImageView;
    private Button mSaveButton;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;
    private StorageReference mUsersProfileImagesStorageReference;

    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserReference;

    private User mUserInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_settings, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mUsersProfileImagesStorageReference = FirebaseStorage.getInstance().getReference().child(RemoteConfig.USERS_PROFILE_PHOTOS_STORAGE);

        String userId = mFirebaseAuth.getCurrentUser().getUid();
        mUserReference = mRootDatabaseReference.child(RemoteConfig.USER).child(userId);

        mLogoutTextView = (TextView) view.findViewById(R.id.tvLogout);
        mSaveButton = (Button) view.findViewById(R.id.btnSaveProfileSettings);

        mFirstNameEditText = (EditText) view.findViewById(R.id.txtProfileSettingsFirstName);
        mLastNameEditText = (EditText) view.findViewById(R.id.txtProfileSettingsLastName);
        mProfilePhotoImageView = (ImageView) view.findViewById(R.id.imageViewProfile);

        mProgressDialog = new ProgressDialog(this.getContext());

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUserInfo = dataSnapshot.getValue(User.class);

                mFirstNameEditText.setText(mUserInfo.getFirstName());
                mLastNameEditText.setText(mUserInfo.getLastName());
                if(mUserInfo.getProfileImageUri() != null) {
                    Glide.with(mProfilePhotoImageView.getContext())
                            .load(mUserInfo.getProfileImageUri())
                            .into(mProfilePhotoImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLogoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = mFirstNameEditText.getText().toString().trim();
                String lastName = mLastNameEditText.getText().toString().trim();

                if(!isFormValid(firstName, lastName) || mUserInfo == null) {
                    return;
                }

                mUserInfo.setFirstName(firstName);
                mUserInfo.setLastName(lastName);

                mProgressDialog.setMessage("Saving changes ...");
                mProgressDialog.show();

                mUserReference.setValue(mUserInfo, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        mProgressDialog.dismiss();
                        Toast.makeText(ProfileSettingsFragment.this.getContext(), R.string.profileSettingsSuccessfullyChanged, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mProfilePhotoImageView.setOnClickListener(new View.OnClickListener() {
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

        if (requestCode == GALLERY_CODE && data != null) {
            Uri fileUri = data.getData();

            // Get a reference to store file at photos/<FILENAME>
            StorageReference storageRef = mUsersProfileImagesStorageReference
                    .child(fileUri.getLastPathSegment());

            mProgressDialog.setMessage("Saving changes ...");
            mProgressDialog.show();

            // Upload file to Firebase Storage
            storageRef.putFile(fileUri)
                    .addOnSuccessListener(this.getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            mUserInfo.setProfileImageUri(downloadUrl.toString());

                            mUserReference.setValue(mUserInfo, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    // TODO : add some notification (toast or something)
                                    Toast.makeText(ProfileSettingsFragment.this.getContext(), R.string.profileImageSuccessfullyChanged, Toast.LENGTH_SHORT).show();
                                }
                            });

                            mProgressDialog.dismiss();

                        }
                    });
        }
    }


    private boolean isFormValid(String firstName, String lastName) {
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(ProfileSettingsFragment.this.getContext(), R.string.firstNameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(ProfileSettingsFragment.this.getContext(), R.string.lastNameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}


