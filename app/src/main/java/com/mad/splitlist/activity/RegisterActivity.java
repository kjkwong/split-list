package com.mad.splitlist.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.mad.splitlist.R;
import com.mad.splitlist.data.FirebaseRegister;
import com.mad.splitlist.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mad.splitlist.util.Utility.isValidEmailFormat;

/**
 * Allows users to register themselves to the SplitList database, which they can then use to login
 * to the application.
 */
public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.activity_register_til_full_name)
    TextInputLayout mTilFullName;
    @BindView(R.id.activity_register_til_email)
    TextInputLayout mTilEmail;
    @BindView(R.id.activity_register_til_password)
    TextInputLayout mTilPassword;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        // Get static reference of FirebaseAuth.
        mFirebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Goes back to LoginActivity
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Validates and attempts to register email and password to Firebase Auth. Redirects user to
     * MainActivity after successful registration.
     */
    @OnClick(R.id.activity_register_btn_signup)
    public void signUp() {
        String name = mTilFullName.getEditText().getText().toString().trim();
        String email = mTilEmail.getEditText().getText().toString().trim();
        String password = mTilPassword.getEditText().getText().toString().trim();
        final User user = new User(name, email, password);

        if (isValidated(user.getName(), user.getEmail(), user.getPassword())) {
            // Create progress dialog to be shown over LoginActivity
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            mProgressDialog.setMessage(getString(R.string.load_register));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            mFirebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(RegisterActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        // Write new user into Firebase using FirebaseRegister.
                                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                        FirebaseRegister firebaseRegister =
                                                new FirebaseRegister(firebaseUser);
                                        firebaseRegister.createNewUser(user);
                                        startMainActivity();
                                    } else {
                                        mProgressDialog.dismiss();

                                        // Throw exceptions and generate error messages associated
                                        // with them.
                                        Exception authEx = task.getException();

                                        try {
                                            throw authEx;
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            mTilEmail.setError
                                                    (getString(R.string.error_email_in_use));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        // Create alert dialog containing error message.
                                        AlertDialog.Builder builder = new AlertDialog
                                                .Builder(RegisterActivity.this)
                                                .setMessage(authEx.getMessage())
                                                .setTitle(R.string.error_register_dialog_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
        }
    }

    /**
     * Start intent to go to MainActivity.
     */
    private void startMainActivity() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Returns whether the user input is valid so that registration can begin.
     */
    private boolean isValidated(String name, String email, String password) {
        boolean valid = true;

        if (!isValidFullName(name)) {
            valid = false;
        }

        if (!isValidEmail(email)) {
            valid = false;
        }

        if (!isValidPassword(password)) {
            valid = false;
        }

        return valid;
    }

    /**
     * Returns whether full name input is a valid type and produce the appropriate error messages
     * if necessary.
     */
    private boolean isValidFullName(String name) {
        if (name.isEmpty()) {
            mTilFullName.setError(getString(R.string.error_required_name));
            return false;
        }

        mTilFullName.setError(null);
        return true;
    }

    /**
     * Returns whether email input is a valid type and produce the appropriate error messages if
     * necessary.
     */
    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            mTilEmail.setError(getString(R.string.error_required_email));
            return false;
        } else if (!isValidEmailFormat(email)) {
            mTilEmail.setError(getString(R.string.error_invalid_format_email));
            return false;
        }

        mTilEmail.setError(null);
        return true;
    }

    /**
     * Returns whether password input is a valid type and produce the appropriate error messages if
     * necessary.
     */
    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            mTilPassword.setError(getString(R.string.error_required_password));
            return false;
        } else if (password.length() < 6) {
            mTilPassword.setError(getString(R.string.error_short_password));
            return false;
        }

        mTilPassword.setError(null);
        return true;
    }
}
