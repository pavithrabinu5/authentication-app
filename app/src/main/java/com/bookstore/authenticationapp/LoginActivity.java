package com.bookstore.authenticationapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvForgotPassword, tvSignUp;
    private ImageView ivTogglePassword;
    private boolean isPasswordVisible = false;

    private UserManager userManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UserManager and SharedPreferences
        userManager = new UserManager(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // Check if user credentials are saved
        loadSavedCredentials();

        // Set up password toggle
        ivTogglePassword.setOnClickListener(view -> togglePasswordVisibility());

        // Login button click listener
        btnLogin.setOnClickListener(view -> handleLogin());

        // Forgot password click listener
        tvForgotPassword.setOnClickListener(view -> {
            Toast.makeText(LoginActivity.this, "Password recovery feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Sign up link click listener
        tvSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            isPasswordVisible = false;
        } else {
            // Show password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            isPasswordVisible = true;
        }
        // Move cursor to end of text
        etPassword.setSelection(etPassword.getText().length());
    }

    private void loadSavedCredentials() {
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Verify credentials
        if (userManager.verifyUser(username, password)) {
            // Save credentials if "Remember Me" is checked
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (cbRememberMe.isChecked()) {
                editor.putBoolean("rememberMe", true);
                editor.putString("username", username);
                editor.putString("password", password);
            } else {
                editor.clear();
            }
            editor.apply();

            // Show success toast
            Toast.makeText(this, "Login successful! Welcome back.", Toast.LENGTH_SHORT).show();

            // Navigate to Welcome Activity
            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
}