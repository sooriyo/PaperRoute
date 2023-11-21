package com.example.paperroute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PaperRouteNew.db";
    public static final int DATABASE_VERSION = 1;

    // Subscriber table columns
    public static final String TABLE_SUBSCRIBER = "subscriber";
    public static final String COLUMN_SUBSCRIBER_ID = "subscriber_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_DELIVERY_STATUS = "delivery_status";
    public static final String COLUMN_SUBSCRIPTION_END_DATE = "subscription_end_date";
    public static final String COLUMN_NEWSPAPER = "newspaper";
    public static final String COLUMN_SUBSCRIPTION_MODE = "subscription_mode";

    public static final String TABLE_DISTRIBUTORS = "distributors";
    public static final String COLUMN_DISTRIBUTOR_ID = "distributor_id";
    public static final String COLUMN_PASSWORD = "password";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the subscriber table
        String createSubscriberTableQuery = "CREATE TABLE " + TABLE_SUBSCRIBER + " (" +
                COLUMN_SUBSCRIBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_CITY + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_DELIVERY_STATUS + " TEXT, " +
                COLUMN_SUBSCRIPTION_END_DATE + " TEXT, " +
                COLUMN_NEWSPAPER + " TEXT, " +
                COLUMN_SUBSCRIPTION_MODE + " TEXT)";

        db.execSQL(createSubscriberTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing tables and recreate them
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIBER);
        onCreate(db);
    }

    public void clearSubscriberTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SUBSCRIBER, null, null);
        db.close();
    }

    public void insertSubscriber(int subscriber_id, String name, String city, String address, String phone,
                                 double latitude, double longitude, String deliveryStatus,
                                 String subscriptionEndDate, String newspaper, String subscriptionMode) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBSCRIBER_ID, subscriber_id);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_DELIVERY_STATUS, deliveryStatus);
        values.put(COLUMN_SUBSCRIPTION_END_DATE, subscriptionEndDate);
        values.put(COLUMN_NEWSPAPER, newspaper);
        values.put(COLUMN_SUBSCRIPTION_MODE, subscriptionMode);

        db.insert(TABLE_SUBSCRIBER, null, values);
        db.close();
    }

    public List<Schedule.Subscriber> getAllSubscribers() {
        List<Schedule.Subscriber> subscribers = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_SUBSCRIBER_ID,
                COLUMN_NAME,
                COLUMN_CITY,
                COLUMN_ADDRESS,
                COLUMN_PHONE,
                COLUMN_LATITUDE,
                COLUMN_LONGITUDE
        };

        Cursor cursor = db.query(
                TABLE_SUBSCRIBER,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int subscriberId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIBER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));

                Schedule.Subscriber subscriber = new Schedule.Subscriber(subscriberId, name, city, address, phone, latitude, longitude);
                subscribers.add(subscriber);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return subscribers;
    }

    public int getDeliveryStatusCount(String deliveryStatus) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {COLUMN_SUBSCRIBER_ID};

        String selection = COLUMN_DELIVERY_STATUS + " = ?";
        String[] selectionArgs = {deliveryStatus};

        Cursor cursor = db.query(
                TABLE_SUBSCRIBER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int count = cursor != null ? cursor.getCount() : 0;

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return count;
    }

    public List<Schedule.Subscriber> getSubscribersWithPendingDelivery() {
        List<Schedule.Subscriber> subscribers = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_SUBSCRIBER_ID,
                COLUMN_NAME,
                COLUMN_CITY,
                COLUMN_ADDRESS,
                COLUMN_PHONE,
                COLUMN_LATITUDE,
                COLUMN_LONGITUDE
        };

        String selection = COLUMN_DELIVERY_STATUS + " = ?";
        String[] selectionArgs = {"pending"};

        Cursor cursor = db.query(
                TABLE_SUBSCRIBER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int subscriberId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIBER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));

                Schedule.Subscriber subscriber = new Schedule.Subscriber(subscriberId, name, city, address, phone, latitude, longitude);
                subscribers.add(subscriber);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return subscribers;
    }

    public List<Schedule.Subscriber> getCompletedSubscribers() {
        List<Schedule.Subscriber> subscribers = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                COLUMN_SUBSCRIBER_ID,
                COLUMN_NAME,
                COLUMN_CITY,
                COLUMN_ADDRESS,
                COLUMN_PHONE,
                COLUMN_LATITUDE,
                COLUMN_LONGITUDE
        };

        String selection = COLUMN_DELIVERY_STATUS + " = ?";
        String[] selectionArgs = {"completed"};

        Cursor cursor = db.query(
                TABLE_SUBSCRIBER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int subscriberId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBSCRIBER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE));

                Schedule.Subscriber subscriber = new Schedule.Subscriber(subscriberId, name, city, address, phone, latitude, longitude);
                subscribers.add(subscriber);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return subscribers;
    }
}
