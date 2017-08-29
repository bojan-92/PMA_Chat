package com.pma.chat.pmaChat.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.data.PhoneContactListProvider;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.PhoneContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.utils.Converters;
import com.pma.chat.pmaChat.utils.RemoteConfig;

import java.util.ArrayList;
import java.util.List;

//implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener
public class ChatContactListFragment extends Fragment  {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private List<PhoneContact> mPhoneContacts;

    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserDatabaseReference = mRootDatabaseReference.child(RemoteConfig.USER);

    private ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        final ChatContactListFragment fragment = this;

        final View view = inflater.inflate(R.layout.users_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.usersListProgressBar);

        readContactsWrapper();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(mPhoneContacts == null) return;

                ArrayList<String> phoneNumbers = new ArrayList<>();


                for(PhoneContact phoneContact : mPhoneContacts) {
                    String phoneNumber = phoneContact.getPhoneNumber().replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
                    phoneNumbers.add(phoneNumber);
                }

                mProgressBar.setVisibility(ProgressBar.VISIBLE);

                ArrayList<String> chatContacts = new ArrayList<>();

                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getActivity().getApplicationContext());

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if(phoneNumbers.contains(user.getPhoneNumber())) {
                        chatContacts.add(user.getFirstName() + " " + user.getLastName());
                        databaseHelper.addOrUpdateChatContact(Converters.userToChatContact(user));
                    }
                }

                ArrayAdapter adapter = new ArrayAdapter(
                        fragment.getActivity(),
                        R.layout.user_list_item,
                        R.id.tv_contact_name,
                        chatContacts);

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
                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT)
                        .show();
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
        mPhoneContacts = contactListProvider.fetchAll();
    }



}
