package com.pma.chat.pmaChat.auth;


import com.pma.chat.pmaChat.model.User;

public interface AuthService {

    boolean isUserLoggedIn();

    String getUserId();

    void loginUser(String email, String password, final AuthCallback callback);

    void registerUser(String email, String password, final User userInfo, final AuthCallback callback);
}
