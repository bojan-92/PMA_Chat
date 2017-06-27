package com.pma.chat.pmaChat.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.activities.ChatActivity;
import com.pma.chat.pmaChat.data.PhoneContactListProvider;
import com.pma.chat.pmaChat.model.UserInfo;

import java.util.ArrayList;

//implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener
public class ChatContactListFragment extends Fragment  {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private ArrayList contacts;

    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserInfoDatabaseReference = mRootDatabaseReference.child("userInfo");

    private ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final ChatContactListFragment fragment = this;

        final View view = inflater.inflate(R.layout.users_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.usersListProgressBar);

        readContactsWrapper();

        mUserInfoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressBar.setVisibility(ProgressBar.VISIBLE);

                ArrayList<String> users = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    users.add(data.getValue(UserInfo.class).getFirstName() + " " + data.getValue(UserInfo.class).getLastName());
                }

                ArrayAdapter adapter = new ArrayAdapter(
                        fragment.getActivity(),
                        R.layout.user_list_item,
                        R.id.tv_contact_name,
                        users);

                ListView listView = (ListView) view.findViewById(R.id.friendsList);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(view.getContext(), ChatActivity.class);
                        startActivity(i);
                    }
                });

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }

    private void readContactsWrapper() {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_CONTACTS);
        if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_CONTACTS)) {
                showMessageOKCancel("You need to allow access to Contacts",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[] {android.Manifest.permission.READ_CONTACTS},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {android.Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        readContacts();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this.getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    readContacts();
                } else {
                    // Permission Denied
                    Toast.makeText(this.getActivity(), "READ_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void readContacts() {
        PhoneContactListProvider contactListProvider = new PhoneContactListProvider(getActivity());
        contacts = contactListProvider.fetchAll();

        Log.i("TEST", contacts.toString());

    }



}
