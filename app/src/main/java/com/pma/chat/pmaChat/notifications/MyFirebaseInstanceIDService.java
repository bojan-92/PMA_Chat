package com.pma.chat.pmaChat.notifications;

import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.services.UserService;
import com.pma.chat.pmaChat.utils.SharedPrefUtil;
import com.pma.chat.pmaChat.R;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    NotificationCompat.Builder builder =
            (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle("Notifications Example")
                    .setContentText("This is a test notification");
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        builder.setSound(alarmSound);
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