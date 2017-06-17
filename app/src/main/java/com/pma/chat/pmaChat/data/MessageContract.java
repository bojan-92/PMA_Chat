package com.pma.chat.pmaChat.data;


import android.provider.BaseColumns;

public class MessageContract {

    public static final class MessageEntry implements BaseColumns {

        public static final String TABLE_NAME = "message";

        public static final String COLUMN_CHAT_CONTACT_ID = "chatContact";

        public static final String COLUMN_IS_SENDER = "isSender";

        public static final String COLUMN_TYPE = "type";

        public static final String COLUMN_CONTENT = "content";

        public static final String COLUMN_TIMESTAMP = "timestamp";

    }
}
