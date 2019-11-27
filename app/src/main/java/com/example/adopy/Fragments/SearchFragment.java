package com.example.adopy.Fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.adopy.Activities.AddPetActivity;
import com.example.adopy.Activities.FilterActivity;
import com.example.adopy.Activities.StartActivity;
import com.example.adopy.R;
import com.example.adopy.UI_utilities.Adapters.PetAdapter2;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Utilities.FileSystemMemory;
import com.example.adopy.Utilities.Interfaces_and_Emuns.ILoadMore;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.Models.SearchPreferences;
import com.example.adopy.Utilities.Models.User;
import com.example.adopy.Utilities.MyLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.example.adopy.Utilities.RequestCodes.REQUEST_CODE_ADD_PET;
import static com.example.adopy.Utilities.RequestCodes.REQUEST_CODE_FILTER;
import static com.example.adopy.Utilities.RequestCodes.SELECT_IMAGE_REQUEST;

public class SearchFragment extends Fragment {
    private static final String TAG = "my_SearchActivity";
    private View root;

    private RecyclerView mRecyclerView;
    private ArrayList<PetModel> mPetModels;
    private PetAdapter2 mPetAdapter;

    FloatingActionButton fab;

    SearchPreferences sp;
    Double userLat, userLng;
    String SPans, PetAns;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseUser fuser;
    DatabaseReference mDatabaseReference;

    Dialogs dialogs;
    MyLocation myLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);

        setHasOptionsMenu(true);
        dialogs = new Dialogs(getActivity());

        mRecyclerView = root.findViewById(R.id.recycler_search_act);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mPetModels = new ArrayList<>();
        mPetAdapter = new PetAdapter2(mRecyclerView, getContext(), mPetModels);

//        mPetAdapter.setLoadMore(new ILoadMore() {
//            @Override
//            public void onLoadMore() {
//                mPetAdapter.notifyItemInserted(mPetAdapter.getItemCount());
//                for (int i = mPetAdapter.getItemCount(); i < i + mPetAdapter.getVisibleThreshold(); i++) {
//                    if (i < mAllPets.size()) {
//                        mPetModels.add(i, mAllPets.get(i));
//                    }
//                }
//            }
//        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "onCreateView: " + fuser);

        fab = root.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuser == null) {
                    loginDialog();
                } else {
                    Intent intent = new Intent(getContext(), AddPetActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ADD_PET);
                }
            }
        });

//        FloatingActionButton fabFilter = root.findViewById(R.id.fabFilter);
//        fabFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), FilterActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_FILTER);
//            }
//        });

        getUserLocation();

        return root;
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                PetModel petModel = dataSnapshot1.getValue(PetModel.class);
                mPetModels.add(petModel);
            }
            //mPetAdapter = new PetAdapter2(SearchFragment.this, mPetModels);
//            for (int i = 0; i < 6; i++) {
//                mPetModels.add(mAllPets.get(i));
//            }
            mRecyclerView.setAdapter(mPetAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            getUserLocation();
            Log.d(TAG, "onDataChange: userLat:" + userLat + " userLng:" + userLng);
            String ans = "types: " + sp.getTypes() + "\nage: " + sp.getAgeMin() + " - " + sp.getAgeMax() + "\nSex: " + sp.getSex() + "\nDistance: " + sp.getDistance();
            Log.d(TAG, "savedPreferences: \n" + ans);
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                PetModel petModel = dataSnapshot1.getValue(PetModel.class);
                if (sp != null) {
                    filterPet(petModel);
                } else {
                    mPetModels.add(petModel);
                    Log.d(TAG, "onDataChange: " + petModel.getName() + " added");
                }
            }

//            for (int i = 0; i < 6; i++) {
//                mPetModels.add(mAllPets.get(i));
//            }
            mPetAdapter = new PetAdapter2(mRecyclerView, getContext(), mPetModels);
            mRecyclerView.setAdapter(mPetAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void filterPet(PetModel petModel) {
        PetAns = petModel.getName() +
                "\nkind: " + petModel.getKind() +
                "\tage: " + petModel.getAge() +
                "\tSex: " + petModel.getGender();
        Log.d(TAG, "onDataChange: \npet: " + PetAns +
                "\n\nKind filter: " + sp.getTypes().contains(petModel.getKind()) +
                "\tAge filter: " + (sp.getAgeMin() <= petModel.getAge() && petModel.getAge() <= sp.getAgeMax()) +
                "\nDistance filter: " + (petDistance(petModel) <= sp.getDistance()) +
                "\tGender filter: " + petModel.getGender().toString().equals(sp.getSex()));
        if (sp.getTypes().contains(petModel.getKind())) {                                    //Kind filter
            if (sp.getAgeMin() <= petModel.getAge() && petModel.getAge() <= sp.getAgeMax()) {  //Age filter
                if (petDistance(petModel) <= sp.getDistance()) {                              //Distance filter
                    if (!sp.getSex().equals("Doesn't matter")) {
                        if (petModel.getGender().toString().equals(sp.getSex())) {                      //Gender filter
                            mPetModels.add(petModel);
                            Log.d(TAG, "onDataChange: " + petModel.getName() + " added");
                        }
                    } else {
                        mPetModels.add(petModel);
                        Log.d(TAG, "onDataChange: " + petModel.getName() + " added");
                    }
                }
            }
        }
    }

    private void loginDialog() {
        dialogs.showLoginDialog(R.id.fabAdd);
    }

    private float petDistance(PetModel petModel) {
        float[] results = new float[1];
        if (userLat == null || userLng == null) {
            getUserLocation();
        }
        Location.distanceBetween(userLat, userLng, petModel.getLatitude(), petModel.getLongitude(), results);
        Log.d(TAG, petModel.getName() + " petDistance: " + results[0] / 1000);
        return results[0] / 1000;
    }

    private void getUserLocation() {
        if (myLocation == null) {
            myLocation = MyLocation.getInstance();
        }
        userLat = myLocation.getLatitude();
        userLng = myLocation.getLongitude();
        Log.d(TAG, String.format("getUserLocation: %s, %s", userLat, userLng));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fuser == null) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");
            mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
        } else {
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: ");
                    User user = dataSnapshot.getValue(User.class);
                    sp = user.getSearchPreferences();
                    firebaseSearch();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Log.d(TAG, "onResume: userLat:" + userLat + " userLng:" + userLng);
    }

    //search data
    private void firebaseSearch() {
        if (mPetModels.size() > 0) {
            mPetModels.clear();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");

        Query firebaseSearchQuery = mDatabaseReference;
        firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener2);

        Log.d(TAG, "firebaseSearch: END");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        Log.d(TAG, "onCreateOptionsMenu: ");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            Intent intent = new Intent(getContext(), FilterActivity.class);
            startActivityForResult(intent, REQUEST_CODE_FILTER);
        }
        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_search, menu);
//        Log.d(TAG, "onCreateOptionsMenu: ");
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "searchView.setOnSearchClickListener: ");
//                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_FILTER);
//
//            }
//        });
//
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + getActivity());
        if (requestCode == REQUEST_CODE_FILTER && resultCode == RESULT_OK) {

            sp = (SearchPreferences) data.getSerializableExtra("key");
            SPans = "types:" + sp.getTypes() +
                    "\nage: " + sp.getAgeMin() + " - " + sp.getAgeMax() +
                    "\nSex: " + sp.getSex() +
                    "\nDistance: " + sp.getDistance();
            Log.d(TAG, "savePreferences: \n" + SPans);
            if (fuser != null) {
                Log.d(TAG, "onActivityResult: " + fuser.getUid());
                updateSearchPreferences();
            }
            firebaseSearch();

        }
        if (requestCode == SELECT_IMAGE_REQUEST) {

        }

        if (requestCode == REQUEST_CODE_ADD_PET && resultCode == RESULT_OK) {
            PetModel newPet = (PetModel) data.getSerializableExtra("pet");

            mPetModels.add(newPet);
            mPetAdapter.notifyDataSetChanged();

            FileSystemMemory.SaveToFile(mPetModels, getContext());
        }
    }

    private void updateSearchPreferences() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> map = new HashMap<>();
        map.put("sp", sp);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: " + task.isSuccessful());
            }
        });
    }
}