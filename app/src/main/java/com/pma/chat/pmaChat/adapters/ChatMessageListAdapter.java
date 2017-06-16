package com.pma.chat.pmaChat.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.model.Message;
import com.pma.chat.pmaChat.model.MessageType;

import java.util.List;

public class ChatMessageListAdapter extends ArrayAdapter<Message> {

    public ChatMessageListAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.chat_messages_list_item, parent, false);
        }

        ImageView messagePhotoImageView = (ImageView) convertView.findViewById(R.id.chatMessagePhoto);
        TextView messageContentTextView = (TextView) convertView.findViewById(R.id.chatMessageContent);
      //  TextView tvMessageAuthor = (TextView) convertView.findViewById(R.id.chatMessageAuthor);

        Message message = getItem(position);

        if(message.getType() == null) {
            messageContentTextView.setVisibility(View.VISIBLE);
            messagePhotoImageView.setVisibility(View.GONE);
            messageContentTextView.setText(message.getContent());
            return convertView;
        }

        switch(message.getType()) {

            case TEXT:
                messageContentTextView.setVisibility(View.VISIBLE);
                messagePhotoImageView.setVisibility(View.GONE);
                messageContentTextView.setText(message.getContent());
                break;

            case PHOTO:
                messageContentTextView.setVisibility(View.GONE);
                messagePhotoImageView.setVisibility(View.VISIBLE);
                Glide.with(messagePhotoImageView.getContext())
                        .load(message.getContent())
                        .into(messagePhotoImageView);
                break;
            case VIDEO:
                break;
            case SOUND:
                break;
            case LOCATION:
                break;
        }
        //   tvMessageAuthor.setText(message.getSenderId().ge);

        return convertView;
    }
}
