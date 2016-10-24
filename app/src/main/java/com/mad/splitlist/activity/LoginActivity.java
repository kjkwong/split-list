package com.mad.splitlist.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.mad.splitlist.R;
import com.mad.splitlist.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Allows registered users to log into the Split List app and redirect them to MainActivity.
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.activity_login_til_email)
    TextInputLayout mTilEmail;
    @BindView(R.id.activity_login_til_password)
    TextInputLayout mTilPassword;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Get static reference of Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Clears error messages in the TextInputLayouts after this activity is no longer visible.
     * Useful for cases such as pressing the 'Up' button in RegisterActivity.
     */
    @Override
    protected void onStop() {
        super.onStop();
        clearTextInputLayout();
    }

    /**
     * Validates and authenticates email and password against Firebase Auth. Successful login
     * generates a token that keeps the user logged in.
     */
    @OnClick(R.id.activity_login_btn_login)
    public void login() {
        String email = mTilEmail.getEditText().getText().toString().trim();
        String password = mTilPassword.getEditText().getText().toString();
        User user = new User(email, password);

        if (isValidated(user.getEmail(), user.getPassword())) {
            // Create progress dialog to be shown over LoginActivity
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage(getString(R.string.load_login));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            mFirebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        /**
                         * Checks whether user login matches the credentials from the database.
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgressDialog.dismiss();

                            if (task.isSuccessful()) {
                                startMainActivity();
                            } else {
                                // Throw exceptions and generate error messages associated
                                // with them.
                                Exception authEx = task.getException();
                                String errorMessage = "";

                                try {
                                    throw authEx;
                                } catch (FirebaseAuthInvalidUserException
                                        | FirebaseAuthInvalidCredentialsException e) {
                                    errorMessage = getString
                                            (R.string.error_incorrect_login_details);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Create alert dialog containing error message.
                                AlertDialog.Builder builder = new AlertDialog
                                        .Builder(LoginActivity.this)
                                        .setTitle(R.string.error_login_dialog_title)
                                        .setMessage(errorMessage)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }
                    });
        }
    }

    /**
     * Start intent to go to RegisterActivity.
     */
    @OnClick(R.id.activity_login_tv_create_account)
    public void register() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    /**
     * Start intent to go to MainActivity.
     */
    private void startMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Returns whether the user input is valid so that authentication can begin.
     */
    private boolean isValidated(String email, String password) {
        boolean valid = true;

        if (!isValidEmail(email)) {
            valid = false;
        }

        if (!isValidPassword(password)) {
            valid = false;
        }

        return valid;
    }

    /**
     * Returns whether email input is a valid type and produce
     * the appropriate error messages if necessary.
     */
    private boolean isValidEmail(String email) {
        if (email.isEmpty()) {
            mTilEmail.setError(getString(R.string.error_no_email));
            return false;
        }

        mTilEmail.setErrorEnabled(false);
        return true;
    }

    /**
     * Returns whether password input is a valid type and produce
     * the appropriate error messages if necessary.
     */
    private boolean isValidPassword(String password) {
        if (password.isEmpty()) {
            mTilPassword.setError(getString(R.string.error_no_password));
            return false;
        }

        mTilPassword.setErrorEnabled(false);
        return true;
    }

    /**
     * Clears the error messages of all TextInputLayouts.
     */
    private void clearTextInputLayout() {
        mTilEmail.setErrorEnabled(false);
        mTilPassword.setErrorEnabled(false);
    }
}
