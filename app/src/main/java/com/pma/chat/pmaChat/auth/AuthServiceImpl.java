package com.pma.chat.pmaChat.auth;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.utils.RemoteConfig;

import java.util.concurrent.Executor;

public class AuthServiceImpl implements AuthService {

    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mFirebaseRootDatabaseRef;

    private static String mVerificationId;

    private static PhoneAuthProvider.ForceResendingToken mResendToken;


    public AuthServiceImpl() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRootDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean isUserLoggedIn() {

        return mFirebaseAuth.getCurrentUser() != null;
    }

    @Override
    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public String getUserId() {

        return mFirebaseAuth.getCurrentUser().getUid();
    }

    @Override
    public void loginUser(String email, String password, final AuthCallback callback) {

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        callback.notifyUI(task.isSuccessful());
                    }
                });
    }

    @Override
    public void registerUser(String email, String password, final User user, final AuthCallback callback) {

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            String userId = mFirebaseAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserRef = mFirebaseRootDatabaseRef.child(RemoteConfig.USER).child(userId);

                            currentUserRef.setValue(user, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    callback.notifyUI(databaseError == null);
                                }
                            });

                        } else {
                            callback.notifyUI(false);
                        }
                    }
                });
    }

    @Override
    public void logoutUser() {
        mFirebaseAuth.signOut();
    }

    public void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AuthService", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("AuthService", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks getAuthCallbacks() {

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

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("AuthService", "onVerificationFailed", e);

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
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };

        return callbacks;
    }

    public String getVerificationId() {
        return mVerificationId;
    }

    public void setVerificationId(String verificationId) {
        mVerificationId = verificationId;
    }

    public PhoneAuthProvider.ForceResendingToken getResendToken() {
        return mResendToken;
    }

    public void setResendToken(PhoneAuthProvider.ForceResendingToken resendToken) {
        mResendToken = resendToken;
    }
}
