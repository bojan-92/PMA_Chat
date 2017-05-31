package com.pma.chat.pmaChat.auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pma.chat.pmaChat.MainActivity;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.model.UserInfo;

import java.util.Calendar;

/**
 * Created by Bojan on 4/24/2017.
 */

public class SignupScreen extends Activity {

    private UserInfo userInfo;

    private Calendar calendar = Calendar.getInstance();

    private EditText txtFirstName;

    private EditText txtLastName;

    private EditText txtEmail;

    private EditText txtPassword;

    private EditText txtPhoneNumber;

    private EditText txtPasswordConfirm;

    private Button btnDatePicker;

    private EditText txtSelectedDateOfBirth;

    private Button btnSignUp;

    private TextView tvGoToLogin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
    }

    @Override
    protected void onStart() {

        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
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
                Intent i = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(i);
            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(SignupScreen.this, date, calendar.get(Calendar.YEAR),
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
                    Toast.makeText(SignupScreen.this, R.string.passwordAndConfirmationDoNotMatchMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO find out how to pass resource id
                progressDialog.setMessage("Registering User ...");
                progressDialog.show();

                userInfo = new UserInfo(firstName, lastName, phoneNumber, calendar);

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss();

                                if (task.isSuccessful()) {

                                    String userId = firebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference currentUserRef = firebaseDatabaseRef.child("userInfo").child(userId);

                                    currentUserRef.setValue(userInfo);

                                    Toast.makeText(SignupScreen.this, R.string.successfulRegistrationMessage, Toast.LENGTH_SHORT).show();

//                                    firebaseAuth.signOut();
                                } else {
                                    Toast.makeText(SignupScreen.this, R.string.failedRegistrationMessage, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(SignupScreen.this, R.string.emailFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignupScreen.this, R.string.passwordFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(SignupScreen.this, R.string.passwordConfirmFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(SignupScreen.this, R.string.firstNameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(SignupScreen.this, R.string.lastNameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(SignupScreen.this, R.string.lastNameFieldEmptyMessage, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}