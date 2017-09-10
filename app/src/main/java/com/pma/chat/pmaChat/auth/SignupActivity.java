package com.pma.chat.pmaChat.auth;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.pma.chat.pmaChat.activities.MainActivity;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.model.User;

import java.util.concurrent.TimeUnit;

import com.google.firebase.auth.PhoneAuthProvider;
import com.pma.chat.pmaChat.utils.AppUtils;


public class SignupActivity extends Activity {

    private EditText mNameEditText;
    private EditText mPhoneNumberEditText;
    private Button mSignUpButton;
    private ProgressDialog progressDialog;

    private AuthService mAuthService;

    private Activity mThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mThis = this;

        mAuthService = new AuthServiceImpl();

        mNameEditText = (EditText) findViewById(R.id.txtName);
        mPhoneNumberEditText = (EditText) findViewById(R.id.txtPhoneNumber);
        progressDialog = new ProgressDialog(this);

//        if(AppUtils.isPermissionGrunted(this, Manifest.permission.READ_PHONE_STATE)) {
//            readPhoneNumber();
//            mPhoneNumberEditText.setText(mPhoneNumber);
//            mPhoneNumberEditText.setEnabled(false);
//        } else {
//            AppUtils.gruntPermission(this, android.Manifest.permission.READ_PHONE_STATE, "You need to allow access to Phone Number");
//        }

        mSignUpButton = (Button) findViewById(R.id.btnSignUp);

        mSignUpButton.setOnClickListener(signUpOnClickListener);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case AppUtils.REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // Permission Granted
//                    readPhoneNumber();
//                    mPhoneNumberEditText.setText(mPhoneNumber);
//                    mPhoneNumberEditText.setEnabled(false);
//                } else {
//                    // Permission Denied
//                    Toast.makeText(this, "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
//                            .show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

//    void readPhoneNumber() {
//        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//        mPhoneNumber = telephonyManager.getLine1Number();
//        if(mPhoneNumber.equals("")){
//            Toast.makeText(this, "Set your phone number in Settings -> About Phone -> Status -> My phone Number", Toast.LENGTH_SHORT)
//                    .show();
//            finish();
//        }
//    }

    private View.OnClickListener signUpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String name = mNameEditText.getText().toString().trim();
            String password = mPhoneNumberEditText.getText().toString().trim();

            if (!isFormValid(name, password)) {
                return;
            }

            // TODO find out how to pass resource id
            progressDialog.setMessage("Registering User ...");
            progressDialog.show();

            User userInfo = new User(name, password, null);

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    password,        // Phone number to verify
                    60,    // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    mThis,               // Activity (for callback binding)
                    callbacks);        // OnVerificationStateChangedCallbacks
        }
    };

    @Override
    protected void onStart() {

        super.onStart();

        if (mAuthService.isUserLoggedIn()) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verificaiton without
            //     user action.
            Log.d("AuthService", "onVerificationCompleted:" + credential);

            progressDialog.dismiss();

            Toast.makeText(SignupActivity.this, R.string.successfulRegistrationMessage, Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

            mAuthService.signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w("AuthService", "onVerificationFailed", e);

            progressDialog.dismiss();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d("AuthService", "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mAuthService.setVerificationId(verificationId);
            mAuthService.setResendToken(token);

            // ...
        }
    };

    private boolean isFormValid(String name, String password) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(SignupActivity.this, R.string.nameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this, R.string.passwordFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}