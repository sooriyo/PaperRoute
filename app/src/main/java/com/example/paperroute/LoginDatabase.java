package com.example.paperroute;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoginDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Login.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    public static final String TABLE_DISTRIBUTORS = "distributors";
    public static final String COLUMN_DISTRIBUTOR_ID = "distributor_id";
    public static final String COLUMN_PASSWORD = "password";

    public LoginDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the distributors table
        String createTableQuery = "CREATE TABLE " + TABLE_DISTRIBUTORS + " (" +
                COLUMN_DISTRIBUTOR_ID + " TEXT PRIMARY KEY," +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTableQuery);



        // Insert login data
        insertDistributor(db, "admin", "admin");
        insertDistributor(db, "a", "a");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISTRIBUTORS);
        onCreate(db);
    }

    private void insertDistributor(SQLiteDatabase db, String distributorId, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISTRIBUTOR_ID, distributorId);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_DISTRIBUTORS, null, values);

    }
}