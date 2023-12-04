package com.example.boeing301house;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boeing301house.itemlist.ItemListActivity;

import java.util.Locale;
import java.util.Objects;

/**
 * Main Activity
 * Source code for Main Activity (currently dedicated to login)
 */
public class MainActivity extends AppCompatActivity {
    EditText signUpUsername, signUpPassword;
    Button signupButton;
    TextView redirectTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // first launch procedure below
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // for testing
        editor.putBoolean("firststart", true);
        editor.commit();
        // set intent
        final Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
        // update firstart value in pref to test functionality
        if(pref.getBoolean("firststart",true)){
            setContentView(R.layout.user_sign_up);
            signUpUsername = findViewById(R.id.signUp_username);
            signUpPassword = findViewById(R.id.signUp_password);
            signupButton = findViewById(R.id.signUp_button);
            redirectTV = findViewById(R.id.signUpRedirect);
            // first launch procedure below (login screen)

            editor.putBoolean("light", true);
            editor.commit();
            signupButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // get string values
                    if (!validateUsername() | !validatePassword()){

                    } else {
                        // store user data
                        String username = signUpUsername.getText().toString();
                        String password = signUpPassword.getText().toString();
                        DBConnection dbConnection = new DBConnection(getApplicationContext());
                        dbConnection.storeLogin(getApplicationContext(), password, username);

                        Toast.makeText(MainActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();

                        startActivity(intent);
                    }
                }
            });
            redirectTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            if (!pref.getBoolean("light", true)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            startActivity(intent);
        }
    }
    /**
     * Validates non empty username
     * @return boolean
     */
    public Boolean validateUsername(){
        String valid = signUpUsername.getText().toString();
        if (valid.isEmpty()){
            signUpUsername.setError("Username cannot be empty");
            return false;
        } else {
            return true;
        }
    }
    /**
     * Validates non empty password
     * @return boolean
     */
    public Boolean validatePassword(){
        String valid = signUpPassword.getText().toString();
        if (valid.isEmpty()){
            signUpPassword.setError("Password cannot be empty");
            return false;
        } else {
            return true;
        }
    }}