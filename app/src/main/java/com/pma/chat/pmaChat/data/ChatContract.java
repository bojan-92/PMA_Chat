package com.pma.chat.pmaChat.data;

import android.provider.BaseColumns;

public class ChatContract {

    public static final class ChatEntry implements BaseColumns {

        public static final String TABLE_NAME = "chat";

        public static final String COLUMN_CHAT_CONTACT_ID = "chatContactId";

        public static final String COLUMN_FIREBASE_ID = "firebaseId";

    }
}
