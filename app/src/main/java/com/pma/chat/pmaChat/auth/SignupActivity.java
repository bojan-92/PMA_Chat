package com.pma.chat.pmaChat.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pma.chat.pmaChat.activities.MainActivity;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.services.UserService;
import com.pma.chat.pmaChat.utils.SharedPrefUtil;


public class SignupActivity extends Activity {

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private EditText mPhoneNumberEditText;
    private Button mSignUpButton;
    private TextView mGoToLoginTextView;
    private ProgressDialog progressDialog;

    private User userInfo;

    private AuthService mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuthService = new AuthServiceImpl();

        mNameEditText = (EditText) findViewById(R.id.txtName);
        mEmailEditText = (EditText) findViewById(R.id.txtSignUpEmail);
        mPasswordEditText = (EditText) findViewById(R.id.txtSignUpPassword);
        mPasswordConfirmEditText = (EditText) findViewById(R.id.txtSignUpPasswordConfirm);
        mPhoneNumberEditText = (EditText) findViewById(R.id.txtPhoneNumber);
        progressDialog = new ProgressDialog(this);

        mGoToLoginTextView = (TextView) findViewById(R.id.tvSignUpBackToLogin);

        mGoToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        mSignUpButton = (Button) findViewById(R.id.btnSignUp);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mNameEditText.getText().toString().trim();
                String email = mEmailEditText.getText().toString().trim();
                String password = mPasswordEditText.getText().toString().trim();
                String passwordConfirm = mPasswordConfirmEditText.getText().toString().trim();
                String phoneNumber = mPhoneNumberEditText.getText().toString().trim();

                if(!isFormValid(name, email, password, passwordConfirm, phoneNumber)) {
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(SignupActivity.this, R.string.passwordAndConfirmationDoNotMatchMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO find out how to pass resource id
                progressDialog.setMessage("Registering User ...");
                progressDialog.show();

                userInfo = new User(name, email, phoneNumber, null);

                mAuthService.registerUser(email, password, userInfo, new    AuthCallback() {
                    @Override
                    public void notifyUI(boolean result) {
                        progressDialog.dismiss();
                        if(result) {
                            new UserService().setFcmToken(new SharedPrefUtil(getApplicationContext()).getString((User.USER_FCM_TOKEN_FIELD)));
                            Toast.makeText(SignupActivity.this, R.string.successfulRegistrationMessage, Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(SignupActivity.this, R.string.failedRegistrationMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();

        if(mAuthService.isUserLoggedIn()) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }

    private boolean isFormValid(String name, String email, String password, String passwordConfirm, String phoneNumber) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(SignupActivity.this, R.string.nameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignupActivity.this, R.string.emailFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this, R.string.passwordFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(SignupActivity.this, R.string.passwordConfirmFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(SignupActivity.this, R.string.phoneNumberFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}