package com.pma.chat.pmaChat.auth;


import com.google.firebase.auth.FirebaseUser;
import com.pma.chat.pmaChat.model.User;

public interface AuthService {

    boolean isUserLoggedIn();

    FirebaseUser getUser();

    String getUserId();

    void loginUser(String email, String password, final AuthCallback callback);

    void registerUser(String email, String password, final User userInfo, final AuthCallback callback);

    void logoutUser();
}
