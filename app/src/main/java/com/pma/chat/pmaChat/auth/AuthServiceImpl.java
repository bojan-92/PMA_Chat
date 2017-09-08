package com.pma.chat.pmaChat.auth;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.utils.RemoteConfig;

public class AuthServiceImpl implements AuthService {

    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mFirebaseRootDatabaseRef;


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
}
