package com.example.paperroute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Button btnDownload;
    private TextView completedTextView;
    private TextView endSoonTextView;

    private TextView nextDeliveryNameTextView;
    private TextView updateDateTextView;
    private TextView upcomingTextView;
    private DatabaseHelper databaseHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        int pendingCount = databaseHelper.getDeliveryStatusCount("pending");
        nextDeliveryNameTextView = findViewById(R.id.NextDeliveryName);
        nextDeliveryNameTextView.setText(getString(R.string.upcoming, String.valueOf(pendingCount)));

        btnDownload = findViewById(R.id.btnDownload);
        Button btnDirection = findViewById(R.id.btnDirection);
        updateDateTextView = findViewById(R.id.updateDateTextView);
        nextDeliveryNameTextView = findViewById(R.id.NextDeliveryName);
        upcomingTextView = findViewById(R.id.upcoming);
        completedTextView = findViewById(R.id.completed);
        endSoonTextView = findViewById(R.id.endsoon);

        // Set the initial text of upcomingTextView to "0"
        upcomingTextView.setText("--");


        TextView greetingTextView = findViewById(R.id.goodMorningTextView);

        // Determine the appropriate greeting based on the current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            greetingTextView.setText(getString(R.string.good_morning));
        } else if (hour < 18) {
            greetingTextView.setText(getString(R.string.good_afternoon));
        } else {
            greetingTextView.setText(getString(R.string.good_evening));
        }

        btnDownload.setOnClickListener(v -> {
            new FetchJSONTask().execute("http://10.0.2.2/subscribers.json");
            // Change the endSoonTextView to display "2"
            endSoonTextView.setText("2");
        });
        

        btnDirection.setOnClickListener(v -> {

            // Retrieve the latitude and longitude
            List<Schedule.Subscriber> subscribers = databaseHelper.getAllSubscribers();
            if (!subscribers.isEmpty()) {
                Schedule.Subscriber firstSubscriber = subscribers.get(0);
                double latitude = firstSubscriber.getLatitude();
                double longitude = firstSubscriber.getLongitude();

                // Launch Google Maps with the specified location
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });

        CardView scheduleCard = findViewById(R.id.ScheduleCard);
        scheduleCard.setOnClickListener(v -> AnimationUtil.animateCardView(scheduleCard, () -> {
            Intent intent = new Intent(MainActivity.this, Schedule.class);
            startActivity(intent);
        }));

        CardView mapCard = findViewById(R.id.MapCard);
        mapCard.setOnClickListener(v -> AnimationUtil.animateCardView(mapCard, () -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        }));
        CardView upcomingCardView = findViewById(R.id.upcomingCardView);
        upcomingCardView.setOnClickListener(v -> AnimationUtil.animateCardView(upcomingCardView, () -> {
            Intent intent = new Intent(MainActivity.this, UpcomingDelivery.class);
            startActivity(intent);
        }));
        CardView completedCardView = findViewById(R.id.completedCardView);
        completedCardView.setOnClickListener(v -> AnimationUtil.animateCardView(completedCardView, () -> {
            Intent intent = new Intent(MainActivity.this, CompletedDeliveries.class);
            startActivity(intent);
        }));
        ImageView profileImageView = findViewById(R.id.profileButton);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
        CardView endingCard = findViewById(R.id.EndingCard);
        endingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the toast message
                Toast.makeText(getApplicationContext(), "Renewal Notification Sent to Subscribers", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);

        // Add menu items programmatically

        popupMenu.getMenu().add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "Upload To Server");
        popupMenu.getMenu().add(Menu.NONE, Menu.FIRST, Menu.NONE, "Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case Menu.FIRST:
                        performLogout();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void performLogout() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void navigateToLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    private class FetchJSONTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String url = urls[0];
            String result = "";

            try {
                URL urlObject = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                result = stringBuilder.toString();

                reader.close();
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() > 0) {
                    // Clear existing data in the table
                    databaseHelper.clearSubscriberTable();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int subscriber_id = jsonObject.getInt("subscriber_id");
                        String name = jsonObject.getString("name");
                        String city = jsonObject.getString("city");
                        String address = jsonObject.getString("address");
                        String phone = jsonObject.getString("phone");
                        double latitude = jsonObject.getDouble("latitude");
                        double longitude = jsonObject.getDouble("longitude");
                        String deliveryStatus = jsonObject.optString("delivery_status");
                        String subscriptionEndDate = jsonObject.optString("subscription_end_date");
                        String newspaper = jsonObject.optString("newspaper");
                        String subscriptionMode = jsonObject.optString("subscription_mode");

                        databaseHelper.insertSubscriber(subscriber_id, name, city, address, phone, latitude,
                                longitude, deliveryStatus, subscriptionEndDate, newspaper, subscriptionMode);
                    }

                    JSONObject firstSubscriber = jsonArray.getJSONObject(0);
                    String name = firstSubscriber.getString("name");
                    String city = firstSubscriber.getString("city");
                    nextDeliveryNameTextView.setText(getString(R.string.user_name, name) + " | " + city);

                    // Get the count of records with COLUMN_DELIVERY_STATUS equal to "pending"
                    int pendingCount = getPendingDeliveryCount();
                    upcomingTextView.setText(String.valueOf(pendingCount));

                    // Update the completedTextView with the completed count
                    int completedCount = getCompletedDeliveryCount();
                    completedTextView.setText(String.valueOf(completedCount));

                    btnDownload.setCompoundDrawables(null, null, null, null);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String currentDate = sdf.format(new Date());
                    updateDateTextView.setText(getString(R.string.last_updated, currentDate));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            btnDownload.setText(R.string.update_again);
            Toast.makeText(MainActivity.this, R.string.schedule_updated, Toast.LENGTH_SHORT).show();
        }

    }
    private int getPendingDeliveryCount() {
        return databaseHelper.getDeliveryStatusCount("pending");

    }
    private int getCompletedDeliveryCount() {
        return databaseHelper.getDeliveryStatusCount("completed");
    }


}
