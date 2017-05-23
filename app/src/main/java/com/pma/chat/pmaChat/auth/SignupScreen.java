package com.pma.chat.pmaChat.auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private EditText txtUsername;

    private EditText txtPassword;

    private EditText txtPasswordConfirm;

    private Button btnDatePicker;

    private EditText dateSelected;

    private Button btnSignUp;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        btnDatePicker = (Button) findViewById(R.id.datePicker);
        dateSelected = (EditText) findViewById(R.id.dateSelected);

        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtUsername = (EditText) findViewById(R.id.txtSignUpEmail);
        txtPassword = (EditText) findViewById(R.id.txtSignUpPassword);
        txtPasswordConfirm = (EditText) findViewById(R.id.txtSignUpPasswordConfirm);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignupScreen.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!txtPassword.getText().toString().equals(txtPasswordConfirm.getText().toString())) {
                    Toast.makeText(SignupScreen.this, "Password and Password Confirm do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Registering User ...");
                progressDialog.show();

                final String username = txtUsername.getText().toString();
                final String password = txtPassword.getText().toString();

                userInfo = getFormData();

                firebaseAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressDialog.dismiss();

                                if (task.isSuccessful()) {

                                    String userId = firebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference currentUserRef = firebaseDatabaseRef.child("userInfo").child(userId);

                                    currentUserRef.setValue(userInfo);

                                    Toast.makeText(SignupScreen.this, "Successful Registration", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(SignupScreen.this, "Registration Failed", Toast.LENGTH_SHORT).show();
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
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //    dateSelected.setText(dayOfMonth + '/' + monthOfYear + '/' + year);
        }
    };

    private UserInfo getFormData() {
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(txtFirstName.getText().toString().trim());
        userInfo.setLastName(txtLastName.getText().toString().trim());
        //  userInfo.setBirthday(calendar);
        return userInfo;

    }

}