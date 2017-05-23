package com.pma.chat.pmaChat.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pma.chat.pmaChat.MainActivity;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.menu.HomeActivity;

/**
 * Created by Bojan on 4/23/2017.
 */

public class LoginScreen extends Activity{

    EditText txtUsername;
    EditText txtPassword;
    Button btnLogin;
    Button btnSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        /*if(firebaseAuth.getCurrentUser() != null){

        }*/

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin = (Button) findViewById(R.id.loginBtn);
        btnSignUp = (Button) findViewById(R.id.signupBtn);
        txtUsername = (EditText)findViewById(R.id.loginUsername);
        txtPassword = (EditText)findViewById(R.id.loginPassword);
        progressDialog = new ProgressDialog(this);

       btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        btnSignUp = (Button)findViewById(R.id.signupBtn);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SignupScreen.class);
                startActivity(i);
            }
        });

    }

    private void userLogin(){
        String username = txtUsername.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(LoginScreen.this, "Username is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginScreen.this, "Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(username,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(LoginScreen.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}