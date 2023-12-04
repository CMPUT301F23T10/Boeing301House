package com.example.boeing301house;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.boeing301house.itemlist.ItemListActivity;
import com.google.firebase.firestore.core.Query;

public class LoginActivity extends AppCompatActivity {
    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView redirectTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);
        DBConnection dbConnection = new DBConnection(getApplicationContext(), true);
        // first launch procedure below
        SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // set intent
        final Intent intent = new Intent(LoginActivity.this, ItemListActivity.class);

        setContentView(R.layout.user_login);
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        redirectTV = findViewById(R.id.loginRedirect);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // get string values
                if (!validateUsername() | !validatePassword()){

                } else {
                    // store user data
                    checkUser();

                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    startActivity(intent);
                }
            }
        });

        redirectTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * Validates non empty username
     * @return boolean
     */
    public Boolean validateUsername(){
        String valid = loginUsername.getText().toString();
        if (valid.isEmpty()){
            loginUsername.setError("Username cannot be empty");
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
        String valid = loginPassword.getText().toString();
        if (valid.isEmpty()){
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            return true;
        }
    }
    public void checkUser(){
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();
        // get database ref
    }
}


