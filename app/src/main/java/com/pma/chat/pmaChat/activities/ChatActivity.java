package com.pma.chat.pmaChat.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pma.chat.pmaChat.R;
import com.pma.chat.pmaChat.adapters.ChatMessageListAdapter;
import com.pma.chat.pmaChat.auth.AuthService;
import com.pma.chat.pmaChat.auth.AuthServiceImpl;
import com.pma.chat.pmaChat.auth.LoginActivity;
import com.pma.chat.pmaChat.data.DatabaseHelper;
import com.pma.chat.pmaChat.model.Chat;
import com.pma.chat.pmaChat.model.ChatContact;
import com.pma.chat.pmaChat.model.MapModel;
import com.pma.chat.pmaChat.model.Message;
import com.pma.chat.pmaChat.model.MessageType;
import com.pma.chat.pmaChat.model.User;
import com.pma.chat.pmaChat.notifications.FcmNotificationBuilder;
import com.pma.chat.pmaChat.services.IUserService;
import com.pma.chat.pmaChat.services.UserCallback;
import com.pma.chat.pmaChat.services.UserService;
import com.pma.chat.pmaChat.sync.MyFirebaseService;
import com.pma.chat.pmaChat.utils.AppUtils;
import com.pma.chat.pmaChat.utils.FileUtils;
import com.pma.chat.pmaChat.utils.RemoteConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    // TODO mix : attach and detach database listeners

    private EditText mMessageEditText;
    private Button mSendMessageButton;
    private Button mSendLocationButton;
    private ListView mMessagesListView;
    private Button mCameraButton;
    private Button mMapButton;
    private ImageView mCameraView;
    private Button mCameraVideoButton;
    private Button mSoundRecordingButton;
    private ImageButton mPhotoPickerButton;
    private Button mStickerButton;

    private LatLng latLng;

    private ProgressBar mProgressBar;

    private ChatMessageListAdapter mMessageAdapter;

    private AuthService mAuthService;
    private IUserService mUserService;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference mChatsDatabaseReference;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private DatabaseHelper mLocalDatabaseInstance;

    private String mChatContactFirebaseUserId;

    // file can be photo, video or audio
    private Uri mCurrentFilePath;

    private static final int PLACE_PICKER_REQUEST = 123;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private static final int RC_PHOTO_PICKER = 2;

    private static final int RC_PHOTO_CAPTURE = 3;

    private static final int RC_VIDEO_CAPTURE = 4;

    private static final int RC_AUDIO_CAPTURE = 5;

    private static final int RC_STICKER = 6;

    private static final int LOCATION = 7;

    private File mLocalStorageDir;

    private User currentUser;
    private User otherUser;
    private ChatContact chatContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        chatContact = (ChatContact) intent.getSerializableExtra("CHAT_CONTACT");

        mChatContactFirebaseUserId = chatContact.getFirebaseUserId();

        mLocalDatabaseInstance = DatabaseHelper.getInstance(getApplicationContext());

        mChatsDatabaseReference = MyFirebaseService.getChatsDatabaseReference();

        mAuthService = new AuthServiceImpl();
        mUserService = new UserService();
        mFirebaseAuth = MyFirebaseService.getFirebaseAuthInstance();

        mLocalStorageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        mFirebaseStorage = MyFirebaseService.getFirebaseStorageInstance();

        // Initialize references to views
        mMessageEditText = (EditText) findViewById(R.id.chatMessageField);
        mSendMessageButton = (Button) findViewById(R.id.chatMessageSendBtn);
        mSendLocationButton = (Button) findViewById(R.id.locationSendBtn);
        mMessagesListView = (ListView) findViewById(R.id.chatMessagesList);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mCameraButton = (Button) findViewById(R.id.btnCamera);
        mCameraVideoButton = (Button) findViewById(R.id.btnCameraVideo);
        mMapButton = (Button) findViewById(R.id.btnMap);
        mSoundRecordingButton = (Button) findViewById(R.id.btnSoundRecording);
        mStickerButton = (Button) findViewById(R.id.btnStick);
        // Initialize progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.chatMessagesProgressBar);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // Initialize message ListView and its adapter
        List<Message> messages = new ArrayList<>();
        mMessageAdapter = new ChatMessageListAdapter(this, R.layout.item_chat_message_friend, messages);
        mMessagesListView.setAdapter(mMessageAdapter);

        // Load users - TREBA SACEKATI DA SE UCITAJU, PA TEK ONDA DOZVOLITI SLANJE PORUKE, MRZI ME :D
        mUserService.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserCallback() {
            @Override
            public void notify(List<User> users) {

            }

            @Override
            public void notify(User user) {
                currentUser = user;
            }
        });
        mUserService.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserCallback() {
            @Override
            public void notify(List<User> users) {

            }

            @Override
            public void notify(User user) {
                otherUser = user;
            }
        });


        final String userId = mAuthService.getUserId();

        final Query findChatQuery1 = mChatsDatabaseReference.orderByChild(RemoteConfig.CHAT_UNIQUE_MARK).equalTo(userId + "_" + mChatContactFirebaseUserId);
        final Query findChatQuery2 = mChatsDatabaseReference.orderByChild(RemoteConfig.CHAT_UNIQUE_MARK).equalTo(mChatContactFirebaseUserId + "_" + userId);

        ValueEventListener findChatQuery1ValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataIterator = dataSnapshot.getChildren().iterator();
                if (dataIterator.hasNext()) {
                    mChatDatabaseReference = dataIterator.next().getRef();
                    attachDatabaseReadListenerForMessages();
                } else {
                    ValueEventListener findChatQuery2ValueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> dataIterator = dataSnapshot.getChildren().iterator();
                            if (dataIterator.hasNext()) {
                                mChatDatabaseReference = dataIterator.next().getRef();
                                attachDatabaseReadListenerForMessages();
                            } else {
                                String chatKey = mChatsDatabaseReference.push().getKey();
                                mChatDatabaseReference = mChatsDatabaseReference.child(chatKey);
                                mChatDatabaseReference.child(RemoteConfig.CHAT_UNIQUE_MARK).setValue(userId + "_" + mChatContactFirebaseUserId);

                                Chat chat = new Chat();
                                chat.setChatContactId(chatContact.getId());
                                mLocalDatabaseInstance.addOrUpdateChat(chat);

                                attachDatabaseReadListenerForMessages();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    findChatQuery2.addListenerForSingleValueEvent(findChatQuery2ValueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        findChatQuery1.addListenerForSingleValueEvent(findChatQuery1ValueEventListener);

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationPlacesIntent();
            }
        });

        mStickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), StickerActivity.class), RC_STICKER);
            }
        });

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Enable Send button when there's text to send
                mSendMessageButton.setEnabled(charSequence.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageContent = mMessageEditText.getText().toString();
                Message message = new Message(MessageType.TEXT, messageContent, mAuthService.getUserId(), null, new Date());

                String id = mMessagesDatabaseReference.push().getKey();
                mMessagesDatabaseReference.child(id).setValue(message);

                sendNotification(message);

                mMessageEditText.setText("");
            }
        });

        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeMediaIntent(MediaStore.ACTION_IMAGE_CAPTURE, RC_PHOTO_CAPTURE, "jpg");
            }
        });

        mCameraVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeMediaIntent(MediaStore.ACTION_VIDEO_CAPTURE, RC_VIDEO_CAPTURE, "mp4");
            }
        });

        mSoundRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeMediaIntent(MediaStore.Audio.Media.RECORD_SOUND_ACTION, RC_AUDIO_CAPTURE, "mp3");
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        };

    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == RC_STICKER) {
            String stickerName = data.getExtras().getString("stickerName");

            Message message = new Message(MessageType.STICKER, stickerName, mAuthService.getUserId(), null, new Date());

            String id = mMessagesDatabaseReference.push().getKey();
            mMessagesDatabaseReference.child(id).setValue(message);


        }

        if (requestCode == RC_AUDIO_CAPTURE ||
                requestCode == RC_PHOTO_CAPTURE ||
                requestCode == RC_VIDEO_CAPTURE ||
                requestCode == RC_PHOTO_PICKER) {

            Uri fileUri = requestCode == RC_PHOTO_PICKER ? data.getData() : mCurrentFilePath;

            // Get a reference to store file at photos/<FILENAME>
            StorageReference storageRef = mFirebaseStorage.getReference()
                    .child(getStorageFolderFromRequestCode(requestCode))
                    .child(fileUri.getLastPathSegment());

            // Upload file to Firebase Storage
            storageRef.putFile(fileUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            // Set the download URL to the message box, so that the user can send it to the database
                            Message message = new Message(getMessageTypeFromRequestCode(requestCode), downloadUrl.toString(), mAuthService.getUserId(), null, new Date());

                            String id = mMessagesDatabaseReference.push().getKey();
                            mMessagesDatabaseReference.child(id).setValue(message);
                        }
                    });
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(this, data);
            if (place != null) {
                latLng = place.getLatLng();
                MapModel mapModel = new MapModel(latLng.latitude + "", latLng.longitude + "");
            }
        }

        if (requestCode == LOCATION) {
            mSendLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocationManager mlocManager = null;

                    Location lastLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    String key = mChatsDatabaseReference.push().getKey();
                    Message message = new Message(MessageType.LOCATION, key, mAuthService.getUserId(), null, new Date());

                    String id = mMessagesDatabaseReference.push().getKey();
                    mMessagesDatabaseReference.child(id).setValue(message);
                }
            });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        attachDatabaseReadListenerForMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        mMessageAdapter.clear();
        detachDatabaseReadListenerForMessages();
    }

    private void onSignedInInitialize(String username) {
    }

    private void onSignedOutCleanup() {
        mMessageAdapter.clear();
        detachDatabaseReadListenerForMessages();
    }

    private void attachDatabaseReadListenerForMessages() {
        if(mChatDatabaseReference == null) return;
        mMessagesDatabaseReference = mChatDatabaseReference.child(RemoteConfig.MESSAGE);
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    mMessageAdapter.add(message);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListenerForMessages() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void dispatchTakeMediaIntent(String action, int requestCode, String format) {
        Intent takeMediaIntent = new Intent(action);
        // Ensure that there's a camera activity to handle the intent
        if (takeMediaIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File file = null;
            try {
                file = FileUtils.createFile(mLocalStorageDir, format);
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (file != null) {
                Uri fileURI = FileProvider.getUriForFile(this,
                        AppUtils.FILE_PROVIDER,
                        file);
                mCurrentFilePath = fileURI;
                takeMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileURI);
                startActivityForResult(takeMediaIntent, requestCode);
            }
        }
    }

    private void locationPlacesIntent() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private String getStorageFolderFromRequestCode(int requestCode) {
        switch(requestCode) {
            case RC_PHOTO_CAPTURE : return RemoteConfig.PHOTO_STORAGE;
            case RC_VIDEO_CAPTURE : return RemoteConfig.VIDEO_STORAGE;
            case RC_AUDIO_CAPTURE : return RemoteConfig.AUDIO_STORAGE;
            case LOCATION : return RemoteConfig.LOCATION;
            default: return "";
        }
    }

    private MessageType getMessageTypeFromRequestCode(int requestCode) {
        switch(requestCode) {
            case RC_PHOTO_CAPTURE : return MessageType.PHOTO;
            case RC_VIDEO_CAPTURE : return MessageType.VIDEO;
            case RC_AUDIO_CAPTURE : return MessageType.SOUND;
            case LOCATION : return MessageType.LOCATION;
            default: return MessageType.TEXT;
        }
    }

    private void sendNotification(final Message message){
        FcmNotificationBuilder.initialize()
                .title(currentUser.getName())
                .message(message.getContent())
                .username(currentUser.getName())
                .uid(mChatContactFirebaseUserId)
                .firebaseToken(currentUser.getFcmToken())
                .conversationId(chatContact.getId().toString())
                .receiverFirebaseToken(otherUser.getFcmToken())
                .send();
    }

}
