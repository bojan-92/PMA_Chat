package com.pma.chat.pmaChat.chat;

import android.app.LauncherActivity.ListItem;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pma.chat.pmaChat.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText txtMessage;

    private Button btnSendMessage;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference messageRef = rootRef.child("message");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);
    }

    protected void onStart() {

        super.onStart();

        txtMessage = (EditText) findViewById(R.id.chatMessageField);
        btnSendMessage = (Button) findViewById(R.id.chatMessageSendBtn);

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> messages = new ArrayList<>();

                for(DataSnapshot data : dataSnapshot.getChildren()) {
                    messages.add(data.getValue(String.class));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ChatActivity.this,
                        R.layout.chat_messages_list_item,
                        R.id.chatMessagesListItem,
                        messages);
                ListView listView = (ListView) findViewById(R.id.chatMessagesList);
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = messageRef.push().getKey();

                String message = txtMessage.getText().toString();

                messageRef.child(id).setValue(message);
            }
        });
    }
}
