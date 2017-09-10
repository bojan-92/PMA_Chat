package com.pma.chat.pmaChat.utils;

import android.database.Cursor;

import com.pma.chat.pmaChat.data.ChatContactContract;
import com.pma.chat.pmaChat.data.ChatContract;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.ChatContact;

public class Helpers {

    public static ChatContact getChatContactFromCursor(Cursor cursor) {
        ChatContact chatContact = new ChatContact();
        chatContact.setId(cursor.getLong(cursor.getColumnIndex(ChatContactContract.ChatContactEntry._ID)));
        chatContact.setName(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_NAME)));
        chatContact.setFirebaseName(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_FIREBASE_NAME)));
        chatContact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_NUMBER)));
        chatContact.setFirebaseUserId(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_FIREBASE_USER_ID)));
        return chatContact;
    }

    public static Chat getChatFromCursor(Cursor cursor) {
        Chat chat = new Chat();
        chat.setId(cursor.getLong(cursor.getColumnIndex(ChatContract.ChatEntry._ID)));
        chat.setChatContactId(cursor.getLong(cursor.getColumnIndex(ChatContract.ChatEntry.COLUMN_CHAT_CONTACT_ID)));
        chat.setFirebaseId(cursor.getString(cursor.getColumnIndex(ChatContract.ChatEntry.COLUMN_FIREBASE_ID)));
        return chat;
    }
}
