package com.example.paperroute;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.paperroute.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Schedule extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
            actionBar.setTitle("");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Schedule.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        databaseHelper = new DatabaseHelper(this);

        List<Subscriber> subscriberList = databaseHelper.getAllSubscribers();

        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        for (Subscriber subscriber : subscriberList) {
            String name = subscriber.getName();
            String city = subscriber.getCity();
            String address = subscriber.getAddress();
            String phone = subscriber.getPhone();
            double latitude = subscriber.getLatitude();
            double longitude = subscriber.getLongitude();

            // Create a new CardView for each subscriber
            View cardView = getLayoutInflater().inflate(R.layout.subscriber_card_layout, containerLayout, false);

            // Set the subscriber data to the corresponding views in the CardView
            TextView nameTextView = cardView.findViewById(R.id.nameTextView);
            TextView cityTextView = cardView.findViewById(R.id.cityTextView);
            TextView addressTextView = cardView.findViewById(R.id.addressTextView);
            TextView phoneTextView = cardView.findViewById(R.id.phoneTextView);
            Button directionButton = cardView.findViewById(R.id.directionButton);
            Button deliverdButton = cardView.findViewById(R.id.deliverdButton);

            nameTextView.setText(name);
            cityTextView.setText(city);
            addressTextView.setText(address);
            phoneTextView.setText(phone);

            // Set OnClickListener for Direction button
            directionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Launch Google Maps with the specified location
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });

            // Set OnClickListener for Deliverd button
            deliverdButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update the delivery status to "completed" in the database
                    updateDeliveryStatus(subscriber.getSubscriberId());

                    // Perform any additional actions after updating the delivery status

                    // For example, remove the cardView from the containerLayout
                    containerLayout.removeView(cardView);
                }
            });

            // Add the CardView to the container layout
            containerLayout.addView(cardView);
        }
    }

    private void updateDeliveryStatus(int subscriberId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DELIVERY_STATUS, "completed");

        String selection = DatabaseHelper.COLUMN_SUBSCRIBER_ID + " = ?";
        String[] selectionArgs = { String.valueOf(subscriberId) };

        int count = db.update(DatabaseHelper.TABLE_SUBSCRIBER, values, selection, selectionArgs);

        if (count > 0) {
            // Update successful
            // Perform any additional actions or show a success message
        } else {
            // Update failed
            // Perform any error handling or show an error message
        }

        db.close();
    }

    public static class Subscriber {
        private int subscriberId;
        private String name;
        private String city;
        private String address;
        private String phone;
        private double latitude;
        private double longitude;

        public Subscriber(int subscriberId, String name, String city, String address, String phone,
                          double latitude, double longitude) {
            this.subscriberId = subscriberId;
            this.name = name;
            this.city = city;
            this.address = address;
            this.phone = phone;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Subscriber() {

        }

        public int getSubscriberId() {
            return subscriberId;
        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        public String getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setSubscriberId(int anInt) {
        }
    }

}
