package com.pma.chat.pmaChat.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.pma.chat.pmaChat.data.ChatContactContract.ChatContactEntry;
import com.pma.chat.pmaChat.data.ChatContract.ChatEntry;
import com.pma.chat.pmaChat.data.MessageContract.MessageEntry;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.utils.Helpers;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "pmaChat.db";
    private static final int DATABASE_VERSION = 1;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that we
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CHAT_CONTACT_TABLE =
                "CREATE TABLE " + ChatContactEntry.TABLE_NAME + " (" +
                        ChatContactEntry._ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ChatContactEntry.COLUMN_NAME    + " TEXT NOT NULL, " +
                        ChatContactEntry.COLUMN_FIREBASE_NAME    + " TEXT NOT NULL, " +
                        ChatContactEntry.COLUMN_EMAIL   + " TEXT NOT NULL, " +
                        ChatContactEntry.COLUMN_NUMBER  + " TEXT NOT NULL, " +
                        ChatContactEntry.COLUMN_FIREBASE_USER_ID  + " TEXT NOT NULL )";

        final String SQL_CREATE_CHAT_TABLE =
                "CREATE TABLE " + ChatEntry.TABLE_NAME + " (" +
                        ChatEntry._ID                       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ChatEntry.COLUMN_CHAT_CONTACT_ID    + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + (ChatEntry.COLUMN_CHAT_CONTACT_ID) + ") REFERENCES " + ChatContactEntry.TABLE_NAME + "(" + ChatContactEntry._ID + ") " + " )";

//        final String SQL_CREATE_MESSAGE_TABLE =
//                "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
//                        MessageEntry._ID                    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                        MessageEntry.COLUMN_CHAT_CONTACT_ID + " INTEGER NOT NULL, " +
//                        // SQLite does not have a separate Boolean storage class. Instead,
//                        // Boolean values are stored as integers 0 (false) and 1 (true).
//                        MessageEntry.COLUMN_IS_SENDER       + " INTEGER NOT NULL, " +
//                        MessageEntry.COLUMN_TYPE            + " TEXT NOT NULL, " +
//                        MessageEntry.COLUMN_CONTENT         + " TEXT NOT NULL, " +
//                        MessageEntry.COLUMN_TIMESTAMP       + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_CHAT_CONTACT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CHAT_TABLE);
//        sqLiteDatabase.execSQL(SQL_CREATE_MESSAGE_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ChatContactEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long addOrUpdateChatContact(ChatContact chatContact) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ChatContactEntry.COLUMN_NAME, chatContact.getName());
            values.put(ChatContactEntry.COLUMN_FIREBASE_NAME, chatContact.getFirebaseName());
            values.put(ChatContactEntry.COLUMN_EMAIL, chatContact.getEmail());
            values.put(ChatContactEntry.COLUMN_NUMBER, chatContact.getPhoneNumber());
            values.put(ChatContactEntry.COLUMN_FIREBASE_USER_ID, chatContact.getFirebaseUserId());

            // First try to update the user in case the user already exists in the database
            // This assumes phoneNumber are unique
            int rows = db.update(ChatContactEntry.TABLE_NAME, values, ChatContactEntry.COLUMN_NUMBER + "= ?", new String[]{chatContact.getPhoneNumber()});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String contactsSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        ChatContactEntry._ID, ChatContactEntry.TABLE_NAME, ChatContactEntry.COLUMN_NUMBER);
                Cursor cursor = db.rawQuery(contactsSelectQuery, new String[]{String.valueOf(chatContact.getPhoneNumber())});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(ChatContactEntry.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            String s = " dfd ";
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    public Cursor getAllChatContactsCursor() {

        String SELECT_QUERY =
                String.format("SELECT * FROM %s", ChatContactEntry.TABLE_NAME);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);

        return cursor;
    }

    public ChatContact getChatContactById(Long chatContactId) {

        String SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s = ?",
                        ChatContactEntry.TABLE_NAME, ChatContactEntry._ID);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{String.valueOf(chatContactId)});

        try {
            if (cursor.moveToFirst()) {
                ChatContact contact = Helpers.getChatContactFromCursor(cursor);
                return contact;
            }
        } catch (Exception e) {
            //Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public ChatContact getChatContactByFirebaseId(String chatContactFirebaseId) {

        String SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s = ?",
                        ChatContactEntry.TABLE_NAME, ChatContactEntry.COLUMN_FIREBASE_USER_ID);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, new String[]{chatContactFirebaseId});

        try {
            if (cursor.moveToFirst()) {
                ChatContact contact = Helpers.getChatContactFromCursor(cursor);
                return contact;
            }
        } catch (Exception e) {
            //Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public long addOrUpdateChat(Chat chat) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long chatId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(ChatEntry.COLUMN_CHAT_CONTACT_ID, chat.getChatContactId());

            // First try to update the user in case the user already exists in the database
            // This assumes phoneNumber are unique
            int rows = db.update(ChatEntry.TABLE_NAME, values, ChatEntry.COLUMN_CHAT_CONTACT_ID + "= ?", new String[]{String.valueOf(chat.getChatContactId())});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String chatsSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        ChatEntry._ID, ChatEntry.TABLE_NAME, ChatEntry.COLUMN_CHAT_CONTACT_ID);
                Cursor cursor = db.rawQuery(chatsSelectQuery, new String[]{String.valueOf(chat.getChatContactId())});
                try {
                    if (cursor.moveToFirst()) {
                        chatId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                chatId = db.insertOrThrow(ChatEntry.TABLE_NAME, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            // Log.d(TAG, "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return chatId;
    }

}
