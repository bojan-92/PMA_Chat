package com.pma.chat.pmaChat.data;

import android.provider.BaseColumns;

public class ChatContactContract {

    public static final class ChatContactEntry implements BaseColumns {

        public static final String TABLE_NAME = "chatContact";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_FIREBASE_NAME = "firebaseName";

        public static final String COLUMN_NUMBER = "phoneNumber";

        public static final String COLUMN_FIREBASE_USER_ID = "firebaseUserId";

    }
}
