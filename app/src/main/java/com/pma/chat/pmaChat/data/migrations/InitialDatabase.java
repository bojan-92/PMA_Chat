package com.pma.chat.pmaChat.data.migrations;

import android.database.sqlite.SQLiteDatabase;


public class InitialDatabase extends Migration {

    public InitialDatabase(SQLiteDatabase database) {
        super(database);
    }

    @Override
    public void up() {
       //  getDatabase().execSQL(
       //         "CREATE TABLE my_model (_id INTEGER PRIMARY KEY AUTOINCREMENT);");
    }

    @Override
    public void down() {
      //  getDatabase().execSQL("DROP TABLE my_model;");
    }

}