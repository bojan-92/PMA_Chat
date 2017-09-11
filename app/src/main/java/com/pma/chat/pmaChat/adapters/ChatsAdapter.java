package com.pma.chat.pmaChat.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.activities.ChatActivity;
import com.pma.chat.pmaChat.data.ChatContract;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.Helpers;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsAdapterViewHolder> {

    private DatabaseHelper mLocalDatabaseInstance;

    private final Context mContext;

    private final ChatsAdapterOnClickHandler mClickHandler;

    public interface ChatsAdapterOnClickHandler {
        void onClick(Chat chat);
    }

    private Cursor mCursor;

    public ChatsAdapter(Context context, ChatsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mLocalDatabaseInstance = DatabaseHelper.getInstance(context.getApplicationContext());
    }

    @Override
    public ChatsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId = R.layout.item_chat;

        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);

        view.setFocusable(true);

        return new ChatsAdapter.ChatsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatsAdapterViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);
        Chat chat = Helpers.getChatFromCursor(mCursor);
        viewHolder.bind(chat);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class ChatsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView profilePhotoImageView;
        final TextView nameTextView;
        final TextView statusTextView;

        ChatsAdapterViewHolder(View itemView) {
            super(itemView);

            profilePhotoImageView = (ImageView) itemView.findViewById(R.id.iv_profile_photo);
            nameTextView = (TextView) itemView.findViewById(R.id.tv_name);
            statusTextView = (TextView) itemView.findViewById(R.id.tv_status);

            itemView.setOnClickListener(this);
        }

        void bind(Chat chat) {
            ChatContact chatContact = mLocalDatabaseInstance.getChatContactById(chat.getChatContactId());
            nameTextView.setText(chatContact.getName());
            DatabaseReference chatContactRef = MyFirebaseService.getUserDatabaseReferenceById(chatContact.getFirebaseUserId());
            chatContactRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User userInfo = dataSnapshot.getValue(User.class);

                    if(userInfo != null && userInfo.getProfileImageUri() != null) {
                        Glide.with(profilePhotoImageView.getContext())
                                .load(userInfo.getProfileImageUri())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePhotoImageView);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            statusTextView.setText("Offline");
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            Chat chat = Helpers.getChatFromCursor(mCursor);
            mClickHandler.onClick(chat);
        }
    }
}