package com.pma.chat.pmaChat.data;

import android.provider.BaseColumns;

/**
 * Created by Mix on 9/4/17.
 */

public class ChatContract {

    public static final class ChatEntry implements BaseColumns {

        public static final String TABLE_NAME = "chat";

        public static final String COLUMN_CHAT_CONTACT_ID = "chatContactId";

        public static final String COLUMN_FIREBASE_CHAT_ID = "firebaseChatId";
    }
}
