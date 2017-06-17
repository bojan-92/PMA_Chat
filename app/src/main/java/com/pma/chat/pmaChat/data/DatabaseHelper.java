package com.pma.chat.pmaChat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pma.chat.pmaChat.data.ChatContactContract.ChatContactEntry;
import com.pma.chat.pmaChat.data.MessageContract.MessageEntry;


public class DatabaseHelper extends SQLiteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "pmaChat.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CHAT_CONTACT_TABLE =
                "CREATE TABLE " + ChatContactEntry.TABLE_NAME + " (" +
                        ChatContactEntry._ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ChatContactEntry.COLUMN_NAME    + " TEXT NOT NULL, " +
                        ChatContactEntry.COLUMN_NUMBER  + " TEXT NOT NULL )" ;

        final String SQL_CREATE_MESSAGE_TABLE =
                "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
                        MessageEntry._ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MessageEntry.COLUMN_CHAT_CONTACT_ID + " INTEGER NOT NULL, " +
                        // SQLite does not have a separate Boolean storage class. Instead,
                        // Boolean values are stored as integers 0 (false) and 1 (true).
                        MessageEntry.COLUMN_IS_SENDER       + " INTEGER NOT NULL, " +
                        MessageEntry.COLUMN_TYPE            + " TEXT NOT NULL, " +
                        MessageEntry.COLUMN_CONTENT         + " TEXT NOT NULL, " +
                        MessageEntry.COLUMN_TIMESTAMP       + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_CHAT_CONTACT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MESSAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ChatContactEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
