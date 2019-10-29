package com.example.adopy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.adopy.UI_utilities.Adapters.PetAdapter2;
import com.example.adopy.Utilities.Models.PetModel;
import com.example.adopy.Utilities.SearchPreferences;
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

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "my_SearchActivity";
    private RecyclerView mRecyclerView;
    private ArrayList<PetModel> mPetModels;
    private PetAdapter2 mPetAdapter;

    DatabaseReference mDatabaseReference;

    public static final int REQUEST_CODE = 1;

    SearchPreferences sp;
    Double userLat, userLng;
    String SPans, PetAns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = findViewById(R.id.recycler_search_act);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mPetModels = new ArrayList<PetModel>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");
        mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);

        getUserLocation();
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                PetModel petModel = dataSnapshot1.getValue(PetModel.class);
                mPetModels.add(petModel);
            }
            mPetAdapter = new PetAdapter2(SearchActivity.this, mPetModels);
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

    private float petDistance(PetModel petModel) {
        float[] results = new float[1];
        Location.distanceBetween(userLat, userLng, petModel.getLatitude(), petModel.getLongitude(), results);
        Log.d(TAG, petModel.getName() + "petDistance: " + results[0]/1000);
        return results[0]/1000;

    }

    private void getUserLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        userLat = location.getLatitude();
        userLng = location.getLongitude();
    }

    //search data
    private void firebaseSearch(String i_searchText) {
        mPetModels.clear();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Pets");

        Query firebaseSearchQuery = mDatabaseReference;

//        firebaseSearchQuery = firebaseSearchQuery.startAt(sp.getAgeMin(), "age").endAt(sp.getAgeMax(), "age");
//
//        if (!sp.getSex().equals("Doesn't matter")) {
//            firebaseSearchQuery = firebaseSearchQuery.equalTo(sp.getSex(), "gender");
//        }

        firebaseSearchQuery.addListenerForSingleValueEvent(valueEventListener2);
//
//        FirebaseRecyclerOptions<PetModel> options =
//                new FirebaseRecyclerOptions.Builder<PetModel>()
//                        .setQuery(firebaseSearchQuery, PetModel.class)
//                        .build();
//
//        FirebaseRecyclerAdapter<PetModel, PetAdapter2.ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PetModel, PetAdapter2.ViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull PetAdapter2.ViewHolder holder, int position, @NonNull PetModel model) {
//                Log.d(TAG, "onBindViewHolder: " + model.getName());
//            }
//
//            @NonNull
//            @Override
//            public PetAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.card_pet, parent, false);
//                PetAdapter2 adapter = new PetAdapter2(getApplicationContext(), mPetModels);
//
//                return adapter.new ViewHolder(view);
//            }
//        };
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
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Log.d(TAG, "onQueryTextSubmit: " + query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Log.d(TAG, "onQueryTextChange: " + newText);
////                Intent intent = new Intent(getApplicationContext(), FilterActivity.class);
////                startActivityForResult(intent , REQUEST_CODE);
//                //mFirebaseRecyclerAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

                sp = (SearchPreferences) data.getSerializableExtra("key");
                SPans = "types:" + sp.getTypes() +
                        "\nage: " + sp.getAgeMin() + " - " + sp.getAgeMax() +
                        "\nSex: " + sp.getSex() +
                        "\nDistance: " + sp.getDistance();
                Log.d(TAG, "savePreferences: \n" + SPans);
                firebaseSearch("");

            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }

    }
}









/*public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "my_SearchActivity";

    private RecyclerView mRecyclerView;
    //private PetsAdapter mFirebaseRecyclerAdapter;
    private List<PetModel> mPetModelArrayList;

    //Firebase
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: STARTED");

        //Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Pets");
        Log.d(TAG, "onCreate: mDatabaseReference: " + mDatabaseReference.toString());


        //RecyclerView START
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        mPetModelArrayList = new ArrayList<>();
        //mFirebaseRecyclerAdapter = new PetsAdapter(mPetModelArrayList, this);

        mRecyclerView = findViewById(R.id.recycler_search_act);


        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("Pets");


//        mFirebaseRecyclerAdapter.setCardListener(new CardListener() {
//            @Override
//            public void onCardClicked(int position, View view) {
//                PetModel pet = mPetModelArrayList.get(position);
//                String str = "name: " + pet.getName() + "\nage: " + pet.getAge() + "\ntype: " + pet.getKind() + "\ngender: " + pet.getGender();
//                Log.d(TAG, "onCardClicked: selected: \n" + str);
//                Toast.makeText(SearchActivity.this, "selected: \n" + str, Toast.LENGTH_SHORT).show();
//                Gson gson = new Gson();
//                Intent intent = new Intent(SearchActivity.this, PetPageActivity.class);
//                String gstr = gson.toJson(pet);
//                Log.d(TAG, "onCardClicked: gson.toJson(pet)");
//                intent.putExtra("pet", gstr);
//                Log.d(TAG, "onCardClicked: putExtra");
//                startActivity(intent);
//                Log.d(TAG, "onCardClicked: done");
//            }
//
//            @Override
//            public void onCardLongClicked(int position, View view) {
//
//            }
//        });

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
//            @Override
//            public int getMovementFlags(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//                return 0;
//            }
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView mRecyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//
//                PetModel petModel = mPetModelArrayList.get(i);
//                //PopUpWindow(petModel,i);
//
//
//            }
//        });
//        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        //RecyclerView END

        //fillPetList();

        Log.d(TAG, "onCreate: ENDED");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        Log.d(TAG, "onCreateOptionsMenu: ");

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                //mFirebaseRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }



    //load data into recycler view - onStart
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: START");

        Query query = mDatabaseReference.orderByKey();

        Query query2 = FirebaseDatabase.getInstance()
                .getReference()
                .child("Pets");

        FirebaseRecyclerOptions<PetModel> options =
                new FirebaseRecyclerOptions.Builder<PetModel>()
                        .setQuery(query2, PetModel.class)
                        .setLifecycleOwner(this)
                        .build();
        mFirebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PetModel, PetsAdapter.ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PetsAdapter.ViewHolder holder, int position, @NonNull PetModel model) {
                        Log.d(TAG, "onStart: onBindViewHolder: pet name" + model.getName());
                        mFirebaseRecyclerAdapter.onBindViewHolder(holder, position);
                    }

                    @NonNull
                    @Override
                    public PetsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        Log.d(TAG, "onStart: onCreateViewHolder: ");
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.card_pet, parent, false);
                        PetsAdapter petsAdapter = new PetsAdapter(mPetModelArrayList, getApplicationContext());

                        return petsAdapter.new ViewHolder(view);
                    }

                    @Override
                    public void onDataChanged() {
                        Log.d(TAG, "onDataChanged: ");
                        super.onDataChanged();
                    }
                };

        mRecyclerView.setAdapter(mFirebaseRecyclerAdapter);

        Log.d(TAG, "onStart: END");
    }

    //------------MY FUNCs---------------

    private void fillPetList() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dog);
        mPetModelArrayList.add(new PetModel("dog", "nero", 4.2, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.dog)));
        mPetModelArrayList.add(new PetModel("cat", "laila", 2.7, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.cat)));
        mPetModelArrayList.add(new PetModel("cat", "nobles", 3.5, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.cat)));
        mPetModelArrayList.add(new PetModel("dog", "nemesh", 0.9, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.dog)));
        mPetModelArrayList.add(new PetModel("iguana", "gourg", 0.5, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.iguana)));
        mPetModelArrayList.add(new PetModel("turtle", "slowy", 10.0, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.turtle)));
        mPetModelArrayList.add(new PetModel("chinchilla", "archy", 5.0, Gender.Male, BitmapFactory.decodeResource(getResources(), R.drawable.chinchilla)));
        mPetModelArrayList.add(new PetModel("rabbit", "carrot", 6.9, Gender.Female, BitmapFactory.decodeResource(getResources(), R.drawable.rabbit)));

        mFirebaseRecyclerAdapter.notifyDataSetChanged();

        Log.d(TAG, "fillPetList: list filled");
    }

    //search data
    private void firebaseSearch(String i_searchText) {
        //Query firebaseSearchQuery = mDatabaseReference
    }
}*/
