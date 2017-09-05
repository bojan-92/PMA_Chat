package com.pma.chat.pmaChat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.data.ChatContract;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.ChatContact;

/**
 * Created by Mix on 9/4/17.
 */

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

        Long chatContactId = mCursor.getLong(mCursor.getColumnIndex(ChatContract.ChatEntry.COLUMN_CHAT_CONTACT_ID));
        // find chat contact by id
        ChatContact chatContact = mLocalDatabaseInstance.getChatContactById(chatContactId);

        String name = chatContact.getName();
        viewHolder.nameTextView.setText(name);
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

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);


        }
    }
}