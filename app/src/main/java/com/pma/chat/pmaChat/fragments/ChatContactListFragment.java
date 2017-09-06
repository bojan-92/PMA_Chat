package com.pma.chat.pmaChat.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.pma.chat.pmaChat.adapters.ChatContactsAdapter;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.data.PhoneContactListProvider;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.PhoneContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.utils.Converters;
import com.pma.chat.pmaChat.utils.RemoteConfig;

import java.util.ArrayList;
import java.util.List;


public class ChatContactListFragment extends Fragment implements
        ChatContactsAdapter.ChatContactsAdapterOnClickHandler  {

    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;

   // private static final int ID_CHAT_CONTACTS_LOADER = 11;

    private List<PhoneContact> mPhoneContacts;

    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserDatabaseReference = mRootDatabaseReference.child(RemoteConfig.USER);

    private DatabaseHelper mLocalDatabaseInstance;

    private ChatContactsAdapter mChatContactsAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        final ChatContactListFragment fragment = this;

        final View view = inflater.inflate(R.layout.fragment_chat_contacts, container, false);

        mLocalDatabaseInstance = DatabaseHelper.getInstance(getActivity().getApplicationContext());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_chat_contacts);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_chat_contacts);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

       // getActivity().getSupportLoaderManager().initLoader(ID_CHAT_CONTACTS_LOADER, null, this);

        readContactsWrapper();

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT);
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

                updateChatContacts(dataSnapshot);

                loadChatContacts();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT)
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


    @Override
    public void onClick(ChatContact chatContact) {

        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
        chatIntent.putExtra("CHAT_CONTACT", chatContact);
        startActivity(chatIntent);
    }


    private void updateChatContacts(DataSnapshot dataSnapshot) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        for(PhoneContact phoneContact : mPhoneContacts) {
            String phoneNumber = phoneContact.getPhoneNumber().replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
            phoneNumbers.add(phoneNumber);
        }

        ArrayList<String> chatContacts = new ArrayList<>();

        for (DataSnapshot data : dataSnapshot.getChildren()) {
            User user = data.getValue(User.class);
            if(phoneNumbers.contains(user.getPhoneNumber())) {
                chatContacts.add(user.getFirstName() + " " + user.getLastName());
                ChatContact chatContact = Converters.userToChatContact(user);
                chatContact.setFirebaseUserId(data.getKey());
                mLocalDatabaseInstance.addOrUpdateChatContact(chatContact);
            }
        }
    }

    private void loadChatContacts() {
        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. In our case, we want a vertical list, so we pass in the constant from the
         * LinearLayoutManager class for vertical lists, LinearLayoutManager.VERTICAL.
         *
         * The third parameter (shouldReverseLayout) should be true if you want to reverse your
         * layout. Generally, this is only true with horizontal lists that need to support a
         * right-to-left layout.
         */
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * Although passing in "this" twice may seem strange, it is actually a sign of separation
         * of concerns, which is best programming practice. The ForecastAdapter requires an
         * Android Context (which all Activities are) as well as an onClickHandler. Since our
         * MainActivity implements the ForecastAdapter ForecastOnClickHandler interface, "this"
         * is also an instance of that type of handler.
         */
        mChatContactsAdapter = new ChatContactsAdapter(this.getContext(), this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mChatContactsAdapter);

        Cursor cursor = mLocalDatabaseInstance.getAllChatContactsCursor();

        mChatContactsAdapter.swapCursor(cursor);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (cursor.getCount() != 0) {
            showChatContactsView();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showChatContactsView() {
        /* First, hide the loading indicator */
        mProgressBar.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
