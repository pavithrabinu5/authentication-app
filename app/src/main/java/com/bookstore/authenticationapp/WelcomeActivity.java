package com.bookstore.authenticationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize UserManager
        userManager = new UserManager(this);

        // Initialize views
        TextView tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle);
        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvUsername = findViewById(R.id.tvUsername);
        TextView tvGender = findViewById(R.id.tvGender);
        TextView tvYearOfBirth = findViewById(R.id.tvYearOfBirth);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Get username from intent
        String username = getIntent().getStringExtra("username");

        if (username != null && !username.isEmpty()) {
            // Retrieve user details
            UserManager.User user = userManager.getUser(username);

            if (user != null) {
                // Set user details to views
                tvWelcomeTitle.setText("Welcome " + user.fullName + "!");
                tvFullName.setText(user.fullName);
                tvUsername.setText(user.username);
                tvGender.setText(user.gender);
                tvYearOfBirth.setText(user.yearOfBirth);
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }

        // Logout button click listener
        btnLogout.setOnClickListener(view -> {
            // Clear "Remember Me" preferences
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(WelcomeActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Navigate back to login screen
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Handle back button press using OnBackPressedCallback (Modern approach)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Disable back button to prevent going back to login after successful login
                Toast.makeText(WelcomeActivity.this, "Please use the logout button to exit", Toast.LENGTH_SHORT).show();
            }
        });
    }
}