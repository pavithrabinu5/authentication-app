package com.bookstore.authenticationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etCustomGender, etUsername, etPassword, etReenterPassword;
    private Spinner spinnerYearOfBirth;
    private RadioGroup rgGender;
    private RadioButton rbOther;
    private Button btnRegister, btnClearForm;
    private TextView tvSignIn;
    private ImageView ivTogglePassword, ivToggleReenterPassword;
    private boolean isPasswordVisible = false;
    private boolean isReenterPasswordVisible = false;

    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UserManager
        userManager = new UserManager(this);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etCustomGender = findViewById(R.id.etCustomGender);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etReenterPassword = findViewById(R.id.etReenterPassword);
        spinnerYearOfBirth = findViewById(R.id.spinnerYearOfBirth);
        rgGender = findViewById(R.id.rgGender);
        rbOther = findViewById(R.id.rbOther);
        btnRegister = findViewById(R.id.btnRegister);
        btnClearForm = findViewById(R.id.btnClearForm);
        tvSignIn = findViewById(R.id.tvSignIn);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleReenterPassword = findViewById(R.id.ivToggleReenterPassword);

        // Setup year spinner
        setupYearSpinner();

        // Handle custom gender input visibility
        rgGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbOther) {
                etCustomGender.setVisibility(View.VISIBLE);
            } else {
                etCustomGender.setVisibility(View.GONE);
                etCustomGender.setText("");
            }
        });

        // Password toggle listeners
        ivTogglePassword.setOnClickListener(view -> togglePasswordVisibility(etPassword, ivTogglePassword, true));
        ivToggleReenterPassword.setOnClickListener(view -> togglePasswordVisibility(etReenterPassword, ivToggleReenterPassword, false));

        // Register button click listener
        btnRegister.setOnClickListener(view -> handleRegistration());

        // Clear form button click listener
        btnClearForm.setOnClickListener(view -> clearForm());

        // Sign in link click listener
        tvSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupYearSpinner() {
        // Create a list of years from current year down to 1900
        List<String> years = new ArrayList<>();
        years.add("Select Year"); // Default/hint option

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = currentYear; year >= 1900; year--) {
            years.add(String.valueOf(year));
        }

        // Create adapter and set it to spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYearOfBirth.setAdapter(adapter);
    }

    private void togglePasswordVisibility(EditText editText, ImageView imageView, boolean isMainPassword) {
        if (isMainPassword) {
            if (isPasswordVisible) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imageView.setImageResource(android.R.drawable.ic_menu_view);
                isPasswordVisible = false;
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                isPasswordVisible = true;
            }
            editText.setSelection(editText.getText().length());
        } else {
            if (isReenterPasswordVisible) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imageView.setImageResource(android.R.drawable.ic_menu_view);
                isReenterPasswordVisible = false;
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                isReenterPasswordVisible = true;
            }
            editText.setSelection(editText.getText().length());
        }
    }

    private void handleRegistration() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String reenterPassword = etReenterPassword.getText().toString().trim();

        // Get selected year
        String yearOfBirth = spinnerYearOfBirth.getSelectedItem().toString();

        // Get selected gender
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";

        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRadioButton = findViewById(selectedGenderId);
        if (selectedRadioButton.getId() == R.id.rbOther) {
            gender = etCustomGender.getText().toString().trim();
            if (gender.isEmpty()) {
                etCustomGender.setError("Please specify your gender");
                etCustomGender.requestFocus();
                return;
            }
        } else {
            gender = selectedRadioButton.getText().toString();
        }

        // Validate all inputs
        if (fullName.isEmpty()) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        // Validate year of birth
        if (yearOfBirth.equals("Select Year")) {
            Toast.makeText(this, "Please select your year of birth", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate age (must be at least 13 years old)
        int year = Integer.parseInt(yearOfBirth);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (currentYear - year < 13) {
            Toast.makeText(this, "You must be at least 13 years old to register", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 4) {
            etUsername.setError("Username must be at least 4 characters");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (reenterPassword.isEmpty()) {
            etReenterPassword.setError("Please re-enter your password");
            etReenterPassword.requestFocus();
            return;
        }

        if (!password.equals(reenterPassword)) {
            etReenterPassword.setError("Passwords do not match");
            etReenterPassword.requestFocus();
            return;
        }

        // Check if username already exists
        if (userManager.userExists(username)) {
            Toast.makeText(this, "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
            etUsername.requestFocus();
            return;
        }

        // Register the user
        boolean success = userManager.registerUser(username, password, fullName, gender, yearOfBirth);

        if (success) {
            Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_LONG).show();

            // Navigate to login screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etFullName.setText("");
        etCustomGender.setText("");
        etUsername.setText("");
        etPassword.setText("");
        etReenterPassword.setText("");
        rgGender.clearCheck();
        spinnerYearOfBirth.setSelection(0); // Reset to "Select Year"
        etCustomGender.setVisibility(View.GONE);
        etFullName.requestFocus();
        Toast.makeText(this, "Form cleared", Toast.LENGTH_SHORT).show();
    }
}