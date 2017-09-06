package com.pma.chat.pmaChat.auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pma.chat.pmaChat.activities.MainActivity;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.model.User;

import java.util.Calendar;


public class SignupActivity extends Activity {

    private User userInfo;

    private Calendar calendar = Calendar.getInstance();

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtPasswordConfirm;
    private EditText txtPhoneNumber;
    private EditText txtSelectedDateOfBirth;
    private Button btnDatePicker;
    private Button btnSignUp;
    private TextView tvGoToLogin;
    private ProgressDialog progressDialog;

    private AuthService mAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuthService = new AuthServiceImpl();
    }

    @Override
    protected void onStart() {

        super.onStart();

        if(mAuthService.isUserLoggedIn()) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtEmail = (EditText) findViewById(R.id.txtSignUpEmail);
        txtPassword = (EditText) findViewById(R.id.txtSignUpPassword);
        txtPasswordConfirm = (EditText) findViewById(R.id.txtSignUpPasswordConfirm);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);

        btnDatePicker = (Button) findViewById(R.id.datePicker);
        txtSelectedDateOfBirth = (EditText) findViewById(R.id.txtSelectedDateOfBirth);
        txtSelectedDateOfBirth.setEnabled(false);

        tvGoToLogin = (TextView) findViewById(R.id.tvSignUpBackToLogin);

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(SignupActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.show();
            }
        });

        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String passwordConfirm = txtPasswordConfirm.getText().toString().trim();
                String firstName = txtFirstName.getText().toString().trim();
                String lastName = txtLastName.getText().toString().trim();
                String phoneNumber = txtPhoneNumber.getText().toString().trim();

                if(!isFormValid(email, password, passwordConfirm, firstName, lastName, phoneNumber)) {
                    return;
                }

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(SignupActivity.this, R.string.passwordAndConfirmationDoNotMatchMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO find out how to pass resource id
                progressDialog.setMessage("Registering User ...");
                progressDialog.show();

                userInfo = new User(firstName, lastName, phoneNumber);

                mAuthService.registerUser(email, password, userInfo, new    AuthCallback() {
                    @Override
                    public void notifyUI(boolean result) {
                        progressDialog.dismiss();
                        if(result) {
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

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            view.setMaxDate(System.currentTimeMillis());
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            txtSelectedDateOfBirth.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(year));
            txtSelectedDateOfBirth.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    };

    private boolean isFormValid(String email, String password, String passwordConfirm, String firstName, String lastName, String phoneNumber) {
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
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(SignupActivity.this, R.string.firstNameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(SignupActivity.this, R.string.lastNameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(SignupActivity.this, R.string.phoneNumberFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}