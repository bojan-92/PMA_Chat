package com.pma.chat.pmaChat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.Message;

import java.util.List;

/**
 * Created by Mix on 9/4/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ChatMessagesAdapterViewHolder> {

    private static final int TYPES_COUNT = 2;
    private static final int TYPE_ME = 0;
    private static final int TYPE_FRIEND = 1;

    private List<Message> mMessages;

    private final Context mContext;

    private FirebaseUser mUser;

    private final ChatMessagesAdapterOnClickHandler mClickHandler;

    public interface ChatMessagesAdapterOnClickHandler {
        void onClick(Chat chat);
    }

    private Cursor mCursor;

    public MessagesAdapter(Context context, ChatMessagesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        return (mUser.getUid().equals(message.getSenderId())) ? TYPE_ME : TYPE_FRIEND;
    }


    @Override
    public ChatMessagesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;

        if(viewType == TYPE_ME) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_message, viewGroup, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_message_friend, viewGroup, false);
        }

        view.setFocusable(true);

        return new MessagesAdapter.ChatMessagesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessagesAdapterViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

//        Long chatContactId = mCursor.getLong(mCursor.getColumnIndex(ChatContract.ChatEntry.COLUMN_CHAT_CONTACT_ID));
//        // find chat contact by id
//        ChatContact chatContact = mLocalDatabaseInstance.getChatContactById(chatContactId);

//        String name = chatContact.getName();
//        viewHolder.nameTextView.setText(name);
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

    class ChatMessagesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMessageContentImageView;
        private ImageView mMessageArrowPhotoView;

        private VideoView mMessageContentVideoView;
        private ImageView mMessageArrowVideoView;

        private TextView mMessageContentTextView;
        private ImageView mMessageArrowTextView;


        ChatMessagesAdapterViewHolder(View itemView) {
            super(itemView);

            mMessageContentImageView = (ImageView) itemView.findViewById(R.id.chat_message_photo_content);
            mMessageArrowPhotoView = (ImageView) itemView.findViewById(R.id.chat_message_arrow_photo_content);

            mMessageContentVideoView = (VideoView) itemView.findViewById(R.id.chat_message_video_content);
            mMessageArrowVideoView = (ImageView) itemView.findViewById(R.id.chat_message_arrow_video_content);

            mMessageContentTextView = (TextView) itemView.findViewById(R.id.chat_message_text_content);
            mMessageArrowTextView = (ImageView) itemView.findViewById(R.id.chat_message_arrow_text_content);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);


        }
    }
}