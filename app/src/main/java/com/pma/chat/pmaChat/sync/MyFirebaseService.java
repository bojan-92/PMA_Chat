package com.pma.chat.pmaChat.sync;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pma.chat.pmaChat.utils.RemoteConfig;

public class MyFirebaseService {

    public MyFirebaseService() {
    }

    public static FirebaseAuth getFirebaseAuthInstance() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseStorage getFirebaseStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    public static StorageReference getUsersProfileImagesStorageReference() {
        return FirebaseStorage.getInstance().getReference().child(RemoteConfig.USERS_PROFILE_PHOTOS_STORAGE);
    }

    public static DatabaseReference getUsersDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child(RemoteConfig.USER);
    }

    public static DatabaseReference getCurrentUserDatabaseReference() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return FirebaseDatabase.getInstance().getReference().child(RemoteConfig.USER).child(userId);
    }

    public static DatabaseReference getUserDatabaseReferenceById(String userId) {
        return FirebaseDatabase.getInstance().getReference().child(RemoteConfig.USER).child(userId);
    }

    public static DatabaseReference getChatsDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child(RemoteConfig.CHAT);
    }

    public static DatabaseReference getChatDatabaseReferenceById(String chatId) {
        return FirebaseDatabase.getInstance().getReference().child(RemoteConfig.CHAT).child(chatId);
    }

}
