package com.example.paperroute;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginDatabase LoginDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Hide the status bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginDatabase = new LoginDatabase(this);

        EditText distributorIdEditText = findViewById(R.id.edit_text_distributor_id);
        EditText passwordEditText = findViewById(R.id.edit_text_password);
        Button loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String distributorId = distributorIdEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (isValidCredentials(distributorId, password)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Username or Password Is Incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidCredentials(String distributorId, String password) {

        SQLiteDatabase db = LoginDatabase.getReadableDatabase();

        String[] projection = {DatabaseHelper.COLUMN_DISTRIBUTOR_ID};

        String selection = DatabaseHelper.COLUMN_DISTRIBUTOR_ID + " = ? AND " +
                DatabaseHelper.COLUMN_PASSWORD + " = ?";

        String[] selectionArgs = {distributorId, password};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_DISTRIBUTORS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isValid = cursor.moveToFirst();
        cursor.close();
        db.close();

        return isValid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginDatabase.close();
    }
    private void navigateToLoginScreen() {
        // Implement your navigation logic here to go back to the login screen
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: Call finish() to close the current activity if needed
    }

}
