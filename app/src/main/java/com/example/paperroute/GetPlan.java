package com.example.paperroute;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Create an AsyncTask for downloading the delivery plan file
class GetPlan extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... voids) {
        // URL of the delivery plan file on the server
        String deliveryPlanUrl = "http://example.com/delivery_plan.json";

        try {
            // Create a URL object and establish a connection
            URL url = new URL(deliveryPlanUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Read the response from the server
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                return stringBuilder.toString();
            } else {
                Log.e("GetPlan", "Error response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e("GetPlan", "IOException: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String deliveryPlanJson) {
        if (deliveryPlanJson != null) {
            // Parse the JSON and save the data to the database
            // Update the UI with the delivery plan details
        } else {
            // Handle the error case
        }

    }

}
