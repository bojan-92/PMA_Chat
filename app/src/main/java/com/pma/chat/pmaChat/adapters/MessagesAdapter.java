package com.pma.chat.pmaChat.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.auth.AuthService;
import com.pma.chat.pmaChat.auth.AuthServiceImpl;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.Message;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesAdapterViewHolder> {

    private static final int TYPES_COUNT = 2;
    private static final int TYPE_ME = 0;
    private static final int TYPE_FRIEND = 1;

    private List<Message> mMessages;
    private FirebaseUser mUser;

    private DatabaseReference mUserReference;

    private final Context mContext;
    private final MessagesAdapterOnClickHandler mClickHandler;

    public interface MessagesAdapterOnClickHandler {
        void onClick(Message message);
    }

//    private Cursor mCursor;

    public MessagesAdapter(Context context, MessagesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        AuthService authService = new AuthServiceImpl();
        mUser = authService.getUser();
        mMessages = new ArrayList<>();
    }

    @Override
    public MessagesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view;

        if(viewType == TYPE_ME) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_message, viewGroup, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_message_friend, viewGroup, false);
        }

        view.setFocusable(true);

        return new MessagesAdapter.MessagesAdapterViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MessagesAdapterViewHolder viewHolder, int position) {
        Message message = mMessages.get(position);
        viewHolder.bind(message);
    }

    @Override
    public int getItemCount() {
        if (mMessages == null) return 0;
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        return (mUser.getUid().equals(message.getSenderId())) ? TYPE_ME : TYPE_FRIEND;
    }

    public void swapCursor(List<Message> messages) {
        mMessages = messages;
        notifyDataSetChanged();
    }

    public void addOne(Message message) {
        mMessages.add(message);
        notifyDataSetChanged();
    }

    public void removeAll() {
        mMessages.clear(); //clear list
        notifyDataSetChanged();
    }

    class MessagesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMessageContentImageView;
        private ImageView mMessageArrowPhotoView;

        private VideoView mMessageContentVideoView;
        private ImageView mMessageArrowVideoView;

        private TextView mMessageContentTextView;
        private ImageView mMessageArrowTextView;

        private ImageView mProfilePhotoImageView;


        MessagesAdapterViewHolder(View itemView) {
            super(itemView);

            mMessageContentImageView = (ImageView) itemView.findViewById(R.id.chat_message_photo_content);
            mMessageArrowPhotoView = (ImageView) itemView.findViewById(R.id.chat_message_arrow_photo_content);

            mMessageContentVideoView = (VideoView) itemView.findViewById(R.id.chat_message_video_content);
            mMessageArrowVideoView = (ImageView) itemView.findViewById(R.id.chat_message_arrow_video_content);

            mMessageContentTextView = (TextView) itemView.findViewById(R.id.chat_message_text_content);
            mMessageArrowTextView = (ImageView) itemView.findViewById(R.id.chat_message_arrow_text_content);

            mProfilePhotoImageView = (ImageView) itemView.findViewById(R.id.chat_profile_image);

            itemView.setOnClickListener(this);
        }

        void bind(Message message) {
            mUserReference = MyFirebaseService.getUserDatabaseReferenceById(message.getSenderId());
            mUserReference.addListenerForSingleValueEvent(userInfoValueEventListener);

            if(message.getType() == null) {
                mMessageContentTextView.setVisibility(View.VISIBLE);
                mMessageContentImageView.setVisibility(View.GONE);
                mMessageContentTextView.setText(message.getContent());
            }

            ViewGroup.LayoutParams messageContentImageViewLayoutParams = (ViewGroup.LayoutParams) mMessageContentImageView.getLayoutParams();

            switch(message.getType()) {

                case TEXT:
                    hideAllContentViews();
                    mMessageContentTextView.setVisibility(View.VISIBLE);
                    mMessageArrowTextView.setVisibility(View.VISIBLE);
                    mMessageContentTextView.setText(message.getContent());
                    break;

                case PHOTO:
                    hideAllContentViews();
                    mMessageContentImageView.setVisibility(View.VISIBLE);
                    mMessageArrowPhotoView.setVisibility(View.VISIBLE);
                    Glide.with(mMessageContentImageView.getContext())
                            .load(message.getContent())
                            .into(mMessageContentImageView);
                    messageContentImageViewLayoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    messageContentImageViewLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mMessageContentImageView.setLayoutParams(messageContentImageViewLayoutParams);

                    break;

                case VIDEO:
                    hideAllContentViews();
                    //     mMessageContentVideoView.setVisibility(View.VISIBLE);
                    //     mMessageContentVideoView.setVideoURI(Uri.parse(message.getContent()));
//                mMessageContentTextView.setVisibility(View.VISIBLE);
//                mMessageContentTextView.setText("VIDEO: " + message.getContent());

                    mMessageArrowVideoView.setVisibility(View.VISIBLE);
                    mMessageContentVideoView.setVisibility(View.VISIBLE);
                    mMessageContentVideoView.setVideoURI(Uri.parse(message.getContent()));
                    mMessageContentVideoView.start();
                    break;

                case SOUND:
                    hideAllContentViews();
                    mMessageContentTextView.setVisibility(View.VISIBLE);
                    mMessageContentTextView.setText("SOUND: " + message.getContent());
                    break;

                case LOCATION:
                    hideAllContentViews();

                    break;

                case STICKER:
                    hideAllContentViews();
                    mMessageContentImageView.setVisibility(View.VISIBLE);
                    //  mMessageArrowPhotoView.setVisibility(View.VISIBLE);
                    Resources resources = mContext.getApplicationContext().getResources();
                    final int resourceId = resources.getIdentifier(message.getContent(), "drawable",
                            mContext.getApplicationContext().getApplicationContext().getPackageName());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    mMessageContentImageView.setImageResource(resourceId);
                    messageContentImageViewLayoutParams.width = 200;
                    messageContentImageViewLayoutParams.height = 200;
                    mMessageContentImageView.setLayoutParams(messageContentImageViewLayoutParams);

                    break;
            }
        }

        private void hideAllContentViews() {
            mMessageContentTextView.setVisibility(View.GONE);
            mMessageContentImageView.setVisibility(View.GONE);
            mMessageArrowPhotoView.setVisibility(View.GONE);
            mMessageContentVideoView.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Message message = mMessages.get(adapterPosition);
            mClickHandler.onClick(message);

        }

        private ValueEventListener userInfoValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User userInfo = dataSnapshot.getValue(User.class);

                if(userInfo != null && userInfo.getProfileImageUri() != null) {
                    Glide.with(mProfilePhotoImageView.getContext())
                            .load(userInfo.getProfileImageUri())
                            .apply(RequestOptions.circleCropTransform())
                            .into(mProfilePhotoImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}