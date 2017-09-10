package com.pma.chat.pmaChat.auth;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pma.chat.pmaChat.model.User;

public interface AuthService {

    boolean isUserLoggedIn();

    FirebaseUser getUser();

    String getUserId();

    void loginUser(String email, String password, final AuthCallback callback);

    void registerUser(String email, String password, final User userInfo, final AuthCallback callback);

    void signInWithPhoneAuthCredential(PhoneAuthCredential credential);

    void logoutUser();

    PhoneAuthProvider.OnVerificationStateChangedCallbacks getAuthCallbacks();

    String getVerificationId();

    void setVerificationId(String verificationId);

    PhoneAuthProvider.ForceResendingToken getResendToken();

    void setResendToken(PhoneAuthProvider.ForceResendingToken resendToken);
}
