package com.pma.chat.pmaChat.utils;

import android.database.Cursor;

import com.pma.chat.pmaChat.data.ChatContactContract;
import com.pma.chat.pmaChat.model.ChatContact;

/**
 * Created by Mix on 9/8/17.
 */

public class Helpers {

    public static ChatContact getChatContactFromCursor(Cursor cursor) {
        ChatContact chatContact = new ChatContact();
        chatContact.setId(cursor.getLong(cursor.getColumnIndex(ChatContactContract.ChatContactEntry._ID)));
        chatContact.setName(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_NAME)));
        chatContact.setFirebaseName(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_FIREBASE_NAME)));
        chatContact.setEmail(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_EMAIL)));
        chatContact.setPhoneNumber(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_NUMBER)));
        chatContact.setFirebaseUserId(cursor.getString(cursor.getColumnIndex(ChatContactContract.ChatContactEntry.COLUMN_FIREBASE_USER_ID)));
        return chatContact;
    }
}
