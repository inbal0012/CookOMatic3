package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.adopy.R;
import com.example.adopy.UI_utilities.Adapters.UserAdapter;
import com.example.adopy.Utilities.Models.Chat;
import com.example.adopy.Utilities.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsersList;

    FirebaseUser mFirebaseUser;
    DatabaseReference reference;

    private List<String> userList;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        recyclerView = findViewById(R.id.chats_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getSender().equals(mFirebaseUser.getUid())) {
                        userList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(mFirebaseUser.getUid())) {
                        userList.add(chat.getSender());
                    }
                }
                
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readChats() {
        mUsersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsersList.clear();

                //display 1 user from chats
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    for (String id : userList) {
                        if (user.getId().equals(id)) {
                            if (mUsersList.size() != 0) {
                                for (User user1 : mUsersList) {
                                    if(!user.getId().equals(user1.getId())) {
                                        mUsersList.add(user);
                                    }
                                }
                            } else {
                                mUsersList.add(user);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getApplicationContext(), mUsersList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
