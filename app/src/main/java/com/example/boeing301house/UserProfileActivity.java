package com.example.boeing301house;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Subclass of {@link AppCompatActivity}.
 * Activity for displaying profile.
 */
public class UserProfileActivity extends AppCompatActivity {
    TextView userName;
    MaterialButton editUsernameBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        MaterialToolbar topbar = findViewById(R.id.UserProfileTopBar);
        setSupportActionBar(topbar);

        userName = findViewById(R.id.userNameTextView);
        editUsernameBtn = findViewById(R.id.editUserNameButton);

        editUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogview = LayoutInflater.from(UserProfileActivity.this).inflate(R.layout.edit_username_dialog, null);
//                TextInputEditText editText = findViewById(R.id.userNameDialogEditText);

                AlertDialog alertDialog = new MaterialAlertDialogBuilder(UserProfileActivity.this)
                        .setTitle("Change username")
                        .setView(dialogview)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO fix edit text : currently breaking when setting text
                                TextInputLayout usernameInput = dialogview.findViewById(R.id.userNameDialogInputLayout);
                                userName.setText(String.format(Locale.CANADA, "%s", Objects.requireNonNull(usernameInput.getEditText().getText().toString())));
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                alertDialog.show();
            }
        });

    }
    /**
     * Inflate menu items in app bar
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ab_user_profile_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This handles option selection in UserProfileActivity
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.closeViewButton) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChangeUsername(){


    }
}
