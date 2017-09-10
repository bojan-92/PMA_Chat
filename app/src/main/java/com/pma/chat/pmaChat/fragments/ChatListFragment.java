package com.pma.chat.pmaChat.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.pma.chat.pmaChat.activities.ChatActivity;
import com.pma.chat.pmaChat.adapters.ChatsAdapter;
import com.pma.chat.pmaChat.auth.AuthService;
import com.pma.chat.pmaChat.auth.AuthServiceImpl;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.Converters;

public class ChatListFragment extends Fragment implements
        ChatsAdapter.ChatsAdapterOnClickHandler {

    private DatabaseHelper mLocalDatabaseInstance;

    private DatabaseReference mChatsDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;

    private AuthService mAuthService;

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private ChatsAdapter mChatsAdapter;
    private int mPosition = RecyclerView.NO_POSITION;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mLocalDatabaseInstance = DatabaseHelper.getInstance(getActivity().getApplicationContext());

        mAuthService = new AuthServiceImpl();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_chats);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_chats);
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        mChatsDatabaseReference = MyFirebaseService.getChatsDatabaseReference();
        mUsersDatabaseReference = MyFirebaseService.getUsersDatabaseReference();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mChatsDatabaseReference.addValueEventListener(chatsValueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mChatsDatabaseReference.removeEventListener(chatsValueEventListener);
    }

    private void updateChats(DataSnapshot dataSnapshot) {

        final String currentUserId = mAuthService.getUserId();

        for(DataSnapshot data : dataSnapshot.getChildren()) {
            final String chatId = data.getKey();
            final String userIdsPair = data.child("user1_user2").getValue(String.class);
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
                            chat.setFirebaseId(chatId);
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

    private void loadChats() {

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mChatsAdapter = new ChatsAdapter(this.getContext(), this);

        mRecyclerView.setAdapter(mChatsAdapter);

        Cursor cursor = mLocalDatabaseInstance.getAllChatsCursor();

        mChatsAdapter.swapCursor(cursor);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (cursor.getCount() != 0) {
            showChatsView();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(Chat chat) {
        //ChatContact chatContact = mLocalDatabaseInstance.getChatContactById(chat.getChatContactId());
        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
        //chatIntent.putExtra("CHAT_CONTACT", chatContact);
        chatIntent.putExtra("CHAT_ID", chat.getFirebaseId());
        startActivity(chatIntent);
    }

    private void showChatsView() {
        /* First, hide the loading indicator */
        mProgressBar.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private ValueEventListener chatsValueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            updateChats(dataSnapshot);

            loadChats();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // if there is no internet, just load chats from local database
            //         loadChats();
        }
    };
}


