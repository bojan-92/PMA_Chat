package com.pma.chat.pmaChat.notifications;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.services.UserService;
import com.pma.chat.pmaChat.utils.SharedPrefUtil;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        new SharedPrefUtil(getApplicationContext()).saveString(User.USER_FCM_TOKEN_FIELD, token);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            new UserService().setFcmToken(token);
        }
    }
}