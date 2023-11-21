package com.example.paperroute;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CompletedDeliveries extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_schedule);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);
            actionBar.setTitle("Completed Deliveries");
        }

        databaseHelper = new DatabaseHelper(this);

        List<Schedule.Subscriber> subscriberList = databaseHelper.getCompletedSubscribers();
        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        for (Schedule.Subscriber subscriber : subscriberList) {
            String name = subscriber.getName();
            String city = subscriber.getCity();
            String address = subscriber.getAddress();
            String phone = subscriber.getPhone();

            // Create a new CardView for each subscriber
            View cardView = getLayoutInflater().inflate(R.layout.activity_completed_deliveries, containerLayout, false);

            // Set the subscriber data to the corresponding views in the CardView
            TextView nameTextView = cardView.findViewById(R.id.nameTextView);
            TextView cityTextView = cardView.findViewById(R.id.cityTextView);
            TextView addressTextView = cardView.findViewById(R.id.addressTextView);
            TextView phoneTextView = cardView.findViewById(R.id.phoneTextView);


            nameTextView.setText(name);
            cityTextView.setText(city);
            addressTextView.setText(address);
            phoneTextView.setText(phone);


            // Add the CardView to the container LinearLayout
            containerLayout.addView(cardView);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
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

        public void setSubscriberId(int subscriberId) {
            this.subscriberId = subscriberId;
        }
    }
}
