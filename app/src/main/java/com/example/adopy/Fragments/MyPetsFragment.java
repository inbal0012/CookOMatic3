package com.example.adopy.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adopy.Activities.AddPetActivity;
import com.example.adopy.R;
import com.example.adopy.UI_utilities.Adapters.MyPetAdapter;
import com.example.adopy.UI_utilities.Adapters.PetAdapter2;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Utilities.FileSystemMemory;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.adopy.Utilities.RequestCodes.REQUEST_CODE_ADD_PET;

public class MyPetsFragment extends Fragment {

    private static final String TAG = "MyPetsFragment";

    private RecyclerView mRecyclerView;
    private ArrayList<PetModel> mPetModels;
    private MyPetAdapter mPetAdapter;

    //firebase
    FirebaseAuth mAuth;
    FirebaseUser fuser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mUserReference;

    User user;

    Dialogs dialogs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_pets, container, false);
//        getActivity().setTitle(getString(R.string.title_my_pets));

        dialogs = new Dialogs(getActivity());

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null) {

            mRecyclerView = root.findViewById(R.id.recycler_my_pets);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
            mPetModels = new ArrayList<>();
            mPetAdapter = new MyPetAdapter(mRecyclerView, getContext(), mPetModels);

            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");
            mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

            mUserReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            mUserReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FloatingActionButton fab = root.findViewById(R.id.fabAdd);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddPetActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ADD_PET);
                }
            });
        }

        return root;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                PetModel petModel = dataSnapshot1.getValue(PetModel.class);
                if (petModel.getPostOwnerId().equals(user.getId())) {
                    mPetModels.add(petModel);
                }
            }
            mRecyclerView.setAdapter(mPetAdapter);
            mPetAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    PetModel petModel = mPetModels.get(viewHolder.getAdapterPosition());
                    showDeleteDialog(viewHolder, petModel);
                }
            };


    public void showDeleteDialog(final RecyclerView.ViewHolder viewHolder, final PetModel petModel) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.AlertTheme).setCancelable(true);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_pet, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView body = dialogView.findViewById(R.id.body);
        String bodyStr = getString(R.string.delete_pet_dialog_body) + " " + petModel.getName();
        body.setText(bodyStr);


        FrameLayout mDialogDelete = dialogView.findViewById(R.id.frmDelete);
        mDialogDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPetModels.remove(viewHolder.getAdapterPosition());
                mPetAdapter.notifyDataSetChanged();
                deletePetFromDatabase(petModel);
                alertDialog.dismiss();
            }
        });

        FrameLayout mDialogKeep = dialogView.findViewById(R.id.frmKeep);
        mDialogKeep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPetAdapter.notifyDataSetChanged();
                alertDialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void deletePetFromDatabase(final PetModel petModel1){
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Pets");
        Log.d(TAG, "deletePetFromDatabase: " + mReference + petModel1.getId());
        mReference.child(petModel1.getId()).setValue(null)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: " + petModel1.getName() + "deleted");
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_PET && resultCode == RESULT_OK) {
            PetModel newPet = (PetModel) data.getSerializableExtra("pet");

            mPetModels.add(newPet);
            mPetAdapter.notifyDataSetChanged();

            FileSystemMemory.SaveToFile(mPetModels, getContext());
        }
    }

}