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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.chat.ChatActivity;
import com.pma.chat.pmaChat.model.Message;
import com.pma.chat.pmaChat.model.UserInfo;

import java.util.ArrayList;

/**
 * Created by david on 4/29/17.
 */

public class FriendsListFragment extends Fragment {

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference userInfoRef = rootRef.child("userInfo");

    ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final FriendsListFragment fragment = this;

        final View view = inflater.inflate(R.layout.users_list, container, false);

        progressDialog = new ProgressDialog(this.getActivity());

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // TODO find out how to pass resource id
                progressDialog.setMessage("Loading Friends");
                progressDialog.show();

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

                progressDialog.hide();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;

    }
}
