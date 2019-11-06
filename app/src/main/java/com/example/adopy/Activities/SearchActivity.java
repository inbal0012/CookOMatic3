package com.example.adopy.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.adopy.R;
import com.example.adopy.UI_utilities.Adapters.PetAdapter2;
import com.example.adopy.Utilities.Dialogs;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.Models.SearchPreferences;
import com.example.adopy.Utilities.MyLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.example.adopy.Utilities.RequestCodes.REQUEST_CODE_FILTER;
import static com.example.adopy.Utilities.RequestCodes.SELECT_IMAGE_REQUEST;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "my_SearchActivity";
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        dialogs = new Dialogs(this);

        mRecyclerView = findViewById(R.id.recycler_search_act);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mPetModels = new ArrayList<>();
        mPetAdapter = new PetAdapter2(SearchActivity.this, mPetModels);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");
        mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

        fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                if (fuser == null) {
                    loginDialog();
                }
                else {
                    dialogs.AddPetDialog(mPetModels, mPetAdapter);
                }
            }
        });

        getUserLocation();
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                PetModel petModel = dataSnapshot1.getValue(PetModel.class);
                mPetModels.add(petModel);
            }
            //mPetAdapter = new PetAdapter2(SearchActivity.this, mPetModels);
            mRecyclerView.setAdapter(mPetAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                PetModel petModel = dataSnapshot1.getValue(PetModel.class);

                if (sp != null) {
                    PetAns = petModel.getName() +
                            "\nkind: " + petModel.getKind() +
                            "\nage: " + petModel.getAge() +
                            "\nSex: " + petModel.getGender();
                    Log.d(TAG, "onDataChange: \npet: " + PetAns +
                            "\nkind filter " + sp.getTypes().contains(petModel.getKind()) +
                            "\nAge filter " + (sp.getAgeMin() <= petModel.getAge() && petModel.getAge() <= sp.getAgeMax()) +
                            "\nDistance filter " + (petDistance(petModel) <= sp.getDistance()) +
                            "\nGender filter " + petModel.getGender().equals(sp.getSex()));
                    if (sp.getTypes().contains(petModel.getKind())) {                                    //Kind filter
                        if (sp.getAgeMin() <= petModel.getAge() && petModel.getAge() <= sp.getAgeMax()) {  //Age filter
                            if (petDistance(petModel) <= sp.getDistance()) {                              //Distance filter
                                if (!sp.getSex().equals("Doesn't matter")) {
                                    if (petModel.getGender().toString().equals(sp.getSex())) {                      //Gender filter
                                        mPetModels.add(petModel);
                                    }
                                } else
                                    mPetModels.add(petModel);
                            }
                        }
                    }
                } else
                    mPetModels.add(petModel);
            }
            mPetAdapter = new PetAdapter2(SearchActivity.this, mPetModels);
            mRecyclerView.setAdapter(mPetAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void loginDialog() {
        dialogs.showLoginDialog(R.id.fabAdd);
    }

    private float petDistance(PetModel petModel) {
        float[] results = new float[1];
        Location.distanceBetween(userLat, userLng, petModel.getLatitude(), petModel.getLongitude(), results);
        Log.d(TAG, petModel.getName() + "petDistance: " + results[0]/1000);
        return results[0]/1000;
    }

    private void getUserLocation() {
        MyLocation myLocation = new MyLocation(this);
        userLat = myLocation.getLatitude();
        userLng = myLocation.getLongitude();
    }

    //search data
    private void firebaseSearch(String i_searchText) {
        mPetModels.clear();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");

        Query firebaseSearchQuery = mDatabaseReference;
        firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener2);

        Log.d(TAG, "firebaseSearch: END");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        Log.d(TAG, "onCreateOptionsMenu: ");

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "searchView.setOnSearchClickListener: ");
                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FILTER);

            }
        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE_FILTER && resultCode == RESULT_OK) {

                sp = (SearchPreferences) data.getSerializableExtra("key");
                SPans = "types:" + sp.getTypes() +
                        "\nage: " + sp.getAgeMin() + " - " + sp.getAgeMax() +
                        "\nSex: " + sp.getSex() +
                        "\nDistance: " + sp.getDistance();
                Log.d(TAG, "savePreferences: \n" + SPans);
                firebaseSearch("");

            }
            if (requestCode == SELECT_IMAGE_REQUEST) {

            }
    }
}