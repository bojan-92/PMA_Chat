package com.pma.chat.pmaChat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.auth.AuthService;
import com.pma.chat.pmaChat.auth.AuthServiceImpl;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.Converters;

public class ChatListFragment extends Fragment {

    private DatabaseHelper mLocalDatabaseInstance;

    private DatabaseReference mChatsDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;

    private AuthService mAuthService;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mLocalDatabaseInstance = DatabaseHelper.getInstance(getActivity().getApplicationContext());

        mAuthService = new AuthServiceImpl();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_chats);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_chats);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        final String currentUserId = mAuthService.getUserId();

        mChatsDatabaseReference = MyFirebaseService.getChatsDatabaseReference();
        mUsersDatabaseReference = MyFirebaseService.getUsersDatabaseReference();

        mChatsDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    String userIdsPair = data.child("user1_user2").getValue(String.class);
                    String[] chatIdParts = userIdsPair.split("_");
                    String userId1 = chatIdParts[0];
                    String userId2 = chatIdParts[1];
                    if(userId1.equals(currentUserId) || userId2.equals(currentUserId)) {
                        String chatContactFirebaseId = userId1.equals(currentUserId) ? userId2 : userId1;
                        ChatContact chatContact = mLocalDatabaseInstance.getChatContactByFirebaseId(chatContactFirebaseId);
                        if(chatContact == null) {
                            mUsersDatabaseReference.child(chatContactFirebaseId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    ChatContact chatContact = Converters.userToChatContact(user);
                                    mLocalDatabaseInstance.addOrUpdateChatContact(chatContact);
                                    Chat chat = new Chat();
                                    chat.setChatContactId(chatContact.getId());
                                    mLocalDatabaseInstance.addOrUpdateChat(chat);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Chat chat = new Chat();
                            chat.setChatContactId(chatContact.getId());
                            mLocalDatabaseInstance.addOrUpdateChat(chat);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
             //   Toast.makeText(getContext(), " ", Toast.LENGTH_SHORT);
            }
        });

        return view;
    }
}


