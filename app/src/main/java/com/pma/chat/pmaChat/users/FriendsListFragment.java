package com.pma.chat.pmaChat.users;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.chat.ChatActivity;
import com.pma.chat.pmaChat.model.UserInfo;

import java.util.ArrayList;

/**
 * Created by david on 4/29/17.
 */

public class FriendsListFragment extends Fragment {

    DatabaseReference mRootDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserInfoDatabaseReference = mRootDatabaseReference.child("userInfo");

    ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final FriendsListFragment fragment = this;

        final View view = inflater.inflate(R.layout.users_list, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.usersListProgressBar);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

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
                        R.id.friendsListItem,
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
}
