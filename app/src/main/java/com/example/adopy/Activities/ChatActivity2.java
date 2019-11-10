package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.adopy.Notifications.APIService;
import com.example.adopy.Notifications.Client;
import com.example.adopy.Notifications.Data;
import com.example.adopy.Notifications.MyResponse;
import com.example.adopy.Notifications.Sender;
import com.example.adopy.Notifications.Token;
import com.example.adopy.R;
import com.example.adopy.UI_utilities.Adapters.MessageAdapter;
import com.example.adopy.Utilities.Models.Chat;
import com.example.adopy.Utilities.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity2 extends AppCompatActivity {

    private static final String TAG = "my_ChatActivity2";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mReference, notifRef;

    MessageAdapter messageAdapter;
    List<Chat> mChatsList;

    RecyclerView recyclerView;

    String userId;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        //toolbar START ------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //toolbar END ------------------------

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        notifRef = FirebaseDatabase.getInstance().getReference("Notifications");
        mReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());
        updateToken();

        //put chatted user in toolbar START ---------------------------------
        Intent intent = getIntent();
        userId = intent.getStringExtra("userid");

        final CircleImageView profile_image = findViewById(R.id.chat_profile_image);
        final TextView username = findViewById(R.id.chat_username);

        mReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //user name
                String userName = user.getUsername();
                username.setText(userName);

                //image
                if (user.getImageUri().equals("default")) {
                    profile_image.setImageResource(R.drawable.user_male);
                    if (user.getGender().equals("Female")) {
                        profile_image.setImageResource(R.drawable.user_female);
                    }
                } else {
                    Glide.with(ChatActivity2.this).load(user.getImageUri()).into(profile_image);
                }

                readMessages(mFirebaseUser.getUid(), userId, user.getImageUri());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //put chatted user in toolbar END ---------------------------------

        ImageButton sendBtn = findViewById(R.id.btn_send);
        final EditText text_send = findViewById(R.id.text_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(mFirebaseUser.getUid(), userId, msg);
                } else {
                    Toast.makeText(ChatActivity2.this, getString(R.string.empty_msg), Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
    }

    private void updateToken() {
        //TODO ?
        final String device_token = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(mFirebaseUser.getUid())
                .child("device_token")
                .setValue(device_token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d(TAG, "updateToken: device_token updated " + device_token);
                    }
                });
    }

    private void sendMessage(String i_Sender, final String i_Receiver, String i_Message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", i_Sender);
        hashMap.put("receiver", i_Receiver);
        hashMap.put("message", i_Message);

        reference.child("Chats").push().setValue(hashMap);


        //notif
        HashMap<String, String> notifHashMap = new HashMap<>();
        notifHashMap.put("from", i_Sender);
        notifHashMap.put("type", "message");

        notifRef.push().child(i_Receiver).setValue(notifHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: msg sent. notif is on it's way");
                        }
                    }
                });

        final String msg = i_Message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(i_Receiver, user.getUsername(), msg);
                    notify = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(mFirebaseUser.getUid(), R.drawable.foot, username + ": " + msg,
                            "New Message", userId);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(ChatActivity2.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(final String i_MyId, final String i_UserId, final String I_ImageUri) {
        mChatsList = new ArrayList<>();
        mReference = FirebaseDatabase.getInstance().getReference("Chats");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChatsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if ((chat.getReceiver().equals(i_MyId) && chat.getSender().equals(i_UserId)) ||
                            (chat.getReceiver().equals(i_UserId) && chat.getSender().equals(i_MyId))) {
                        mChatsList.add(chat);
                    }

                    messageAdapter = new MessageAdapter(ChatActivity2.this, mChatsList, I_ImageUri);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
