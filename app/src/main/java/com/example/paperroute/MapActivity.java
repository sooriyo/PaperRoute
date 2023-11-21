package com.example.paperroute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the status bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        databaseHelper = new DatabaseHelper(this);

        Button showTripButton = findViewById(R.id.showTrip);
        showTripButton.setOnClickListener(v -> drawRoute());

        Button myLocationButton = findViewById(R.id.MyLocation);
        myLocationButton.setOnClickListener(v -> zoomToCurrentLocation());

        Button showOfflineButton = findViewById(R.id.showOffline);
        showOfflineButton.setOnClickListener(v -> showOfflineMap());
    }

    private void showOfflineMap() {
    }
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        try {
            mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        } catch (Exception e) {
            e.printStackTrace();
        }
        LatLng sriLankaLatLng = new LatLng(6.929146, 79.863926);
        float zoomLevel = 13.0f;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sriLankaLatLng)
                .zoom(zoomLevel)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SUBSCRIBER,
                new String[]{DatabaseHelper.COLUMN_LATITUDE, DatabaseHelper.COLUMN_LONGITUDE, DatabaseHelper.COLUMN_NAME,
                        DatabaseHelper.COLUMN_ADDRESS, DatabaseHelper.COLUMN_PHONE, DatabaseHelper.COLUMN_SUBSCRIPTION_END_DATE, DatabaseHelper.COLUMN_NEWSPAPER},
                null, null, null, null, null);


        if (cursor != null && cursor.moveToFirst()) {
            do {
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE));
                String newspaper = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NEWSPAPER));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUBSCRIPTION_END_DATE));

                LatLng location = new LatLng(latitude, longitude);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.location);


                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(icon));

                assert marker != null;
                marker.setTag(new SubscriberDetails(name, address, phone, endDate, newspaper, latitude, longitude));

            } while (cursor.moveToNext());
            cursor.close();
        }

        mMap.setOnMarkerClickListener(marker -> {
            SubscriberDetails subscriberDetails = (SubscriberDetails) marker.getTag();

            if (subscriberDetails != null) {
                showPopupCard(subscriberDetails);
            }

            return true;
        });
    }

    private void showPopupCard(SubscriberDetails subscriberDetails) {
        String name = subscriberDetails.getName();
        String address = subscriberDetails.getAddress();
        String phone = subscriberDetails.getPhone();
        String newspaper = subscriberDetails.getNewspaper();
        String endDate = subscriberDetails.getEndDate();
        double latitude = subscriberDetails.getLatitude();
        double longitude = subscriberDetails.getLongitude();

        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_layout, null);
        TextView nameTextView = dialogView.findViewById(R.id.nameTextView);
        nameTextView.setText(name);
        TextView addressTextView = dialogView.findViewById(R.id.addressTextView);
        addressTextView.setText("Address : " + address);
        TextView newsPaperTextView = dialogView.findViewById(R.id.newsPaperTextView);
        newsPaperTextView.setText("News Paper : " + newspaper);
        TextView endDateTextView = dialogView.findViewById(R.id.endDateTextView);
        endDateTextView.setText("Valid Till : " + endDate);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("Close", null)
                .setNeutralButton("Direction", (dialog, which) -> openNavigation(latitude, longitude))
                .setNegativeButton("Call", (dialog, which) -> makePhoneCall(phone))
                .show();
    }

    private void openNavigation(double latitude, double longitude) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(MapActivity.this, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void zoomToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        // Get the last known location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                float zoomLevel = 15.0f;

                BitmapDescriptor currentLocationIcon = BitmapDescriptorFactory.fromResource(R.drawable.mylocation);
                mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .icon(currentLocationIcon)
                        .title("Current Location"));


                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel));
            } else {
                Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawRoute() {
        // Retrieve the list of points from the database
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_SUBSCRIBER,
                new String[]{DatabaseHelper.COLUMN_LATITUDE, DatabaseHelper.COLUMN_LONGITUDE},
                null, null, null, null, null);

        List<LatLng> points = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE));
                LatLng location = new LatLng(latitude, longitude);
                points.add(location);
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Draw the route on the map
        if (points.size() >= 2) {
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyA0PaClaEpGky5ijLBWc-STxwy51EzbBks")
                    .build();

            DirectionsApiRequest directionsRequest = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(new com.google.maps.model.LatLng(points.get(0).latitude, points.get(0).longitude))
                    .destination(new com.google.maps.model.LatLng(points.get(points.size() - 1).latitude, points.get(points.size() - 1).longitude));

            for (int i = 1; i < points.size() - 1; i++) {
                directionsRequest.waypoints(new com.google.maps.model.LatLng(points.get(i).latitude, points.get(i).longitude));
            }

            try {
                DirectionsResult directionsResult = directionsRequest.await();
                if (directionsResult.routes != null && directionsResult.routes.length > 0) {
                    DirectionsRoute route = directionsResult.routes[0];

                    List<com.google.maps.model.LatLng> path = route.overviewPolyline.decodePath();
                    List<LatLng> decodedPath = new ArrayList<>();

                    for (com.google.maps.model.LatLng latLng : path) {
                        decodedPath.add(new LatLng(latLng.lat, latLng.lng));
                    }

                    mMap.addPolyline(new PolylineOptions().addAll(decodedPath).color(Color.BLUE).width(5));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public static class SubscriberDetails {
        private final String name, address, phone, endDate, newspaper;
        private final double latitude, longitude;

        public SubscriberDetails(String name, String address, String phone, String endDate, String newspaper, double latitude, double longitude) {
            this.name = name;
            this.address = address;
            this.phone = phone;
            this.endDate = endDate;
            this.newspaper = newspaper;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getNewspaper() {
            return newspaper;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}
