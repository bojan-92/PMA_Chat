package com.pma.chat.pmaChat.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.data.PhoneContactListProvider;

import java.util.ArrayList;


public class ChatContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUserInfoDatabaseReference = mRootDatabaseReference.child("userInfo");

    private ProgressBar mProgressBar;

    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    private final static int[] TO_IDS = {
            R.id.tv_contact_name
    };

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;


    private SimpleCursorAdapter mChatContactsCursor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final ChatContactListFragment fragment = this;

        final View view = inflater.inflate(R.layout.users_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.usersListProgressBar);

        PhoneContactListProvider contactListProvider = new PhoneContactListProvider(getActivity());

        ArrayList contacts = contactListProvider.fetchAll();

        Log.i("TEST", contacts.toString());
/*
        mUserInfoDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressBar.setVisibility(ProgressBar.VISIBLE);

                ArrayList<String> users = new ArrayList<>();

                for(DataSnapshot data : dataSnapshot.getChildren()) {
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
*/


        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Always call the super method first
        super.onActivityCreated(savedInstanceState);

        ListView listView = (ListView) getActivity().findViewById(R.id.friendsList);

        mChatContactsCursor = new SimpleCursorAdapter(
                getActivity(),
                R.layout.user_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);

        listView.setAdapter(mChatContactsCursor);

        getLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        Uri contentUri = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_FILTER_URI,
                Uri.encode(""));

        return new CursorLoader(
                getActivity(),
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mChatContactsCursor.swapCursor(data);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mChatContactsCursor.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
