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
import com.pma.chat.pmaChat.services.UserServiceImpl;
import com.pma.chat.pmaChat.utils.SharedPrefUtil;


public class LoginActivity extends Activity {

    private EditText txtEmail;
    private EditText txtPassword;
    private Button btnLogin;
    private TextView tvGoToSignUp;
    private ProgressDialog progressDialog;

    private AuthService mAuthService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuthService = new AuthServiceImpl();
    }

    @Override
    protected void onStart() {

        super.onStart();

        if(mAuthService.isUserLoggedIn()){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        btnLogin = (Button) findViewById(R.id.loginBtn);
        tvGoToSignUp = (TextView) findViewById(R.id.tvGoToSignUp);
        txtEmail = (EditText) findViewById(R.id.loginEmail);
        txtPassword = (EditText) findViewById(R.id.loginPassword);
        progressDialog = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        tvGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(i);
            }
        });

    }

    private void userLogin() {

        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(!isFormValid(email, password)) {
            return;
        }

        // TODO find out how to pass resource id
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        mAuthService.loginUser(email, password, new AuthCallback() {
            @Override
            public void notifyUI(boolean result) {
                progressDialog.dismiss();
                if(result) {
                    new UserServiceImpl().setFcmToken(new SharedPrefUtil(getApplicationContext()).getString((User.USER_FCM_TOKEN_FIELD)));
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, R.string.loginFailedMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isFormValid(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, R.string.emailFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, R.string.passwordFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}