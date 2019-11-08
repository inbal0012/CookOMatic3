package com.example.adopy.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.adopy.R;
import com.example.adopy.UI_utilities.Adapters.MessageAdapter;
import com.example.adopy.UI_utilities.Adapters.UserAdapter;
import com.example.adopy.Utilities.Models.Chat;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private static final String TAG = "my_ChatsActivity";
    private View root;

    private RecyclerView recyclerView;
    private List<User> mUsersList;
    private UserAdapter userAdapter;

    FirebaseUser mFirebaseUser;
    DatabaseReference reference;

    private List<String> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.activity_chats, container, false);

        recyclerView = root.findViewById(R.id.chats_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        mUsersList = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsersList);
        recyclerView.setAdapter(userAdapter);

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

        return root;
    }

//    @Override
//     public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chats);
//
//        recyclerView = findViewById(R.id.chats_recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
//
//        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        userList = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                userList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//
//                    if(chat.getSender().equals(mFirebaseUser.getUid())) {
//                        userList.add(chat.getReceiver());
//                    }
//                    if(chat.getReceiver().equals(mFirebaseUser.getUid())) {
//                        userList.add(chat.getSender());
//                    }
//                }
//
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

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
                    boolean uniqueUser;
                    for (String id : userList) {
                        uniqueUser = true;
                        if (user.getId().equals(id)) {
                            if (mUsersList.size() != 0) {
                                for (User user1 : mUsersList) {
                                    if(user.getId().equals(user1.getId())) {
                                        uniqueUser = false;
                                    }
                                }
                                if (uniqueUser) {
                                    mUsersList.add(user);
                                }
                            } else {
                                mUsersList.add(user);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUsersList);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    User user = mUsersList.get(viewHolder.getAdapterPosition());
                    showDeleteDialog(viewHolder, user);
                }
            };


    public void showDeleteDialog(final RecyclerView.ViewHolder viewHolder, final User user) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertTheme).setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_pet, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView body = dialogView.findViewById(R.id.body);
        String bodyStr = getString(R.string.delete_chat_dialog_body) + " " + user.getUsername();
        body.setText(bodyStr);

        TextView title = dialogView.findViewById(R.id.DialogTitle);
        String titleStr = getString(R.string.deleting_a_chat);
        title.setText(titleStr);


        FrameLayout mDialogDelete = dialogView.findViewById(R.id.frmDelete);
        mDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsersList.remove(viewHolder.getAdapterPosition());
                userAdapter.notifyDataSetChanged();
                deleteChatFromDatabase(user);
                alertDialog.dismiss();
            }
        });

        FrameLayout mDialogKeep = dialogView.findViewById(R.id.frmKeep);
        mDialogKeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAdapter.notifyDataSetChanged();
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void deleteChatFromDatabase(final User user){

        final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Chats");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if ((chat.getReceiver().equals(mFirebaseUser.getUid()) && chat.getSender().equals(user.getId())) ||
                            (chat.getReceiver().equals(user.getId()) && chat.getSender().equals(mFirebaseUser.getUid()))) {
                        mReference.child(snapshot.getKey()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: message deleted");
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
